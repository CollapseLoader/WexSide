package ru.wexside.misc;

import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.NumberSetting;

public final class WorldBoxSettings {
   public static final String DOTTED_STYLE = "Dotted";
   private final BooleanSetting enabled;
   private final ModeSetting style;
   private final ColorSetting color;
   private final NumberSetting scale;
   private final BooleanSetting depthTest;

   public WorldBoxSettings(BooleanSetting enabled, ModeSetting style, ColorSetting color, NumberSetting scale, BooleanSetting depthTest) {
      this.enabled = enabled;
      this.style = style;
      this.color = color;
      this.scale = scale;
      this.depthTest = depthTest;
   }

   public boolean isEnabled() {
      return this.enabled.isEnabled();
   }

   public boolean isDottedStyle() {
      return "Dotted".equals(this.style.getSelectedOption());
   }

   public int getColor() {
      return this.color.getColor(0.0F);
   }

   public int getScale() {
      return this.scale.getIntValue();
   }

   public boolean isDepthTestEnabled() {
      return this.depthTest.isEnabled();
   }
}
