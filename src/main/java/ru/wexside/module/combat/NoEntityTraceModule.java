package ru.wexside.module.combat;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class NoEntityTraceModule extends Module implements ConfigSerializable {
   private static volatile NoEntityTraceModule instance;
   private final BooleanSetting enabledSetting;

   public NoEntityTraceModule(EventBus eventBus) {
      super(
         eventBus,
         "no_entity_trace",
         "No Entity Trace",
         "Отключает трассировку сущностей, позволяя взаимодействовать сквозь них",
         ModuleCategory.valueOf("COMBAT")
      );
      instance = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Отключает трассировку сущностей, позволяя взаимодействовать сквозь них")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
   }

   @Override
   protected void initialize() {
   }

   public static boolean isEnabled() {
      NoEntityTraceModule module = instance;
      return module != null && module.enabledSetting.isEnabled();
   }
}
