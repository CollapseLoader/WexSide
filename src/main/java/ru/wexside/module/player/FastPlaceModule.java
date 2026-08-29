package ru.wexside.module.player;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class FastPlaceModule extends Module implements ConfigSerializable {
   private static final int VANILLA_DELAY = 4;
   private static volatile FastPlaceModule instance;
   private final BooleanSetting enabledSetting;
   private final NumberSetting delay;

   public FastPlaceModule(EventBus eventBus) {
      super(eventBus, "fast_place", "Fast Place", "Позволяет быстро ставить блоки", ModuleCategory.valueOf("PLAYER"));
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
            .range(0.0, 4.0)
            .defaultValue(1.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Delay")
            .id("delay")
            .description("Задержка между установкой блоков"))
         .build();
      this.registerSetting(this.delay);
   }

   @Override
   protected void initialize() {
   }

   public static int getIntType() {
      FastPlaceModule module = instance;
      return module != null && module.enabledSetting.isEnabled() ? module.delay.getIntValue() : 4;
   }
}
