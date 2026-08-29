package ru.wexside.util;

import net.minecraft.class_1661;
import net.minecraft.class_2735;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;

public class HotbarSlotLock {
   private long longType;
   private int slot = -1;

   public HotbarSlotLock(EventBus eventBus) {
      eventBus.subscribe(ClientTickEvent.class, ignored -> this.update2());
      eventBus.subscribe(IncomingPacketEvent.class, this::onIncomingPacket);
   }

   public boolean isActive() {
      return this.slot != -1;
   }

   public void process(int n, long l) {
      if (n >= 0 && n <= 8) {
         this.slot = n;
         this.longType = System.currentTimeMillis() + Math.max(50L, l);
      }
   }

   private void onIncomingPacket(IncomingPacketEvent gameEvent5) {
      if (this.slot != -1) {
         if (gameEvent5.getPacket() instanceof class_2735) {
            class_746 player2 = class_310.method_1551().field_1724;
            if (player2 != null) {
               class_1661 inv = player2.method_31548();
               if (inv.method_67532() != this.slot) {
                  inv.method_61496(this.slot);
               }
            }
         }
      }
   }

   public void update() {
      this.slot = -1;
      this.longType = 0L;
   }

   private void update2() {
      if (this.slot != -1) {
         if (System.currentTimeMillis() >= this.longType) {
            this.update();
         }
      }
   }
}
