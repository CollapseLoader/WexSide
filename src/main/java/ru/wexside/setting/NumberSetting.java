package ru.wexside.setting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.render.RenderFrameClock;
import ru.wexside.util.RussianPluralForms;

public final class NumberSetting extends Setting implements ConfigSerializable {
   private final double minimum;
   private final double maximum;
   private final double defaultValue;
   private final double multiplier;
   private final float animationSpeed;
   private final int precision;
   private final double markerStep;
   private final double snapStep;
   private final boolean markersEnabled;
   private double normalizedValue;
   private float animatedValue = Float.NaN;
   private int animationFrame = -1;
   private RussianPluralForms formatter;

   NumberSetting(NumberSettingBuilder builder) {
      super(builder);
      this.minimum = builder.getMinimum();
      this.maximum = builder.getMaximum();
      this.defaultValue = builder.getDefaultValue();
      this.multiplier = builder.getMultiplier();
      this.animationSpeed = builder.getAnimationSpeed();
      this.precision = builder.getPrecision();
      this.markersEnabled = builder.hasMarkers();
      this.markerStep = builder.getMarkerStep();
      this.snapStep = builder.getSnapStep();
      this.formatter = builder.getFormatter();
      this.setUnscaledValue(this.defaultValue);
   }

   public static NumberSettingBuilder builder() {
      return new NumberSettingBuilder();
   }

   @Override
   protected void readValue(DataInputStream input) throws IOException {
      this.setUnscaledValue(input.readDouble());
   }

   @Override
   protected void writeValue(DataOutputStream output) throws IOException {
      output.writeDouble(this.getUnscaledValue());
   }

   public double getValue() {
      return this.getUnscaledValue() * this.multiplier;
   }

   public float getFloatValue() {
      return (float)this.getValue();
   }

   public int getIntValue() {
      return (int)this.getValue();
   }

   public long getLongValue() {
      return (long)this.getValue();
   }

   public double getUnscaledValue() {
      return this.minimum + (this.maximum - this.minimum) * this.normalizedValue;
   }

   public void setUnscaledValue(double value) {
      double range = this.maximum - this.minimum;
      this.normalizedValue = range <= 0.0 ? 0.0 : clamp01((value - this.minimum) / range);
   }

   public void setValue(double value) {
      this.setUnscaledValue(value);
   }

   public double getMinimum() {
      return this.minimum;
   }

   public double getMaximum() {
      return this.maximum;
   }

   public double getNormalizedValue() {
      return this.normalizedValue;
   }

   public void setNormalizedValue(double value) {
      this.normalizedValue = clamp01(value);
   }

   public float getAnimatedValue() {
      float target = this.getFloatValue();
      int frame = RenderFrameClock.currentFrame();
      if (Float.isNaN(this.animatedValue) || frame - this.animationFrame > 5) {
         this.animatedValue = target;
      } else if (frame != this.animationFrame) {
         this.animatedValue = FrameInterpolator.lerpTowards(this.animatedValue, target, this.animationSpeed);
         if ((double)Math.abs(this.animatedValue - target) < (this.maximum - this.minimum) * this.multiplier * 1.0E-4) {
            this.animatedValue = target;
         }
      }

      this.animationFrame = frame;
      return this.animatedValue;
   }

   public int getPrecision() {
      return this.precision;
   }

   public double getMarkerStep() {
      return this.markerStep;
   }

   public double getSnapStep() {
      return this.snapStep;
   }

   public boolean hasFormatter() {
      return this.formatter != null;
   }

   public boolean hasMarkers() {
      return this.markersEnabled || this.markerStep > 0.0;
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

   public String getFormattedValue() {
      return this.format(this.getUnscaledValue());
   }

   public void setFormatter(RussianPluralForms formatter) {
      this.formatter = formatter;
   }

   public NumberSetting copy() {
      NumberSettingBuilder builder = (NumberSettingBuilder)builder().id(this.getId()).name(this.getDisplayName());
      builder.range(this.minimum, this.maximum)
         .defaultValue(this.getUnscaledValue())
         .multiplier(this.multiplier)
         .precision(this.precision)
         .animationSpeed(this.animationSpeed);
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

      NumberSetting result = builder.build();
      result.restorePayload(this.copyPayload());
      return result;
   }

   private static double clamp01(double value) {
      return Math.max(0.0, Math.min(1.0, value));
   }
}
