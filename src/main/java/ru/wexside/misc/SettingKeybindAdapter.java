package ru.wexside.misc;

import java.util.Objects;
import ru.wexside.input.BindInput;
import ru.wexside.setting.SettingKeybind;

public final class SettingKeybindAdapter implements KeybindDescriptor {
   private final SettingKeybind settingKeybind;

   public SettingKeybindAdapter(SettingKeybind settingKeybind) {
      this.settingKeybind = settingKeybind;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof SettingKeybindAdapter)) {
         return false;
      } else {
         SettingKeybindAdapter settingKeybindAdapter = (SettingKeybindAdapter)object;
         return Objects.equals(this.settingKeybind, settingKeybindAdapter.settingKeybind);
      }
   }

   @Override
   public String toString() {
      String string = String.valueOf(this.settingKeybind);
      return "OfSettingBinding[binding=" + string + "]";
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.settingKeybind);
   }

   @Override
   public String getString() {
      return this.settingKeybind.getSetting().getDisplayName();
   }

   @Override
   public String getString2() {
      return this.settingKeybind.getSetting().getDescription();
   }

   public SettingKeybind getSettingKeybind() {
      return this.settingKeybind;
   }

   @Override
   public void setBindInput(BindInput bindInput) {
      this.settingKeybind.setBindInput(bindInput);
   }

   @Override
   public BindInput getBindInput() {
      return this.settingKeybind.getBindInput();
   }
}
