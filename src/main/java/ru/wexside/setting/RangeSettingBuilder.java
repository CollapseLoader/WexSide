package ru.wexside.setting;

import ru.wexside.util.RussianPluralForms;

public final class RangeSettingBuilder extends SettingBuilder {
   private double minimum;
   private double maximum = 1.0;
   private double defaultLowerNormalizedValue;
   private double defaultUpperNormalizedValue;
   private double multiplier = 1.0;
   private double markerStep;
   private double snapStep;
   private int precision;
   private boolean markers;
   private RussianPluralForms formatter;

   public RangeSettingBuilder range(double minimum, double maximum) {
      if (maximum <= minimum) {
         throw new IllegalArgumentException("max must be > min");
      } else {
         this.minimum = minimum;
         this.maximum = maximum;
         return this;
      }
   }

   public RangeSettingBuilder defaultNormalizedRange(double lower, double upper) {
      if (lower < 0.0 || lower > 1.0) {
         throw new IllegalArgumentException("lower value must be in [0, 1]");
      } else if (upper < 0.0 || upper > 1.0) {
         throw new IllegalArgumentException("upper value must be in [0, 1]");
      } else if (upper < lower) {
         throw new IllegalArgumentException("upper value must be >= lower value");
      } else {
         this.defaultLowerNormalizedValue = lower;
         this.defaultUpperNormalizedValue = upper;
         return this;
      }
   }

   public RangeSettingBuilder multiplier(double multiplier) {
      if (multiplier <= 0.0) {
         throw new IllegalArgumentException("multiplier must be > 0");
      } else {
         this.multiplier = multiplier;
         return this;
      }
   }

   public RangeSettingBuilder divideBy(double divider) {
      if (divider <= 0.0) {
         throw new IllegalArgumentException("divider must be > 0");
      } else {
         this.multiplier /= divider;
         return this;
      }
   }

   public RangeSettingBuilder precision(int precision) {
      this.precision = Math.max(0, precision);
      return this;
   }

   public RangeSettingBuilder showMarkers() {
      this.markers = true;
      return this;
   }

   public RangeSettingBuilder markers(double spacing) {
      if (spacing <= 0.0) {
         throw new IllegalArgumentException("marker spacing must be > 0");
      } else {
         this.markers = true;
         this.markerStep = spacing;
         return this;
      }
   }

   public RangeSettingBuilder snapTo(double step) {
      if (step <= 0.0) {
         throw new IllegalArgumentException("snap step must be > 0");
      } else {
         this.snapStep = step;
         return this;
      }
   }

   public RangeSettingBuilder formatter(RussianPluralForms formatter) {
      this.formatter = formatter;
      return this;
   }

   public RangeSettingBuilder formatter(String singular, String paucal, String plural) {
      return this.formatter(new RussianPluralForms(singular, paucal, plural));
   }

   public RangeSettingBuilder formatter(NumberUnit unit) {
      return this.formatter(unit.getFormatter());
   }

   public RangeSetting build() {
      if (this.maximum <= this.minimum) {
         throw new IllegalArgumentException("max must be > min");
      } else {
         return new RangeSetting(this);
      }
   }

   double getMinimum() {
      return this.minimum;
   }

   double getMaximum() {
      return this.maximum;
   }

   double getDefaultLowerNormalizedValue() {
      return this.defaultLowerNormalizedValue;
   }

   double getDefaultUpperNormalizedValue() {
      return this.defaultUpperNormalizedValue;
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

   int getPrecision() {
      return this.precision;
   }

   boolean hasMarkers() {
      return this.markers;
   }

   RussianPluralForms getFormatter() {
      return this.formatter;
   }

   protected RangeSettingBuilder self() {
      return this;
   }
}
