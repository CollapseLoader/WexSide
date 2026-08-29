package ru.wexside.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wexside.module.movement.AirStuckModule;
import ru.wexside.module.movement.AutoSprintModule;
import ru.wexside.module.movement.SpeedModule;

@Mixin({class_1657.class})
public abstract class PlayerEntityFeatureMixin {
   private class_243 wexside$velocityBeforeAttack;
   private boolean wexside$sprintingBeforeAttack;
   private boolean wexside$restoreSprintAfterAttack;

   @Inject(
      method = {"method_6091"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void wexside$freezeAirStuckMovement(class_243 movement, CallbackInfo callback) {
      if ((Object)this instanceof class_746 player && AirStuckModule.compute2(player)) {
         player.method_18799(class_243.field_1353);
         callback.cancel();
      }
   }

   @Inject(
      method = {"method_6029"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void wexside$applyCollisionSpeed(CallbackInfoReturnable<Float> callback) {
      if ((Object)this == class_310.method_1551().field_1724 && SpeedModule.value > 0.0F) {
         callback.setReturnValue(SpeedModule.value);
      }
   }

   @Inject(
      method = {"method_7324"},
      at = {@At("HEAD")}
   )
   private void wexside$captureSprintState(class_1297 target, CallbackInfo callback) {
      class_1657 player = (class_1657)(Object)this;
      this.wexside$restoreSprintAfterAttack = player == class_310.method_1551().field_1724 && AutoSprintModule.isEnabled2() && !player.method_7325();
      if (this.wexside$restoreSprintAfterAttack) {
         this.wexside$velocityBeforeAttack = player.method_18798();
         this.wexside$sprintingBeforeAttack = player.method_5624();
      }
   }

   @Inject(
      method = {"method_7324"},
      at = {@At("TAIL")}
   )
   private void wexside$restoreSprintState(class_1297 target, CallbackInfo callback) {
      if (this.wexside$restoreSprintAfterAttack) {
         class_1657 player = (class_1657)(Object)this;
         player.method_18799(this.wexside$velocityBeforeAttack);
         player.method_5728(this.wexside$sprintingBeforeAttack);
         this.wexside$restoreSprintAfterAttack = false;
      }
   }
}
