package ru.wexside.event;

public final class OutgoingChatEvent extends CancellableEvent {
   private String message;

   public OutgoingChatEvent(String message) {
      this.message = message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public String getMessage() {
      return this.message;
   }

   @Deprecated
   public void setString(String message) {
      this.setMessage(message);
   }

   @Deprecated
   public String getString() {
      return this.getMessage();
   }
}
