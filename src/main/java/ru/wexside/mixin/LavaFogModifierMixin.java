package ru.wexside.mixin;

import net.minecraft.class_11401;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_7285;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.module.render.NoRenderModule;

@Mixin({class_11401.class})
public abstract class LavaFogModifierMixin {
   @Inject(
      method = {"method_42591"},
      at = {@At("TAIL")}
   )
   private void wexside$removeLavaFog(class_7285 fog, class_4184 camera, class_638 world, float viewDistance, class_9779 tickCounter, CallbackInfo callback) {
      if (NoRenderModule.isUnderLavaDisabled()) {
         fog.field_60582 = 1.0E7F;
         fog.field_60583 = 1.0E7F;
         fog.field_60584 = 1.0E8F;
         fog.field_60585 = 1.0E8F;
         fog.field_60099 = 1.0E8F;
         fog.field_60100 = 1.0E8F;
      }
   }
}
