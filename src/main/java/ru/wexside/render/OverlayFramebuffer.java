package ru.wexside.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import net.minecraft.class_10868;
import org.lwjgl.opengl.GL30;

class OverlayFramebuffer {
    private GpuTexture colorTexture;
    private int colorTextureId;
    private int width;
    private int height;
    private int framebufferId;
    private int depthStencilRenderbufferId;
    private GpuTextureView colorTextureView;

    OverlayFramebuffer() {
    }

    void ensureSize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        if (this.width == width && this.height == height && this.framebufferId != 0) {
            return;
        }
        this.close();
        this.colorTexture = RenderSystem.getDevice().createTexture(() -> "wex/overlay-stage", 12, TextureFormat.RGBA8, width, height, 1, 1);
        this.colorTextureView = RenderSystem.getDevice().createTextureView(this.colorTexture);
        GpuTexture gpuTexture = this.colorTexture;
        if (!(gpuTexture instanceof class_10868)) {
            throw new IllegalStateException("FboStage requires OpenGL Mojang device (got " + String.valueOf(this.colorTexture.getClass()) + ")");
        }
        class_10868 glTexture = (class_10868)gpuTexture;
        this.colorTextureId = glTexture.method_68427();
        int previousFramebuffer = GL30.glGetInteger((int)36006);
        int previousRenderbuffer = GL30.glGetInteger((int)36007);
        this.framebufferId = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer((int)36160, (int)this.framebufferId);
        GL30.glFramebufferTexture2D((int)36160, (int)36064, (int)3553, (int)this.colorTextureId, (int)0);
        this.depthStencilRenderbufferId = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer((int)36161, (int)this.depthStencilRenderbufferId);
        GL30.glRenderbufferStorage((int)36161, (int)35056, (int)width, (int)height);
        GL30.glFramebufferRenderbuffer((int)36160, (int)33306, (int)36161, (int)this.depthStencilRenderbufferId);
        int status = GL30.glCheckFramebufferStatus((int)36160);
        if (status != 36053) {
            GL30.glBindFramebuffer((int)36160, (int)previousFramebuffer);
            GL30.glBindRenderbuffer((int)36161, (int)previousRenderbuffer);
            throw new IllegalStateException("Overlay FBO is incomplete: " + status);
        }
        GL30.glBindFramebuffer((int)36160, (int)previousFramebuffer);
        GL30.glBindRenderbuffer((int)36161, (int)previousRenderbuffer);
        this.width = width;
        this.height = height;
    }

    GpuTextureView getColorTextureView() {
        return this.colorTextureView;
    }

    void bindAndClear() {
        float[] previousClearColor = new float[4];
        GL30.glGetFloatv((int)3106, (float[])previousClearColor);
        int previousClearStencil = GL30.glGetInteger((int)2961);
        GL30.glBindFramebuffer((int)36160, (int)this.framebufferId);
        GL30.glViewport((int)0, (int)0, (int)this.width, (int)this.height);
        GL30.glDisable((int)3089);
        GL30.glClearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        GL30.glClearStencil((int)0);
        GL30.glClear((int)17664);
        GL30.glClearColor((float)previousClearColor[0], (float)previousClearColor[1], (float)previousClearColor[2], (float)previousClearColor[3]);
        GL30.glClearStencil((int)previousClearStencil);
    }

    void close() {
        if (this.framebufferId != 0) {
            GL30.glDeleteFramebuffers((int)this.framebufferId);
            this.framebufferId = 0;
        }
        if (this.depthStencilRenderbufferId != 0) {
            GL30.glDeleteRenderbuffers((int)this.depthStencilRenderbufferId);
            this.depthStencilRenderbufferId = 0;
        }
        if (this.colorTextureView != null) {
            this.colorTextureView.close();
            this.colorTextureView = null;
        }
        if (this.colorTexture != null) {
            this.colorTexture.close();
            this.colorTexture = null;
        }
        this.colorTextureId = 0;
        this.width = 0;
        this.height = 0;
    }
}

