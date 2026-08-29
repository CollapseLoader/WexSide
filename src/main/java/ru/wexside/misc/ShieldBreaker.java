package ru.wexside.misc;

public final class ShieldBreaker {
   private static volatile double value;
   private static volatile boolean enabled;
   private static volatile boolean enabled2;

   private ShieldBreaker() {
   }

   public static void reset() {
      update();
   }

   public static void consider(boolean blocking, double useProgress) {
      process(blocking, useProgress);
   }

   public static void update() {
      enabled2 = false;
      enabled = true;
      value = 0.0;
   }

   public static void process(boolean bl, double d) {
      enabled = bl;
      value = d;
      enabled2 = true;
   }

   public static boolean isActive() {
      return enabled2 && !enabled && value > 0.0;
   }

   public static boolean isAvailable() {
      return enabled;
   }

   public static boolean isActive3() {
      return enabled2;
   }

   public static double getDoubleType() {
      return value;
   }
}
