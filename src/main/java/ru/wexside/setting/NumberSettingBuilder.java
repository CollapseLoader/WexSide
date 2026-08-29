package ru.wexside.setting;

import ru.wexside.util.RussianPluralForms;

public final class NumberSettingBuilder extends SettingBuilder {
   private double minimum;
   private double maximum = 1.0;
   private double defaultValue;
   private double multiplier = 1.0;
   private double markerStep;
   private double snapStep;
   private float animationSpeed = 20.0F;
   private int precision;
   private boolean markers;
   private RussianPluralForms formatter;

   public NumberSettingBuilder range(double minimum, double maximum) {
      if (maximum <= minimum) {
         throw new IllegalArgumentException("max must be > min");
      } else {
         this.minimum = minimum;
         this.maximum = maximum;
         return this;
      }
   }

   public NumberSettingBuilder defaultValue(double value) {
      this.defaultValue = value;
      return this;
   }

   public NumberSettingBuilder multiplier(double multiplier) {
      if (multiplier <= 0.0) {
         throw new IllegalArgumentException("multiplier must be > 0");
      } else {
         this.multiplier = multiplier;
         return this;
      }
   }

   public NumberSettingBuilder divideBy(double divider) {
      if (divider <= 0.0) {
         throw new IllegalArgumentException("divider must be > 0");
      } else {
         this.multiplier /= divider;
         return this;
      }
   }

   public NumberSettingBuilder precision(int precision) {
      this.precision = Math.max(0, precision);
      return this;
   }

   public NumberSettingBuilder markers(double spacing) {
      if (spacing <= 0.0) {
         throw new IllegalArgumentException("marker spacing must be > 0");
      } else {
         this.markers = true;
         this.markerStep = spacing;
         return this;
      }
   }

   public NumberSettingBuilder showMarkers() {
      this.markers = true;
      return this;
   }

   public NumberSettingBuilder snapTo(double step) {
      if (step <= 0.0) {
         throw new IllegalArgumentException("snap step must be > 0");
      } else {
         this.snapStep = step;
         return this;
      }
   }

   public NumberSettingBuilder animationSpeed(float speed) {
      if (speed <= 0.0F) {
         throw new IllegalArgumentException("animation speed must be > 0");
      } else {
         this.animationSpeed = speed;
         return this;
      }
   }

   public NumberSettingBuilder formatter(RussianPluralForms formatter) {
      this.formatter = formatter;
      return this;
   }

   public NumberSettingBuilder formatter(String singular, String paucal, String plural) {
      return this.formatter(new RussianPluralForms(singular, paucal, plural));
   }

   public NumberSettingBuilder formatter(NumberUnit unit) {
      return this.formatter(unit.getFormatter());
   }

   public NumberSetting build() {
      if (this.maximum <= this.minimum) {
         throw new IllegalArgumentException("max must be > min");
      } else {
         this.defaultValue = Math.max(this.minimum, Math.min(this.maximum, this.defaultValue));
         return new NumberSetting(this);
      }
   }

   double getMinimum() {
      return this.minimum;
   }

   double getMaximum() {
      return this.maximum;
   }

   double getDefaultValue() {
      return this.defaultValue;
   }

   double getMultiplier() {
      return this.multiplier;
   }

   double getMarkerStep() {
      return this.markerStep;
   }

   double getSnapStep() {
      return this.snapStep;
   }

   float getAnimationSpeed() {
      return this.animationSpeed;
   }

   int getPrecision() {
      return this.precision;
   }

   boolean hasMarkers() {
      return this.markers;
   }

   RussianPluralForms getFormatter() {
      return this.formatter;
   }

   protected NumberSettingBuilder self() {
      return this;
   }
}
