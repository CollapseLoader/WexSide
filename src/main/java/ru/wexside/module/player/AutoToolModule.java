package ru.wexside.module.player;

import java.util.List;
import java.util.Optional;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1887;
import net.minecraft.class_1893;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_5321;
import net.minecraft.class_638;
import net.minecraft.class_6880;
import net.minecraft.class_746;
import net.minecraft.class_9276;
import net.minecraft.class_9304;
import net.minecraft.class_9334;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.ActionSequence;
import ru.wexside.misc.BundleUse;
import ru.wexside.misc.Bundles;
import ru.wexside.misc.ClickPolicy;
import ru.wexside.misc.ClickSlotAction;
import ru.wexside.misc.InventoryAction;
import ru.wexside.misc.InventoryTask;
import ru.wexside.misc.RunnableAction;
import ru.wexside.misc.TaskFlag;
import ru.wexside.misc.TaskPriority;
import ru.wexside.misc.TimedAction;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.util.InventoryController;

public class AutoToolModule extends Module implements ConfigSerializable {
   private static final String OWNER = "auto_tool";
   private final BooleanSetting enabledSetting;
   private final BooleanSetting fromInventory;
   private final BooleanSetting swapBack;
   private final BooleanSetting fromBundle;
   private final BooleanSetting ftMode;
   private int rememberedHotbarSlot = -1;
   private int inventorySourceSlot = -1;
   private int inventoryHotbarSlot = -1;
   private int hotbarSwitchSlot = -1;
   private boolean inventorySwapActive;
   private boolean hotbarSwitchActive;
   private boolean toolActive;
   private BundleUse bundleUse;

   public AutoToolModule(EventBus eventBus) {
      super(eventBus, "auto_tool", "Auto Tool", "Автосмена на лучший инструмент", ModuleCategory.valueOf("PLAYER"));
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      this.fromInventory = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("From Inventory")
            .id("from_inventory")
            .description("Брать из инвентаря"))
         .build();
      this.registerSetting(this.fromInventory);
      this.swapBack = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Swap Back")
            .id("swap_back")
            .description("Возвращать предыдущий предмет"))
         .build();
      this.registerSetting(this.swapBack);
      this.fromBundle = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Из мешков")
            .id("from_bundle")
            .description("Доставать инструмент из мешка если нет в инвентаре"))
         .build();
      this.registerSetting(this.fromBundle);
      this.ftMode = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("FT-Mode")
            .id("ft_mode")
            .description("Поддержка мешков без лимита вместимости")
            .visibleWhen(this.fromBundle::isEnabled))
         .build();
      this.registerSetting(this.ftMode);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.onTick());
   }

   private void onTick() {
      if (!this.enabledSetting.isEnabled()) {
         this.reset();
      } else {
         class_746 player = class_310.method_1551().field_1724;
         class_638 world = class_310.method_1551().field_1687;
         if (player != null && world != null && !player.method_7325()) {
            class_2338 pos = this.targetedBlock(world);
            boolean mining = pos != null && class_310.method_1551().field_1690.field_1886.method_1434();
            if (!mining) {
               this.onStopMining();
            } else {
               class_2680 state = world.method_8320(pos);
               int bestSlot = this.findBestSlot(state, player, this.fromInventory.isEnabled() ? 36 : 9);
               if (this.fromBundle.isEnabled() && this.tryUseBundle(player, state, bestSlot)) {
                  this.toolActive = true;
               } else if (this.bundleUse != null) {
                  if (this.inventoryBetterThanBundle(player, state, bestSlot)) {
                     this.restoreBundle();
                  }

                  this.toolActive = true;
               } else if (bestSlot == -1) {
                  this.toolActive = true;
               } else if (bestSlot <= 8) {
                  this.switchHotbar(player, bestSlot);
                  this.toolActive = true;
               } else {
                  this.switchFromInventory(player, bestSlot);
                  this.toolActive = true;
               }
            }
         } else {
            this.reset();
         }
      }
   }

   private boolean inventoryBetterThanBundle(class_746 player, class_2680 state, int slot) {
      if (slot != -1 && this.bundleUse != null) {
         class_1661 inventory = player.method_31548();
         return this.miningSpeed(inventory.method_5438(slot), state) > this.miningSpeed(inventory.method_5438(this.bundleUse.slot()), state);
      } else {
         return false;
      }
   }

   private boolean restoreBundle() {
      if (this.bundleUse == null) {
         return true;
      } else {
         this.bundleUse = null;
         return true;
      }
   }

   private void restorePrevious() {
      if (this.inventorySwapActive) {
         this.restoreInventorySwap();
      } else if (this.hotbarSwitchActive) {
         this.restoreHotbarSwitch();
      } else {
         this.clearSwapState();
      }
   }

   private void restoreInventorySwap() {
      if (this.inventorySourceSlot >= 9
         && this.inventoryHotbarSlot >= 0
         && this.inventoryHotbarSlot <= 8
         && this.rememberedHotbarSlot >= 0
         && this.rememberedHotbarSlot <= 8) {
         InventoryController inventory = WexSideClient.getInventoryController();
         if (inventory == null) {
            this.clearSwapState();
         } else {
            int sourceSlot = this.inventorySourceSlot;
            int hotbarSlot = this.inventoryHotbarSlot;
            int remembered = this.rememberedHotbarSlot;
            inventory.submit(InventoryTask.builder().action(this.swapSequence(this.toContainerSlot(sourceSlot), hotbarSlot, () -> {
               class_746 player = class_310.method_1551().field_1724;
               if (player != null) {
                  this.selectHotbar(player, remembered);
               }
            })).owner("auto_tool").flag(TaskFlag.REPLACE).policy(ClickPolicy.VISIBLE).priority(TaskPriority.HIGH).build());
            this.clearSwapState();
         }
      } else {
         this.clearSwapState();
      }
   }

   private void restoreHotbarSwitch() {
      if (this.rememberedHotbarSlot >= 0 && this.rememberedHotbarSlot <= 8) {
         class_746 player = class_310.method_1551().field_1724;
         if (player != null) {
            this.selectHotbar(player, this.rememberedHotbarSlot);
         }

         this.clearSwapState();
      } else {
         this.clearSwapState();
      }
   }

   private void selectHotbar(class_746 player, int slot) {
      if (slot >= 0 && slot <= 8) {
         class_1661 inventory = player.method_31548();
         if (inventory.method_67532() != slot) {
            inventory.method_61496(slot);
         }
      }
   }

   private float miningSpeed(class_1799 stack, class_2680 state) {
      if (stack.method_7960()) {
         return 1.0F;
      } else {
         float speed = stack.method_7924(state);
         if (speed <= 1.0F) {
            return 1.0F;
         } else {
            int efficiency = this.efficiencyLevel(stack);
            if (efficiency > 0) {
               speed += (float)(efficiency * efficiency + 1);
            }

            return this.isCorrectTool(stack, state) ? speed : speed * 0.3F;
         }
      }
   }

   private class_2338 targetedBlock(class_638 world) {
      class_239 hit = class_310.method_1551().field_1765;
      if (hit instanceof class_3965 blockHit) {
         class_2338 pos = blockHit.method_17777();
         return world.method_8320(pos).method_26215() ? null : pos;
      } else {
         return null;
      }
   }

   private int findBestSlot(class_2680 state, class_746 player, int limit) {
      if (state.method_26215()) {
         return -1;
      } else {
         int bestSlot = -1;
         float bestSpeed = 1.0F;
         class_1661 inventory = player.method_31548();

         for(int slot = 0; slot < limit; ++slot) {
            class_1799 stack = inventory.method_5438(slot);
            if (!stack.method_7960()) {
               float speed = this.miningSpeed(stack, state);
               if (speed > bestSpeed) {
                  bestSpeed = speed;
                  bestSlot = slot;
               }
            }
         }

         return bestSlot;
      }
   }

   private int efficiencyLevel(class_1799 stack) {
      class_9304 enchants = (class_9304)stack.method_58695(class_9334.field_49633, class_9304.field_49385);

      for(class_6880<class_1887> enchantment : enchants.method_57534()) {
         Optional<class_5321<class_1887>> key = enchantment.method_40230();
         if (key.isPresent() && ((class_5321)key.get()).equals(class_1893.field_9131)) {
            return enchants.method_57536(enchantment);
         }
      }

      return 0;
   }

   private int toContainerSlot(int slot) {
      return slot < 9 ? slot + 36 : slot;
   }

   private boolean tryUseBundle(class_746 player, class_2680 state, int bestSlot) {
      if (!state.method_26215() && this.bundleUse == null) {
         InventoryController inventory = WexSideClient.getInventoryController();
         if (inventory != null && !inventory.isActive()) {
            class_1661 playerInventory = player.method_31548();
            float current = bestSlot == -1 ? 1.0F : this.miningSpeed(playerInventory.method_5438(bestSlot), state);
            int[] found = this.findInBundles(playerInventory, state, current);
            if (found == null) {
               return false;
            } else {
               this.bundleUse = Bundles.useFromBundle(player, inventory, "auto_tool", found[0], found[1], this.ftMode.isEnabled());
               return this.bundleUse != null;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void onStopMining() {
      if (this.toolActive) {
         if (this.swapBack.isEnabled()) {
            this.restorePrevious();
         } else {
            this.clearSwapState();
         }

         if (this.restoreBundle()) {
            this.toolActive = false;
         }
      }
   }

   private void switchHotbar(class_746 player, int slot) {
      if (this.inventorySwapActive) {
         if (slot == this.inventoryHotbarSlot) {
            this.selectHotbar(player, slot);
            return;
         }

         if (this.swapBack.isEnabled()) {
            this.restorePrevious();
         } else {
            this.clearInventorySwap();
         }
      }

      if (this.hotbarSwitchActive && this.hotbarSwitchSlot == slot) {
         this.selectHotbar(player, slot);
      } else {
         if (this.shouldRememberSlot()) {
            this.rememberedHotbarSlot = player.method_31548().method_67532();
         }

         this.hotbarSwitchActive = true;
         this.hotbarSwitchSlot = slot;
         this.selectHotbar(player, slot);
      }
   }

   private void switchFromInventory(class_746 player, int slot) {
      if (slot >= 9 && slot < 36) {
         if (!this.inventorySwapActive || this.inventorySourceSlot != slot) {
            if (this.hotbarSwitchActive) {
               if (this.swapBack.isEnabled()) {
                  this.restorePrevious();
               } else {
                  this.clearHotbarSwitch();
               }
            }

            if (this.inventorySwapActive) {
               if (this.swapBack.isEnabled()) {
                  this.restorePrevious();
               } else {
                  this.clearInventorySwap();
               }
            }

            InventoryController inventory = WexSideClient.getInventoryController();
            if (inventory != null && !inventory.process("auto_tool")) {
               if (this.shouldRememberSlot()) {
                  this.rememberedHotbarSlot = player.method_31548().method_67532();
               }

               this.inventoryHotbarSlot = this.pickHotbarDestination(player.method_31548());
               this.inventorySourceSlot = slot;
               this.inventorySwapActive = true;
               int hotbarSlot = this.inventoryHotbarSlot;
               inventory.submit(InventoryTask.builder().action(this.swapSequence(this.toContainerSlot(slot), hotbarSlot, () -> {
                  class_746 current = class_310.method_1551().field_1724;
                  if (current != null) {
                     this.selectHotbar(current, hotbarSlot);
                  }
               })).owner("auto_tool").flag(TaskFlag.REPLACE).policy(ClickPolicy.VISIBLE).priority(TaskPriority.NORMAL).build());
            }
         }
      }
   }

   private InventoryAction swapSequence(int containerSlot, int hotbarSlot, Runnable after) {
      return new ActionSequence(List.of(new TimedAction(0, new ClickSlotAction(containerSlot, hotbarSlot)), new TimedAction(0, new RunnableAction(after))));
   }

   private int[] findInBundles(class_1661 inventory, class_2680 state, float minSpeed) {
      int[] best = null;
      float bestSpeed = minSpeed;

      for(int slot = 0; slot < 36; ++slot) {
         class_9276 contents = (class_9276)inventory.method_5438(slot).method_58694(class_9334.field_49650);
         if (contents != null) {
            for(int nested = 0; nested < contents.method_57426(); ++nested) {
               class_1799 stack = contents.method_57422(nested);
               if (!stack.method_7960()) {
                  float speed = this.miningSpeed(stack, state);
                  if (speed > bestSpeed) {
                     bestSpeed = speed;
                     best = new int[]{slot, nested};
                  }
               }
            }
         }
      }

      return best;
   }

   private void clearHotbarSwitch() {
      this.hotbarSwitchActive = false;
      this.hotbarSwitchSlot = -1;
   }

   private void clearInventorySwap() {
      this.inventorySwapActive = false;
      this.inventorySourceSlot = -1;
      this.inventoryHotbarSlot = -1;
   }

   private void clearSwapState() {
      this.clearHotbarSwitch();
      this.clearInventorySwap();
      this.rememberedHotbarSlot = -1;
   }

   private void reset() {
      this.restoreBundle();
      this.bundleUse = null;
      this.hotbarSwitchActive = false;
      this.inventorySwapActive = false;
      this.hotbarSwitchSlot = -1;
      this.inventorySourceSlot = -1;
      this.rememberedHotbarSlot = -1;
      this.inventoryHotbarSlot = -1;
      this.toolActive = false;
   }

   private boolean shouldRememberSlot() {
      return !this.hotbarSwitchActive && !this.inventorySwapActive && this.rememberedHotbarSlot == -1;
   }

   private boolean isCorrectTool(class_1799 stack, class_2680 state) {
      return !state.method_29291() || stack.method_7951(state);
   }

   private int pickHotbarDestination(class_1661 inventory) {
      int selected = inventory.method_67532();
      int fallback = -1;

      for(int i = 0; i < 9; ++i) {
         int slot = (selected + i) % 9;
         class_1799 stack = inventory.method_5438(slot);
         if (stack.method_7960()) {
            return slot;
         }

         if (!this.isProtected(stack) && fallback == -1) {
            fallback = slot;
         }
      }

      return fallback == -1 ? selected : fallback;
   }

   private boolean isProtected(class_1799 stack) {
      return stack.method_7958() || stack.method_31574(class_1802.field_8288) || stack.method_57826(class_9334.field_50075);
   }
}
