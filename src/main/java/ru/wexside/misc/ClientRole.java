package ru.wexside.misc;

public enum ClientRole {
   USER("User", 0, "u"),
   PREMIUM("Premium", -7709441, "p"),
   TESTER("Tester", -11546113, "t"),
   MEDIA("Media", -37987, "m"),
   PARTNER("Partner", -14249, "p"),
   MODERATOR("Moderator", -11153017, "m"),
   DEVELOPER("Developer", -10704641, "d"),
   ADMINISTRATOR("Administrator", -41872, "a");

   private final String displayName;
   private final int color;
   private final String iconGlyph;

   private ClientRole(String displayName, int color, String iconGlyph) {
      this.displayName = displayName;
      this.color = color;
      this.iconGlyph = iconGlyph;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public int getColor() {
      return this.color;
   }

   public String getIconGlyph() {
      return this.iconGlyph;
   }
}
