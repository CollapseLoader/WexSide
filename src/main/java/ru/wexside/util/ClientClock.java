package ru.wexside.util;

import net.minecraft.class_310;

public final class ClientClock {
   private static long ticks;

   private ClientClock() {
   }

   public static void advanceTick() {
      ++ticks;
   }

   public static long ticks() {
      return ticks;
   }

   public static double elapsedSeconds(float tickDelta) {
      return (double)((float)ticks + tickDelta) / 20.0;
   }

   public static float tickDelta() {
      return class_310.method_1551().method_61966().method_60637(false);
   }
}
