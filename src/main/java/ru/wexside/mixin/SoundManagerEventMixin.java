package ru.wexside.mixin;

import net.minecraft.class_1113;
import net.minecraft.class_1144;
import net.minecraft.class_2960;
import net.minecraft.class_1140.class_11518;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wexside.module.misc.CustomSoundsModule;
import ru.wexside.module.misc.SoundRemoverModule;

@Mixin({class_1144.class})
public abstract class SoundManagerEventMixin {
   @Inject(
      method = {"method_4873"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void wexside$filterImmediateSound(class_1113 sound, CallbackInfoReturnable<class_11518> callback) {
      if (sound != null) {
         class_2960 soundId = sound.method_4775();
         boolean muted = SoundRemoverModule.compute4(soundId);
         SoundRemoverModule.handle(soundId, muted);
         if (muted) {
            callback.setReturnValue(class_11518.field_60956);
         } else {
            if (CustomSoundsModule.compute8(sound, 0L)) {
               callback.setReturnValue(class_11518.field_60956);
            }
         }
      }
   }

   @Inject(
      method = {"method_4872"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void wexside$filterDelayedSound(class_1113 sound, int delay, CallbackInfo callback) {
      if (sound != null) {
         class_2960 soundId = sound.method_4775();
         boolean muted = SoundRemoverModule.compute4(soundId);
         SoundRemoverModule.handle(soundId, muted);
         if (muted) {
            callback.cancel();
         } else {
            if (CustomSoundsModule.compute8(sound, (long)delay * 50L)) {
               callback.cancel();
            }
         }
      }
   }
}
