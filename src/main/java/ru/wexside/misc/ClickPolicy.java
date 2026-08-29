package ru.wexside.misc;

public enum ClickPolicy {
   SILENT(0, 0, false),
   VISIBLE(2, 0, false),
   SWAP(5, 5, true);

   public final int beforeDelay;
   public final int afterDelay;
   public final boolean closeScreenAfterwards;

   private ClickPolicy(int beforeDelay, int afterDelay, boolean closeScreenAfterwards) {
      this.beforeDelay = beforeDelay;
      this.afterDelay = afterDelay;
      this.closeScreenAfterwards = closeScreenAfterwards;
   }

   public boolean hasTiming() {
      return this.beforeDelay > 0 || this.afterDelay > 0 || this.closeScreenAfterwards;
   }
}
