package ru.wexside.misc;

import net.minecraft.class_310;
import ru.wexside.input.InputBindings;
import ru.wexside.ui.GuiBounds;

public final class TextInputController {
   private static final int ESCAPE_KEY = 256;
   private static final int BACKSPACE_KEY = 259;
   private static final int LEFT_CONTROL_KEY = 341;
   private static final int RIGHT_CONTROL_KEY = 345;
   private static final int SELECT_ALL_KEY = 65;
   private static final int COPY_KEY = 67;
   private static final int PASTE_KEY = 86;
   private final TextInputModel model;
   private boolean focused;
   private boolean allSelected;
   private int focusTicks;

   public TextInputController(TextInputModel model) {
      this.model = model;
   }

   public boolean isAllSelected() {
      return this.allSelected;
   }

   public boolean isFocused() {
      return this.focused;
   }

   public boolean onKeyPressed(int keyCode) {
      if (!this.focused) {
         return false;
      } else {
         if (this.isControlPressed()) {
            if (keyCode == 65) {
               this.allSelected = !this.getText().isEmpty();
               return true;
            }

            if (keyCode == 67) {
               class_310.method_1551().field_1774.method_1455(this.model.getClipboardText());
               return true;
            }

            if (keyCode == 86) {
               String existingText = this.getInsertionPrefix();
               String pastedText = this.model.filterInput(class_310.method_1551().field_1774.method_1460(), existingText);
               this.setText(existingText + pastedText);
               this.allSelected = false;
               return true;
            }
         }

         if (keyCode == 256) {
            return false;
         } else if (keyCode == 259) {
            if (this.allSelected) {
               this.setText("");
               this.allSelected = false;
               return true;
            } else {
               String text = this.getText();
               if (!text.isEmpty()) {
                  this.setText(text.substring(0, text.length() - 1));
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   public boolean onCharTyped(char character) {
      if (this.focused && !Character.isISOControl(character) && !this.isControlPressed()) {
         String existingText = this.getInsertionPrefix();
         String acceptedText = this.model.filterInput(String.valueOf(character), existingText);
         if (acceptedText.isEmpty()) {
            return true;
         } else {
            this.setText(existingText + acceptedText);
            this.allSelected = false;
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean onMousePressed(GuiBounds bounds, int mouseX, int mouseY, int button) {
      boolean inside = bounds.contains((float)mouseX, (float)mouseY);
      if (!this.focused) {
         if (button == 0 && inside) {
            this.focus();
            return true;
         } else {
            return false;
         }
      } else {
         return inside;
      }
   }

   public void blurIfOutside(GuiBounds bounds, int mouseX, int mouseY) {
      if (this.focused && !bounds.contains((float)mouseX, (float)mouseY)) {
         this.blur();
      }
   }

   public void focus() {
      if (!this.focused) {
         this.focused = true;
         this.focusTicks = 0;
         this.model.onFocusGained();
      }
   }

   public void blur() {
      if (this.focused) {
         this.model.onFocusLost();
      }

      this.focused = false;
      this.allSelected = false;
      this.focusTicks = 0;
   }

   public void tick() {
      if (this.focused) {
         ++this.focusTicks;
      } else {
         this.focusTicks = 0;
      }
   }

   public String getText() {
      String text = this.model.getText();
      return text == null ? "" : text;
   }

   public boolean isCaretVisible() {
      return this.focused && this.focusTicks / 10 % 2 == 0;
   }

   private boolean isControlPressed() {
      return InputBindings.isKeyPressed(341) || InputBindings.isKeyPressed(345);
   }

   private String getInsertionPrefix() {
      return this.allSelected ? "" : this.getText();
   }

   private void setText(String text) {
      this.model.setText(text == null ? "" : text);
   }
}
