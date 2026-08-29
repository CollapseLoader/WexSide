package ru.wexside.util;

import net.minecraft.class_1703;
import net.minecraft.class_2561;
import net.minecraft.class_437;

public final class ContainerScreenHelper {
   private ContainerScreenHelper() {
   }

   public static boolean isAuctionContainer(class_1703 handler, class_437 screen) {
      if (screen != null) {
         class_2561 title = screen.method_25440();
         if (title != null) {
            String lower = title.getString().toLowerCase();
            if (lower.contains("auction") || lower.contains("аукцион") || lower.contains("donmarket") || lower.contains("донмаркет")) {
               return true;
            }
         }
      }

      return handler != null && handler.field_7763 != 0 && handler.field_7761.size() >= 27;
   }

   public static boolean isPlayerInventoryContainer(class_1703 handler, class_437 screen) {
      return !isAuctionContainer(handler, screen);
   }
}
