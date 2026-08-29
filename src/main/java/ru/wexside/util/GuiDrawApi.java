package ru.wexside.util;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.class_1041;
import net.minecraft.class_310;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import ru.wexside.misc.AlphaStack;
import ru.wexside.misc.OpenGlScissorRegion;
import ru.wexside.misc.PreparedLayer;
import ru.wexside.misc.ScissorStack;
import ru.wexside.misc.TextureHandle;
import ru.wexside.render.GuiDrawMode;
import ru.wexside.render.RenderFrameClock;
import ru.wexside.util.GuiRenderDriver;
import ru.wexside.util.GuiVertexBuffer;
import ru.wexside.util.RenderContextState;
import ru.wexside.util.RenderFrameState;
import ru.wexside.util.StencilState;

public final class GuiDrawApi {
    private final Deque<RenderFrameState> frameStack;
    private AlphaStack currentAlpha;
    private boolean contextStatePreserved = false;
    private boolean layerFrame;
    private final Matrix4f matrix4f;
    private int lastFrameId;
    private Deque<RenderContextState> contextStack;
    private int frameRetentionCount;
    private boolean drawing = false;
    private final Deque<ScissorStack> scissorPool;
    private final Vector4f scratchVector;
    private Deque<StencilState> stencilStack;
    private int stencilMode;
    private int framebufferWidth;
    private ScissorStack currentScissors;
    private final GuiVertexBuffer vertexBuffer = new GuiVertexBuffer();
    private final GuiRenderDriver renderDriver;
    private final Deque<AlphaStack> alphaPool;
    private boolean pendingFinish;
    private float layerOffsetX;
    private int framebufferHeight;
    private float layerOffsetY;

    public GuiDrawApi(GuiRenderDriver renderDriver) {
        this.currentAlpha = new AlphaStack();
        this.currentScissors = new ScissorStack();
        this.stencilStack = new ArrayDeque<StencilState>();
        this.matrix4f = new Matrix4f();
        this.scratchVector = new Vector4f();
        this.contextStack = new ArrayDeque<RenderContextState>();
        this.frameStack = new ArrayDeque<RenderFrameState>();
        this.lastFrameId = Integer.MIN_VALUE;
        this.alphaPool = new ArrayDeque<AlphaStack>();
        this.scissorPool = new ArrayDeque<ScissorStack>();
        this.renderDriver = renderDriver;
    }

    public void drawRoundedTexture(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, int n, int n2) {
        this.vertexBuffer.process(matrix4f, f, f2, f3, f4, f5, f6, f7, f8, f9, f9, f9, f9, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1, n2, n2, n2, n2, n, GuiDrawMode.ROUNDED_TEXTURE);
    }

    public void drawRoundedRectangleBordered(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, int n) {
        this.drawRoundedRectangleDetailed(matrix4f, f, f2, f3, f4, f5, f5, f5, f5, 0.0f, f6, -1, n, n, n, n);
    }

    public void pushScreenScissor(float f, float f2, float f3, float f4) {
        int n = Math.round(f);
        int n2 = Math.round(f2);
        int n3 = Math.round(f3);
        int n4 = Math.round(f4);
        n2 = this.framebufferHeight - n2 - n4;
        this.flush();
        this.currentScissors.push(n, n2, n3, n4);
        this.renderDriver.setScissor(n, n2, n3, n4);
    }

    public void drawTexture(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int n, int n2) {
        this.drawTextureGradient(matrix4f, f, f2, f3, f4, f5, f6, f7, f8, n, n2, n2, n2, n2);
    }

    public int bindTexture(int n, int n2, int n3) {
        int n4 = this.renderDriver.bindTexture(n, n2, n3);
        if (n4 == -1) {
            this.flush();
            this.renderDriver.resetTextureBindings();
            n4 = this.renderDriver.bindTexture(n, n2, n3);
        }
        return n4;
    }

    public void fillRectangle(Matrix4f matrix4f, float f, float f2, float f3, float f4, int n) {
        this.drawColorGradient(matrix4f, f, f2, f3, f4, n, n, n, n);
    }

    public void end() {
        if (!this.drawing) {
            throw new IllegalStateException("DrawApi not begun");
        }
        if (this.contextStack.isEmpty() && this.layerFrame) {
            throw new IllegalStateException("Use endLayerFrame() for layer frames");
        }
        if (!this.contextStack.isEmpty()) {
            this.endNestedContext();
            return;
        }
        this.endRootFrame();
    }

    public void begin() {
        this.ensureCurrentFrame();
        if (!this.drawing) {
            class_1041 window = class_310.method_1551().method_22683();
            this.beginFrame(window.method_4489(), window.method_4506(), false, null);
            if (this.frameRetentionCount > 0) {
                this.pendingFinish = true;
                this.beginNestedContext();
            }
            return;
        }
        this.beginNestedContext();
    }

    public void drawRoundedOutline(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, int n) {
        this.drawGradientRoundedRectangleDetailed(matrix4f, f, f2, f3, f4, f5, f5, f5, f5, f6, n, n, n, n);
    }

    private void resetFrameState() {
        this.drawing = false;
        this.pendingFinish = false;
        this.layerFrame = false;
        this.framebufferWidth = 0;
        this.framebufferHeight = 0;
        this.stencilMode = 0;
        this.vertexBuffer.update();
        this.currentAlpha = this.acquireAlphaStack();
        this.currentScissors = this.acquireScissorStack();
        this.contextStatePreserved = false;
        this.contextStack = new ArrayDeque<RenderContextState>();
        this.stencilStack = new ArrayDeque<StencilState>();
        this.frameStack.clear();
    }

    public void drawGradientRoundedRectangle(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int n, int n2, int n3, int n4) {
        this.drawRoundedRectangleDetailed(matrix4f, f, f2, f3, f4, f5, f6, f7, f8, 0.0f, -0.5f, -1, n, n2, n3, n4);
    }

    public float getLayerOffsetX() {
        return this.layerOffsetX;
    }

    public PreparedLayer prepareDedicatedLayer(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5) {
        float f6 = f3 + f5 * 2.0f;
        float f7 = f4 + f5 * 2.0f;
        float f8 = Math.max(Math.abs(matrix4f.m00()), 1.0f);
        float f9 = Math.max(Math.abs(matrix4f.m11()), 1.0f);
        float f10 = f6 * f8;
        float f11 = f7 * f9;
        int n = Math.max(1, (int)Math.ceil(f10));
        int n2 = Math.max(1, (int)Math.ceil(f11));
        float f12 = Math.min(1.0f, f10 / (float)n);
        float f13 = Math.min(1.0f, f11 / (float)n2);
        return new PreparedLayer(this.acquireDedicatedLayer(n, n2), new Matrix4f().scale(f8, f9, 1.0f), f5, f5, f - f5, f2 - f5, f6, f7, f12, f13);
    }

    public void finishIfIdle() {
        if (!this.pendingFinish || !this.drawing || this.layerFrame || !this.contextStack.isEmpty()) {
            return;
        }
        this.pendingFinish = false;
        this.endRootFrame();
    }

    public void drawRoundedLayerTextureCorners(Matrix4f matrix4f, TextureHandle texture, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, int n) {
        int n2 = this.bindTextureHandle(texture);
        this.vertexBuffer.process(matrix4f, f, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1, n, n, n, n, n2, GuiDrawMode.ROUNDED_LAYER_TEXTURE);
    }

    private void endNestedContext() {
        this.flush();
        this.currentScissors.verifyBalanced();
        this.currentAlpha.verifyBalanced();
        this.recycleContextStacks();
        RenderContextState context = this.contextStack.pop();
        this.currentAlpha = context.colors();
        this.currentScissors = context.scissors();
        this.stencilMode = context.stencilMode();
        this.stencilStack = context.stencilStates();
        this.renderDriver.setStencilMode(this.stencilMode);
        OpenGlScissorRegion openGlScissorRegion = this.currentScissors.peek();
        this.renderDriver.setScissor(openGlScissorRegion.getX(), openGlScissorRegion.getY(), openGlScissorRegion.getWidth(), openGlScissorRegion.getHeight());
        this.vertexBuffer.update();
    }

    public void drawRoundedRectangleGradient(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, int n, int n2, int n3, int n4) {
        this.drawRoundedRectangleDetailed(matrix4f, f, f2, f3, f4, f5, f5, f5, f5, 0.0f, -0.5f, -1, n, n2, n3, n4);
    }

    public void drawTintedTexture(Matrix4f matrix4f, float f, float f2, float f3, float f4, int n, int n2) {
        this.drawTextureGradient(matrix4f, f, f2, f3, f4, 0.0f, 0.0f, 1.0f, 1.0f, n, n2, n2, n2, n2);
    }

    public void drawRoundedTextureTinted(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, int n, int n2) {
        this.drawRoundedTextureGradient(matrix4f, f, f2, f3, f4, f5, f5, f5, f5, n, n2, n2, n2, n2);
    }

    public void drawTextureGradient(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int n, int n2, int n3, int n4, int n5) {
        this.vertexBuffer.process(matrix4f, f, f2, f3, f4, f5, f6, f7, f8, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1, n2, n3, n4, n5, n, GuiDrawMode.TEXTURE);
    }

    public void drawRoundedShadowCorners(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, int n) {
        this.vertexBuffer.process(matrix4f, f, f2, f3, f4, 0.0f, 0.0f, 1.0f, 1.0f, f5, f6, f7, f8, 0.0f, f9, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0, n, n, n, n, 0, GuiDrawMode.ROUNDED_SHADOW);
    }

    private void flush() {
        if (this.vertexBuffer.getIntType() == 0) {
            return;
        }
        this.renderDriver.drawIndexed(this.vertexBuffer.getByteBuffer(), GuiDrawApi.getVertexStride(), this.vertexBuffer.getByteBuffer2(), this.vertexBuffer.getIntType());
        this.vertexBuffer.update();
    }

    private void endRootFrame() {
        this.flush();
        this.currentScissors.verifyBalanced();
        this.currentAlpha.verifyBalanced();
        this.renderDriver.endFrame();
        this.recycleContextStacks();
        this.resetFrameState();
    }

    public int bindTextureHandle(TextureHandle texture) {
        return this.bindTexture(texture.getTextureId(), texture.getWidth(), texture.getHeight());
    }

    private static int getVertexStride() {
        return 86;
    }

    public void flushPending() {
        this.flush();
    }

    public void drawRoundedRectangleCorners(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int n) {
        this.drawRoundedRectangleDetailed(matrix4f, f, f2, f3, f4, f5, f6, f7, f8, 0.0f, -0.5f, -1, n, n, n, n);
    }

    private void beginFrame(int n, int n2, boolean bl, TextureHandle texture) {
        this.drawing = true;
        this.framebufferWidth = n;
        this.framebufferHeight = n2;
        if (!bl) {
            this.layerOffsetX = 0.0f;
            this.layerOffsetY = 0.0f;
        }
        this.layerFrame = bl;
        this.stencilMode = 0;
        this.matrix4f.identity().ortho2D(0.0f, (float)this.framebufferWidth, (float)this.framebufferHeight, 0.0f);
        if (bl) {
            this.renderDriver.beginLayer(texture);
        } else {
            this.renderDriver.beginFrame(this.framebufferWidth, this.framebufferHeight);
        }
        this.renderDriver.setProjectionMatrix(this.matrix4f);
        this.renderDriver.setViewportSize(this.framebufferWidth, this.framebufferHeight);
        this.renderDriver.setStencilMode(this.stencilMode);
        this.contextStack = new ArrayDeque<RenderContextState>();
        this.stencilStack = new ArrayDeque<StencilState>();
        this.initializeContext();
    }

    public void drawShadow(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, int n) {
        this.vertexBuffer.process(matrix4f, f, f2, f3, f4, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, f5, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0, n, n, n, n, 0, GuiDrawMode.SHADOW);
    }

    public void setStencilWriteEnabled(boolean bl) {
        this.flush();
        if (!this.stencilStack.isEmpty()) {
            this.stencilStack.peek().setMode(bl ? 1 : 0);
        }
        this.stencilMode = bl ? 1 : 0;
        this.renderDriver.setStencilMode(this.stencilMode);
    }

    public void drawRingSector(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, int n, int n2) {
        float f8 = (float)Math.toRadians(f3);
        float f9 = (float)Math.toRadians(f4);
        float f10 = f6 * 2.0f;
        float f11 = f - f6;
        float f12 = f2 - f6;
        this.vertexBuffer.process(matrix4f, f11, f12, f10, f10, 0.0f, 0.0f, 0.0f, 0.0f, f8, f9, f5, f6, f7, 0.75f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, n2, n, n, n, n, 0, GuiDrawMode.RING_SECTOR);
    }

    public void applyStencilMask(int n) {
        this.flush();
        if (this.stencilStack.isEmpty()) {
            throw new IllegalStateException("Stencil stack is empty");
        }
        StencilState stencilState = this.stencilStack.peek();
        if (stencilState.reference() != n) {
            int n2 = stencilState.reference();
            int n3 = n;
            throw new IllegalStateException("Attempted to use stencil ref " + n3 + " but active ref is " + n2);
        }
        stencilState.setMode(2);
        this.stencilMode = 2;
        this.renderDriver.applyStencil(n);
    }

    private Deque<StencilState> copyStencilStates() {
        ArrayDeque<StencilState> arrayDeque = new ArrayDeque<StencilState>();
        for (StencilState stencilState : this.stencilStack) {
            arrayDeque.addLast(new StencilState(stencilState.reference(), stencilState.mode()));
        }
        return arrayDeque;
    }

    public void pushScissor(Matrix4f matrix4f, float f, float f2, float f3, float f4) {
        Vector4f scratchVector = matrix4f.transform(this.scratchVector.set(f, f2, 0.0f, 1.0f));
        Vector4f vector4f2 = matrix4f.transform(this.scratchVector.set(f + f3, f2 + f4, 0.0f, 1.0f));
        int n = (int)scratchVector.x;
        int n2 = (int)((float)this.framebufferHeight - vector4f2.y);
        int n3 = (int)(vector4f2.x - scratchVector.x);
        int n4 = (int)(vector4f2.y - scratchVector.y);
        this.flush();
        this.currentScissors.push(n, n2, n3, n4);
        this.renderDriver.setScissor(n, n2, n3, n4);
    }

    public TextureHandle acquireDedicatedLayer(int n, int n2) {
        return this.renderDriver.acquireDedicatedLayer(n, n2);
    }

    public void drawRoundedRectangleBorderGradient(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, int n, int n2) {
        this.drawRoundedRectangleDetailed(matrix4f, f, f2, f3, f4, f5, f5, f5, f5, f6, 0.0f, n, n2, n2, n2, n2);
    }

    private void endLayerFrameInternal() {
        this.flush();
        this.currentScissors.verifyBalanced();
        this.currentAlpha.verifyBalanced();
        this.renderDriver.endLayerFrame();
        this.recycleContextStacks();
        if (this.frameStack.isEmpty()) {
            this.resetFrameState();
            return;
        }
        this.setRenderFrameState(this.frameStack.pop());
    }

    private void recycleContextStacks() {
        if (this.alphaPool.size() < 16) {
            this.alphaPool.push(this.currentAlpha);
        }
        if (this.scissorPool.size() < 16) {
            this.scissorPool.push(this.currentScissors);
        }
    }

    public TextureHandle acquireLayer(int n, int n2) {
        return this.renderDriver.acquireLayer(n, n2);
    }

    public void drawRoundedRectangleDetailed(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, int n, int n2, int n3, int n4, int n5) {
        this.vertexBuffer.process(matrix4f, f, f2, f3, f4, 0.0f, 0.0f, 1.0f, 1.0f, f5, f6, f7, f8, f9, f10, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, n, n2, n3, n4, n5, 0, GuiDrawMode.ROUNDED_RECTANGLE);
    }

    private AlphaStack acquireAlphaStack() {
        AlphaStack currentAlpha = this.alphaPool.poll();
        return currentAlpha == null ? new AlphaStack() : currentAlpha;
    }

    public int bindPreparedLayer(PreparedLayer preparedLayer) {
        return this.bindTextureHandle(preparedLayer.getTexture());
    }

    public TextureHandle blurTexture(TextureHandle texture, float f) {
        return this.renderDriver.blurTexture(texture, f, RenderFrameClock.currentFrame());
    }

    public void beginStencil(int n) {
        this.flush();
        this.stencilStack.push(new StencilState(n, 1));
        this.stencilMode = 1;
        this.renderDriver.beginStencil(n);
    }

    public void drawRoundedTextureGradient(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int n, int n2, int n3, int n4, int n5) {
        this.vertexBuffer.process(matrix4f, f, f2, f3, f4, 0.0f, 0.0f, 1.0f, 1.0f, f5, f6, f7, f8, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1, n2, n3, n4, n5, n, GuiDrawMode.ROUNDED_TEXTURE);
    }

    private void beginNestedContext() {
        this.flush();
        this.contextStack.push(new RenderContextState(this.currentAlpha, this.currentScissors, this.stencilMode, this.stencilStack));
        this.contextStatePreserved = true;
        this.stencilStack = this.copyStencilStates();
        this.initializeContext();
    }

    public void drawRoundedRectangle(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, int n) {
        this.drawRoundedRectangleDetailed(matrix4f, f, f2, f3, f4, f5, f5, f5, f5, 0.0f, -0.5f, -1, n, n, n, n);
    }

    public void drawRoundedRectangleRadii(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int n) {
        this.drawRoundedRectangleDetailed(matrix4f, f, f2, f3, f4, f5, f6, f7, f8, 0.0f, -0.5f, -1, n, n, n, n);
    }

    public float getLayerOffsetY() {
        return this.layerOffsetY;
    }

    public void drawRoundedTextureColorGradient(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, int n, int n2, int n3, int n4, int n5) {
        this.drawRoundedTextureGradient(matrix4f, f, f2, f3, f4, f5, f5, f5, f5, n, n2, n3, n4, n5);
    }

    private ScissorStack acquireScissorStack() {
        ScissorStack currentScissors = this.scissorPool.poll();
        return currentScissors == null ? new ScissorStack() : currentScissors;
    }

    public void drawShimmer(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, int n) {
        this.drawShimmerHighlight(matrix4f, f, f2, f3, f4, f5, f6, n, -1);
    }

    public PreparedLayer prepareLayer(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5) {
        float f6 = f3 + f5 * 2.0f;
        float f7 = f4 + f5 * 2.0f;
        float f8 = Math.max(Math.abs(matrix4f.m00()), 1.0f);
        float f9 = Math.max(Math.abs(matrix4f.m11()), 1.0f);
        float f10 = f6 * f8;
        float f11 = f7 * f9;
        int n = Math.max(1, (int)Math.ceil(f10));
        int n2 = Math.max(1, (int)Math.ceil(f11));
        TextureHandle texture = this.acquireLayer(n, n2);
        float f12 = Math.min(1.0f, f10 / (float)texture.getWidth());
        float f13 = Math.min(1.0f, f11 / (float)texture.getHeight());
        return new PreparedLayer(texture, new Matrix4f().scale(f8, f9, 1.0f), f5, f5, f - f5, f2 - f5, f6, f7, f12, f13);
    }

    public void drawRoundedTextureRadii(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int n, int n2) {
        this.drawRoundedTextureGradient(matrix4f, f, f2, f3, f4, f5, f6, f7, f8, n, n2, n2, n2, n2);
    }

    public void drawBackdrop(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, int n, int n2) {
        this.drawBackdropStack(matrix4f, f, f2, f3, f4, f5, f5, f5, f5, n, n2);
    }

    private void initializeContext() {
        if (!this.contextStatePreserved) {
            this.recycleContextStacks();
        }
        this.currentAlpha = this.acquireAlphaStack();
        this.currentAlpha.reset();
        this.currentScissors = this.acquireScissorStack();
        this.contextStatePreserved = false;
        this.currentScissors.push(0, 0, this.framebufferWidth, this.framebufferHeight);
        this.renderDriver.setScissor(0, 0, this.framebufferWidth, this.framebufferHeight);
        this.vertexBuffer.update();
    }

    public void drawPreparedLayer(Matrix4f matrix4f, PreparedLayer preparedLayer, int n) {
        this.drawLayerTexture(matrix4f, preparedLayer.getTexture(), preparedLayer.drawX(), preparedLayer.drawY(), preparedLayer.drawWidth(), preparedLayer.drawHeight(), 0.0f, 1.0f, preparedLayer.maxU(), 1.0f - preparedLayer.maxV(), n);
    }

    public void drawBlurredRectangle(Matrix4f matrix4f, float f, float f2, float f3, float f4) {
        this.vertexBuffer.process(matrix4f, f, f2, f3, f4, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, this.layerOffsetX, this.layerOffsetY, 0, 0, 0, 0, 0, this.renderDriver.getBackdropTextureSlot(), GuiDrawMode.BLURRED_RECTANGLE);
    }

    public GuiVertexBuffer getGuiVertexBuffer() {
        return this.vertexBuffer;
    }

    public GuiRenderDriver getRenderDriver() {
        return this.renderDriver;
    }

    public void drawRoundedRectangleBorder(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, int n, int n2) {
        this.drawRoundedRectangleDetailed(matrix4f, f, f2, f3, f4, f5, f6, f7, f8, f9, 0.0f, n, n2, n2, n2, n2);
    }

    public Matrix4f getMatrix4f() {
        return this.matrix4f;
    }

    public void drawRoundedLayerTextureGradient(Matrix4f matrix4f, TextureHandle texture, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, int n, int n2, int n3, int n4) {
        int n5 = this.bindTextureHandle(texture);
        this.vertexBuffer.process(matrix4f, f, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1, n, n2, n3, n4, n5, GuiDrawMode.ROUNDED_LAYER_TEXTURE);
    }

    public void drawTextureColorGradient(Matrix4f matrix4f, float f, float f2, float f3, float f4, int n, int n2, int n3, int n4, int n5) {
        this.drawTextureGradient(matrix4f, f, f2, f3, f4, 0.0f, 0.0f, 1.0f, 1.0f, n, n2, n3, n4, n5);
    }

    public void drawLayerTexture(Matrix4f matrix4f, TextureHandle texture, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int n) {
        int n2 = this.bindTextureHandle(texture);
        this.vertexBuffer.process(matrix4f, f, f2, f3, f4, f5, f6, f7, f8, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1, n, n, n, n, n2, GuiDrawMode.LAYER_TEXTURE);
    }

    public void drawCircleSector(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, int n, float f7) {
        float f8 = (float)Math.toRadians(f3) / f7;
        float f9 = (float)Math.toRadians(f4) / f7;
        float f10 = f6 * 2.0f;
        float f11 = f - f6;
        float f12 = f2 - f6;
        this.vertexBuffer.process(matrix4f, f11, f12, f10, f10, 0.0f, 0.0f, 0.0f, 0.0f, f8, f9, 0.0f, 0.0f, f5, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0, n, n, n, n, 0, GuiDrawMode.CIRCLE);
    }

    public void drawPreparedLayerRounded(Matrix4f matrix4f, PreparedLayer preparedLayer, float f, int n) {
        this.drawPreparedLayerRoundedCorners(matrix4f, preparedLayer, f, f, f, f, n);
    }

    public void drawRoundedRectangleSoft(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, int n) {
        this.drawRoundedRectangleDetailed(matrix4f, f, f2, f3, f4, f5, f6, f7, f8, 0.0f, f9, -1, n, n, n, n);
    }

    public void drawPreparedLayerRoundedCorners(Matrix4f matrix4f, PreparedLayer preparedLayer, float f, float f2, float f3, float f4, int n) {
        int n2 = this.bindPreparedLayer(preparedLayer);
        this.vertexBuffer.process(matrix4f, preparedLayer.drawX(), preparedLayer.drawY(), preparedLayer.drawWidth(), preparedLayer.drawHeight(), 0.0f, 1.0f, preparedLayer.maxU(), 1.0f - preparedLayer.maxV(), f, f2, f3, f4, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1, n, n, n, n, n2, GuiDrawMode.ROUNDED_LAYER_TEXTURE);
    }

    public void drawGradientRoundedRectangleUniform(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, int n, int n2, int n3, int n4) {
        this.drawGradientRoundedRectangleDetailed(matrix4f, f, f2, f3, f4, f5, f5, f5, f5, f6, n, n2, n3, n4);
    }

    public void drawBlurredRoundedRectangleCorners(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        this.vertexBuffer.process(matrix4f, f, f2, f3, f4, 0.0f, 0.0f, 1.0f, 1.0f, f5, f6, f7, f8, 0.0f, -0.5f, 0.0f, 0.0f, 0.0f, this.layerOffsetX, this.layerOffsetY, 0, 0, 0, 0, 0, this.renderDriver.getBackdropTextureSlot(), GuiDrawMode.BLURRED_ROUNDED_RECTANGLE);
    }

    public void drawBackdropStack(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int n, int n2) {
        this.vertexBuffer.process(matrix4f, f, f2, f3, f4, 0.0f, 0.0f, 1.0f, 1.0f, f5, f6, f7, f8, 0.0f, -0.5f, 0.0f, 0.0f, 0.0f, this.layerOffsetX, this.layerOffsetY, n, n2, n2, n2, n2, this.renderDriver.getBackdropTextureSlot(), GuiDrawMode.BACKDROP_STACK);
    }

    public void drawSplitGradientRectangle(Matrix4f matrix4f, float f, float f2, float f3, float f4, int n, int n2, int n3, int n4) {
        this.vertexBuffer.process2(matrix4f, f, f2, f3, f4, n, n2, n3, n4);
    }

    public void drawShimmerHighlight(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, int n, int n2) {
        this.vertexBuffer.process(matrix4f, f, f2, f3, f4, 0.0f, 0.0f, 1.0f, 1.0f, f5, f5, f5, f5, f6, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, n2, n, n, n, n, 0, GuiDrawMode.SHIMMER_HIGHLIGHT);
    }

    public void drawLayerTextureFlipped(Matrix4f matrix4f, TextureHandle texture, float f, float f2, float f3, float f4, int n) {
        this.drawLayerTexture(matrix4f, texture, f, f2, f3, f4, 0.0f, 1.0f, 1.0f, 0.0f, n);
    }

    private void ensureCurrentFrame() {
        int n = RenderFrameClock.currentFrame();
        if (n == this.lastFrameId) {
            return;
        }
        this.lastFrameId = n;
        if (!this.drawing) {
            return;
        }
        this.resetFrameState();
        this.renderDriver.resetFrameResources();
    }

    public TextureHandle blurTextureRange(TextureHandle texture, float f, float f2, float f3, float f4) {
        return this.renderDriver.blurTextureRange(texture, f, f2, f3, f4, RenderFrameClock.currentFrame());
    }

    public void drawRoundedRectangleOutlined(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, int n, int n2) {
        this.drawRoundedRectangleDetailed(matrix4f, f, f2, f3, f4, f5, f5, f5, f5, f6, 0.0f, n2, n, n, n, n);
    }

    public void beginLayerFrame(TextureHandle texture, float f, float f2) {
        if (this.drawing) {
            this.flush();
            this.frameStack.push(new RenderFrameState(this.currentAlpha, this.currentScissors, this.contextStack, this.stencilStack, this.framebufferWidth, this.framebufferHeight, this.stencilMode, this.layerFrame, this.layerOffsetX, this.layerOffsetY));
            this.contextStatePreserved = true;
        }
        this.layerOffsetX = f;
        this.layerOffsetY = f2;
        this.beginFrame(texture.getWidth(), texture.getHeight(), true, texture);
    }

    public void drawPreparedLayerDefault(Matrix4f matrix4f, PreparedLayer preparedLayer) {
        this.drawPreparedLayer(matrix4f, preparedLayer, -1);
    }

    public int getFramebufferHeight() {
        return this.framebufferHeight;
    }

    public void drawGradientRoundedRectangleDetailed(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, int n, int n2, int n3, int n4) {
        float f10 = Math.max(Math.abs(matrix4f.m00()), 1.0f);
        float f11 = Math.max(Math.abs(matrix4f.m11()), 1.0f);
        float f12 = (float)Math.round(f * f10) / f10;
        float f13 = (float)Math.round(f2 * f11) / f11;
        float f14 = (float)Math.round((f + f3) * f10) / f10;
        float f15 = (float)Math.round((f2 + f4) * f11) / f11;
        this.vertexBuffer.process(matrix4f, f12, f13, f14 - f12, f15 - f13, 0.0f, 0.0f, 1.0f, 1.0f, f5, f6, f7, f8, f9, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0, n, n2, n3, n4, 0, GuiDrawMode.GRADIENT_ROUNDED_RECTANGLE);
    }

    public void beginLayerFrame(TextureHandle texture) {
        this.beginLayerFrame(texture, 0.0f, 0.0f);
    }

    public void drawRoundedShadow(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, int n) {
        this.drawRoundedShadowCorners(matrix4f, f, f2, f3, f4, f5, f5, f5, f5, f6, n);
    }

    public void drawBlurredRoundedRectangle(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5) {
        this.drawBlurredRoundedRectangleCorners(matrix4f, f, f2, f3, f4, f5, f5, f5, f5);
    }

    public void drawLayerTextureGradient(Matrix4f matrix4f, TextureHandle texture, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int n, int n2, int n3, int n4) {
        int n5 = this.bindTextureHandle(texture);
        this.vertexBuffer.process(matrix4f, f, f2, f3, f4, f5, f6, f7, f8, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1, n, n2, n3, n4, n5, GuiDrawMode.LAYER_TEXTURE);
    }

    public void retainFrame() {
        ++this.frameRetentionCount;
    }

    public void releaseFrame() {
        if (this.frameRetentionCount == 0) {
            return;
        }
        --this.frameRetentionCount;
        if (this.frameRetentionCount > 0 || !this.pendingFinish) {
            return;
        }
        this.pendingFinish = false;
        while (this.drawing && this.layerFrame) {
            this.endLayerFrameInternal();
        }
        while (this.drawing && !this.contextStack.isEmpty()) {
            this.endNestedContext();
        }
        if (this.drawing) {
            this.endRootFrame();
        }
    }

    public void drawRoundedLayerTexture(Matrix4f matrix4f, TextureHandle texture, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, int n) {
        this.drawRoundedLayerTextureCorners(matrix4f, texture, f, f2, f3, f4, f5, f6, f7, f8, f9, f9, f9, f9, n);
    }

    public void popScissor() {
        this.flush();
        this.currentScissors.pop();
        OpenGlScissorRegion openGlScissorRegion = this.currentScissors.peek();
        this.renderDriver.setScissor(openGlScissorRegion.getX(), openGlScissorRegion.getY(), openGlScissorRegion.getWidth(), openGlScissorRegion.getHeight());
    }

    public AlphaStack getAlphaStack() {
        return this.currentAlpha;
    }

    public void drawRoundedRectangleAdvanced(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, int n, int n2) {
        this.drawRoundedRectangleDetailed(matrix4f, f, f2, f3, f4, f5, f6, f7, f8, f9, f10, n, n2, n2, n2, n2);
    }

    private void setRenderFrameState(RenderFrameState renderFrameState) {
        this.currentAlpha = renderFrameState.alphaStack();
        this.currentScissors = renderFrameState.scissorStack();
        this.contextStack = renderFrameState.contextStates();
        this.stencilStack = renderFrameState.stencilStates();
        this.framebufferWidth = renderFrameState.framebufferWidth();
        this.framebufferHeight = renderFrameState.framebufferHeight();
        this.stencilMode = renderFrameState.stencilMode();
        this.layerFrame = renderFrameState.layerFrame();
        this.layerOffsetX = renderFrameState.layerOriginX();
        this.layerOffsetY = renderFrameState.layerOriginY();
        this.matrix4f.identity().ortho2D(0.0f, (float)this.framebufferWidth, (float)this.framebufferHeight, 0.0f);
        this.renderDriver.setProjectionMatrix(this.matrix4f);
        this.renderDriver.setViewportSize(this.framebufferWidth, this.framebufferHeight);
        this.renderDriver.setStencilMode(this.stencilMode);
        OpenGlScissorRegion openGlScissorRegion = this.currentScissors.peek();
        this.renderDriver.setScissor(openGlScissorRegion.getX(), openGlScissorRegion.getY(), openGlScissorRegion.getWidth(), openGlScissorRegion.getHeight());
        this.vertexBuffer.update();
    }

    public void endStencil() {
        this.flush();
        if (this.stencilStack.isEmpty()) {
            throw new IllegalStateException("Stencil stack is empty");
        }
        this.stencilStack.pop();
        this.renderDriver.endStencil();
        this.stencilMode = this.stencilStack.isEmpty() ? 0 : this.stencilStack.peek().mode();
        this.renderDriver.setStencilMode(this.stencilMode);
    }

    public void endLayerFrame() {
        if (!this.layerFrame) {
            throw new IllegalStateException("DrawApi current frame is not a layer frame");
        }
        if (!this.contextStack.isEmpty()) {
            throw new IllegalStateException("Close nested begin()/end() contexts before endLayerFrame()");
        }
        this.endLayerFrameInternal();
    }

    public void drawCircle(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, int n) {
        float f7 = (float)Math.toRadians(f3);
        float f8 = (float)Math.toRadians(f4);
        float f9 = f6 * 2.0f;
        float f10 = f - f6;
        float f11 = f2 - f6;
        this.vertexBuffer.process(matrix4f, f10, f11, f9, f9, 0.0f, 0.0f, 0.0f, 0.0f, f7, f8, 0.0f, 0.0f, f5, 0.75f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0, n, n, n, n, 0, GuiDrawMode.CIRCLE);
    }

    public void drawRoundedRectangleAdvancedUniform(Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, int n) {
        this.drawRoundedRectangleDetailed(matrix4f, f, f2, f3, f4, f5, f6, f7, f8, 0.0f, f9, -1, n, n, n, n);
    }

    public void drawColorGradient(Matrix4f matrix4f, float f, float f2, float f3, float f4, int n, int n2, int n3, int n4) {
        this.vertexBuffer.process(matrix4f, f, f2, f3, f4, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1, n, n2, n3, n4, 0, GuiDrawMode.COLOR);
    }
}

