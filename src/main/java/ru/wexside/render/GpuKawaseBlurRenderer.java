package ru.wexside.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.OptionalInt;
import net.minecraft.class_10868;
import ru.wexside.misc.FrameRateLimiter;
import ru.wexside.render.ClientRenderPipelines;

public final class GpuKawaseBlurRenderer
implements AutoCloseable {
    private static final int PASS_COUNT = 3;
    private static final int TARGET_COUNT = 4;
    private static final int REFRESH_RATE = 120;
    private static final int TEXTURE_USAGE_RENDER_TARGET_AND_SAMPLED = 12;
    private static final int VERTEX_BUFFER_USAGE = 32;
    private static final int UNIFORM_BUFFER_USAGE = 136;
    private static final int UNIFORM_BUFFER_SIZE = 16;
    private final FrameRateLimiter refreshLimiter = new FrameRateLimiter(120);
    private BlurTarget[] targets = new BlurTarget[0];
    private GpuBuffer quadVertexBuffer;
    private GpuBuffer uniformBuffer;
    private GpuTextureView ownedSourceView;
    private int width = -1;
    private int height = -1;
    private int lastRenderedFrame = Integer.MIN_VALUE;
    private int refreshDecisionFrame = Integer.MIN_VALUE;
    private float offset = 1.0f;
    private float uploadedOffset = Float.NaN;
    private boolean refreshAllowed;

    public void setOffset(float offset) {
        if (Math.abs(this.offset - offset) < 0.001f) {
            return;
        }
        this.offset = offset;
        this.invalidate();
    }

    public void invalidate() {
        this.lastRenderedFrame = Integer.MIN_VALUE;
        this.refreshDecisionFrame = Integer.MIN_VALUE;
        this.refreshLimiter.reset();
    }

    public void capture(int frame, GpuTexture sourceTexture, GpuTextureView sourceView, int width, int height) {
        boolean targetsReusable;
        if (sourceTexture == null || width <= 0 || height <= 0) {
            return;
        }
        long now = System.nanoTime();
        boolean bl = targetsReusable = this.width == width && this.height == height && this.targets.length == 4 && this.targets[0].view != null;
        if (this.refreshDecisionFrame != frame) {
            this.refreshDecisionFrame = frame;
            boolean bl2 = this.refreshAllowed = !targetsReusable || this.refreshLimiter.canRun(now);
        }
        if (sourceView == null && targetsReusable) {
            return;
        }
        if (!this.refreshAllowed || this.lastRenderedFrame == frame) {
            return;
        }
        this.closeOwnedSourceView();
        GpuTextureView readableSourceView = sourceView;
        if (readableSourceView == null) {
            readableSourceView = this.ownedSourceView = RenderSystem.getDevice().createTextureView(sourceTexture);
        }
        this.ensureTargets(width, height);
        this.renderBlurPyramid(readableSourceView);
        this.lastRenderedFrame = frame;
        this.refreshLimiter.markRun(now);
    }

    public int getResultTextureId() {
        int n;
        if (this.targets.length == 0) {
            return 0;
        }
        GpuTexture texture = this.targets[0].texture;
        if (texture instanceof class_10868) {
            class_10868 glTexture = (class_10868)texture;
            n = glTexture.method_68427();
        } else {
            n = 0;
        }
        return n;
    }

    public GpuTextureView getResultView() {
        return this.targets.length == 0 ? null : this.targets[0].view;
    }

    private void ensureTargets(int width, int height) {
        if (this.width == width && this.height == height && this.targets.length == 4) {
            return;
        }
        this.closeTargets();
        this.width = width;
        this.height = height;
        this.targets = new BlurTarget[4];
        for (int level = 0; level < 4; ++level) {
            int targetWidth = Math.max(1, width >> level);
            int targetHeight = Math.max(1, height >> level);
            int targetLevel = level;
            GpuTexture texture = RenderSystem.getDevice().createTexture(() -> "wex/dc-kawase-" + targetLevel, 12, TextureFormat.RGBA8, targetWidth, targetHeight, 1, 1);
            this.targets[level] = new BlurTarget(texture, RenderSystem.getDevice().createTextureView(texture));
        }
    }

    private void renderBlurPyramid(GpuTextureView sourceView) {
        int level;
        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
        RenderSystem.class_5590 sequentialIndices = RenderSystem.getSequentialBuffer((VertexFormat.class_5596)VertexFormat.class_5596.field_27382);
        GpuBuffer indexBuffer = sequentialIndices.method_68274(6);
        GpuBuffer vertexBuffer = this.getOrCreateQuadVertexBuffer();
        this.updateUniformBuffer(commandEncoder);
        this.renderFullscreenPass(commandEncoder, ClientRenderPipelines.KAWASE_DOWNSAMPLE, this.targets[1].view, sourceView, vertexBuffer, indexBuffer, sequentialIndices.method_31924());
        for (level = 2; level <= 3; ++level) {
            this.renderFullscreenPass(commandEncoder, ClientRenderPipelines.KAWASE_DOWNSAMPLE, this.targets[level].view, this.targets[level - 1].view, vertexBuffer, indexBuffer, sequentialIndices.method_31924());
        }
        for (level = 3; level >= 1; --level) {
            this.renderFullscreenPass(commandEncoder, ClientRenderPipelines.KAWASE_UPSAMPLE, this.targets[level - 1].view, this.targets[level].view, vertexBuffer, indexBuffer, sequentialIndices.method_31924());
        }
    }

    private void renderFullscreenPass(CommandEncoder commandEncoder, RenderPipeline pipeline, GpuTextureView outputView, GpuTextureView inputView, GpuBuffer vertexBuffer, GpuBuffer indexBuffer, VertexFormat.class_5595 indexType) {
        try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "wex/dc-kawase", outputView, OptionalInt.empty());){
            renderPass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
            renderPass.setUniform("KawaseData", this.uniformBuffer.slice());
            renderPass.setVertexBuffer(0, vertexBuffer);
            renderPass.setIndexBuffer(indexBuffer, indexType);
            renderPass.bindTexture("InSampler", inputView, RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR));
            renderPass.drawIndexed(0, 0, 6, 1);
        }
    }

    private GpuBuffer getOrCreateQuadVertexBuffer() {
        if (this.quadVertexBuffer != null) {
            return this.quadVertexBuffer;
        }
        ByteBuffer vertices = ByteBuffer.allocateDirect(48).order(ByteOrder.nativeOrder());
        vertices.putFloat(-1.0f).putFloat(-1.0f).putFloat(0.0f);
        vertices.putFloat(1.0f).putFloat(-1.0f).putFloat(0.0f);
        vertices.putFloat(1.0f).putFloat(1.0f).putFloat(0.0f);
        vertices.putFloat(-1.0f).putFloat(1.0f).putFloat(0.0f);
        vertices.flip();
        this.quadVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "wex/dc-kawase-quad", 32, vertices);
        return this.quadVertexBuffer;
    }

    private void updateUniformBuffer(CommandEncoder commandEncoder) {
        ByteBuffer uniformData = ByteBuffer.allocateDirect(16).order(ByteOrder.nativeOrder());
        uniformData.putFloat(this.offset).putFloat(0.0f).putFloat(0.0f).putFloat(0.0f);
        uniformData.flip();
        if (this.uniformBuffer == null) {
            this.uniformBuffer = RenderSystem.getDevice().createBuffer(() -> "wex/dc-kawase-data", 136, uniformData);
            this.uploadedOffset = this.offset;
            return;
        }
        if (this.uploadedOffset != this.offset) {
            commandEncoder.writeToBuffer(this.uniformBuffer.slice(), uniformData);
            this.uploadedOffset = this.offset;
        }
    }

    private void closeOwnedSourceView() {
        if (this.ownedSourceView != null) {
            this.ownedSourceView.close();
            this.ownedSourceView = null;
        }
    }

    private void closeTargets() {
        for (BlurTarget target : this.targets) {
            if (target == null) continue;
            target.close();
        }
        this.targets = new BlurTarget[0];
        this.width = -1;
        this.height = -1;
        this.refreshAllowed = false;
        this.invalidate();
    }

    @Override
    public void close() {
        this.closeOwnedSourceView();
        if (this.quadVertexBuffer != null) {
            this.quadVertexBuffer.close();
            this.quadVertexBuffer = null;
        }
        if (this.uniformBuffer != null) {
            this.uniformBuffer.close();
            this.uniformBuffer = null;
        }
        this.uploadedOffset = Float.NaN;
        this.closeTargets();
    }

    private static final class BlurTarget
    implements AutoCloseable {
        private GpuTexture texture;
        private GpuTextureView view;

        private BlurTarget(GpuTexture texture, GpuTextureView view) {
            this.texture = texture;
            this.view = view;
        }

        @Override
        public void close() {
            if (this.view != null) {
                this.view.close();
                this.view = null;
            }
            if (this.texture != null) {
                this.texture.close();
                this.texture = null;
            }
        }
    }
}

