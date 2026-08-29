package ru.wexside.render.hand;

public enum HandMaterialMode {
   GLASS("Glass Hand"),
   LIQUID_GLASS("Liquid Glass"),
   CHROME("Chrome"),
   SIMPLE("Simple"),
   FILL("Fill");

   private final String settingName;

   private HandMaterialMode(String settingName) {
      this.settingName = settingName;
   }

   public static HandMaterialMode fromSetting(String value) {
      for(HandMaterialMode mode : values()) {
         if (mode.settingName.equalsIgnoreCase(value)) {
            return mode;
         }
      }

      return GLASS;
   }
}
