package ru.wexside.misc;

public final class RunnableAction implements InventoryAction {
   public final Runnable runnable;

   public RunnableAction(Runnable runnable) {
      this.runnable = runnable;
   }

   public Runnable runnable() {
      return this.runnable;
   }
}
