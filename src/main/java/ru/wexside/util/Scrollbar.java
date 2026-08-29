package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.input.InputBindings;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.ThemeColors;
import ru.wexside.ui.GuiBounds;

public final class Scrollbar {
   private ScrollController scrollController;
   private final float value;
   private final float value2;
   private boolean dragging;
   private final float value3;
   private final float value4 = 1.5F;
   private float value5;
   private final float value6;
   private final float BlockItem;
   private final float value7 = 3.0F;
   private float value8;
   private float value9;
   private float value10;
   private float value11;
   private float value12;
   private final float value13;

   public Scrollbar() {
      this.value3 = 128.0F;
      this.value6 = 3.5F;
      this.value = 4.0F;
      this.BlockItem = 8.0F;
      this.value13 = 20.0F;
      this.value2 = 20.0F;
   }

   private void setFloatType(float f) {
      if (this.dragging) {
         if (!InputBindings.isMouseButtonPressed(0)) {
            this.dragging = false;
         } else {
            this.setFloatType2(f);
         }
      }
   }

   public boolean isActive() {
      return this.dragging;
   }

   private boolean isActive2() {
      return this.scrollController != null && this.scrollController.getContentHeight() > this.value12 + 0.5F && this.value12 > 8.0F;
   }

   private GuiBounds getBounds() {
      float f = 11.0F;
      return new GuiBounds(this.value10 - 3.5F - 3.0F - 4.0F, this.value5, f, this.value12);
   }

   private void setFloatType2(float f) {
      float f2 = this.value12 - this.getFloatType();
      if (!(f2 <= 0.0F)) {
         float f3 = Math.max(0.0F, Math.min(1.0F, (f - this.value9 - this.value5) / f2));
         this.scrollController.scrollTo(f3 * this.scrollController.getMinimumOffset(this.value12), this.value12);
      }
   }

   public void process(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, ScrollController scrollController, float f4, float f5) {
      this.value10 = f;
      this.value5 = f2;
      this.value12 = f3;
      this.scrollController = scrollController;
      boolean bl = this.isActive2();
      this.value11 = FrameInterpolator.lerpTowards(this.value11, bl ? 1.0F : 0.0F, 20.0F);
      if (!bl) {
         this.dragging = false;
      }

      this.setFloatType(f5);
      boolean bl2 = this.dragging || this.getBounds().contains(f4, f5);
      this.value8 = FrameInterpolator.lerpTowards(this.value8, bl2 && bl ? 1.0F : 0.0F, 20.0F);
      if (!(this.value11 <= 0.01F)) {
         float f6 = 1.5F + 1.5F * this.value8;
         drawApi.drawRoundedRectangle(
            matrix4f,
            f - 3.5F - f6,
            this.getFloatType2(),
            f6,
            this.getFloatType(),
            128.0F,
            ColorUtils.multiplyAlpha(ColorUtils.lerp(ThemeColors.borderPrimary(), ThemeColors.borderStrong(), (double)this.value8), this.value11)
         );
      }
   }

   private float getFloatType() {
      return Math.max(8.0F, this.value12 * this.value12 / this.scrollController.getContentHeight());
   }

   private float getFloatType2() {
      float f = this.scrollController.getMinimumOffset(this.value12);
      float f2 = f >= -1.0E-4F ? 0.0F : Math.max(0.0F, Math.min(1.0F, this.scrollController.getOffset() / f));
      return this.value5 + (this.value12 - this.getFloatType()) * f2;
   }

   public boolean onMousePressed(int n, int n2, int n3) {
      if (n3 == 0 && this.isActive2() && this.getBounds().contains((float)n, (float)n2)) {
         float f = this.getFloatType2();
         float f2 = this.getFloatType();
         this.dragging = true;
         this.value9 = (float)n2 >= f && (float)n2 <= f + f2 ? (float)n2 - f : f2 / 2.0F;
         this.setFloatType2((float)n2);
         return true;
      } else {
         return false;
      }
   }
}
