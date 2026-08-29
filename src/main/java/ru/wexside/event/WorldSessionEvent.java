package ru.wexside.event;

public record WorldSessionEvent(WorldSessionEvent.Change change) implements Event {
   public static enum Change {
      JOINED,
      DISCONNECTED;
   }
}
