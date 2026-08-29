package ru.wexside.module.hud;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1703;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1844;
import net.minecraft.class_310;
import net.minecraft.class_4081;
import net.minecraft.class_465;
import net.minecraft.class_746;
import net.minecraft.class_9334;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.misc.SlotHighlight;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.ContainerScreenHelper;

public final class HealthHelperModule extends Module implements ConfigSerializable {
   private static volatile HealthHelperModule instance;
   private final BooleanSetting enabledSetting;
   private final ColorSetting buffColor;
   private final BooleanSetting debuffs;
   private final ColorSetting debuffColor;
   private final BooleanSetting gapple;
   private final ColorSetting gappleColor;
   private final BooleanSetting enchantedGapple;
   private final ColorSetting enchantedGappleColor;
   private final BooleanSetting tint;
   private final NumberSetting tintPeriod;

   public HealthHelperModule(EventBus eventBus) {
      super(eventBus, "health_helper", "Health Helper", "Подсвечивает исцеляющие предметы в инвентаре", ModuleCategory.valueOf("DISPLAY"));
      instance = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Подсвечивает исцеляющие предметы в инвентаре")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      ColorSetting buff = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(0).name("Color buff").id("buff_color").description("Цвет полезных зелий"))
         .build();
      this.applyDefaultPalette(buff);
      this.buffColor = buff;
      this.registerSetting(buff);
      this.debuffs = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Debuffs")
            .id("debuffs")
            .description("Подсвечивает вредные зелья в инвентаре"))
         .build();
      this.registerSetting(this.debuffs);
      ColorSetting debuff = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(3)
            .name("Color debuff")
            .id("debuff_color")
            .description("Цвет вредных зелий")
            .visibleWhen(this.debuffs::isEnabled))
         .build();
      this.applyDefaultPalette(debuff);
      this.debuffColor = debuff;
      this.registerSetting(debuff);
      this.gapple = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("GApple")
            .id("gapple")
            .description("Подсвечивает золотые яблоки"))
         .build();
      this.registerSetting(this.gapple);
      ColorSetting gappleColorSetting = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(4)
            .name("Color gapple")
            .id("gapple_color")
            .description("Цвет золотого яблока")
            .visibleWhen(this.gapple::isEnabled))
         .build();
      this.applyDefaultPalette(gappleColorSetting);
      this.gappleColor = gappleColorSetting;
      this.registerSetting(gappleColorSetting);
      this.enchantedGapple = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("E-GApple")
            .id("e_gapple")
            .description("Подсвечивает зачарованные золотые яблоки"))
         .build();
      this.registerSetting(this.enchantedGapple);
      ColorSetting egappleColorSetting = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(2)
            .name("Color e-gapple")
            .id("e_gapple_color")
            .description("Цвет зачарованного золотого яблока")
            .visibleWhen(this.enchantedGapple::isEnabled))
         .build();
      this.applyDefaultPalette(egappleColorSetting);
      this.enchantedGappleColor = egappleColorSetting;
      this.registerSetting(egappleColorSetting);
      this.tint = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Tint")
            .id("tint")
            .description("Пульсация прозрачности подсветки"))
         .build();
      this.registerSetting(this.tint);
      this.tintPeriod = ((NumberSettingBuilder)NumberSetting.builder()
            .range(500.0, 2000.0)
            .defaultValue(1200.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Tint period")
            .id("tint_period")
            .description("Период пульсации прозрачности")
            .visibleWhen(this.tint::isEnabled))
         .build();
      this.registerSetting(this.tintPeriod);
   }

   @Override
   protected void initialize() {
      WexSideClient.getSlotHighlightRegistry().setCallback56(this::collectHighlights);
   }

   private List<SlotHighlight> collectHighlights(class_465<?> screen) {
      if (this.enabledSetting.isEnabled() && screen != null && !shouldSkipPlayer()) {
         class_1703 handler = screen.method_17577();
         if (handler != null && ContainerScreenHelper.isPlayerInventoryContainer(handler, screen)) {
            ArrayList<SlotHighlight> highlights = new ArrayList<>();

            for(int i = 0; i < handler.field_7761.size(); ++i) {
               class_1735 slot = (class_1735)handler.field_7761.get(i);
               Integer color = this.resolveColor(slot.method_7677());
               if (color != null) {
                  highlights.add(new SlotHighlight(i, color));
               }
            }

            return highlights;
         } else {
            return List.of();
         }
      } else {
         return List.of();
      }
   }

   public static int process7(class_1799 stack) {
      HealthHelperModule module = instance;
      if (module != null && module.enabledSetting.isEnabled() && stack != null && !stack.method_7960() && !shouldSkipPlayer()) {
         Integer color = module.resolveColor(stack);
         return color == null ? 0 : color;
      } else {
         return 0;
      }
   }

   private static boolean shouldSkipPlayer() {
      class_746 player = class_310.method_1551().field_1724;
      return player != null && player.method_68878();
   }

   private Integer resolveColor(class_1799 stack) {
      if (stack.method_31574(class_1802.field_8367)) {
         return this.enchantedGapple.isEnabled() ? this.resolveTintedColor(this.enchantedGappleColor) : null;
      } else if (stack.method_31574(class_1802.field_8463)) {
         return this.gapple.isEnabled() ? this.resolveTintedColor(this.gappleColor) : null;
      } else if (!stack.method_31574(class_1802.field_8574) && !stack.method_31574(class_1802.field_8436) && !stack.method_31574(class_1802.field_8150)) {
         return null;
      } else {
         class_1844 contents = (class_1844)stack.method_58694(class_9334.field_49651);
         if (contents == null || !contents.method_57405()) {
            return null;
         } else if (this.isMostlyHarmful(contents)) {
            return this.debuffs.isEnabled() ? this.resolveTintedColor(this.debuffColor) : null;
         } else {
            return this.resolveTintedColor(this.buffColor);
         }
      }
   }

   private boolean isMostlyHarmful(class_1844 contents) {
      int harmful = 0;
      int beneficial = 0;

      for(class_1293 effect : contents.method_57397()) {
         class_1291 type = (class_1291)effect.method_5579().comp_349();
         if (type.method_18792() == class_4081.field_18272) {
            ++harmful;
         } else if (type.method_18792() == class_4081.field_18271) {
            ++beneficial;
         }
      }

      return harmful > beneficial;
   }

   private int resolveTintedColor(ColorSetting colorSetting) {
      int color = this.resolveBaseColor(colorSetting);
      if (this.tint.isEnabled()) {
         color = ColorUtils.multiplyAlpha(color, this.getTintStrength());
      }

      return color;
   }

   private int resolveBaseColor(ColorSetting colorSetting) {
      if (colorSetting.isAstolfoMode()) {
         return colorSetting.getColor();
      } else {
         return colorSetting.isDoubleColorMode()
            ? ColorUtils.lerp(colorSetting.getPrimaryColor(), colorSetting.getSecondaryColor(), pulse(1500L))
            : colorSetting.getPrimaryColor();
      }
   }

   private float getTintStrength() {
      long period = Math.max(1L, (long)this.tintPeriod.getIntValue());
      return (float)(0.3 + 0.7 * pulse(period));
   }

   private static double pulse(long periodMs) {
      double phase = (double)(System.currentTimeMillis() % periodMs) / (double)periodMs;
      return 1.0 - Math.abs(2.0 * phase - 1.0);
   }

   private void applyDefaultPalette(ColorSetting colorSetting) {
      colorSetting.setPrimaryColor(0, -11753627);
      colorSetting.setPrimaryColor(1, -1543135);
      colorSetting.setPrimaryColor(2, -9279489);
      colorSetting.setPrimaryColor(3, -46001);
      colorSetting.setPrimaryColor(4, -13218);
      colorSetting.setPrimaryColor(5, -10582785);
      colorSetting.setPrimaryColor(6, -2732032);
   }
}
