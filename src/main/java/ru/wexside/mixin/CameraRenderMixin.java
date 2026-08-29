package ru.wexside.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_4184;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wexside.module.movement.FreeCameraModule;
import ru.wexside.module.render.CameraModule;
import ru.wexside.module.render.FreeLookModule;

@Mixin({class_4184.class})
public abstract class CameraRenderMixin {
   @Shadow
   protected abstract void method_19325(float var1, float var2);

   @Shadow
   protected abstract void method_19322(class_243 var1);

   @Inject(
      method = {"method_19321"},
      at = {@At("TAIL")}
   )
   private void wexside$applyFreeLookRotation(
      class_1937 world, class_1297 focusedEntity, boolean thirdPerson, boolean inverseView, float tickProgress, CallbackInfo callback
   ) {
      if (FreeCameraModule.isEnabled()) {
         this.method_19322(FreeCameraModule.compute(tickProgress));
         this.method_19325(FreeCameraModule.getFloatType2(), FreeCameraModule.getFloatType());
      } else if (FreeLookModule.isEnabled()) {
         this.method_19325(FreeLookModule.getFloatType2(), FreeLookModule.getFloatType());
      }
   }

   @Inject(
      method = {"method_19318"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void wexside$disableCameraClipping(float desiredDistance, CallbackInfoReturnable<Float> callback) {
      if (CameraModule.isEnabled4()) {
         callback.setReturnValue(desiredDistance);
      }
   }

   @Inject(
      method = {"method_19318"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void wexside$smoothCameraDistance(float desiredDistance, CallbackInfoReturnable<Float> callback) {
      if (CameraModule.isEnabled3() && !CameraModule.isEnabled4()) {
         callback.setReturnValue(CameraModule.compute(callback.getReturnValue()));
      }
   }

   @Inject(
      method = {"method_19337"},
      at = {@At("TAIL")}
   )
   private void wexside$resetCameraSmoothing(CallbackInfo callback) {
      CameraModule.tick();
   }
}
