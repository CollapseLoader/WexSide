package ru.wexside.module.misc;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BindSetting;
import ru.wexside.setting.BindSettingBuilder;

public final class AlternativeMenuKeyModule extends Module implements ConfigSerializable {
   private static volatile AlternativeMenuKeyModule instance;
   private final BindSetting key;

   public AlternativeMenuKeyModule(EventBus eventBus) {
      super(eventBus, "alternative_menu_key", "Alternative Menu Key", "Доп. клавиша открытия меню", ModuleCategory.valueOf("MISC"));
      instance = this;
      this.key = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).name("Key").id("key").description("Дополнительная клавиша открытия меню"))
         .build();
      this.registerSetting(this.key);
   }

   @Override
   protected void initialize() {
   }

   public static boolean process(int keyCode) {
      AlternativeMenuKeyModule module = instance;
      return module != null && module.key.getBindInput().matchesKeyboard(keyCode);
   }
}
