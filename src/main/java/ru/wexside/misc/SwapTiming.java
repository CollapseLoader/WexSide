package ru.wexside.misc;

public enum SwapTiming {
   DEFAULT(1, 1, 2, 3),
   LEGIT(2, 3, 4, 6),
   FUNTIME(1, 2, 7, 8);

   public final int beforeClickDelay;
   public final int afterFirstClickDelay;
   public final int secondClickDelay;
   public final int completionDelay;

   private SwapTiming(int beforeClickDelay, int afterFirstClickDelay, int secondClickDelay, int completionDelay) {
      this.beforeClickDelay = beforeClickDelay;
      this.afterFirstClickDelay = afterFirstClickDelay;
      this.secondClickDelay = secondClickDelay;
      this.completionDelay = completionDelay;
   }
}
