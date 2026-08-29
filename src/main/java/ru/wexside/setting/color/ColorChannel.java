package ru.wexside.setting.color;

public enum ColorChannel {
   PRIMARY,
   SECONDARY;

   public static ColorChannel fromOrdinal(int ordinal) {
      ColorChannel[] values = values();
      return ordinal >= 0 && ordinal < values.length ? values[ordinal] : PRIMARY;
   }
}
