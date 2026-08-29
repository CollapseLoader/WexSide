package ru.wexside.misc;

import ru.wexside.input.BindInput;

public final class PotionEditorState {
   private PotionPresetDraft originalPreset;
   private int selectedSlot = -1;
   private final PotionPresetDraft workingCopy = new PotionPresetDraft("");
   private int selectorSlot = -1;
   private String searchQuery = "";

   public void beginEditing(PotionPresetDraft preset) {
      this.originalPreset = preset;
      this.selectedSlot = -1;
      this.selectorSlot = -1;
      this.workingCopy.setName(preset.getName());
      this.workingCopy.setBindInput(preset.getBindInput());

      for(int slot = 0; slot < 4; ++slot) {
         this.workingCopy.setPotionId(slot, preset.getPotionId(slot));
      }
   }

   public PotionPresetDraft getOriginalPreset() {
      return this.originalPreset;
   }

   public void reset() {
      this.originalPreset = null;
      this.selectedSlot = -1;
      this.selectorSlot = -1;
      this.workingCopy.setName("");
      this.workingCopy.setBindInput(BindInput.unbound());

      for(int slot = 0; slot < 4; ++slot) {
         this.workingCopy.setPotionId(slot, null);
      }
   }

   public void setSelectedSlot(int slot) {
      this.selectedSlot = slot;
   }

   public boolean isActive() {
      return this.originalPreset != null;
   }

   public int getSelectorSlot() {
      return this.selectorSlot;
   }

   public int getSelectedSlot() {
      return this.selectedSlot;
   }

   public PotionPresetDraft getWorkingCopy() {
      return this.workingCopy;
   }

   public void setSelectorSlot(int slot) {
      this.selectorSlot = slot;
   }

   public void applyWorkingCopyTo(PotionPresetDraft preset) {
      preset.setName(this.workingCopy.getName());
      preset.setBindInput(this.workingCopy.getBindInput());

      for(int slot = 0; slot < 4; ++slot) {
         preset.setPotionId(slot, this.workingCopy.getPotionId(slot));
      }
   }

   public String getSearchQuery() {
      return this.searchQuery;
   }

   public void setSearchQuery(String searchQuery) {
      this.searchQuery = searchQuery;
   }
}
