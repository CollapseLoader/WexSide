package ru.wexside.model.esp;

public enum EspTargetType {
   PLAYERS("Players", "л"),
   ENTITIES("Entities", "Й"),
   ITEMS("Items", "У"),
   SELF("Self", "4");

   private final String title;
   private final String icon;

   private EspTargetType(String title, String icon) {
      this.title = title;
      this.icon = icon;
   }

   public String getTitle() {
      return this.title;
   }

   public String getIcon() {
      return this.icon;
   }
}
