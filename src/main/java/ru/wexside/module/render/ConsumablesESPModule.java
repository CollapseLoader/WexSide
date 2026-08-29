package ru.wexside.module.render;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.ConsumablesEspRenderer;
import ru.wexside.misc.ConsumablesEspSettings;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;

public final class ConsumablesESPModule extends Module implements ConfigSerializable {
   private static final String[] CONSUMABLE_TYPES = new String[]{"Трапка", "Пласт", "Дезориентация", "Явная пыль", "Снежок заморозка"};
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("Включить подсветку зон расходников")
         .withKeybind()
         .toggle())
      .build();
   private final BooleanSetting self;
   private final MultiSelectSetting selfTypes;
   private final ColorSetting selfColor;
   private final BooleanSetting enemies;
   private final MultiSelectSetting enemyTypes;
   private final ColorSetting enemyColor;
   private final ConsumablesEspSettings options;
   private final ConsumablesEspRenderer renderer;

   public ConsumablesESPModule(EventBus eventBus) {
      super(eventBus, "consumables_esp", "Consumables ESP", "Подсветка зон расходников", ModuleCategory.valueOf("RENDER"));
      this.registerSetting(this.enabledSetting);
      this.self = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Self")
            .id("self")
            .description("Отображать свои расходники"))
         .build();
      this.registerSetting(this.self);
      MultiSelectSetting selfTypesSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("Трапка", "Пласт", "Дезориентация", "Явная пыль", "Снежок заморозка")
            .selectAll(true)
            .optionListEnabled(false)
            .name("Self Types")
            .id("self_types")
            .description("Какие свои расходники отображать")
            .aliases("self types", "свои расходники")
            .visibleWhen(this.self::isEnabled))
         .build();
      selfTypesSetting.setOptions(CONSUMABLE_TYPES);
      this.selfTypes = selfTypesSetting;
      this.registerSetting(selfTypesSetting);
      ColorSetting selfColorSetting = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(5)
            .name("Self Color")
            .id("self_color")
            .description("Цвет своих расходников")
            .aliases("self color", "цвет своих")
            .visibleWhen(this.self::isEnabled))
         .build();
      this.applyPalette(selfColorSetting);
      this.selfColor = selfColorSetting;
      this.registerSetting(selfColorSetting);
      this.enemies = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Enemies")
            .id("enemies")
            .description("Отображать вражеские расходники"))
         .build();
      this.registerSetting(this.enemies);
      MultiSelectSetting enemyTypesSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("Трапка", "Пласт", "Дезориентация", "Явная пыль", "Снежок заморозка")
            .selectAll(true)
            .optionListEnabled(false)
            .name("Enemy Types")
            .id("enemy_types")
            .description("Какие вражеские расходники отображать")
            .aliases("enemy types", "вражеские расходники")
            .visibleWhen(this.enemies::isEnabled))
         .build();
      enemyTypesSetting.setOptions(CONSUMABLE_TYPES);
      this.enemyTypes = enemyTypesSetting;
      this.registerSetting(enemyTypesSetting);
      ColorSetting enemyColorSetting = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(3)
            .name("Enemy Color")
            .id("enemy_color")
            .description("Цвет вражеских расходников")
            .aliases("enemy color", "цвет вражеских")
            .visibleWhen(this.enemies::isEnabled))
         .build();
      this.applyPalette(enemyColorSetting);
      this.enemyColor = enemyColorSetting;
      this.registerSetting(enemyColorSetting);
      this.options = new ConsumablesEspSettings(this.enabledSetting, this.self, this.selfTypes, this.selfColor, this.enemies, this.enemyTypes, this.enemyColor);
      this.renderer = new ConsumablesEspRenderer(this.options);
   }

   @Override
   protected void initialize() {
      this.listen(WorldRenderEvent.class, event -> this.renderer.setWorldRenderEvent(event));
      this.listen(WorldSessionEvent.class, event -> this.renderer.update());
   }

   private void applyPalette(ColorSetting setting) {
      setting.setPrimaryColor(0, -11753627);
      setting.setPrimaryColor(1, -1543135);
      setting.setPrimaryColor(2, -9279489);
      setting.setPrimaryColor(3, -46001);
      setting.setPrimaryColor(4, -13218);
      setting.setPrimaryColor(5, -10582785);
      setting.setPrimaryColor(6, -2732032);
   }
}
