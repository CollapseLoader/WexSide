package ru.wexside.mixin;

import net.minecraft.class_1268;
import net.minecraft.class_1309;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wexside.WexSideClient;
import ru.wexside.event.EventBus;
import ru.wexside.event.HandSwingSpeedEvent;
import ru.wexside.event.PreAttackEvent;

@Mixin({class_1309.class})
public abstract class LivingEntityEventMixin {
   @Inject(
      method = {"method_6028"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void applySwingDurationOverride(CallbackInfoReturnable<Integer> callback) {
      if ((Object)this == class_310.method_1551().field_1724) {
         EventBus events = WexSideClient.getEventBus();
         if (events != null) {
            HandSwingSpeedEvent event = new HandSwingSpeedEvent();
            events.post(event);
            if (event.isCancelled()) {
               callback.setReturnValue(Math.max(1, Math.round(6.0F * event.getSpeedMultiplier())));
            }
         }
      }
   }

   @Inject(
      method = {"method_23667"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void beforeLocalHandSwing(class_1268 hand, boolean fromServer, CallbackInfo callback) {
      class_1309 entity = (class_1309)(Object)this;
      if (entity == class_310.method_1551().field_1724 && !fromServer) {
         EventBus events = WexSideClient.getEventBus();
         if (events != null) {
            PreAttackEvent event = new PreAttackEvent(entity.method_36454());
            events.post(event);
            if (event.isCancelled()) {
               callback.cancel();
            }
         }
      }
   }
}
