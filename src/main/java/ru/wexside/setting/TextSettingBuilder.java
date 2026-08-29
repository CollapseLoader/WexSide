package ru.wexside.setting;

import ru.wexside.misc.TextSettingLayout;

public final class TextSettingBuilder extends SettingBuilder {
   TextSettingLayout layout = TextSettingLayout.COMPACT;
   String initialValue = "";
   int maxLength = 16;

   public TextSettingBuilder value(String value) {
      this.initialValue = value;
      return this;
   }

   public TextSetting build() {
      return new TextSetting(this);
   }

   public TextSettingBuilder maxLength(int maxLength) {
      this.maxLength = maxLength;
      return this;
   }

   public TextSettingBuilder expanded() {
      this.layout = TextSettingLayout.EXPANDED;
      return this;
   }

   protected TextSettingBuilder self() {
      return this;
   }

   public TextSettingBuilder compact() {
      this.layout = TextSettingLayout.COMPACT;
      return this;
   }
}
