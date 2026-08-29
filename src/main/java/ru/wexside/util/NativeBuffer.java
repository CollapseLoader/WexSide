package ru.wexside.util;

import java.nio.ByteBuffer;
import org.lwjgl.system.MemoryUtil;
import ru.wexside.misc.NativeMemoryAccess;
import ru.wexside.misc.UnsafeNativeMemoryAccess;

public class NativeBuffer implements AutoCloseable, NativeMemoryAccess {
   private long longType;
   private long address;
   private final UnsafeNativeMemoryAccess callback51Impl;
   private long getTextureId;

   public NativeBuffer(UnsafeNativeMemoryAccess callback51Impl, long l) {
      this.callback51Impl = callback51Impl;
      this.address = callback51Impl.allocate(l);
      this.getTextureId = l;
   }

   public void reset() {
      this.longType = 0L;
   }

   @Override
   public void close() {
      this.callback51Impl.free(this.address);
   }

   @Override
   public void process(long l, long l2) {
      this.callback51Impl.process(l, l2);
   }

   @Override
   public int getUnsignedShort(long l) {
      return this.callback51Impl.getUnsignedShort(l);
   }

   @Override
   public void process3(long l, float f) {
      this.callback51Impl.process3(l, f);
   }

   public void setLongType(long l) {
      this.longType += l;
   }

   @Override
   public long process4(long l) {
      return this.callback51Impl.process4(l);
   }

   @Override
   public void process5(long l, int n) {
      this.callback51Impl.process5(l, n);
   }

   @Override
   public int process6(long l) {
      return this.callback51Impl.process6(l);
   }

   @Override
   public void process7(long l, long l2, long l3) {
      this.callback51Impl.process7(l, l2, l3);
   }

   @Override
   public void process8(long l, int n) {
      this.callback51Impl.process8(l, n);
   }

   public void setLongType2(long l) {
      long l2 = this.longType + l;
      if (l2 > this.getTextureId) {
         long l3 = Math.max(this.getTextureId * 2L, l2);
         this.address = this.callback51Impl.process(this.address, this.getTextureId, l3);
         this.getTextureId = l3;
      }
   }

   public ByteBuffer getByteBuffer() {
      return this.process10((int)this.longType);
   }

   public long getLongType() {
      return this.address + this.longType;
   }

   @Override
   public void process9(long l, int n) {
      this.callback51Impl.process9(l, n);
   }

   public long getLongType2() {
      return this.longType;
   }

   public ByteBuffer getByteBuffer2() {
      return MemoryUtil.memByteBuffer(this.address, (int)this.longType);
   }

   public ByteBuffer process10(int n) {
      return MemoryUtil.memByteBuffer(this.address, n);
   }

   @Override
   public int process10(long l) {
      return this.callback51Impl.process10(l);
   }
}
