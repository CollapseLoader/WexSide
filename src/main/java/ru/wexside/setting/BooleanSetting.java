package ru.wexside.setting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ru.wexside.config.ConfigSerializable;

public final class BooleanSetting extends Setting implements ConfigSerializable {
   private final boolean defaultValue;
   private boolean enabled;

   BooleanSetting(BooleanSettingBuilder booleanSettingBuilder) {
      super(booleanSettingBuilder);
      this.enabled = booleanSettingBuilder.initialValue;
      this.defaultValue = booleanSettingBuilder.defaultValue;
   }

   public static BooleanSettingBuilder builder() {
      return new BooleanSettingBuilder();
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   @Override
   protected void readValue(DataInputStream dataInputStream) throws IOException {
      this.enabled = dataInputStream.readBoolean();
   }

   @Override
   protected void writeValue(DataOutputStream dataOutputStream) throws IOException {
      dataOutputStream.writeBoolean(this.enabled);
   }

   @Override
   public byte[] togglePayload() {
      BooleanSetting booleanSetting = this.copySetting();
      booleanSetting.setEnabled(!this.enabled);
      return booleanSetting.copyPayload();
   }

   private BooleanSetting copySetting() {
      BooleanSetting booleanSetting = ((BooleanSettingBuilder)((BooleanSettingBuilder)builder().id(this.getId())).name(this.getDisplayName()))
         .value(this.enabled)
         .defaultValue(this.defaultValue)
         .build();
      booleanSetting.restorePayload(this.copyPayload());
      return booleanSetting;
   }

   public BooleanSetting copy() {
      return this.copySetting();
   }

   public boolean isDefaultEnabled() {
      return this.defaultValue;
   }
}
