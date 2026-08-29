package ru.wexside.event;

import net.minecraft.class_2248;

public class BlockInteractEvent extends CancellableEvent implements Event {
   private final class_2248 block;

   public BlockInteractEvent(class_2248 block) {
      this.block = block;
   }

   public class_2248 getBlock() {
      return this.block;
   }
}
