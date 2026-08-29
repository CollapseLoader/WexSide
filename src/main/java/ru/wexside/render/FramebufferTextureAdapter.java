package ru.wexside.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import net.minecraft.class_1044;
import net.minecraft.class_276;

public final class FramebufferTextureAdapter extends class_1044 implements AutoCloseable {
   @Override
   public void close() {
      this.field_56974 = null;
      this.field_60597 = null;
      this.field_63613 = null;
   }

   public void bind(class_276 framebuffer) {
      this.field_56974 = framebuffer.method_30277();
      this.field_60597 = framebuffer.method_71639();
      this.field_63613 = RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR);
   }
}
