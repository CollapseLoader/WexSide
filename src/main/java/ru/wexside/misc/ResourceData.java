package ru.wexside.misc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public interface ResourceData {
   String getPath();

   default String readUtf8() {
      return new String(this.readBytes(), StandardCharsets.UTF_8);
   }

   default void writeTo(ByteBuffer buffer) {
      buffer.put(this.readBytes());
   }

   default byte[] readBytes() {
      try {
         byte[] var2;
         try (InputStream stream = this.openStream()) {
            if (stream == null) {
               throw new IllegalStateException("Resource stream is null: " + this.getPath());
            }

            var2 = stream.readAllBytes();
         }

         return var2;
      } catch (IOException var6) {
         throw new IllegalStateException("Failed to read resource: " + this.getPath(), var6);
      }
   }

   InputStream openStream();

   default ByteBuffer toDirectByteBuffer() {
      byte[] bytes = this.readBytes();
      ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
      buffer.put(bytes);
      return buffer.flip();
   }
}
