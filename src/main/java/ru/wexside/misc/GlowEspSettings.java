package ru.wexside.misc;

import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.NumberSetting;

public final class GlowEspSettings {
   private final BooleanSetting enabled;
   private final ColorSetting color;
   private final NumberSetting maximumDistance;
   private final NumberSetting radius;

   public GlowEspSettings(BooleanSetting enabled, ColorSetting color, NumberSetting maximumDistance, NumberSetting radius) {
      this.enabled = enabled;
      this.color = color;
      this.maximumDistance = maximumDistance;
      this.radius = radius;
   }

   public boolean isEnabled() {
      return this.enabled.isEnabled();
   }

   public double getMaximumDistance() {
      return this.maximumDistance.getValue();
   }

   public float getRadius() {
      return (float)this.radius.getValue();
   }

   public int getColor() {
      return this.color.getColor();
   }
}
