package ru.wexside.misc;

public final class Gps {
   private static volatile boolean active;
   private static volatile int targetX;
   private static volatile int targetZ;

   private Gps() {
   }

   public static boolean isActive() {
      return active;
   }

   public static int getX() {
      return targetX;
   }

   public static int getZ() {
      return targetZ;
   }

   public static void set(int x, int z) {
      targetX = x;
      targetZ = z;
      active = true;
   }

   public static void clear() {
      active = false;
   }
}
