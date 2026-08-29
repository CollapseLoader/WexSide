package ru.wexside.util;

import ru.wexside.misc.FrameInterpolator;
import ru.wexside.render.RenderFrameClock;

public final class VisibilityAnimationCache {
   private int slot = Integer.MIN_VALUE;
   private boolean enabled;
   private float value;

   public float process(boolean bl, boolean bl2, float f) {
      int n = RenderFrameClock.currentFrame();
      if (this.slot == n) {
         return this.value;
      } else {
         float f2 = bl2 && bl ? 1.0F : 0.0F;
         if (!this.enabled) {
            this.value = f2;
            this.enabled = true;
         } else {
            this.value = FrameInterpolator.lerpTowards(this.value, f2, f);
            if (Math.abs(f2 - this.value) <= 0.001F) {
               this.value = f2;
            }
         }

         this.slot = n;
         return this.value;
      }
   }
}
