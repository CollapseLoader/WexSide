package ru.wexside.misc;

import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import ru.wexside.module.hud.HealthHelperModule;
import ru.wexside.render.BakedItemIcon;
import ru.wexside.render.ItemIconCache;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class HotbarSlotRenderer {
   private static final int STENCIL_MASK_COLOR = -1;
   static final float value = 128.0F;
   static final float value2 = 51.0F;
   static final float value3 = 30.0F;
   private static final int SELECTION_BACKGROUND_COLOR = 654311423;
   private float value4;
   private float value5;
   static final float value6 = 20.0F;
   static final float value7 = 89.0F;
   private float value8;
   private static final int SELECTION_ACCENT_COLOR = -1;
   private static final int COOLDOWN_OVERLAY_COLOR = -16777216;
   private static final int USE_PROGRESS_COLOR = -1;
   private float value9;
   private float value10;
   static final float value11 = 200.0F;
   private int slot6;
   private final int slot7;
   static final float value12 = 102.0F;
   private static final int STACK_COUNT_COLOR = -1;
   static final float process = 76.0F;
   private float value13;

   public HotbarSlotRenderer(int n) {
      this.slot7 = n;
   }

   public void process(
      GuiDrawApi drawApi,
      Matrix4f matrix4f,
      ItemIconCache itemIconCache,
      BakedItemIcon iiIlilllII2,
      class_1799 stack,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      boolean bl,
      boolean bl2
   ) {
      int n = stack != null && !stack.method_7960() ? HealthHelperModule.process7(stack) : 0;
      if (n != 0) {
         this.slot6 = n;
      }

      this.value5 = FrameInterpolator.lerpTowards(this.value5, bl ? 1.0F : 0.0F, 30.0F);
      this.value9 = FrameInterpolator.lerpTowards(this.value9, n != 0 && !bl ? 1.0F : 0.0F, 30.0F);
      this.value13 = FrameInterpolator.lerpTowards(this.value13, this.process2(stack) ? 1.0F : 0.0F, 20.0F);
      this.value4 = FrameInterpolator.lerpTowards(this.value4, this.process7(stack, bl2) ? 1.0F : 0.0F, 20.0F);
      this.process6(drawApi, matrix4f, f, f2, f3, f5);
      this.process3(drawApi, matrix4f, f, f2, f3, f5, this.value10, this.value13, -16777216, 89.0F);
      this.process3(drawApi, matrix4f, f, f2, f3, f5, this.value8, this.value4, -1, 128.0F);
      if (stack != null && !stack.method_7960()) {
         float f6 = f + (f3 - f4) / 2.0F;
         float f7 = f2 + (f3 - f4) / 2.0F;
         itemIconCache.process3(drawApi, matrix4f, iiIlilllII2, f6, f7, f4);
         this.process5(drawApi, matrix4f, stack, f6, f7, f4);
         this.process8(drawApi, matrix4f, stack, f6, f7, f4);
      }
   }

   private boolean process2(class_1799 stack) {
      class_746 player2 = class_310.method_1551().field_1724;
      if (player2 != null && stack != null && !stack.method_7960() && player2.method_7357().method_7904(stack)) {
         this.value10 = player2.method_7357().method_7905(stack, class_310.method_1551().method_61966().method_60637(false));
         return true;
      } else {
         this.value10 = 0.0F;
         return false;
      }
   }

   private void process3(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6, int n, float f7) {
      if (!(f6 <= 0.001F)) {
         drawApi.drawRoundedRectangle(matrix4f, f, f2, f3, f3, f4, ColorUtils.withAlpha(n, 51.0F * f6));
         if (f5 > 0.0F) {
            float f8 = f3 * f5;
            drawApi.beginStencil(this.slot7);
            drawApi.fillRectangle(matrix4f, f, f2 + f3 - f8, f3, f8, -1);
            drawApi.applyStencilMask(this.slot7);
            drawApi.drawRoundedRectangle(matrix4f, f, f2, f3, f3, f4, ColorUtils.withAlpha(n, 76.0F * f6));
            drawApi.endStencil();
         }

         drawApi.drawRoundedOutline(matrix4f, f, f2, f3, f3, f4, f4 / 9.0F, ColorUtils.withAlpha(n, f7 * f6));
      }
   }

   private void process4(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, int n, int n2, boolean bl) {
      drawApi.drawSplitGradientRectangle(matrix4f, f - f3, f2, f3 * 1.5F, f3, 0, n, 0, 2);
      drawApi.drawSplitGradientRectangle(matrix4f, f + f3 * 0.5F, f2, f3 * 1.5F, f3, 0, 0, n, 1);
      if (bl) {
         drawApi.drawGradientRoundedRectangleUniform(matrix4f, f, f2, f3, f3, f4, f4 / 9.0F, n2, n2, n2, n2);
      } else {
         drawApi.drawGradientRoundedRectangleUniform(matrix4f, f, f2, f3, f3, f4, f4 / 9.0F, n2, n2, 0, 0);
      }
   }

   private void process5(GuiDrawApi drawApi, Matrix4f matrix4f, class_1799 stack, float f, float f2, float f3) {
      if (stack.method_7963() && stack.method_7919() > 0) {
         float f4 = 1.0F - (float)stack.method_7919() / (float)stack.method_7936();
         float f6 = f2 + f3 - 1.0F;
         drawApi.fillRectangle(matrix4f, f, f6, f3, 1.0F, ColorUtils.rgba(0, 0, 0, 200));
         int n = ColorUtils.lerp(ColorUtils.rgb(230, 40, 40), ColorUtils.rgb(70, 220, 70), (double)f4);
         drawApi.fillRectangle(matrix4f, f, f6, f3 * f4, 1.0F, n);
      }
   }

   private void process6(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4) {
      boolean bl2 = this.value9 > 0.001F && this.slot6 != 0;
      boolean bl = this.value5 > 0.001F;
      if (bl2 || bl) {
         drawApi.beginStencil(this.slot7);
         drawApi.drawRoundedRectangle(matrix4f, f, f2, f3, f3, f4, -1);
         drawApi.applyStencilMask(this.slot7);
         if (bl2) {
            float f5 = (float)ColorUtils.unpackRgba(this.slot6)[3] / 255.0F * this.value9;
            this.process4(drawApi, matrix4f, f, f2, f3, f4, ColorUtils.withAlpha(this.slot6, 102.0F * f5), ColorUtils.withAlpha(this.slot6, 200.0F * f5), true);
         }

         if (bl) {
            drawApi.drawRoundedRectangle(matrix4f, f, f2, f3, f3, f4, ColorUtils.multiplyAlpha(654311423, this.value5));
            this.process4(
               drawApi, matrix4f, f, f2, f3, f4, ColorUtils.withAlpha(-1, 102.0F * this.value5), ColorUtils.withAlpha(-1, 200.0F * this.value5), false
            );
         }

         drawApi.endStencil();
      }
   }

   private boolean process7(class_1799 stack, boolean bl) {
      class_746 player2 = class_310.method_1551().field_1724;
      if (player2 != null && bl && stack != null && !stack.method_7960()) {
         int n = player2.method_6030().method_7935(player2);
         this.value8 = n > 0 ? (float)player2.method_6048() / (float)n : 0.0F;
         return true;
      } else {
         this.value8 = 0.0F;
         return false;
      }
   }

   private void process8(GuiDrawApi drawApi, Matrix4f matrix4f, class_1799 stack, float f, float f2, float f3) {
      if (stack.method_7947() > 1) {
         String string = String.valueOf(stack.method_7947());
         float f4 = f3 * 0.42F;
         float f5 = FontRegistry.font6.process3(string, f4);
         float f6 = FontRegistry.font6.process4(string, f4);
         FontRegistry.font6.process2(matrix4f, drawApi, string, f + f3 - f5, f2 + f3 - f6, f4, -1);
      }
   }
}
