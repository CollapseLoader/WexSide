package ru.wexside.misc;

import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ModeSetting;

public final class ModelEspSettings {
   public static final String MODEL_STYLE = "Model";
   public static final String SKELETON_STYLE = "Skeleton";
   private final BooleanSetting enabled;
   private final ModeSetting style;
   private final ColorSetting outlineColor;
   private final BooleanSetting fillEnabled;
   private final ColorSetting fillColor;

   public ModelEspSettings(BooleanSetting enabled, ModeSetting style, ColorSetting outlineColor, BooleanSetting fillEnabled, ColorSetting fillColor) {
      this.enabled = enabled;
      this.style = style;
      this.outlineColor = outlineColor;
      this.fillEnabled = fillEnabled;
      this.fillColor = fillColor;
   }

   public boolean isEnabled() {
      return this.enabled.isEnabled();
   }

   public boolean isModelStyle() {
      return "Model".equals(this.style.getSelectedOption());
   }

   public int getOutlineColor() {
      return this.outlineColor.getColor();
   }

   public boolean isFillEnabled() {
      return this.fillEnabled.isEnabled();
   }

   public int getFillColor() {
      return this.fillColor.getColor();
   }
}
