package ru.wexside.util;

import org.joml.Matrix4f;

public final class EspBoxBorderRenderer {
   public static final int SHADOW_COLOR = 1073741824;

   private EspBoxBorderRenderer() {
   }

   public static void process(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, int n, int n2, int n3, int n4) {
      process5(drawApi, matrix4f, f, f2, f + 1.5F, f4, 1073741824);
      process5(drawApi, matrix4f, f + 1.5F, f2, f3 - 1.5F, f2 + 1.5F, 1073741824);
      process5(drawApi, matrix4f, f3 - 1.5F, f2, f3, f4, 1073741824);
      process5(drawApi, matrix4f, f + 1.5F, f4 - 1.5F, f3 - 1.5F, f4, 1073741824);
      process2(drawApi, matrix4f, f + 0.5F, f2 + 0.5F, f + 1.0F, f4 - 0.5F, n, n4);
      process3(drawApi, matrix4f, f + 1.0F, f2 + 0.5F, f3 - 0.5F, f2 + 1.0F, n, n2);
      process2(drawApi, matrix4f, f3 - 1.0F, f2 + 0.5F, f3 - 0.5F, f4 - 0.5F, n2, n3);
      process3(drawApi, matrix4f, f + 1.0F, f4 - 1.0F, f3 - 1.0F, f4 - 0.5F, n4, n3);
   }

   public static void process2(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, int n, int n2) {
      drawApi.drawColorGradient(matrix4f, f, f2, f3 - f, f4 - f2, n2, n2, n, n);
   }

   public static void process3(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, int n, int n2) {
      drawApi.drawColorGradient(matrix4f, f, f2, f3 - f, f4 - f2, n, n2, n2, n);
   }

   public static void process4(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, int n, int n2, int n3, int n4) {
      float f5 = f3 - f;
      float f6 = f4 - f2;
      float f7 = Math.min(f5 / 4.0F, f6 / 4.0F);
      process5(drawApi, matrix4f, f, f2, f + 1.5F, f2 + f7, 1073741824);
      process5(drawApi, matrix4f, f, f2, f + f7, f2 + 1.5F, 1073741824);
      process5(drawApi, matrix4f, f3 - f7, f2, f3, f2 + 1.5F, 1073741824);
      process5(drawApi, matrix4f, f3 - 1.5F, f2, f3, f2 + f7, 1073741824);
      process5(drawApi, matrix4f, f, f4 - f7, f + 1.5F, f4, 1073741824);
      process5(drawApi, matrix4f, f, f4 - 1.5F, f + f7, f4, 1073741824);
      process5(drawApi, matrix4f, f3 - 1.5F, f4 - f7, f3, f4, 1073741824);
      process5(drawApi, matrix4f, f3 - f7, f4 - 1.5F, f3, f4, 1073741824);
      process2(drawApi, matrix4f, f + 0.5F, f2 + 0.5F, f + 1.0F, f2 + f7 - 0.5F, n, n4);
      process2(drawApi, matrix4f, f + 0.5F, f2 + 0.5F, f + f7 - 0.5F, f2 + 1.0F, n, n4);
      process2(drawApi, matrix4f, f3 - f7 + 0.5F, f2 + 0.5F, f3 - 0.5F, f2 + 1.0F, n, n2);
      process2(drawApi, matrix4f, f3 - 1.0F, f2 + 0.5F, f3 - 0.5F, f2 + f7 - 0.5F, n, n2);
      process2(drawApi, matrix4f, f + 0.5F, f4 - f7 + 0.5F, f + 1.0F, f4 - 0.5F, n2, n3);
      process2(drawApi, matrix4f, f + 0.5F, f4 - 1.0F, f + f7 - 0.5F, f4 - 0.5F, n2, n3);
      process2(drawApi, matrix4f, f3 - 1.0F, f4 - f7 + 0.5F, f3 - 0.5F, f4 - 0.5F, n4, n3);
      process2(drawApi, matrix4f, f3 - f7 + 0.5F, f4 - 1.0F, f3 - 0.5F, f4 - 0.5F, n4, n3);
   }

   public static void process5(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, int n) {
      drawApi.fillRectangle(matrix4f, f, f2, f3 - f, f4 - f2, n);
   }
}
