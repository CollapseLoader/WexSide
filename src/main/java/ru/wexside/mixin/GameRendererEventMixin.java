package ru.wexside.mixin;

import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_9779;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wexside.WexSideClient;
import ru.wexside.event.AspectRatioEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.OverlayRenderEvent;
import ru.wexside.event.OverlayType;
import ru.wexside.misc.ChamsRenderer;
import ru.wexside.module.render.NoRenderModule;
import ru.wexside.render.ClientEventBridge;
import ru.wexside.render.HologramImpostorRenderer;
import ru.wexside.ui.WexsideScreen;

@Mixin({class_757.class})
public abstract class GameRendererEventMixin {
   @Shadow
   @Final
   private class_310 field_4015;
   @Shadow
   private float field_4019;
   @Shadow
   private float field_3999;

   @Shadow
   public abstract float method_32796();

   @Inject(
      method = {"method_3192"},
      at = {@At("TAIL")}
   )
   private void renderWexsidePanel(class_9779 tickCounter, boolean tick, CallbackInfo callback) {
      class_437 var5 = this.field_4015.field_1755;
      if (var5 instanceof WexsideScreen screen) {
         screen.renderPanel(tickCounter.method_60636());
      }
   }

   @Inject(
      method = {"method_3188"},
      at = {@At("TAIL")}
   )
   private void wexside$finishDeferredWorldEffects(class_9779 tickCounter, CallbackInfo callback) {
      HologramImpostorRenderer.flush();
      ChamsRenderer.presentPendingChams();
   }

   @Inject(
      method = {"method_3198"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void allowHurtCameraCancellation(class_4587 matrices, float tickDelta, CallbackInfo callback) {
      EventBus events = WexSideClient.getEventBus();
      if (events != null) {
         OverlayRenderEvent event = new OverlayRenderEvent(OverlayType.CAMERA_HURT);
         events.post(event);
         if (event.isCancelled()) {
            callback.cancel();
         }
      }
   }

   @Inject(
      method = {"method_3186"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void disableViewBobbing(class_4587 matrices, float tickDelta, CallbackInfo callback) {
      if (NoRenderModule.isViewBobbingDisabled()) {
         callback.cancel();
      }
   }

   @Inject(
      method = {"method_3199"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void disableDynamicFov(CallbackInfo callback) {
      if (NoRenderModule.isEnabled()) {
         this.field_3999 = 1.0F;
         this.field_4019 = 1.0F;
         callback.cancel();
      }
   }

   @Inject(
      method = {"method_22973"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void useConfiguredAspectRatio(float fov, CallbackInfoReturnable<Matrix4f> callback) {
      EventBus events = WexSideClient.getEventBus();
      if (events != null) {
         float vanillaRatio = (float)this.field_4015.method_22683().method_4489() / (float)Math.max(1, this.field_4015.method_22683().method_4506());
         AspectRatioEvent event = new AspectRatioEvent(vanillaRatio);
         events.post(event);
         if (Float.compare(event.getAspectRatio(), vanillaRatio) != 0) {
            callback.setReturnValue(new Matrix4f().perspective(fov * ((float) (Math.PI / 180.0)), event.getAspectRatio(), 0.05F, this.method_32796()));
         }
      }
   }
}
