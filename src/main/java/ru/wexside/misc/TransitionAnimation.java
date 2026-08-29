package ru.wexside.misc;

import net.minecraft.class_310;
import net.minecraft.class_3532;
import ru.wexside.util.Easing;

public class TransitionAnimation {
   private double progress;
   private final Easing secondaryEasing;
   private double previousProgress;
   private final Easing primaryEasing;

   public TransitionAnimation(Easing easing) {
      this(easing, easing);
   }

   public TransitionAnimation(Easing primaryEasing, Easing secondaryEasing) {
      this.primaryEasing = primaryEasing;
      this.secondaryEasing = secondaryEasing;
   }

   public void setActive(boolean active) {
      this.advance(active, 0.2);
   }

   public float getPrimaryProgress() {
      return (float)this.getPrimaryProgressDouble();
   }

   public void advance(boolean active, double step) {
      this.previousProgress = this.progress;
      this.progress = class_3532.method_15350(this.progress + (active ? step : -step), 0.0, 1.0);
   }

   public float getSecondaryProgress(float tickProgress) {
      return (float)this.interpolateSecondary(tickProgress);
   }

   public double interpolatePrimary(float tickProgress) {
      double interpolated = this.previousProgress + (this.progress - this.previousProgress) * (double)tickProgress;
      return this.primaryEasing.apply(interpolated);
   }

   public void finish() {
      this.previousProgress = 1.0;
      this.progress = 1.0;
   }

   public double getSecondaryProgressDouble() {
      return this.interpolateSecondary(class_310.method_1551().method_61966().method_60637(true));
   }

   public double interpolateSecondary(float tickProgress) {
      double interpolated = this.previousProgress + (this.progress - this.previousProgress) * (double)tickProgress;
      return this.secondaryEasing.apply(interpolated);
   }

   public boolean isAtStart() {
      return this.previousProgress + this.progress <= 1.0E-4;
   }

   public float getSecondaryProgress() {
      return (float)this.getSecondaryProgressDouble();
   }

   public float getPrimaryProgress(float tickProgress) {
      return (float)this.interpolatePrimary(tickProgress);
   }

   public void reset() {
      this.progress = 0.0;
      this.previousProgress = 0.0;
   }

   public void setProgress(double progress) {
      this.progress = progress;
      this.previousProgress = progress;
   }

   public double getPrimaryProgressDouble() {
      return this.interpolatePrimary(class_310.method_1551().method_61966().method_60637(true));
   }
}
