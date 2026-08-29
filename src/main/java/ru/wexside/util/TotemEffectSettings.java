package ru.wexside.util;

import java.util.Objects;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.NumberSetting;

public final class TotemEffectSettings {
   private final BooleanSetting booleanSetting;
   private final NumberSetting numberSetting;
   private final ColorSetting colorSetting;
   private final NumberSetting numberSetting2;
   private final ModeSetting modeSetting;
   private final NumberSetting numberSetting3;

   public TotemEffectSettings(
      ColorSetting colorSetting,
      ModeSetting modeSetting,
      NumberSetting numberSetting,
      BooleanSetting booleanSetting,
      NumberSetting number,
      NumberSetting number2
   ) {
      this.colorSetting = colorSetting;
      this.modeSetting = modeSetting;
      this.numberSetting2 = numberSetting;
      this.booleanSetting = booleanSetting;
      this.numberSetting = number;
      this.numberSetting3 = number2;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof TotemEffectSettings)) {
         return false;
      } else {
         TotemEffectSettings totemEffectSettings = (TotemEffectSettings)object;
         return Objects.equals(this.colorSetting, totemEffectSettings.colorSetting)
            && Objects.equals(this.modeSetting, totemEffectSettings.modeSetting)
            && Objects.equals(this.numberSetting2, totemEffectSettings.numberSetting2)
            && Objects.equals(this.booleanSetting, totemEffectSettings.booleanSetting)
            && Objects.equals(this.numberSetting, totemEffectSettings.numberSetting)
            && Objects.equals(this.numberSetting3, totemEffectSettings.numberSetting3);
      }
   }

   @Override
   public String toString() {
      String string = String.valueOf(this.numberSetting3);
      String string2 = String.valueOf(this.numberSetting);
      String string3 = String.valueOf(this.booleanSetting);
      String string4 = String.valueOf(this.numberSetting2);
      String string5 = String.valueOf(this.modeSetting);
      String string6 = String.valueOf(this.colorSetting);
      return "TotemEffectsSettings[color="
         + string6
         + ", particleType="
         + string5
         + ", particleCount="
         + string4
         + ", particlePhysics="
         + string3
         + ", particleSpeed="
         + string2
         + ", particleLifeTime="
         + string
         + "]";
   }

   public int numberSetting() {
      return Objects.hash(this.colorSetting, this.modeSetting, this.numberSetting2, this.booleanSetting, this.numberSetting, this.numberSetting3);
   }

   public int getIntType() {
      return this.numberSetting2.getIntValue();
   }

   public int getIntType2() {
      return Math.max(1, this.numberSetting3.getIntValue());
   }

   public double getDoubleType() {
      return this.numberSetting.getValue();
   }

   public int getIntType3() {
      return this.colorSetting.getColor();
   }

   public NumberSetting enabled() {
      return this.numberSetting;
   }

   public long getLongType() {
      return (long)this.getIntType2() * 50L;
   }

   public NumberSetting getNumberSetting() {
      return this.numberSetting3;
   }

   public ModeSetting getModeSetting() {
      return this.modeSetting;
   }

   public String getString() {
      return this.modeSetting.getSelectedOption();
   }

   public NumberSetting getNumberSetting2() {
      return this.numberSetting2;
   }

   public ColorSetting getColorSetting() {
      return this.colorSetting;
   }

   public boolean isActive() {
      return this.booleanSetting.isEnabled();
   }

   public BooleanSetting getBooleanSetting() {
      return this.booleanSetting;
   }
}
