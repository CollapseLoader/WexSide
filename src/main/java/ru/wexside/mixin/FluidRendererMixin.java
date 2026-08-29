package ru.wexside.mixin;

import net.minecraft.class_1920;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3486;
import net.minecraft.class_3610;
import net.minecraft.class_4588;
import net.minecraft.class_775;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.module.render.NoRenderModule;

@Mixin({class_775.class})
public abstract class FluidRendererMixin {
   @Inject(
      method = {"method_3347"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void wexside$hideWorldLava(
      class_1920 world, class_2338 position, class_4588 vertices, class_2680 blockState, class_3610 fluidState, CallbackInfo callback
   ) {
      if (NoRenderModule.isWorldLavaDisabled() && fluidState.method_15767(class_3486.field_15518)) {
         callback.cancel();
      }
   }
}
