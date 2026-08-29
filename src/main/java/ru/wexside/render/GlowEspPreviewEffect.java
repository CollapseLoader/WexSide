package ru.wexside.render;

import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.class_10868;
import net.minecraft.class_6367;
import ru.wexside.misc.CaptureFramebuffer;
import ru.wexside.misc.GlowEspSettings;

final class GlowEspPreviewEffect {
   private final GlowOutlineCompositor compositor = new GlowOutlineCompositor();
   private final long startedAtMillis = System.currentTimeMillis();
   private class_6367 gradientFramebuffer;
   private class_6367 horizontalOutlineFramebuffer;
   private class_6367 outlineFramebuffer;
   private int width;
   private int height;

   int render(CaptureFramebuffer sourceFramebuffer, int sourceWidth, int sourceHeight, GlowEspSettings settings) {
      if (sourceFramebuffer != null && settings != null && sourceWidth > 0 && sourceHeight > 0) {
         GpuTextureView sourceView = sourceFramebuffer.method_71639();
         if (sourceView == null) {
            return 0;
         } else {
            int effectWidth = Math.max(1, sourceWidth / 2);
            int effectHeight = Math.max(1, sourceHeight / 2);
            this.ensureFramebuffers(effectWidth, effectHeight);
            clearFramebuffer(this.gradientFramebuffer);
            clearFramebuffer(this.horizontalOutlineFramebuffer);
            clearFramebuffer(this.outlineFramebuffer);
            GpuTextureView gradientView = this.gradientFramebuffer.method_71639();
            GpuTextureView horizontalOutlineView = this.horizontalOutlineFramebuffer.method_71639();
            GpuTextureView outlineView = this.outlineFramebuffer.method_71639();
            if (gradientView != null && horizontalOutlineView != null && outlineView != null) {
               float elapsed = (float)(System.currentTimeMillis() - this.startedAtMillis) / 700.0F;
               this.compositor
                  .applyGradient(
                     gradientView,
                     sourceView,
                     effectWidth,
                     effectHeight,
                     settings.getColor(),
                     false,
                     0.0F,
                     0.0F,
                     (float)effectWidth,
                     (float)effectHeight,
                     elapsed
                  );
               float outlineSize = Math.max(1.0F, (float)Math.round(settings.getRadius() * 0.5F));
               this.compositor.applyOutline(horizontalOutlineView, gradientView, sourceView, effectWidth, effectHeight, outlineSize, 1.0F, 0.0F, false);
               this.compositor.applyOutline(outlineView, horizontalOutlineView, gradientView, effectWidth, effectHeight, outlineSize, 0.0F, 1.0F, true);
               return getColorTextureId(this.outlineFramebuffer);
            } else {
               return 0;
            }
         }
      } else {
         return 0;
      }
   }

   int getWidth() {
      return this.width;
   }

   int getHeight() {
      return this.height;
   }

   void releaseFramebuffers() {
      if (this.gradientFramebuffer != null) {
         this.gradientFramebuffer.method_1238();
         this.gradientFramebuffer = null;
      }

      if (this.horizontalOutlineFramebuffer != null) {
         this.horizontalOutlineFramebuffer.method_1238();
         this.horizontalOutlineFramebuffer = null;
      }

      if (this.outlineFramebuffer != null) {
         this.outlineFramebuffer.method_1238();
         this.outlineFramebuffer = null;
      }

      this.width = 0;
      this.height = 0;
   }

   private void ensureFramebuffers(int width, int height) {
      if (this.gradientFramebuffer == null || this.width != width || this.height != height) {
         this.releaseFramebuffers();
         this.gradientFramebuffer = new class_6367("wexside_glow_preview_gradient", width, height, false);
         this.horizontalOutlineFramebuffer = new class_6367("wexside_glow_preview_outline_temp", width, height, false);
         this.outlineFramebuffer = new class_6367("wexside_glow_preview_outline", width, height, false);
         this.width = width;
         this.height = height;
      }
   }

   private static int getColorTextureId(class_6367 framebuffer) {
      GpuTexture texture = framebuffer.method_30277();
      return texture instanceof class_10868 glTexture ? glTexture.method_68427() : 0;
   }

   private static void clearFramebuffer(class_6367 framebuffer) {
      GpuTexture texture = framebuffer.method_30277();
      if (texture != null) {
         CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
         commandEncoder.clearColorTexture(texture, 0);
      }
   }
}
