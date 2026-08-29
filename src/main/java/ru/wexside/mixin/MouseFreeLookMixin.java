package ru.wexside.mixin;

import net.minecraft.class_312;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.wexside.module.movement.FreeCameraModule;
import ru.wexside.module.render.FreeLookModule;

@Mixin({class_312.class})
public abstract class MouseFreeLookMixin {
   @Redirect(
      method = {"method_1606"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_746;method_5872(DD)V"
)
   )
   private void wexside$redirectLookDirection(class_746 player, double cursorDeltaX, double cursorDeltaY) {
      if (FreeCameraModule.isEnabled()) {
         FreeCameraModule.handle(cursorDeltaX, cursorDeltaY);
      } else if (FreeLookModule.isEnabled()) {
         FreeLookModule.handle(cursorDeltaX, cursorDeltaY);
      } else {
         player.method_5872(cursorDeltaX, cursorDeltaY);
      }
   }
}
