package ru.wexside.render;

import java.nio.ByteBuffer;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import ru.wexside.misc.TextureHandle;
import ru.wexside.render.OverlayFramebuffer;
import ru.wexside.render.OverlaySampler;
import ru.wexside.render.RenderFrameClock;
import ru.wexside.render.WexGlobals;
import ru.wexside.util.GuiRenderBackend;
import ru.wexside.util.GuiRenderDriver;

public final class GuiRenderPipeline
implements GuiRenderDriver {
    private static final int BACKDROP_TEXTURE_SLOT = 4;
    private static final int BACKDROP_REQUEST_WINDOW_FRAMES = 4;
    private static final int BACKDROP_REFRESH_INTERVAL_FRAMES = 30;
    private int lastBackdropCaptureFrame;
    private final OverlayFramebuffer overlayFramebuffer = new OverlayFramebuffer();
    private final WexGlobals wexGlobals;
    private boolean backdropDirty;
    private boolean backdropRequested;
    private int backdropHeight = -1;
    private int lastBackdropRequestFrame;
    private final GuiRenderBackend guiRenderBackend;
    private int backdropWidth = -1;
    private final OverlaySampler overlaySampler = new OverlaySampler();

    public GuiRenderPipeline(GuiRenderBackend guiRenderBackend, WexGlobals wexGlobals) {
        this.guiRenderBackend = guiRenderBackend;
        this.wexGlobals = wexGlobals;
    }

    @Override
    public void endFrame() {
        this.guiRenderBackend.endFrame();
        this.overlaySampler.present(this.overlayFramebuffer.getColorTextureView());
    }

    @Override
    public void setViewportSize(int n, int n2) {
        this.guiRenderBackend.setViewportSize(n, n2);
    }

    @Override
    public int getBackdropTextureSlot() {
        this.lastBackdropRequestFrame = RenderFrameClock.currentFrame();
        this.backdropRequested = true;
        return 4;
    }

    @Override
    public void endLayerFrame() {
        this.guiRenderBackend.endLayerFrame();
    }

    @Override
    public int bindTexture(int n, int n2, int n3) {
        return this.guiRenderBackend.bindTexture(n, n2, n3);
    }

    @Override
    public void setProjectionMatrix(Matrix4f matrix4f) {
        this.guiRenderBackend.setProjectionMatrix(matrix4f);
    }

    @Override
    public TextureHandle blurTextureRange(TextureHandle texture, float f, float f2, float f3, float f4, int n) {
        return this.guiRenderBackend.blurTextureRange(texture, f, f2, f3, f4, n);
    }

    @Override
    public void setStencilMode(int n) {
        this.guiRenderBackend.setStencilMode(n);
    }

    @Override
    public void close() {
        this.overlayFramebuffer.close();
        this.overlaySampler.close();
    }

    @Override
    public void prepareBackdrop() {
        this.wexGlobals.prepareBackdrop();
        this.backdropDirty = true;
    }

    @Override
    public void setBackdropBlurRadius(float f) {
        this.wexGlobals.setBackdropBlurRadius(f);
        this.backdropDirty = true;
    }

    @Override
    public void setScissor(int n, int n2, int n3, int n4) {
        this.guiRenderBackend.setScissor(n, n2, n3, n4);
    }

    @Override
    public void resetTextureBindings() {
        this.guiRenderBackend.resetTextureBindings();
    }

    @Override
    public void drawIndexed(ByteBuffer byteBuffer, int n, ByteBuffer byteBuffer2, int n2) {
        this.guiRenderBackend.drawIndexed(byteBuffer, n, byteBuffer2, n2);
    }

    @Override
    public void resetFrameResources() {
        this.guiRenderBackend.resetFrameResources();
    }

    @Override
    public void beginFrame(int n, int n2) {
        this.overlayFramebuffer.ensureSize(n, n2);
        if (this.shouldRefreshBackdrop(n, n2)) {
            this.wexGlobals.captureBackdrop(n, n2);
            this.backdropDirty = false;
            this.lastBackdropCaptureFrame = RenderFrameClock.currentFrame();
            this.backdropWidth = n;
            this.backdropHeight = n2;
        }
        this.guiRenderBackend.prepareBackdrop();
        this.guiRenderBackend.beginFrame(n, n2);
        this.overlayFramebuffer.bindAndClear();
        this.bindBackdropTexture();
    }

    @Override
    public TextureHandle blurTexture(TextureHandle texture, float f, int n) {
        return this.guiRenderBackend.blurTexture(texture, f, n);
    }

    @Override
    public void beginLayer(TextureHandle texture) {
        this.guiRenderBackend.beginLayer(texture);
    }

    @Override
    public void beginStencil(int n) {
        this.guiRenderBackend.beginStencil(n);
    }

    @Override
    public TextureHandle acquireLayer(int n, int n2) {
        return this.guiRenderBackend.acquireLayer(n, n2);
    }

    @Override
    public TextureHandle acquireDedicatedLayer(int n, int n2) {
        return this.guiRenderBackend.acquireDedicatedLayer(n, n2);
    }

    @Override
    public void endStencil() {
        this.guiRenderBackend.endStencil();
    }

    @Override
    public void applyStencil(int n) {
        this.guiRenderBackend.applyStencil(n);
    }

    private void bindBackdropTexture() {
        int textureId = this.wexGlobals.getBackdropTextureId();
        if (textureId <= 0) {
            return;
        }
        GL13.glActiveTexture((int)33988);
        GL11.glBindTexture((int)3553, (int)textureId);
        GL13.glActiveTexture((int)33984);
    }

    private boolean shouldRefreshBackdrop(int width, int height) {
        if (this.backdropDirty || this.wexGlobals.getBackdropTextureId() <= 0 || this.backdropWidth != width || this.backdropHeight != height) {
            return true;
        }
        int currentFrame = RenderFrameClock.currentFrame();
        int framesSinceRequest = currentFrame - this.lastBackdropRequestFrame;
        if (this.backdropRequested && framesSinceRequest >= 0 && framesSinceRequest <= 4) {
            return true;
        }
        int framesSinceCapture = currentFrame - this.lastBackdropCaptureFrame;
        return framesSinceCapture < 0 || framesSinceCapture >= 30;
    }
}

