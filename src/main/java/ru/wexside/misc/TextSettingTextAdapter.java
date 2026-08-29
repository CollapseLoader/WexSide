package ru.wexside.misc;

import ru.wexside.setting.TextSetting;

public final class TextSettingTextAdapter implements TextInputModel {
   private final TextSetting textSetting;

   public TextSettingTextAdapter(TextSetting textSetting) {
      this.textSetting = textSetting;
   }

   @Override
   public int getMaximumLength() {
      return this.textSetting.getMaxLength();
   }

   @Override
   public String getText() {
      return this.textSetting.getValue();
   }

   @Override
   public void setText(String text) {
      this.textSetting.setValue(text);
   }
}
