package ru.wexside.mixin;

import net.minecraft.class_1937;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wexside.module.render.AmbientModule;

@Mixin({class_1937.class})
public abstract class WorldAmbientMixin {
   @Inject(
      method = {"method_8532"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void wexside$useConfiguredTime(CallbackInfoReturnable<Long> callback) {
      if (AmbientModule.isEnabled2()) {
         callback.setReturnValue(AmbientModule.getLongType());
      }
   }
}
