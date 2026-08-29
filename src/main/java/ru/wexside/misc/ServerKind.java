package ru.wexside.misc;

public enum ServerKind {
   GENERAL,
   OTHERS;

   public static ServerKind parse(String value) {
      return value != null && value.equalsIgnoreCase("others") ? OTHERS : GENERAL;
   }
}
