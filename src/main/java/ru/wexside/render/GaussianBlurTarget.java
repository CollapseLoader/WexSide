package ru.wexside.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import ru.wexside.misc.FrameRateLimiter;
import ru.wexside.misc.TextureHandle;

final class GaussianBlurTarget implements TextureHandle, AutoCloseable {
   final int[] textures = new int[2];
   final int[] framebuffers = new int[2];
   final FrameRateLimiter refreshLimiter = new FrameRateLimiter(144);
   int lastRenderedFrame = Integer.MIN_VALUE;
   int sourceGeneration = Integer.MIN_VALUE;
   float sigmaStart = -1.0F;
   float sigmaEnd = -1.0F;
   int width;
   int height;

   GaussianBlurTarget(int width, int height) {
      int previousTexture = GL30.glGetInteger(32873);
      int previousFramebuffer = GL30.glGetInteger(36006);

      for(int index = 0; index < this.textures.length; ++index) {
         this.textures[index] = GL11.glGenTextures();
         GL11.glBindTexture(3553, this.textures[index]);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
         this.framebuffers[index] = GL30.glGenFramebuffers();
         GL30.glBindFramebuffer(36160, this.framebuffers[index]);
         GL30.glFramebufferTexture2D(36160, 36064, 3553, this.textures[index], 0);
      }

      this.allocateStorage(width, height);
      GL11.glBindTexture(3553, previousTexture);
      GL30.glBindFramebuffer(36160, previousFramebuffer);
   }

   private void allocateStorage(int width, int height) {
      this.width = width;
      this.height = height;

      for(int index = 0; index < this.textures.length; ++index) {
         GL11.glBindTexture(3553, this.textures[index]);
         GL11.glTexImage2D(3553, 0, 32856, width, height, 0, 6408, 5121, 0L);
         GL30.glBindFramebuffer(36160, this.framebuffers[index]);
         int status = GL30.glCheckFramebufferStatus(36160);
         if (status != 36053) {
            throw new IllegalStateException("Gaussian blur framebuffer is incomplete: " + status);
         }
      }
   }

   void resize(int width, int height) {
      if (this.width != width || this.height != height) {
         int previousTexture = GL30.glGetInteger(32873);
         int previousFramebuffer = GL30.glGetInteger(36006);
         this.allocateStorage(width, height);
         GL11.glBindTexture(3553, previousTexture);
         GL30.glBindFramebuffer(36160, previousFramebuffer);
      }
   }

   @Override
   public int getTextureId() {
      return this.textures[1];
   }

   @Override
   public int getWidth() {
      return this.width;
   }

   @Override
   public int getHeight() {
      return this.height;
   }

   @Override
   public void close() {
      for(int index = 0; index < this.textures.length; ++index) {
         if (this.framebuffers[index] != 0) {
            GL30.glDeleteFramebuffers(this.framebuffers[index]);
            this.framebuffers[index] = 0;
         }

         if (this.textures[index] != 0) {
            GL11.glDeleteTextures(this.textures[index]);
            this.textures[index] = 0;
         }
      }
   }
}
