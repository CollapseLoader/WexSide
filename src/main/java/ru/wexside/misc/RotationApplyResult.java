package ru.wexside.misc;

import ru.wexside.util.Angle;

public record RotationApplyResult(Angle angle, boolean ready) {
   public static RotationApplyResult notReady(Angle angle) {
      return new RotationApplyResult(angle, false);
   }

   public static RotationApplyResult applied(Angle angle, boolean ready) {
      return new RotationApplyResult(angle, ready);
   }

   public boolean isReady() {
      return this.ready;
   }
}
