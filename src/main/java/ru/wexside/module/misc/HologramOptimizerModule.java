package ru.wexside.module.misc;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.render.HologramImpostorRenderer;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class HologramOptimizerModule extends Module implements ConfigSerializable {
   private static volatile HologramOptimizerModule instance;
   private final BooleanSetting enabledSetting;
   private final NumberSetting distance;
   private final NumberSetting quality;

   public HologramOptimizerModule(EventBus eventBus) {
      super(eventBus, "hologram_optimizer", "Hologram Optimizer", "Оптимизация рендера голограмм", ModuleCategory.valueOf("MISC"));
      instance = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Оптимизация рендера голограмм")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      this.distance = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 128.0)
            .defaultValue(48.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Distance")
            .id("distance")
            .description("Макс. дистанция рендера голограмм"))
         .build();
      this.registerSetting(this.distance);
      this.quality = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 4.0)
            .defaultValue(4.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Quality")
            .id("quality")
            .description("Кратность супер-сэмплинга"))
         .build();
      this.registerSetting(this.quality);
   }

   @Override
   protected void initialize() {
      this.listen(WorldSessionEvent.class, event -> HologramImpostorRenderer.clear());
   }

   public static boolean isEnabled() {
      return isActive2();
   }

   public static boolean isActive2() {
      HologramOptimizerModule module = instance;
      return module != null && module.enabledSetting.isEnabled();
   }

   public static double maxDistanceSquared() {
      HologramOptimizerModule module = instance;
      if (module == null) {
         return Double.MAX_VALUE;
      } else {
         double distance = module.distance.getValue();
         return distance * distance;
      }
   }

   public static int qualityScale() {
      HologramOptimizerModule module = instance;
      if (module == null) {
         return 2;
      } else {
         int quality = (int)Math.round(module.quality.getValue());
         return Math.max(1, Math.min(4, quality));
      }
   }
}
