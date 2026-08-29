package ru.wexside.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.class_10017;
import net.minecraft.class_10071;
import net.minecraft.class_10366;
import net.minecraft.class_11278;
import net.minecraft.class_12075;
import net.minecraft.class_12249;
import net.minecraft.class_1299;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_276;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_6367;
import net.minecraft.class_9799;
import net.minecraft.class_327.class_6415;
import net.minecraft.class_4597.class_4598;
import net.minecraft.class_8113.class_8123;
import net.minecraft.class_8113.class_8123.class_8124;
import net.minecraft.class_8113.class_8123.class_8125;
import net.minecraft.class_8113.class_8123.class_8126;
import net.minecraft.class_8113.class_8123.class_8230;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.wexside.module.misc.HologramOptimizerModule;

public final class HologramImpostorRenderer {
   private static final Logger LOGGER = LoggerFactory.getLogger(HologramImpostorRenderer.class);
   private static final class_2960 CACHE_TEXTURE_ID = class_2960.method_60655("wexside", "hologram_impostor");
   private static final int MAX_CACHE_ENTRIES = 256;
   private static final int MAX_REQUESTS_PER_FRAME = 256;
   private static final int MAX_TEXTURE_SIZE = 2048;
   private static final int FULL_BRIGHT_LIGHT = 15728880;
   private static final Map<HologramImpostorRenderer.CacheKey, HologramImpostorRenderer.CachedImpostor> CACHE = new HashMap<>();
   private static final List<HologramImpostorRenderer.RenderRequest> REQUESTS = new ArrayList<>();
   private static final FramebufferTextureAdapter CACHE_TEXTURE = new FramebufferTextureAdapter();
   private static class_11278 projection;
   private static class_4598 offscreenConsumers;
   private static boolean textureRegistered;
   private static boolean failed;

   private HologramImpostorRenderer() {
   }

   public static boolean captureEntityLabel(class_10017 state, class_4587 matrices, class_12075 camera) {
      if (canCapture() && state != null && matrices != null && camera != null) {
         class_2561 text = state.field_53337;
         class_243 labelPosition = state.field_53338;
         if (text != null && labelPosition != null && isHologramEntity(state.field_58171)) {
            if (state.field_53332 > HologramOptimizerModule.maxDistanceSquared()) {
               return true;
            } else {
               class_327 textRenderer = class_310.method_1551().field_1772;
               int width = Math.max(1, textRenderer.method_27525(text));
               int height = 10;
               int background = defaultBackgroundColor();
               Matrix4f transform = new Matrix4f(matrices.method_23760().method_23761())
                  .translate((float)labelPosition.field_1352, (float)labelPosition.field_1351 + 0.5F, (float)labelPosition.field_1350)
                  .rotate(camera.field_63081)
                  .scale(0.025F, -0.025F, 0.025F)
                  .translate((float)(-width) / 2.0F, 0.0F, 0.0F);
               HologramImpostorRenderer.TextDrawer drawer = (renderer, consumers) -> {
                  Matrix4f drawMatrix = new Matrix4f();
                  renderer.method_27522(
                     text, 0.0F, 0.0F, -1, false, drawMatrix, consumers, class_6415.field_33993, 0, 15728880
                  );
               };
               REQUESTS.add(
                  new HologramImpostorRenderer.RenderRequest(
                     transform, new HologramImpostorRenderer.CacheKey(text, background, 0), width, height, background, 255, true, drawer
                  )
               );
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean captureTextDisplay(class_10071 state, class_4587 matrices, float tickProgress) {
      if (canCapture() && state != null && matrices != null) {
         class_8125 textLines = state.field_53589;
         class_8230 data = state.field_53588;
         if (textLines != null && !textLines.comp_1247().isEmpty() && data != null) {
            if (state.field_53332 > HologramOptimizerModule.maxDistanceSquared()) {
               return true;
            } else {
               int opacity = data.comp_1336().method_48889(tickProgress) & 0xFF;
               if (opacity == 0) {
                  return true;
               } else {
                  byte flags = data.comp_1338();
                  int background = (flags & 4) != 0 ? defaultBackgroundColor() : data.comp_1337().method_48889(tickProgress);
                  int width = Math.max(1, textLines.comp_1248());
                  int height = Math.max(1, textLines.comp_1247().size() * 10);
                  Matrix4f transform = new Matrix4f(matrices.method_23760().method_23761())
                     .rotate((float) Math.PI, 0.0F, 1.0F, 0.0F)
                     .scale(-0.025F)
                     .translate(1.0F - (float)width / 2.0F, -((float)height - 1.0F), 0.0F);
                  class_8124 alignment = class_8123.method_48902(flags);
                  boolean shadow = (flags & 1) != 0;
                  List<class_8126> lines = List.copyOf(textLines.comp_1247());
                  HologramImpostorRenderer.TextDrawer drawer = (renderer, consumers) -> {
                     Matrix4f drawMatrix = new Matrix4f();
                     float y = 0.0F;

                     for(class_8126 line : lines) {
                        float x = alignedX(alignment, width, line.comp_1250());
                        renderer.method_22942(line.comp_1249(), x, y, -1, shadow, drawMatrix, consumers, class_6415.field_33993, 0, 15728880);
                        y += 10.0F;
                     }
                  };
                  boolean seeThrough = (flags & 2) != 0;
                  REQUESTS.add(
                     new HologramImpostorRenderer.RenderRequest(
                        transform, new HologramImpostorRenderer.CacheKey(textLines, background, flags), width, height, background, opacity, seeThrough, drawer
                     )
                  );
                  return true;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static void flush() {
      if (failed || !HologramOptimizerModule.isEnabled()) {
         clear();
      } else if (!REQUESTS.isEmpty()) {
         try {
            registerTexture();

            for(HologramImpostorRenderer.RenderRequest request : REQUESTS) {
               CACHE.computeIfAbsent(request.key(), ignored -> createImpostor(request));
            }

            class_4598 consumers = class_310.method_1551().method_22940().method_23000();

            for(HologramImpostorRenderer.RenderRequest request : REQUESTS) {
               HologramImpostorRenderer.CachedImpostor cached = CACHE.get(request.key());
               if (cached != null) {
                  drawCached(consumers, request, cached);
               }
            }

            evictUnusedEntries();
         } catch (Throwable var7) {
            failed = true;
            LOGGER.warn("Hologram impostor renderer disabled after a rendering failure", var7);
            clear();
         } finally {
            REQUESTS.clear();
         }
      }
   }

   public static void clear() {
      for(HologramImpostorRenderer.CachedImpostor cached : CACHE.values()) {
         cached.framebuffer().method_1238();
      }

      CACHE.clear();
      REQUESTS.clear();
   }

   private static boolean canCapture() {
      return !failed && REQUESTS.size() < MAX_REQUESTS_PER_FRAME && HologramOptimizerModule.isEnabled() && RenderSystem.tryGetDevice() != null;
   }

   private static boolean isHologramEntity(class_1299<?> type) {
      return type == class_1299.field_6131 || type == class_1299.field_6052 || type == class_1299.field_42456;
   }

   private static void registerTexture() {
      if (!textureRegistered) {
         class_310.method_1551().method_1531().method_4616(CACHE_TEXTURE_ID, CACHE_TEXTURE);
         textureRegistered = true;
      }
   }

   private static HologramImpostorRenderer.CachedImpostor createImpostor(HologramImpostorRenderer.RenderRequest request) {
      int quality = HologramOptimizerModule.qualityScale();

      while(quality > 1 && ((long)request.width() * (long)quality > 2048L || (long)request.height() * (long)quality > 2048L)) {
         --quality;
      }

      class_6367 framebuffer = new class_6367("wexside_hologram_impostor", request.width() * quality, request.height() * quality, true);
      boolean complete = false;

      HologramImpostorRenderer.CachedImpostor var34;
      try {
         RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(framebuffer.method_30277(), 0, framebuffer.method_30278(), 1.0);
         class_4598 consumers = offscreenConsumers();
         GpuBufferSlice projectionSlice = projection().method_71092((float)request.width(), (float)request.height());
         RenderSystem.backupProjectionMatrix();

         try {
            RenderSystem.setProjectionMatrix(projectionSlice, class_10366.field_54954);
            Matrix4fStack modelView = RenderSystem.getModelViewStack();
            modelView.pushMatrix();

            try {
               modelView.identity();
               GpuTextureView previousColor = RenderSystem.outputColorTextureOverride;
               GpuTextureView previousDepth = RenderSystem.outputDepthTextureOverride;
               RenderSystem.outputColorTextureOverride = framebuffer.method_71639();
               RenderSystem.outputDepthTextureOverride = framebuffer.method_71640();

               try {
                  drawBackground(consumers, request.width(), request.height(), request.backgroundColor());
                  request.drawer().draw(class_310.method_1551().field_1772, consumers);
                  consumers.method_22993();
               } finally {
                  RenderSystem.outputColorTextureOverride = previousColor;
                  RenderSystem.outputDepthTextureOverride = previousDepth;
               }
            } finally {
               modelView.popMatrix();
            }
         } finally {
            RenderSystem.restoreProjectionMatrix();
         }

         complete = true;
         HologramImpostorRenderer.CachedImpostor cachedImpostor = new HologramImpostorRenderer.CachedImpostor(
            framebuffer, (float)request.width(), (float)request.height()
         );
         var34 = cachedImpostor;
      } finally {
         if (!complete) {
            framebuffer.method_1238();
         }
      }

      return var34;
   }

   private static void drawBackground(class_4598 consumers, int width, int height, int color) {
      if (color >>> 24 != 0) {
         Matrix4f identity = new Matrix4f();
         class_1921 layer = class_12249.method_76023();
         class_4588 vertices = consumers.method_73477(layer);
         vertices.method_22918(identity, 0.0F, 0.0F, 0.01F).method_39415(color);
         vertices.method_22918(identity, 0.0F, (float)height, 0.01F).method_39415(color);
         vertices.method_22918(identity, (float)width, (float)height, 0.01F).method_39415(color);
         vertices.method_22918(identity, (float)width, 0.0F, 0.01F).method_39415(color);
         consumers.method_22994(layer);
      }
   }

   private static void drawCached(class_4598 consumers, HologramImpostorRenderer.RenderRequest request, HologramImpostorRenderer.CachedImpostor cached) {
      CACHE_TEXTURE.bind(cached.framebuffer());
      class_1921 layer = request.seeThrough() ? class_12249.method_76000(CACHE_TEXTURE_ID) : class_12249.method_75994(CACHE_TEXTURE_ID);
      class_4588 vertices = consumers.method_73477(layer);
      Matrix4f transform = request.transform();
      int color = request.alpha() << 24 | 16777215;
      float width = cached.width();
      float height = cached.height();
      vertex(vertices, transform, 0.0F, 0.0F, 0.0F, 1.0F, color);
      vertex(vertices, transform, width, 0.0F, 1.0F, 1.0F, color);
      vertex(vertices, transform, width, height, 1.0F, 0.0F, color);
      vertex(vertices, transform, 0.0F, 0.0F, 0.0F, 1.0F, color);
      vertex(vertices, transform, width, height, 1.0F, 0.0F, color);
      vertex(vertices, transform, 0.0F, height, 0.0F, 0.0F, color);
      consumers.method_22994(layer);
   }

   private static void vertex(class_4588 vertices, Matrix4f matrix, float x, float y, float u, float v, int color) {
      vertices.method_22918(matrix, x, y, 0.0F).method_39415(color).method_22913(u, v).method_22922(0x00A000A0).method_60803(0x00F000F0).method_22914(0.0F, 1.0F, 0.0F);
   }

   private static void evictUnusedEntries() {
      if (CACHE.size() > 256) {
         HashSet<HologramImpostorRenderer.CacheKey> active = new HashSet<>();

         for(HologramImpostorRenderer.RenderRequest request : REQUESTS) {
            active.add(request.key());
         }

         Iterator<Entry<HologramImpostorRenderer.CacheKey, HologramImpostorRenderer.CachedImpostor>> iterator = CACHE.entrySet().iterator();

         while(CACHE.size() > 256 && iterator.hasNext()) {
            Entry<HologramImpostorRenderer.CacheKey, HologramImpostorRenderer.CachedImpostor> entry = iterator.next();
            if (!active.contains(entry.getKey())) {
               entry.getValue().framebuffer().method_1238();
               iterator.remove();
            }
         }
      }
   }

   private static class_11278 projection() {
      if (projection == null) {
         projection = new class_11278("wexside_hologram_projection", -1000.0F, 1000.0F, true);
      }

      return projection;
   }

   private static class_4598 offscreenConsumers() {
      if (offscreenConsumers == null) {
         offscreenConsumers = class_4597.method_22991(new class_9799(1048576));
      }

      return offscreenConsumers;
   }

   private static int defaultBackgroundColor() {
      class_310 client = class_310.method_1551();
      return client.field_1690 == null ? 0 : (int)(client.field_1690.method_19343(0.25F) * 255.0F) << 24;
   }

   private static float alignedX(class_8124 alignment, int lineWidth, int textWidth) {
      return switch(alignment) {
         case field_42451 -> 0.0F;
         case field_42452 -> (float)(lineWidth - textWidth);
         case field_42450 -> (float)(lineWidth - textWidth) / 2.0F;
         default -> throw new MatchException(null, null);
      };
   }

   private static record CacheKey(Object content, int backgroundColor, int flags) {
   }

   private static record CachedImpostor(class_276 framebuffer, float width, float height) {
   }

   private static record RenderRequest(
      Matrix4f transform,
      HologramImpostorRenderer.CacheKey key,
      int width,
      int height,
      int backgroundColor,
      int alpha,
      boolean seeThrough,
      HologramImpostorRenderer.TextDrawer drawer
   ) {
   }

   @FunctionalInterface
   private interface TextDrawer {
      void draw(class_327 var1, class_4597 var2);
   }
}
