package ru.wexside.event;

import net.minecraft.class_4587;

public final class WorldRenderEvent implements Event {
   private final float tickDelta;
   private final class_4587 matrices;

   public WorldRenderEvent(class_4587 matrices, float tickDelta) {
      this.matrices = matrices;
      this.tickDelta = tickDelta;
   }

   public class_4587 getMatrices() {
      return this.matrices;
   }

   @Deprecated
   public float getFloatType() {
      return this.tickDelta;
   }

   public float getTickDelta() {
      return this.tickDelta;
   }
}
