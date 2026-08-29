package ru.wexside.misc;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class DecodedImage {
   private final int width;
   private final ByteBuffer byteBuffer;
   private final int height;

   private DecodedImage(ByteBuffer byteBuffer, int n, int n2) {
      this.byteBuffer = byteBuffer;
      this.width = n;
      this.height = n2;
   }

   static DecodedImage decode(ByteBuffer byteBuffer) {
      ByteBuffer byteBuffer2 = byteBuffer.duplicate();
      byteBuffer2.rewind();
      MemoryStack memoryStack = MemoryStack.stackPush();

      DecodedImage var11;
      try {
         IntBuffer intBuffer = memoryStack.mallocInt(1);
         IntBuffer intBuffer2 = memoryStack.mallocInt(1);
         IntBuffer intBuffer3 = memoryStack.mallocInt(1);
         ByteBuffer byteBuffer3 = STBImage.stbi_load_from_memory(byteBuffer2, intBuffer, intBuffer2, intBuffer3, 4);
         if (byteBuffer3 == null) {
            throw new IllegalStateException("Couldn't load image from memory");
         }

         int n = intBuffer.get(0);
         int n2 = intBuffer2.get(0);
         DecodedImage decodedImage = new DecodedImage(byteBuffer3, n, n2);
         var11 = decodedImage;
      } catch (Throwable var13) {
         if (memoryStack != null) {
            try {
               memoryStack.close();
            } catch (Throwable var12) {
               var13.addSuppressed(var12);
            }
         }

         throw var13;
      }

      if (memoryStack != null) {
         memoryStack.close();
      }

      return var11;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   void close() {
      STBImage.stbi_image_free(this.byteBuffer);
   }

   public ByteBuffer getByteBuffer() {
      return this.byteBuffer;
   }
}
