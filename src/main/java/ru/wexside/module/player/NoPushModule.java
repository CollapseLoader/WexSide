package ru.wexside.module.player;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;

public final class NoPushModule extends Module implements ConfigSerializable {
   public static final String PLAYERS = "Players";
   public static final String BLOCKS = "Blocks";
   public static final String WATER = "Water";
   private static volatile NoPushModule instance;
   private final BooleanSetting enabledSetting;
   private final MultiSelectSetting types;

   public NoPushModule(EventBus eventBus) {
      super(eventBus, "no_push", "No Push", "Позволяет уменьшать отталкивание", ModuleCategory.valueOf("PLAYER"));
      instance = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Включить уменьшение отталкивания")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      MultiSelectSetting typeSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("Players", "Blocks", "Water")
            .selectAll(false)
            .optionListEnabled(false)
            .name("Types")
            .id("types")
            .description("Типы отталкивания"))
         .build();
      this.types = typeSetting;
      this.registerSetting(typeSetting);
   }

   @Override
   protected void initialize() {
   }

   public static boolean compute(String type) {
      NoPushModule module = instance;
      return module != null && module.enabledSetting.isEnabled() && module.types.getSelectedOptions().contains(type);
   }
}
