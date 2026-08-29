package ru.wexside.module.misc;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class AutoConfigSaveModule extends Module implements ConfigSerializable {
   private static volatile AutoConfigSaveModule instance;
   private final BooleanSetting enabledSetting;

   public AutoConfigSaveModule(EventBus eventBus) {
      super(eventBus, "auto_config_save", "Auto Config Save", "Автосохранение конфига при закрытии игры", ModuleCategory.valueOf("MISC"));
      instance = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Автосохранение конфига при закрытии игры")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
   }

   @Override
   protected void initialize() {
   }

   public static boolean isEnabled() {
      return isActive();
   }

   public static boolean isActive() {
      AutoConfigSaveModule module = instance;
      return module == null || module.enabledSetting.isEnabled();
   }
}
