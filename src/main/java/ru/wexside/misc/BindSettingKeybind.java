package ru.wexside.misc;

import ru.wexside.input.BindInput;
import ru.wexside.setting.BindSetting;

public final class BindSettingKeybind extends KeybindBinding {
   private final BindSetting bindSetting;
   private final BindSettingConsumer pressedCallback;
   private final BindSettingConsumer releasedCallback;

   public BindSettingKeybind(BindSetting bindSetting, BindInput bindInput, BindSettingConsumer pressedCallback, BindSettingConsumer releasedCallback) {
      super(bindSetting, bindInput);
      this.bindSetting = bindSetting;
      this.pressedCallback = pressedCallback == null ? ignored -> {
      } : pressedCallback;
      this.releasedCallback = releasedCallback == null ? ignored -> {
      } : releasedCallback;
   }

   @Override
   public void onReleased() {
      this.releasedCallback.setBindSetting(this.bindSetting);
   }

   @Override
   public void onPressed() {
      this.pressedCallback.setBindSetting(this.bindSetting);
   }

   public BindSettingConsumer getReleasedCallback() {
      return this.releasedCallback;
   }

   public BindSettingConsumer getPressedCallback() {
      return this.pressedCallback;
   }

   public BindSetting getBindSetting() {
      return this.bindSetting;
   }
}
