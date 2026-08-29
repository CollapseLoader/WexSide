package ru.wexside.misc;

import ru.wexside.setting.BindSetting;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.RangeSetting;
import ru.wexside.setting.Setting;
import ru.wexside.setting.TextSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.setting.SettingRow;
import ru.wexside.util.BindSettingRow;
import ru.wexside.util.BooleanSettingRow;
import ru.wexside.util.ColorSettingRow;
import ru.wexside.util.ModeSettingRow;
import ru.wexside.util.MultiSelectSettingRow;
import ru.wexside.util.NumberSettingRow;
import ru.wexside.util.RangeSettingRow;
import ru.wexside.util.TextSettingRow;

public final class SettingRowFactory {
   public static SettingRow<?> process(Setting setting, ContainerDisplay containerDisplay) {
      if (setting instanceof BindSetting bindSetting) {
         return new BindSettingRow(new GuiBounds(0.0F, 0.0F, 0.0F, 11.0F), bindSetting, containerDisplay);
      } else if (setting instanceof BooleanSetting booleanSetting) {
         return new BooleanSettingRow(new GuiBounds(0.0F, 0.0F, 0.0F, 11.0F), booleanSetting, containerDisplay);
      } else if (setting instanceof ColorSetting colorSetting) {
         return new ColorSettingRow(new GuiBounds(0.0F, 0.0F, 0.0F, 11.0F), colorSetting, containerDisplay);
      } else if (setting instanceof TextSetting textSetting) {
         return new TextSettingRow(new GuiBounds(0.0F, 0.0F, 0.0F, 11.0F), textSetting, containerDisplay);
      } else if (setting instanceof ModeSetting modeSetting) {
         return new ModeSettingRow(new GuiBounds(0.0F, 0.0F, 0.0F, 11.0F), modeSetting, containerDisplay);
      } else if (setting instanceof MultiSelectSetting multiSelectSetting) {
         return new MultiSelectSettingRow(new GuiBounds(0.0F, 0.0F, 0.0F, 11.0F), multiSelectSetting, containerDisplay);
      } else if (setting instanceof NumberSetting numberSetting) {
         return new NumberSettingRow(new GuiBounds(0.0F, 0.0F, 0.0F, 11.0F), numberSetting, containerDisplay);
      } else {
         return setting instanceof RangeSetting rangeSetting
            ? new RangeSettingRow(new GuiBounds(0.0F, 0.0F, 0.0F, 11.0F), rangeSetting, containerDisplay)
            : null;
      }
   }
}
