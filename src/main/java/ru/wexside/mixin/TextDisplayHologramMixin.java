package ru.wexside.mixin;

import net.minecraft.class_10071;
import net.minecraft.class_11659;
import net.minecraft.class_4587;
import net.minecraft.class_8138.class_8141;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.render.HologramImpostorRenderer;

@Mixin({class_8141.class})
public abstract class TextDisplayHologramMixin {
   @Inject(
      method = {"method_49056"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void wexside$captureTextDisplay(class_10071 state, class_4587 matrices, class_11659 queue, int light, float tickProgress, CallbackInfo callback) {
      if (HologramImpostorRenderer.captureTextDisplay(state, matrices, tickProgress)) {
         callback.cancel();
      }
   }
}
