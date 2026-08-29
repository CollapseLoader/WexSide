package ru.wexside.util;

import ru.wexside.misc.TextInputModel;
import ru.wexside.setting.NumberSetting;

public final class NumericTextEditor implements TextInputModel {
   private final NumberSetting setting;
   private String editingText;

   public NumericTextEditor(NumberSetting setting) {
      this.setting = setting;
   }

   @Override
   public boolean accepts(char character, String currentText) {
      if (Character.isDigit(character)) {
         return true;
      } else if ((character == '.' || character == ',') && this.setting.getPrecision() > 0) {
         return !currentText.contains(".") && !currentText.contains(",");
      } else {
         return character == '-' && this.setting.getMinimum() < 0.0 && currentText.isEmpty();
      }
   }

   private void applyEditingText() {
      if (this.editingText != null
         && !this.editingText.isBlank()
         && !"-".equals(this.editingText)
         && !".".equals(this.editingText)
         && !"-.".equals(this.editingText)) {
         try {
            double value = Double.parseDouble(this.editingText);
            value = Math.max(this.setting.getMinimum(), Math.min(this.setting.getMaximum(), value));
            if (this.setting.hasSnapStep()) {
               value = NumberFormatting.snap(value, this.setting.getMinimum(), this.setting.getMaximum(), this.setting.getSnapStep());
            }

            value = NumberFormatting.round(value, this.setting.getPrecision());
            this.setting.setValue(value);
         } catch (NumberFormatException var3) {
         }
      }
   }

   private String formatNumber(double value) {
      return NumberFormatting.format(value, this.setting.getPrecision());
   }

   @Override
   public void onFocusGained() {
      this.editingText = this.displayValue();
   }

   @Override
   public int getMaximumLength() {
      return Math.max(this.formatNumber(this.setting.getMinimum()).length(), this.formatNumber(this.setting.getMaximum()).length()) + 2;
   }

   private String displayValue() {
      return this.formatNumber(this.setting.getUnscaledValue());
   }

   @Override
   public void onFocusLost() {
      this.applyEditingText();
      this.editingText = null;
   }

   @Override
   public String getText() {
      return this.editingText != null ? this.editingText : this.displayValue();
   }

   @Override
   public String filterInput(String text, String existingText) {
      return normalize(TextInputModel.super.filterInput(text, existingText));
   }

   @Override
   public void setText(String text) {
      this.editingText = normalize(text == null ? "" : text);
      this.applyEditingText();
   }

   private static String normalize(String text) {
      return text.replace(',', '.');
   }
}
