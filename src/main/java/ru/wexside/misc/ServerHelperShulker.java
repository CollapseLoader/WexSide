package ru.wexside.misc;

import net.minecraft.class_1661;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2480;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.util.InventoryController;

public class ServerHelperShulker {
   private final String field12 = "server_helper_shulker";

   public void update() {
      class_746 player2 = class_310.method_1551().field_1724;
      if (player2 != null) {
         InventoryController inventory = WexSideClient.getInventoryController();
         if (inventory != null) {
            if (this.isActive()) {
               ClientChat.send("Во время открытия шалкера нельзя двигаться.");
            } else {
               class_1661 inv = player2.method_31548();
               int n = this.process(inv);
               if (n == -1) {
                  ClientChat.send("В Вашем инвентаре нет шалкера.");
               } else {
                  int n2 = n < 9 ? n + 36 : n;
                  inventory.submit(
                     InventoryTask.builder()
                        .action(new PickupSlotAction(n2, 1))
                        .owner("server_helper_shulker")
                        .flag(TaskFlag.REPLACE)
                        .policy(ClickPolicy.SILENT)
                        .priority(TaskPriority.NORMAL)
                        .build()
                  );
               }
            }
         }
      }
   }

   private boolean isActive() {
      class_315 options = class_310.method_1551().field_1690;
      if (options == null) {
         return false;
      } else {
         return options.field_1894.method_1434()
            || options.field_1881.method_1434()
            || options.field_1913.method_1434()
            || options.field_1849.method_1434()
            || options.field_1903.method_1434();
      }
   }

   private int process(class_1661 inv) {
      for(int i = 0; i < 36; ++i) {
         class_1799 stack = inv.method_5438(i);
         class_1747 blockItem2;
         class_1792 iiIilIIilI2;
         if (!stack.method_7960()
            && (iiIilIIilI2 = stack.method_7909()) instanceof class_1747
            && (blockItem2 = (class_1747)iiIilIIilI2).method_7711() instanceof class_2480) {
            return i;
         }
      }

      return -1;
   }
}
