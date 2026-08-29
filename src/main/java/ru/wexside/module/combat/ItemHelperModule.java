package ru.wexside.module.combat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1844;
import net.minecraft.class_310;
import net.minecraft.class_746;
import net.minecraft.class_9276;
import net.minecraft.class_9334;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.BlockInteractEvent;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.misc.BundleUse;
import ru.wexside.misc.Bundles;
import ru.wexside.misc.ClickPolicy;
import ru.wexside.misc.ClickSlotAction;
import ru.wexside.misc.InventoryAction;
import ru.wexside.misc.InventoryTask;
import ru.wexside.misc.ItemAlerts;
import ru.wexside.misc.ItemBindBox;
import ru.wexside.misc.ItemHelperCatalog;
import ru.wexside.misc.ItemHelperEntry;
import ru.wexside.misc.TaskFlag;
import ru.wexside.misc.TaskPriority;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BindSetting;
import ru.wexside.setting.BindSettingBuilder;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.util.InventoryController;
import ru.wexside.util.ItemStatusHudElement;

public class ItemHelperModule extends Module implements ConfigSerializable {
   private static final String OWNER = "item_helper";
   private final BooleanSetting enabledSetting;
   private final ModeSetting swapMode;
   private final BooleanSetting fromBundle;
   private final BooleanSetting ftMode;
   private final BooleanSetting visualizer;
   private final BooleanSetting visualizerAll;
   private final MultiSelectSetting visualizerItems;
   private final BooleanSetting highlightEmpty;
   private ItemStatusHudElement visualizerHud;
   private ItemHelperEntry currentItem;
   private BundleUse bundleUse;
   private int itemSlot = -1;
   private int previousSlot = -1;
   private boolean swappedFromInventory;
   private boolean releasing;

   public ItemHelperModule(EventBus eventBus) {
      super(eventBus, "item_helper", "Item Helper", "Быстрое использование предметов по биндам", ModuleCategory.valueOf("COMBAT"), "itemhelper", "items");
      List<ItemHelperEntry> presets = ItemHelperCatalog.list;
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
      this.swapMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Default", "Legit")
            .defaultOption("Default")
            .name("Mode")
            .id("swap_mode")
            .description("Скорость свапа: Default - мгновенно, Legit - с задержкой"))
         .build();
      this.registerSetting(this.swapMode);
      this.fromBundle = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Из мешков")
            .id("from_bundle")
            .description("Доставать предмет из мешка если нет в инвентаре"))
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
      ItemHelperEntry chorus = presets.get(0);
      BindSetting chorusBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .onReleased(bind -> this.onPress(chorus, bind))
            .onPressed(bind -> this.onRelease(chorus, bind))
            .name("Плод хоруса")
            .id("bind_chorus")
            .description("Использовать пока зажато: Плод хоруса"))
         .build();
      this.registerSetting(chorusBind);
      ItemHelperEntry gapple = presets.get(1);
      BindSetting gappleBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .onReleased(bind -> this.onPress(gapple, bind))
            .onPressed(bind -> this.onRelease(gapple, bind))
            .name("Золотое яблоко")
            .id("bind_gapple")
            .description("Использовать пока зажато: Золотое яблоко"))
         .build();
      this.registerSetting(gappleBind);
      ItemHelperEntry enchantedGapple = presets.get(2);
      BindSetting enchantedGappleBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .onReleased(bind -> this.onPress(enchantedGapple, bind))
            .onPressed(bind -> this.onRelease(enchantedGapple, bind))
            .name("Зачарованное золотое яблоко")
            .id("bind_enchanted_gapple")
            .description("Использовать пока зажато: Зачарованное золотое яблоко"))
         .build();
      this.registerSetting(enchantedGappleBind);
      ItemHelperEntry instantHealing = presets.get(3);
      BindSetting instantHealingBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .onReleased(bind -> this.onPress(instantHealing, bind))
            .onPressed(bind -> this.onRelease(instantHealing, bind))
            .name("Зелье исцеления")
            .id("bind_instant_healing")
            .description("Использовать пока зажато: Зелье исцеления"))
         .build();
      this.registerSetting(instantHealingBind);
      ItemHelperEntry shield = presets.get(4);
      BindSetting shieldBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .onReleased(bind -> this.onPress(shield, bind))
            .onPressed(bind -> this.onRelease(shield, bind))
            .name("Щит")
            .id("bind_shield")
            .description("Использовать пока зажато: Щит"))
         .build();
      this.registerSetting(shieldBind);
      ItemHelperEntry crossbow = presets.get(5);
      BindSetting crossbowBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .onReleased(bind -> this.onPress(crossbow, bind))
            .onPressed(bind -> this.onRelease(crossbow, bind))
            .name("Арбалет")
            .id("bind_crossbow")
            .description("Использовать пока зажато: Арбалет"))
         .build();
      this.registerSetting(crossbowBind);
      ItemHelperEntry milk = presets.get(6);
      BindSetting milkBind = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .onReleased(bind -> this.onPress(milk, bind))
            .onPressed(bind -> this.onRelease(milk, bind))
            .name("Молоко")
            .id("bind_milk")
            .description("Использовать пока зажато: Молоко"))
         .build();
      this.registerSetting(milkBind);
      this.visualizer = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Visualizer")
            .id("visualizer")
            .description("Сетка предметов с биндами"))
         .build();
      this.registerSetting(this.visualizer);
      this.visualizerAll = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Все предметы")
            .id("visualizer_all")
            .description("Показывать все предметы в сетке")
            .visibleWhen(this.visualizer::isEnabled))
         .build();
      this.registerSetting(this.visualizerAll);
      MultiSelectSetting visualizerItemsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("Плод хоруса", "Золотое яблоко", "Зачарованное золотое яблоко", "Зелье исцеления", "Щит", "Арбалет", "Молоко")
            .selectAll(true)
            .optionListEnabled(false)
            .name("Предметы")
            .id("visualizer_items")
            .description("Предметы в сетке")
            .visibleWhen(() -> this.visualizer.isEnabled() && !this.visualizerAll.isEnabled()))
         .build();
      visualizerItemsSetting.setOptions(
         new String[]{"Плод хоруса", "Золотое яблоко", "Зачарованное золотое яблоко", "Зелье исцеления", "Щит", "Арбалет", "Молоко"}
      );
      this.visualizerItems = visualizerItemsSetting;
      this.registerSetting(visualizerItemsSetting);
      this.highlightEmpty = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Highlight Empty")
            .id("highlight_empty")
            .description("Подсветка отсутствующих предметов")
            .visibleWhen(this.visualizer::isEnabled))
         .build();
      this.registerSetting(this.highlightEmpty);
      List<ItemBindBox> boxes = new ArrayList<>();
      BindSetting[] binds = new BindSetting[]{chorusBind, gappleBind, enchantedGappleBind, instantHealingBind, shieldBind, crossbowBind, milkBind};

      for(int i = 0; i < presets.size(); ++i) {
         ItemHelperEntry preset = presets.get(i);
         boxes.add(new ItemBindBox(preset.getItem(), binds[i], stack -> matchesItem(preset, stack), () -> this.isVisualizerItemEnabled(preset)));
      }

      this.visualizerHud = new ItemStatusHudElement("Item Helper", this::isVisualizerEnabled, boxes, this.highlightEmpty::isEnabled);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.onTick());
      this.listen(HudRenderEvent.class, event -> {
         if (this.visualizerHud != null) {
            this.visualizerHud.renderFrame();
         }
      });
      this.listen(BlockInteractEvent.class, this::onBlockInteract);
   }

   private void onTick() {
      if (this.currentItem != null) {
         class_746 player = class_310.method_1551().field_1724;
         if (player == null) {
            this.reset();
         } else if (!this.releasing) {
            if (this.bundleUse == null || matches(player.method_31548().method_5438(this.itemSlot), this.currentItem)) {
               class_310.method_1551().field_1690.field_1904.method_23481(true);
            }
         } else {
            class_310.method_1551().field_1690.field_1904.method_23481(false);
            if (this.bundleUse == null && this.swappedFromInventory) {
               this.swapSlots(this.itemSlot, this.previousSlot);
            } else if (this.bundleUse == null && this.previousSlot != -1 && this.previousSlot != this.itemSlot) {
               player.method_31548().method_61496(this.previousSlot);
            }

            this.reset();
         }
      }
   }

   private void reset() {
      this.currentItem = null;
      this.itemSlot = -1;
      this.previousSlot = -1;
      this.swappedFromInventory = false;
      this.releasing = false;
      this.bundleUse = null;
   }

   private void onPress(ItemHelperEntry itemBind, BindSetting ignored) {
      this.startUsing(itemBind);
   }

   private void onRelease(ItemHelperEntry itemBind, BindSetting ignored) {
      if (this.currentItem == itemBind) {
         this.releasing = true;
      }
   }

   private void startUsing(ItemHelperEntry itemBind) {
      if (this.enabledSetting.isEnabled()) {
         if (this.currentItem == null) {
            class_746 player = class_310.method_1551().field_1724;
            if (player != null) {
               class_1661 inventory = player.method_31548();
               int slot = this.findSlot(inventory, itemBind);
               boolean inBundle = this.fromBundle.isEnabled()
                  && (
                     itemBind.isInstantHealingPotion()
                        ? this.findBestHealingInBundle(inventory) != null
                        : Bundles.contains(inventory, stack -> stack.method_7909() == itemBind.getItem())
                  );
               class_1799 preview = new class_1799(itemBind.getItem());
               if (slot == -1 && !inBundle) {
                  ItemAlerts.warnMissing(preview, itemBind.getName());
               } else if (!ItemAlerts.isBusy(null, preview, itemBind.getName())) {
                  if (!this.fromBundle.isEnabled() || !this.bundleHasBetterHealing(inventory, itemBind, slot) || !this.useFromBundle(player, itemBind)) {
                     if (slot != -1) {
                        this.equip(player, itemBind, slot);
                     }
                  }
               }
            }
         }
      }
   }

   private void equip(class_746 player, ItemHelperEntry itemBind, int slot) {
      this.currentItem = itemBind;
      this.itemSlot = slot;
      this.previousSlot = player.method_31548().method_67532();
      if (slot < 9) {
         this.swappedFromInventory = false;
         if (this.previousSlot != slot) {
            player.method_31548().method_61496(slot);
         }
      } else {
         this.swappedFromInventory = true;
         this.swapSlots(slot, this.previousSlot);
      }
   }

   private boolean useFromBundle(class_746 player, ItemHelperEntry itemBind) {
      InventoryController inventory = WexSideClient.getInventoryController();
      if (inventory != null && !inventory.isActive()) {
         int[] found = itemBind.isInstantHealingPotion()
            ? this.findBestHealingInBundle(player.method_31548())
            : Bundles.findInBundle(player.method_31548(), stack -> stack.method_7909() == itemBind.getItem());
         if (found == null) {
            return false;
         } else {
            BundleUse session = Bundles.useFromBundle(player, inventory, "item_helper", found[0], found[1], this.ftMode.isEnabled());
            if (session == null) {
               return false;
            } else {
               this.bundleUse = session;
               this.currentItem = itemBind;
               this.itemSlot = session.slot();
               this.previousSlot = session.slot();
               this.swappedFromInventory = false;
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private boolean bundleHasBetterHealing(class_1661 inventory, ItemHelperEntry itemBind, int slot) {
      if (slot == -1) {
         return true;
      } else if (!itemBind.isInstantHealingPotion()) {
         return false;
      } else {
         int[] found = this.findBestHealingInBundle(inventory);
         return found != null && found[2] > healingAmplifier(inventory.method_5438(slot));
      }
   }

   private void swapSlots(int slot, int selected) {
      InventoryController inventory = WexSideClient.getInventoryController();
      if (inventory != null) {
         ClickPolicy policy = "Legit".equalsIgnoreCase(this.swapMode.getSelectedOption()) ? ClickPolicy.SWAP : ClickPolicy.VISIBLE;
         inventory.submit(this.task(new ClickSlotAction(slot, selected), policy));
      }
   }

   private int findSlot(class_1661 inventory, ItemHelperEntry itemBind) {
      if (itemBind.isInstantHealingPotion()) {
         return this.findBestHealingSlot(inventory);
      } else {
         for(int slot = 0; slot < 36; ++slot) {
            class_1799 stack = inventory.method_5438(slot);
            if (!stack.method_7960() && stack.method_7909() == itemBind.getItem()) {
               return slot;
            }
         }

         return -1;
      }
   }

   private int findBestHealingSlot(class_1661 inventory) {
      int bestSlot = -1;
      int bestAmplifier = -1;

      for(int slot = 0; slot < 36; ++slot) {
         int amplifier = healingAmplifier(inventory.method_5438(slot));
         if (amplifier > bestAmplifier) {
            bestAmplifier = amplifier;
            bestSlot = slot;
         }
      }

      return bestSlot;
   }

   private int[] findBestHealingInBundle(class_1661 inventory) {
      int[] best = null;
      int bestAmplifier = -1;

      for(int slot = 0; slot < 36; ++slot) {
         class_9276 contents = (class_9276)inventory.method_5438(slot).method_58694(class_9334.field_49650);
         if (contents != null) {
            for(int nested = 0; nested < contents.method_57426(); ++nested) {
               int amplifier = healingAmplifier(contents.method_57422(nested));
               if (amplifier > bestAmplifier) {
                  bestAmplifier = amplifier;
                  best = new int[]{slot, nested, amplifier};
               }
            }
         }
      }

      return best;
   }

   private void onBlockInteract(BlockInteractEvent event) {
      if (this.currentItem != null) {
         event.cancel();
      }
   }

   private boolean isVisualizerEnabled() {
      return this.enabledSetting.isEnabled() && this.visualizer.isEnabled();
   }

   private boolean isVisualizerItemEnabled(ItemHelperEntry itemBind) {
      return this.visualizerAll.isEnabled() || this.visualizerItems.getSelectedOptions().contains(itemBind.getName());
   }

   private static boolean matches(class_1799 stack, ItemHelperEntry itemBind) {
      return itemBind.isInstantHealingPotion() ? isHealingPotion(stack) : stack.method_7909() == itemBind.getItem();
   }

   private static boolean matchesItem(ItemHelperEntry itemBind, class_1799 stack) {
      return stack.method_7909() == itemBind.getItem();
   }

   private static boolean isHealingPotion(class_1799 stack) {
      return healingAmplifier(stack) >= 0;
   }

   private static int healingAmplifier(class_1799 stack) {
      if (stack.method_7909() != class_1802.field_8574) {
         return -1;
      } else {
         class_1844 contents = (class_1844)stack.method_58694(class_9334.field_49651);
         if (contents == null) {
            return -1;
         } else {
            int amplifier = -1;

            for(class_1293 instance : contents.method_57397()) {
               if (instance.method_5579().equals(class_1294.field_5915)) {
                  amplifier = Math.max(amplifier, instance.method_5578());
               }
            }

            return amplifier;
         }
      }
   }

   private InventoryTask task(InventoryAction action, ClickPolicy policy) {
      return InventoryTask.builder().action(action).owner("item_helper").flag(TaskFlag.DEFAULT).policy(policy).priority(TaskPriority.HIGH).build();
   }
}
