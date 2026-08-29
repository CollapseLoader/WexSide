package ru.wexside.setting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.input.BindInput;
import ru.wexside.misc.BindSettingKeybind;

public final class BindSetting extends Setting implements ConfigSerializable {
   private final BindSettingKeybind keybindBinding;

   BindSetting(BindSettingBuilder bindSettingBuilder) {
      super(bindSettingBuilder);
      this.keybindBinding = new BindSettingKeybind(this, bindSettingBuilder.bindInput, bindSettingBuilder.pressedCallback, bindSettingBuilder.releasedCallback);
   }

   public static BindSettingBuilder getBindSettingBuilder() {
      return new BindSettingBuilder();
   }

   @Override
   protected void readValue(DataInputStream dataInputStream) throws IOException {
      this.keybindBinding.setLegacyCode(dataInputStream.readInt());
   }

   @Override
   protected void writeValue(DataOutputStream dataOutputStream) throws IOException {
      dataOutputStream.writeInt(this.keybindBinding.getLegacyCode());
   }

   public int getLegacyCode() {
      return this.keybindBinding.getLegacyCode();
   }

   public void setLegacyCode(int legacyCode) {
      this.keybindBinding.setLegacyCode(legacyCode);
   }

   public BindInput getBindInput() {
      return this.keybindBinding.getBindInput();
   }

   public String getKeyDisplayName() {
      return this.keybindBinding.getDisplayName();
   }

   private BindSetting copySetting() {
      BindSetting bindSetting = ((BindSettingBuilder)((BindSettingBuilder)getBindSettingBuilder().id(this.getId())).name(this.getDisplayName()))
         .input(this.getBindInput())
         .build();
      bindSetting.restorePayload(this.copyPayload());
      return bindSetting;
   }

   public BindSetting copy() {
      return this.copySetting();
   }

   public boolean isPressed() {
      return this.keybindBinding.isPressed();
   }

   public void setBindInput(BindInput bindInput) {
      this.keybindBinding.setBindInput(bindInput);
   }

   public BindSettingKeybind getKeybindBinding() {
      return this.keybindBinding;
   }
}
