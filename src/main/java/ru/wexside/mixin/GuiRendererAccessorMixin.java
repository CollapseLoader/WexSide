package ru.wexside.mixin;

import com.mojang.blaze3d.textures.GpuTexture;
import java.util.Map;
import net.minecraft.class_11228;
import net.minecraft.class_276;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.wexside.misc.ActiveFramebufferContext;
import ru.wexside.misc.GuiRenderTargetAccessor;

@Mixin({class_11228.class})
public abstract class GuiRendererAccessorMixin implements GuiRenderTargetAccessor {
   @Shadow
   private GpuTexture field_59919;
   @Shadow
   private GpuTexture field_59920;
   @Shadow
   private Map<Object, ?> field_59913;
   @Shadow
   private int field_59922;
   @Shadow
   private int field_59923;

   @Override
   public GpuTexture getItemAtlasDepthTexture() {
      return this.field_59920;
   }

   @Override
   public GpuTexture getItemAtlasTexture() {
      return this.field_59919;
   }

   @Override
   public Map<Object, ?> getRenderedItems() {
      return this.field_59913;
   }

   @Override
   public void setItemAtlasX(int x) {
      this.field_59922 = x;
   }

   @Override
   public void setItemAtlasY(int y) {
      this.field_59923 = y;
   }

   @Redirect(
      method = {"method_71291"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_310;method_1522()Lnet/minecraft/class_276;"
)
   )
   private class_276 wexside$useCaptureFramebuffer(class_310 client) {
      class_276 capture = ActiveFramebufferContext.getFramebuffer();
      return capture == null ? client.method_1522() : capture;
   }
}
