package ru.wexside.render;

import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL33;
import ru.wexside.misc.TextureHandle;
import ru.wexside.render.RenderFrameClock;

public final class LayerFramebuffer
implements TextureHandle {
    private int generation;
    private final int framebufferId;
    private boolean inUse;
    private int lastUsedFrame = RenderFrameClock.currentFrame();
    private int width;
    private final int depthStencilId;
    private int height;
    private final int colorTextureId;

    public LayerFramebuffer(int width, int height) {
        int previousFramebuffer = GL33.glGetInteger((int)36006);
        int previousTexture = GL33.glGetInteger((int)32873);
        int previousRenderbuffer = GL33.glGetInteger((int)36007);
        this.colorTextureId = GL33.glGenTextures();
        GL33.glBindTexture((int)3553, (int)this.colorTextureId);
        GL33.glTexParameteri((int)3553, (int)10241, (int)9728);
        GL33.glTexParameteri((int)3553, (int)10240, (int)9728);
        GL33.glTexParameteri((int)3553, (int)10242, (int)33071);
        GL33.glTexParameteri((int)3553, (int)10243, (int)33071);
        this.framebufferId = GL33.glGenFramebuffers();
        this.depthStencilId = GL33.glGenRenderbuffers();
        this.resize(width, height);
        GL33.glBindRenderbuffer((int)36161, (int)previousRenderbuffer);
        GL33.glBindTexture((int)3553, (int)previousTexture);
        GL33.glBindFramebuffer((int)36160, (int)previousFramebuffer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void resize(int width, int height) {
        int previousFramebuffer = GL33.glGetInteger((int)36006);
        int previousTexture = GL33.glGetInteger((int)32873);
        int previousRenderbuffer = GL33.glGetInteger((int)36007);
        this.width = width;
        this.height = height;
        try {
            GL33.glBindTexture((int)3553, (int)this.colorTextureId);
            GL33.glTexImage2D((int)3553, (int)0, (int)32856, (int)width, (int)height, (int)0, (int)6408, (int)5121, (ByteBuffer)null);
            GL33.glBindRenderbuffer((int)36161, (int)this.depthStencilId);
            GL33.glRenderbufferStorage((int)36161, (int)35056, (int)width, (int)height);
            GL33.glBindFramebuffer((int)36160, (int)this.framebufferId);
            GL33.glFramebufferTexture2D((int)36160, (int)36064, (int)3553, (int)this.colorTextureId, (int)0);
            GL33.glFramebufferRenderbuffer((int)36160, (int)33306, (int)36161, (int)this.depthStencilId);
            int status = GL33.glCheckFramebufferStatus((int)36160);
            if (status != 36053) {
                throw new IllegalStateException("Layer framebuffer is incomplete: " + status);
            }
        }
        finally {
            GL33.glBindRenderbuffer((int)36161, (int)previousRenderbuffer);
            GL33.glBindTexture((int)3553, (int)previousTexture);
            GL33.glBindFramebuffer((int)36160, (int)previousFramebuffer);
        }
    }

    public void delete() {
        GL33.glDeleteFramebuffers((int)this.framebufferId);
        GL33.glDeleteRenderbuffers((int)this.depthStencilId);
        GL33.glDeleteTextures((int)this.colorTextureId);
    }

    public int framebufferId() {
        return this.framebufferId;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public int generation() {
        return this.generation;
    }

    public void incrementGeneration() {
        ++this.generation;
    }

    public boolean inUse() {
        return this.inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    public int lastUsedFrame() {
        return this.lastUsedFrame;
    }

    public void markUsed(int frame) {
        this.lastUsedFrame = frame;
    }

    @Override
    public int getTextureId() {
        return this.colorTextureId;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getGeneration() {
        return this.generation;
    }
}

