package ru.wexside.misc;

import ru.wexside.util.AnimationMath;

public final class TargetTransition<T> {
   private long phaseStartedAt;
   private TransitionPhase phase = TransitionPhase.IDLE;
   private T current;

   public TargetChange<T> updateTarget(T target) {
      long now = System.currentTimeMillis();
      if (target != null && target != this.current) {
         T previous = this.current;
         this.current = target;
         this.phase = TransitionPhase.APPEARING;
         this.phaseStartedAt = now;
         return new TargetChange<>(previous, this.current, true, false);
      } else if (target == null && this.current != null && this.phase != TransitionPhase.DISAPPEARING) {
         this.phase = TransitionPhase.DISAPPEARING;
         this.phaseStartedAt = now;
         return new TargetChange<>(this.current, this.current, false, true);
      } else {
         return new TargetChange<>(this.current, this.current, false, false);
      }
   }

   public float progress(long durationMillis) {
      return durationMillis <= 0L ? 1.0F : AnimationMath.clamp01((float)(System.currentTimeMillis() - this.phaseStartedAt) / (float)durationMillis);
   }

   public boolean isEmpty() {
      return this.current == null && this.phase == TransitionPhase.IDLE;
   }

   public T current() {
      return this.current;
   }

   public TransitionPhase phase() {
      return this.phase;
   }

   public void finishAppearance() {
      this.phase = TransitionPhase.IDLE;
   }

   public void reset() {
      this.current = null;
      this.phaseStartedAt = 0L;
      this.phase = TransitionPhase.IDLE;
   }
}
