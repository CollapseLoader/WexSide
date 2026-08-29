package ru.wexside.event;

public class PreAttackEvent extends CancellableEvent implements Event {
   private float value;

   public PreAttackEvent(float f) {
      this.value = f;
   }

   public void onTick(float f) {
      this.value = f;
   }

   public float getFloatType() {
      return this.value;
   }
}
