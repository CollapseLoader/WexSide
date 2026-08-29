package ru.wexside.module;

public enum ModuleCategory {
   COMBAT("Combat"),
   MOVEMENT("Movement"),
   RENDER("Render"),
   PLAYER("Player"),
   MISC("Miscellaneous"),
   DISPLAY("Display"),
   HIDDEN("Hidden");

   private final String name;

   private ModuleCategory(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }
}
