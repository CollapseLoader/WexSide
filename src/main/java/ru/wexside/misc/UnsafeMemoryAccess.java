package ru.wexside.misc;

import sun.misc.Unsafe;

public class UnsafeMemoryAccess implements NativeMemoryAccess, NativeMemoryAllocator, UnsafeNativeMemoryAccess {
   private final Unsafe unsafe;

   public UnsafeMemoryAccess(Unsafe unsafe) {
      this.unsafe = unsafe;
   }

   @Override
   public void free(long l) {
      this.unsafe.freeMemory(l);
   }

   @Override
   public void process(long l, long l2) {
      this.unsafe.putLong(l, l2);
   }

   @Override
   public int getUnsignedShort(long l) {
      return this.unsafe.getShort(l) & 65535;
   }

   @Override
   public void process3(long l, float f) {
      this.unsafe.putFloat(l, f);
   }

   @Override
   public long process(long l, long l2, long l3) {
      return this.unsafe.reallocateMemory(l, l3);
   }

   @Override
   public long process4(long l) {
      return this.unsafe.getLong(l);
   }

   @Override
   public void process5(long l, int n) {
      this.unsafe.putInt(l, n);
   }

   @Override
   public int process6(long l) {
      return this.unsafe.getInt(l);
   }

   @Override
   public long allocate(long l) {
      return this.unsafe.allocateMemory(l);
   }

   @Override
   public void process7(long l, long l2, long l3) {
      this.unsafe.copyMemory(l, l2, l3);
   }

   @Override
   public void process8(long l, int n) {
      this.unsafe.putByte(l, (byte)n);
   }

   @Override
   public void process9(long l, int n) {
      this.unsafe.putShort(l, (short)n);
   }

   @Override
   public int process10(long l) {
      return this.unsafe.getByte(l) & 0xFF;
   }
}
