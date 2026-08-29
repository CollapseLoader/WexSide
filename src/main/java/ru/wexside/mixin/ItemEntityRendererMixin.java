package ru.wexside.mixin;

import net.minecraft.class_10039;
import net.minecraft.class_11659;
import net.minecraft.class_12075;
import net.minecraft.class_4587;
import net.minecraft.class_7833;
import net.minecraft.class_916;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.module.render.ItemPhysicModule;

@Mixin({class_916.class})
public abstract class ItemEntityRendererMixin {
   @Inject(
      method = {"method_3996"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_916;method_72986(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_10428;Lnet/minecraft/class_5819;Lnet/minecraft/class_238;)V",
   shift = Shift.BEFORE
)}
   )
   private void wexside$layItemOnGround(class_10039 state, class_4587 matrices, class_11659 queue, class_12075 cameraState, CallbackInfo callback) {
      if (ItemPhysicModule.isEnabled()) {
         matrices.method_22907(class_7833.field_40714.rotationDegrees(90.0F));
      }
   }
}
