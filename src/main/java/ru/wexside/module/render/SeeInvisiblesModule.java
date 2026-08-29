package ru.wexside.module.render;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class SeeInvisiblesModule extends Module implements ConfigSerializable {
   private final NumberSetting alpha;
   private final BooleanSetting enabledSetting;
   static volatile SeeInvisiblesModule seeInvisiblesModule2;

   @Override
   protected void initialize() {
   }

   public static boolean isEnabled() {
      SeeInvisiblesModule seeInvisiblesModule = seeInvisiblesModule2;
      return seeInvisiblesModule != null && seeInvisiblesModule.enabledSetting.isEnabled();
   }

   public static int getIntType() {
      SeeInvisiblesModule seeInvisiblesModule = seeInvisiblesModule2;
      float f = seeInvisiblesModule != null ? (float)seeInvisiblesModule.alpha.getValue() : 0.5F;
      int n = Math.max(0, Math.min(255, (int)(f * 255.0F)));
      return n << 24 | 16777215;
   }

   public SeeInvisiblesModule(EventBus eventBus) {
      super(eventBus, "see_invisibles", "See Invisibles", "Видеть невидимых сущностей", ModuleCategory.valueOf("RENDER"));
      seeInvisiblesModule2 = this;
      BooleanSetting booleanSetting;
      this.enabledSetting = booleanSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Видеть невидимых сущностей")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(booleanSetting);
      NumberSetting numberSetting;
      this.alpha = numberSetting = ((NumberSettingBuilder)NumberSetting.builder()
            .range(10.0, 100.0)
            .defaultValue(50.0)
            .multiplier(0.01)
            .precision(0)
            .animationSpeed(20.0F)
            .markers(10.0)
            .snapTo(10.0)
            .name("Alpha")
            .id("alpha")
            .description("Прозрачность невидимой сущности"))
         .build();
      this.registerSetting(numberSetting);
   }
}
