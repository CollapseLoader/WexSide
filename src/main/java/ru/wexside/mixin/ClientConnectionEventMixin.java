package ru.wexside.mixin;

import io.netty.channel.ChannelFutureListener;
import net.minecraft.class_2535;
import net.minecraft.class_2547;
import net.minecraft.class_2596;
import net.minecraft.class_2598;
import net.minecraft.class_8697;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.WexSideClient;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.event.OutgoingPacketEvent;

@Mixin({class_2535.class})
public abstract class ClientConnectionEventMixin {
   @Inject(
      method = {"method_52906"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void wexside$onPacketSend(class_2596<?> packet, ChannelFutureListener listener, boolean flush, CallbackInfo callback) {
      class_2535 connection = (class_2535)(Object)this;
      if (connection.method_36121() == class_2598.field_11942) {
         EventBus eventBus = WexSideClient.getEventBus();
         if (eventBus != null) {
            OutgoingPacketEvent event = new OutgoingPacketEvent(packet);
            eventBus.post(event);
            if (event.isCancelled()) {
               callback.cancel();
            }
         }
      }
   }

   @Inject(
      method = {"method_10759"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static <T extends class_2547> void wexside$onPacketReceive(class_2596<T> packet, class_2547 listener, CallbackInfo callback) {
      if (listener instanceof class_8697) {
         EventBus eventBus = WexSideClient.getEventBus();
         if (eventBus != null) {
            IncomingPacketEvent event = new IncomingPacketEvent(packet);
            eventBus.post(event);
            if (event.isCancelled()) {
               callback.cancel();
            }
         }
      }
   }
}
