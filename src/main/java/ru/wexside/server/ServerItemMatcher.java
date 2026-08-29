package ru.wexside.server;

import java.util.Locale;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import ru.wexside.misc.ServerKind;

public final class ServerItemMatcher {
   private ServerItemMatcher() {
   }

   public static boolean matches(class_1799 stack, ServerHelperAction action, ServerKind serverKind) {
      if (stack == null || stack.method_7960() || action == null) {
         return false;
      } else if (action.matchByItem()) {
         return stack.method_31574(action.icon());
      } else {
         String name = normalize(stack.method_7964());
         if (serverKind == ServerKind.OTHERS) {
            return action.alternateServerTags().stream().map(ServerItemMatcher::normalize).anyMatch(tag -> !tag.isEmpty() && name.contains(tag));
         } else {
            String generalTag = normalize(action.generalServerTag());
            return !generalTag.isEmpty() && name.contains(generalTag);
         }
      }
   }

   public static class_1799 findStack(class_1661 inventory, ServerHelperAction action, ServerKind serverKind) {
      int slot = findSlot(inventory, action, serverKind, false);
      return slot < 0 ? class_1799.field_8037 : inventory.method_5438(slot);
   }

   public static int findSlot(class_1661 inventory, ServerHelperAction action, ServerKind serverKind, boolean hotbarOnly) {
      if (inventory != null && action != null) {
         int limit = hotbarOnly ? Math.min(9, inventory.method_5439()) : inventory.method_5439();

         for(int slot = 0; slot < limit; ++slot) {
            if (matches(inventory.method_5438(slot), action, serverKind)) {
               return slot;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private static String normalize(class_2561 text) {
      return text == null ? "" : normalize(text.getString());
   }

   private static String normalize(String value) {
      return value == null ? "" : value.strip().toLowerCase(Locale.ROOT);
   }
}
