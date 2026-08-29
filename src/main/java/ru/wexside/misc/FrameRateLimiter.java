package ru.wexside.misc;

public final class FrameRateLimiter {
   private long lastRunNanos = Long.MIN_VALUE;
   private final long minimumIntervalNanos;

   public FrameRateLimiter(int targetFps) {
      if (targetFps <= 0) {
         throw new IllegalArgumentException("targetFps must be > 0");
      } else {
         this.minimumIntervalNanos = 1000000000L / (long)targetFps;
      }
   }

   public boolean canRun(long nowNanos) {
      return this.lastRunNanos == Long.MIN_VALUE || nowNanos - this.lastRunNanos >= this.minimumIntervalNanos;
   }

   public void markRun(long nowNanos) {
      this.lastRunNanos = nowNanos;
   }

   public void reset() {
      this.lastRunNanos = Long.MIN_VALUE;
   }
}
