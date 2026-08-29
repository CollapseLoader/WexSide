package ru.wexside.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.misc.PlayerShieldStateAccessor;
import ru.wexside.module.render.HitParticlesModule;

@Mixin({class_746.class})
public abstract class ClientPlayerStateMixin implements PlayerShieldStateAccessor {
   private boolean wexside$shieldDesynchronized;

   @Override
   public boolean isShieldUseForced() {
      return this.wexside$shieldDesynchronized;
   }

   @Override
   public void setShieldUseForced(boolean state) {
      this.wexside$shieldDesynchronized = state;
   }

   @Inject(
      method = {"method_7277"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void wexside$replaceCriticalParticles(class_1297 target, CallbackInfo callback) {
      if (HitParticlesModule.isEnabled()) {
         callback.cancel();
      }
   }

   @Inject(
      method = {"method_7304"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void wexside$replaceEnchantedParticles(class_1297 target, CallbackInfo callback) {
      if (HitParticlesModule.isEnabled()) {
         callback.cancel();
      }
   }
}
