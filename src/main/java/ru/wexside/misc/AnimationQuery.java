package ru.wexside.misc;

import java.util.function.ToDoubleFunction;

public final class AnimationQuery {
   private final ToDoubleFunction<double[]> toDoubleFunction;
   private final int slot;

   public AnimationQuery(int n, ToDoubleFunction<double[]> toDoubleFunction) {
      this.slot = n;
      this.toDoubleFunction = toDoubleFunction;
   }

   public ToDoubleFunction<double[]> values3() {
      return this.toDoubleFunction;
   }

   public int getIntType() {
      return this.slot;
   }
}
