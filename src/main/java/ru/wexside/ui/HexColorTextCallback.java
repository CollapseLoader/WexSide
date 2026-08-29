package ru.wexside.ui;

import java.util.Locale;
import ru.wexside.misc.TextInputModel;
import ru.wexside.setting.ColorSetting;
import ru.wexside.util.ColorUtils;

public final class HexColorTextCallback implements TextInputModel {
   private final ColorSetting color;

   public HexColorTextCallback(ColorSetting color) {
      this.color = color;
   }

   @Override
   public boolean accepts(char character, String currentText) {
      return character == '#' ? currentText == null || currentText.indexOf(35) < 0 : Character.digit(character, 16) >= 0;
   }

   @Override
   public int getMaximumLength() {
      return 7;
   }

   @Override
   public String getText() {
      return this.color.getAstolfoHex();
   }

   @Override
   public void setText(String value) {
      String normalized = normalize(value);
      if (normalized.length() == 7) {
         int rgb = Integer.parseInt(normalized.substring(1), 16);
         int alpha = ColorUtils.unpackRgba(this.color.getColor())[3];
         this.color.setEditingColor(ColorUtils.rgba(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF, alpha));
      }
   }

   private static String normalize(String value) {
      String input = value == null ? "" : value.toUpperCase(Locale.ROOT);
      StringBuilder result = new StringBuilder(7).append('#');

      for(int i = 0; i < input.length() && result.length() < 7; ++i) {
         char character = input.charAt(i);
         if (Character.digit(character, 16) >= 0) {
            result.append(character);
         }
      }

      return result.toString();
   }
}
