package ru.wexside.misc;

import java.util.function.Consumer;
import java.util.function.Supplier;
import ru.wexside.input.BindInput;
import ru.wexside.setting.BindSetting;
import ru.wexside.setting.SettingKeybind;

public interface KeybindDescriptor {
   static KeybindDescriptor process(String string, String string2, Supplier<BindInput> supplier, Consumer<BindInput> consumer) {
      return new DelegatingKeybind(string, string2, supplier, consumer);
   }

   String getString();

   String getString2();

   static KeybindDescriptor process2(BindSetting bindSetting) {
      return new BindSettingAdapter(bindSetting);
   }

   void setBindInput(BindInput var1);

   BindInput getBindInput();

   static KeybindDescriptor process3(SettingKeybind settingKeybind) {
      return new SettingKeybindAdapter(settingKeybind);
   }

   default boolean isActive() {
      return !this.getBindInput().isUnbound();
   }
}
