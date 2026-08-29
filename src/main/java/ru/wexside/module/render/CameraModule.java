package ru.wexside.module.render;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class CameraModule extends Module implements ConfigSerializable {
   private static final float CLIP_SCALE = 3.0F;
   private static final float SMOOTH_SPEED = 12.0F;
   private static volatile CameraModule instance;
   private final BooleanSetting enabledSetting;
   private final BooleanSetting clip;
   private final BooleanSetting smooth;
   private float smoothedDistance;

   public CameraModule(EventBus eventBus) {
      super(eventBus, "camera", "Camera", "Настройки камеры от третьего лица", ModuleCategory.valueOf("RENDER"), "camera", "камера");
      instance = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Включить настройки камеры")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      this.clip = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Clip").id("clip").description("Игнорировать стены"))
         .build();
      this.registerSetting(this.clip);
      this.smooth = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Smooth").id("smooth").description("Плавный режим"))
         .build();
      this.registerSetting(this.smooth);
   }

   @Override
   protected void initialize() {
   }

   public static boolean isEnabled2() {
      CameraModule module = instance;
      return module != null && module.enabledSetting.isEnabled();
   }

   public static boolean isEnabled3() {
      CameraModule module = instance;
      return module != null && module.enabledSetting.isEnabled() && module.smooth.isEnabled();
   }

   public static boolean isEnabled4() {
      CameraModule module = instance;
      return module != null && module.enabledSetting.isEnabled() && module.clip.isEnabled();
   }

   public static void tick() {
      CameraModule module = instance;
      if (module != null) {
         module.smoothedDistance = Float.NaN;
      }
   }

   public static float compute(float distance) {
      CameraModule module = instance;
      if (module == null) {
         return distance;
      } else {
         if (Float.isNaN(module.smoothedDistance)) {
            module.smoothedDistance = 0.0F;
         }

         module.smoothedDistance = FrameInterpolator.lerpTowards(module.smoothedDistance, distance, 12.0F);
         return Math.max(distance / 3.0F, module.smoothedDistance);
      }
   }
}
