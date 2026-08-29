package ru.wexside.event;

import net.minecraft.class_2596;

public class IncomingPacketEvent extends CancellableEvent implements Event {
   private final class_2596<?> packet2;

   public IncomingPacketEvent(class_2596<?> packet3) {
      this.packet2 = packet3;
   }

   public class_2596<?> getPacket() {
      return this.packet2;
   }
}
