package ru.wexside.setting;

import ru.wexside.misc.MultiSelectAnimation;

public final class MultiSelectSettingBuilder extends SettingBuilder {
   String actionLabel;
   MultiSelectAnimation animation = MultiSelectAnimation.NONE;
   Runnable action;
   boolean optionListEnabled;
   String[] options = new String[0];
   boolean selectAll;

   public MultiSelectSetting build() {
      return new MultiSelectSetting(this);
   }

   public MultiSelectSettingBuilder options(String... options) {
      this.options = options == null ? new String[0] : options;
      return this;
   }

   public MultiSelectSettingBuilder selectAll(boolean selectAll) {
      this.selectAll = selectAll;
      return this;
   }

   public MultiSelectSettingBuilder optionListEnabled(boolean enabled) {
      this.optionListEnabled = enabled;
      return this;
   }

   public MultiSelectSettingBuilder action(String label, Runnable action) {
      this.actionLabel = label;
      this.action = action;
      return this;
   }

   public MultiSelectSettingBuilder animation(MultiSelectAnimation animation) {
      this.animation = animation == null ? MultiSelectAnimation.NONE : animation;
      return this;
   }

   protected MultiSelectSettingBuilder self() {
      return this;
   }
}
