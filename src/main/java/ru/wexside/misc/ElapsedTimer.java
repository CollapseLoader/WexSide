package ru.wexside.misc;

public final class ElapsedTimer {
   private long longType = System.currentTimeMillis();

   public void update() {
      this.longType = System.currentTimeMillis();
   }

   public boolean process(long l) {
      return this.getLongType() >= l;
   }

   public boolean process2(long l) {
      if (this.process(l)) {
         this.update();
         return true;
      } else {
         return false;
      }
   }

   public long getLongType() {
      return System.currentTimeMillis() - this.longType;
   }
}
