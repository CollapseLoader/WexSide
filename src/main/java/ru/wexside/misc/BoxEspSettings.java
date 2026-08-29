package ru.wexside.misc;

import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ModeSetting;

public final class BoxEspSettings {
   public static final String RECTANGLE_STYLE = "Rectangle";
   public static final String CORNER_STYLE = "Corner";
   private final BooleanSetting enabled;
   private final BooleanSetting boxEnabled;
   private final ModeSetting boxStyle;
   private final ColorSetting boxColor;
   private final BooleanSetting healthBarEnabled;
   private final ColorSetting healthColor;
   private final BooleanSetting armorBarEnabled;
   private final ColorSetting armorColor;
   private final BooleanSetting partnerItemsEnabled;

   public BoxEspSettings(
      BooleanSetting enabled,
      BooleanSetting boxEnabled,
      ModeSetting boxStyle,
      ColorSetting boxColor,
      BooleanSetting healthBarEnabled,
      ColorSetting healthColor,
      BooleanSetting armorBarEnabled,
      ColorSetting armorColor,
      BooleanSetting partnerItemsEnabled
   ) {
      this.enabled = enabled;
      this.boxEnabled = boxEnabled;
      this.boxStyle = boxStyle;
      this.boxColor = boxColor;
      this.healthBarEnabled = healthBarEnabled;
      this.healthColor = healthColor;
      this.armorBarEnabled = armorBarEnabled;
      this.armorColor = armorColor;
      this.partnerItemsEnabled = partnerItemsEnabled;
   }

   public boolean isEnabled() {
      return this.enabled.isEnabled();
   }

   public boolean isBoxEnabled() {
      return this.boxEnabled.isEnabled();
   }

   public boolean isCornerStyle() {
      return "Corner".equals(this.boxStyle.getSelectedOption());
   }

   public int getBoxColor(float progress) {
      return this.boxColor.getColor(progress);
   }

   public boolean isHealthBarEnabled() {
      return this.healthBarEnabled.isEnabled();
   }

   public int getHealthColor(float progress) {
      return this.healthColor.getColor(progress);
   }

   public boolean isArmorBarEnabled() {
      return this.armorBarEnabled.isEnabled();
   }

   public int getArmorColor(float progress) {
      return this.armorColor.getColor(progress);
   }

   public boolean isPartnerItemsEnabled() {
      return this.partnerItemsEnabled.isEnabled();
   }
}
