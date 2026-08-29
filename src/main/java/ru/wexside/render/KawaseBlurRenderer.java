package ru.wexside.render;

import com.mojang.blaze3d.textures.GpuTexture;
import java.nio.FloatBuffer;
import net.minecraft.class_10868;
import net.minecraft.class_310;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import ru.wexside.misc.ClasspathResource;
import ru.wexside.misc.FrameRateLimiter;
import ru.wexside.misc.ResourceResolver;
import ru.wexside.misc.ShaderUniformWriter;
import ru.wexside.render.GlShaderProgram;
import ru.wexside.util.GlRenderStateSnapshot;

public final class KawaseBlurRenderer
implements AutoCloseable {
    private static final int PASS_COUNT = 3;
    private static final int TARGET_COUNT = 4;
    private static final int REFRESH_RATE = 120;
    private static final int RESULT_TEXTURE_UNIT = 5;
    private static final float DEFAULT_OFFSET = 2.0f;
    private final GlShaderProgram downsampleShader;
    private final GlShaderProgram upsampleShader;
    private final ShaderUniformWriter downsampleImageUniform;
    private final ShaderUniformWriter downsampleOffsetUniform;
    private final ShaderUniformWriter downsampleResolutionUniform;
    private final ShaderUniformWriter upsampleImageUniform;
    private final ShaderUniformWriter upsampleOffsetUniform;
    private final ShaderUniformWriter upsampleResolutionUniform;
    private final int[] textures = new int[4];
    private final int[] framebuffers = new int[4];
    private final FrameRateLimiter refreshLimiter = new FrameRateLimiter(120);
    private int width = -1;
    private int height = -1;
    private int lastRenderedFrame = Integer.MIN_VALUE;
    private int vertexArrayId;
    private int vertexBufferId;

    public KawaseBlurRenderer() {
        ResourceResolver resources = new ResourceResolver("/assets/wexside/shaders/", ClasspathResource::new);
        this.downsampleShader = new GlShaderProgram(resources.resolve("gl/kawase_down.frag"), resources.resolve("gl/fullscreen_quad.vert"));
        this.downsampleImageUniform = this.downsampleShader.registerUniform("image");
        this.downsampleOffsetUniform = this.downsampleShader.registerUniform("offset");
        this.downsampleResolutionUniform = this.downsampleShader.registerUniform("resolution");
        this.upsampleShader = new GlShaderProgram(resources.resolve("gl/kawase_up.frag"), resources.resolve("gl/fullscreen_quad.vert"));
        this.upsampleImageUniform = this.upsampleShader.registerUniform("image");
        this.upsampleOffsetUniform = this.upsampleShader.registerUniform("offset");
        this.upsampleResolutionUniform = this.upsampleShader.registerUniform("resolution");
    }

    public void captureMainFramebuffer(int frame, int width, int height) {
        boolean targetsReusable;
        long now = System.nanoTime();
        boolean bl = targetsReusable = this.width == width && this.height == height && this.textures[0] != 0;
        if (targetsReusable && (this.lastRenderedFrame == frame || !this.refreshLimiter.canRun(now))) {
            return;
        }
        int sourceTextureId = KawaseBlurRenderer.getMainFramebufferTextureId();
        if (sourceTextureId <= 0) {
            return;
        }
        this.ensureFullscreenQuad();
        this.ensureTargets(width, height);
        this.renderBlurPyramid(sourceTextureId, width, height, 2.0f, 3);
        this.lastRenderedFrame = frame;
        this.refreshLimiter.markRun(now);
    }

    public void bindResultTexture() {
        if (this.textures[0] == 0) {
            return;
        }
        GL13.glActiveTexture((int)33989);
        GL11.glBindTexture((int)3553, (int)this.textures[0]);
        GL33.glBindSampler((int)5, (int)0);
        GL13.glActiveTexture((int)33984);
    }

    public void releaseIfStale(int currentFrame, int maximumIdleFrames) {
        if (this.textures[0] != 0 && currentFrame - this.lastRenderedFrame >= maximumIdleFrames) {
            this.deleteTargets();
        }
    }

    private void ensureFullscreenQuad() {
        if (this.vertexArrayId != 0) {
            return;
        }
        float[] vertices = new float[]{-1.0f, -1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
        FloatBuffer vertexData = BufferUtils.createFloatBuffer((int)vertices.length);
        vertexData.put(vertices).flip();
        this.vertexArrayId = GL30.glGenVertexArrays();
        this.vertexBufferId = GL15.glGenBuffers();
        GL30.glBindVertexArray((int)this.vertexArrayId);
        GL15.glBindBuffer((int)34962, (int)this.vertexBufferId);
        GL15.glBufferData((int)34962, (FloatBuffer)vertexData, (int)35044);
        GL20.glEnableVertexAttribArray((int)0);
        GL20.glVertexAttribPointer((int)0, (int)2, (int)5126, (boolean)false, (int)16, (long)0L);
        GL20.glEnableVertexAttribArray((int)1);
        GL20.glVertexAttribPointer((int)1, (int)2, (int)5126, (boolean)false, (int)16, (long)8L);
        GL15.glBindBuffer((int)34962, (int)0);
        GL30.glBindVertexArray((int)0);
    }

    private void ensureTargets(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        if (this.width == width && this.height == height && this.textures[0] != 0) {
            return;
        }
        this.deleteTargets();
        this.width = width;
        this.height = height;
        for (int level = 0; level < 4; ++level) {
            this.textures[level] = GL11.glGenTextures();
            GL11.glBindTexture((int)3553, (int)this.textures[level]);
            GL11.glTexImage2D((int)3553, (int)0, (int)32856, (int)KawaseBlurRenderer.mipDimension(width, level), (int)KawaseBlurRenderer.mipDimension(height, level), (int)0, (int)6408, (int)5121, (long)0L);
            GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
            GL11.glTexParameteri((int)3553, (int)10242, (int)33071);
            GL11.glTexParameteri((int)3553, (int)10243, (int)33071);
            this.framebuffers[level] = GL30.glGenFramebuffers();
            GL30.glBindFramebuffer((int)36160, (int)this.framebuffers[level]);
            GL30.glFramebufferTexture2D((int)36160, (int)36064, (int)3553, (int)this.textures[level], (int)0);
            int status = GL30.glCheckFramebufferStatus((int)36160);
            if (status == 36053) continue;
            throw new IllegalStateException("Kawase blur framebuffer is incomplete: " + status);
        }
        GL11.glBindTexture((int)3553, (int)0);
        GL30.glBindFramebuffer((int)36160, (int)0);
    }

    private static int getMainFramebufferTextureId() {
        int n;
        GpuTexture colorAttachment = class_310.method_1551().method_1522().method_30277();
        if (colorAttachment instanceof class_10868) {
            class_10868 glTexture = (class_10868)colorAttachment;
            n = glTexture.method_68427();
        } else {
            n = -1;
        }
        return n;
    }

    private static int mipDimension(int dimension, int level) {
        return Math.max(1, dimension >> level);
    }

    private void renderBlurPyramid(int sourceTextureId, int width, int height, float offset, int passCount) {
        int level;
        GlRenderStateSnapshot previousState = GlRenderStateSnapshot.captureRenderState();
        GL13.glActiveTexture((int)33984);
        GL33.glBindSampler((int)0, (int)0);
        GL11.glDisable((int)3042);
        GL11.glDisable((int)2884);
        GL11.glDisable((int)2929);
        GL11.glDisable((int)3089);
        this.renderPass(this.downsampleShader, this.downsampleImageUniform, this.downsampleOffsetUniform, this.downsampleResolutionUniform, sourceTextureId, this.framebuffers[1], width, height, KawaseBlurRenderer.mipDimension(width, 1), KawaseBlurRenderer.mipDimension(height, 1), offset);
        for (level = 2; level <= passCount; ++level) {
            this.renderPass(this.downsampleShader, this.downsampleImageUniform, this.downsampleOffsetUniform, this.downsampleResolutionUniform, this.textures[level - 1], this.framebuffers[level], KawaseBlurRenderer.mipDimension(width, level - 1), KawaseBlurRenderer.mipDimension(height, level - 1), KawaseBlurRenderer.mipDimension(width, level), KawaseBlurRenderer.mipDimension(height, level), offset);
        }
        for (level = passCount; level >= 1; --level) {
            this.renderPass(this.upsampleShader, this.upsampleImageUniform, this.upsampleOffsetUniform, this.upsampleResolutionUniform, this.textures[level], this.framebuffers[level - 1], KawaseBlurRenderer.mipDimension(width, level), KawaseBlurRenderer.mipDimension(height, level), KawaseBlurRenderer.mipDimension(width, level - 1), KawaseBlurRenderer.mipDimension(height, level - 1), offset);
        }
        previousState.restore();
    }

    private void renderPass(GlShaderProgram shader, ShaderUniformWriter imageUniform, ShaderUniformWriter offsetUniform, ShaderUniformWriter resolutionUniform, int inputTextureId, int outputFramebufferId, int inputWidth, int inputHeight, int outputWidth, int outputHeight, float offset) {
        GL30.glBindFramebuffer((int)36160, (int)outputFramebufferId);
        GL11.glViewport((int)0, (int)0, (int)outputWidth, (int)outputHeight);
        GL11.glClearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        GL11.glClear((int)16384);
        shader.bind();
        imageUniform.set(0);
        offsetUniform.set(offset);
        resolutionUniform.set(1.0f / (float)inputWidth, 1.0f / (float)inputHeight);
        GL13.glActiveTexture((int)33984);
        GL11.glBindTexture((int)3553, (int)inputTextureId);
        GL30.glBindVertexArray((int)this.vertexArrayId);
        GL11.glDrawArrays((int)5, (int)0, (int)4);
    }

    private void deleteTargets() {
        for (int index = 0; index < 4; ++index) {
            if (this.framebuffers[index] != 0) {
                GL30.glDeleteFramebuffers((int)this.framebuffers[index]);
                this.framebuffers[index] = 0;
            }
            if (this.textures[index] == 0) continue;
            GL11.glDeleteTextures((int)this.textures[index]);
            this.textures[index] = 0;
        }
        this.width = -1;
        this.height = -1;
        this.lastRenderedFrame = Integer.MIN_VALUE;
        this.refreshLimiter.reset();
    }

    @Override
    public void close() {
        this.deleteTargets();
        if (this.vertexBufferId != 0) {
            GL15.glDeleteBuffers((int)this.vertexBufferId);
            this.vertexBufferId = 0;
        }
        if (this.vertexArrayId != 0) {
            GL30.glDeleteVertexArrays((int)this.vertexArrayId);
            this.vertexArrayId = 0;
        }
        this.downsampleShader.close();
        this.upsampleShader.close();
    }
}

