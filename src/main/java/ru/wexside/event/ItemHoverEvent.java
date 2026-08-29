package ru.wexside.event;

import net.minecraft.class_1799;

public class ItemHoverEvent extends CancellableEvent implements Event {
   private final class_1799 stack;

   public ItemHoverEvent(class_1799 stack) {
      this.stack = stack;
   }

   public class_1799 getStack() {
      return this.stack;
   }
}
