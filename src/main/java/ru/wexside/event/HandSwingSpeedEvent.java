package ru.wexside.event;

public final class HandSwingSpeedEvent extends CancellableEvent implements Event {
   private float speedMultiplier;

   public void setSpeedMultiplier(float speedMultiplier) {
      this.speedMultiplier = speedMultiplier;
   }

   public float getSpeedMultiplier() {
      return this.speedMultiplier;
   }
}
