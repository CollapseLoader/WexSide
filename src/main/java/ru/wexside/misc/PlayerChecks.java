package ru.wexside.misc;

import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_310;
import net.minecraft.class_3489;

public final class PlayerChecks {
   private PlayerChecks() {
   }

   public static boolean isHoldingWeapon() {
      class_1657 player = class_310.method_1551().field_1724;
      if (player == null) {
         return false;
      } else {
         class_1799 stack = player.method_6047();
         return stack.method_31573(class_3489.field_42611)
            || stack.method_31573(class_3489.field_42612)
            || stack.method_31574(class_1802.field_49814)
            || stack.method_31574(class_1802.field_8547);
      }
   }

   public static boolean isUsingItem() {
      class_1657 player = class_310.method_1551().field_1724;
      return player != null && player.method_6115();
   }
}
