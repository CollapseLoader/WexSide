package ru.wexside.util;

import java.nio.ByteBuffer;
import org.joml.Matrix4f;
import ru.wexside.misc.TextureHandle;

public interface GuiRenderDriver {
    default public void endFrame() {
    }

    public void setViewportSize(int var1, int var2);

    default public int getBackdropTextureSlot() {
        return 5;
    }

    public void endLayerFrame();

    public int bindTexture(int var1, int var2, int var3);

    public void setProjectionMatrix(Matrix4f var1);

    default public TextureHandle blurTextureRange(TextureHandle texture, float startRadius, float endRadius, float startOffset, float endOffset, int frameId) {
        return this.blurTexture(texture, Math.max(startRadius, endRadius), frameId);
    }

    public void setStencilMode(int var1);

    public void close();

    default public void prepareBackdrop() {
    }

    default public void setBackdropBlurRadius(float radius) {
    }

    public void setScissor(int var1, int var2, int var3, int var4);

    public void resetTextureBindings();

    public void drawIndexed(ByteBuffer var1, int var2, ByteBuffer var3, int var4);

    default public void resetFrameResources() {
    }

    public void beginFrame(int var1, int var2);

    default public TextureHandle blurTexture(TextureHandle texture, float radius, int frameId) {
        return texture;
    }

    public void beginLayer(TextureHandle var1);

    public void beginStencil(int var1);

    default public TextureHandle acquireLayer(int width, int height) {
        return this.acquireDedicatedLayer(width, height);
    }

    public TextureHandle acquireDedicatedLayer(int var1, int var2);

    public void endStencil();

    public void applyStencil(int var1);
}

