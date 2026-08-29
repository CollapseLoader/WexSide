package ru.wexside.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wexside.module.player.NoPushModule;

public final class NoPushMixin {
   private NoPushMixin() {
   }

   @Mixin({class_746.class})
   public abstract static class ClientPlayerHook {
      @Inject(
         method = {"method_30673"},
         at = {@At("HEAD")},
         cancellable = true
      )
      private void wexside$ignoreBlockPush(double x, double z, CallbackInfo callback) {
         if (NoPushModule.compute("Blocks")) {
            callback.cancel();
         }
      }
   }

   @Mixin({class_1297.class})
   public abstract static class EntityHook {
      @Inject(
         method = {"method_5697"},
         at = {@At("HEAD")},
         cancellable = true
      )
      private void wexside$ignoreEntityPush(class_1297 other, CallbackInfo callback) {
         if ((Object)this == class_310.method_1551().field_1724 && NoPushModule.compute("Players")) {
            callback.cancel();
         }
      }
   }

   @Mixin({class_1657.class})
   public abstract static class PlayerHook {
      @Inject(
         method = {"method_5675"},
         at = {@At("HEAD")},
         cancellable = true
      )
      private void wexside$ignoreFluidPush(CallbackInfoReturnable<Boolean> callback) {
         if ((Object)this == class_310.method_1551().field_1724 && NoPushModule.compute("Water")) {
            callback.setReturnValue(false);
         }
      }
   }
}
