package ru.wexside.setting;

import ru.wexside.input.BindInput;
import ru.wexside.misc.BindSettingConsumer;

public final class BindSettingBuilder extends SettingBuilder {
   BindSettingConsumer pressedCallback;
   BindSettingConsumer releasedCallback;
   BindInput bindInput = BindInput.unbound();

   public BindSettingBuilder() {
      this.pressedCallback = ignored -> {
      };
      this.releasedCallback = ignored -> {
      };
   }

   public BindSettingBuilder onReleased(BindSettingConsumer callback) {
      this.releasedCallback = callback == null ? ignored -> {
      } : callback;
      return this;
   }

   public BindSettingBuilder onPressed(BindSettingConsumer callback) {
      this.pressedCallback = callback == null ? ignored -> {
      } : callback;
      return this;
   }

   public BindSetting build() {
      return new BindSetting(this);
   }

   public BindSettingBuilder input(BindInput bindInput) {
      this.bindInput = bindInput == null ? BindInput.unbound() : bindInput;
      return this;
   }

   public BindSettingBuilder keyboard(int keyCode) {
      return this.input(BindInput.keyboard(keyCode));
   }

   public BindSettingBuilder mouse(int button) {
      return this.input(BindInput.mouse(button));
   }

   public BindSettingBuilder legacyInput(int legacyCode) {
      return this.input(BindInput.fromLegacyCode(legacyCode));
   }

   protected BindSettingBuilder self() {
      return this;
   }
}
