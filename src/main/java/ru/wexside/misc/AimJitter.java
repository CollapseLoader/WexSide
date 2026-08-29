package ru.wexside.misc;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_3532;
import ru.wexside.util.Angle;

public class AimJitter {
   private final int slot;
   private Angle angle;
   private final float value;
   private int slot2;
   private final float value2;
   private final float value3;
   private Angle angle2;

   public AimJitter() {
      this(30.0F, 15.0F, 2, 0.6F);
   }

   public AimJitter(float f, float f2, int n, float f3) {
      this.angle2 = Angle.angle;
      this.angle = Angle.angle;
      this.value2 = f;
      this.value = f2;
      this.slot = n;
      this.value3 = f3;
   }

   public void update() {
      this.angle2 = Angle.angle;
      this.angle = Angle.angle;
      this.slot2 = 0;
   }

   public Angle process(Angle angle) {
      if (++this.slot2 >= this.slot) {
         this.slot2 = 0;
         float f2 = ThreadLocalRandom.current().nextFloat(-this.value2, this.value2);
         float f = ThreadLocalRandom.current().nextFloat(-this.value, this.value);
         this.angle = new Angle(f2, f);
      }

      float f2 = class_3532.method_16439(this.value3, this.angle2.getFloatType(), this.angle.getFloatType());
      float f = class_3532.method_16439(this.value3, this.angle2.getFloatType2(), this.angle.getFloatType2());
      this.angle2 = new Angle(f2, f);
      float f3 = angle.getFloatType() + this.angle2.getFloatType();
      float f4 = class_3532.method_15363(angle.getFloatType2() + this.angle2.getFloatType2(), -90.0F, 90.0F);
      return new Angle(f3, f4);
   }
}
