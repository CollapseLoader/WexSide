package ru.wexside.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;

final class RenderTargetOverrideScope implements AutoCloseable {
   private final GpuTextureView previousColor = RenderSystem.outputColorTextureOverride;
   private final GpuTextureView previousDepth = RenderSystem.outputDepthTextureOverride;

   private RenderTargetOverrideScope(GpuTextureView color, GpuTextureView depth) {
      RenderSystem.outputColorTextureOverride = color;
      RenderSystem.outputDepthTextureOverride = depth;
   }

   static RenderTargetOverrideScope use(GpuTextureView color, GpuTextureView depth) {
      return new RenderTargetOverrideScope(color, depth);
   }

   @Override
   public void close() {
      RenderSystem.outputColorTextureOverride = this.previousColor;
      RenderSystem.outputDepthTextureOverride = this.previousDepth;
   }
}
