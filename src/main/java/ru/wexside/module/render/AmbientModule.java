package ru.wexside.module.render;

import net.minecraft.class_2761;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public class AmbientModule extends Module implements ConfigSerializable {
   private static final String SHADER = "Shader";
   private static final String DEFAULT = "Default";
   private static final String COLOR = "Color";
   private static final double TIME_LERP = 0.15;
   private static final String[] SKY_SHADERS = new String[]{
      "Water", "Silk", "Aurora", "Nebula", "Milky Veil", "Plasma", "Frost", "Ember", "Pulse", "Sky Stripes"
   };
   private static volatile AmbientModule instance;
   private final BooleanSetting enabledSetting;
   private final ModeSetting skyMode;
   private final ColorSetting skyColor;
   private final ModeSetting skyShader;
   private final NumberSetting skyIntensity;
   private final NumberSetting skySpeed;
   private final BooleanSetting fog;
   private final ColorSetting fogColor;
   private final NumberSetting fogRadius;
   private final BooleanSetting changeTimeOfDay;
   private final NumberSetting timeOfDay;
   private double smoothedTime;
   private boolean timeInitialized;

   public AmbientModule(EventBus eventBus) {
      super(eventBus, "ambient", "Ambient", "Настройка окружения", ModuleCategory.valueOf("RENDER"));
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
      this.skyMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Default", "Color", "Shader")
            .defaultOption("Default")
            .name("Sky mode")
            .id("sky_mode")
            .description("Режим неба")
            .aliases("sky", "небо"))
         .build();
      this.registerSetting(this.skyMode);
      ColorSetting sky = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(0)
            .name("Sky color")
            .id("sky_color")
            .description("Цвет неба")
            .aliases("sky color", "цвет неба")
            .visibleWhen(() -> !"Default".equals(this.skyMode.getSelectedOption())))
         .build();
      sky.setPrimaryColor(0, -11108117);
      sky.setPrimaryColor(1, -1543135);
      sky.setPrimaryColor(2, -9279489);
      sky.setPrimaryColor(3, -46001);
      sky.setPrimaryColor(4, -13218);
      sky.setPrimaryColor(5, -10582785);
      sky.setPrimaryColor(6, -2732032);
      this.skyColor = sky;
      this.registerSetting(sky);
      this.skyShader = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Water", "Silk", "Aurora", "Nebula", "Milky Veil", "Plasma", "Frost", "Ember", "Pulse", "Sky Stripes")
            .defaultOption("Water")
            .name("Sky shader")
            .id("sky_shader")
            .description("Шейдер неба")
            .aliases("sky shader", "шейдер неба")
            .visibleWhen(() -> "Shader".equals(this.skyMode.getSelectedOption())))
         .build();
      this.registerSetting(this.skyShader);
      this.skyIntensity = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 100.0)
            .defaultValue(25.0)
            .multiplier(0.02)
            .precision(0)
            .animationSpeed(20.0F)
            .markers(25.0)
            .snapTo(25.0)
            .name("Sky intensity")
            .id("sky_intensity")
            .description("Интенсивность шейдера неба")
            .aliases("sky intensity", "интенсивность")
            .visibleWhen(() -> "Shader".equals(this.skyMode.getSelectedOption())))
         .build();
      this.registerSetting(this.skyIntensity);
      this.skySpeed = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 10.0)
            .defaultValue(1.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Sky speed")
            .id("sky_speed")
            .description("Скорость анимации шейдера неба")
            .aliases("sky speed", "скорость")
            .visibleWhen(() -> "Shader".equals(this.skyMode.getSelectedOption())))
         .build();
      this.registerSetting(this.skySpeed);
      this.fog = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Change fog")
            .id("fog")
            .description("Изменить туман")
            .aliases("fog", "туман"))
         .build();
      this.registerSetting(this.fog);
      ColorSetting fogSetting = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(0)
            .name("Fog color")
            .id("fog_color")
            .description("Цвет тумана")
            .aliases("fog color", "цвет тумана")
            .visibleWhen(this.fog::isEnabled))
         .build();
      fogSetting.setPrimaryColor(0, -4270357);
      fogSetting.setPrimaryColor(1, -1543135);
      fogSetting.setPrimaryColor(2, -9279489);
      fogSetting.setPrimaryColor(3, -46001);
      fogSetting.setPrimaryColor(4, -13218);
      fogSetting.setPrimaryColor(5, -10582785);
      fogSetting.setPrimaryColor(6, -2732032);
      this.fogColor = fogSetting;
      this.registerSetting(fogSetting);
      this.fogRadius = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 12.0)
            .defaultValue(4.0)
            .multiplier(16.0)
            .precision(0)
            .animationSpeed(20.0F)
            .markers(1.0)
            .name("Fog radius")
            .id("fog_radius")
            .description("Радиус тумана")
            .aliases("fog radius", "радиус тумана")
            .visibleWhen(this.fog::isEnabled))
         .build();
      this.registerSetting(this.fogRadius);
      this.changeTimeOfDay = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Change time of day")
            .id("change_time_of_day")
            .description("Изменить время суток"))
         .build();
      this.registerSetting(this.changeTimeOfDay);
      this.timeOfDay = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 23.0)
            .defaultValue(6.0)
            .multiplier(1000.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Time of day")
            .id("time_of_day")
            .description("Время суток")
            .visibleWhen(this.changeTimeOfDay::isEnabled))
         .build();
      this.registerSetting(this.timeOfDay);
   }

   @Override
   protected void initialize() {
      this.listen(IncomingPacketEvent.class, event -> {
         if (this.customTimeEnabled()) {
            if (event.getPacket() instanceof class_2761) {
               event.update();
            }
         }
      });
      this.listen(ClientTickEvent.class, event -> {
         if (!this.customTimeEnabled()) {
            this.timeInitialized = false;
         } else {
            double target = this.timeOfDay.getValue();
            if (!this.timeInitialized) {
               this.smoothedTime = target;
               this.timeInitialized = true;
            } else {
               this.smoothedTime += (target - this.smoothedTime) * 0.15;
            }
         }
      });
   }

   public static int getIntType() {
      AmbientModule module = instance;
      return module == null ? -16777216 : module.skyColor.getPrimaryColor() | 0xFF000000;
   }

   public static int getIntType2() {
      AmbientModule module = instance;
      return module == null ? -1 : module.fogColor.getColor();
   }

   public static boolean isEnabled() {
      AmbientModule module = instance;
      return module != null && module.enabledSetting.isEnabled() && "Shader".equals(module.skyMode.getSelectedOption());
   }

   public static boolean isEnabled2() {
      AmbientModule module = instance;
      return module != null && module.customTimeEnabled();
   }

   public static double getDoubleType() {
      AmbientModule module = instance;
      return module == null ? 224.0 : module.fogRadius.getValue();
   }

   public static long getLongType() {
      AmbientModule module = instance;
      return module == null ? 0L : (long)(module.timeInitialized ? module.smoothedTime : module.timeOfDay.getValue());
   }

   public static boolean isEnabled4() {
      AmbientModule module = instance;
      return module != null && module.enabledSetting.isEnabled() && module.fog.isEnabled();
   }

   public static boolean isEnabled5() {
      AmbientModule module = instance;
      if (module != null && module.enabledSetting.isEnabled()) {
         String selected = module.skyMode.getSelectedOption();
         return "Color".equals(selected) || "Shader".equals(selected);
      } else {
         return false;
      }
   }

   private boolean customTimeEnabled() {
      return this.enabledSetting.isEnabled() && this.changeTimeOfDay.isEnabled();
   }

   private int shaderIndex() {
      String selected = this.skyShader.getSelectedOption();

      for(int i = 0; i < SKY_SHADERS.length; ++i) {
         if (SKY_SHADERS[i].equals(selected)) {
            return i;
         }
      }

      return 0;
   }
}
