package ru.wexside.event;

import ru.wexside.misc.TickPhase;

public class AttackWindowEvent implements Event {
   private final TickPhase tickPhase;

   public AttackWindowEvent(TickPhase tickPhase) {
      this.tickPhase = tickPhase;
   }

   public boolean isPre() {
      return this.isActive();
   }

   public boolean isPost() {
      return this.isAvailable();
   }

   public boolean isActive() {
      return this.tickPhase == TickPhase.PRE;
   }

   public boolean isAvailable() {
      return this.tickPhase == TickPhase.POST;
   }

   public TickPhase getPhase() {
      return this.tickPhase;
   }

   public TickPhase getTickPhase() {
      return this.tickPhase;
   }
}
