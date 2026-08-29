package ru.wexside.event;

public final class OverlayRenderEvent extends CancellableEvent {
   private final OverlayType type;

   public OverlayRenderEvent(OverlayType type) {
      this.type = type;
   }

   public OverlayType type() {
      return this.type;
   }
}
