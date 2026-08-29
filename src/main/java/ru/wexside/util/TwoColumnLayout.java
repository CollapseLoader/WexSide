package ru.wexside.util;

import java.util.Arrays;
import org.joml.Matrix4f;
import ru.wexside.misc.ListLayout;

public final class TwoColumnLayout {
   private final float value;
   private final int slot;
   private final float value2;

   public TwoColumnLayout(int n, float f, float f2) {
      this.slot = Math.max(1, n);
      this.value2 = f;
      this.value = f2;
   }

   public float process(ListLayout callback75, Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6) {
      float f7 = (f3 - this.value2 * (float)(this.slot - 1)) / (float)this.slot;
      float f8 = f2 + f4;
      float[] fArray = new float[this.slot];
      float[] fArray2 = new float[this.slot];
      Arrays.fill(fArray, f8);
      Arrays.fill(fArray2, f8);
      int n = callback75.getIntType();

      for(int i = 0; i < n; ++i) {
         int n2 = i % this.slot;
         float f9 = f + (float)n2 * (f7 + this.value2);
         float f10 = fArray[n2];
         float f11 = callback75.process(i);
         float f12 = !(f10 + f11 < f5) && !(f10 > f6) ? callback75.process2(i, matrix4f, f9, f10, f7) : f10 + f11;
         fArray[n2] = f12 + this.value;
         fArray2[n2] = f12;
      }

      float f13 = this.process3(fArray2) - f4;
      return Math.max(0.0F, f13 - f2);
   }

   public float process2(ListLayout callback75) {
      int n = callback75.getIntType();
      if (n == 0) {
         return 0.0F;
      } else {
         float[] fArray = new float[this.slot];

         for(int i = 0; i < n; ++i) {
            int n2 = i % this.slot;
            fArray[n2] += callback75.process(i) + this.value;
         }

         return this.process3(fArray) - this.value;
      }
   }

   private float process3(float[] fArray) {
      float f = fArray[0];

      for(int i = 1; i < fArray.length; ++i) {
         if (fArray[i] > f) {
            f = fArray[i];
         }
      }

      return f;
   }
}
