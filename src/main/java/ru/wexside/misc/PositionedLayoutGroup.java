package ru.wexside.misc;

import java.util.Objects;

public final class PositionedLayoutGroup {
   private final LayoutGroup layoutGroup2;
   private final float value;
   private final boolean enabled;
   private final float value2;
   private final float y;

   public PositionedLayoutGroup(LayoutGroup layoutGroup2, float f, float f2, float f3, boolean bl) {
      this.layoutGroup2 = layoutGroup2;
      this.value = f;
      this.y = f2;
      this.value2 = f3;
      this.enabled = bl;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof PositionedLayoutGroup)) {
         return false;
      } else {
         PositionedLayoutGroup positionedLayoutGroup = (PositionedLayoutGroup)object;
         return Objects.equals(this.layoutGroup2, positionedLayoutGroup.layoutGroup2)
            && Float.compare(this.value, positionedLayoutGroup.value) == 0
            && Float.compare(this.y, positionedLayoutGroup.y) == 0
            && Float.compare(this.value2, positionedLayoutGroup.value2) == 0
            && this.enabled == positionedLayoutGroup.enabled;
      }
   }

   @Override
   public String toString() {
      boolean bl = this.enabled;
      float f = this.value2;
      float f2 = this.y;
      float f3 = this.value;
      String string = String.valueOf(this.layoutGroup2);
      return "PositionedLayoutGroup[layoutGroup=" + string + ", x=" + f3 + ", y=" + f2 + ", width=" + f + ", hasLeadingGap=" + bl + "]";
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.layoutGroup2, this.value, this.y, this.value2, this.enabled);
   }

   public LayoutGroup getLayoutGroup() {
      return this.layoutGroup2;
   }

   public boolean isActive() {
      return this.enabled;
   }

   public float getFloatType() {
      return this.value;
   }

   public float getFloatType2() {
      return this.y;
   }

   public float getFloatType3() {
      return this.value2;
   }
}
