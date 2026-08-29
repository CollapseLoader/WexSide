package ru.wexside.misc;

import net.minecraft.class_1661;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.module.combat.MaceHelperModule;
import ru.wexside.server.ServerHelperAction;
import ru.wexside.server.ServerHelperActions;
import ru.wexside.server.ServerItemMatcher;
import ru.wexside.util.InventoryController;

public class ServerHelper {
   private final String field12 = "server_helper";

   public ItemUseResult process(
      ServerHelperAction action, ServerKind serverKind2, SwapTiming timing, Runnable runnable, Runnable runnable2, boolean bl, boolean bl2
   ) {
      class_746 player2 = class_310.method_1551().field_1724;
      if (player2 == null) {
         return ItemUseResult.UNAVAILABLE;
      } else {
         InventoryController inventory = WexSideClient.getInventoryController();
         if (inventory == null) {
            return ItemUseResult.UNAVAILABLE;
         } else if (ServerItemMatcher.matches(player2.method_6079(), action, serverKind2)) {
            if (runnable != null) {
               runnable.run();
            }

            inventory.submit(
               InventoryTask.builder()
                  .action(new UseItemAction())
                  .owner("server_helper")
                  .flag(TaskFlag.DEFAULT)
                  .policy(ClickPolicy.SILENT)
                  .priority(TaskPriority.NORMAL)
                  .build()
            );
            if (runnable2 != null) {
               runnable2.run();
            }

            this.afterUse(action);
            return ItemUseResult.USED_OFFHAND;
         } else {
            class_1661 inv = player2.method_31548();
            int n = ServerItemMatcher.findSlot(inv, action, serverKind2, false);
            if (n == -1) {
               int[] nArray;
               if (bl && (nArray = Bundles.findInBundle(inv, stack -> ServerItemMatcher.matches(stack, action, serverKind2))) != null) {
                  Runnable runnable3 = () -> {
                     if (runnable != null) {
                        runnable.run();
                     }

                     inventory.update3();
                     if (runnable2 != null) {
                        runnable2.run();
                     }
                  };
                  Bundles.useFromBundle(player2, inventory, "server_helper", nArray[0], nArray[1], bl2, runnable3);
                  this.afterUse(action);
                  return ItemUseResult.QUEUED_INVENTORY_SWAP;
               } else {
                  return ItemUseResult.UNAVAILABLE;
               }
            } else if (n < 9) {
               if (runnable != null) {
                  runnable.run();
               }

               inventory.submit(
                  InventoryTask.builder()
                     .action(new HotbarSelectAction(n, true))
                     .owner("server_helper")
                     .flag(TaskFlag.DEFAULT)
                     .policy(ClickPolicy.SILENT)
                     .priority(TaskPriority.NORMAL)
                     .build()
               );
               if (runnable2 != null) {
                  runnable2.run();
               }

               this.afterUse(action);
               return ItemUseResult.SELECTED_HOTBAR;
            } else {
               int n2 = inv.method_67532();
               Runnable runnable6 = () -> {
                  if (runnable != null) {
                     runnable.run();
                  }

                  inventory.update3();
                  if (runnable2 != null) {
                     runnable2.run();
                  }
               };
               int n3 = n;
               Runnable runnable7 = null;
               if (action == ServerHelperActions.WIND_CHARGE && MaceHelperModule.isEnabled2()) {
                  if (MaceHelperModule.isEnabled5()) {
                     MaceHelperModule.refreshSession();
                  } else if (!MaceHelperModule.isEnabled6()) {
                     if (!MaceHelperModule.isEnabled3() && !MaceHelperModule.isEnabled()) {
                        n3 = MaceHelperModule.findMaceContainerSlot();
                        MaceHelperModule.startSwapSession(n3, n2);
                     } else {
                        MaceHelperModule.startSession();
                     }
                  }

                  if (MaceHelperModule.isEnabled4()) {
                     runnable7 = MaceHelperModule::selectMaceHotbar;
                  }
               }

               inventory.submit(
                  InventoryTask.builder()
                     .action(inventory.process3(n, n2, runnable6, timing, n3, runnable7))
                     .owner("server_helper")
                     .flag(TaskFlag.DEFAULT)
                     .policy(ClickPolicy.VISIBLE)
                     .priority(TaskPriority.NORMAL)
                     .blocking(true)
                     .build()
               );
               return ItemUseResult.QUEUED_INVENTORY_SWAP;
            }
         }
      }
   }

   public ItemUseResult process2(ServerHelperAction action, ServerKind serverKind2, SwapTiming timing, Runnable runnable, Runnable runnable2) {
      return this.process(action, serverKind2, timing, runnable, runnable2, false, false);
   }

   private void afterUse(ServerHelperAction action) {
      if (action == ServerHelperActions.WIND_CHARGE) {
         if (MaceHelperModule.isEnabled5()) {
            MaceHelperModule.refreshSession();
         } else if (!MaceHelperModule.isEnabled6()) {
            MaceHelperModule.startSession();
         }
      }
   }
}
