package ru.wexside.mixin;

import net.minecraft.class_10017;
import net.minecraft.class_11659;
import net.minecraft.class_12075;
import net.minecraft.class_1297;
import net.minecraft.class_4587;
import net.minecraft.class_897;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.render.HologramImpostorRenderer;
import ru.wexside.util.NameplateRenderer;

@Mixin({class_897.class})
public abstract class EntityRenderStateMixin<T extends class_1297, S extends class_10017> {
   @Inject(
      method = {"method_62354"},
      at = {@At("TAIL")}
   )
   private void wexside$hideVanillaNameplate(T entity, S state, float tickProgress, CallbackInfo callback) {
      if (NameplateRenderer.process16(entity)) {
         state.field_53337 = null;
         state.field_53338 = null;
      }
   }

   @Inject(
      method = {"method_3926"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void wexside$captureHologramLabel(S state, class_4587 matrices, class_11659 queue, class_12075 camera, CallbackInfo callback) {
      if (HologramImpostorRenderer.captureEntityLabel(state, matrices, camera)) {
         callback.cancel();
      }
   }
}
