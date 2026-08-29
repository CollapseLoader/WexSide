package ru.wexside.util;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_3532;
import ru.wexside.misc.RotationApplyResult;
import ru.wexside.misc.RotationStrategy;

public final class RandomizedDirectRotationStrategy implements RotationStrategy {
   private static final float value = 60.0F;
   private static final float value2 = 1.0F;
   private static final float value3 = 60.0F;
   private static final float value4 = 45.0F;
   private static final float value5 = 5.0F;

   @Override
   public RotationApplyResult process(RotationState iIiIlIIliI2, RotationIntent intent) {
      Angle angle2 = iIiIlIIliI2.getPlayerAngle();
      Angle angle = iIiIlIIliI2.getAppliedAngle() != null ? iIiIlIIliI2.getAppliedAngle() : angle2;
      if (intent.hasTarget() && intent.targetAngle() != null) {
         Angle angle4 = process2(angle, intent.targetAngle());
         boolean bl = angle4.process(intent.targetAngle()) < 5.0F;
         return RotationApplyResult.applied(angle4, bl && iIiIlIIliI2.isAttackReady());
      } else {
         return angle.process(angle2) < 1.0F ? RotationApplyResult.notReady(angle2) : RotationApplyResult.notReady(process2(angle, angle2));
      }
   }

   @Override
   public void onDeactivated(RotationState iIiIlIIliI2) {
   }

   @Override
   public void onActivated(RotationState iIiIlIIliI2) {
   }

   private static Angle process2(Angle angle, Angle angle2) {
      float f = class_3532.method_15393(angle2.getFloatType() - angle.getFloatType());
      float f2 = class_3532.method_15393(angle2.getFloatType2() - angle.getFloatType2());
      float f3 = (float)Math.hypot((double)Math.abs(f), (double)Math.abs(f2));
      if (f3 == 0.0F) {
         return angle;
      } else {
         float f4 = Math.abs(f / f3) * 60.0F;
         float f5 = Math.abs(f2 / f3) * 45.0F;
         float f6 = class_3532.method_15363(f, -f4, f4);
         float f7 = class_3532.method_15363(f2, -f5, f5);
         float f8 = Math.abs(f6) + Math.abs(f7);
         float f9 = 60.0F * ThreadLocalRandom.current().nextFloat(0.9F, 1.0F);
         float f10 = f8 == 0.0F ? 0.0F : Math.abs(f6 / f8) * f9;
         float f11 = f8 == 0.0F ? 0.0F : Math.abs(f7 / f8) * f9;
         float f12 = angle.getFloatType() + class_3532.method_15363(f6, -f10, f10);
         float f13 = angle.getFloatType2() + class_3532.method_15363(f7, -f11, f11);
         return new Angle(f12, f13);
      }
   }
}
