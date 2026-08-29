package ru.wexside.module.render;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class ItemPhysicModule extends Module implements ConfigSerializable {
   private final BooleanSetting enabledSetting;
   static volatile ItemPhysicModule itemPhysicModule2;

   @Override
   protected void initialize() {
   }

   public static boolean isEnabled() {
      ItemPhysicModule itemPhysicModule = itemPhysicModule2;
      return itemPhysicModule != null && itemPhysicModule.enabledSetting.isEnabled();
   }

   public ItemPhysicModule(EventBus eventBus) {
      super(eventBus, "item_physic", "Item Physic", "Физика выпавших предметов", ModuleCategory.valueOf("RENDER"));
      itemPhysicModule2 = this;
      BooleanSetting booleanSetting;
      this.enabledSetting = booleanSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Включить физику выпавших предметов")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(booleanSetting);
   }
}
