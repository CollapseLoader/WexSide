package ru.wexside.misc;

import ru.wexside.setting.BindSetting;
import ru.wexside.setting.BindSettingBuilder;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.Setting;
import ru.wexside.setting.SettingKeybind;

public final class BindingVisibility {
   private final SettingKeybind keybind;
   private BindActivationMode activationMode;
   private final Setting activationValueSetting;
   private final BindSetting inputSetting;
   private final BooleanSetting showInHudSetting;

   public BindingVisibility(SettingKeybind settingKeybind) {
      this.keybind = settingKeybind;
      this.activationValueSetting = settingKeybind.getSetting().copy();
      this.inputSetting = ((BindSettingBuilder)((BindSettingBuilder)BindSetting.getBindSettingBuilder().id("binding_input")).name("binding_input"))
         .input(settingKeybind.getBindInput())
         .build();
      this.showInHudSetting = ((BooleanSettingBuilder)((BooleanSettingBuilder)BooleanSetting.builder().id("binding_visibility")).name("binding_visibility"))
         .value(settingKeybind.isShownInHud())
         .build();
      this.loadFromKeybind();
   }

   public BindActivationMode getBindActivationMode() {
      return this.activationMode;
   }

   public Setting getSetting() {
      return this.activationValueSetting;
   }

   public void setBindActivationMode(BindActivationMode bindActivationMode) {
      this.activationMode = bindActivationMode == null ? BindActivationMode.TOGGLE : bindActivationMode;
   }

   public void loadFromKeybind() {
      if (!this.keybind.isEditorInitialized() && this.keybind.getBindInput().isUnbound()) {
         this.activationValueSetting.restorePayload(this.keybind.getSetting().togglePayload());
      } else {
         this.activationValueSetting.restorePayload(this.keybind.getActivationPayload());
      }

      this.inputSetting.setBindInput(this.keybind.getBindInput());
      this.showInHudSetting.setEnabled(this.keybind.isShownInHud());
      this.activationMode = this.keybind.getBindActivationMode();
   }

   public void saveToKeybind() {
      this.keybind.setActivationPayload(this.activationValueSetting.copyPayload());
      this.keybind.setBindInput(this.inputSetting.getBindInput());
      this.keybind.setShownInHud(this.showInHudSetting.isEnabled());
      this.keybind.setBindActivationMode(this.activationMode);
   }

   public BindSetting getBindSetting() {
      return this.inputSetting;
   }

   public BooleanSetting getBooleanSetting() {
      return this.showInHudSetting;
   }

   public SettingKeybind getSettingKeybind() {
      return this.keybind;
   }
}
