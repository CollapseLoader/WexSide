package ru.wexside.module.movement;

import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public class AutoSprintModule extends Module implements ConfigSerializable {
   private static volatile AutoSprintModule instance;
   private final BooleanSetting enabledSetting;
   private final BooleanSetting keepSprint;
   private boolean suppressUntilNextTick;

   public AutoSprintModule(EventBus eventBus) {
      super(eventBus, "auto_sprint", "Auto Sprint", "Автоматически включает спринт", ModuleCategory.valueOf("MOVEMENT"));
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
      this.keepSprint = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Keep Sprint")
            .id("keep_sprint")
            .description("Сохранение спринта при атаке"))
         .build();
      this.registerSetting(this.keepSprint);
   }

   @Override
   protected void initialize() {
   }

   public static boolean isEnabled() {
      AutoSprintModule module = instance;
      if (module == null || !module.enabledSetting.isEnabled()) {
         return false;
      } else if (module.suppressUntilNextTick) {
         module.suppressUntilNextTick = false;
         return false;
      } else {
         class_746 player = class_310.method_1551().field_1724;
         if (player == null) {
            return false;
         } else {
            return player.field_3913.method_20622() && !player.method_6115() && !player.method_5715() ? module.canSprint(player) : false;
         }
      }
   }

   public static boolean isEnabled2() {
      AutoSprintModule module = instance;
      return module != null && module.enabledSetting.isEnabled() && module.keepSprint.isEnabled();
   }

   public static void tick() {
      AutoSprintModule module = instance;
      if (module != null) {
         module.suppressUntilNextTick = true;
      }
   }

   private boolean canSprint(class_746 player) {
      if (player.method_5869()) {
         return true;
      } else if (player.method_31549().field_7479) {
         return true;
      } else {
         return player.method_7344().method_7586() > 6;
      }
   }
}
