package ru.wexside.util;

import net.minecraft.class_3532;

public class FixedStepRotationSmoother {
   private final float value;

   public FixedStepRotationSmoother(float f) {
      this.value = f;
   }

   public Angle process(Angle angle, Angle angle2) {
      float f = class_3532.method_15393(angle2.getFloatType() - angle.getFloatType());
      float f2 = class_3532.method_15393(angle2.getFloatType2() - angle.getFloatType2());
      float f3 = Math.abs(f) + Math.abs(f2);
      if (f3 < 1.0E-4F) {
         return angle2;
      } else {
         float f4 = Math.abs(f) / f3 * this.value;
         float f5 = Math.abs(f2) / f3 * this.value;
         float f6 = angle.getFloatType() + class_3532.method_15363(f, -f4, f4);
         float f7 = class_3532.method_15363(angle.getFloatType2() + class_3532.method_15363(f2, -f5, f5), -90.0F, 90.0F);
         return new Angle(f6, f7);
      }
   }
}
