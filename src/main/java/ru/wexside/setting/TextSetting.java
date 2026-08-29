package ru.wexside.setting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.misc.TextSettingLayout;

public final class TextSetting extends Setting implements ConfigSerializable {
   private TextSettingLayout layout;
   private int maxLength;
   private String value;

   TextSetting(TextSettingBuilder textSettingBuilder) {
      super(textSettingBuilder);
      this.value = textSettingBuilder.initialValue == null ? "" : textSettingBuilder.initialValue;
      this.layout = textSettingBuilder.layout;
      this.maxLength = Math.max(0, textSettingBuilder.maxLength);
   }

   @Override
   protected void readValue(DataInputStream dataInputStream) throws IOException {
      this.setValue(dataInputStream.readUTF());
   }

   @Override
   protected void writeValue(DataOutputStream dataOutputStream) throws IOException {
      dataOutputStream.writeUTF(this.value == null ? "" : this.value);
   }

   public static TextSettingBuilder getTextSettingBuilder() {
      return new TextSettingBuilder();
   }

   public String getValue() {
      return this.value;
   }

   public int getMaxLength() {
      return this.maxLength;
   }

   public TextSettingLayout getLayout() {
      return this.layout;
   }

   public void setValue(String value) {
      String normalizedValue = value == null ? "" : value;
      if (normalizedValue.length() > this.maxLength) {
         normalizedValue = normalizedValue.substring(0, this.maxLength);
      }

      this.value = normalizedValue;
   }

   public boolean isExpanded() {
      return this.layout == TextSettingLayout.EXPANDED;
   }

   public void setLayout(TextSettingLayout layout) {
      this.layout = layout;
   }

   public void setMaxLength(int maxLength) {
      this.maxLength = Math.max(0, maxLength);
      this.setValue(this.value);
   }

   private TextSetting copySetting() {
      TextSettingBuilder textSettingBuilder = ((TextSettingBuilder)((TextSettingBuilder)getTextSettingBuilder().id(this.getId())).name(this.getDisplayName()))
         .value(this.value)
         .maxLength(this.maxLength);
      if (this.isExpanded()) {
         textSettingBuilder.expanded();
      } else {
         textSettingBuilder.compact();
      }

      TextSetting textSetting = textSettingBuilder.build();
      textSetting.restorePayload(this.copyPayload());
      return textSetting;
   }

   public TextSetting copy() {
      return this.copySetting();
   }
}
