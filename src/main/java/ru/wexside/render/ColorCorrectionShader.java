package ru.wexside.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.FloatBuffer;
import net.minecraft.class_10868;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wexside.misc.ClasspathResource;
import ru.wexside.misc.ResourceResolver;
import ru.wexside.misc.ShaderUniformWriter;

public final class ColorCorrectionShader implements AutoCloseable {
   private static final int VERTEX_STRIDE = 20;
   private static final int VERTEX_COUNT = 6;
   private static final int VERTEX_BUFFER_SIZE = 120;
   private GlShaderProgram shaderProgram;
   private ShaderUniformWriter screenSizeUniform;
   private ShaderUniformWriter sceneSamplerUniform;
   private ShaderUniformWriter contrastUniform;
   private ShaderUniformWriter saturationUniform;
   private ShaderUniformWriter brightnessUniform;
   private int framebufferId;
   private int vertexArrayId;
   private int vertexBufferId;
   private int quadWidth = -1;
   private int quadHeight = -1;

   public void apply(GpuTextureView outputView, GpuTextureView sceneView, int width, int height, float contrast, float saturation, float brightness) {
      RenderSystem.assertOnRenderThread();
      int outputTextureId = getTextureId(outputView);
      int sceneTextureId = getTextureId(sceneView);
      if (outputTextureId != 0 && sceneTextureId != 0 && width > 0 && height > 0) {
         this.ensureInitialized();
         this.updateFullscreenQuad(width, height);
         OpenGlStateSnapshot previousState = OpenGlStateSnapshot.capture();

         try {
            this.attachOutputTexture(outputTextureId);
            GL11.glViewport(0, 0, width, height);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glDisable(2884);
            GL11.glDisable(3089);
            GL11.glDisable(3042);
            this.shaderProgram.bind();
            this.screenSizeUniform.set((float)width, (float)height);
            this.sceneSamplerUniform.set(0);
            this.contrastUniform.set(contrast);
            this.saturationUniform.set(saturation);
            this.brightnessUniform.set(brightness);
            bindTexture(33984, sceneTextureId);
            this.drawFullscreenQuad();
         } finally {
            previousState.restore();
         }
      }
   }

   private void ensureInitialized() {
      if (this.shaderProgram == null) {
         ResourceResolver resources = new ResourceResolver("/assets/wexside/shaders/colorcorrection/", ClasspathResource::new);
         this.shaderProgram = new GlShaderProgram(resources.resolve("colorcorrection.frag"), resources.resolve("colorcorrection.vert"));
         this.screenSizeUniform = this.shaderProgram.registerUniform("uScreenSize");
         this.sceneSamplerUniform = this.shaderProgram.registerUniform("uScene");
         this.contrastUniform = this.shaderProgram.registerUniform("uContrast");
         this.saturationUniform = this.shaderProgram.registerUniform("uSaturation");
         this.brightnessUniform = this.shaderProgram.registerUniform("uBrightness");
         this.vertexArrayId = GL30.glGenVertexArrays();
         this.vertexBufferId = GL15.glGenBuffers();
         GL30.glBindVertexArray(this.vertexArrayId);
         GL15.glBindBuffer(34962, this.vertexBufferId);
         GL15.glBufferData(34962, 120L, 35048);
         GL20.glEnableVertexAttribArray(0);
         GL20.glVertexAttribPointer(0, 3, 5126, false, 20, 0L);
         GL20.glEnableVertexAttribArray(2);
         GL20.glVertexAttribPointer(2, 2, 5126, false, 20, 12L);
         GL15.glBindBuffer(34962, 0);
         GL30.glBindVertexArray(0);
         this.framebufferId = GL30.glGenFramebuffers();
         if (this.framebufferId == 0) {
            throw new IllegalStateException("Failed to create color correction framebuffer");
         }
      }
   }

   private void updateFullscreenQuad(int width, int height) {
      if (this.quadWidth != width || this.quadHeight != height) {
         FloatBuffer vertices = BufferUtils.createFloatBuffer(30);
         putVertex(vertices, 0.0F, 0.0F, 0.0F, 1.0F);
         putVertex(vertices, (float)width, 0.0F, 1.0F, 1.0F);
         putVertex(vertices, (float)width, (float)height, 1.0F, 0.0F);
         putVertex(vertices, 0.0F, 0.0F, 0.0F, 1.0F);
         putVertex(vertices, (float)width, (float)height, 1.0F, 0.0F);
         putVertex(vertices, 0.0F, (float)height, 0.0F, 0.0F);
         vertices.flip();
         GL15.glBindBuffer(34962, this.vertexBufferId);
         GL15.glBufferSubData(34962, 0L, vertices);
         GL15.glBindBuffer(34962, 0);
         this.quadWidth = width;
         this.quadHeight = height;
      }
   }

   private static void putVertex(FloatBuffer vertices, float x, float y, float u, float v) {
      vertices.put(x).put(y).put(0.0F).put(u).put(v);
   }

   private void attachOutputTexture(int textureId) {
      GL30.glBindFramebuffer(36160, this.framebufferId);
      GL30.glFramebufferTexture2D(36160, 36064, 3553, textureId, 0);
      GL30.glFramebufferTexture2D(36160, 36096, 3553, 0, 0);
      if (GL30.glCheckFramebufferStatus(36160) != 36053) {
         throw new IllegalStateException("Failed to assemble color correction target");
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
      GL13.glActiveTexture(textureUnit);
      GL11.glBindTexture(3553, textureId);
   }

   private void drawFullscreenQuad() {
      GL30.glBindVertexArray(this.vertexArrayId);
      GL11.glDrawArrays(4, 0, 6);
      GL30.glBindVertexArray(0);
   }

   @Override
   public void close() {
      if (this.vertexArrayId != 0) {
         GL30.glDeleteVertexArrays(this.vertexArrayId);
         this.vertexArrayId = 0;
      }

      if (this.vertexBufferId != 0) {
         GL15.glDeleteBuffers(this.vertexBufferId);
         this.vertexBufferId = 0;
      }

      if (this.framebufferId != 0) {
         GL30.glDeleteFramebuffers(this.framebufferId);
         this.framebufferId = 0;
      }

      if (this.shaderProgram != null) {
         this.shaderProgram.close();
         this.shaderProgram = null;
      }

      this.quadWidth = -1;
      this.quadHeight = -1;
   }
}
