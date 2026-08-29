package ru.wexside.misc;

import java.util.List;

public final class ActionSequence implements InventoryAction {
   private final List<TimedAction> steps;

   public ActionSequence(List<TimedAction> steps) {
      this.steps = List.copyOf(steps);
   }

   public List<TimedAction> steps() {
      return this.steps;
   }

   public int maxDelay() {
      return this.steps.stream().mapToInt(TimedAction::delay).max().orElse(0);
   }
}
