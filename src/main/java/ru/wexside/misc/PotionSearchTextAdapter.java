package ru.wexside.misc;

final class PotionSearchTextAdapter implements TextInputModel {
   private final PotionEditorState potionEditorState;

   PotionSearchTextAdapter(PotionEditorState potionEditorState) {
      this.potionEditorState = potionEditorState;
   }

   @Override
   public int getMaximumLength() {
      return 48;
   }

   @Override
   public String getText() {
      return this.potionEditorState.getSearchQuery();
   }

   @Override
   public void setText(String text) {
      this.potionEditorState.setSearchQuery(text == null ? "" : text);
   }
}
