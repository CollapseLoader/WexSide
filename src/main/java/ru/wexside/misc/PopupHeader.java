package ru.wexside.misc;

import java.util.function.IntSupplier;
import org.joml.Matrix4f;
import ru.wexside.ui.GuiBounds;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class PopupHeader {
   private float value = 5.5F;
   private IntSupplier intSupplier = ThemeColors::textPrimary;
   private final String string4;
   private float value2 = 6.75F;
   private final float value3;
   private final GuiBounds bounds2;
   private final float value4 = 6.0F;
   private final String string5;
   private final float value5 = 2.0F;
   private final String string6;

   public PopupHeader(GuiBounds bounds2, String string, String string2, String string3) {
      this.value3 = 2.0F;
      this.bounds2 = bounds2;
      this.string6 = string;
      this.string5 = string2;
      this.string4 = string3;
   }

   public float getFloatType() {
      return this.value;
   }

   public void BlockHitResult(Matrix4f matrix4f, GuiDrawApi drawApi) {
      this.process3(matrix4f, drawApi, 1.0F);
   }

   public float getFloatType2() {
      return this.getFloatType3() + 2.0F + this.getDescriptionHeight();
   }

   private static int process(int n, float f) {
      if (f >= 0.999F) {
         return n;
      } else {
         int n2 = n >>> 24 & 0xFF;
         return ColorUtils.withAlpha(n, (float)(n2 == 0 ? 255 : n2) * f);
      }
   }

   public PopupHeader process2(IntSupplier intSupplier) {
      this.intSupplier = intSupplier;
      return this;
   }

   public GuiBounds getBounds() {
      return this.bounds2;
   }

   private float getFloatType3() {
      return FontRegistry.font4.process4(this.string6, this.value);
   }

   public float getFloatType4() {
      return 6.0F;
   }

   public void process3(Matrix4f matrix4f, GuiDrawApi drawApi, float f) {
      FontRegistry.font4.process2(matrix4f, drawApi, this.string6, this.bounds2.getX(), this.bounds2.getY(), this.value, process(ThemeColors.accent(), f));
      float f2 = FontRegistry.font3.process4(this.string5, 6.0F);
      float f3 = this.bounds2.getX() + FontRegistry.font4.process3(this.string6, this.value) + 2.0F;
      float f4 = this.bounds2.getY() + (this.getFloatType3() - f2) / 2.0F;
      FontRegistry.font3.process5(matrix4f, drawApi, this.string5, f3, f4, 6.0F, process(ThemeColors.accent(), f));
      FontRegistry.font2
         .process2(
            matrix4f,
            drawApi,
            this.string4,
            this.bounds2.getX(),
            this.bounds2.getY() + this.getFloatType3() + 2.0F,
            this.value2,
            process(this.intSupplier.getAsInt(), f)
         );
   }

   public float getFloatType5() {
      return this.value2;
   }

   public String getString() {
      return this.string4;
   }

   public PopupHeader withDescriptionFontSize(float f) {
      this.value2 = f;
      return this;
   }

   public String getString2() {
      return this.string6;
   }

   public PopupHeader process4(float f) {
      this.value = f;
      return this;
   }

   public IntSupplier RaycastContext() {
      return this.intSupplier;
   }

   public float getFloatType6() {
      return 2.0F;
   }

   public String getString3() {
      return this.string5;
   }

   public float getFloatType7() {
      return 2.0F;
   }

   private float getDescriptionHeight() {
      return FontRegistry.font2.process4(this.string4, this.value2);
   }
}
