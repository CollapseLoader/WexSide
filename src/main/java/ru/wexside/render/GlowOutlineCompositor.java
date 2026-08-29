package ru.wexside.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.FloatBuffer;
import net.minecraft.class_10868;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;
import ru.wexside.misc.ClasspathResource;
import ru.wexside.misc.ResourceResolver;
import ru.wexside.misc.ShaderUniformWriter;

public final class GlowOutlineCompositor implements AutoCloseable {
   private static final int VERTEX_STRIDE = 20;
   private static final int VERTEX_COUNT = 6;
   private static final int VERTEX_BUFFER_SIZE = 120;
   private GlShaderProgram gradientShader;
   private ShaderUniformWriter gradientScreenSizeUniform;
   private ShaderUniformWriter gradientTextureUniform;
   private ShaderUniformWriter gradientLocationUniform;
   private ShaderUniformWriter gradientRectangleSizeUniform;
   private ShaderUniformWriter gradientTimeUniform;
   private ShaderUniformWriter gradientEnabledUniform;
   private ShaderUniformWriter primaryColorUniform;
   private ShaderUniformWriter secondaryColorUniform;
   private GlShaderProgram outlineShader;
   private ShaderUniformWriter outlineScreenSizeUniform;
   private ShaderUniformWriter outlineInputUniform;
   private ShaderUniformWriter outlineCheckTextureUniform;
   private ShaderUniformWriter outlineDirectionUniform;
   private ShaderUniformWriter outlineSizeUniform;
   private ShaderUniformWriter outlineUseCheckUniform;
   private GlShaderProgram presentShader;
   private ShaderUniformWriter presentScreenSizeUniform;
   private ShaderUniformWriter presentTextureUniform;
   private int framebufferId;
   private int vertexArrayId;
   private int vertexBufferId;
   private int quadWidth = -1;
   private int quadHeight = -1;
   private FloatBuffer quadVertices;

   public void applyOutline(
      GpuTextureView outputView,
      GpuTextureView inputView,
      GpuTextureView checkView,
      int width,
      int height,
      float outlineSize,
      float directionX,
      float directionY,
      boolean useCheckTexture
   ) {
      RenderSystem.assertOnRenderThread();
      int outputTextureId = getTextureId(outputView);
      int inputTextureId = getTextureId(inputView);
      int checkTextureId = getTextureId(checkView);
      if (outputTextureId != 0 && inputTextureId != 0 && width > 0 && height > 0) {
         this.ensureInitialized();
         this.updateFullscreenQuad(width, height);
         OpenGlStateSnapshot previousState = OpenGlStateSnapshot.capture();

         try {
            this.attachOutputTexture(outputTextureId);
            GlStateManager._viewport(0, 0, width, height);
            GlStateManager._disableDepthTest();
            GlStateManager._depthMask(false);
            GlStateManager._disableCull();
            GlStateManager._disableScissorTest();
            GlStateManager._disableBlend();
            this.outlineShader.bind();
            this.outlineScreenSizeUniform.set((float)width, (float)height);
            this.outlineInputUniform.set(0);
            this.outlineCheckTextureUniform.set(1);
            this.outlineDirectionUniform.set(directionX, directionY);
            this.outlineSizeUniform.set(outlineSize);
            this.outlineUseCheckUniform.set(useCheckTexture ? 1 : 0);
            bindTexture(33984, inputTextureId);
            bindTexture(33985, checkTextureId);
            this.drawFullscreenQuad();
         } finally {
            previousState.restore();
         }
      }
   }

   public void applyGradient(
      GpuTextureView outputView,
      GpuTextureView inputView,
      int width,
      int height,
      int color,
      boolean useGradient,
      float rectangleX,
      float rectangleY,
      float rectangleWidth,
      float rectangleHeight,
      float time
   ) {
      RenderSystem.assertOnRenderThread();
      int outputTextureId = getTextureId(outputView);
      int inputTextureId = getTextureId(inputView);
      if (outputTextureId != 0 && inputTextureId != 0 && width > 0 && height > 0) {
         this.ensureInitialized();
         this.updateFullscreenQuad(width, height);
         OpenGlStateSnapshot previousState = OpenGlStateSnapshot.capture();

         try {
            this.attachOutputTexture(outputTextureId);
            GlStateManager._viewport(0, 0, width, height);
            GlStateManager._disableDepthTest();
            GlStateManager._depthMask(false);
            GlStateManager._disableCull();
            GlStateManager._disableScissorTest();
            GlStateManager._enableBlend();
            GlStateManager._blendFuncSeparate(773, 1, 773, 1);
            float red = (float)(color >> 16 & 0xFF) / 255.0F;
            float green = (float)(color >> 8 & 0xFF) / 255.0F;
            float blue = (float)(color & 0xFF) / 255.0F;
            float alpha = (float)(color >>> 24 & 0xFF) / 255.0F;
            this.gradientShader.bind();
            this.gradientScreenSizeUniform.set((float)width, (float)height);
            this.gradientTextureUniform.set(0);
            this.gradientLocationUniform.set(rectangleX, rectangleY);
            this.gradientRectangleSizeUniform.set(rectangleWidth, rectangleHeight);
            this.gradientTimeUniform.set(time);
            this.gradientEnabledUniform.set(useGradient ? 1 : 0);
            this.primaryColorUniform.set(red, green, blue, alpha);
            this.secondaryColorUniform.set(red * 0.2F, green * 0.2F, blue * 0.2F, alpha);
            bindTexture(33984, inputTextureId);
            this.drawFullscreenQuad();
         } finally {
            previousState.restore();
         }
      }
   }

   public void present(GpuTextureView outputView, GpuTextureView inputView, int width, int height) {
      RenderSystem.assertOnRenderThread();
      int outputTextureId = getTextureId(outputView);
      int inputTextureId = getTextureId(inputView);
      if (outputTextureId != 0 && inputTextureId != 0 && width > 0 && height > 0) {
         this.ensureInitialized();
         this.updateFullscreenQuad(width, height);
         OpenGlStateSnapshot previousState = OpenGlStateSnapshot.capture();

         try {
            this.attachOutputTexture(outputTextureId);
            GlStateManager._viewport(0, 0, width, height);
            GlStateManager._disableDepthTest();
            GlStateManager._depthMask(false);
            GlStateManager._disableCull();
            GlStateManager._disableScissorTest();
            GlStateManager._enableBlend();
            GlStateManager._blendFuncSeparate(770, 771, 1, 771);
            this.presentShader.bind();
            this.presentScreenSizeUniform.set((float)width, (float)height);
            this.presentTextureUniform.set(0);
            bindTexture(33984, inputTextureId);
            this.drawFullscreenQuad();
         } finally {
            previousState.restore();
         }
      }
   }

   private void ensureInitialized() {
      if (this.gradientShader == null) {
         ResourceResolver resources = new ResourceResolver("/assets/wexside/shaders/glowesp/", ClasspathResource::new);
         this.gradientShader = new GlShaderProgram(resources.resolve("glowesp_gradient.frag"), resources.resolve("glowesp_screen.vert"));
         this.gradientScreenSizeUniform = this.gradientShader.registerUniform("uScreenSize");
         this.gradientTextureUniform = this.gradientShader.registerUniform("uTexture");
         this.gradientLocationUniform = this.gradientShader.registerUniform("uLocation");
         this.gradientRectangleSizeUniform = this.gradientShader.registerUniform("uRectSize");
         this.gradientTimeUniform = this.gradientShader.registerUniform("uTime");
         this.gradientEnabledUniform = this.gradientShader.registerUniform("uGradient");
         this.primaryColorUniform = this.gradientShader.registerUniform("uColor1");
         this.secondaryColorUniform = this.gradientShader.registerUniform("uColor2");
         this.outlineShader = new GlShaderProgram(resources.resolve("glowesp_outline.frag"), resources.resolve("glowesp_screen.vert"));
         this.outlineScreenSizeUniform = this.outlineShader.registerUniform("uScreenSize");
         this.outlineInputUniform = this.outlineShader.registerUniform("uTextureIn");
         this.outlineCheckTextureUniform = this.outlineShader.registerUniform("uTextureCheck");
         this.outlineDirectionUniform = this.outlineShader.registerUniform("uDirection");
         this.outlineSizeUniform = this.outlineShader.registerUniform("uSize");
         this.outlineUseCheckUniform = this.outlineShader.registerUniform("uUseCheck");
         this.presentShader = new GlShaderProgram(resources.resolve("glowesp_present.frag"), resources.resolve("glowesp_screen.vert"));
         this.presentScreenSizeUniform = this.presentShader.registerUniform("uScreenSize");
         this.presentTextureUniform = this.presentShader.registerUniform("uTexture");
         this.vertexArrayId = GL46.glGenVertexArrays();
         this.vertexBufferId = GL46.glGenBuffers();
         GL46.glBindVertexArray(this.vertexArrayId);
         GL46.glBindBuffer(34962, this.vertexBufferId);
         GL46.glBufferData(34962, 120L, 35048);
         GL46.glEnableVertexAttribArray(0);
         GL46.glVertexAttribPointer(0, 3, 5126, false, 20, 0L);
         GL46.glEnableVertexAttribArray(2);
         GL46.glVertexAttribPointer(2, 2, 5126, false, 20, 12L);
         GL46.glBindBuffer(34962, 0);
         GL46.glBindVertexArray(0);
         this.framebufferId = GL46.glGenFramebuffers();
         if (this.framebufferId == 0) {
            throw new IllegalStateException("Failed to create glow ESP framebuffer");
         }
      }
   }

   private void updateFullscreenQuad(int width, int height) {
      if (this.quadWidth != width || this.quadHeight != height) {
         if (this.quadVertices == null) {
            this.quadVertices = BufferUtils.createFloatBuffer(30);
         }

         this.quadVertices.clear();
         putVertex(this.quadVertices, 0.0F, 0.0F, 0.0F, 1.0F);
         putVertex(this.quadVertices, (float)width, 0.0F, 1.0F, 1.0F);
         putVertex(this.quadVertices, (float)width, (float)height, 1.0F, 0.0F);
         putVertex(this.quadVertices, 0.0F, 0.0F, 0.0F, 1.0F);
         putVertex(this.quadVertices, (float)width, (float)height, 1.0F, 0.0F);
         putVertex(this.quadVertices, 0.0F, (float)height, 0.0F, 0.0F);
         this.quadVertices.flip();
         GL46.glBindBuffer(34962, this.vertexBufferId);
         GL46.glBufferSubData(34962, 0L, this.quadVertices);
         GL46.glBindBuffer(34962, 0);
         this.quadWidth = width;
         this.quadHeight = height;
      }
   }

   private static void putVertex(FloatBuffer vertices, float x, float y, float u, float v) {
      vertices.put(x).put(y).put(0.0F).put(u).put(v);
   }

   private void attachOutputTexture(int textureId) {
      GL46.glBindFramebuffer(36160, this.framebufferId);
      GL46.glFramebufferTexture(36160, 36064, textureId, 0);
      GL46.glFramebufferTexture(36160, 36096, 0, 0);
      if (GL46.glCheckFramebufferStatus(36160) != 36053) {
         throw new IllegalStateException("Failed to assemble glow ESP target");
      }
   }

   private static int getTextureId(GpuTextureView view) {
      if (view != null && !view.isClosed()) {
         GpuTexture texture = view.texture();
         if (texture instanceof class_10868 glTexture && !glTexture.isClosed()) {
            return glTexture.method_68427();
         }

         return 0;
      } else {
         return 0;
      }
   }

   private static void bindTexture(int textureUnit, int textureId) {
      GlStateManager._activeTexture(textureUnit);
      GL46.glBindSampler(textureUnit - 33984, 0);
      GlStateManager._bindTexture(textureId);
      GL46.glTexParameteri(3553, 10241, 9729);
      GL46.glTexParameteri(3553, 10240, 9729);
   }

   private void drawFullscreenQuad() {
      GL46.glBindVertexArray(this.vertexArrayId);
      GL46.glDrawArrays(4, 0, 6);
      GL46.glBindVertexArray(0);
   }

   @Override
   public void close() {
      if (this.vertexArrayId != 0) {
         GL46.glDeleteVertexArrays(this.vertexArrayId);
         this.vertexArrayId = 0;
      }

      if (this.vertexBufferId != 0) {
         GL46.glDeleteBuffers(this.vertexBufferId);
         this.vertexBufferId = 0;
      }

      if (this.framebufferId != 0) {
         GL46.glDeleteFramebuffers(this.framebufferId);
         this.framebufferId = 0;
      }

      if (this.gradientShader != null) {
         this.gradientShader.close();
         this.gradientShader = null;
      }

      if (this.outlineShader != null) {
         this.outlineShader.close();
         this.outlineShader = null;
      }

      if (this.presentShader != null) {
         this.presentShader.close();
         this.presentShader = null;
      }

      this.quadWidth = -1;
      this.quadHeight = -1;
   }
}
