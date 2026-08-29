package ru.wexside.misc;

import java.util.List;
import java.util.Objects;
import ru.wexside.ui.GuiElement;

public final class LayoutGroup {
   private final List<GuiElement> elements;
   private final boolean enabled;
   private final float value;
   private final boolean enabled2;

   public LayoutGroup(List<GuiElement> list, float f, boolean bl, boolean bl2) {
      this.elements = list;
      this.value = f;
      this.enabled2 = bl;
      this.enabled = bl2;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof LayoutGroup)) {
         return false;
      } else {
         LayoutGroup layoutGroup2 = (LayoutGroup)object;
         return Objects.equals(this.elements, layoutGroup2.elements)
            && Float.compare(this.value, layoutGroup2.value) == 0
            && this.enabled2 == layoutGroup2.enabled2
            && this.enabled == layoutGroup2.enabled;
      }
   }

   @Override
   public String toString() {
      boolean bl = this.enabled;
      boolean bl2 = this.enabled2;
      float f = this.value;
      String string = String.valueOf(this.elements);
      return "LayoutGroup[children=" + string + ", animation=" + f + ", targetVisible=" + bl2 + ", sharedVisibility=" + bl + "]";
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.elements, this.value, this.enabled2, this.enabled);
   }

   public float getFloatType() {
      return this.value;
   }

   public List<GuiElement> getList() {
      return this.elements;
   }

   public boolean isActive() {
      return this.enabled2;
   }

   public boolean isActive2() {
      return this.enabled;
   }
}
