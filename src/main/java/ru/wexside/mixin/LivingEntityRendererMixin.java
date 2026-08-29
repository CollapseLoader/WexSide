package ru.wexside.mixin;

import net.minecraft.class_10042;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_922;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wexside.module.render.SeeInvisiblesModule;

@Mixin({class_922.class})
public abstract class LivingEntityRendererMixin {
   @Shadow
   public abstract class_2960 method_3885(class_10042 var1);

   @Inject(
      method = {"method_24302"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void wexside$useTranslucentLayer(
      class_10042 state, boolean showBody, boolean translucent, boolean showOutline, CallbackInfoReturnable<class_1921> callback
   ) {
      if (SeeInvisiblesModule.isEnabled() && state.field_53333) {
         callback.setReturnValue(class_12249.method_75998(this.method_3885(state)));
      }
   }

   @Inject(
      method = {"method_62484"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void wexside$applyInvisibleAlpha(class_10042 state, CallbackInfoReturnable<Integer> callback) {
      if (SeeInvisiblesModule.isEnabled() && state.field_53333) {
         callback.setReturnValue(SeeInvisiblesModule.getIntType());
      }
   }
}
