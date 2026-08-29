package ru.wexside.misc;

import com.mojang.blaze3d.textures.GpuTexture;
import java.util.Map;

public interface GuiRenderTargetAccessor {
   GpuTexture getItemAtlasDepthTexture();

   Map<Object, ?> getRenderedItems();

   void setItemAtlasX(int var1);

   GpuTexture getItemAtlasTexture();

   void setItemAtlasY(int var1);
}
