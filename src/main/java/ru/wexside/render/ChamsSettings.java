package ru.wexside.render;

import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ModeSetting;

public final class ChamsSettings {
   public static final String[] MATERIAL_MODES = new String[]{"Simple", "Filled", "Glass", "Chrome", "Liquid Glass"};
   private final BooleanSetting enabled;
   private final ModeSetting materialMode;
   private final BooleanSetting visibleFill;
   private final ColorSetting visibleColor;
   private final BooleanSetting hiddenFill;
   private final ColorSetting hiddenColor;

   public ChamsSettings(
      BooleanSetting enabled,
      ModeSetting materialMode,
      BooleanSetting visibleFill,
      ColorSetting visibleColor,
      BooleanSetting hiddenFill,
      ColorSetting hiddenColor
   ) {
      this.enabled = enabled;
      this.materialMode = materialMode;
      this.visibleFill = visibleFill;
      this.visibleColor = visibleColor;
      this.hiddenFill = hiddenFill;
      this.hiddenColor = hiddenColor;
   }

   public boolean isEnabled() {
      return this.enabled.isEnabled();
   }

   public boolean isVisibleFillEnabled() {
      return this.visibleFill.isEnabled();
   }

   public boolean isHiddenFillEnabled() {
      return this.hiddenFill.isEnabled();
   }

   public int getVisibleColor() {
      return this.visibleColor.getColor();
   }

   public int getHiddenColor() {
      return this.hiddenColor.getColor();
   }

   public int getMaterialModeIndex() {
      String selectedMode = this.materialMode.getSelectedOption();

      for(int index = 0; index < MATERIAL_MODES.length; ++index) {
         if (MATERIAL_MODES[index].equals(selectedMode)) {
            return index;
         }
      }

      return 2;
   }
}
