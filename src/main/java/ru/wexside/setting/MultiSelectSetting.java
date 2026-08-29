package ru.wexside.setting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.misc.MultiSelectAnimation;

public class MultiSelectSetting extends Setting implements ConfigSerializable {
   private List<String> selectedOptions;
   private int[] selectionHashes;
   private boolean optionListEnabled;
   private String actionLabel;
   private String[] options;
   private Runnable action;
   private MultiSelectAnimation animation;

   MultiSelectSetting(MultiSelectSettingBuilder multiSelectSettingBuilder) {
      super(multiSelectSettingBuilder);
      this.initializeOptions(multiSelectSettingBuilder.options, multiSelectSettingBuilder.selectAll);
      this.optionListEnabled = multiSelectSettingBuilder.optionListEnabled;
      this.actionLabel = multiSelectSettingBuilder.actionLabel;
      this.action = multiSelectSettingBuilder.action;
      this.animation = multiSelectSettingBuilder.animation;
   }

   public static MultiSelectSettingBuilder getMultiSelectSettingBuilder() {
      return new MultiSelectSettingBuilder();
   }

   public List<String> getSelectedOptions() {
      return this.selectedOptions;
   }

   @Override
   protected void readValue(DataInputStream dataInputStream) throws IOException {
      for(int index = 0; index < this.selectionHashes.length; ++index) {
         this.selectionHashes[index] = 0;
      }

      this.selectedOptions.clear();
      int selectedCount = dataInputStream.readInt();

      for(int i = 0; i < selectedCount; ++i) {
         String selectedOption = dataInputStream.readUTF();

         for(int optionIndex = 0; optionIndex < this.options.length; ++optionIndex) {
            String option = this.options[optionIndex];
            if (option.equals(selectedOption)) {
               this.selectedOptions.add(selectedOption);
               this.selectionHashes[optionIndex] = selectedOption.hashCode();
            }
         }
      }
   }

   @Override
   protected void writeValue(DataOutputStream dataOutputStream) throws IOException {
      List<String> selectedSnapshot = new ArrayList<>(this.selectedOptions);
      dataOutputStream.writeInt(selectedSnapshot.size());

      for(String option : selectedSnapshot) {
         dataOutputStream.writeUTF(option);
      }
   }

   private MultiSelectSetting copySetting() {
      MultiSelectSetting multiSelectSetting = ((MultiSelectSettingBuilder)((MultiSelectSettingBuilder)getMultiSelectSettingBuilder().id(this.getId()))
            .name(this.getDisplayName()))
         .options(this.options)
         .optionListEnabled(false)
         .build();
      multiSelectSetting.restorePayload(this.copyPayload());
      return multiSelectSetting;
   }

   public MultiSelectSetting copy() {
      return this.copySetting();
   }

   private void initializeOptions(String[] options, boolean selectAll) {
      this.selectionHashes = new int[options.length];
      this.selectedOptions = new ArrayList<>();
      this.options = options;
      if (selectAll) {
         for(int i = 0; i < options.length; ++i) {
            this.selectionHashes[i] = options[i].hashCode();
            this.selectedOptions.add(options[i]);
         }
      }
   }

   public void setOptions(String[] options) {
      String[] newOptions = options == null ? new String[0] : options;
      List<String> previousSelection = new ArrayList<>(this.selectedOptions);
      this.options = newOptions;
      this.selectionHashes = new int[newOptions.length];
      this.selectedOptions.clear();

      for(int i = 0; i < newOptions.length; ++i) {
         if (previousSelection.contains(newOptions[i])) {
            this.selectedOptions.add(newOptions[i]);
            this.selectionHashes[i] = newOptions[i].hashCode();
         }
      }
   }

   public boolean isOptionListEnabled() {
      return this.optionListEnabled;
   }

   public Runnable getAction() {
      return this.action;
   }

   public String[] getOptions() {
      return (String[])this.options.clone();
   }

   public String getActionLabel() {
      return this.actionLabel;
   }

   public int[] getSelectionHashes() {
      return (int[])this.selectionHashes.clone();
   }

   public MultiSelectAnimation getAnimation() {
      return this.animation;
   }
}
