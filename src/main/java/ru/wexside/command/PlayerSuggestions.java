package ru.wexside.command;

import java.util.List;
import net.minecraft.class_310;
import net.minecraft.class_634;

final class PlayerSuggestions {
   private PlayerSuggestions() {
   }

   static List<String> onlinePlayerNames() {
      class_634 networkHandler = class_310.method_1551().method_1562();
      return networkHandler == null
         ? List.of()
         : networkHandler.method_2880().stream().map(entry -> entry.method_2966().name()).sorted(String.CASE_INSENSITIVE_ORDER).toList();
   }
}
