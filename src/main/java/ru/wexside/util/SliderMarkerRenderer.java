package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.ThemeColors;

public final class SliderMarkerRenderer {
   private final float innerInset;
   private float hoverProgress;
   private final float markerSize;
   private final int shadowColor;
   private boolean hovered;
   private final float positionAnimationSpeed;
   private float animatedY;
   private boolean positionInitialized;
   private float animatedX;
   private final float hoverAnimationSpeed;
   private final float shadowRadius;

   public SliderMarkerRenderer(float markerSize, float innerInset, float shadowRadius, float hoverAnimationSpeed, float positionAnimationSpeed, int shadowColor) {
      this.markerSize = markerSize;
      this.innerInset = innerInset;
      this.shadowRadius = shadowRadius;
      this.hoverAnimationSpeed = hoverAnimationSpeed;
      this.positionAnimationSpeed = positionAnimationSpeed;
      this.shadowColor = shadowColor;
   }

   public SliderMarkerRenderer(float f, float f2, float f3, float f4, int n) {
      this(f, f2, f3, f4, 30.0F, n);
   }

   public void setHovered(boolean hovered) {
      this.hovered = hovered;
   }

   public void render(Matrix4f matrix, GuiDrawApi renderer, float x, float y, int color) {
      if (!this.positionInitialized) {
         this.animatedX = x;
         this.animatedY = y;
         this.positionInitialized = true;
      }

      this.hoverProgress = FrameInterpolator.lerpTowards(this.hoverProgress, this.hovered ? 1.0F : 0.0F, this.hoverAnimationSpeed);
      if (this.positionAnimationSpeed <= 0.0F) {
         this.animatedX = x;
         this.animatedY = y;
      } else {
         this.animatedX = FrameInterpolator.lerpTowards(this.animatedX, x, this.positionAnimationSpeed);
         this.animatedY = FrameInterpolator.lerpTowards(this.animatedY, y, this.positionAnimationSpeed);
      }

      float scale = 1.0F + 0.5F * this.hoverProgress;
      float size = this.markerSize * scale;
      float halfSize = size / 2.0F;
      float innerSize = Math.max(0.0F, size - this.innerInset * 2.0F);
      renderer.drawRoundedShadow(
         matrix,
         this.animatedX - halfSize / 2.0F,
         this.animatedY - halfSize / 2.0F,
         size / 2.0F,
         size / 2.0F,
         size / 2.0F,
         this.shadowRadius * scale,
         this.shadowColor
      );
      renderer.drawRoundedRectangle(matrix, this.animatedX - halfSize, this.animatedY - halfSize, size, size, size, ThemeColors.backgroundControl());
      renderer.drawRoundedRectangle(matrix, this.animatedX - innerSize / 2.0F, this.animatedY - innerSize / 2.0F, innerSize, innerSize, innerSize, color);
   }

   float getAnimatedX(float fallback) {
      return this.positionInitialized ? this.animatedX : fallback;
   }

   void translate(float deltaX, float deltaY) {
      if (this.positionInitialized) {
         this.animatedX += deltaX;
         this.animatedY += deltaY;
      }
   }

   float getAnimatedY(float fallback) {
      return this.positionInitialized ? this.animatedY : fallback;
   }
}
