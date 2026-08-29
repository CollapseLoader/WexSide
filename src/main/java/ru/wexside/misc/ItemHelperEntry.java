package ru.wexside.misc;

import java.util.Objects;
import net.minecraft.class_1792;

public final class ItemHelperEntry {
   private final boolean enabled;
   private final String string4;
   private final class_1792 item2;
   private final String string5;

   public ItemHelperEntry(String string, String string2, class_1792 iiIilIIilI2, boolean bl) {
      this.string5 = string;
      this.string4 = string2;
      this.item2 = iiIilIIilI2;
      this.enabled = bl;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof ItemHelperEntry)) {
         return false;
      } else {
         ItemHelperEntry itemHelperEntry = (ItemHelperEntry)object;
         return Objects.equals(this.string5, itemHelperEntry.string5)
            && Objects.equals(this.string4, itemHelperEntry.string4)
            && Objects.equals(this.item2, itemHelperEntry.item2)
            && this.enabled == itemHelperEntry.enabled;
      }
   }

   @Override
   public String toString() {
      boolean bl = this.enabled;
      String string = String.valueOf(this.item2);
      String string2 = this.string4;
      String string3 = this.string5;
      return "ItemHelperEntry[id=" + string3 + ", displayName=" + string2 + ", item=" + string + ", instantHealingPotion=" + bl + "]";
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.string5, this.string4, this.item2, this.enabled);
   }

   public boolean isActive() {
      return this.enabled;
   }

   public String getString() {
      return this.string4;
   }

   public String getName() {
      return this.string4;
   }

   public class_1792 getItem() {
      return this.item2;
   }

   public boolean isInstantHealingPotion() {
      return this.enabled;
   }

   public String getString2() {
      return this.string5;
   }
}
