package ru.wexside.module.render;

import java.util.LinkedHashMap;
import java.util.Map;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.TotemPopEvent;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.TotemEffectComposite;
import ru.wexside.misc.TotemEffectRenderer;
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
import ru.wexside.util.Snowflake;
import ru.wexside.util.TotemEffectSettings;
import ru.wexside.util.TotemGhostRenderer;
import ru.wexside.util.TotemHologramRenderer;

public final class TotemEffectsModule extends Module implements ConfigSerializable {
   private static final String HOLOGRAM = "Hologram";
   private static final String PARTICLES = "Particles";
   private static final String GHOST = "Ghost";
   private static final String BOTH = "Both";
   static volatile TotemEffectsModule instance;
   private final BooleanSetting enabledSetting;
   private final ColorSetting color;
   private final ModeSetting mode;
   private final ModeSetting particleType;
   private final NumberSetting particleCount;
   private final BooleanSetting physics;
   private final NumberSetting speed;
   private final NumberSetting lifeTime;
   private final TotemEffectSettings settings;
   private final TotemEffectRenderer hologram;
   private final TotemEffectRenderer particles;
   private final TotemEffectRenderer ghost;
   private final TotemEffectRenderer both;
   private final Map<String, TotemEffectRenderer> effects;
   private String lastMode;

   public TotemEffectsModule(EventBus eventBus) {
      super(eventBus, "totem_effects", "Totem Effects", "Эффекты при срабатывании тотема у игроков", ModuleCategory.valueOf("RENDER"));
      instance = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Включить эффекты при срабатывании тотема")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      ColorSetting colorSetting = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(0)
            .name("Color")
            .id("color")
            .description("Цвет эффекта")
            .aliases("color", "цвет"))
         .build();
      colorSetting.setPrimaryColor(0, -11753627);
      colorSetting.setPrimaryColor(1, -1543135);
      colorSetting.setPrimaryColor(2, -9279489);
      colorSetting.setPrimaryColor(3, -46001);
      colorSetting.setPrimaryColor(4, -13218);
      colorSetting.setPrimaryColor(5, -10582785);
      colorSetting.setPrimaryColor(6, -2732032);
      this.color = colorSetting;
      this.registerSetting(colorSetting);
      this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Hologram", "Particles", "Ghost", "Both")
            .defaultOption("Hologram")
            .name("Mode")
            .id("mode")
            .description("Тип эффекта"))
         .build();
      this.registerSetting(this.mode);
      this.particleType = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Star", "Mini-star", "Snowflake", "Dollar", "Cross")
            .defaultOption("Star")
            .name("Particle type")
            .id("particle_type")
            .description("Тип частиц")
            .visibleWhen(this::particlesVisible))
         .build();
      this.registerSetting(this.particleType);
      this.particleCount = ((NumberSettingBuilder)NumberSetting.builder()
            .range(10.0, 100.0)
            .defaultValue(50.0)
            .multiplier(0.5)
            .precision(0)
            .animationSpeed(20.0F)
            .snapTo(10.0)
            .name("Particles count")
            .id("particle_count")
            .description("Количество частиц")
            .visibleWhen(this::particlesVisible))
         .build();
      this.registerSetting(this.particleCount);
      this.physics = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Physics")
            .id("physics")
            .description("Физика частиц (отскок от блоков, гравитация)")
            .aliases("physics", "физика")
            .visibleWhen(this::particlesVisible))
         .build();
      this.registerSetting(this.physics);
      this.speed = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 5.0)
            .defaultValue(2.0)
            .multiplier(0.025)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Speed")
            .id("speed")
            .description("Скорость частиц")
            .aliases("speed", "скорость")
            .visibleWhen(this::particlesVisible))
         .build();
      this.registerSetting(this.speed);
      this.lifeTime = ((NumberSettingBuilder)NumberSetting.builder()
            .range(10.0, 100.0)
            .defaultValue(40.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .snapTo(10.0)
            .name("Life time")
            .id("life_time")
            .description("Время жизни частиц")
            .visibleWhen(this::particlesVisible))
         .build();
      this.registerSetting(this.lifeTime);
      this.settings = new TotemEffectSettings(this.color, this.particleType, this.particleCount, this.physics, this.speed, this.lifeTime);
      this.hologram = new TotemHologramRenderer(this.settings);
      this.particles = new Snowflake(this.settings);
      this.ghost = new TotemGhostRenderer(this.settings);
      this.both = new TotemEffectComposite(this.hologram, this.particles, this.ghost);
      this.effects = this.buildEffects();
   }

   @Override
   protected void initialize() {
      this.listen(TotemPopEvent.class, this::onTotemPop);
      this.listen(WorldRenderEvent.class, this::onWorldRender);
      this.listen(WorldSessionEvent.class, event -> this.clearEffects());
   }

   public static boolean isEnabled() {
      TotemEffectsModule module = instance;
      return module != null && module.enabledSetting.isEnabled();
   }

   private void onTotemPop(TotemPopEvent event) {
      if (this.enabledSetting.isEnabled()) {
         this.activeEffect().setTotemPopEvent(event);
      }
   }

   private void onWorldRender(WorldRenderEvent event) {
      if (!this.enabledSetting.isEnabled()) {
         this.clearEffects();
      } else {
         this.syncMode();
         this.activeEffect().render(event);
      }
   }

   private Map<String, TotemEffectRenderer> buildEffects() {
      LinkedHashMap<String, TotemEffectRenderer> map = new LinkedHashMap<>();
      map.put("Hologram", this.hologram);
      map.put("Particles", this.particles);
      map.put("Ghost", this.ghost);
      map.put("Both", this.both);
      return map;
   }

   private TotemEffectRenderer activeEffect() {
      return this.effects.getOrDefault(this.mode.getSelectedOption(), this.hologram);
   }

   private void syncMode() {
      String current = this.mode.getSelectedOption();
      if (current != null) {
         if (this.lastMode == null) {
            this.lastMode = current;
         } else {
            if (!current.equals(this.lastMode)) {
               this.clearEffects();
               this.lastMode = current;
            }
         }
      }
   }

   private boolean particlesVisible() {
      if (!this.enabledSetting.isEnabled()) {
         return false;
      } else {
         String current = this.mode.getSelectedOption();
         return "Particles".equals(current) || "Both".equals(current);
      }
   }

   private void clearEffects() {
      this.effects.values().forEach(TotemEffectRenderer::update2);
   }
}
