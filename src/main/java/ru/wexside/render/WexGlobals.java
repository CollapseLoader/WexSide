package ru.wexside.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.OptionalInt;
import net.minecraft.class_276;
import net.minecraft.class_310;
import org.joml.Matrix4f;
import ru.wexside.misc.TextureHandle;
import ru.wexside.misc.TextureSlotBindings;
import ru.wexside.render.ClientRenderPipelines;
import ru.wexside.render.GpuKawaseBlurRenderer;
import ru.wexside.render.MaskTexture;
import ru.wexside.render.RenderFrameClock;
import ru.wexside.render.StencilMaskState;
import ru.wexside.util.GuiRenderDriver;

public final class WexGlobals
implements GuiRenderDriver {
    private int scissorWidth = 0;
    private int scissorY = 0;
    private int scissorX = 0;
    private int framebufferWidth = 1;
    private final float[] projectionMatrixValues = new float[16];
    private int framebufferHeight = 1;
    private int scissorHeight = 0;
    private final Deque<StencilMaskState> stencilStack;
    private int stencilMode = 0;
    private static final int MASK_CACHE_TTL_FRAMES = 600;
    private final GpuKawaseBlurRenderer backdropBlurRenderer;
    private int stencilOperation = 0;
    private final TextureSlotBindings textureSlotBindings;
    private final List<MaskTexture> maskTextures = new ArrayList<MaskTexture>();
    private final RenderPipeline renderPipeline;

    public WexGlobals(RenderPipeline renderPipeline) {
        this.stencilStack = new ArrayDeque<StencilMaskState>();
        this.textureSlotBindings = new TextureSlotBindings();
        this.backdropBlurRenderer = new GpuKawaseBlurRenderer();
        this.renderPipeline = renderPipeline;
    }

    @Override
    public void setViewportSize(int n, int n2) {
        this.framebufferWidth = n;
        this.framebufferHeight = n2;
    }

    @Override
    public void endLayerFrame() {
        throw new UnsupportedOperationException("Offscreen layers are only supported on GL backend");
    }

    @Override
    public int bindTexture(int n, int n2, int n3) {
        return this.textureSlotBindings.resolveTextureSlot(n, n2, n3);
    }

    @Override
    public void setProjectionMatrix(Matrix4f matrix4f) {
        matrix4f.get(this.projectionMatrixValues);
    }

    @Override
    public void setStencilMode(int n) {
        this.stencilMode = n;
    }

    @Override
    public void close() {
        for (MaskTexture iiiiIIiIlI2 : this.maskTextures) {
            if (iiiiIIiIlI2 == null) continue;
            iiiiIIiIlI2.close();
        }
        this.maskTextures.clear();
        this.stencilStack.clear();
        this.backdropBlurRenderer.close();
        this.textureSlotBindings.close();
    }

    @Override
    public void prepareBackdrop() {
        this.backdropBlurRenderer.invalidate();
    }

    @Override
    public void setBackdropBlurRadius(float f) {
        this.backdropBlurRenderer.setOffset(f);
    }

    @Override
    public void setScissor(int n, int n2, int n3, int n4) {
        this.scissorX = n;
        this.scissorY = n2;
        this.scissorWidth = n3;
        this.scissorHeight = n4;
    }

    @Override
    public void resetTextureBindings() {
        this.textureSlotBindings.resetTextureSlots();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void drawIndexed(ByteBuffer byteBuffer, int n, ByteBuffer byteBuffer2, int n2) {
        ByteBuffer byteBuffer3 = byteBuffer.duplicate().order(ByteOrder.nativeOrder());
        ByteBuffer byteBuffer4 = byteBuffer2.duplicate().order(ByteOrder.nativeOrder());
        GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "wex/verts", 32, byteBuffer3);
        GpuBuffer gpuBuffer2 = RenderSystem.getDevice().createBuffer(() -> "wex/idx", 64, byteBuffer4);
        ByteBuffer byteBuffer5 = ByteBuffer.allocateDirect(96).order(ByteOrder.nativeOrder());
        for (int i = 0; i < 16; ++i) {
            byteBuffer5.putFloat(this.projectionMatrixValues[i]);
        }
        byteBuffer5.putFloat(this.framebufferWidth).putFloat(this.framebufferHeight);
        byteBuffer5.putInt(this.getBackdropTextureSlot()).putInt(0);
        byteBuffer5.putInt(this.scissorX).putInt(this.scissorY).putInt(this.scissorWidth).putInt(this.scissorHeight);
        byteBuffer5.flip();
        GpuBuffer gpuBuffer3 = RenderSystem.getDevice().createBuffer(() -> "wex/WexGlobals", 128, byteBuffer5);
        if (this.stencilOperation == 1) {
            StencilMaskState iiIlliiIlI2 = this.stencilStack.peek();
            if (iiIlliiIlI2 != null) {
                OptionalInt optionalInt = iiIlliiIlI2.clearBeforeWrite ? OptionalInt.of(0) : OptionalInt.empty();
                try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "wex/dc-mask", iiIlliiIlI2.target.view, optionalInt);){
                    renderPass.setPipeline(ClientRenderPipelines.MASK_WRITER);
                    RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
                    renderPass.setUniform("WexGlobals", gpuBuffer3.slice());
                    renderPass.setVertexBuffer(0, gpuBuffer);
                    renderPass.setIndexBuffer(gpuBuffer2, VertexFormat.class_5595.field_27373);
                    this.textureSlotBindings.bindTextures(renderPass);
                    StencilMaskState iiIlliiIlI3 = this.getStencilMaskState();
                    if (iiIlliiIlI3 != null) {
                        renderPass.bindTexture("MaskTex", iiIlliiIlI3.target.view, RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST));
                    }
                    renderPass.drawIndexed(0, 0, n2, 1);
                    iiIlliiIlI2.clearBeforeWrite = false;
                }
            }
        } else {
            class_276 iIllIIiilI2 = class_310.method_1551().method_1522();
            RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "wex/core-dc", iIllIIiilI2.method_71639(), OptionalInt.empty());
            try {
                renderPass.setPipeline(this.renderPipeline);
                RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
                renderPass.setUniform("WexGlobals", gpuBuffer3.slice());
                renderPass.setVertexBuffer(0, gpuBuffer);
                renderPass.setIndexBuffer(gpuBuffer2, VertexFormat.class_5595.field_27373);
                this.textureSlotBindings.bindTextures(renderPass);
                if (this.stencilOperation == 2 && !this.stencilStack.isEmpty()) {
                    renderPass.bindTexture("MaskTex", this.stencilStack.peek().target.view, RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST));
                }
                renderPass.drawIndexed(0, 0, n2, 1);
            }
            finally {
                gpuBuffer.close();
                gpuBuffer2.close();
                gpuBuffer3.close();
            }
        }
    }

    @Override
    public void beginFrame(int n, int n2) {
        this.framebufferWidth = n;
        this.framebufferHeight = n2;
        this.stencilMode = 0;
        this.stencilOperation = 0;
        this.stencilStack.clear();
        this.resetFrameResources();
        this.textureSlotBindings.beginFrame();
        class_276 iIllIIiilI2 = class_310.method_1551().method_1522();
        GpuTexture gpuTexture = iIllIIiilI2.method_30277();
        GpuTextureView gpuTextureView = iIllIIiilI2.method_71639();
        this.backdropBlurRenderer.capture(RenderFrameClock.currentFrame(), gpuTexture, gpuTextureView, this.framebufferWidth, this.framebufferHeight);
        GpuTextureView gpuTextureView2 = this.backdropBlurRenderer.getResultView();
        this.textureSlotBindings.setInputTexture(gpuTextureView2 != null ? gpuTextureView2 : gpuTextureView);
    }

    @Override
    public void beginLayer(TextureHandle texture) {
        throw new UnsupportedOperationException("Offscreen layers are only supported on GL backend");
    }

    @Override
    public void beginStencil(int n) {
        int n2 = this.stencilStack.size();
        this.stencilStack.push(new StencilMaskState(n, this.getOrCreateMaskTexture(n2)));
        this.stencilOperation = 1;
        this.stencilMode = 1;
    }

    @Override
    public TextureHandle acquireDedicatedLayer(int n, int n2) {
        throw new UnsupportedOperationException("Offscreen layers are only supported on GL backend");
    }

    @Override
    public void endStencil() {
        if (this.stencilStack.isEmpty()) {
            throw new IllegalStateException("Stencil stack is empty");
        }
        this.stencilStack.pop();
        if (this.stencilStack.isEmpty()) {
            this.stencilOperation = 0;
            this.stencilMode = 0;
            return;
        }
        this.stencilOperation = this.stencilStack.peek().writing ? 1 : 2;
        this.stencilMode = this.stencilOperation == 1 ? 1 : 2;
    }

    @Override
    public void applyStencil(int n) {
        StencilMaskState iiIlliiIlI2 = this.stencilStack.peek();
        if (iiIlliiIlI2 == null) {
            throw new IllegalStateException("Stencil stack is empty");
        }
        if (iiIlliiIlI2.reference != n) {
            int n2 = iiIlliiIlI2.reference;
            int n3 = n;
            throw new IllegalStateException("Attempted to use stencil ref " + n3 + " but active ref is " + n2);
        }
        iiIlliiIlI2.writing = false;
        this.stencilOperation = 2;
        this.stencilMode = 2;
    }

    public void captureBackdrop(int n, int n2) {
        class_276 iIllIIiilI2 = class_310.method_1551().method_1522();
        if (iIllIIiilI2 == null) {
            return;
        }
        GpuTexture gpuTexture = iIllIIiilI2.method_30277();
        GpuTextureView gpuTextureView = iIllIIiilI2.method_71639();
        if (gpuTexture == null) {
            return;
        }
        this.backdropBlurRenderer.capture(RenderFrameClock.currentFrame(), gpuTexture, gpuTextureView, n, n2);
    }

    @Override
    public int getBackdropTextureSlot() {
        if (this.stencilOperation != 1) {
            return this.stencilMode;
        }
        return this.stencilStack.size() > 1 ? 3 : 1;
    }

    @Override
    public void resetFrameResources() {
        int n = RenderFrameClock.currentFrame();
        for (int i = 0; i < this.maskTextures.size(); ++i) {
            MaskTexture iiiiIIiIlI2 = this.maskTextures.get(i);
            if (iiiiIIiIlI2 == null || n - iiiiIIiIlI2.lastUsedFrame < 600) continue;
            iiiiIIiIlI2.close();
            this.maskTextures.set(i, null);
        }
    }

    private StencilMaskState getStencilMaskState() {
        boolean bl = false;
        for (StencilMaskState iiIlliiIlI2 : this.stencilStack) {
            if (bl) {
                return iiIlliiIlI2;
            }
            bl = true;
        }
        return null;
    }

    private MaskTexture createMaskTexture(int n, int n2) {
        GpuTexture gpuTexture = RenderSystem.getDevice().createTexture(() -> "wex/mask", 12, TextureFormat.RED8, n, n2, 1, 1);
        return new MaskTexture(gpuTexture, RenderSystem.getDevice().createTextureView(gpuTexture), n, n2);
    }

    private MaskTexture getOrCreateMaskTexture(int n) {
        while (this.maskTextures.size() <= n) {
            this.maskTextures.add(null);
        }
        MaskTexture iiiiIIiIlI2 = this.maskTextures.get(n);
        if (iiiiIIiIlI2 != null) {
            iiiiIIiIlI2.lastUsedFrame = RenderFrameClock.currentFrame();
        }
        if (iiiiIIiIlI2 == null || iiiiIIiIlI2.width != this.framebufferWidth || iiiiIIiIlI2.height != this.framebufferHeight) {
            if (iiiiIIiIlI2 != null) {
                iiiiIIiIlI2.close();
            }
            iiiiIIiIlI2 = this.createMaskTexture(this.framebufferWidth, this.framebufferHeight);
            iiiiIIiIlI2.lastUsedFrame = RenderFrameClock.currentFrame();
            this.maskTextures.set(n, iiiiIIiIlI2);
        }
        return iiiiIIiIlI2;
    }

    public int getBackdropTextureId() {
        return this.backdropBlurRenderer.getResultTextureId();
    }
}

