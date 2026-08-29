package ru.wexside.misc;

import java.util.Objects;

public final class MsdfGlyph {
   private final float planeBottom;
   private final float advance;
   private final float planeTop;
   private final float v1;
   private final float u0;
   private final float planeLeft;
   private final float v0;
   private final float u1;
   private final int codepoint;
   private final float planeRight;

   public MsdfGlyph(int n, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9) {
      this.codepoint = n;
      this.planeLeft = f;
      this.planeTop = f2;
      this.planeRight = f3;
      this.planeBottom = f4;
      this.u0 = f5;
      this.v0 = f6;
      this.u1 = f7;
      this.v1 = f8;
      this.advance = f9;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof MsdfGlyph)) {
         return false;
      } else {
         MsdfGlyph msdfGlyph = (MsdfGlyph)object;
         return this.codepoint == msdfGlyph.codepoint
            && Float.compare(this.planeLeft, msdfGlyph.planeLeft) == 0
            && Float.compare(this.planeTop, msdfGlyph.planeTop) == 0
            && Float.compare(this.planeRight, msdfGlyph.planeRight) == 0
            && Float.compare(this.planeBottom, msdfGlyph.planeBottom) == 0
            && Float.compare(this.u0, msdfGlyph.u0) == 0
            && Float.compare(this.v0, msdfGlyph.v0) == 0
            && Float.compare(this.u1, msdfGlyph.u1) == 0
            && Float.compare(this.v1, msdfGlyph.v1) == 0
            && Float.compare(this.advance, msdfGlyph.advance) == 0;
      }
   }

   @Override
   public String toString() {
      float f = this.advance;
      float f2 = this.v1;
      float f3 = this.u1;
      float f4 = this.v0;
      float f5 = this.u0;
      float f6 = this.planeBottom;
      float f7 = this.planeRight;
      float f8 = this.planeTop;
      float f9 = this.planeLeft;
      int n = this.codepoint;
      return "MSDFGlyph[codepoint="
         + n
         + ", planeLeft="
         + f9
         + ", planeTop="
         + f8
         + ", planeRight="
         + f7
         + ", planeBottom="
         + f6
         + ", u0="
         + f5
         + ", v0="
         + f4
         + ", u1="
         + f3
         + ", v1="
         + f2
         + ", advance="
         + f
         + "]";
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.codepoint, this.planeLeft, this.planeTop, this.planeRight, this.planeBottom, this.u0, this.v0, this.u1, this.v1, this.advance);
   }

   public float getFloatType() {
      return this.v0;
   }

   public float getFloatType2() {
      return this.planeTop - this.planeBottom;
   }

   public float getFloatType3() {
      return this.planeRight;
   }

   public float getFloatType4() {
      return this.advance;
   }

   public float getPlaneLeft() {
      return this.planeLeft;
   }

   public float getFloatType5() {
      return this.u1;
   }

   public float getFloatType6() {
      return this.u0;
   }

   public int getIntType() {
      return this.codepoint;
   }

   public float getFloatType7() {
      return this.planeTop;
   }

   public float getFloatType8() {
      return this.planeRight - this.planeLeft;
   }

   public float getFloatType9() {
      return this.planeBottom;
   }

   public float getFloatType10() {
      return this.v1;
   }
}
