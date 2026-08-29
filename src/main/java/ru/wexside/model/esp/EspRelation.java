package ru.wexside.model.esp;

public enum EspRelation {
   DEFAULT("Default"),
   FRIEND("Friend");

   private final String title;

   private EspRelation(String title) {
      this.title = title;
   }

   public String getTitle() {
      return this.title;
   }
}
