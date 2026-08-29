package ru.wexside.module.render;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.render.ColorCorrectionEffect;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class ColorCorrectionModule extends Module implements ConfigSerializable {
   private static volatile ColorCorrectionModule instance;
   private final BooleanSetting enabledSetting;
   private final NumberSetting contrast;
   private final NumberSetting saturation;
   private final NumberSetting brightness;
   private final ColorCorrectionEffect pipeline = new ColorCorrectionEffect();

   public ColorCorrectionModule(EventBus eventBus) {
      super(eventBus, "color_correction", "Color Correction", "Пост-обработка кадра: контраст, насыщенность и яркость", ModuleCategory.valueOf("RENDER"));
      instance = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Включить цветокоррекцию кадра")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      this.contrast = ((NumberSettingBuilder)NumberSetting.builder()
            .range(100.0, 200.0)
            .defaultValue(100.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Contrast")
            .id("contrast")
            .description("Контраст изображения")
            .aliases("contrast", "контраст"))
         .build();
      this.registerSetting(this.contrast);
      this.saturation = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 200.0)
            .defaultValue(100.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Saturation")
            .id("saturation")
            .description("Насыщенность цветов")
            .aliases("saturation", "насыщенность"))
         .build();
      this.registerSetting(this.saturation);
      this.brightness = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 200.0)
            .defaultValue(100.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Brightness")
            .id("brightness")
            .description("Яркость изображения")
            .aliases("brightness", "яркость"))
         .build();
      this.registerSetting(this.brightness);
   }

   @Override
   protected void initialize() {
      this.listen(WorldSessionEvent.class, event -> this.pipeline.close());
   }

   public static void tick() {
      ColorCorrectionModule module = instance;
      if (module != null) {
         if (!module.enabledSetting.isEnabled()) {
            module.pipeline.close();
         } else {
            module.pipeline
               .apply((float)(module.contrast.getValue() / 100.0), (float)(module.saturation.getValue() / 100.0), (float)(module.brightness.getValue() / 100.0));
         }
      }
   }
}
