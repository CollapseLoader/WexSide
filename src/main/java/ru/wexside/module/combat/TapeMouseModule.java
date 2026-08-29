package ru.wexside.module.combat;

import net.minecraft.class_310;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.AttackWindowEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.AttackInvoker;
import ru.wexside.misc.HitCooldown;
import ru.wexside.misc.PlayerChecks;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public class TapeMouseModule extends Module implements ConfigSerializable {
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("")
         .withKeybind()
         .toggle())
      .build();
   private final BooleanSetting tpsSync;
   private final BooleanSetting press;
   private final BooleanSetting stopOnEat;
   private final BooleanSetting weaponOnly;
   private HitCooldown hitCooldown;

   public TapeMouseModule(EventBus eventBus) {
      super(eventBus, "tape_mouse", "Tape Mouse", "Бесперерывная атака", ModuleCategory.valueOf("COMBAT"));
      this.registerSetting(this.enabledSetting);
      this.tpsSync = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("TPS Sync")
            .id("tps_sync")
            .description("Синхронизирует работу модуля с TPS сервера"))
         .build();
      this.registerSetting(this.tpsSync);
      this.press = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("При зажатии")
            .id("press")
            .description("Работает только при зажатой ЛКМ"))
         .build();
      this.registerSetting(this.press);
      this.stopOnEat = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Стоп во время еды")
            .id("stop_on_eat")
            .description("Не атакует, пока вы едите или пьёте"))
         .build();
      this.registerSetting(this.stopOnEat);
      this.weaponOnly = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Только с оружием")
            .id("weapon_only")
            .description("Работает только с мечом или топором в руках"))
         .build();
      this.registerSetting(this.weaponOnly);
   }

   @Override
   protected void initialize() {
      this.hitCooldown = new HitCooldown();
      this.listen(AttackWindowEvent.class, this::onAttackWindow);
   }

   private void onAttackWindow(AttackWindowEvent event) {
      if (this.enabledSetting.isEnabled()) {
         class_310 client = class_310.method_1551();
         if (client.field_1724 != null && client.field_1687 != null) {
            if (!this.weaponOnly.isEnabled() || PlayerChecks.isHoldingWeapon()) {
               if (!this.stopOnEat.isEnabled() || !PlayerChecks.isUsingItem()) {
                  if (!this.press.isEnabled() || client.field_1690.field_1886.method_1434()) {
                     this.hitCooldown.setBooleanType(event.isActive());
                     this.hitCooldown.setBooleanType3(this.tpsSync.isEnabled());
                     if (this.hitCooldown.process(false)) {
                        ((AttackInvoker)client).invokeAttack();
                        this.hitCooldown.setLongType(500L);
                     }
                  }
               }
            }
         }
      }
   }
}
