package ru.wexside.misc;

public interface TextInputModel {
   default boolean accepts(char character, String currentText) {
      return true;
   }

   default void onFocusGained() {
   }

   int getMaximumLength();

   default void onFocusLost() {
   }

   String getText();

   default String filterInput(String input, String existingText) {
      if (input != null && !input.isEmpty()) {
         String prefix = existingText == null ? "" : existingText;
         StringBuilder acceptedText = new StringBuilder(input.length());

         for(int index = 0; index < input.length(); ++index) {
            char character = input.charAt(index);
            if (!Character.isISOControl(character)) {
               if (prefix.length() + acceptedText.length() >= this.getMaximumLength()) {
                  break;
               }

               if (this.accepts(character, prefix + acceptedText)) {
                  acceptedText.append(character);
               }
            }
         }

         return acceptedText.toString();
      } else {
         return "";
      }
   }

   default String getClipboardText() {
      return this.getText();
   }

   void setText(String var1);
}
