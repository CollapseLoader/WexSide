package ru.wexside.util;

import ru.wexside.misc.TextInputModel;
import ru.wexside.setting.RangeSetting;

public final class RangeValueTextAdapter implements TextInputModel {
   private String editingText;
   private final RangeSetting rangeSetting;
   private final boolean lowerBound;

   public RangeValueTextAdapter(RangeSetting rangeSetting, boolean lowerBound) {
      this.rangeSetting = rangeSetting;
      this.lowerBound = lowerBound;
   }

   @Override
   public boolean accepts(char character, String currentText) {
      if (Character.isDigit(character)) {
         return true;
      } else if ((character == '.' || character == ',') && this.rangeSetting.getPrecision() > 0) {
         return !currentText.contains(".") && !currentText.contains(",");
      } else {
         return character == '-' && this.rangeSetting.getMinimum() < 0.0 && currentText.isEmpty();
      }
   }

   private void applyValue() {
      if (this.editingText != null
         && !this.editingText.isBlank()
         && !"-".equals(this.editingText)
         && !".".equals(this.editingText)
         && !"-.".equals(this.editingText)) {
         try {
            double value = Double.parseDouble(this.editingText);
            value = Math.max(this.rangeSetting.getMinimum(), Math.min(this.rangeSetting.getMaximum(), value));
            if (this.rangeSetting.hasSnapStep()) {
               value = NumberFormatting.snap(value, this.rangeSetting.getMinimum(), this.rangeSetting.getMaximum(), this.rangeSetting.getSnapStep());
            }

            value = NumberFormatting.round(value, this.rangeSetting.getPrecision());
            double range = this.rangeSetting.getMaximum() - this.rangeSetting.getMinimum();
            if (range <= 0.0) {
               return;
            }

            double normalizedValue = (value - this.rangeSetting.getMinimum()) / range;
            normalizedValue = Math.max(0.0, Math.min(1.0, normalizedValue));
            if (this.lowerBound) {
               this.rangeSetting.setLowerNormalizedValue(Math.min(this.rangeSetting.getUpperNormalizedValue(), normalizedValue));
               return;
            }

            this.rangeSetting.setUpperNormalizedValue(Math.max(this.rangeSetting.getLowerNormalizedValue(), normalizedValue));
         } catch (NumberFormatException var7) {
         }
      }
   }

   private String formatValue(double value) {
      return NumberFormatting.format(value, this.rangeSetting.getPrecision());
   }

   @Override
   public void onFocusGained() {
      this.editingText = this.getText();
   }

   @Override
   public int getMaximumLength() {
      return Math.max(this.formatValue(this.rangeSetting.getMinimum()).length(), this.formatValue(this.rangeSetting.getMaximum()).length()) + 2;
   }

   private String formattedValue() {
      return this.formatValue(this.lowerBound ? this.rangeSetting.getLowerUnscaledValue() : this.rangeSetting.getUpperUnscaledValue());
   }

   @Override
   public void onFocusLost() {
      this.applyValue();
      this.editingText = null;
   }

   @Override
   public String getText() {
      return this.editingText != null ? this.editingText : this.formattedValue();
   }

   private String normalize(String text) {
      return text == null ? "" : text.replace(',', '.');
   }

   @Override
   public String filterInput(String input, String existingText) {
      return this.normalize(TextInputModel.super.filterInput(input, existingText));
   }

   @Override
   public void setText(String text) {
      this.editingText = text == null ? "" : this.normalize(text);
      this.applyValue();
   }
}
