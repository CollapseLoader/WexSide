package ru.wexside.misc;

final class MutableTextBuffer implements TextInputModel {
   private final int maximumLength;
   private String text = "";

   MutableTextBuffer(int maximumLength) {
      this.maximumLength = maximumLength;
   }

   @Override
   public int getMaximumLength() {
      return this.maximumLength;
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   public void setText(String text) {
      this.text = text == null ? "" : text;
   }
}
