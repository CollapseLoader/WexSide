package ru.wexside.mixin;

import net.minecraft.class_765;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.wexside.WexSideClient;
import ru.wexside.event.BrightnessEvent;
import ru.wexside.event.EventBus;

@Mixin({class_765.class})
public abstract class LightmapEventMixin {
   @Inject(
      method = {"method_62226"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private static void applyBrightnessOverride(float ambientLight, int lightLevel, CallbackInfoReturnable<Float> callback) {
      EventBus events = WexSideClient.getEventBus();
      if (events != null) {
         BrightnessEvent event = new BrightnessEvent(callback.getReturnValueF());
         events.post(event);
         callback.setReturnValue(event.getBrightness());
      }
   }
}
