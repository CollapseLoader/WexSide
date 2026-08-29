package ru.wexside.misc;

import org.lwjgl.opengl.GL33;

public class OpenGlTexture implements ResizableTexture {
   private final int height;
   private final int width;
   private final int textureId;

   OpenGlTexture(int n, int n2, int n3) {
      this.textureId = n;
      this.width = n2;
      this.height = n3;
   }

   @Override
   public int getHeight() {
      return this.height;
   }

   @Override
   public int getWidth() {
      return this.width;
   }

   public int getTextureId() {
      return this.textureId;
   }

   @Override
   public void close() {
      GL33.glDeleteTextures(this.textureId);
   }

   public static OpenGlTexture create(DecodedImage decodedImage2, int n, int n2, int n3, int n4, boolean bl, int n5) {
      int n6 = GL33.glGetInteger(32873);
      int n7 = GL33.glGetInteger(3317);
      GL33.glBindTexture(3553, n5);
      GL33.glPixelStorei(3312, 0);
      GL33.glPixelStorei(3313, 0);
      GL33.glPixelStorei(3314, 0);
      GL33.glPixelStorei(32878, 0);
      GL33.glPixelStorei(3315, 0);
      GL33.glPixelStorei(3316, 0);
      GL33.glPixelStorei(32877, 0);
      GL33.glPixelStorei(3317, 1);
      GL33.glTexParameteri(3553, 33084, 0);
      GL33.glTexParameteri(3553, 33085, 1000);
      GL33.glTexParameteri(3553, 10242, n3);
      GL33.glTexParameteri(3553, 10243, n4);
      GL33.glTexParameteri(3553, 10241, n2);
      GL33.glTexParameteri(3553, 10240, n);
      GL33.glTexImage2D(3553, 0, 32856, decodedImage2.getWidth(), decodedImage2.getHeight(), 0, 6408, 5121, decodedImage2.getByteBuffer());
      if (bl) {
         GL33.glGenerateMipmap(3553);
      }

      GL33.glPixelStorei(3317, n7);
      GL33.glBindTexture(3553, n6);
      return new OpenGlTexture(n5, decodedImage2.getWidth(), decodedImage2.getHeight());
   }

   public void bindToUnit(int n) {
      GL33.glActiveTexture(33984 + n);
      GL33.glBindTexture(3553, this.textureId);
   }
}
