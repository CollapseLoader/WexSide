package ru.wexside.util;

import java.util.function.Consumer;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import ru.wexside.misc.PreparedLayer;

public final class ClippedContentRenderer {
   private final boolean useSharedLayer;
   private final float startFadeSize;
   private final boolean horizontal;
   private final float endFadeSize;
   private final float edgePadding;

   public ClippedContentRenderer(float edgePadding, float startFadeSize, float endFadeSize, boolean useSharedLayer) {
      this(edgePadding, startFadeSize, endFadeSize, useSharedLayer, false);
   }

   public ClippedContentRenderer(float edgePadding, float startFadeSize, float endFadeSize, boolean useSharedLayer, boolean horizontal) {
      this.edgePadding = Math.max(0.0F, edgePadding);
      this.startFadeSize = Math.max(0.0F, startFadeSize);
      this.endFadeSize = Math.max(0.0F, endFadeSize);
      this.useSharedLayer = useSharedLayer;
      this.horizontal = horizontal;
   }

   public void render(
      GuiDrawApi renderer,
      Matrix4f matrix,
      float x,
      float y,
      float width,
      float height,
      float scrollOffset,
      float minimumOffset,
      Consumer<Matrix4f> drawContent
   ) {
      if (!(width <= 0.5F) && !(height <= 0.5F)) {
         if (minimumOffset >= -0.01F) {
            drawContent.accept(matrix);
         } else {
            float layerX = this.horizontal ? x - this.edgePadding : x;
            float layerY = this.horizontal ? y : y - this.edgePadding;
            float layerWidth = this.horizontal ? width + this.edgePadding : width;
            float layerHeight = this.horizontal ? height : height + this.edgePadding;
            PreparedLayer layer = this.useSharedLayer
               ? renderer.prepareLayer(matrix, layerX, layerY, layerWidth, layerHeight, 0.0F)
               : renderer.prepareDedicatedLayer(matrix, layerX, layerY, layerWidth, layerHeight, 0.0F);
            Vector4f transformedBottomLeft = matrix.transform(new Vector4f(layerX, layerY + layerHeight, 0.0F, 1.0F));
            float textureBottomPadding = (float)layer.getTexture().getHeight() * (1.0F - layer.maxV());
            renderer.beginLayerFrame(
               layer.getTexture(),
               renderer.getLayerOffsetX() + transformedBottomLeft.x,
               renderer.getLayerOffsetY() + ((float)renderer.getFramebufferHeight() - transformedBottomLeft.y) - textureBottomPadding
            );
            Matrix4f contentMatrix = new Matrix4f(layer.getContentMatrix()).translate(layer.contentX() - layerX, layer.contentY() - layerY, 0.0F);
            drawContent.accept(contentMatrix);
            renderer.endLayerFrame();
            this.drawFadedLayer(renderer, matrix, layer, layerX, layerY, layerWidth, layerHeight, scrollOffset, minimumOffset);
         }
      }
   }

   private int alphaColor(float alpha) {
      return ColorUtils.withAlpha(-1, (float)Math.max(0, Math.min(255, Math.round(alpha * 255.0F))));
   }

   private void drawFadedLayer(
      GuiDrawApi renderer, Matrix4f matrix, PreparedLayer layer, float x, float y, float width, float height, float scrollOffset, float minimumOffset
   ) {
      float start = this.horizontal ? x : y;
      float length = this.horizontal ? width : height;
      float endFade = Math.min(this.endFadeSize, Math.max(0.0F, length - this.edgePadding) * 0.5F);
      float endAlpha = 1.0F - ratio(scrollOffset - minimumOffset, endFade);
      float endFadeStart = start + length - endFade;
      float opaqueStart = start + this.edgePadding + this.startFadeSize * ratio(-scrollOffset, this.startFadeSize);
      if (opaqueStart > endFadeStart) {
         opaqueStart = endFadeStart;
      }

      if (opaqueStart - start > 0.01F) {
         this.drawLayerSlice(renderer, matrix, layer, x, y, width, height, start, opaqueStart, 0.0F, 1.0F);
      }

      if (endFadeStart - opaqueStart > 0.01F) {
         this.drawLayerSlice(renderer, matrix, layer, x, y, width, height, opaqueStart, endFadeStart, 1.0F, 1.0F);
      }

      if (endFade > 0.01F) {
         this.drawLayerSlice(renderer, matrix, layer, x, y, width, height, endFadeStart, start + length, 1.0F, endAlpha);
      }
   }

   private void drawLayerSlice(
      GuiDrawApi renderer,
      Matrix4f matrix,
      PreparedLayer layer,
      float x,
      float y,
      float width,
      float height,
      float sliceStart,
      float sliceEnd,
      float startAlpha,
      float endAlpha
   ) {
      int startColor = this.alphaColor(startAlpha);
      int endColor = this.alphaColor(endAlpha);
      if (this.horizontal) {
         float minU = (sliceStart - x) / width * layer.maxU();
         float maxU = (sliceEnd - x) / width * layer.maxU();
         renderer.drawLayerTextureGradient(
            matrix,
            layer.getTexture(),
            sliceStart,
            y,
            sliceEnd - sliceStart,
            height,
            minU,
            1.0F,
            maxU,
            1.0F - layer.maxV(),
            startColor,
            endColor,
            endColor,
            startColor
         );
      } else {
         float minV = 1.0F - (sliceStart - y) / height * layer.maxV();
         float maxV = 1.0F - (sliceEnd - y) / height * layer.maxV();
         renderer.drawLayerTextureGradient(
            matrix, layer.getTexture(), x, sliceStart, width, sliceEnd - sliceStart, 0.0F, minV, layer.maxU(), maxV, endColor, endColor, startColor, startColor
         );
      }
   }

   private static float ratio(float value, float range) {
      return range <= 0.01F ? 0.0F : Math.max(0.0F, Math.min(1.0F, value / range));
   }
}
