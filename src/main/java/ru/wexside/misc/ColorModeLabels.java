package ru.wexside.misc;

import ru.wexside.setting.color.ColorMode;

public final class ColorModeLabels {
   private final ColorMode colorMode2;
   private final String string3;
   private final String string4;

   public ColorModeLabels(ColorMode colorMode2, String string, String string2) {
      this.colorMode2 = colorMode2 == null ? ColorMode.STATIC : colorMode2;
      this.string3 = string == null ? "" : string;
      this.string4 = string2 == null ? "" : string2;
   }

   public String getString() {
      return this.string3;
   }

   public String getString2() {
      return this.string4;
   }

   public ColorMode getColorMode() {
      return this.colorMode2;
   }
}
