package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.ScaleSettings;
import ru.wexside.misc.ThemeColors;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.SliderTrack;

public final class SliderRenderer {
   private final float value;
   private final float value2;
   private final float value3 = 28.0F;
   private final float value4;
   private final float value5;
   private final float value6;
   private final float value7;
   private final float value8;
   private final float value9;
   private final float value10;
   private final float value11 = 23.0F;
   private final float value12;

   public SliderRenderer() {
      this.value6 = 5.0F;
      this.value8 = 3.0F;
      this.value4 = 2.0F;
      this.value = 1.5F;
      this.value2 = 3.0F;
      this.value12 = 2.0F;
      this.value7 = 0.5F;
      this.value9 = 1.0F;
      this.value5 = 5.0F;
      this.value10 = 3.0F;
   }

   public void process(Matrix4f matrix4f, GuiDrawApi drawApi, float f, float f2, int n) {
      drawApi.drawRoundedRectangle(matrix4f, f - 3.0F, f2 - 3.0F, 6.0F, 6.0F, 6.0F, n);
      float f3 = Math.max(0.5F, 2.0F);
      drawApi.drawRoundedRectangle(matrix4f, f - f3, f2 - f3, f3 * 2.0F, f3 * 2.0F, f3 * 2.0F, ThemeColors.backgroundControl());
   }

   public void process2(Matrix4f matrix4f, GuiDrawApi drawApi, SliderTrack track, int n) {
      drawApi.drawRoundedRectangle(matrix4f, track.x(), track.y(), track.width(), 1.5F, 2.0F, n);
   }

   public float getFloatType() {
      return 5.0F;
   }

   public float getFloatType2() {
      return 3.0F;
   }

   public float process3(float f, ScaleSettings scaleSettings) {
      float f2 = Math.max(0.0F, Math.min(1.0F, f));
      int n = this.process8(scaleSettings);
      if (n <= 0) {
         return f2;
      } else {
         float f3 = this.process5(n);
         if (f3 <= 0.0F) {
            return f2;
         } else {
            float f4 = f2 * f3;
            float f5 = 0.0F;

            for(int i = 0; i < n; ++i) {
               float f6 = this.value13(i, n);
               float f7 = f5 + f6;
               if (f4 <= f7 || i == n - 1) {
                  if (f6 <= 0.0F) {
                     return (float)i / (float)n;
                  } else {
                     float f8 = (f4 - f5) / f6;
                     f8 = Math.max(0.0F, Math.min(1.0F, f8));
                     return ((float)i + f8) / (float)n;
                  }
               }

               f5 = f7;
            }

            return 1.0F;
         }
      }
   }

   public float process4(float f, ScaleSettings scaleSettings) {
      float f2 = Math.max(0.0F, Math.min(1.0F, f));
      int n = this.process8(scaleSettings);
      if (n <= 0) {
         return f2;
      } else if (f2 >= 1.0F) {
         return 1.0F;
      } else {
         float f3 = f2 * (float)n;
         int n2 = Math.min(n - 1, (int)Math.floor((double)f3));
         float f4 = f3 - (float)n2;
         float f5 = this.process5(n);
         if (f5 <= 0.0F) {
            return f2;
         } else {
            float f6 = 0.0F;

            for(int i = 0; i < n2; ++i) {
               f6 += this.value13(i, n);
            }

            float f7 = this.value13(n2, n);
            return (f6 + f7 * f4) / f5;
         }
      }
   }

   private float process5(int n) {
      float f = 0.0F;

      for(int i = 0; i < n; ++i) {
         f += this.value13(i, n);
      }

      return f;
   }

   public void renderTickMarks(Matrix4f matrix4f, GuiDrawApi drawApi, SliderTrack track, ScaleSettings scaleSettings, int n, int n2) {
      int n3 = this.process8(scaleSettings);
      if (n3 > 0) {
         float f = track.y() + 1.5F;
         float f2 = f + 2.0F + 1.0F;

         for(int i = 0; i <= n3; ++i) {
            float f4 = (float)i / (float)n3;
            float f5 = track.x() + track.width() * this.process4(f4, scaleSettings);
            if (i > 0 && i < n3) {
               float f3 = f5 - 0.25F;
               drawApi.drawRoundedRectangle(matrix4f, f3, f, 0.5F, 2.0F, 1.0F, n);
            }

            if (i != 0 && i != n3) {
               float f3 = (float)i / (float)n3;
               double d = scaleSettings.minimum() + (scaleSettings.maximum() - scaleSettings.minimum()) * (double)f3;
               String string = NumberFormatting.format(d, scaleSettings.precision());
               float f6 = f5 - FontRegistry.font2.process3(string, 5.0F) / 2.0F;
               FontRegistry.font2.process2(matrix4f, drawApi, string, f6, f2, 5.0F, n2);
            }
         }
      }
   }

   public float process6(boolean bl) {
      return bl ? 28.0F : 23.0F;
   }

   private float value13(int n, int n2) {
      if (n >= 0 && n < n2) {
         float f = 3.0F;
         return n != 0 && n != n2 - 1 ? 1.0F : 1.0F / f;
      } else {
         return 0.0F;
      }
   }

   public SliderTrack process7(GuiBounds bounds2, float f) {
      float f2 = bounds2.getX() + 5.0F;
      float f3 = bounds2.getY() + 3.0F + f + 2.0F;
      float f4 = Math.max(1.0F, bounds2.getWidth() - 10.0F);
      return new SliderTrack(f2, f3, f4);
   }

   public float getFloatType3() {
      return 1.5F;
   }

   public int process8(ScaleSettings scaleSettings) {
      if (!scaleSettings.hasMarkers()) {
         return 0;
      } else if (scaleSettings.hasMarkerSpacing()) {
         return (int)Math.round(NumberFormatting.normalize(scaleSettings.minimum(), scaleSettings.maximum(), scaleSettings.markerStep()) * 10.0);
      } else if (scaleSettings.hasSnapStep()) {
         return (int)Math.round(NumberFormatting.normalize(scaleSettings.minimum(), scaleSettings.maximum(), scaleSettings.snapStep()) * 10.0);
      } else {
         double d2 = scaleSettings.maximum() - scaleSettings.minimum();
         if (d2 <= 0.0) {
            return 0;
         } else {
            if (scaleSettings.precision() == 0) {
               double d3 = Math.rint(d2);
               if (Math.abs(d3 - d2) < 1.0E-9 && d3 >= 2.0 && d3 <= 12.0) {
                  return (int)d3;
               }
            }

            return 10;
         }
      }
   }
}
