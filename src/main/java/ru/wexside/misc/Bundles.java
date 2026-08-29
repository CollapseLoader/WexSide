package ru.wexside.misc;

import java.util.function.Predicate;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_746;
import ru.wexside.util.InventoryController;

public final class Bundles {
   private Bundles() {
   }

   public static boolean contains(class_1661 inventory, Predicate<class_1799> predicate) {
      if (inventory == null) {
         return false;
      } else {
         for(int slot = 0; slot < inventory.method_5439(); ++slot) {
            if (predicate.test(inventory.method_5438(slot))) {
               return true;
            }
         }

         return false;
      }
   }

   public static int[] findInBundle(class_1661 inventory, Predicate<class_1799> predicate) {
      if (inventory == null) {
         return null;
      } else {
         for(int slot = 0; slot < inventory.method_5439(); ++slot) {
            if (predicate.test(inventory.method_5438(slot))) {
               return new int[]{slot, 0};
            }
         }

         return null;
      }
   }

   public static void useFromBundle(class_746 player, InventoryController inventory, String owner, int slot, int nestedSlot, boolean funtime, Runnable use) {
      if (inventory != null && player != null) {
         inventory.submit(
            InventoryTask.builder()
               .action(
                  inventory.process2(slot < 9 ? slot + 36 : slot, player.method_31548().method_67532(), use, funtime ? SwapTiming.FUNTIME : SwapTiming.DEFAULT)
               )
               .owner(owner)
               .flag(TaskFlag.DEFAULT)
               .policy(ClickPolicy.SILENT)
               .priority(TaskPriority.NORMAL)
               .build()
         );
      }
   }

   public static boolean useFromBundle(
      class_746 player, InventoryController inventory, String owner, int slot, int nestedSlot, int destinationSlot, boolean funtime
   ) {
      useFromBundle(player, inventory, owner, slot, nestedSlot, funtime, inventory::update3);
      return true;
   }

   public static BundleUse useFromBundle(class_746 player, InventoryController inventory, String owner, int slot, int nestedSlot, boolean funtime) {
      useFromBundle(player, inventory, owner, slot, nestedSlot, funtime, inventory::update3);
      return new BundleUse(slot);
   }
}
