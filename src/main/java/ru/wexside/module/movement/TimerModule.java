package ru.wexside.module.movement;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public class TimerModule extends Module implements ConfigSerializable {
   private static volatile TimerModule instance;
   private final BooleanSetting enabledSetting;
   private final NumberSetting speed;

   public TimerModule(EventBus eventBus) {
      super(eventBus, "timer", "Timer", "Изменяет скорость таймера", ModuleCategory.valueOf("MOVEMENT"));
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
      this.speed = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.01, 5.0)
            .defaultValue(1.1)
            .multiplier(1.0)
            .precision(2)
            .animationSpeed(20.0F)
            .name("Speed")
            .id("speed")
            .description("Множитель скорости таймера")
            .visibleWhen(this.enabledSetting::isEnabled))
         .build();
      this.registerSetting(this.speed);
   }

   @Override
   protected void initialize() {
   }

   public static float getFloatType() {
      TimerModule module = instance;
      return module != null && module.enabledSetting.isEnabled() ? (float)Math.max(0.01, module.speed.getValue()) : 1.0F;
   }
}
