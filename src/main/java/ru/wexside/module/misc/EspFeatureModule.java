package ru.wexside.module.misc;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.Setting;

public final class EspFeatureModule extends Module implements ConfigSerializable {
   private final String group;

   public EspFeatureModule(EventBus eventBus, String id, String displayName, String description, String group) {
      super(eventBus, id, displayName, description, ModuleCategory.valueOf("RENDER"));
      this.group = group;
   }

   @Override
   protected void initialize() {
   }

   public void setBooleanSetting(BooleanSetting setting) {
      this.registerSetting(this.registerToggle(setting));
   }

   public void setSetting(Setting setting) {
      this.registerSetting(setting);
   }

   public String getString() {
      return this.group;
   }
}
