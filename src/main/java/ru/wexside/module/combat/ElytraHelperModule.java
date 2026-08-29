package ru.wexside.module.combat;

import java.util.function.Predicate;
import net.minecraft.class_10192;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;
import net.minecraft.class_9334;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.AttackUrgency;
import ru.wexside.misc.Bundles;
import ru.wexside.misc.ClickPolicy;
import ru.wexside.misc.CorrectionMode;
import ru.wexside.misc.HotbarSelectAction;
import ru.wexside.misc.Inventories;
import ru.wexside.misc.InventoryAction;
import ru.wexside.misc.InventoryTask;
import ru.wexside.misc.ItemAlerts;
import ru.wexside.misc.SwapSlotsAction;
import ru.wexside.misc.SwapTiming;
import ru.wexside.misc.TaskFlag;
import ru.wexside.misc.TaskPriority;
import ru.wexside.misc.UseItemAction;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BindSetting;
import ru.wexside.setting.BindSettingBuilder;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.Angle;
import ru.wexside.util.InventoryController;
import ru.wexside.util.RotationController;
import ru.wexside.util.RotationIntent;

public class ElytraHelperModule extends Module implements ConfigSerializable {
   private static final String OWNER = "elytra_helper";
   private static final int CHEST_SLOT = 6;
   private static volatile ElytraHelperModule instance;
   private final BooleanSetting enabledSetting;
   private final ModeSetting mode;
   private final BindSetting swapKey;
   private final BindSetting fireworkKey;
   private final BooleanSetting autoFly;
   private final BooleanSetting autoFirework;
   private final BooleanSetting followTarget;
   private final NumberSetting fireworkDelay;
   private final BooleanSetting armorOnLand;
   private final BooleanSetting fromBundle;
   private final BooleanSetting ftMode;
   private AttackAuraModule attackAura;
   private boolean fireworkUsedAfterTakeoff;
   private boolean requestStartGlide;
   private boolean wasOnGround;
   private int glideCooldown;
   private long lastFireworkTime;

   public ElytraHelperModule(EventBus eventBus) {
      super(eventBus, "elytra_helper", "Elytra Helper", "Управление элитрой и фейерверками", ModuleCategory.valueOf("COMBAT"));
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
      this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Default", "Legit", "FS")
            .defaultOption("Default")
            .name("Mode")
            .id("mode")
            .description("Режим свапа элитры"))
         .build();
      this.registerSetting(this.mode);
      this.swapKey = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .onReleased(this::onSwapBind)
            .name("Swap Key")
            .id("swap_key")
            .description("Кнопка надевания элитры"))
         .build();
      this.registerSetting(this.swapKey);
      this.fireworkKey = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .onReleased(this::onFireworkBind)
            .name("Firework Key")
            .id("firework_key")
            .description("Кнопка фейерверка"))
         .build();
      this.registerSetting(this.fireworkKey);
      this.autoFly = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Auto Fly")
            .id("auto_fly")
            .description("Автоматическое включение полёта"))
         .build();
      this.registerSetting(this.autoFly);
      this.autoFirework = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Auto Firework")
            .id("auto_firework")
            .description("Запуск фейерверка после взлёта")
            .visibleWhen(this.autoFly::isEnabled))
         .build();
      this.registerSetting(this.autoFirework);
      this.followTarget = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Follow Target")
            .id("follow_target")
            .description("Полёт за целью"))
         .build();
      this.registerSetting(this.followTarget);
      this.fireworkDelay = ((NumberSettingBuilder)NumberSetting.builder()
            .range(100.0, 5000.0)
            .defaultValue(500.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Firework Delay")
            .id("firework_delay")
            .description("Задержка между фейерверками")
            .visibleWhen(this.followTarget::isEnabled))
         .build();
      this.registerSetting(this.fireworkDelay);
      this.armorOnLand = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Armor On Land")
            .id("armor_on_land")
            .description("Надевает нагрудник при приземлении"))
         .build();
      this.registerSetting(this.armorOnLand);
      this.fromBundle = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Из мешков")
            .id("from_bundle")
            .description("Доставать элитру и фейерверки из мешка если нет в инвентаре"))
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
      if (this.enabledSetting.isEnabled()) {
         class_746 player = class_310.method_1551().field_1724;
         if (player != null) {
            boolean wearingElytra = player.method_6118(class_1304.field_6174).method_31574(class_1802.field_8833);
            boolean onGround = player.method_24828();
            if (wearingElytra && this.armorOnLand.isEnabled() && onGround && !this.wasOnGround) {
               this.swapChest();
            }

            if (this.autoFly.isEnabled() && wearingElytra) {
               this.tickAutoFly(player, onGround);
            }

            if (this.followTarget.isEnabled() && wearingElytra) {
               this.tickFollowTarget(player);
            }

            this.wasOnGround = onGround;
         }
      }
   }

   private void onSwapBind(BindSetting ignored) {
      this.swapChest();
   }

   private void onFireworkBind(BindSetting ignored) {
      this.useFirework();
   }

   private void useFirework() {
      if (this.enabledSetting.isEnabled()) {
         class_746 player = class_310.method_1551().field_1724;
         if (player != null) {
            InventoryController inventory = WexSideClient.getInventoryController();
            if (inventory != null) {
               if (player.method_6079().method_31574(class_1802.field_8639)) {
                  inventory.submit(this.task(new UseItemAction(), ClickPolicy.SILENT));
                  this.lastFireworkTime = System.currentTimeMillis();
               } else {
                  int slot = Inventories.findSlot(class_1802.field_8639);
                  if (slot == -1) {
                     int[] found;
                     if (this.fromBundle.isEnabled()
                        && (found = Bundles.findInBundle(player.method_31548(), stack -> stack.method_31574(class_1802.field_8639))) != null) {
                        Bundles.useFromBundle(player, inventory, "elytra_helper", found[0], found[1], this.ftMode.isEnabled(), inventory::update3);
                        this.lastFireworkTime = System.currentTimeMillis();
                     } else {
                        class_1799 firework = new class_1799(class_1802.field_8639);
                        ItemAlerts.warnMissing(firework, firework.method_7964().getString());
                     }
                  } else {
                     if (slot < 9) {
                        inventory.submit(this.task(new HotbarSelectAction(slot, true), ClickPolicy.SILENT));
                     } else {
                        this.swapFireworkFromInventory(player, slot, inventory);
                     }

                     this.lastFireworkTime = System.currentTimeMillis();
                  }
               }
            }
         }
      }
   }

   private void swapFireworkFromInventory(class_746 player, int slot, InventoryController inventory) {
      int selected = player.method_31548().method_67532();
      int containerSlot = slot < 9 ? slot + 36 : slot;
      inventory.submit(this.task(inventory.process2(containerSlot, selected, inventory::update3, this.swapTiming()), ClickPolicy.VISIBLE));
   }

   private void swapChest() {
      if (this.enabledSetting.isEnabled()) {
         class_746 player = class_310.method_1551().field_1724;
         if (player != null) {
            InventoryController inventory = WexSideClient.getInventoryController();
            if (inventory != null) {
               class_1799 chest = player.method_6118(class_1304.field_6174);
               boolean wearingElytra = chest.method_31574(class_1802.field_8833);
               int slot = wearingElytra ? this.findChestplateSlot(player) : this.findElytraSlot(player);
               if (slot != -1 || !this.fromBundle.isEnabled() || !this.useChestFromBundle(player, inventory, chest)) {
                  if (slot == -1 && wearingElytra) {
                     slot = this.findEmptySlot(player);
                  }

                  if (slot == -1) {
                     if (wearingElytra) {
                        ItemAlerts.warnMissing(new class_1799(class_1802.field_8058), "Нагрудник");
                     } else {
                        class_1799 elytra = new class_1799(class_1802.field_8833);
                        ItemAlerts.warnMissing(elytra, elytra.method_7964().getString());
                     }
                  } else {
                     inventory.submit(this.task(new SwapSlotsAction(6, slot), this.legitMode() ? ClickPolicy.SWAP : ClickPolicy.VISIBLE));
                  }
               }
            }
         }
      }
   }

   private boolean useChestFromBundle(class_746 player, InventoryController inventory, class_1799 chest) {
      if (inventory.isActive()) {
         return false;
      } else {
         Predicate<class_1799> predicate = chest.method_31574(class_1802.field_8833)
            ? ElytraHelperModule::isChestArmor
            : stack -> stack.method_31574(class_1802.field_8833) && stack.method_7919() < stack.method_7936() - 1;
         int[] found = Bundles.findInBundle(player.method_31548(), predicate);
         return found == null ? false : Bundles.useFromBundle(player, inventory, "elytra_helper", found[0], found[1], 6, this.ftMode.isEnabled());
      }
   }

   private void tickFollowTarget(class_746 player) {
      class_1309 target = this.getAttackAuraTarget();
      if (target != null) {
         RotationController rotations = WexSideClient.getRotationController();
         if (rotations != null) {
            class_243 from = player.method_33571();
            class_243 to = target.method_33571();
            Angle angle = Angle.fromVectors(from, to);
            rotations.process2(new RotationIntent(target, to, angle, AttackUrgency.HIT, CorrectionMode.FOCUSED, false), "Simple");
            long now = System.currentTimeMillis();
            long delay = (long)this.fireworkDelay.getFloatValue();
            boolean fire = target.field_6235 == 0 && now - this.lastFireworkTime >= delay
               || player.method_5739(target) < 10.0F && now - this.lastFireworkTime >= 500L;
            if (fire) {
               this.useFirework();
            }
         }
      }
   }

   private void tickAutoFly(class_746 player, boolean onGround) {
      if (this.glideCooldown > 0) {
         --this.glideCooldown;
      }

      if (onGround) {
         this.fireworkUsedAfterTakeoff = false;
         this.requestStartGlide = true;
      } else if (player.method_6128()) {
         if (this.autoFirework.isEnabled() && !this.fireworkUsedAfterTakeoff) {
            this.fireworkUsedAfterTakeoff = true;
            this.useFirework();
         }
      } else if (this.glideCooldown <= 0) {
         if (!(player.method_18798().field_1351 >= 0.0)) {
            if (!player.method_5799() && !player.method_5869() && !player.method_31549().field_7479) {
               this.requestStartGlide = true;
               this.glideCooldown = 5;
            }
         }
      }
   }

   private class_1309 getAttackAuraTarget() {
      if (this.attackAura == null) {
         for(Module module : WexSideClient.getInstance().getModuleManager().getModules()) {
            if (module instanceof AttackAuraModule) {
               AttackAuraModule aura;
               this.attackAura = aura = (AttackAuraModule)module;
               break;
            }
         }
      }

      return this.attackAura == null ? null : this.attackAura.getLivingEntity();
   }

   private static boolean isChestArmor(class_1799 stack) {
      if (!stack.method_7960() && !stack.method_31574(class_1802.field_8833)) {
         class_10192 equippable = (class_10192)stack.method_58694(class_9334.field_54196);
         return equippable != null && equippable.comp_3174() == class_1304.field_6174;
      } else {
         return false;
      }
   }

   private int findElytraSlot(class_746 player) {
      class_1661 inventory = player.method_31548();

      for(int slot = 0; slot < 36; ++slot) {
         class_1799 stack = inventory.method_5438(slot);
         if (stack.method_31574(class_1802.field_8833) && stack.method_7919() < stack.method_7936() - 1) {
            return slot;
         }
      }

      return -1;
   }

   private int findChestplateSlot(class_746 player) {
      class_1661 inventory = player.method_31548();

      for(int slot = 0; slot < 36; ++slot) {
         if (isChestArmor(inventory.method_5438(slot))) {
            return slot;
         }
      }

      return -1;
   }

   private int findEmptySlot(class_746 player) {
      class_1661 inventory = player.method_31548();

      for(int slot = 0; slot < 36; ++slot) {
         if (inventory.method_5438(slot).method_7960()) {
            return slot;
         }
      }

      return -1;
   }

   private InventoryTask task(InventoryAction action, ClickPolicy policy) {
      return InventoryTask.builder().action(action).owner("elytra_helper").flag(TaskFlag.DEFAULT).policy(policy).priority(TaskPriority.NORMAL).build();
   }

   private boolean legitMode() {
      return "Legit".equals(this.mode.getSelectedOption());
   }

   private SwapTiming swapTiming() {
      String value = this.mode.getSelectedOption();
      if ("Legit".equals(value)) {
         return SwapTiming.LEGIT;
      } else {
         return "FS".equals(value) ? SwapTiming.FUNTIME : SwapTiming.DEFAULT;
      }
   }

   public static boolean isEnabled2() {
      ElytraHelperModule module = instance;
      if (module != null && module.requestStartGlide) {
         module.requestStartGlide = false;
         return true;
      } else {
         return false;
      }
   }
}
