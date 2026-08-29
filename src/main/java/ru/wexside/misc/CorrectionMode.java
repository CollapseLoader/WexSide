package ru.wexside.misc;

public enum CorrectionMode {
   NONE,
   FOCUSED,
   FREE,
   LOCK;

   public static CorrectionMode fromName(String name) {
      if (name == null) {
         return NONE;
      } else {
         return switch(name) {
            case "Focused" -> FOCUSED;
            case "Free" -> FREE;
            case "Lock" -> LOCK;
            default -> NONE;
         };
      }
   }

   public static CorrectionMode process(String name) {
      return fromName(name);
   }
}
