package ru.wexside.mixin;

import net.minecraft.class_11909;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_465;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.WexSideClient;
import ru.wexside.event.EventBus;
import ru.wexside.event.ItemHoverEvent;
import ru.wexside.event.TooltipRenderEvent;
import ru.wexside.module.hud.AnimateModule;

@Mixin({class_465.class})
public abstract class HandledScreenEventMixin {
   @Shadow
   protected class_1735 field_2787;
   @Unique
   private boolean wexside$animationMatrixPushed;

   @Inject(
      method = {"method_25394"},
      at = {@At("HEAD")}
   )
   private void wexside$beginScreenAnimation(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo callback) {
      if (AnimateModule.compute2((class_437)(Object)this)) {
         context.method_51448().pushMatrix();
         AnimateModule.handle(context.method_51448(), (float)context.method_51421(), (float)context.method_51443());
         this.wexside$animationMatrixPushed = true;
      }
   }

   @Inject(
      method = {"method_25394"},
      at = {@At("RETURN")}
   )
   private void wexside$endScreenAnimation(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo callback) {
      if (this.wexside$animationMatrixPushed) {
         this.wexside$animationMatrixPushed = false;
         context.method_51448().popMatrix();
      }
   }

   @ModifyVariable(
      method = {"method_25394"},
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private int wexside$transformRenderMouseX(int mouseX) {
      return AnimateModule.compute6(mouseX);
   }

   @ModifyVariable(
      method = {"method_25394"},
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 1
   )
   private int wexside$transformRenderMouseY(int mouseY) {
      return AnimateModule.compute7(mouseY);
   }

   @ModifyVariable(
      method = {"method_25402", "method_25403", "method_25406"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private class_11909 wexside$transformAnimatedClick(class_11909 click) {
      int transformedX = AnimateModule.compute6((int)Math.round(click.comp_4798()));
      int transformedY = AnimateModule.compute7((int)Math.round(click.comp_4799()));
      return transformedX == (int)Math.round(click.comp_4798()) && transformedY == (int)Math.round(click.comp_4799())
         ? click
         : new class_11909((double)transformedX, (double)transformedY, click.comp_4800());
   }

   @Inject(
      method = {"method_2380"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void beforeItemTooltip(class_332 context, int mouseX, int mouseY, CallbackInfo callback) {
      EventBus events = WexSideClient.getEventBus();
      if (events != null && this.field_2787 != null) {
         class_1799 stack = this.field_2787.method_7677();
         if (!stack.method_7960()) {
            ItemHoverEvent event = new ItemHoverEvent(stack);
            events.post(event);
            if (event.isCancelled()) {
               callback.cancel();
            }
         }
      }
   }

   @Inject(
      method = {"method_25394"},
      at = {@At("TAIL")}
   )
   private void afterScreenRender(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo callback) {
      EventBus events = WexSideClient.getEventBus();
      if (events != null) {
         events.post(new TooltipRenderEvent(context, mouseX, mouseY));
      }
   }
}
