package ru.wexside.misc;

import net.minecraft.class_1294;
import net.minecraft.class_2338;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;

public final class CritCondition {
   public boolean process(boolean criticalsOnly, boolean jumpOnly, boolean waterCriticals) {
      if (!criticalsOnly) {
         return true;
      } else {
         class_746 player = class_310.method_1551().field_1724;
         if (player == null) {
            return false;
         } else if (this.isForcedCrit(player)) {
            return true;
         } else {
            boolean falling = !player.method_24828() && player.field_6017 > 0.0;
            if (player.method_5799()) {
               boolean waterOk = waterCriticals && !player.method_5681() && this.hasWaterAbove(player) && this.feetInAir(player);
               return waterOk ? falling : true;
            } else {
               if (jumpOnly) {
                  boolean jumping = class_310.method_1551().field_1690.field_1903.method_1434() || !player.method_24828();
                  if (!jumping) {
                     return false;
                  }
               }

               return falling;
            }
         }
      }
   }

   private boolean hasWaterAbove(class_746 player) {
      class_638 world = class_310.method_1551().field_1687;
      if (world == null) {
         return false;
      } else {
         return !world.method_8316(player.method_24515().method_10084()).method_15769();
      }
   }

   private boolean feetInAir(class_746 player) {
      class_638 world = class_310.method_1551().field_1687;
      if (world == null) {
         return false;
      } else {
         class_2338 pos = player.method_24515().method_10062();
         return world.method_8320(pos).method_26215();
      }
   }

   private boolean isForcedCrit(class_746 player) {
      return player.method_6101()
         || player.method_31549().field_7479
         || player.method_6059(class_1294.field_5919)
         || player.method_6059(class_1294.field_5906)
         || player.method_5869()
         || player.method_5765()
         || player.method_6128();
   }
}
