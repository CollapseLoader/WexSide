package ru.wexside.mixin;

import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_636;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wexside.WexSideClient;
import ru.wexside.event.BlockInteractEvent;
import ru.wexside.event.EntityAttackEvent;
import ru.wexside.event.EventBus;

@Mixin({class_636.class})
public abstract class ClientInteractionEventMixin {
   @Shadow
   @Final
   private class_310 field_3712;

   @Inject(
      method = {"method_2918"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void wexside$beforeAttack(class_1657 player, class_1297 target, CallbackInfo callback) {
      EventBus eventBus = WexSideClient.getEventBus();
      if (eventBus != null) {
         EntityAttackEvent event = new EntityAttackEvent(target);
         eventBus.post(event);
         if (event.isCancelled()) {
            callback.cancel();
         }
      }
   }

   @Inject(
      method = {"method_2896"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void wexside$beforeBlockInteract(class_746 player, class_1268 hand, class_3965 hit, CallbackInfoReturnable<class_1269> callback) {
      if (this.field_3712.field_1687 != null) {
         EventBus eventBus = WexSideClient.getEventBus();
         if (eventBus != null) {
            BlockInteractEvent event = new BlockInteractEvent(this.field_3712.field_1687.method_8320(hit.method_17777()).method_26204());
            eventBus.post(event);
            if (event.isCancelled()) {
               callback.setReturnValue(class_1269.field_5814);
            }
         }
      }
   }
}
