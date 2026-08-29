package ru.wexside.render.hand;

public enum EnergyAuraMode {
   STATIC("Static"),
   HAND_AURA("Hand Aura"),
   RIBBONS("Ribbons");

   private final String settingName;

   private EnergyAuraMode(String settingName) {
      this.settingName = settingName;
   }

   public static EnergyAuraMode fromSetting(String value) {
      for(EnergyAuraMode mode : values()) {
         if (mode.settingName.equalsIgnoreCase(value)) {
            return mode;
         }
      }

      return STATIC;
   }
}
