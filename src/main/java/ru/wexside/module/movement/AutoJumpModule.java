package ru.wexside.module.movement;

import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public class AutoJumpModule extends Module implements ConfigSerializable {
   private static volatile AutoJumpModule instance;
   private final BooleanSetting enabledSetting;

   public AutoJumpModule(EventBus eventBus) {
      super(eventBus, "auto_jump", "Auto Jump", "Автоматически выполняет прыжок при касании земли", ModuleCategory.valueOf("MOVEMENT"));
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
      AutoJumpModule module = instance;
      if (module != null && module.enabledSetting.isEnabled()) {
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         return player != null && client.field_1687 != null && !player.method_31549().field_7479 && !player.method_5799();
      } else {
         return false;
      }
   }
}
