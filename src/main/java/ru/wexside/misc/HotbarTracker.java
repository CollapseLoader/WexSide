package ru.wexside.misc;

import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_746;

public final class HotbarTracker {
   public class_1799 simulate(int ticksAhead) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null && ticksAhead > 0 && player.method_6115()) {
         class_1799 activeItem = player.method_6030();
         return activeItem.method_7960() ? null : activeItem.method_7972();
      } else {
         return null;
      }
   }

   public class_1799 process(int ticksAhead) {
      return this.simulate(ticksAhead);
   }
}
