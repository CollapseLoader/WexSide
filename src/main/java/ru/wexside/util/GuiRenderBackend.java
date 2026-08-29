package ru.wexside.util;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL41;
import ru.wexside.misc.RenderFrameContext;
import ru.wexside.misc.ShaderUniformWriter;
import ru.wexside.misc.StencilFrameState;
import ru.wexside.misc.TextureHandle;
import ru.wexside.render.GaussianBlurRenderer;
import ru.wexside.render.GlShaderProgram;
import ru.wexside.render.KawaseBlurRenderer;
import ru.wexside.render.LayerFramebuffer;
import ru.wexside.render.OpenGlTextureUnitAllocator;
import ru.wexside.render.RenderFrameClock;
import ru.wexside.render.VertexAttributeFormat;
import ru.wexside.util.GlRenderStateSnapshot;
import ru.wexside.util.GuiRenderDriver;

public final class GuiRenderBackend
implements GuiRenderDriver {
    private final Deque<RenderFrameContext> frameContextStack;
    private int stencilMode = 0;
    private int lastCleanupFrame;
    private static final int FRAMEBUFFER_CACHE_TTL_FRAMES = 600;
    final KawaseBlurRenderer kawaseBlurRenderer;
    private final ShaderUniformWriter stencilModeUniform;
    private final ShaderUniformWriter timeUniform;
    private final List<LayerFramebuffer> pooledFramebuffers;
    private static final int MAX_TEXTURE_SIZE_CAP = 8192;
    final GaussianBlurRenderer gaussianBlurRenderer;
    private final int vertexArrayId;
    private boolean skipBackdropCapture = false;
    private final int vertexBufferId;
    private final ShaderUniformWriter textureSamplersUniform;
    private final List<LayerFramebuffer> dedicatedFramebuffers;
    private final int indexBufferId;
    final OpenGlTextureUnitAllocator textureUnitAllocator = new OpenGlTextureUnitAllocator();
    private GlRenderStateSnapshot glRenderStateSnapshot;
    private final Deque<StencilFrameState> stencilStack;
    private final ShaderUniformWriter projectionMatrixUniform;
    private static final int LAYER_SIZE_ALIGNMENT = 64;
    private boolean shaderBound = false;
    private static int cachedMaxTextureSize;
    private final GlShaderProgram shaderProgram;
    private final ShaderUniformWriter viewportSizeUniform;

    public GuiRenderBackend(GlShaderProgram shaderProgram) {
        this.kawaseBlurRenderer = new KawaseBlurRenderer();
        this.gaussianBlurRenderer = new GaussianBlurRenderer();
        this.lastCleanupFrame = Integer.MIN_VALUE;
        this.dedicatedFramebuffers = new ArrayList<LayerFramebuffer>();
        this.pooledFramebuffers = new ArrayList<LayerFramebuffer>();
        this.frameContextStack = new ArrayDeque<RenderFrameContext>();
        this.stencilStack = new ArrayDeque<StencilFrameState>();
        this.shaderProgram = shaderProgram;
        this.projectionMatrixUniform = shaderProgram.registerUniform("orthographicMatrix");
        this.viewportSizeUniform = shaderProgram.registerUniform("uViewportSize");
        this.stencilModeUniform = shaderProgram.registerUniform("uStencilMode");
        this.timeUniform = shaderProgram.registerUniform("uTime");
        this.textureSamplersUniform = shaderProgram.registerUniform("textureSampler");
        this.vertexArrayId = GL33.glGenVertexArrays();
        this.vertexBufferId = GL33.glGenBuffers();
        this.indexBufferId = GL33.glGenBuffers();
        GL33.glBindVertexArray((int)this.vertexArrayId);
        GL33.glBindBuffer((int)34962, (int)this.vertexBufferId);
        GL33.glBindBuffer((int)34963, (int)this.indexBufferId);
        List<VertexAttributeFormat> list = List.of(VertexAttributeFormat.float2, VertexAttributeFormat.float2, VertexAttributeFormat.float2, VertexAttributeFormat.float2, VertexAttributeFormat.float4, VertexAttributeFormat.color4, VertexAttributeFormat.color4, VertexAttributeFormat.float1, VertexAttributeFormat.float1, VertexAttributeFormat.float1, VertexAttributeFormat.float1, VertexAttributeFormat.float1, VertexAttributeFormat.float2, VertexAttributeFormat.byte1, VertexAttributeFormat.byte1);
        int n = list.stream().mapToInt(VertexAttributeFormat::byteSize).sum();
        int n2 = 0;
        for (int i = 0; i < list.size(); ++i) {
            VertexAttributeFormat format = list.get(i);
            GL20.glEnableVertexAttribArray((int)i);
            if (format.integer()) {
                GL33.glVertexAttribIPointer((int)i, (int)format.componentCount(), (int)format.glType(), (int)n, (long)n2);
            } else {
                GL20.glVertexAttribPointer((int)i, (int)format.componentCount(), (int)format.glType(), (boolean)format.normalized(), (int)n, (long)n2);
            }
            n2 += format.byteSize();
        }
        GL33.glBindVertexArray((int)0);
        GL33.glBindBuffer((int)34962, (int)0);
        GL33.glBindBuffer((int)34963, (int)0);
    }

    @Override
    public void endFrame() {
        this.shaderProgram.unbind();
        this.shaderBound = false;
        GL33.glBindVertexArray((int)0);
        GL33.glBindBuffer((int)34962, (int)0);
        GL33.glBindBuffer((int)34963, (int)0);
        if (this.glRenderStateSnapshot != null) {
            this.glRenderStateSnapshot.restore();
            this.glRenderStateSnapshot = null;
        }
        this.textureUnitAllocator.update();
        if (!this.frameContextStack.isEmpty()) {
            RenderFrameContext renderFrameContext = this.frameContextStack.pop();
            this.glRenderStateSnapshot = renderFrameContext.stateSnapshot();
            this.shaderBound = renderFrameContext.shaderBound();
            this.stencilMode = renderFrameContext.stencilMode();
            this.stencilStack.clear();
            for (StencilFrameState stencilFrameState : renderFrameContext.stencilStates()) {
                this.stencilStack.addLast(new StencilFrameState(stencilFrameState.reference(), stencilFrameState.internalReference(), stencilFrameState.writeMode()));
            }
        } else {
            this.stencilStack.clear();
        }
    }

    @Override
    public void setViewportSize(int n, int n2) {
        if (this.shaderBound && this.viewportSizeUniform != null) {
            this.viewportSizeUniform.set(n, n2);
        }
    }

    @Override
    public void endLayerFrame() {
        this.endFrame();
    }

    @Override
    public int bindTexture(int n, int n2, int n3) {
        return this.textureUnitAllocator.process(n);
    }

    @Override
    public void setProjectionMatrix(Matrix4f matrix4f) {
        if (this.shaderBound && this.projectionMatrixUniform != null) {
            this.projectionMatrixUniform.set(matrix4f);
        }
    }

    @Override
    public TextureHandle blurTextureRange(TextureHandle texture, float f, float f2, float f3, float f4, int n) {
        return this.gaussianBlurRenderer.blurRange(texture, f, f2, f3, f4, n);
    }

    private void beginStencilWrite(int n) {
        GL33.glEnable((int)2960);
        GL33.glColorMask((boolean)false, (boolean)false, (boolean)false, (boolean)false);
        GL33.glStencilMask((int)255);
        GL33.glStencilFunc((int)514, (int)n, (int)255);
        GL33.glStencilOp((int)7680, (int)7680, (int)7682);
    }

    private static int alignLayerSize(int n) {
        int n2 = GuiRenderBackend.clampTextureDimension(n);
        return Math.max(64, (n2 + 64 - 1) / 64 * 64);
    }

    private void disableStencilTest() {
        GL33.glStencilMask((int)255);
        GL33.glColorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        GL33.glStencilFunc((int)519, (int)0, (int)255);
        GL33.glStencilOp((int)7680, (int)7680, (int)7680);
        GL33.glDisable((int)2960);
    }

    private static int getMaxTextureSize() {
        if (cachedMaxTextureSize <= 0) {
            int n = GL33.glGetInteger((int)3379);
            cachedMaxTextureSize = n > 0 ? Math.min(n, 8192) : 8192;
        }
        return cachedMaxTextureSize;
    }

    @Override
    public void prepareBackdrop() {
        this.skipBackdropCapture = true;
    }

    private static int clampTextureDimension(int n) {
        return Math.min(Math.max(n, 1), GuiRenderBackend.getMaxTextureSize());
    }

    private void clearStencil(int n) {
        GL33.glClear((int)1024);
        this.beginNestedStencilWrite(n);
    }

    @Override
    public void setStencilMode(int n) {
        this.stencilMode = n;
        if (this.shaderBound && this.stencilModeUniform != null) {
            this.stencilModeUniform.set(n);
        }
    }

    private void prepareDrawState(int n, int n2) {
        GL33.glEnable((int)3042);
        GL33.glBlendEquation((int)32774);
        GL14.glBlendFuncSeparate((int)770, (int)771, (int)1, (int)771);
        GL33.glDisable((int)2884);
        GL33.glDisable((int)2929);
        GL33.glDisable((int)2960);
        GL33.glEnable((int)3089);
        GL33.glColorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        GL33.glStencilMask((int)255);
        GL33.glStencilFunc((int)519, (int)0, (int)255);
        GL33.glStencilOp((int)7680, (int)7680, (int)7680);
        GL33.glPolygonMode((int)1032, (int)6914);
        GL33.glBindSampler((int)0, (int)0);
        GL41.glBindProgramPipeline((int)0);
        this.shaderProgram.bind();
        this.shaderBound = true;
        this.setViewportSize(n, n2);
        this.textureSamplersUniform.set(this.textureUnitAllocator.getIntBuffer());
        if (this.stencilModeUniform != null) {
            this.stencilModeUniform.set(this.stencilMode);
        }
        if (this.timeUniform != null) {
            this.timeUniform.set(RenderFrameClock.elapsedSeconds());
        }
        GL33.glBindVertexArray((int)this.vertexArrayId);
        GL33.glBindBuffer((int)34962, (int)this.vertexBufferId);
        GL33.glBindBuffer((int)34963, (int)this.indexBufferId);
    }

    private void beginNestedStencilWrite(int n) {
        GL33.glEnable((int)2960);
        GL33.glColorMask((boolean)false, (boolean)false, (boolean)false, (boolean)false);
        GL33.glStencilMask((int)255);
        GL33.glStencilFunc((int)519, (int)n, (int)255);
        GL33.glStencilOp((int)7680, (int)7680, (int)7681);
    }

    @Override
    public void close() {
        this.kawaseBlurRenderer.close();
        this.gaussianBlurRenderer.close();
        for (LayerFramebuffer framebuffer : this.dedicatedFramebuffers) {
            framebuffer.delete();
        }
        this.dedicatedFramebuffers.clear();
        for (LayerFramebuffer framebuffer : this.pooledFramebuffers) {
            framebuffer.delete();
        }
        this.pooledFramebuffers.clear();
        if (this.vertexArrayId != 0) {
            GL33.glDeleteVertexArrays((int)this.vertexArrayId);
        }
        if (this.vertexBufferId != 0) {
            GL33.glDeleteBuffers((int)this.vertexBufferId);
        }
        if (this.indexBufferId != 0) {
            GL33.glDeleteBuffers((int)this.indexBufferId);
        }
    }

    private void evictStaleFramebuffers(List<LayerFramebuffer> list, int n) {
        Iterator<LayerFramebuffer> iterator = list.iterator();
        while (iterator.hasNext()) {
            LayerFramebuffer framebuffer = iterator.next();
            if (n - framebuffer.lastUsedFrame() < 600) continue;
            this.gaussianBlurRenderer.invalidateSourceTexture(framebuffer.getTextureId());
            framebuffer.delete();
            iterator.remove();
        }
    }

    private StencilFrameState findParentStencilState(StencilFrameState stencilFrameState) {
        boolean bl = false;
        for (StencilFrameState stencilFrameState2 : this.stencilStack) {
            if (bl) {
                return stencilFrameState2;
            }
            if (stencilFrameState2 != stencilFrameState) continue;
            bl = true;
        }
        return null;
    }

    private Deque<StencilFrameState> copyStencilStack() {
        ArrayDeque<StencilFrameState> arrayDeque = new ArrayDeque<StencilFrameState>();
        for (StencilFrameState stencilFrameState : this.stencilStack) {
            arrayDeque.addLast(new StencilFrameState(stencilFrameState.reference(), stencilFrameState.internalReference(), stencilFrameState.writeMode()));
        }
        return arrayDeque;
    }

    public OpenGlTextureUnitAllocator getRenderPipeline8() {
        return this.textureUnitAllocator;
    }

    private void configureStencilRead(int n) {
        GL33.glColorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        GL33.glStencilMask((int)0);
        GL33.glEnable((int)2960);
        GL33.glStencilFunc((int)514, (int)n, (int)255);
        GL33.glStencilOp((int)7680, (int)7680, (int)7680);
    }

    private boolean pushFrameContext() {
        if (this.glRenderStateSnapshot == null && !this.shaderBound) {
            return false;
        }
        this.frameContextStack.push(new RenderFrameContext(this.glRenderStateSnapshot, this.shaderBound, this.stencilMode, this.copyStencilStack()));
        return true;
    }

    private LayerFramebuffer requireLayerFramebuffer(TextureHandle texture) {
        if (!(texture instanceof LayerFramebuffer)) {
            String string = String.valueOf(texture);
            throw new IllegalArgumentException("Unsupported layer target: " + string);
        }
        return (LayerFramebuffer)texture;
    }

    @Override
    public void setScissor(int n, int n2, int n3, int n4) {
        GL33.glScissor((int)n, (int)n2, (int)n3, (int)n4);
    }

    @Override
    public void resetTextureBindings() {
        this.textureUnitAllocator.update();
    }

    @Override
    public void drawIndexed(ByteBuffer byteBuffer, int n, ByteBuffer byteBuffer2, int n2) {
        GL33.glBindVertexArray((int)this.vertexArrayId);
        GL33.glBindBuffer((int)34962, (int)this.vertexBufferId);
        GL33.glBindBuffer((int)34963, (int)this.indexBufferId);
        GL33.glBufferData((int)34962, (ByteBuffer)byteBuffer, (int)35040);
        GL33.glBufferData((int)34963, (ByteBuffer)byteBuffer2, (int)35040);
        GL33.glDrawElements((int)4, (int)n2, (int)5125, (long)0L);
    }

    @Override
    public void resetFrameResources() {
        this.frameContextStack.clear();
        this.stencilStack.clear();
        this.glRenderStateSnapshot = null;
        this.shaderBound = false;
        this.stencilMode = 0;
    }

    @Override
    public void beginFrame(int n, int n2) {
        boolean bl = this.skipBackdropCapture;
        this.skipBackdropCapture = false;
        boolean bl2 = this.pushFrameContext();
        this.glRenderStateSnapshot = GlRenderStateSnapshot.captureRenderState();
        if (!bl2) {
            int n3;
            if (!bl) {
                this.kawaseBlurRenderer.captureMainFramebuffer(RenderFrameClock.currentFrame(), n, n2);
                this.kawaseBlurRenderer.bindResultTexture();
            }
            if ((n3 = RenderFrameClock.currentFrame()) != this.lastCleanupFrame) {
                this.lastCleanupFrame = n3;
                for (LayerFramebuffer framebuffer : this.dedicatedFramebuffers) {
                    framebuffer.setInUse(false);
                }
                for (LayerFramebuffer framebuffer : this.pooledFramebuffers) {
                    framebuffer.setInUse(false);
                }
                this.evictStaleFramebuffers(this.dedicatedFramebuffers, n3);
                this.evictStaleFramebuffers(this.pooledFramebuffers, n3);
                this.kawaseBlurRenderer.releaseIfStale(n3, 600);
            }
        }
        this.stencilMode = 0;
        this.stencilStack.clear();
        this.prepareDrawState(n, n2);
    }

    @Override
    public TextureHandle blurTexture(TextureHandle texture, float f, int n) {
        return this.gaussianBlurRenderer.blur(texture, f, n);
    }

    @Override
    public void beginLayer(TextureHandle texture) {
        LayerFramebuffer framebuffer = this.requireLayerFramebuffer(texture);
        this.pushFrameContext();
        this.glRenderStateSnapshot = GlRenderStateSnapshot.captureRenderState();
        this.stencilMode = 0;
        this.stencilStack.clear();
        GL33.glBindFramebuffer((int)36160, (int)framebuffer.framebufferId());
        GL33.glViewport((int)0, (int)0, (int)framebuffer.width(), (int)framebuffer.height());
        GL33.glDisable((int)3089);
        GL33.glColorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        GL33.glStencilMask((int)255);
        GL33.glClearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        GL33.glClearStencil((int)0);
        GL33.glClear((int)17664);
        framebuffer.incrementGeneration();
        this.prepareDrawState(framebuffer.width(), framebuffer.height());
    }

    @Override
    public void beginStencil(int n) {
        StencilFrameState stencilFrameState = this.stencilStack.peek();
        int n2 = stencilFrameState == null ? 1 : stencilFrameState.internalReference() + 1;
        StencilFrameState stencilFrameState2 = new StencilFrameState(n, n2, true);
        this.stencilStack.push(stencilFrameState2);
        if (stencilFrameState == null) {
            this.clearStencil(n2);
        } else {
            this.beginStencilWrite(stencilFrameState.internalReference());
        }
        this.stencilMode = 1;
        if (this.shaderBound && this.stencilModeUniform != null) {
            this.stencilModeUniform.set(1);
        }
    }

    @Override
    public TextureHandle acquireLayer(int n, int n2) {
        if (n <= 0 || n2 <= 0) {
            throw new IllegalArgumentException("Layer size must be positive");
        }
        n = GuiRenderBackend.clampTextureDimension(n);
        n2 = GuiRenderBackend.clampTextureDimension(n2);
        int n3 = GuiRenderBackend.alignLayerSize(n);
        int n4 = GuiRenderBackend.alignLayerSize(n2);
        LayerFramebuffer framebuffer = null;
        for (LayerFramebuffer candidate : this.pooledFramebuffers) {
            if (candidate.inUse() || candidate.width() != n3 || candidate.height() != n4) continue;
            framebuffer = candidate;
            break;
        }
        if (framebuffer == null) {
            framebuffer = new LayerFramebuffer(n3, n4);
            this.pooledFramebuffers.add(framebuffer);
        }
        framebuffer.setInUse(true);
        framebuffer.markUsed(RenderFrameClock.currentFrame());
        return framebuffer;
    }

    @Override
    public TextureHandle acquireDedicatedLayer(int n, int n2) {
        LayerFramebuffer object;
        if (n <= 0 || n2 <= 0) {
            throw new IllegalArgumentException("Layer size must be positive");
        }
        n = GuiRenderBackend.clampTextureDimension(n);
        n2 = GuiRenderBackend.clampTextureDimension(n2);
        LayerFramebuffer matching = null;
        LayerFramebuffer reusable = null;
        for (LayerFramebuffer candidate : this.dedicatedFramebuffers) {
            if (candidate.inUse()) continue;
            if (candidate.width() == n && candidate.height() == n2) {
                matching = candidate;
                break;
            }
            if (reusable != null) continue;
            reusable = candidate;
        }
        if (matching != null) {
            object = matching;
        } else if (reusable != null) {
            reusable.resize(n, n2);
            object = reusable;
        } else {
            object = new LayerFramebuffer(n, n2);
            this.dedicatedFramebuffers.add(object);
        }
        object.setInUse(true);
        object.markUsed(RenderFrameClock.currentFrame());
        return object;
    }

    @Override
    public void endStencil() {
        if (this.stencilStack.isEmpty()) {
            throw new IllegalStateException("Stencil stack is empty");
        }
        this.stencilStack.pop();
        StencilFrameState stencilFrameState = this.stencilStack.peek();
        if (stencilFrameState == null) {
            this.disableStencilTest();
            this.stencilMode = 0;
        } else {
            StencilFrameState stencilFrameState2 = this.findParentStencilState(stencilFrameState);
            if (stencilFrameState.writeMode()) {
                if (stencilFrameState2 == null) {
                    this.beginNestedStencilWrite(stencilFrameState.internalReference());
                } else {
                    this.beginStencilWrite(stencilFrameState2.internalReference());
                }
                this.stencilMode = 1;
            } else {
                this.configureStencilRead(stencilFrameState.internalReference());
                this.stencilMode = 2;
            }
        }
        if (this.shaderBound && this.stencilModeUniform != null) {
            this.stencilModeUniform.set(this.stencilMode);
        }
    }

    @Override
    public void applyStencil(int n) {
        StencilFrameState stencilFrameState = this.stencilStack.peek();
        if (stencilFrameState == null) {
            throw new IllegalStateException("Stencil stack is empty");
        }
        if (stencilFrameState.reference() != n) {
            int n2 = stencilFrameState.reference();
            int n3 = n;
            throw new IllegalStateException("Attempted to use stencil ref " + n3 + " but active ref is " + n2);
        }
        stencilFrameState = new StencilFrameState(stencilFrameState.reference(), stencilFrameState.internalReference(), false);
        this.stencilStack.pop();
        this.stencilStack.push(stencilFrameState);
        this.configureStencilRead(stencilFrameState.internalReference());
        this.stencilMode = 2;
        if (this.shaderBound && this.stencilModeUniform != null) {
            this.stencilModeUniform.set(2);
        }
    }
}

