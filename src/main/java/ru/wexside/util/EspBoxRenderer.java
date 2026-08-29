package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.misc.WorldBoxSettings;
import ru.wexside.ui.GuiBounds;

public final class EspBoxRenderer {
   public void member1871(GuiDrawApi drawApi, Matrix4f matrix4f, GuiBounds bounds2, WorldBoxSettings dotted, float f) {
      float f2 = (float)(dotted.getScale() - 1) * 0.012F * Math.min(bounds2.getWidth(), bounds2.getHeight());
      float f3 = bounds2.getX() - f2;
      float f4 = bounds2.getY() - f2;
      float f5 = bounds2.getX() + bounds2.getWidth() + f2;
      float f6 = bounds2.getY() + bounds2.getHeight() + f2;
      float f7 = (f3 + f5) * 0.5F;
      float f8 = (f4 + f6) * 0.5F;
      float f9 = (f5 - f3) * 0.5F;
      float f10 = (f6 - f4) * 0.5F;
      float f11 = f9;
      float f12 = (float)Math.toRadians((double)f);
      float f13 = (float)Math.cos((double)f12);
      float f14 = (float)Math.sin((double)f12);
      int n = dotted.getColor();
      boolean bl = dotted.isDottedStyle();
      float f15 = 1.1F;
      float[] fArray = new float[8];
      float[] fArray2 = new float[8];

      for(int i = 0; i < 8; ++i) {
         float f16 = (i & 1) == 0 ? -f9 : f9;
         float f17 = (i & 2) == 0 ? -f10 : f10;
         float f18 = (i & 4) == 0 ? -f11 : f11;
         fArray[i] = f7 + f16 * f13 + f18 * f14;
         fArray2[i] = f8 + f17;
      }

      int[] nArray = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
      process3(drawApi, matrix4f, fArray, fArray2, nArray, ColorUtils.multiplyAlpha(n, 0.35F));
      int[][] nArrayArray = new int[][]{{0, 1}, {2, 3}, {4, 5}, {6, 7}, {0, 2}, {1, 3}, {4, 6}, {5, 7}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};

      for(int[] nArray2 : nArrayArray) {
         process(drawApi, matrix4f, bl, f15, n, fArray[nArray2[0]], fArray2[nArray2[0]], fArray[nArray2[1]], fArray2[nArray2[1]]);
      }
   }

   private static void process(GuiDrawApi drawApi, Matrix4f matrix4f, boolean bl, float f, int n, float f2, float f3, float f4, float f5) {
      boolean bl2 = Math.abs(f5 - f3) <= Math.abs(f4 - f2);
      float f6 = bl2 ? Math.abs(f4 - f2) : Math.abs(f5 - f3);
      if (!(f6 < 0.001F)) {
         float f8 = Math.min(f2, f4);
         float f9 = Math.min(f3, f5);
         if (!bl) {
            process2(drawApi, matrix4f, bl2, f, n, f8, f9, f6);
         } else {
            float f10 = 2.2F;
            float f11 = f10 + 1.6F;

            for(float f12 = 0.0F; f12 < f6; f12 += f11) {
               float f13 = Math.min(f10, f6 - f12);
               if (bl2) {
                  process2(drawApi, matrix4f, true, f, n, f8 + f12, f9, f13);
               } else {
                  process2(drawApi, matrix4f, false, f, n, f8, f9 + f12, f13);
               }
            }
         }
      }
   }

   private static void process2(GuiDrawApi drawApi, Matrix4f matrix4f, boolean bl, float f, int n, float f2, float f3, float f4) {
      if (bl) {
         drawApi.fillRectangle(matrix4f, f2, f3 - f * 0.5F, f4, f, n);
      } else {
         drawApi.fillRectangle(matrix4f, f2 - f * 0.5F, f3, f, f4, n);
      }
   }

   private static void process3(GuiDrawApi drawApi, Matrix4f matrix4f, float[] fArray, float[] fArray2, int[] nArray, int n) {
      float f = Float.MAX_VALUE;
      float f2 = Float.MAX_VALUE;
      float f3 = -Float.MAX_VALUE;
      float f4 = -Float.MAX_VALUE;

      for(int n2 : nArray) {
         f = Math.min(f, fArray[n2]);
         f2 = Math.min(f2, fArray2[n2]);
         f3 = Math.max(f3, fArray[n2]);
         f4 = Math.max(f4, fArray2[n2]);
      }

      drawApi.fillRectangle(matrix4f, f, f2, f3 - f, f4 - f2, n);
   }
}
