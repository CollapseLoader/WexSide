package ru.wexside.misc;

import ru.wexside.input.BindInput;
import ru.wexside.input.InputBindings;
import ru.wexside.setting.Setting;

public class KeybindBinding {
   private final Setting setting;
   private BindInput input;

   public KeybindBinding(Setting setting, BindInput bindInput) {
      this.setting = setting;
      this.input = bindInput == null ? BindInput.unbound() : bindInput;
   }

   public int getLegacyCode() {
      return this.input.toLegacyCode();
   }

   public BindInput getBindInput() {
      return this.input;
   }

   public void setLegacyCode(int legacyCode) {
      this.setBindInput(BindInput.fromLegacyCode(legacyCode));
   }

   public boolean isPressed() {
      return InputBindings.isPressed(this.input);
   }

   public void setBindInput(BindInput bindInput) {
      this.input = bindInput == null ? BindInput.unbound() : bindInput;
   }

   public String getDisplayName() {
      return InputBindings.displayName(this.input);
   }

   public void onReleased() {
   }

   public Setting getSetting() {
      return this.setting;
   }

   public void onPressed() {
   }

   public boolean matches(BindInput bindInput) {
      return this.input.equals(bindInput);
   }

   public void clear() {
      this.input = BindInput.unbound();
   }

   public boolean matchesKeyboard(int keyCode) {
      return this.input.matchesKeyboard(keyCode);
   }

   public boolean matchesMouse(int button) {
      return this.input.matchesMouse(button);
   }
}
