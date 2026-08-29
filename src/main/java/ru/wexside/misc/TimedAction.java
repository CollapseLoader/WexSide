package ru.wexside.misc;

public final class TimedAction {
   public final int delay;
   public final InventoryAction action;

   public TimedAction(int delay, InventoryAction action) {
      this.delay = delay;
      this.action = action;
   }

   public int delay() {
      return this.delay;
   }

   public InventoryAction action() {
      return this.action;
   }
}
