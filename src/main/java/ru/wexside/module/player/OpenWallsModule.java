package ru.wexside.module.player;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class OpenWallsModule extends Module implements ConfigSerializable {
   private static volatile OpenWallsModule instance;
   private final BooleanSetting enabledSetting;

   public OpenWallsModule(EventBus eventBus) {
      super(eventBus, "open_walls", "Open Walls", "Отключает трассировку блоков, с которыми нельзя взаимодействовать", ModuleCategory.valueOf("PLAYER"));
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
      OpenWallsModule module = instance;
      return module != null && module.enabledSetting.isEnabled();
   }
}
