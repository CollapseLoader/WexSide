package ru.wexside.misc;

public interface NativeMemoryAllocator {
   void free(long var1);

   long process(long var1, long var3, long var5);

   long allocate(long var1);
}
