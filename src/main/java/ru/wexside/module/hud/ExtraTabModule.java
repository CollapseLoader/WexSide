package ru.wexside.module.hud;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class ExtraTabModule extends Module implements ConfigSerializable {
   private static volatile ExtraTabModule instance;
   private final BooleanSetting enabledSetting;

   public ExtraTabModule(EventBus eventBus) {
      super(eventBus, "extra_tab", "Extra Tab", "Полный список игроков в табе", ModuleCategory.valueOf("DISPLAY"));
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
   }

   @Override
   protected void initialize() {
   }

   public static boolean isEnabled() {
      ExtraTabModule module = instance;
      return module != null && module.enabledSetting.isEnabled();
   }
}
