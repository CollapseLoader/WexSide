package ru.wexside.setting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import ru.wexside.config.ConfigSerializable;

public class ModeSetting extends Setting implements ConfigSerializable {
   private String selectedOption;
   private int decodedIndex = -1;
   private String[] options;
   private int selectedIndex;

   ModeSetting(ModeSettingBuilder modeSettingBuilder) {
      super(modeSettingBuilder);
      this.options = modeSettingBuilder.options;
      if (modeSettingBuilder.defaultOption != null) {
         this.selectedOption = modeSettingBuilder.defaultOption;

         for(int i = 0; i < this.options.length; ++i) {
            if (this.options[i].equals(modeSettingBuilder.defaultOption)) {
               this.selectedIndex = i;
               break;
            }
         }
      } else {
         this.selectedIndex = modeSettingBuilder.defaultIndex;
         this.selectedOption = this.options.length > this.selectedIndex ? this.options[this.selectedIndex] : null;
      }
   }

   public static ModeSettingBuilder getModeSettingBuilder() {
      return new ModeSettingBuilder();
   }

   public String getSelectedOption() {
      return this.selectedOption;
   }

   @Override
   protected void readValue(DataInputStream dataInputStream) throws IOException {
      String decodedOption = dataInputStream.readUTF();
      List<String> availableOptions = this.options == null ? List.of() : List.of(this.options);
      int optionIndex = availableOptions.indexOf(decodedOption);
      if (optionIndex < 0 && this.options != null && this.options.length > 0) {
         optionIndex = 0;
         decodedOption = this.options[0];
      }

      this.selectedOption = decodedOption;
      this.selectedIndex = Math.max(optionIndex, 0);
      this.decodedIndex = optionIndex;
   }

   @Override
   protected void writeValue(DataOutputStream dataOutputStream) throws IOException {
      dataOutputStream.writeUTF(this.selectedOption == null ? "" : this.selectedOption);
   }

   public void setSelectedOption(String selectedOption) {
      this.selectedOption = selectedOption;
      this.selectedIndex = Math.max(List.of(this.options).indexOf(selectedOption), 0);
   }

   public void setOptions(String[] options) {
      this.options = options == null ? new String[0] : options;
      int optionIndex = -1;

      for(int i = 0; i < this.options.length; ++i) {
         if (this.options[i].equals(this.selectedOption)) {
            optionIndex = i;
            break;
         }
      }

      if (optionIndex < 0) {
         optionIndex = this.options.length > 0 ? 0 : -1;
         this.selectedOption = optionIndex >= 0 ? this.options[0] : null;
      }

      this.selectedIndex = Math.max(optionIndex, 0);
      this.decodedIndex = optionIndex;
   }

   public String[] getOptions() {
      return (String[])this.options.clone();
   }

   private ModeSetting copySetting() {
      ModeSetting modeSetting = ((ModeSettingBuilder)((ModeSettingBuilder)getModeSettingBuilder().id(this.getId())).name(this.getDisplayName()))
         .options(this.options)
         .defaultOption(this.selectedOption)
         .build();
      modeSetting.restorePayload(this.copyPayload());
      return modeSetting;
   }

   public ModeSetting copy() {
      return this.copySetting();
   }

   public int getSelectedIndex() {
      return this.selectedIndex;
   }

   public int getDecodedIndex() {
      return this.decodedIndex;
   }
}
