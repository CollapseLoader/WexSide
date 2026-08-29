package ru.wexside.misc;

import java.util.Objects;

public final class MsdfMetrics {
   private final float ascender;
   private final float lineHeight;
   private final float descender;

   public MsdfMetrics(float f, float f2, float f3) {
      this.lineHeight = f;
      this.ascender = f2;
      this.descender = f3;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof MsdfMetrics)) {
         return false;
      } else {
         MsdfMetrics msdfMetrics = (MsdfMetrics)object;
         return Float.compare(this.lineHeight, msdfMetrics.lineHeight) == 0
            && Float.compare(this.ascender, msdfMetrics.ascender) == 0
            && Float.compare(this.descender, msdfMetrics.descender) == 0;
      }
   }

   @Override
   public String toString() {
      float f = this.descender;
      float f2 = this.ascender;
      float f3 = this.lineHeight;
      return "Metrics[lineHeight=" + f3 + ", ascender=" + f2 + ", descender=" + f + "]";
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.lineHeight, this.ascender, this.descender);
   }

   public float getFloatType() {
      return this.descender;
   }

   public float getFloatType2() {
      return this.lineHeight + this.descender;
   }

   public float getFloatType3() {
      return this.ascender;
   }

   public float getFloatType4() {
      return this.lineHeight;
   }
}
