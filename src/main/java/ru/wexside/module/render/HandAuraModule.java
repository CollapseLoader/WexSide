package ru.wexside.module.render;

import net.minecraft.class_1268;
import net.minecraft.class_4587;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.HandRenderEvent;
import ru.wexside.event.HandRenderPhase;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.render.hand.EnergyAuraMode;
import ru.wexside.render.hand.HandAuraEffects;
import ru.wexside.render.hand.HandMaterialMode;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class HandAuraModule extends Module implements ConfigSerializable {
   static final int ENERGY_COLOR_PRESET = -1125978881;
   static final int MATERIAL_COLOR_PRESET = -1427968769;
   private static volatile HandAuraModule instance;
   private final BooleanSetting enabledSetting;
   private final BooleanSetting energy;
   private final ModeSetting energyMode;
   private final ColorSetting energyColor;
   private final NumberSetting energyRadius;
   private final BooleanSetting fillHands;
   private final BooleanSetting material;
   private final ModeSetting materialMode;
   private final ColorSetting materialColor;
   private final NumberSetting materialRadius;
   private boolean lastEnabledState;

   public HandAuraModule(EventBus eventBus) {
      super(eventBus, "hand_aura", "Hand Aura", "Эффекты на руках", ModuleCategory.valueOf("RENDER"));
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
      this.energy = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Effect")
            .id("energy")
            .description("Энергетическая аура вокруг рук"))
         .build();
      this.registerSetting(this.energy);
      this.energyMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Static", "Hand Aura", "Ribbons")
            .defaultOption("Static")
            .name("Effect mode")
            .id("energy_mode")
            .description("Режим энергетического эффекта")
            .visibleWhen(this.energy::isEnabled))
         .build();
      this.registerSetting(this.energyMode);
      ColorSetting energyColorSetting = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(0)
            .name("Effect color")
            .id("energy_color")
            .description("Цвет эффекта")
            .visibleWhen(this.energy::isEnabled))
         .build();
      energyColorSetting.setPrimaryColor(0, -1125978881);
      energyColorSetting.setPrimaryColor(1, -1543135);
      energyColorSetting.setPrimaryColor(2, -9279489);
      energyColorSetting.setPrimaryColor(3, -46001);
      energyColorSetting.setPrimaryColor(4, -13218);
      energyColorSetting.setPrimaryColor(5, -10582785);
      energyColorSetting.setPrimaryColor(6, -2732032);
      this.energyColor = energyColorSetting;
      this.registerSetting(energyColorSetting);
      this.energyRadius = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.5, 2.0)
            .defaultValue(1.0)
            .multiplier(1.0)
            .precision(1)
            .animationSpeed(20.0F)
            .name("Energy radius")
            .id("energy_radius")
            .description("Радиус эффекта вокруг предметов")
            .visibleWhen(this.energy::isEnabled))
         .build();
      this.registerSetting(this.energyRadius);
      this.fillHands = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Fill hands")
            .id("fill_hands")
            .description("Накладывать эффект поверх рук")
            .visibleWhen(this.energy::isEnabled))
         .build();
      this.registerSetting(this.fillHands);
      this.material = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Material")
            .id("material")
            .description("Текстурировать руку"))
         .build();
      this.registerSetting(this.material);
      this.materialMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Glass Hand", "Liquid Glass", "Chrome", "Simple", "Fill")
            .defaultOption("Glass Hand")
            .name("Material mode")
            .id("material_mode")
            .description("Режим текстуры")
            .visibleWhen(this.material::isEnabled))
         .build();
      this.registerSetting(this.materialMode);
      ColorSetting materialColorSetting = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(0)
            .name("Material color")
            .id("material_color")
            .description("Цвет накладываемой текстуры")
            .visibleWhen(this.material::isEnabled))
         .build();
      materialColorSetting.setPrimaryColor(0, -1427968769);
      materialColorSetting.setPrimaryColor(1, -1543135);
      materialColorSetting.setPrimaryColor(2, -9279489);
      materialColorSetting.setPrimaryColor(3, -46001);
      materialColorSetting.setPrimaryColor(4, -13218);
      materialColorSetting.setPrimaryColor(5, -10582785);
      materialColorSetting.setPrimaryColor(6, -2732032);
      this.materialColor = materialColorSetting;
      this.registerSetting(materialColorSetting);
      this.materialRadius = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.6, 1.8)
            .defaultValue(1.0)
            .multiplier(1.0)
            .precision(2)
            .animationSpeed(20.0F)
            .name("Material radius")
            .id("material_radius")
            .description("Радиус материального эффекта")
            .visibleWhen(this.material::isEnabled))
         .build();
      this.registerSetting(this.materialRadius);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.onToggle());
      this.listen(WorldSessionEvent.class, event -> this.onWorldChange());
      this.listen(HandRenderEvent.class, this::onHandRender);
   }

   public static HandAuraModule getInstance() {
      return instance;
   }

   public static boolean isModuleEnabled() {
      HandAuraModule module = instance;
      return module != null && module.enabledSetting.isEnabled();
   }

   public void applyEnergyEffect(class_1268 hand, Object matrices) {
      if (this.enabledSetting.isEnabled() && this.isEnergyEnabled() && matrices instanceof class_4587 matrixStack) {
         HandAuraEffects.applyHandEnergy(hand, matrixStack);
      }
   }

   private void onToggle() {
      boolean enabled = this.enabledSetting.isEnabled();
      if (enabled != this.lastEnabledState) {
         this.lastEnabledState = enabled;
         if (enabled) {
            HandAuraEffects.reset();
            this.syncRenderers();
         } else {
            HandAuraEffects.disableAll();
            HandAuraEffects.reset();
         }
      }
   }

   private void onWorldChange() {
      if (this.enabledSetting.isEnabled()) {
         HandAuraEffects.reset();
         this.syncRenderers();
      }
   }

   private void onHandRender(HandRenderEvent event) {
      if (this.enabledSetting.isEnabled()) {
         this.syncRenderers();
         if (event.phase() == HandRenderPhase.BEFORE) {
            HandAuraEffects.beforeHandRender(event.matrices(), event.tickDelta());
         } else {
            HandAuraEffects.afterHandRender(event.matrices());
         }
      }
   }

   private void syncRenderers() {
      boolean energyEnabled = this.isEnergyEnabled();
      boolean materialEnabled = this.isMaterialEnabled();
      boolean energyGradient = this.energyColor.isAstolfoMode() || this.energyColor.isDoubleColorMode();
      HandAuraEffects.configureEnergy(
         energyEnabled,
         this.resolveEnergyMode(),
         this.energyColor.getColor(0.0F),
         energyGradient ? this.energyColor.getColor(0.5F) : this.energyColor.getColor(0.0F),
         energyGradient,
         (float)this.energyRadius.getValue(),
         this.fillHands.isEnabled()
      );
      if (!materialEnabled) {
         HandAuraEffects.configureMaterial(false, this.resolveMaterialMode(), 0.0F, 0, 0, false);
      } else {
         boolean materialGradient = this.materialColor.isAstolfoMode() || this.materialColor.isDoubleColorMode();
         HandAuraEffects.configureMaterial(
            true,
            this.resolveMaterialMode(),
            (float)this.materialRadius.getValue(),
            this.materialColor.getColor(0.0F),
            materialGradient ? this.materialColor.getColor(0.5F) : this.materialColor.getColor(0.0F),
            materialGradient
         );
      }
   }

   private boolean isEnergyEnabled() {
      return this.enabledSetting.isEnabled() && this.energy.isEnabled();
   }

   private boolean isMaterialEnabled() {
      return this.enabledSetting.isEnabled() && this.material.isEnabled();
   }

   private EnergyAuraMode resolveEnergyMode() {
      return HandAuraEffects.resolveEnergyMode(this.energyMode.getSelectedOption());
   }

   private HandMaterialMode resolveMaterialMode() {
      return HandAuraEffects.resolveMaterialMode(this.materialMode.getSelectedOption());
   }
}
