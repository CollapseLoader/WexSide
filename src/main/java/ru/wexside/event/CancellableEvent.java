package ru.wexside.event;

public abstract class CancellableEvent implements Event {
   private boolean cancelled;

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void cancel() {
      this.cancelled = true;
   }

   @Deprecated
   public boolean isActive() {
      return this.isCancelled();
   }

   @Deprecated
   public void update() {
      this.cancel();
   }
}
