package ru.wexside.mixin;

import net.minecraft.class_11659;
import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1799;
import net.minecraft.class_4587;
import net.minecraft.class_742;
import net.minecraft.class_746;
import net.minecraft.class_759;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.WexSideClient;
import ru.wexside.event.EventBus;
import ru.wexside.event.FirstPersonItemTransformEvent;
import ru.wexside.event.FirstPersonSwingTransformEvent;
import ru.wexside.event.HandRenderEvent;
import ru.wexside.event.HandRenderPhase;

@Mixin({class_759.class})
public abstract class HeldItemRendererEventMixin {
   @Unique
   private class_1268 wexside$currentHand;
   @Unique
   private float wexside$currentEquipProgress;

   @Shadow
   private void method_3217(class_4587 matrices, class_1306 arm, float swingProgress) {
      throw new AssertionError();
   }

   @Inject(
      method = {"method_3228"},
      at = {@At("HEAD")}
   )
   private void beforeFirstPersonItem(
      class_742 player,
      float tickDelta,
      float pitch,
      class_1268 hand,
      float swingProgress,
      class_1799 stack,
      float equipProgress,
      class_4587 matrices,
      class_11659 queue,
      int light,
      CallbackInfo callback
   ) {
      this.wexside$currentHand = hand;
      this.wexside$currentEquipProgress = equipProgress;
      EventBus events = WexSideClient.getEventBus();
      if (events != null) {
         FirstPersonItemTransformEvent event = new FirstPersonItemTransformEvent(matrices, stack, hand);
         events.post(event);
         float scale = event.getScale();
         if (scale != 1.0F) {
            matrices.method_22905(scale, scale, scale);
         }
      }
   }

   @Redirect(
      method = {"method_65816"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_759;method_3217(Lnet/minecraft/class_4587;Lnet/minecraft/class_1306;F)V"
)
   )
   private void replaceSwingTransform(class_759 renderer, class_4587 matrices, class_1306 arm, float swingProgress) {
      EventBus events = WexSideClient.getEventBus();
      if (events != null && this.wexside$currentHand != null) {
         FirstPersonSwingTransformEvent event = new FirstPersonSwingTransformEvent(
            matrices, this.wexside$currentHand, swingProgress, this.wexside$currentEquipProgress
         );
         events.post(event);
         if (event.isCancelled()) {
            return;
         }
      }

      this.method_3217(matrices, arm, swingProgress);
   }

   @Inject(
      method = {"method_22976"},
      at = {@At("HEAD")}
   )
   private void beforeHandRender(float tickDelta, class_4587 matrices, class_11659 queue, class_746 player, int light, CallbackInfo callback) {
      EventBus events = WexSideClient.getEventBus();
      if (events != null) {
         events.post(new HandRenderEvent(HandRenderPhase.BEFORE, matrices, tickDelta));
      }
   }

   @Inject(
      method = {"method_22976"},
      at = {@At("RETURN")}
   )
   private void afterHandRender(float tickDelta, class_4587 matrices, class_11659 queue, class_746 player, int light, CallbackInfo callback) {
      EventBus events = WexSideClient.getEventBus();
      if (events != null) {
         events.post(new HandRenderEvent(HandRenderPhase.AFTER, matrices, tickDelta));
      }
   }
}
