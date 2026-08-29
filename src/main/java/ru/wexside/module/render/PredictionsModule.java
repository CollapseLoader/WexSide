package ru.wexside.module.render;

import java.util.function.BooleanSupplier;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.util.PredictionsRenderer;

public final class PredictionsModule extends Module implements ConfigSerializable {
   public static final float ARROW_MARKER_SCALE = 0.3F;
   public static final float LANDING_TIME_PADDING = 0.1F;
   public static final double ENTITY_HIT_DISTANCE = 3.0;
   public static final float TRAJECTORY_WIDTH = 1.75F;
   public static final float IMPACT_RING_SCALE = 0.2F;
   public static final int LIVING_IMPACT_MARKER = -45747;
   private final BooleanSetting enabledSetting;
   private final BooleanSetting heldItems;
   private final BooleanSetting trident;
   private final BooleanSetting selfTrident;
   private final ColorSetting selfTridentColor;
   private final BooleanSetting enemiesTrident;
   private final ColorSetting enemiesTridentColor;
   private final BooleanSetting pearl;
   private final BooleanSetting selfPearl;
   private final ColorSetting selfPearlColor;
   private final BooleanSetting enemiesPearl;
   private final ColorSetting enemiesPearlColor;
   private final BooleanSetting arrow;
   private final BooleanSetting selfArrow;
   private final ColorSetting selfArrowColor;
   private final BooleanSetting enemiesArrow;
   private final ColorSetting enemiesArrowColor;
   private final ModeSetting arrowStyle;
   private final BooleanSetting crossbow;
   private final BooleanSetting selfCrossbow;
   private final ColorSetting selfCrossbowColor;
   private final BooleanSetting enemiesCrossbow;
   private final ColorSetting enemiesCrossbowColor;
   private final BooleanSetting potions;
   private final ColorSetting potionsColor;
   private final BooleanSetting items;
   private final ColorSetting itemsColor;
   private final BooleanSetting impactMarker;
   private final ColorSetting impactColor;
   private final BooleanSetting landingTime;
   private final BooleanSetting entityHitBox;
   private final PredictionsRenderer renderer = new PredictionsRenderer(this);

   public PredictionsModule(EventBus eventBus) {
      super(eventBus, "prediction_preview", "Predictions", "Предпросмотр траекторий снарядов и точек попадания", ModuleCategory.valueOf("RENDER"));
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Включить предпросмотр снарядов")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      this.heldItems = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Held items")
            .id("held_items")
            .description("Показывать траекторию предмета в руке")
            .aliases("held items", "рука"))
         .build();
      this.registerSetting(this.heldItems);
      this.trident = this.registerCategoryToggle("trident", "Trident", "Предпоказ траектории трезубца", "trident", "трезубец");
      this.selfTrident = this.registerSelfToggle(
         "self_trident", "Self trident", "Отоброжать траекторию полета своего трезубца", "self trident", this::isTridentEnabled
      );
      this.selfTridentColor = this.registerColor("self_trident_color", "Self trident color", "Цвет траектории полета своего трезубца", 5, this::showSelfTrident);
      this.enemiesTrident = this.registerEnemyToggle(
         "enemies_trident", "Enemies trident", "Цвет траектории полета вражеского трезубца", "enemies trident", this::isTridentEnabled
      );
      this.enemiesTridentColor = this.registerColor("enemies_trident_color", "Enemy trident color", "", 3, this::showEnemyTrident);
      this.pearl = this.registerCategoryToggle("pearl", "Pearl", "Предпоказ траектории жемчуга", null, null);
      this.selfPearl = this.registerSelfToggle("self_pearl", "Self pearl", "Отображать траекторию полета своего жемчуга", "self pearl", this::isPearlEnabled);
      this.selfPearlColor = this.registerColor("self_pearl_color", "Self pearl color", "Цвет траектории полета своего жемчуга", 5, this::showSelfPearl);
      this.enemiesPearl = this.registerEnemyToggle(
         "enemies_pearl", "Enemies pearl", "Отображать траекторию полета вражеского жемчуга", "enemies pearl", this::isPearlEnabled
      );
      this.enemiesPearlColor = this.registerColor(
         "enemies_pearl_color", "Enemy pearl color", "Цвет траектории полета вражеского жемчуга", 3, this::showEnemyPearl
      );
      this.arrow = this.registerCategoryToggle("arrow", "Arrow", "Предпоказ траектории стрел", null, null);
      this.selfArrow = this.registerSelfToggle("self_arrow", "Self arrow", "Отображать траекторию полета своих стрел", "self arrow", this::isArrowEnabled);
      this.selfArrowColor = this.registerColor("self_arrow_color", "Self arrow color", "Цвет траектории полета своих стрел", 4, this::showSelfArrow);
      this.enemiesArrow = this.registerEnemyToggle(
         "enemies_arrow", "Enemies arrow", "Отображать траекторию полета вражеских стрел", "enemies arrow", this::isArrowEnabled
      );
      this.enemiesArrowColor = this.registerColor("enemies_arrow_color", "Enemy arrow color", "Цвет траектории полета вражеских стрел", 3, this::showEnemyArrow);
      this.arrowStyle = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Classic", "Model")
            .defaultOption("Classic")
            .name("Arrow Style")
            .id("arrow_style")
            .description("Вид точки попадания стрелы/арбалета")
            .visibleWhen(() -> this.isArrowEnabled() || this.isCrossbowEnabled()))
         .build();
      this.registerSetting(this.arrowStyle);
      this.crossbow = this.registerCategoryToggle("crossbow", "Crossbow", "Предпоказ траектории стрел из арбалета", "crossbow", "арбалет");
      this.selfCrossbow = this.registerSelfToggle(
         "self_crossbow", "Self crossbow", "Отображать траекторию полета своих стрел из арбалета", "self crossbow", this::isCrossbowEnabled
      );
      this.selfCrossbowColor = this.registerColor(
         "self_crossbow_color", "Self crossbow color", "Цвет траектории полета своих стрел из арбалета", 5, this::showSelfCrossbow
      );
      this.enemiesCrossbow = this.registerEnemyToggle(
         "enemies_crossbow", "Enemies crossbow", "Отображать траекторию полета вражеских стрел из арбалета", "enemies crossbow", this::isCrossbowEnabled
      );
      this.enemiesCrossbowColor = this.registerColor(
         "enemies_crossbow_color", "Enemy crossbow color", "Цвет траектории полета вражеских стрел из арбалета", 3, this::showEnemyCrossbow
      );
      this.potions = this.registerCategoryToggle("potions", "Potions", "Предпоказ траектории зелий", "potions", "зелья");
      this.potionsColor = this.registerColor("potions_color", "Potions color", "Цвет траектории полета зелий", 2, this::isPotionsEnabled);
      this.items = this.registerCategoryToggle("items", "Items", "Предпоказ траектории предметов", "items", "предметы");
      this.itemsColor = this.registerColor("items_color", "Items color", "Цвет траектории полета предметов", 0, this::isItemsEnabled);
      this.impactMarker = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Impact marker")
            .id("impact_marker")
            .description("Отображать маркер места попадания")
            .aliases("impact marker", "маркер"))
         .build();
      this.registerSetting(this.impactMarker);
      this.impactColor = this.registerColor("impact_color", "Impact color", "Цвет места попадания", 0, this::showImpactMarker);
      this.landingTime = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Landing time")
            .id("landing_time")
            .description("Плашка со временем до приземления снаряда"))
         .build();
      this.registerSetting(this.landingTime);
      this.entityHitBox = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Entity Hit Box")
            .id("entity_hit_box")
            .description("Бокс на сущности, в которую попадает траектория"))
         .build();
      this.registerSetting(this.entityHitBox);
   }

   @Override
   protected void initialize() {
      this.listen(WorldRenderEvent.class, this.renderer::onWorldRender);
      this.listen(HudRenderEvent.class, this.renderer::onHudRender);
      this.listen(WorldSessionEvent.class, event -> this.renderer.resetSession());
   }

   public boolean isModuleEnabled() {
      return this.enabledSetting.isEnabled();
   }

   public boolean showLandingTime() {
      return this.landingTime.isEnabled();
   }

   public boolean showImpactMarker() {
      return this.impactMarker.isEnabled();
   }

   public int getImpactColorArgb() {
      return this.impactColor.getColor();
   }

   public boolean showEntityHitBox() {
      return this.entityHitBox.isEnabled();
   }

   public boolean showHeldItems() {
      return this.heldItems.isEnabled();
   }

   public boolean isClassicArrowStyle() {
      return "Classic".equals(this.arrowStyle.getSelectedOption());
   }

   public boolean isTridentEnabled() {
      return this.trident.isEnabled();
   }

   public boolean isPearlEnabled() {
      return this.pearl.isEnabled();
   }

   public boolean isArrowEnabled() {
      return this.arrow.isEnabled();
   }

   public boolean isCrossbowEnabled() {
      return this.crossbow.isEnabled();
   }

   public boolean isPotionsEnabled() {
      return this.potions.isEnabled();
   }

   public boolean isItemsEnabled() {
      return this.items.isEnabled();
   }

   public boolean showSelfTrident() {
      return this.isTridentEnabled() && this.selfTrident.isEnabled();
   }

   public boolean showEnemyTrident() {
      return this.isTridentEnabled() && this.enemiesTrident.isEnabled();
   }

   public boolean showSelfPearl() {
      return this.isPearlEnabled() && this.selfPearl.isEnabled();
   }

   public boolean showEnemyPearl() {
      return this.isPearlEnabled() && this.enemiesPearl.isEnabled();
   }

   public boolean showSelfArrow() {
      return this.isArrowEnabled() && this.selfArrow.isEnabled();
   }

   public boolean showEnemyArrow() {
      return this.isArrowEnabled() && this.enemiesArrow.isEnabled();
   }

   public boolean showSelfCrossbow() {
      return this.isCrossbowEnabled() && this.selfCrossbow.isEnabled();
   }

   public boolean showEnemyCrossbow() {
      return this.isCrossbowEnabled() && this.enemiesCrossbow.isEnabled();
   }

   public boolean isCategoryEnabled(PredictionsModule.ProjectileKind kind) {
      return switch(kind.ordinal()) {
         case 0 -> this.isTridentEnabled();
         case 1 -> this.isPearlEnabled();
         case 2 -> this.isArrowEnabled();
         case 3 -> this.isCrossbowEnabled();
         case 4 -> this.isPotionsEnabled();
         case 5 -> this.isItemsEnabled();
         case 6 -> false;
         default -> throw new MatchException(null, null);
      };
   }

   public boolean isHeldItemCategoryEnabled(PredictionsModule.ProjectileKind kind) {
      return kind == PredictionsModule.ProjectileKind.ITEM ? false : this.isCategoryEnabled(kind);
   }

   public boolean shouldShowTrajectory(PredictionsModule.ProjectileKind kind, boolean self) {
      return switch(kind.ordinal()) {
         case 0 -> this.isTridentEnabled() && (self ? this.showSelfTrident() : this.showEnemyTrident());
         case 1 -> this.isPearlEnabled() && (self ? this.showSelfPearl() : this.showEnemyPearl());
         case 2 -> this.isArrowEnabled() && (self ? this.showSelfArrow() : this.showEnemyArrow());
         case 3 -> this.isCrossbowEnabled() && (self ? this.showSelfCrossbow() : this.showEnemyCrossbow());
         case 4, 5, 6 -> false;
         default -> throw new MatchException(null, null);
      };
   }

   public int getTrajectoryColor(PredictionsModule.ProjectileKind kind, boolean self) {
      return switch(kind.ordinal()) {
         case 0 -> (self ? this.selfTridentColor : this.enemiesTridentColor).getColor();
         case 1 -> (self ? this.selfPearlColor : this.enemiesPearlColor).getColor();
         case 2 -> (self ? this.selfArrowColor : this.enemiesArrowColor).getColor();
         case 3 -> (self ? this.selfCrossbowColor : this.enemiesCrossbowColor).getColor();
         case 4 -> this.potionsColor.getColor();
         case 5 -> this.itemsColor.getColor();
         case 6 -> -1;
         default -> throw new MatchException(null, null);
      };
   }

   public class_1799 getTrackedHeldStack(class_1657 player) {
      return PredictionsRenderer.getTrackedHeldStack(this, player);
   }

   private BooleanSetting registerCategoryToggle(String id, String name, String description, String alias1, String alias2) {
      BooleanSettingBuilder builder = (BooleanSettingBuilder)BooleanSetting.builder()
         .value(true)
         .defaultValue(false)
         .name(name)
         .id(id)
         .description(description);
      if (alias1 != null) {
         builder = (BooleanSettingBuilder)builder.aliases(new String[]{alias1, alias2});
      }

      BooleanSetting setting = builder.build();
      this.registerSetting(setting);
      return setting;
   }

   private BooleanSetting registerSelfToggle(String id, String name, String description, String alias, BooleanSupplier parent) {
      BooleanSetting setting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name(name)
            .id(id)
            .description(description)
            .aliases(alias)
            .visibleWhen(() -> parent.getAsBoolean()))
         .build();
      this.registerSetting(setting);
      return setting;
   }

   private BooleanSetting registerEnemyToggle(String id, String name, String description, String alias, BooleanSupplier parent) {
      BooleanSetting setting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name(name)
            .id(id)
            .description(description)
            .aliases(alias)
            .visibleWhen(() -> parent.getAsBoolean()))
         .build();
      this.registerSetting(setting);
      return setting;
   }

   private ColorSetting registerColor(String id, String name, String description, int defaultPreset, BooleanSupplier visible) {
      ColorSetting colorSetting = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(defaultPreset)
            .name(name)
            .id(id)
            .description(description)
            .visibleWhen(() -> visible.getAsBoolean()))
         .build();
      colorSetting.setPrimaryColor(0, -11753627);
      colorSetting.setPrimaryColor(1, -1543135);
      colorSetting.setPrimaryColor(2, -9279489);
      colorSetting.setPrimaryColor(3, -46001);
      colorSetting.setPrimaryColor(4, -13218);
      colorSetting.setPrimaryColor(5, -10582785);
      colorSetting.setPrimaryColor(6, -2732032);
      this.registerSetting(colorSetting);
      return colorSetting;
   }

   public static enum ProjectileKind {
      TRIDENT,
      PEARL,
      ARROW,
      CROSSBOW,
      POTION,
      ITEM,
      UNKNOWN;
   }
}
