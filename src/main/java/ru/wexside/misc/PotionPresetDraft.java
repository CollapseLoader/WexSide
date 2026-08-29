package ru.wexside.misc;

import java.util.ArrayList;
import java.util.List;
import ru.wexside.input.BindInput;

public final class PotionPresetDraft {
   public static final int SLOT_COUNT = 4;
   private final String[] potionIds = new String[4];
   private String name;
   private boolean favorite;
   private BindInput bindInput = BindInput.unbound();

   public PotionPresetDraft(String name) {
      this.name = name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int findFirstEmptySlot() {
      for(int slot = 0; slot < 4; ++slot) {
         if (this.potionIds[slot] == null) {
            return slot;
         }
      }

      return -1;
   }

   public String[] getPotionIds() {
      return (String[])this.potionIds.clone();
   }

   public void setBindInput(BindInput bindInput) {
      this.bindInput = bindInput;
   }

   public String getPotionId(int slot) {
      return slot >= 0 && slot < 4 ? this.potionIds[slot] : null;
   }

   public boolean isFavorite() {
      return this.favorite;
   }

   public void setFavorite(boolean favorite) {
      this.favorite = favorite;
   }

   public String getName() {
      return this.name;
   }

   public boolean isEmpty() {
      for(String potionId : this.potionIds) {
         if (potionId != null) {
            return false;
         }
      }

      return true;
   }

   public BindInput getBindInput() {
      return this.bindInput;
   }

   public PotionPresetDraft copy() {
      PotionPresetDraft copy = new PotionPresetDraft(this.name);
      copy.bindInput = this.bindInput;
      copy.favorite = this.favorite;
      System.arraycopy(this.potionIds, 0, copy.potionIds, 0, 4);
      return copy;
   }

   public void setPotionId(int slot, String potionId) {
      if (slot >= 0 && slot < 4) {
         this.potionIds[slot] = potionId;
      }
   }

   public List<PotionCatalogEntry> getPotions() {
      ArrayList<PotionCatalogEntry> potions = new ArrayList<>();

      for(String potionId : this.potionIds) {
         PotionCatalogEntry potion = PotionCatalog.findById(potionId);
         if (potion != null) {
            potions.add(potion);
         }
      }

      return potions;
   }
}
