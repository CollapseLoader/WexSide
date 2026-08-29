package ru.wexside.event;

import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_4587;

public final class FirstPersonItemTransformEvent extends CancellableEvent implements Event {
   private final class_4587 matrices;
   private final class_1799 stack;
   private float scale = 1.0F;
   private final class_1268 hand;

   public FirstPersonItemTransformEvent(class_4587 matrices2, class_1799 stack, class_1268 illlliiIiI2) {
      this.matrices = matrices2;
      this.stack = stack;
      this.hand = illlliiIiI2;
   }

   public class_1268 getHand() {
      return this.hand;
   }

   public float getScale() {
      return this.scale;
   }

   public void setScale(float scale) {
      this.scale = scale;
   }

   public class_1799 getStack() {
      return this.stack;
   }

   public class_4587 getMatrices() {
      return this.matrices;
   }
}
