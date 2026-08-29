package ru.wexside.misc;

import ru.wexside.module.Module;

public final class ModuleTogglePulse {
   private static final long DURATION_MS = 1050L;
   private static volatile Module activeModule;
   private static volatile long startedAtNanos;

   private ModuleTogglePulse() {
   }

   public static void start(Module module) {
      activeModule = module;
      startedAtNanos = System.nanoTime();
   }

   public static float progress(Module module) {
      if (module != null && module == activeModule) {
         long elapsedMs = (System.nanoTime() - startedAtNanos) / 1000000L;
         if (elapsedMs >= 1050L) {
            activeModule = null;
            return 0.0F;
         } else {
            return (float)elapsedMs / 1050.0F;
         }
      } else {
         return 0.0F;
      }
   }
}
