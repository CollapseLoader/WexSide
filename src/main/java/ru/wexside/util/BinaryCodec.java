package ru.wexside.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

public final class BinaryCodec {
   private BinaryCodec() {
   }

   public static String encodeBase64(byte[] data) {
      return Base64.getEncoder().encodeToString(Objects.requireNonNull(data));
   }

   public static byte[] decodeBase64(String value) {
      return Base64.getDecoder().decode(Objects.requireNonNull(value));
   }

   public static byte[] slice(byte[] data, int offset, int length) {
      Objects.requireNonNull(data);
      if (offset >= 0 && length >= 0 && offset + length <= data.length) {
         byte[] result = new byte[length];
         System.arraycopy(data, offset, result, 0, length);
         return result;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public static byte[] concatenate(byte[]... arrays) {
      int length = 0;

      for(byte[] array : arrays) {
         length += array == null ? 0 : array.length;
      }

      byte[] result = new byte[length];
      int offset = 0;

      for(byte[] array : arrays) {
         if (array != null) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
         }
      }

      return result;
   }

   public static byte[] sha256(byte[] data) {
      try {
         return MessageDigest.getInstance("SHA-256").digest(Objects.requireNonNull(data));
      } catch (NoSuchAlgorithmException var2) {
         throw new IllegalStateException("SHA-256 is unavailable", var2);
      }
   }

   public static byte[] littleEndianLong(long value) {
      return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(value).array();
   }
}
