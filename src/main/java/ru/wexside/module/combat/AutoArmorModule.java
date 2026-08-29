package ru.wexside.module.combat;

import java.util.HashSet;
import net.minecraft.class_10192;
import net.minecraft.class_1304;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_490;
import net.minecraft.class_5134;
import net.minecraft.class_746;
import net.minecraft.class_9276;
import net.minecraft.class_9285;
import net.minecraft.class_9334;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.Bundles;
import ru.wexside.misc.ClickPolicy;
import ru.wexside.misc.Inventories;
import ru.wexside.misc.InventoryTask;
import ru.wexside.misc.SwapSlotsAction;
import ru.wexside.misc.TaskFlag;
import ru.wexside.misc.TaskPriority;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.InventoryController;

public class AutoArmorModule extends Module implements ConfigSerializable {
   private static final String OWNER = "auto_armor";
   private static final long BUNDLE_RETRY_DELAY_MS = 3000L;
   private static final class_1304[] ARMOR_SLOTS = new class_1304[]{class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166};
   private static final class_1304[] ARMOR_SLOTS_KEEP_ELYTRA = new class_1304[]{class_1304.field_6169, class_1304.field_6172, class_1304.field_6166};
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("")
         .withKeybind()
         .toggle())
      .build();
   private final BooleanSetting fromBundle;
   private final BooleanSetting ftMode;
   private final BooleanSetting byDurability;
   private final NumberSetting durabilityThreshold;
   private long nextBundleAttemptAt;

   public AutoArmorModule(EventBus eventBus) {
      super(eventBus, "auto_armor", "Auto Armor", "Автоматически надевает лучшую броню", ModuleCategory.valueOf("COMBAT"));
      this.registerSetting(this.enabledSetting);
      this.fromBundle = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Из мешков")
            .id("from_bundle")
            .description("Учитывать броню в мешках"))
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
      this.byDurability = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("По прочности")
            .id("by_durability")
            .description("Менять броню, когда её прочность упала до предела"))
         .build();
      this.registerSetting(this.byDurability);
      this.durabilityThreshold = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 99.0)
            .defaultValue(10.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Порог прочности")
            .id("durability_threshold")
            .description("Прочность, при которой и ниже броня меняется на целую")
            .visibleWhen(this.byDurability::isEnabled))
         .build();
      this.registerSetting(this.durabilityThreshold);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.onTick());
   }

   private void onTick() {
      InventoryController inventory = WexSideClient.getInventoryController();
      if (inventory != null) {
         if (!this.enabledSetting.isEnabled()) {
            inventory.setString("auto_armor");
         } else {
            class_746 player = class_310.method_1551().field_1724;
            if (player != null && !player.method_7325()) {
               class_437 screen = class_310.method_1551().field_1755;
               if (screen == null || screen instanceof class_490) {
                  boolean wearingElytra = player.method_6118(class_1304.field_6174).method_31574(class_1802.field_8833);
                  class_1304[] slots = wearingElytra ? ARMOR_SLOTS_KEEP_ELYTRA : ARMOR_SLOTS;

                  for(class_1304 slot : slots) {
                     int armorSlot = Inventories.findArmorSlot(slot);
                     if (armorSlot != -1) {
                        int bestSlot = this.findBestArmorSlot(player, slot);
                        if (this.tryEquipFromBundle(player, inventory, slot, armorSlot, bestSlot)) {
                           return;
                        }

                        if (bestSlot != -1 && this.isBetterArmor(slot, bestSlot)) {
                           inventory.submit(
                              InventoryTask.builder()
                                 .action(new SwapSlotsAction(armorSlot, bestSlot))
                                 .owner("auto_armor")
                                 .flag(TaskFlag.REPLACE)
                                 .policy(ClickPolicy.VISIBLE)
                                 .priority(TaskPriority.NORMAL)
                                 .build()
                           );
                           return;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isBetterArmor(class_1304 slot, int inventorySlot) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null && inventorySlot >= 0 && inventorySlot < 36) {
         class_1799 candidate = player.method_31548().method_5438(inventorySlot);
         if (candidate.method_7960()) {
            return false;
         } else {
            class_10192 equippable = (class_10192)candidate.method_58694(class_9334.field_54196);
            if (equippable != null && equippable.comp_3174() == slot) {
               class_1799 equipped = player.method_6118(slot);
               if (this.isBelowDurabilityThreshold(equipped)) {
                  return !this.isBelowDurabilityThreshold(candidate);
               } else {
                  return getProtection(candidate, slot) > getProtection(equipped, slot);
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private boolean isBelowDurabilityThreshold(class_1799 stack) {
      if (this.byDurability.isEnabled() && !stack.method_7960() && stack.method_7963()) {
         int maxDamage = stack.method_7936();
         if (maxDamage <= 0) {
            return false;
         } else {
            double remainingPercent = (double)(maxDamage - stack.method_7919()) * 100.0 / (double)maxDamage;
            return remainingPercent <= this.durabilityThreshold.getValue();
         }
      } else {
         return false;
      }
   }

   private int findBestArmorSlot(class_746 player, class_1304 slot) {
      class_1799 equipped = player.method_6118(slot);
      boolean replaceDamaged = this.isBelowDurabilityThreshold(equipped);
      int bestProtection = replaceDamaged ? Integer.MIN_VALUE : getProtection(equipped, slot);
      int bestSlot = -1;
      HashSet<Integer> seenProtection = new HashSet<>();
      class_1661 inventory = player.method_31548();

      for(int i = 0; i < 36; ++i) {
         class_1799 stack = inventory.method_5438(i);
         if (!stack.method_7960()) {
            class_10192 equippable = (class_10192)stack.method_58694(class_9334.field_54196);
            if (equippable != null && equippable.comp_3174() == slot && (!replaceDamaged || !this.isBelowDurabilityThreshold(stack))) {
               int protection = getProtection(stack, slot);
               if (!seenProtection.contains(protection)) {
                  seenProtection.add(protection);
                  if (protection > bestProtection) {
                     bestProtection = protection;
                     bestSlot = i;
                  }
               }
            }
         }
      }

      return bestSlot;
   }

   private boolean tryEquipFromBundle(class_746 player, InventoryController inventory, class_1304 slot, int armorSlot, int bestInventorySlot) {
      if (this.fromBundle.isEnabled() && !inventory.isActive()) {
         long now = System.currentTimeMillis();
         if (now < this.nextBundleAttemptAt) {
            return false;
         } else {
            class_1661 playerInventory = player.method_31548();
            class_1799 equipped = player.method_6118(slot);
            boolean replaceDamaged = this.isBelowDurabilityThreshold(equipped);
            int bestProtection = replaceDamaged ? Integer.MIN_VALUE : getProtection(equipped, slot);
            if (bestInventorySlot != -1) {
               class_1799 best = playerInventory.method_5438(bestInventorySlot);
               if (!replaceDamaged || !this.isBelowDurabilityThreshold(best)) {
                  bestProtection = Math.max(bestProtection, getProtection(best, slot));
               }
            }

            int[] found = this.findBestInBundle(playerInventory, slot, bestProtection, replaceDamaged);
            if (found == null) {
               return false;
            } else if (Bundles.useFromBundle(player, inventory, "auto_armor", found[0], found[1], armorSlot, this.ftMode.isEnabled())) {
               return true;
            } else {
               this.nextBundleAttemptAt = now + 3000L;
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private int[] findBestInBundle(class_1661 inventory, class_1304 slot, int minProtection, boolean replaceDamaged) {
      int[] best = null;
      int bestProtection = minProtection;

      for(int i = 0; i < 36; ++i) {
         class_9276 contents = (class_9276)inventory.method_5438(i).method_58694(class_9334.field_49650);
         if (contents != null) {
            for(int nested = 0; nested < contents.method_57426(); ++nested) {
               class_1799 stack = contents.method_57422(nested);
               if (!stack.method_7960()) {
                  class_10192 equippable = (class_10192)stack.method_58694(class_9334.field_54196);
                  if (equippable != null && equippable.comp_3174() == slot && (!replaceDamaged || !this.isBelowDurabilityThreshold(stack))) {
                     int protection = getProtection(stack, slot);
                     if (protection > bestProtection) {
                        bestProtection = protection;
                        best = new int[]{i, nested};
                     }
                  }
               }
            }
         }
      }

      return best;
   }

   private static int getProtection(class_1799 stack, class_1304 slot) {
      class_9285 modifiers = (class_9285)stack.method_58694(class_9334.field_49636);
      if (modifiers == null) {
         return 0;
      } else {
         double[] total = new double[]{0.0};
         modifiers.method_57482(slot, (attribute, modifier) -> {
            if (attribute.method_55838(class_5134.field_23724)) {
               total[0] += modifier.comp_2449();
            }
         });
         return (int)total[0];
      }
   }
}
