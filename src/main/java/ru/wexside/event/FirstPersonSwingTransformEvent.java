package ru.wexside.event;

import net.minecraft.class_1268;
import net.minecraft.class_4587;

public final class FirstPersonSwingTransformEvent extends CancellableEvent implements Event {
   private final class_4587 matrices;
   private final float swingProgress;
   private final float equipProgress;
   private final class_1268 hand;

   public FirstPersonSwingTransformEvent(class_4587 matrices, class_1268 hand, float swingProgress, float equipProgress) {
      this.matrices = matrices;
      this.hand = hand;
      this.swingProgress = swingProgress;
      this.equipProgress = equipProgress;
   }

   public class_4587 getMatrices() {
      return this.matrices;
   }

   public class_1268 getHand() {
      return this.hand;
   }

   public float getEquipProgress() {
      return this.equipProgress;
   }

   public float getSwingProgress() {
      return this.swingProgress;
   }
}
