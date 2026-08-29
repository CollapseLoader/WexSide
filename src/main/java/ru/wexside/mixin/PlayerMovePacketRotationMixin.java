package ru.wexside.mixin;

import net.minecraft.class_2828;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.WexSideClient;
import ru.wexside.util.RotationController;

@Mixin({class_2828.class})
public abstract class PlayerMovePacketRotationMixin {
   @Shadow
   @Final
   protected boolean field_12888;
   @Shadow
   @Final
   @Mutable
   protected float field_12887;
   @Shadow
   @Final
   @Mutable
   protected float field_12885;

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void wexside$applySilentRotation(
      double x,
      double y,
      double z,
      float yaw,
      float pitch,
      boolean onGround,
      boolean horizontalCollision,
      boolean changePosition,
      boolean changeLook,
      CallbackInfo callback
   ) {
      if (this.field_12888) {
         RotationController rotations = WexSideClient.getRotationController();
         if (rotations != null) {
            Float requestedYaw = rotations.getFloatType2();
            if (requestedYaw != null) {
               this.field_12887 = requestedYaw;
               this.field_12885 = rotations.getFloatType();
               rotations.onTick(this.field_12885);
            }
         }
      }
   }
}
