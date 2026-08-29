package ru.wexside.util;

import java.awt.Color;
import ru.wexside.misc.AstolfoState;

public final class AstolfoColorController {
   public AstolfoState fromColor(AstolfoState state, int argb) {
      float[] hsb = rgbToHsb(argb);
      float phaseOffset = state.getPhaseOffset();
      if (hasVisibleHue(hsb)) {
         phaseOffset = this.phaseOffsetForHue(state, hsb[0], phaseOffset);
      }

      return new AstolfoState(phaseOffset, state.getHueSpeed(), clamp01(hsb[1]), clamp01(hsb[2]), clamp01((float)(argb >>> 24 & 0xFF) / 255.0F));
   }

   public float getCurrentHue(AstolfoState state) {
      return wrapHue(this.animatedBaseHue(state) + state.getPhaseOffset());
   }

   public AstolfoState withHsb(AstolfoState state, float hue, float saturation, float brightness, int alpha) {
      float phaseOffset = state.getPhaseOffset();
      if (saturation > 1.0E-4F && brightness > 1.0E-4F) {
         phaseOffset = this.phaseOffsetForHue(state, clamp01(hue), state.getPhaseOffset());
      }

      return new AstolfoState(phaseOffset, state.getHueSpeed(), clamp01(saturation), clamp01(brightness), clamp01((float)clampByte(alpha) / 255.0F));
   }

   public int getColor(AstolfoState state, float phase) {
      return this.toArgb(state, this.animatedHue(state, state.getPhaseOffset(), phase));
   }

   public int getStaticGradientColor(AstolfoState state, float phase) {
      return this.toArgb(state, mirroredHue(wrapHue(phase)));
   }

   public AstolfoState withCurrentHue(AstolfoState state, float hue) {
      return state.withPhaseOffset(wrapHue(hue - this.animatedBaseHue(state)));
   }

   public float[] getCurrentHsb(AstolfoState state) {
      return new float[]{this.animatedHue(state, state.getPhaseOffset(), 0.0F), state.getSaturation(), state.getBrightness()};
   }

   private int toArgb(AstolfoState state, float hue) {
      return clampByte(Math.round(state.getAlpha() * 255.0F)) << 24 | Color.HSBtoRGB(hue, state.getSaturation(), state.getBrightness()) & 16777215;
   }

   private float animatedHue(AstolfoState state, float offset, float phase) {
      float hue = wrapHue(this.animatedBaseHue(state) + wrapHue(offset) + phase * 0.5F);
      return mirroredHue(hue);
   }

   private float phaseOffsetForHue(AstolfoState state, float hue, float fallbackOffset) {
      float target = Math.max(0.5F, clamp01(hue));
      float baseHue = this.animatedBaseHue(state);
      float fallbackHue = wrapHue(baseHue + fallbackOffset);
      float lower = clamp01(target - 0.5F);
      float upper = clamp01(1.5F - target);
      float nearest = nearestCircularHue(fallbackHue, lower, upper);
      return wrapHue(nearest - baseHue);
   }

   private float animatedBaseHue(AstolfoState state) {
      double value = (double)System.currentTimeMillis() * (double)state.getHueSpeed() / 50.0 % 1.0;
      return wrapHue((float)value);
   }

   private static float mirroredHue(float hue) {
      float shifted = 0.5F + hue;
      if (shifted > 1.0F) {
         shifted = 2.0F - shifted;
      }

      return clamp01(shifted);
   }

   private static float nearestCircularHue(float source, float first, float second) {
      return circularDistance(source, first) <= circularDistance(source, second) ? first : second;
   }

   private static float circularDistance(float first, float second) {
      float distance = Math.abs(first - second);
      return Math.min(distance, 1.0F - distance);
   }

   private static boolean hasVisibleHue(float[] hsb) {
      return hsb[1] > 1.0E-4F && hsb[2] > 1.0E-4F;
   }

   private static float[] rgbToHsb(int argb) {
      return Color.RGBtoHSB(argb >> 16 & 0xFF, argb >> 8 & 0xFF, argb & 0xFF, null);
   }

   private static float wrapHue(float value) {
      float wrapped = value % 1.0F;
      return wrapped < 0.0F ? wrapped + 1.0F : wrapped;
   }

   private static float clamp01(float value) {
      return Math.max(0.0F, Math.min(1.0F, value));
   }

   private static int clampByte(int value) {
      return Math.clamp((long)value, 0, 255);
   }
}
