package ru.wexside.misc;

import java.util.Objects;

public final class MsdfAtlas {
   private final float range;
   private final float width;
   private final float height;

   public MsdfAtlas(float f, float f2, float f3) {
      this.range = f;
      this.width = f2;
      this.height = f3;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof MsdfAtlas)) {
         return false;
      } else {
         MsdfAtlas msdfAtlas = (MsdfAtlas)object;
         return Float.compare(this.range, msdfAtlas.range) == 0
            && Float.compare(this.width, msdfAtlas.width) == 0
            && Float.compare(this.height, msdfAtlas.height) == 0;
      }
   }

   @Override
   public String toString() {
      float f = this.height;
      float f2 = this.width;
      float f3 = this.range;
      return "Atlas[range=" + f3 + ", width=" + f2 + ", height=" + f + "]";
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.range, this.width, this.height);
   }

   public float getFloatType() {
      return this.height;
   }

   public float getFloatType2() {
      return this.width;
   }

   public float getFloatType3() {
      return this.range;
   }
}
