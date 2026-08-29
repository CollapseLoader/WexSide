package ru.wexside.misc;

import net.minecraft.class_1792;
import net.minecraft.class_1802;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.util.ColorUtils;

public final class ConsumablesEspSettings {
   private final BooleanSetting booleanSetting;
   public static final String string2 = "Пласт";
   private final BooleanSetting booleanSetting2;
   public static final String string3 = "Явная пыль";
   public static final String string4 = "Дезориентация";
   private final BooleanSetting booleanSetting3;
   public static final String TRAP_ITEM = "Трапка";
   private final MultiSelectSetting multiSelectSetting;
   public static final String[] string5 = new String[]{"Трапка", "Пласт", "Дезориентация", "Явная пыль", "Снежок заморозка"};
   private final ColorSetting colorSetting;
   public static final String string6 = "Снежок заморозка";
   private final MultiSelectSetting multiSelectSetting2;
   private final ColorSetting colorSetting2;

   public ConsumablesEspSettings(
      BooleanSetting booleanSetting,
      BooleanSetting toggle,
      MultiSelectSetting multiSelectSetting,
      ColorSetting colorSetting,
      BooleanSetting toggle2,
      MultiSelectSetting multi,
      ColorSetting colorSetting3
   ) {
      this.booleanSetting3 = booleanSetting;
      this.booleanSetting2 = toggle;
      this.multiSelectSetting2 = multiSelectSetting;
      this.colorSetting = colorSetting;
      this.booleanSetting = toggle2;
      this.multiSelectSetting = multi;
      this.colorSetting2 = colorSetting3;
   }

   public boolean isActive() {
      return this.booleanSetting3.isEnabled();
   }

   private static String process(class_1792 iiIilIIilI2) {
      if (iiIilIIilI2 == class_1802.field_22021) {
         return "Трапка";
      } else if (iiIilIIilI2 == class_1802.field_8614) {
         return "Пласт";
      } else if (iiIilIIilI2 == class_1802.field_8450) {
         return "Дезориентация";
      } else if (iiIilIIilI2 == class_1802.field_8479) {
         return "Явная пыль";
      } else {
         return iiIilIIilI2 == class_1802.field_8543 ? "Снежок заморозка" : null;
      }
   }

   public ColorSetting process2(class_1792 iiIilIIilI2) {
      return this.colorSetting;
   }

   public boolean process3(class_1792 iiIilIIilI2) {
      return process4(this.multiSelectSetting2, iiIilIIilI2);
   }

   private static boolean process4(MultiSelectSetting multiSelectSetting, class_1792 iiIilIIilI2) {
      String string = process(iiIilIIilI2);
      return string != null && multiSelectSetting.getSelectedOptions().contains(string);
   }

   public boolean isActive2() {
      return this.booleanSetting2.isEnabled();
   }

   public static int process5(ColorSetting colorSetting, float f) {
      if (colorSetting.isAstolfoMode()) {
         return colorSetting.getEditingColor(f);
      } else {
         int n = colorSetting.getPrimaryColor();
         int n2 = colorSetting.isDoubleColorMode() ? colorSetting.getSecondaryColor() : n;
         return ColorUtils.lerp(n, n2, (double)f);
      }
   }

   public boolean process6(class_1792 iiIilIIilI2) {
      return process4(this.multiSelectSetting, iiIilIIilI2);
   }

   public boolean isActive3() {
      return this.booleanSetting.isEnabled();
   }

   public ColorSetting process7(class_1792 iiIilIIilI2) {
      return this.colorSetting2;
   }
}
