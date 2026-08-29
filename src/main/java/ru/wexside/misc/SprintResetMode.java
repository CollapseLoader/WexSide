package ru.wexside.misc;

public enum SprintResetMode {
   PACKET(true, false),
   NONE(false, false),
   LEGIT(false, true),
   SEMI_LEGIT(true, true);

   private final boolean packetBased;
   private final boolean clientMovement;

   private SprintResetMode(boolean packetBased, boolean clientMovement) {
      this.packetBased = packetBased;
      this.clientMovement = clientMovement;
   }

   public static SprintResetMode process(String name) {
      if (name == null) {
         return PACKET;
      } else {
         return switch(name) {
            case "None" -> NONE;
            case "Semi-Legit" -> SEMI_LEGIT;
            case "Legit" -> LEGIT;
            default -> PACKET;
         };
      }
   }

   public boolean isActive() {
      return this.packetBased;
   }

   public boolean isAvailable() {
      return this.clientMovement;
   }
}
