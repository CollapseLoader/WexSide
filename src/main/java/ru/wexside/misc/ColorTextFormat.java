package ru.wexside.misc;

public enum ColorTextFormat {
   HEX("HEX"),
   RGBA("RGBA");

   public final String title;

   private ColorTextFormat(String title) {
      this.title = title;
   }

   public static ColorTextFormat fromIndex(int index) {
      return index == 1 ? RGBA : HEX;
   }
}
