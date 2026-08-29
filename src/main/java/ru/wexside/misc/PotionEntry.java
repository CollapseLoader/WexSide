package ru.wexside.misc;

import java.util.Objects;
import net.minecraft.class_1291;
import net.minecraft.class_6880;

public final class PotionEntry {
   private final class_6880<class_1291> effect;
   private final String name;

   public PotionEntry(String name, class_6880<class_1291> effect) {
      this.name = name;
      this.effect = effect;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof PotionEntry)) {
         return false;
      } else {
         PotionEntry other = (PotionEntry)object;
         return Objects.equals(this.name, other.name) && Objects.equals(this.effect, other.effect);
      }
   }

   @Override
   public String toString() {
      return "PotionEntry[label=" + this.name + ", effect=" + this.effect + "]";
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.name, this.effect);
   }

   public String getString() {
      return this.name;
   }

   public String getName() {
      return this.name;
   }

   public class_6880<class_1291> getEffect() {
      return this.effect;
   }
}
