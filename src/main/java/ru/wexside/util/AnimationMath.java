package ru.wexside.util;

public final class AnimationMath {
   private AnimationMath() {
   }

   public static float sin(float value) {
      return (float)Math.sin((double)value);
   }

   public static float lerp(float start, float end, float progress) {
      return start + (end - start) * clamp01(progress);
   }

   public static int lerpColor(int start, int end, float progress) {
      float t = clamp01(progress);
      int a = Math.round(lerp((float)(start >>> 24), (float)(end >>> 24), t));
      int r = Math.round(lerp((float)(start >>> 16 & 0xFF), (float)(end >>> 16 & 0xFF), t));
      int g = Math.round(lerp((float)(start >>> 8 & 0xFF), (float)(end >>> 8 & 0xFF), t));
      int b = Math.round(lerp((float)(start & 0xFF), (float)(end & 0xFF), t));
      return a << 24 | r << 16 | g << 8 | b;
   }

   public static int applyOpacity(int color, float opacity, float scale) {
      int alpha = Math.round((float)(color >>> 24) * clamp01(opacity) * clamp01(scale));
      return color & 16777215 | alpha << 24;
   }

   public static float easeInOutSine(float progress) {
      float t = clamp01(progress);
      return (1.0F - (float)Math.cos(Math.PI * (double)t)) * 0.5F;
   }

   public static float smoothStep(float progress) {
      float t = clamp01(progress);
      return t * t * (3.0F - 2.0F * t);
   }

   public static float easeOut(float progress, float exponent) {
      float t = clamp01(progress);
      return 1.0F - (float)Math.pow((double)(1.0F - t), (double)Math.max(0.01F, exponent));
   }

   public static float clamp01(float value) {
      return Math.max(0.0F, Math.min(1.0F, value));
   }
}
