package ru.wexside.mixin;

import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wexside.WexSideClient;
import ru.wexside.event.EventBus;
import ru.wexside.event.MovementSlowdownEvent;

@Mixin({class_746.class})
public abstract class ClientPlayerMovementEventMixin {
   @Inject(
      method = {"method_20303"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void allowSlowdownCancellation(CallbackInfoReturnable<Boolean> callback) {
      EventBus events = WexSideClient.getEventBus();
      if (events != null) {
         MovementSlowdownEvent event = new MovementSlowdownEvent();
         events.post(event);
         if (event.isCancelled()) {
            callback.setReturnValue(false);
         }
      }
   }
}
