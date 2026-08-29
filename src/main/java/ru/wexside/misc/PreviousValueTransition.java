package ru.wexside.misc;

import ru.wexside.util.AnimationMath;

public final class PreviousValueTransition<T> {
   private boolean active;
   private long startedAt;
   private T previousValue;

   public void remember(T value) {
      if (value != null) {
         this.previousValue = value;
         this.startedAt = System.currentTimeMillis();
         this.active = true;
      }
   }

   public boolean hasValue() {
      return this.active && this.previousValue != null;
   }

   public T get() {
      return this.previousValue;
   }

   public float progress(long durationMillis) {
      return durationMillis <= 0L ? 1.0F : AnimationMath.clamp01((float)(System.currentTimeMillis() - this.startedAt) / (float)durationMillis);
   }

   public void clear() {
      this.previousValue = null;
      this.startedAt = 0L;
      this.active = false;
   }

   public void reset() {
      this.clear();
   }
}
