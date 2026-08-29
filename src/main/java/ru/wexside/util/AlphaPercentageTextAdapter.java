package ru.wexside.util;

import java.util.function.Supplier;
import ru.wexside.misc.TextInputModel;

final class AlphaPercentageTextAdapter implements TextInputModel {
   private final HexColorEditor owner;
   private final Supplier<String> labelSupplier;

   AlphaPercentageTextAdapter(HexColorEditor owner, Supplier<String> labelSupplier) {
      this.owner = owner;
      this.labelSupplier = labelSupplier;
   }

   @Override
   public boolean accepts(char character, String currentText) {
      return Character.isDigit(character);
   }

   @Override
   public int getMaximumLength() {
      return 3;
   }

   private String normalize(String string) {
      if (string != null && !string.isBlank()) {
         int n = Integer.parseInt(string);
         return String.valueOf(this.owner.clampPercentage(n));
      } else {
         return "";
      }
   }

   @Override
   public void setText(String string) {
      if (string != null && !string.isBlank()) {
         int n = this.owner.clampPercentage(Integer.parseInt(this.normalize(string)));
         int[] nArray = ColorUtils.unpackRgba(this.owner.getColorSetting().getColor());
         int n2 = Math.round((float)n / 100.0F * 255.0F);
         this.owner.getColorSetting().setEditingColor(ColorUtils.rgba(nArray[0], nArray[1], nArray[2], n2));
      }
   }

   @Override
   public String getText() {
      return this.owner.getAlphaPercentage();
   }

   @Override
   public String getClipboardText() {
      return this.labelSupplier.get();
   }
}
