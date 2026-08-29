package ru.wexside.module.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1309;
import net.minecraft.class_310;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.AttackWindowEvent;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.AttackOptions;
import ru.wexside.misc.CritCondition;
import ru.wexside.misc.CrosshairTarget;
import ru.wexside.misc.PlayerChecks;
import ru.wexside.misc.RaycastMode;
import ru.wexside.misc.SprintResetMode;
import ru.wexside.misc.TargetFilter;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.setting.RangeSetting;
import ru.wexside.setting.RangeSettingBuilder;
import ru.wexside.util.AuraProcessor;
import ru.wexside.util.CriticalsHandler;

public class TriggerBotModule extends Module implements ConfigSerializable {
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("")
         .withKeybind()
         .toggle())
      .build();
   private final MultiSelectSetting targets;
   private final BooleanSetting criticalsOnly;
   private final BooleanSetting toggleSprint;
   private final BooleanSetting waterCriticals;
   private final BooleanSetting spaceOnly;
   private final BooleanSetting weaponOnly;
   private final BooleanSetting stopOnEat;
   private final ModeSetting attackMode;
   private final RangeSetting cps;
   private final ModeSetting sprintReset;
   private final BooleanSetting tpsSync;
   private AuraProcessor auraProcessor;
   private CrosshairTarget crosshairTarget;
   private CritCondition critCondition;
   private class_1309 target;
   private boolean attackReady;

   public TriggerBotModule(EventBus eventBus) {
      super(eventBus, "trigger_bot", "Trigger Bot", "Автоматически бьёт того, на кого вы смотрите", ModuleCategory.valueOf("COMBAT"));
      this.registerSetting(this.enabledSetting);
      MultiSelectSetting targetSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("Players", "Friends", "Bots", "Naked", "Invisibles", "Animals", "Mobs", "Villagers")
            .selectAll(false)
            .optionListEnabled(false)
            .name("Цели")
            .id("targets")
            .description("Типы целей для атаки")
            .withKeybind())
         .build();
      this.targets = targetSetting;
      this.registerSetting(targetSetting);
      this.criticalsOnly = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Только криты")
            .id("criticals_only")
            .description("Атака только критическим уроном")
            .withKeybind())
         .build();
      this.registerSetting(this.criticalsOnly);
      this.toggleSprint = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Сброс спринта")
            .id("toggle_sprint")
            .description("Автоматически сбрасывает спринт при атаке"))
         .build();
      this.registerSetting(this.toggleSprint);
      this.waterCriticals = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Криты в воде")
            .id("water_criticals")
            .description("Нанесение критического урона в водопаде")
            .visibleWhen(() -> this.criticalsOnly.isEnabled()))
         .build();
      this.registerSetting(this.waterCriticals);
      this.spaceOnly = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Только при прыжке")
            .id("space_only")
            .description("Работает только при зажатом пробеле")
            .visibleWhen(() -> this.criticalsOnly.isEnabled()))
         .build();
      this.registerSetting(this.spaceOnly);
      this.weaponOnly = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Только с оружием")
            .id("weapon_only")
            .description("Работает только с мечом или топором в руках"))
         .build();
      this.registerSetting(this.weaponOnly);
      this.stopOnEat = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Стоп во время еды")
            .id("stop_on_eat")
            .description("Не атакует, пока вы едите или пьёте"))
         .build();
      this.registerSetting(this.stopOnEat);
      this.attackMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("1.9+", "1.8")
            .defaultOption("1.9+")
            .name("Режим атаки")
            .id("attack_mode")
            .description("Режим атаки"))
         .build();
      this.registerSetting(this.attackMode);
      this.cps = ((RangeSettingBuilder)RangeSetting.builder()
            .range(5.0, 20.0)
            .defaultNormalizedRange(0.4666666666666667, 0.8666666666666667)
            .multiplier(1.0)
            .precision(0)
            .name("CPS")
            .id("cps")
            .description("Диапазон случайных значений CPS")
            .visibleWhen(() -> "1.8".equals(this.attackMode.getSelectedOption())))
         .build();
      this.registerSetting(this.cps);
      this.sprintReset = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("FT", "Packet", "Semi-Legit", "Legit")
            .defaultOption("Packet")
            .name("Режим сброса спринта")
            .id("sprint_reset")
            .description("Режим сброса спринта")
            .visibleWhen(() -> this.toggleSprint.isEnabled()))
         .build();
      this.registerSetting(this.sprintReset);
      this.tpsSync = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("TPS Sync")
            .id("tps_sync")
            .description("Синхронизирует кулдаун с TPS сервера"))
         .build();
      this.registerSetting(this.tpsSync);
   }

   @Override
   protected void initialize() {
      this.crosshairTarget = new CrosshairTarget();
      this.critCondition = new CritCondition();
      this.auraProcessor = new AuraProcessor(new CriticalsHandler());
      this.listen(ClientTickEvent.class, event -> this.onTick());
      this.listen(AttackWindowEvent.class, this::onAttackWindow);
   }

   private void onTick() {
      this.auraProcessor.update();
      if (!this.enabledSetting.isEnabled() || class_310.method_1551().field_1724 == null) {
         this.clearTarget();
      } else if (this.weaponOnly.isEnabled() && !PlayerChecks.isHoldingWeapon()) {
         this.clearTarget();
      } else if (this.stopOnEat.isEnabled() && PlayerChecks.isUsingItem()) {
         this.clearTarget();
      } else if (!this.critCondition.process(this.criticalsOnly.isEnabled(), this.spaceOnly.isEnabled(), this.waterCriticals.isEnabled())) {
         this.clearTarget();
      } else {
         TargetFilter filter = new TargetFilter(this.targets.getSelectedOptions(), null, 0);
         class_1309 entity = this.crosshairTarget.process(filter);
         if (entity == null) {
            this.clearTarget();
         } else {
            this.target = entity;
            this.attackReady = true;
         }
      }
   }

   private void onAttackWindow(AttackWindowEvent event) {
      if (this.enabledSetting.isEnabled() && this.attackReady && this.target != null) {
         this.auraProcessor.getHitCooldown().setBooleanType(event.isActive());
         this.auraProcessor.getHitCooldown().setBooleanType3(this.tpsSync.isEnabled());
         int minCps = this.cps.getLowerIntValue();
         int maxCps = this.cps.getUpperIntValue();
         int cps = minCps >= maxCps ? minCps : ThreadLocalRandom.current().nextInt(minCps, maxCps + 1);
         AttackOptions options = new AttackOptions(
            null,
            0.0F,
            cps,
            false,
            this.toggleSprint.isEnabled(),
            SprintResetMode.process(this.sprintReset.getSelectedOption()),
            false,
            RaycastMode.VISIBLE,
            "1.8".equals(this.attackMode.getSelectedOption()),
            100.0,
            false,
            false,
            false,
            1.6F
         );
         if (this.auraProcessor.process(this.target, options)) {
            this.attackReady = false;
         }
      }
   }

   private void clearTarget() {
      this.target = null;
      this.attackReady = false;
   }
}
