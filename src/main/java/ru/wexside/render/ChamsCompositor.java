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

public final class ChamsCompositor implements AutoCloseable {
   private static final int VERTEX_STRIDE = 20;
   private static final int VERTEX_COUNT = 6;
   private static final int VERTEX_BUFFER_SIZE = 120;
   private GlShaderProgram compositeShader;
   private ShaderUniformWriter compositeScreenSizeUniform;
   private ShaderUniformWriter maskTextureUniform;
   private ShaderUniformWriter sceneTextureUniform;
   private ShaderUniformWriter maskDepthTextureUniform;
   private ShaderUniformWriter sceneDepthTextureUniform;
   private ShaderUniformWriter materialModeUniform;
   private ShaderUniformWriter visibleColorUniform;
   private ShaderUniformWriter hiddenColorUniform;
   private ShaderUniformWriter visibleEnabledUniform;
   private ShaderUniformWriter hiddenEnabledUniform;
   private ShaderUniformWriter rectangleLocationUniform;
   private ShaderUniformWriter rectangleSizeUniform;
   private ShaderUniformWriter timeUniform;
   private GlShaderProgram presentShader;
   private ShaderUniformWriter presentScreenSizeUniform;
   private ShaderUniformWriter presentTextureUniform;
   private int framebufferId;
   private int vertexArrayId;
   private int vertexBufferId;
   private int quadWidth = -1;
   private int quadHeight = -1;
   private FloatBuffer quadVertices;

   public void composite(
      GpuTextureView outputView,
      GpuTextureView maskView,
      GpuTextureView maskDepthView,
      GpuTextureView sceneView,
      GpuTextureView sceneDepthView,
      int width,
      int height,
      int visibleColor,
      int hiddenColor,
      boolean renderVisible,
      boolean renderHidden,
      int materialMode,
      float rectangleX,
      float rectangleY,
      float rectangleWidth,
      float rectangleHeight,
      float time
   ) {
      RenderSystem.assertOnRenderThread();
      int outputTextureId = getTextureId(outputView);
      int maskTextureId = getTextureId(maskView);
      int maskDepthTextureId = getTextureId(maskDepthView);
      int sceneTextureId = getTextureId(sceneView);
      int sceneDepthTextureId = getTextureId(sceneDepthView);
      if (outputTextureId != 0
         && maskTextureId != 0
         && maskDepthTextureId != 0
         && sceneTextureId != 0
         && sceneDepthTextureId != 0
         && width > 0
         && height > 0
         && (renderVisible || renderHidden)) {
         this.ensureInitialized();
         this.updateFullscreenQuad(width, height);
         ScissorRegion scissor = calculateScissorRegion(width, height, rectangleX, rectangleY, rectangleWidth, rectangleHeight);
         if (scissor != null) {
            OpenGlStateSnapshot previousState = OpenGlStateSnapshot.capture();

            try {
               this.attachOutputTexture(outputTextureId);
               GlStateManager._viewport(0, 0, width, height);
               GlStateManager._disableDepthTest();
               GlStateManager._depthMask(false);
               GlStateManager._disableCull();
               GlStateManager._enableScissorTest();
               GlStateManager._scissorBox(scissor.x(), scissor.y(), scissor.width(), scissor.height());
               GlStateManager._disableBlend();
               this.compositeShader.bind();
               this.compositeScreenSizeUniform.set((float)width, (float)height);
               this.maskTextureUniform.set(0);
               this.sceneTextureUniform.set(1);
               this.maskDepthTextureUniform.set(2);
               this.sceneDepthTextureUniform.set(3);
               this.materialModeUniform.set(materialMode);
               this.visibleEnabledUniform.set(renderVisible ? 1 : 0);
               this.hiddenEnabledUniform.set(renderHidden ? 1 : 0);
               setColor(this.visibleColorUniform, visibleColor);
               setColor(this.hiddenColorUniform, hiddenColor);
               this.rectangleLocationUniform.set(rectangleX, rectangleY);
               this.rectangleSizeUniform.set(rectangleWidth, rectangleHeight);
               this.timeUniform.set(time);
               bindTexture(33984, maskTextureId);
               bindTexture(33985, sceneTextureId);
               bindTexture(33986, maskDepthTextureId);
               bindTexture(33987, sceneDepthTextureId);
               this.drawFullscreenQuad();
            } finally {
               previousState.restore();
            }
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
      if (this.compositeShader == null) {
         ResourceResolver resources = new ResourceResolver("/assets/wexside/shaders/chams/", ClasspathResource::new);
         this.compositeShader = new GlShaderProgram(resources.resolve("chams_apply.frag"), resources.resolve("chams_screen.vert"));
         this.compositeScreenSizeUniform = this.compositeShader.registerUniform("uScreenSize");
         this.maskTextureUniform = this.compositeShader.registerUniform("uMaskTexture");
         this.sceneTextureUniform = this.compositeShader.registerUniform("uSceneTexture");
         this.maskDepthTextureUniform = this.compositeShader.registerUniform("uMaskDepthTexture");
         this.sceneDepthTextureUniform = this.compositeShader.registerUniform("uSceneDepthTexture");
         this.materialModeUniform = this.compositeShader.registerUniform("uMode");
         this.visibleColorUniform = this.compositeShader.registerUniform("uColor");
         this.hiddenColorUniform = this.compositeShader.registerUniform("uHiddenColor");
         this.visibleEnabledUniform = this.compositeShader.registerUniform("uVisibleEnabled");
         this.hiddenEnabledUniform = this.compositeShader.registerUniform("uHiddenEnabled");
         this.rectangleLocationUniform = this.compositeShader.registerUniform("uLocation");
         this.rectangleSizeUniform = this.compositeShader.registerUniform("uRectSize");
         this.timeUniform = this.compositeShader.registerUniform("uTime");
         this.presentShader = new GlShaderProgram(resources.resolve("chams_present.frag"), resources.resolve("chams_screen.vert"));
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
            throw new IllegalStateException("Failed to create chams composite framebuffer");
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
         throw new IllegalStateException("Failed to assemble chams composite target");
      }
   }

   private static ScissorRegion calculateScissorRegion(int framebufferWidth, int framebufferHeight, float x, float y, float width, float height) {
      int left = Math.max(0, (int)Math.floor((double)x));
      int bottom = Math.max(0, (int)Math.floor((double)y));
      int right = Math.min(framebufferWidth, (int)Math.ceil((double)(x + width)));
      int top = Math.min(framebufferHeight, (int)Math.ceil((double)(y + height)));
      int scissorWidth = right - left;
      int scissorHeight = top - bottom;
      return scissorWidth > 0 && scissorHeight > 0 ? new ScissorRegion(left, bottom, scissorWidth, scissorHeight) : null;
   }

   private static void setColor(ShaderUniformWriter uniform, int color) {
      uniform.set(
         (float)(color >> 16 & 0xFF) / 255.0F, (float)(color >> 8 & 0xFF) / 255.0F, (float)(color & 0xFF) / 255.0F, (float)(color >>> 24 & 0xFF) / 255.0F
      );
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
      GlStateManager._bindTexture(textureId);
      GL46.glBindSampler(textureUnit - 33984, 0);
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

      if (this.compositeShader != null) {
         this.compositeShader.close();
         this.compositeShader = null;
      }

      if (this.presentShader != null) {
         this.presentShader.close();
         this.presentShader = null;
      }

      this.quadWidth = -1;
      this.quadHeight = -1;
   }
}
