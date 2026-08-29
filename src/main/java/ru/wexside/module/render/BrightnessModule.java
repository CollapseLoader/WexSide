package ru.wexside.module.render;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.BrightnessEvent;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public class BrightnessModule extends Module implements ConfigSerializable {
   private final BooleanSetting enabledSetting;
   private final NumberSetting strength;

   @Override
   protected void initialize() {
      this.listen(BrightnessEvent.class, gameEvent3 -> {
         if (this.enabledSetting.isEnabled()) {
            gameEvent3.setBrightness(this.strength.getFloatValue());
         }
      });
   }

   public BrightnessModule(EventBus eventBus) {
      super(eventBus, "brightness", "Brightness", "Изменяет яркость в мире", ModuleCategory.valueOf("RENDER"));
      BooleanSetting booleanSetting;
      this.enabledSetting = booleanSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(booleanSetting);
      NumberSetting numberSetting;
      this.strength = numberSetting = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.1, 1.0)
            .defaultValue(0.3)
            .multiplier(1.0)
            .precision(1)
            .animationSpeed(20.0F)
            .name("Strength")
            .id("strength")
            .description("Интенсивность"))
         .build();
      this.registerSetting(numberSetting);
   }
}
