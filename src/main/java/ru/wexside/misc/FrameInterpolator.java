package ru.wexside.misc;

import net.minecraft.class_310;
import net.minecraft.class_3532;

public final class FrameInterpolator {
   private FrameInterpolator() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static float lerpTowards(float current, float target, float speed) {
      float progress = class_3532.method_15363((float)(getFrameDeltaSeconds() * (double)speed), 0.0F, 1.0F);
      return class_3532.method_16439(progress, current, target);
   }

   public static double getFrameDeltaSeconds() {
      int fps = class_310.method_1551().method_47599();
      return fps > 0 ? 1.0 / (double)fps : 1.0;
   }
}
