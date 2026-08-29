package ru.wexside.setting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.util.RussianPluralForms;

public final class RangeSetting extends Setting implements ConfigSerializable {
   private double minimum;
   private double maximum;
   private double lowerNormalizedValue;
   private double upperNormalizedValue;
   private final double defaultLowerNormalizedValue;
   private final double defaultUpperNormalizedValue;
   private final double multiplier;
   private final int precision;
   private final double markerStep;
   private final double snapStep;
   private final boolean markersEnabled;
   private RussianPluralForms formatter;

   RangeSetting(RangeSettingBuilder builder) {
      super(builder);
      this.minimum = builder.getMinimum();
      this.maximum = builder.getMaximum();
      this.lowerNormalizedValue = builder.getDefaultLowerNormalizedValue();
      this.upperNormalizedValue = builder.getDefaultUpperNormalizedValue();
      this.defaultLowerNormalizedValue = builder.getDefaultLowerNormalizedValue();
      this.defaultUpperNormalizedValue = builder.getDefaultUpperNormalizedValue();
      this.multiplier = builder.getMultiplier();
      this.precision = builder.getPrecision();
      this.markerStep = builder.getMarkerStep();
      this.snapStep = builder.getSnapStep();
      this.markersEnabled = builder.hasMarkers();
      this.formatter = builder.getFormatter();
   }

   public static RangeSettingBuilder builder() {
      return new RangeSettingBuilder();
   }

   @Override
   protected void readValue(DataInputStream input) throws IOException {
      double lower = input.readDouble();
      double upper = input.readDouble();
      double range = this.maximum - this.minimum;
      if (!(range <= 0.0) && !(lower < this.minimum) && !(upper > this.maximum) && !(upper < lower)) {
         this.lowerNormalizedValue = (lower - this.minimum) / range;
         this.upperNormalizedValue = (upper - this.minimum) / range;
      } else {
         this.lowerNormalizedValue = this.defaultLowerNormalizedValue;
         this.upperNormalizedValue = this.defaultUpperNormalizedValue;
      }
   }

   @Override
   protected void writeValue(DataOutputStream output) throws IOException {
      output.writeDouble(this.getLowerUnscaledValue());
      output.writeDouble(this.getUpperUnscaledValue());
   }

   public double getMinimum() {
      return this.minimum;
   }

   public double getMaximum() {
      return this.maximum;
   }

   public double getLowerNormalizedValue() {
      return this.lowerNormalizedValue;
   }

   public double getUpperNormalizedValue() {
      return this.upperNormalizedValue;
   }

   public double getMarkerStep() {
      return this.markerStep;
   }

   public double getSnapStep() {
      return this.snapStep;
   }

   public int getPrecision() {
      return this.precision;
   }

   public void setMinimum(double minimum) {
      this.minimum = minimum;
   }

   public void setMaximum(double maximum) {
      this.maximum = maximum;
   }

   public void setLowerNormalizedValue(double value) {
      this.lowerNormalizedValue = clamp01(value);
   }

   public void setUpperNormalizedValue(double value) {
      this.upperNormalizedValue = clamp01(value);
   }

   public double getLowerUnscaledValue() {
      return this.denormalize(this.lowerNormalizedValue);
   }

   public double getUpperUnscaledValue() {
      return this.denormalize(this.upperNormalizedValue);
   }

   public double getLowerValue() {
      return this.getLowerUnscaledValue() * this.multiplier;
   }

   public double getUpperValue() {
      return this.getUpperUnscaledValue() * this.multiplier;
   }

   public float getLowerFloatValue() {
      return (float)this.getLowerValue();
   }

   public float getUpperFloatValue() {
      return (float)this.getUpperValue();
   }

   public int getLowerIntValue() {
      return (int)this.getLowerValue();
   }

   public int getUpperIntValue() {
      return (int)this.getUpperValue();
   }

   public long getLowerLongValue() {
      return (long)this.getLowerValue();
   }

   public long getUpperLongValue() {
      return (long)this.getUpperValue();
   }

   public boolean hasMarkers() {
      return this.markersEnabled || this.markerStep > 0.0;
   }

   public boolean hasFormatter() {
      return this.formatter != null;
   }

   public boolean hasSnapStep() {
      return this.snapStep > 0.0;
   }

   public boolean hasMarkerSpacing() {
      return this.markerStep > 0.0;
   }

   public String format(double value) {
      return this.formatter == null ? "" : this.formatter.process3(value);
   }

   public void setFormatter(RussianPluralForms formatter) {
      this.formatter = formatter;
   }

   public RangeSetting copy() {
      RangeSettingBuilder builder = (RangeSettingBuilder)builder().id(this.getId()).name(this.getDisplayName());
      builder.range(this.minimum, this.maximum)
         .defaultNormalizedRange(this.lowerNormalizedValue, this.upperNormalizedValue)
         .multiplier(this.multiplier)
         .precision(this.precision);
      if (this.markerStep > 0.0) {
         builder.markers(this.markerStep);
      } else if (this.markersEnabled) {
         builder.showMarkers();
      }

      if (this.snapStep > 0.0) {
         builder.snapTo(this.snapStep);
      }

      if (this.formatter != null) {
         builder.formatter(this.formatter);
      }

      RangeSetting result = builder.build();
      result.restorePayload(this.copyPayload());
      return result;
   }

   private double denormalize(double normalizedValue) {
      return this.minimum + (this.maximum - this.minimum) * normalizedValue;
   }

   private static double clamp01(double value) {
      return Math.max(0.0, Math.min(1.0, value));
   }
}
