package ru.wexside.util;

import ru.wexside.misc.FrameInterpolator;

public final class ScrollController {
   private final float animationSpeed;
   private final float snapEpsilon;
   private float animatedOffset;
   private final float wheelStep;
   private float contentHeight;
   private float targetOffset;

   public ScrollController(float wheelStep, float animationSpeed) {
      this(wheelStep, animationSpeed, 0.25F);
   }

   public ScrollController(float wheelStep, float animationSpeed, float snapEpsilon) {
      this.wheelStep = wheelStep;
      this.animationSpeed = animationSpeed;
      this.snapEpsilon = snapEpsilon;
   }

   public void setContentHeight(float viewportHeight, float contentHeight) {
      this.contentHeight = Math.max(0.0F, contentHeight);
      this.clampOffsets(viewportHeight, false);
   }

   public float getMinimumOffset(float viewportHeight) {
      return Math.min(0.0F, viewportHeight - this.contentHeight);
   }

   public void scrollTo(float offset, float viewportHeight) {
      this.targetOffset = offset;
      this.clampOffsets(viewportHeight, true);
   }

   public float getOffset() {
      return this.animatedOffset;
   }

   public float getContentHeight() {
      return this.contentHeight;
   }

   public void update(float viewportHeight, float contentHeight) {
      this.contentHeight = Math.max(0.0F, contentHeight);
      this.clampOffsets(viewportHeight, false);
      this.animatedOffset = FrameInterpolator.lerpTowards(this.animatedOffset, this.targetOffset, this.animationSpeed);
      if (Math.abs(this.animatedOffset - this.targetOffset) <= this.snapEpsilon) {
         this.animatedOffset = this.targetOffset;
      }

      this.clampOffsets(viewportHeight, false);
   }

   public void scrollByWheel(double amount, float viewportHeight) {
      this.targetOffset += (float)(amount * (double)this.wheelStep);
      this.clampOffsets(viewportHeight, true);
   }

   public void scrollToTop() {
      this.targetOffset = 0.0F;
   }

   public float getWheelStep() {
      return this.wheelStep;
   }

   public float getAnimationSpeed() {
      return this.animationSpeed;
   }

   private void clampOffsets(float viewportHeight, boolean targetOnly) {
      float minimumOffset = this.getMinimumOffset(viewportHeight);
      if (this.targetOffset < minimumOffset) {
         this.targetOffset = minimumOffset;
      }

      if (this.targetOffset > 0.0F) {
         this.targetOffset = 0.0F;
      }

      if (!targetOnly) {
         if (this.animatedOffset < minimumOffset) {
            this.animatedOffset = minimumOffset;
         }

         if (this.animatedOffset > 0.0F) {
            this.animatedOffset = 0.0F;
         }
      }
   }

   public float getSnapEpsilon() {
      return this.snapEpsilon;
   }

   public float getTargetOffset() {
      return this.targetOffset;
   }
}
