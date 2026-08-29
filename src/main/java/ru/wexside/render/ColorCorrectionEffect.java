package ru.wexside.render;

import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import net.minecraft.class_276;
import net.minecraft.class_310;

public final class ColorCorrectionEffect implements AutoCloseable {
   private static final float NEUTRAL_EPSILON = 0.001F;
   private static final int SCENE_TEXTURE_USAGE = 13;
   private final class_310 client = class_310.method_1551();
   private final ColorCorrectionShader shader = new ColorCorrectionShader();
   private GpuTexture sceneTexture;
   private GpuTextureView sceneView;
   private int sceneWidth;
   private int sceneHeight;

   public void apply(float contrast, float saturation, float brightness) {
      if (!isNeutral(contrast) || !isNeutral(saturation) || !isNeutral(brightness)) {
         class_276 framebuffer = this.client.method_1522();
         if (framebuffer != null && framebuffer.method_30277() != null) {
            int width = framebuffer.field_1482;
            int height = framebuffer.field_1481;
            this.ensureSceneTarget(width, height);
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            commandEncoder.copyTextureToTexture(framebuffer.method_30277(), this.sceneTexture, 0, 0, 0, 0, 0, width, height);
            this.shader.apply(framebuffer.method_71639(), this.sceneView, width, height, contrast, saturation, brightness);
         }
      }
   }

   private static boolean isNeutral(float value) {
      return Math.abs(value - 1.0F) < 0.001F;
   }

   private void ensureSceneTarget(int width, int height) {
      if (this.sceneTexture == null || this.sceneWidth != width || this.sceneHeight != height) {
         this.releaseSceneTarget();
         this.sceneTexture = RenderSystem.getDevice().createTexture(() -> "wexside:color_correction_scene", 13, TextureFormat.RGBA8, width, height, 1, 1);
         this.sceneView = RenderSystem.getDevice().createTextureView(this.sceneTexture);
         this.sceneWidth = width;
         this.sceneHeight = height;
      }
   }

   private void releaseSceneTarget() {
      if (this.sceneView != null) {
         this.sceneView.close();
         this.sceneView = null;
      }

      if (this.sceneTexture != null) {
         this.sceneTexture.close();
         this.sceneTexture = null;
      }

      this.sceneWidth = 0;
      this.sceneHeight = 0;
   }

   @Override
   public void close() {
      this.releaseSceneTarget();
      this.shader.close();
   }
}
