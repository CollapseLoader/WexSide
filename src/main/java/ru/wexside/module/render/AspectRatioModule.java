package ru.wexside.module.render;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.AspectRatioEvent;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class AspectRatioModule extends Module implements ConfigSerializable {
   private final ModeSetting ratio;
   private final NumberSetting number;
   private final BooleanSetting enabledSetting;
   private static final String string = "Custom";

   private void onGame(AspectRatioEvent gameEvent) {
      if (this.enabledSetting.isEnabled()) {
         String var3 = this.ratio.getSelectedOption();

         float f = switch(var3) {
            case "21:9" -> 2.3333333F;
            case "16:10" -> 1.6F;
            case "16:9" -> 1.7777778F;
            case "4:3" -> 1.3333334F;
            default -> this.number.getFloatValue();
         };
         gameEvent.setAspectRatio(f);
      }
   }

   @Override
   protected void initialize() {
      this.listen(AspectRatioEvent.class, this::onGame);
   }

   public AspectRatioModule(EventBus eventBus) {
      super(eventBus, "aspect_ratio", "Aspect Ratio", "Изменение соотношения сторон", ModuleCategory.valueOf("RENDER"));
      BooleanSetting booleanSetting;
      this.enabledSetting = booleanSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Изменение соотношения сторон")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(booleanSetting);
      ModeSetting modeSetting;
      this.ratio = modeSetting = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Custom", "21:9", "16:10", "16:9", "4:3")
            .defaultOption("Custom")
            .name("Ratio")
            .id("ratio")
            .description("Соотношение сторон")
            .aliases("ratio", "соотношение"))
         .build();
      this.registerSetting(modeSetting);
      NumberSetting numberSetting;
      this.number = numberSetting = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 2.0)
            .defaultValue(1.5)
            .multiplier(1.0)
            .precision(1)
            .animationSpeed(20.0F)
            .name("Value")
            .id("value")
            .description("Значение соотношения")
            .aliases("value", "значение")
            .visibleWhen(() -> "Custom".equals(this.ratio.getSelectedOption())))
         .build();
      this.registerSetting(numberSetting);
   }
}
