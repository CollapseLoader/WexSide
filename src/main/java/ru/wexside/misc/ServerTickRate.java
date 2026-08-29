package ru.wexside.misc;

import net.minecraft.class_2761;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;

public class ServerTickRate {
   private float value;
   private long lastTimeUpdateMillis;
   static final int slot = 20;
   static final float value2 = 20.0F;
   static final float value3 = 0.85F;

   public ServerTickRate(EventBus eventBus) {
      eventBus.subscribe(IncomingPacketEvent.class, this::onIncomingPacket);
   }

   public float process(float f) {
      return this.value <= 0.0F ? f : f * (20.0F / this.value);
   }

   private void onIncomingPacket(IncomingPacketEvent gameEvent5) {
      if (gameEvent5.getPacket() instanceof class_2761) {
         long l2 = System.currentTimeMillis();
         long l;
         if (this.lastTimeUpdateMillis > 0L && (l = l2 - this.lastTimeUpdateMillis) > 0L && l < 5000L) {
            float f = 20000.0F / (float)l;
            if (f > 22.0F) {
               f = 22.0F;
            }

            if (f < 1.0F) {
               f = 1.0F;
            }

            this.value = this.value * 0.85F + f * 0.14999998F;
         }

         this.lastTimeUpdateMillis = l2;
      }
   }

   public float getFloatType() {
      return this.value;
   }
}
