package ru.wexside.setting.color;

public enum ColorMode {
   STATIC,
   ASTOLFO,
   DOUBLE_COLOR;

   public static ColorMode fromOrdinal(int ordinal) {
      ColorMode[] values = values();
      return ordinal >= 0 && ordinal < values.length ? values[ordinal] : STATIC;
   }
}
