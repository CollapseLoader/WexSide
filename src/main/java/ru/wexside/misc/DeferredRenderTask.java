package ru.wexside.misc;

public final class DeferredRenderTask {
   private static volatile Runnable runnable;

   private DeferredRenderTask() {
   }

   public static void update() {
      Runnable pendingTask = runnable;
      runnable = null;
      if (pendingTask != null) {
         pendingTask.run();
      }
   }

   public static void setRunnable(Runnable runnable) {
      DeferredRenderTask.runnable = runnable;
   }
}
