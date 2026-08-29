package ru.wexside.util;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import ru.wexside.event.EventBus;
import ru.wexside.misc.BoxEspSettings;
import ru.wexside.misc.GlowEspSettings;
import ru.wexside.misc.ModelEspSettings;
import ru.wexside.misc.NameTagSettings;
import ru.wexside.misc.WorldBoxSettings;
import ru.wexside.model.esp.EspRelation;
import ru.wexside.model.esp.EspTargetType;
import ru.wexside.module.Module;
import ru.wexside.module.misc.EspFeatureModule;
import ru.wexside.render.ChamsSettings;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.setting.NumberUnit;

public final class EspFeatureRegistry {
   private final EnumMap<EspTargetType, EnumMap<EspRelation, BoxEspSettings>> box2dSettings;
   private final EnumMap<EspTargetType, EnumMap<EspRelation, WorldBoxSettings>> worldBoxSettings;
   private final EnumMap<EspRelation, GlowEspSettings> glowSettings;
   private final EnumMap<EspRelation, ModelEspSettings> modelEspSettings;
   private final EnumMap<EspTargetType, EnumMap<EspRelation, NameTagSettings>> nameTagSettings;
   private final EnumMap<EspRelation, ChamsSettings> chamsSettings;
   private final EnumMap<EspTargetType, EnumMap<EspRelation, List<Module>>> modulesByTarget;
   private final EventBus eventBus;
   private final List<Module> modules;

   public EspFeatureRegistry(EventBus eventBus) {
      this.eventBus = eventBus;
      this.box2dSettings = new EnumMap<>(EspTargetType.class);
      this.worldBoxSettings = new EnumMap<>(EspTargetType.class);
      this.glowSettings = new EnumMap<>(EspRelation.class);
      this.modelEspSettings = new EnumMap<>(EspRelation.class);
      this.nameTagSettings = new EnumMap<>(EspTargetType.class);
      this.chamsSettings = new EnumMap<>(EspRelation.class);
      this.modulesByTarget = new EnumMap<>(EspTargetType.class);
      this.modules = new ArrayList<>();

      for(EspTargetType targetType : EspTargetType.values()) {
         EnumMap<EspRelation, List<Module>> modulesByRelation = new EnumMap<>(EspRelation.class);
         EspRelation[] espRelationArray;
         if (targetType == EspTargetType.PLAYERS) {
            espRelationArray = EspRelation.values();
         } else {
            EspRelation[] espRelationArray2 = new EspRelation[1];
            espRelationArray = espRelationArray2;
            espRelationArray2[0] = EspRelation.DEFAULT;
         }

         for(EspRelation relation : espRelationArray) {
            List<Module> modules = List.copyOf(this.createFeatureModules(targetType, relation));
            modulesByRelation.put(relation, modules);
            this.modules.addAll(modules);
         }

         this.modulesByTarget.put(targetType, modulesByRelation);
      }
   }

   public WorldBoxSettings getWorldBoxSettings(EspTargetType espTargetType, EspRelation espRelation) {
      return this.getSettings(this.worldBoxSettings, espTargetType, espRelation);
   }

   public EspRelation getRelation(Module module) {
      for(EnumMap<EspRelation, List<Module>> enumMap : this.modulesByTarget.values()) {
         if (enumMap.getOrDefault(EspRelation.DEFAULT, List.of()).contains(module)) {
            return EspRelation.DEFAULT;
         }

         if (enumMap.getOrDefault(EspRelation.FRIEND, List.of()).contains(module)) {
            return EspRelation.FRIEND;
         }
      }

      return EspRelation.DEFAULT;
   }

   public ModelEspSettings getModelEspSettings(EspRelation espRelation) {
      return this.modelEspSettings.get(espRelation);
   }

   public List<Module> getModules() {
      return this.modules;
   }

   public List<Module> getDefaultModules() {
      ArrayList<Module> arrayList = new ArrayList<>();

      for(EnumMap<EspRelation, List<Module>> enumMap : this.modulesByTarget.values()) {
         for(Module module : enumMap.getOrDefault(EspRelation.DEFAULT, List.of())) {
            if (!arrayList.contains(module)) {
               arrayList.add(module);
            }
         }
      }

      return arrayList;
   }

   public boolean hasEnabledNameTags() {
      for(EnumMap<EspRelation, NameTagSettings> enumMap : this.nameTagSettings.values()) {
         for(NameTagSettings talisman : enumMap.values()) {
            if (talisman.isEnabled()) {
               return true;
            }
         }
      }

      return false;
   }

   private EspFeatureModule createGlowModule(EspTargetType espTargetType, EspRelation espRelation) {
      EspFeatureModule cls0919Module = new EspFeatureModule(
         this.eventBus,
         this.createModuleId(espTargetType, espRelation, "glow"),
         "Glow ESP",
         "Свечение-обводка игроков сквозь стены",
         this.getCategoryTitle(espTargetType, espRelation)
      );
      BooleanSetting booleanSetting = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder()
                     .id("enabled"))
                  .name("Enabled"))
               .description("Свечение-обводка игроков"))
            .withKeybind())
         .value(false)
         .build();
      ColorSetting colorSetting = ((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)ColorSetting.builder().id("player_color"))
               .name("Player color"))
            .description("Цвет свечения"))
         .build();
      NumberSetting numberSetting = ((NumberSettingBuilder)((NumberSettingBuilder)((NumberSettingBuilder)NumberSetting.builder().id("max_distance"))
               .name("Max distance"))
            .description("Максимальная дистанция отрисовки"))
         .range(4.0, 12.0)
         .defaultValue(8.0)
         .multiplier(16.0)
         .showMarkers()
         .formatter(NumberUnit.BLOCKS)
         .build();
      NumberSetting number = ((NumberSettingBuilder)((NumberSettingBuilder)((NumberSettingBuilder)NumberSetting.builder().id("radius")).name("Radius"))
            .description("Толщина свечения"))
         .range(8.0, 16.0)
         .defaultValue(12.0)
         .formatter(NumberUnit.PIXELS)
         .build();
      cls0919Module.setBooleanSetting(booleanSetting);
      cls0919Module.setSetting(colorSetting);
      cls0919Module.setSetting(numberSetting);
      cls0919Module.setSetting(number);
      this.glowSettings.put(espRelation, new GlowEspSettings(booleanSetting, colorSetting, numberSetting, number));
      return cls0919Module;
   }

   private EspFeatureModule createNameTagModule(EspTargetType espTargetType, EspRelation espRelation) {
      EspFeatureModule cls0919Module = new EspFeatureModule(
         this.eventBus,
         this.createModuleId(espTargetType, espRelation, "nametags"),
         "Name Tags",
         "Ники и информация над сущностями",
         this.getCategoryTitle(espTargetType, espRelation)
      );
      BooleanSetting booleanSetting = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder()
                     .id("enabled"))
                  .name("Enabled"))
               .description("Показывать ники над сущностями"))
            .withKeybind())
         .value(false)
         .build();
      cls0919Module.setBooleanSetting(booleanSetting);
      MultiSelectSetting multiSelectSetting = null;
      BooleanSetting toggle = null;
      BooleanSetting toggle2 = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder()
                     .id("overlap"))
                  .name("Overlap"))
               .description("Скрывать перекрывающиеся теги"))
            .withKeybind())
         .value(false)
         .build();
      NumberSetting numberSetting = ((NumberSettingBuilder)((NumberSettingBuilder)((NumberSettingBuilder)((NumberSettingBuilder)NumberSetting.builder()
                     .id("threshold"))
                  .name("Threshold"))
               .description("Порог перекрытия для скрытия"))
            .range(0.0, 1.0)
            .defaultValue(0.5)
            .precision(2)
            .visibleWhen(toggle2::isEnabled))
         .build();
      if (espTargetType != EspTargetType.ITEMS) {
         String[] stringArray;
         if (espTargetType == EspTargetType.ENTITIES) {
            String[] stringArray2 = new String[]{"Health", null};
            stringArray = stringArray2;
            stringArray2[1] = "Money";
         } else {
            String[] stringArray3 = new String[4];
            stringArray3[0] = "Health";
            stringArray3[1] = "Items";
            stringArray3[2] = "Sphere";
            stringArray = stringArray3;
            stringArray3[3] = "Talisman";
         }

         MultiSelectSetting multi = ((MultiSelectSettingBuilder)((MultiSelectSettingBuilder)((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder(
                        
                     )
                     .id("display"))
                  .name("Display"))
               .description("Что показывать рядом с ником"))
            .options(stringArray)
            .build();
         multi.getSelectedOptions().add("Health");
         multi.getSelectedOptions().add("Items");
         multi.getSelectedOptions().add("Talisman");
         multiSelectSetting = multi;
         toggle = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder(
                              
                           )
                           .id("show_enchantments"))
                        .name("Show Enchantments"))
                     .description("Зачарования предметов"))
                  .withKeybind())
               .visibleWhen(() -> multi.getSelectedOptions().contains("Items")))
            .value(false)
            .build();
         cls0919Module.setSetting(multi);
         cls0919Module.setSetting(toggle);
      }

      cls0919Module.setSetting(toggle2);
      cls0919Module.setSetting(numberSetting);
      this.registerSettings(
         this.nameTagSettings, espTargetType, espRelation, new NameTagSettings(booleanSetting, multiSelectSetting, toggle, toggle2, numberSetting)
      );
      return cls0919Module;
   }

   private String createModuleId(EspTargetType espTargetType, EspRelation espRelation, String string) {
      if (espTargetType == EspTargetType.PLAYERS && espRelation == EspRelation.FRIEND) {
         return "esp_player_friend_" + string;
      } else {
         String string4 = espTargetType.name().toLowerCase();
         return "esp_" + string4 + "_" + string;
      }
   }

   public boolean hasEnabledGlow() {
      for(GlowEspSettings glowEspSettings : this.glowSettings.values()) {
         if (glowEspSettings.isEnabled()) {
            return true;
         }
      }

      return false;
   }

   public BoxEspSettings getBox2dSettings(EspTargetType espTargetType, EspRelation espRelation) {
      return this.getSettings(this.box2dSettings, espTargetType, espRelation);
   }

   public boolean hasEnabledWorldBox() {
      for(EnumMap<EspRelation, WorldBoxSettings> enumMap : this.worldBoxSettings.values()) {
         for(WorldBoxSettings dotted : enumMap.values()) {
            if (dotted.isEnabled()) {
               return true;
            }
         }
      }

      return false;
   }

   private EspFeatureModule createModelEspModule(EspTargetType espTargetType, EspRelation espRelation) {
      EspFeatureModule cls0919Module = new EspFeatureModule(
         this.eventBus,
         this.createModuleId(espTargetType, espRelation, "model"),
         "Model ESP",
         "3D-модель или скелет игроков сквозь стены",
         this.getCategoryTitle(espTargetType, espRelation)
      );
      BooleanSetting booleanSetting = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder()
                     .id("enabled"))
                  .name("Enabled"))
               .description("Отрисовка 3D-модели/скелета игроков"))
            .withKeybind())
         .value(false)
         .build();
      ModeSetting modeSetting = ((ModeSettingBuilder)((ModeSettingBuilder)((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().id("type")).name("Type"))
            .description("Модель или скелет"))
         .options("Model", "Skeleton")
         .defaultOption("Model")
         .build();
      ColorSetting colorSetting = ((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)ColorSetting.builder().id("color")).name("Color"))
            .description("Цвет обводки/скелета"))
         .build();
      BooleanSetting toggle = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder(
                           
                        )
                        .id("filled"))
                     .name("Filled"))
                  .description("Заливать модель"))
               .withKeybind())
            .visibleWhen(() -> "Model".equals(modeSetting.getSelectedOption())))
         .build();
      ColorSetting colorSetting2 = ((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)ColorSetting.builder()
                     .id("filled_color"))
                  .name("Filled color"))
               .description("Цвет заливки модели"))
            .visibleWhen(() -> "Model".equals(modeSetting.getSelectedOption()) && toggle.isEnabled()))
         .build();
      cls0919Module.setBooleanSetting(booleanSetting);
      cls0919Module.setSetting(modeSetting);
      cls0919Module.setSetting(colorSetting);
      cls0919Module.setSetting(toggle);
      cls0919Module.setSetting(colorSetting2);
      this.modelEspSettings.put(espRelation, new ModelEspSettings(booleanSetting, modeSetting, colorSetting, toggle, colorSetting2));
      return cls0919Module;
   }

   public List<Module> getModules(EspTargetType espTargetType, EspRelation espRelation) {
      EnumMap<EspRelation, List<Module>> enumMap = this.modulesByTarget.get(espTargetType);
      if (enumMap == null) {
         return List.of();
      } else {
         List<Module> list = enumMap.get(espRelation);
         return list == null ? List.of() : list;
      }
   }

   private List<Module> createFeatureModules(EspTargetType espTargetType, EspRelation espRelation) {
      ArrayList<Module> arrayList = new ArrayList<>();
      arrayList.add(this.createNameTagModule(espTargetType, espRelation));
      if (espTargetType != EspTargetType.SELF) {
         arrayList.add(this.createBox2dModule(espTargetType, espRelation));
         arrayList.add(this.createWorldBoxModule(espTargetType, espRelation));
         if (espTargetType == EspTargetType.PLAYERS) {
            arrayList.add(this.createChamsModule(espTargetType, espRelation));
            arrayList.add(this.createGlowModule(espTargetType, espRelation));
            arrayList.add(this.createModelEspModule(espTargetType, espRelation));
         }
      }

      return arrayList;
   }

   private EspFeatureModule createWorldBoxModule(EspTargetType espTargetType, EspRelation espRelation) {
      EspFeatureModule cls0919Module = new EspFeatureModule(
         this.eventBus,
         this.createModuleId(espTargetType, espRelation, "worldbox"),
         "WorldBox",
         "Объёмная 3D-рамка вокруг сущностей",
         this.getCategoryTitle(espTargetType, espRelation)
      );
      BooleanSetting booleanSetting = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder()
                     .id("enabled"))
                  .name("Enabled"))
               .description("Рисовать 3D-рамку"))
            .withKeybind())
         .value(false)
         .build();
      ModeSetting modeSetting = ((ModeSettingBuilder)((ModeSettingBuilder)((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().id("style")).name("Style"))
            .description("Стиль контура рамки"))
         .options("Dotted", "Straight")
         .defaultIndex(1)
         .build();
      ColorSetting colorSetting = ((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)ColorSetting.builder().id("color")).name("Color"))
            .description("Цвет рамки"))
         .build();
      NumberSetting numberSetting = ((NumberSettingBuilder)((NumberSettingBuilder)((NumberSettingBuilder)NumberSetting.builder().id("scale")).name("Box scale"))
            .description("Масштаб рамки"))
         .range(1.0, 10.0)
         .defaultValue(1.0)
         .precision(0)
         .build();
      BooleanSetting toggle = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder()
                     .id("depth"))
                  .name("Depth"))
               .description("Учитывать глубину (сквозь стены)"))
            .withKeybind())
         .value(true)
         .build();
      cls0919Module.setBooleanSetting(booleanSetting);
      cls0919Module.setSetting(modeSetting);
      cls0919Module.setSetting(colorSetting);
      cls0919Module.setSetting(numberSetting);
      cls0919Module.setSetting(toggle);
      this.registerSettings(
         this.worldBoxSettings, espTargetType, espRelation, new WorldBoxSettings(booleanSetting, modeSetting, colorSetting, numberSetting, toggle)
      );
      return cls0919Module;
   }

   public NameTagSettings getNameTagSettings(EspTargetType espTargetType, EspRelation espRelation) {
      return this.getSettings(this.nameTagSettings, espTargetType, espRelation);
   }

   public boolean hasEnabledChams() {
      for(ChamsSettings chamsSettings : this.chamsSettings.values()) {
         if (chamsSettings.isEnabled()) {
            return true;
         }
      }

      return false;
   }

   public ChamsSettings getChamsSettings(EspRelation espRelation) {
      return this.chamsSettings.get(espRelation);
   }

   private EspFeatureModule createChamsModule(EspTargetType espTargetType, EspRelation espRelation) {
      EspFeatureModule cls0919Module = new EspFeatureModule(
         this.eventBus,
         this.createModuleId(espTargetType, espRelation, "chams"),
         "Chams",
         "Прозрачные/стеклянные/металлические оверлеи на игроков",
         this.getCategoryTitle(espTargetType, espRelation)
      );
      BooleanSetting booleanSetting = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder()
                     .id("enabled"))
                  .name("Enabled"))
               .description("Оверлей на моделях игроков"))
            .withKeybind())
         .value(false)
         .build();
      ModeSetting modeSetting = ((ModeSettingBuilder)((ModeSettingBuilder)((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().id("mode")).name("Mode"))
            .description("Стиль отрисовки"))
         .options(ChamsSettings.MATERIAL_MODES)
         .defaultOption("Glass")
         .build();
      BooleanSetting toggle = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder()
                     .id("fill_visible_chams"))
                  .name("Fill visible"))
               .description("Заливать видимые части модели"))
            .withKeybind())
         .value(true)
         .build();
      ColorSetting colorSetting = ((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)ColorSetting.builder()
                     .id("visible_color"))
                  .name("Visible color"))
               .description("Цвет видимых частей"))
            .visibleWhen(toggle::isEnabled))
         .build();
      BooleanSetting toggle2 = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder()
                     .id("fill_hidden_chams"))
                  .name("Fill hidden"))
               .description("Заливать скрытые части модели"))
            .withKeybind())
         .value(true)
         .build();
      ColorSetting colorSetting2 = ((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)ColorSetting.builder()
                     .id("hidden_color"))
                  .name("Hidden color"))
               .description("Цвет скрытых частей"))
            .visibleWhen(toggle2::isEnabled))
         .build();
      cls0919Module.setBooleanSetting(booleanSetting);
      cls0919Module.setSetting(modeSetting);
      cls0919Module.setSetting(toggle);
      cls0919Module.setSetting(colorSetting);
      cls0919Module.setSetting(toggle2);
      cls0919Module.setSetting(colorSetting2);
      this.chamsSettings.put(espRelation, new ChamsSettings(booleanSetting, modeSetting, toggle, colorSetting, toggle2, colorSetting2));
      return cls0919Module;
   }

   private <T> void registerSettings(EnumMap<EspTargetType, EnumMap<EspRelation, T>> enumMap, EspTargetType espTargetType2, EspRelation espRelation, T t) {
      EnumMap enumMap2 = enumMap.computeIfAbsent(espTargetType2, espTargetType -> new EnumMap<>(EspRelation.class));
      if (espTargetType2 == EspTargetType.PLAYERS) {
         enumMap2.put(espRelation, t);
      } else {
         for(EspRelation espRelation2 : EspRelation.values()) {
            enumMap2.put(espRelation2, t);
         }
      }
   }

   private EspFeatureModule createBox2dModule(EspTargetType espTargetType, EspRelation espRelation) {
      EspFeatureModule cls0919Module = new EspFeatureModule(
         this.eventBus,
         this.createModuleId(espTargetType, espRelation, "box2d"),
         "2D ESP",
         "2D-бокс со шкалами здоровья и брони",
         this.getCategoryTitle(espTargetType, espRelation)
      );
      BooleanSetting booleanSetting = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder()
                     .id("enabled"))
                  .name("Enabled"))
               .description("Подсвечивать сущности 2D-боксом"))
            .withKeybind())
         .value(false)
         .build();
      BooleanSetting toggle = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder()
                     .id("box"))
                  .name("Box"))
               .description("Рисовать рамку вокруг сущности"))
            .withKeybind())
         .value(true)
         .build();
      ModeSetting modeSetting = ((ModeSettingBuilder)((ModeSettingBuilder)((ModeSettingBuilder)((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
                     .id("box_type"))
                  .name("Box Type"))
               .description("Форма рамки"))
            .options("Rectangle", "Corner")
            .defaultIndex(0)
            .visibleWhen(toggle::isEnabled))
         .build();
      ColorSetting colorSetting = ((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)ColorSetting.builder()
                     .id("box_color"))
                  .name("Box Color"))
               .description("Цвет рамки"))
            .selectedIndex(4)
            .visibleWhen(toggle::isEnabled))
         .build();
      BooleanSetting toggle2 = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder()
                     .id("health_bar"))
                  .name("Health Bar"))
               .description("Шкала здоровья слева от бокса"))
            .withKeybind())
         .value(false)
         .build();
      ColorSetting colorSetting2 = ((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)ColorSetting.builder()
                     .id("health_color"))
                  .name("Health Color"))
               .description("Цвет шкалы здоровья"))
            .selectedIndex(3)
            .visibleWhen(toggle2::isEnabled))
         .build();
      BooleanSetting toggle3 = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder()
                     .id("armor_bar"))
                  .name("Armor Bar"))
               .description("Шкала брони под боксом"))
            .withKeybind())
         .value(false)
         .build();
      ColorSetting colorSetting3 = ((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)((ColorSettingBuilder)ColorSetting.builder()
                     .id("bar_color"))
                  .name("Bar Color"))
               .description("Цвет шкалы брони"))
            .selectedIndex(5)
            .visibleWhen(toggle3::isEnabled))
         .build();
      BooleanSetting toggle4 = ((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder()
                     .id("partner_items"))
                  .name("Partner Items"))
               .description("Предметы Server Helper под сущностью с % дистанции"))
            .withKeybind())
         .value(false)
         .build();
      cls0919Module.setBooleanSetting(booleanSetting);
      cls0919Module.setSetting(toggle);
      cls0919Module.setSetting(modeSetting);
      cls0919Module.setSetting(colorSetting);
      cls0919Module.setSetting(toggle2);
      cls0919Module.setSetting(colorSetting2);
      cls0919Module.setSetting(toggle3);
      cls0919Module.setSetting(colorSetting3);
      cls0919Module.setSetting(toggle4);
      this.registerSettings(
         this.box2dSettings,
         espTargetType,
         espRelation,
         new BoxEspSettings(booleanSetting, toggle, modeSetting, colorSetting, toggle2, colorSetting2, toggle3, colorSetting3, toggle4)
      );
      return cls0919Module;
   }

   public EspTargetType getTargetType(Module module) {
      for(EspTargetType espTargetType : this.modulesByTarget.keySet()) {
         for(List<Module> list : this.modulesByTarget.get(espTargetType).values()) {
            if (list.contains(module)) {
               return espTargetType;
            }
         }
      }

      return null;
   }

   public boolean hasEnabledModelEsp() {
      for(ModelEspSettings skeleton : this.modelEspSettings.values()) {
         if (skeleton.isEnabled()) {
            return true;
         }
      }

      return false;
   }

   public boolean hasEnabledBox2d() {
      for(EnumMap<EspRelation, BoxEspSettings> enumMap : this.box2dSettings.values()) {
         for(BoxEspSettings rectangle : enumMap.values()) {
            if (rectangle.isEnabled()) {
               return true;
            }
         }
      }

      return false;
   }

   public GlowEspSettings getGlowSettings(EspRelation espRelation) {
      return this.glowSettings.get(espRelation);
   }

   private String getCategoryTitle(EspTargetType espTargetType, EspRelation espRelation) {
      return espTargetType == EspTargetType.PLAYERS && espRelation == EspRelation.FRIEND ? "Friends" : espTargetType.getTitle();
   }

   private <T> T getSettings(EnumMap<EspTargetType, EnumMap<EspRelation, T>> enumMap, EspTargetType espTargetType, EspRelation espRelation) {
      EnumMap<EspRelation, T> enumMap2 = enumMap.get(espTargetType);
      return enumMap2 == null ? null : enumMap2.get(espRelation);
   }
}
