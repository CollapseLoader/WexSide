package ru.wexside.mixin;

import net.minecraft.class_1058;
import net.minecraft.class_11659;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4603;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.WexSideClient;
import ru.wexside.event.EventBus;
import ru.wexside.event.OverlayRenderEvent;
import ru.wexside.event.OverlayType;

@Mixin({class_4603.class})
public abstract class InGameOverlayEventMixin {
   @Shadow
   private class_1799 field_59972;

   @Inject(
      method = {"method_70939"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void allowTotemAnimationCancellation(class_4587 matrices, float tickDelta, class_11659 queue, CallbackInfo callback) {
      if (this.field_59972 != null && this.field_59972.method_31574(class_1802.field_8288) && this.isCancelled(OverlayType.TOTEM)) {
         callback.cancel();
      }
   }

   @Shadow
   private static void method_23068(class_1058 sprite, class_4587 matrices, class_4597 vertexConsumers) {
      throw new AssertionError();
   }

   @Shadow
   private static void method_23070(class_4587 matrices, class_4597 vertexConsumers, class_1058 sprite) {
      throw new AssertionError();
   }

   @Redirect(
      method = {"method_23067"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_4603;method_23068(Lnet/minecraft/class_1058;Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;)V"
)
   )
   private void renderBlockOverlayConditionally(class_1058 sprite, class_4587 matrices, class_4597 vertexConsumers) {
      if (!this.isCancelled(OverlayType.BLOCK)) {
         method_23068(sprite, matrices, vertexConsumers);
      }
   }

   @Redirect(
      method = {"method_23067"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_4603;method_23070(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;Lnet/minecraft/class_1058;)V"
)
   )
   private void renderFireOverlayConditionally(class_4587 matrices, class_4597 vertexConsumers, class_1058 sprite) {
      if (!this.isCancelled(OverlayType.FIRE)) {
         method_23070(matrices, vertexConsumers, sprite);
      }
   }

   private boolean isCancelled(OverlayType type) {
      EventBus events = WexSideClient.getEventBus();
      if (events == null) {
         return false;
      } else {
         OverlayRenderEvent event = new OverlayRenderEvent(type);
         events.post(event);
         return event.isCancelled();
      }
   }
}
