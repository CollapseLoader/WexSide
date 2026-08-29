package ru.wexside.ui.setting;

import java.util.Objects;
import ru.wexside.setting.Setting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;

public class SettingComponent<T extends Setting> extends GuiElement {
   private final T setting;

   public SettingComponent(GuiBounds bounds, Setting setting) {
      super(bounds);
      this.setting = Objects.requireNonNull((T)setting, "setting");
   }

   public T getSetting() {
      return this.setting;
   }

   public float getFloatType() {
      return this.getBounds().getWidth();
   }

   public float getFloatType2() {
      return this.getBounds().getHeight();
   }
}
