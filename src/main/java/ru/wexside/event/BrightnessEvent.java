package ru.wexside.event;

public final class BrightnessEvent implements Event {
   private float brightness;

   public BrightnessEvent(float f) {
      this.brightness = f;
   }

   public void setBrightness(float brightness) {
      this.brightness = brightness;
   }

   public float getBrightness() {
      return this.brightness;
   }
}
