package ru.wexside.render;

public enum GlowCompositeMode {
   INNER(true, false),
   OUTER(false, true),
   BOTH(true, true);

   private final boolean inner;
   private final boolean outer;

   private GlowCompositeMode(boolean inner, boolean outer) {
      this.inner = inner;
      this.outer = outer;
   }

   public boolean drawsInnerGlow() {
      return this.inner;
   }

   public boolean drawsOuterGlow() {
      return this.outer;
   }
}
