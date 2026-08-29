package ru.wexside.animation;

import ru.wexside.util.AnimationMath;

public final class TimedPulse {
   private long startedAt;

   public void trigger() {
      this.startedAt = System.currentTimeMillis();
   }

   public long elapsed(long now) {
      return now - this.startedAt;
   }

   public boolean isActive(long now, long durationMillis) {
      long elapsed = this.elapsed(now);
      return elapsed >= 0L && elapsed < durationMillis;
   }

   public float progress(long now, long durationMillis) {
      return AnimationMath.clamp01((float)this.elapsed(now) / (float)durationMillis);
   }

   public void reset() {
      this.startedAt = 0L;
   }
}
