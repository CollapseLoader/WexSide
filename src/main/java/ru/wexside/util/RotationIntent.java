package ru.wexside.util;

import net.minecraft.class_1309;
import net.minecraft.class_243;
import ru.wexside.misc.AttackUrgency;
import ru.wexside.misc.CorrectionMode;

public record RotationIntent(class_1309 target, class_243 aimPoint, Angle targetAngle, AttackUrgency urgency, CorrectionMode correction, boolean changeViewLook) {
   public static RotationIntent empty() {
      return new RotationIntent(null, null, null, AttackUrgency.NONE, CorrectionMode.NONE, false);
   }

   public boolean hasTarget() {
      return this.target != null;
   }

   public boolean hasCorrection() {
      return this.correction != CorrectionMode.NONE;
   }
}
