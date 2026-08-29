package ru.wexside.misc;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_3532;
import ru.wexside.util.Angle;
import ru.wexside.util.RotationIntent;
import ru.wexside.util.RotationState;

public class HumanizedRotationStrategy implements RotationStrategy {
   static final float value = 16.0F;
   private final AimJitter field12 = new AimJitter(20.0F, 10.0F, 2, 0.6F);
   static final int slot = 9;
   static final float value2 = 18.0F;
   static final float value3 = 24.0F;
   private Angle field16;
   static final float value4 = 12.0F;

   private Angle process(Angle angle) {
      ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
      float f = (threadLocalRandom.nextBoolean() ? 1.0F : -1.0F) * threadLocalRandom.nextFloat(18.0F, 24.0F);
      float f2 = threadLocalRandom.nextFloat(12.0F, 16.0F);
      boolean bl2 = angle.getFloatType2() + f2 <= 90.0F;
      boolean bl = angle.getFloatType2() - f2 >= -90.0F;
      float f3 = bl2 && bl ? (threadLocalRandom.nextBoolean() ? 1.0F : -1.0F) : (bl ? -1.0F : 1.0F);
      float f4 = class_3532.method_15363(angle.getFloatType2() + f3 * f2, -90.0F, 90.0F);
      return new Angle(angle.getFloatType() + f, f4);
   }

   @Override
   public RotationApplyResult process(RotationState rotationState, RotationIntent intent) {
      Angle currentAngle = rotationState.getPlayerAngle();
      if (intent.hasTarget() && intent.targetAngle() != null) {
         if (intent.urgency() == AttackUrgency.HIT) {
            this.field16 = this.process(intent.targetAngle());
            return RotationApplyResult.applied(intent.targetAngle(), true);
         } else {
            Angle baseAngle = this.field16 != null && rotationState.getTicksSinceHit() < 9 ? this.field16 : currentAngle;
            return RotationApplyResult.applied(this.field12.process(baseAngle), false);
         }
      } else {
         this.field16 = null;
         return RotationApplyResult.notReady(this.field12.process(currentAngle));
      }
   }

   @Override
   public void onDeactivated(RotationState iIiIlIIliI2) {
      this.field12.update();
      this.field16 = null;
   }

   @Override
   public void onActivated(RotationState iIiIlIIliI2) {
      this.field12.update();
      this.field16 = null;
   }
}
