package ru.wexside.setting;

public final class ModeSettingBuilder extends SettingBuilder {
   String defaultOption;
   String[] options = new String[0];
   int defaultIndex;

   public ModeSettingBuilder options(String... options) {
      this.options = options == null ? new String[0] : options;
      return this;
   }

   public ModeSetting build() {
      return new ModeSetting(this);
   }

   public ModeSettingBuilder defaultIndex(int defaultIndex) {
      this.defaultIndex = defaultIndex;
      return this;
   }

   public ModeSettingBuilder defaultOption(String defaultOption) {
      this.defaultOption = defaultOption;
      return this;
   }

   protected ModeSettingBuilder self() {
      return this;
   }
}
