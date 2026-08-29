package ru.wexside.event;

public final class DamageEvent implements Event {
   private final DamageType type;

   public DamageEvent(DamageType type) {
      this.type = type;
   }

   public DamageType type() {
      return this.type;
   }
}
