package ru.wexside.misc;

public final class SprintReset {
   private static volatile boolean enabled;

   private SprintReset() {
   }

   public static void setActive(boolean active) {
      enabled = active;
   }

   public static void setBooleanType(boolean bl) {
      setActive(bl);
   }

   public static boolean isActive() {
      return enabled;
   }
}
