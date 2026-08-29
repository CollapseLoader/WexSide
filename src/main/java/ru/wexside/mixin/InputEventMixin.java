package ru.wexside.mixin;

import net.minecraft.class_11908;
import net.minecraft.class_11910;
import net.minecraft.class_309;
import net.minecraft.class_312;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.WexSideClient;
import ru.wexside.event.EventBus;
import ru.wexside.event.KeyPressedEvent;
import ru.wexside.event.KeyReleasedEvent;
import ru.wexside.event.MousePressedEvent;
import ru.wexside.event.MouseReleasedEvent;

public final class InputEventMixin {
   private InputEventMixin() {
   }

   @Mixin({class_309.class})
   public static class KeyboardHook {
      @Inject(
         method = {"method_1466"},
         at = {@At("TAIL")}
      )
      private void wexside$onKey(long window, int action, class_11908 input, CallbackInfo callback) {
         EventBus eventBus = WexSideClient.getEventBus();
         if (eventBus != null) {
            if (action == 1) {
               eventBus.post(new KeyPressedEvent(input.comp_4795(), input.comp_4796()));
            } else if (action == 0) {
               eventBus.post(new KeyReleasedEvent(input.comp_4795(), input.comp_4796()));
            }
         }
      }
   }

   @Mixin({class_312.class})
   public static class MouseHook {
      @Inject(
         method = {"method_1601"},
         at = {@At("TAIL")}
      )
      private void wexside$onMouseButton(long window, class_11910 input, int action, CallbackInfo callback) {
         EventBus eventBus = WexSideClient.getEventBus();
         if (eventBus != null) {
            if (action == 1) {
               eventBus.post(new MousePressedEvent(input.comp_4801()));
            } else if (action == 0) {
               eventBus.post(new MouseReleasedEvent(input.comp_4801()));
            }
         }
      }
   }
}
