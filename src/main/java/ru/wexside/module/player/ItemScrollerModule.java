package ru.wexside.module.player;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class ItemScrollerModule extends Module implements ConfigSerializable {
   private static volatile ItemScrollerModule instance;
   private final BooleanSetting enabledSetting;
   private final NumberSetting delay;
   private long lastScrollTime;

   public ItemScrollerModule(EventBus eventBus) {
      super(eventBus, "item_scroller", "Item Scroller", "Позволяет перемещать предметы с зажатой клавишей ЛКМ", ModuleCategory.valueOf("PLAYER"));
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
      this.delay = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 1000.0)
            .defaultValue(100.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Delay")
            .id("delay")
            .description("Задержка между перемещениями"))
         .build();
      this.registerSetting(this.delay);
   }

   @Override
   protected void initialize() {
   }

   public static boolean isEnabled() {
      ItemScrollerModule module = instance;
      return module != null && module.enabledSetting.isEnabled();
   }

   public static boolean isEnabled2() {
      ItemScrollerModule module = instance;
      if (module != null && module.enabledSetting.isEnabled()) {
         long now = System.currentTimeMillis();
         if (now - module.lastScrollTime >= (long)module.delay.getIntValue()) {
            module.lastScrollTime = now;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
