package ru.wexside.event;

import net.minecraft.class_4587;

public final class HandRenderEvent implements Event {
   private final HandRenderPhase phase;
   private final class_4587 matrices;
   private final float tickDelta;

   public HandRenderEvent(HandRenderPhase phase, class_4587 matrices, float tickDelta) {
      this.phase = phase;
      this.matrices = matrices;
      this.tickDelta = tickDelta;
   }

   public HandRenderPhase phase() {
      return this.phase;
   }

   public class_4587 matrices() {
      return this.matrices;
   }

   public float tickDelta() {
      return this.tickDelta;
   }
}
