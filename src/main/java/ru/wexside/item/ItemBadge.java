package ru.wexside.item;

import java.util.Locale;
import net.minecraft.class_1799;
import net.minecraft.class_2561;

public record ItemBadge(class_2561 label, ItemBadgeCategory category) {
   public static ItemBadge fromStack(class_1799 stack) {
      if (stack != null && !stack.method_7960()) {
         class_2561 label = stack.method_7964();
         String normalizedName = label.getString().toLowerCase(Locale.ROOT);
         ItemBadgeCategory category;
         if (containsAny(normalizedName, "талисман", "talisman", "амулет", "amulet")) {
            category = ItemBadgeCategory.TALISMAN;
         } else {
            if (!containsAny(normalizedName, "сфера", "sphere", "шар", "orb")) {
               return null;
            }

            category = ItemBadgeCategory.SPHERE;
         }

         return new ItemBadge(label.method_27661(), category);
      } else {
         return null;
      }
   }

   private static boolean containsAny(String value, String... markers) {
      for(String marker : markers) {
         if (value.contains(marker)) {
            return true;
         }
      }

      return false;
   }
}
