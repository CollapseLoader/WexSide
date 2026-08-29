package ru.wexside.mixin;

import net.minecraft.class_12076;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_9975;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.module.render.AmbientModule;

@Mixin({class_9975.class})
public abstract class SkyRenderingMixin {
   @Inject(
      method = {"method_74926"},
      at = {@At("TAIL")}
   )
   private void wexside$applyConfiguredSkyColor(class_638 world, float tickProgress, class_4184 camera, class_12076 state, CallbackInfo callback) {
      if (AmbientModule.isEnabled5()) {
         state.field_63097 = AmbientModule.getIntType();
      }
   }
}
