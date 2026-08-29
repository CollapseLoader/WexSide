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
import ru.wexside.ui.setting.SettingComponent;
import ru.wexside.util.ModeSettingComponent;
import ru.wexside.util.MultiSelectSettingComponent;
import ru.wexside.util.NumberSettingComponent;
import ru.wexside.util.RangeSettingComponent;
import ru.wexside.util.TextSettingComponent;

public final class SettingComponentFactory {
   public static SettingComponent<?> process(Setting setting) {
      if (setting instanceof BindSetting bindSetting) {
         return new BindSettingComponent(bindSetting);
      } else if (setting instanceof BooleanSetting booleanSetting) {
         return new BooleanSettingComponent(booleanSetting);
      } else if (setting instanceof ColorSetting colorSetting) {
         return new ColorSettingComponent(colorSetting);
      } else if (setting instanceof TextSetting textSetting) {
         return new TextSettingComponent(textSetting, (TextFieldStyle)(textSetting.isExpanded() ? new ExpandedTextFieldStyle() : new CompactTextFieldStyle()));
      } else if (setting instanceof ModeSetting modeSetting) {
         return new ModeSettingComponent(modeSetting);
      } else if (setting instanceof MultiSelectSetting multiSelectSetting) {
         return new MultiSelectSettingComponent(multiSelectSetting);
      } else if (setting instanceof NumberSetting numberSetting) {
         return new NumberSettingComponent(numberSetting);
      } else {
         return setting instanceof RangeSetting rangeSetting ? new RangeSettingComponent(rangeSetting) : null;
      }
   }
}
