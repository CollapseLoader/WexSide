package ru.wexside.misc;

import net.minecraft.class_10192;
import net.minecraft.class_1304;
import net.minecraft.class_1661;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_310;
import net.minecraft.class_3489;
import net.minecraft.class_746;
import net.minecraft.class_9334;

public final class Inventories {
   private Inventories() {
   }

   public static int findSlot(class_1792 item) {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return -1;
      } else {
         class_1661 inventory = player.method_31548();

         for(int slot = 0; slot < 36; ++slot) {
            if (inventory.method_5438(slot).method_31574(item)) {
               return slot;
            }
         }

         return -1;
      }
   }

   public static int findAxeSlot() {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return -1;
      } else {
         class_1661 inventory = player.method_31548();

         for(int slot = 0; slot < 9; ++slot) {
            class_1799 stack = inventory.method_5438(slot);
            if (stack.method_31573(class_3489.field_42612) || stack.method_31574(class_1802.field_49814)) {
               return slot;
            }
         }

         return -1;
      }
   }

   public static int findArmorSlot(class_1304 slot) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null && slot != null) {
         class_1661 inventory = player.method_31548();

         for(int index = 0; index < inventory.method_5439(); ++index) {
            class_1799 stack = inventory.method_5438(index);
            class_10192 equippable;
            if (!stack.method_7960()
               && player.method_6118(slot) != stack
               && (equippable = (class_10192)stack.method_58694(class_9334.field_54196)) != null
               && equippable.comp_3174() == slot) {
               return index;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static int findHotbarSlot(class_1792 item) {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return -1;
      } else {
         class_1661 inventory = player.method_31548();

         for(int slot = 0; slot < 9; ++slot) {
            if (inventory.method_5438(slot).method_31574(item)) {
               return slot;
            }
         }

         return -1;
      }
   }

   public static int findInventorySlot(class_1792 item) {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return -1;
      } else {
         class_1661 inventory = player.method_31548();

         for(int slot = 9; slot < 36; ++slot) {
            if (inventory.method_5438(slot).method_31574(item)) {
               return slot;
            }
         }

         return -1;
      }
   }

   public static int toContainerSlot(int slot) {
      return slot < 9 ? slot + 36 : slot;
   }

   public static boolean isFood(class_1799 stack) {
      return stack != null && !stack.method_7960() && stack.method_58694(class_9334.field_50075) != null;
   }

   public static int findFoodHotbar() {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return -1;
      } else {
         class_1661 inventory = player.method_31548();

         for(int slot = 0; slot < 9; ++slot) {
            if (isFood(inventory.method_5438(slot))) {
               return slot;
            }
         }

         return -1;
      }
   }

   public static int findFoodInventory() {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return -1;
      } else {
         class_1661 inventory = player.method_31548();

         for(int slot = 9; slot < 36; ++slot) {
            if (isFood(inventory.method_5438(slot))) {
               return slot;
            }
         }

         return -1;
      }
   }
}
