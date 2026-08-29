package ru.wexside.misc;

import java.util.Objects;

public final class AstolfoState {
   private final float phaseOffset;
   private final float hueSpeed;
   private final float saturation;
   private final float brightness;
   private final float alpha;

   public AstolfoState() {
      this(0.0F, 0.01F, 0.7F, 1.0F, 1.0F);
   }

   public AstolfoState(float phaseOffset, float hueSpeed, float saturation, float brightness, float alpha) {
      this.phaseOffset = phaseOffset;
      this.hueSpeed = hueSpeed <= 0.0F ? 0.01F : hueSpeed;
      this.saturation = saturation;
      this.brightness = brightness;
      this.alpha = alpha;
   }

   public float getPhaseOffset() {
      return this.phaseOffset;
   }

   public float getHueSpeed() {
      return this.hueSpeed;
   }

   public float getSaturation() {
      return this.saturation;
   }

   public float getBrightness() {
      return this.brightness;
   }

   public float getAlpha() {
      return this.alpha;
   }

   public AstolfoState withPhaseOffset(float value) {
      return new AstolfoState(value, this.hueSpeed, this.saturation, this.brightness, this.alpha);
   }

   public AstolfoState withHueSpeed(float value) {
      return new AstolfoState(this.phaseOffset, value, this.saturation, this.brightness, this.alpha);
   }

   public AstolfoState withSaturation(float value) {
      return new AstolfoState(this.phaseOffset, this.hueSpeed, value, this.brightness, this.alpha);
   }

   public AstolfoState withBrightness(float value) {
      return new AstolfoState(this.phaseOffset, this.hueSpeed, this.saturation, value, this.alpha);
   }

   public AstolfoState withAlpha(float value) {
      return new AstolfoState(this.phaseOffset, this.hueSpeed, this.saturation, this.brightness, value);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof AstolfoState)) {
         return false;
      } else {
         AstolfoState other = (AstolfoState)object;
         return Float.compare(this.phaseOffset, other.phaseOffset) == 0
            && Float.compare(this.hueSpeed, other.hueSpeed) == 0
            && Float.compare(this.saturation, other.saturation) == 0
            && Float.compare(this.brightness, other.brightness) == 0
            && Float.compare(this.alpha, other.alpha) == 0;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.phaseOffset, this.hueSpeed, this.saturation, this.brightness, this.alpha);
   }

   @Override
   public String toString() {
      return "AstolfoState[phaseOffset="
         + this.phaseOffset
         + ", hueSpeed="
         + this.hueSpeed
         + ", saturation="
         + this.saturation
         + ", brightness="
         + this.brightness
         + ", alpha="
         + this.alpha
         + "]";
   }
}
