package ru.wexside.mixin;

import net.minecraft.class_2394;
import net.minecraft.class_2396;
import net.minecraft.class_2398;
import net.minecraft.class_702;
import net.minecraft.class_703;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wexside.module.render.HitParticlesModule;

@Mixin({class_702.class})
public abstract class HitParticleFilterMixin {
   @Inject(
      method = {"method_3056"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void wexside$removeVanillaHitParticles(
      class_2394 effect, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<class_703> callback
   ) {
      if (effect != null && HitParticlesModule.isEnabled() && isVanillaHitParticle(effect.method_10295())) {
         callback.setReturnValue(null);
      }
   }

   private static boolean isVanillaHitParticle(class_2396<?> type) {
      return type == class_2398.field_11205 || type == class_2398.field_11208 || type == class_2398.field_11209;
   }
}
