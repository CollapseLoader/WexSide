package ru.wexside.module.combat;

import java.util.List;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1541;
import net.minecraft.class_1548;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1844;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_9276;
import net.minecraft.class_9334;
import net.minecraft.class_2338.class_2339;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.TotemPopEvent;
import ru.wexside.misc.Bundles;
import ru.wexside.misc.ClickPolicy;
import ru.wexside.misc.ClickSlotAction;
import ru.wexside.misc.FriendList;
import ru.wexside.misc.Inventories;
import ru.wexside.misc.InventoryTask;
import ru.wexside.misc.TaskFlag;
import ru.wexside.misc.TaskPriority;
import ru.wexside.misc.VisibilityCondition;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.InventoryController;
import ru.wexside.util.entity.NpcDetector;

public class AutoTotemModule extends Module implements ConfigSerializable {
   private static final String OWNER = "auto_totem";
   private static final int OFFHAND_CONTAINER_SLOT = 45;
   private static final int TOTEM_POP_LOCK_TICKS = 20;
   private static final int SWAP_CONFIRM_TICKS = 10;
   private static volatile AutoTotemModule instance;
   private final BooleanSetting enabledSetting;
   private final MultiSelectSetting consider;
   private final NumberSetting health;
   private final NumberSetting maceHealth;
   private final BooleanSetting ignoreAutoswap;
   private final BooleanSetting swapBack;
   private final NumberSetting swapBackDelay;
   private final BooleanSetting ignoreWhenUsing;
   private final MultiSelectSetting ignoreOnlyItems;
   private final MultiSelectSetting ignoreExceptThreats;
   private final BooleanSetting fromBundle;
   private final BooleanSetting ftMode;
   private int swapBackTicks;
   private int pendingOffhandTicks;
   private int totemPopTicks;
   private class_1799 expectedOffhand = class_1799.field_8037;
   private class_1799 previousOffhand = class_1799.field_8037;

   public AutoTotemModule(EventBus eventBus) {
      super(eventBus, "auto_totem", "Auto Totem", "Берёт тотем в офхенд при опасности", ModuleCategory.valueOf("COMBAT"));
      instance = this;
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
      MultiSelectSetting considerSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("Absorption", "Falling", "Crystals", "Anchor", "Mace")
            .selectAll(false)
            .optionListEnabled(false)
            .name("Consider")
            .id("consider")
            .description("Факторы для активации"))
         .build();
      this.consider = considerSetting;
      this.registerSetting(considerSetting);
      this.health = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 20.0)
            .defaultValue(4.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .snapTo(1.0)
            .name("Health")
            .id("health")
            .description("Минимальный HP для активации"))
         .build();
      this.registerSetting(this.health);
      this.maceHealth = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 20.0)
            .defaultValue(10.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .snapTo(1.0)
            .name("Mace HP")
            .id("mace_health")
            .description("Порог HP, ниже которого угроза Mace функционирует")
            .visibleWhen(() -> this.consider.getSelectedOptions().contains("Mace")))
         .build();
      this.registerSetting(this.maceHealth);
      this.ignoreAutoswap = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Ignore AutoSwap")
            .id("ignore_autoswap")
            .description("Блокировка AutoSwap когда нужен тотем (HP-порог или угроза)"))
         .build();
      this.registerSetting(this.ignoreAutoswap);
      this.swapBack = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Swap-Back")
            .id("swap_back")
            .description("Возврат предыдущего предмета из офхенда когда тотем не нужен"))
         .build();
      this.registerSetting(this.swapBack);
      this.swapBackDelay = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 6.0)
            .defaultValue(3.0)
            .multiplier(1.0)
            .precision(1)
            .animationSpeed(20.0F)
            .snapTo(0.5)
            .name("Задержка возврата")
            .id("swap_back_delay")
            .description("Задержка перед возвратом предмета после того как тотем стал не нужен\nПомогает при активном PvP когда HP дёргается в районе порога")
            .visibleWhen(this.swapBack::isEnabled))
         .build();
      this.registerSetting(this.swapBackDelay);
      this.ignoreWhenUsing = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Игнор при использовании")
            .id("ignore_when_using")
            .description("Не свапать если используется предмет"))
         .build();
      this.registerSetting(this.ignoreWhenUsing);
      VisibilityCondition ignoreWhenUsingVisibility = VisibilityCondition.process("ignore_when_using", this.ignoreWhenUsing::isEnabled);
      MultiSelectSetting ignoreOnlyItemsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("GApple", "Чарка", "Зелье Исцеления")
            .selectAll(false)
            .optionListEnabled(false)
            .name("Только если:")
            .id("ignore_only_items")
            .description("Игнор только при исп. выбранных предметов, которые сейвят по HP-порогу. Если 0/3 - абсолютно любой предмет")
            .visibility(ignoreWhenUsingVisibility))
         .build();
      this.ignoreOnlyItems = ignoreOnlyItemsSetting;
      this.registerSetting(ignoreOnlyItemsSetting);
      MultiSelectSetting ignoreExceptThreatsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("Falling", "Crystals", "Anchor", "Mace")
            .selectAll(false)
            .optionListEnabled(false)
            .name("За исключением")
            .id("ignore_except_threats")
            .description("Свап на тотем даже при игноре, если есть выбранная угроза")
            .visibility(ignoreWhenUsingVisibility))
         .build();
      this.ignoreExceptThreats = ignoreExceptThreatsSetting;
      this.registerSetting(ignoreExceptThreatsSetting);
      this.fromBundle = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Из мешков")
            .id("from_bundle")
            .description("Доставать тотем из мешка если нет в инвентаре"))
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
      this.listen(TotemPopEvent.class, this::onTotemPop);
   }

   public static boolean isActive() {
      AutoTotemModule module = instance;
      if (module != null && module.enabledSetting.isEnabled() && module.ignoreAutoswap.isEnabled()) {
         class_746 player = class_310.method_1551().field_1724;
         return player != null && module.needsTotem(player);
      } else {
         return false;
      }
   }

   private void onTick() {
      if (!this.enabledSetting.isEnabled()) {
         this.reset();
      } else {
         class_746 player = class_310.method_1551().field_1724;
         if (player == null) {
            this.reset();
         } else {
            this.tickPendingOffhand(player);
            if (this.totemPopTicks > 0) {
               --this.totemPopTicks;
            }

            if (!this.shouldIgnoreWhileUsing(player)) {
               InventoryController inventory = WexSideClient.getInventoryController();
               if (inventory != null) {
                  boolean needTotem = this.needsTotem(player);
                  if (needTotem) {
                     this.swapBackTicks = this.swapBackDelayTicks();
                  } else if (this.swapBackTicks > 0) {
                     --this.swapBackTicks;
                  }

                  boolean totemPopLock = this.totemPopTicks > 0;
                  if (totemPopLock) {
                     this.swapBackTicks = 0;
                  }

                  if (totemPopLock || !this.isSwapPending()) {
                     class_1661 playerInventory = player.method_31548();
                     class_1799 offhand = player.method_6079();
                     boolean holdingTotem = offhand.method_7909() == class_1802.field_8288;
                     if (needTotem) {
                        this.equipTotem(player, inventory, playerInventory, offhand, holdingTotem);
                     } else {
                        this.trySwapBack(player, inventory, playerInventory);
                     }
                  }
               }
            }
         }
      }
   }

   private void equipTotem(class_746 player, InventoryController inventory, class_1661 playerInventory, class_1799 offhand, boolean holdingTotem) {
      if (!player.method_7357().method_7904(new class_1799(class_1802.field_8288))) {
         int totemSlot = this.findTotemSlot(playerInventory);
         if (totemSlot == -1) {
            if (!holdingTotem && this.fromBundle.isEnabled() && !inventory.isActive()) {
               int[] found = Bundles.findInBundle(playerInventory, stack -> stack.method_31574(class_1802.field_8288));
               if (found != null) {
                  class_1799 previous = offhand.method_7972();
                  class_1799 fromBundle = this.stackFromBundle(playerInventory, found);
                  if (Bundles.useFromBundle(player, inventory, "auto_totem", found[0], found[1], 45, this.ftMode.isEnabled())) {
                     if (!previous.method_7960()) {
                        this.previousOffhand = previous;
                     }

                     this.rememberSwap(fromBundle);
                  }
               }
            }
         } else {
            boolean foundGlint = playerInventory.method_5438(totemSlot).method_7958();
            boolean offhandGlint = holdingTotem && offhand.method_7958();
            if (!holdingTotem || offhandGlint && !foundGlint) {
               if (!offhand.method_7960()) {
                  this.previousOffhand = offhand.method_7972();
               }

               class_1799 totem = playerInventory.method_5438(totemSlot).method_7972();
               this.swapToOffhand(inventory, Inventories.toContainerSlot(totemSlot));
               this.rememberSwap(totem);
            }
         }
      }
   }

   private void trySwapBack(class_746 player, InventoryController inventory, class_1661 playerInventory) {
      if (this.swapBack.isEnabled() && !this.previousOffhand.method_7960() && this.swapBackTicks <= 0) {
         int slot = this.findMatchingSlot(playerInventory, this.previousOffhand);
         if (slot != -1) {
            class_1799 restored = playerInventory.method_5438(slot).method_7972();
            this.swapToOffhand(inventory, Inventories.toContainerSlot(slot));
            this.rememberSwap(restored);
            this.previousOffhand = class_1799.field_8037;
         } else if (!this.fromBundle.isEnabled()) {
            this.previousOffhand = class_1799.field_8037;
         } else {
            class_1799 previous = this.previousOffhand;
            int[] found = Bundles.findInBundle(playerInventory, stack -> class_1799.method_7973(stack, previous));
            if (found == null) {
               this.previousOffhand = class_1799.field_8037;
            } else if (!inventory.isActive()) {
               if (Bundles.useFromBundle(player, inventory, "auto_totem", found[0], found[1], 45, this.ftMode.isEnabled())) {
                  this.rememberSwap(previous);
               }

               this.previousOffhand = class_1799.field_8037;
            }
         }
      }
   }

   private void onTotemPop(TotemPopEvent event) {
      if (this.enabledSetting.isEnabled()) {
         class_746 player = class_310.method_1551().field_1724;
         if (player != null && event.getEntity() == player) {
            this.totemPopTicks = 20;
            this.swapBackTicks = 0;
         }
      }
   }

   private void tickPendingOffhand(class_746 player) {
      if (this.pendingOffhandTicks > 0) {
         if (class_1799.method_7973(player.method_6079(), this.expectedOffhand)) {
            this.pendingOffhandTicks = 0;
            this.expectedOffhand = class_1799.field_8037;
         } else {
            if (--this.pendingOffhandTicks <= 0) {
               this.expectedOffhand = class_1799.field_8037;
            }
         }
      }
   }

   private boolean shouldIgnoreWhileUsing(class_746 player) {
      if (!this.ignoreWhenUsing.isEnabled()) {
         return false;
      } else if (!this.isUsingIgnoredItem(player)) {
         return false;
      } else {
         return !this.hasThreat(player, this.ignoreExceptThreats.getSelectedOptions());
      }
   }

   private boolean isUsingIgnoredItem(class_746 player) {
      List<String> items = this.ignoreOnlyItems.getSelectedOptions();
      if (items.isEmpty()) {
         return class_310.method_1551().field_1690.field_1904.method_1434();
      } else if (!player.method_6115()) {
         return false;
      } else {
         class_1799 active = player.method_6030();
         if (items.contains("GApple") && active.method_31574(class_1802.field_8463)) {
            return true;
         } else if (items.contains("Чарка") && active.method_31574(class_1802.field_8367)) {
            return true;
         } else {
            return items.contains("Зелье Исцеления") && this.isHealingPotion(active);
         }
      }
   }

   private boolean needsTotem(class_746 player) {
      if (this.hasConsideredThreat(player)) {
         return true;
      } else {
         float hp = player.method_6032();
         if (this.consider.getSelectedOptions().contains("Absorption")) {
            hp += player.method_6059(class_1294.field_5898) ? player.method_6067() : 0.0F;
         }

         return hp <= this.health.getFloatValue();
      }
   }

   private boolean hasConsideredThreat(class_746 player) {
      return this.hasThreat(player, this.consider.getSelectedOptions());
   }

   private boolean hasThreat(class_746 player, List<String> threats) {
      if (threats.contains("Crystals") && this.isNearExplosive(player)) {
         return true;
      } else if (threats.contains("Anchor") && this.isNearCobweb(player)) {
         return true;
      } else if (threats.contains("Falling") && this.isFallingFar(player)) {
         return true;
      } else {
         return threats.contains("Mace") && this.isMaceThreat(player);
      }
   }

   private boolean isNearExplosive(class_746 player) {
      class_638 world = class_310.method_1551().field_1687;
      if (world == null) {
         return false;
      } else {
         for(class_1297 entity : world.method_18112()) {
            if (this.isExplosive(entity) && (double)player.method_5739(entity) <= 6.0) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean isExplosive(class_1297 entity) {
      return entity instanceof class_1511 || entity instanceof class_1548 || entity instanceof class_1541;
   }

   private boolean isNearCobweb(class_746 player) {
      class_638 world = class_310.method_1551().field_1687;
      if (world == null) {
         return false;
      } else {
         class_2338 origin = player.method_24515();
         class_2339 cursor = new class_2339();

         for(int x = -6; x <= 6; ++x) {
            for(int y = -6; y <= 6; ++y) {
               for(int z = -6; z <= 6; ++z) {
                  cursor.method_10103(origin.method_10263() + x, origin.method_10264() + y, origin.method_10260() + z);
                  if (world.method_8320(cursor).method_27852(class_2246.field_10343)) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   private boolean isFallingFar(class_746 player) {
      if (player.method_5799()) {
         return false;
      } else if (player.method_6101()) {
         return false;
      } else {
         return player.field_6017 > 10.0;
      }
   }

   private boolean isMaceThreat(class_746 player) {
      if (player.method_6032() > this.maceHealth.getFloatValue()) {
         return false;
      } else {
         class_638 world = class_310.method_1551().field_1687;
         if (world == null) {
            return false;
         } else {
            for(class_1657 other : world.method_18456()) {
               if (this.isMaceAttacker(player, other)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   private boolean isMaceAttacker(class_746 player, class_1657 other) {
      if (other != player && other.method_5805()) {
         if (!other.method_6047().method_31574(class_1802.field_49814)) {
            return false;
         } else {
            FriendList friends = WexSideClient.getFriends();
            if (friends != null && friends.contains(other.method_5477().getString())) {
               return false;
            } else {
               NpcDetector npcDetector = WexSideClient.getNpcDetector();
               if (npcDetector != null && npcDetector.isNpc(other)) {
                  return false;
               } else if (other.method_24828()) {
                  return false;
               } else if (other.method_23318() - other.field_6036 > -0.15) {
                  return false;
               } else if (other.method_23318() - player.method_23318() < 2.0) {
                  return false;
               } else {
                  double dx = other.method_23317() - player.method_23317();
                  double dz = other.method_23321() - player.method_23321();
                  return dx * dx + dz * dz <= 25.0;
               }
            }
         }
      } else {
         return false;
      }
   }

   private int findTotemSlot(class_1661 inventory) {
      int enchanted = -1;

      for(int slot = 0; slot < 36; ++slot) {
         class_1799 stack = inventory.method_5438(slot);
         if (stack.method_7909() == class_1802.field_8288) {
            if (!stack.method_7958()) {
               return slot;
            }

            enchanted = slot;
         }
      }

      return enchanted;
   }

   private class_1799 stackFromBundle(class_1661 inventory, int[] found) {
      class_9276 contents = (class_9276)inventory.method_5438(found[0]).method_58694(class_9334.field_49650);
      return contents == null ? class_1799.field_8037 : contents.method_57422(found[1]).method_7972();
   }

   private int findMatchingSlot(class_1661 inventory, class_1799 target) {
      for(int slot = 0; slot < 36; ++slot) {
         if (class_1799.method_7973(inventory.method_5438(slot), target)) {
            return slot;
         }
      }

      return -1;
   }

   private boolean isHealingPotion(class_1799 stack) {
      if (!stack.method_31574(class_1802.field_8574)) {
         return false;
      } else {
         class_1844 contents = (class_1844)stack.method_58694(class_9334.field_49651);
         if (contents == null) {
            return false;
         } else {
            for(class_1293 effect : contents.method_57397()) {
               if (effect.method_5579().equals(class_1294.field_5915)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   private void swapToOffhand(InventoryController inventory, int containerSlot) {
      inventory.submit(
         InventoryTask.builder()
            .action(new ClickSlotAction(containerSlot, 40))
            .owner("auto_totem")
            .flag(TaskFlag.DEFAULT)
            .policy(ClickPolicy.VISIBLE)
            .priority(TaskPriority.NORMAL)
            .build()
      );
   }

   private void rememberSwap(class_1799 stack) {
      this.expectedOffhand = stack == null ? class_1799.field_8037 : stack.method_7972();
      this.pendingOffhandTicks = 10;
      this.totemPopTicks = 0;
   }

   private int swapBackDelayTicks() {
      return Math.round(this.swapBackDelay.getFloatValue() * 20.0F);
   }

   private boolean isSwapPending() {
      return this.pendingOffhandTicks > 0;
   }

   private void reset() {
      this.swapBackTicks = 0;
      this.pendingOffhandTicks = 0;
      this.totemPopTicks = 0;
      this.expectedOffhand = class_1799.field_8037;
   }
}
