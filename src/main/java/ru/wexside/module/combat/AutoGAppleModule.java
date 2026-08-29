package ru.wexside.module.combat;

import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public class AutoGAppleModule extends Module implements ConfigSerializable {
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("")
         .withKeybind()
         .toggle())
      .build();
   private final NumberSetting health;
   private boolean eating;

   public AutoGAppleModule(EventBus eventBus) {
      super(eventBus, "auto_gapple", "Auto GApple", "Ест яблоко из офхенда при низком HP", ModuleCategory.valueOf("COMBAT"));
      this.registerSetting(this.enabledSetting);
      this.health = ((NumberSettingBuilder)NumberSetting.builder()
            .range(4.0, 20.0)
            .defaultValue(15.0)
            .multiplier(1.0)
            .precision(1)
            .animationSpeed(20.0F)
            .name("Health")
            .id("health")
            .description("Минимальный HP для активации"))
         .build();
      this.registerSetting(this.health);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.onTick());
   }

   private void onTick() {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null) {
         if (this.shouldEat(player)) {
            class_310.method_1551().field_1690.field_1904.method_23481(true);
            this.eating = true;
         } else if (this.eating) {
            class_310.method_1551().field_1690.field_1904.method_23481(false);
            this.eating = false;
         }
      }
   }

   private boolean shouldEat(class_746 player) {
      if (!this.enabledSetting.isEnabled()) {
         return false;
      } else if (!player.method_6079().method_31574(class_1802.field_8463)) {
         return false;
      } else if (player.method_7357().method_7904(new class_1799(class_1802.field_8463))) {
         return false;
      } else {
         return player.method_6032() + player.method_6067() <= this.health.getFloatValue();
      }
   }
}
