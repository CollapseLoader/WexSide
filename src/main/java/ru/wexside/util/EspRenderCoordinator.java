package ru.wexside.util;

import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_12249;
import net.minecraft.class_1297;
import net.minecraft.class_1921;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_4604;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_9801;
import org.joml.Matrix4f;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.misc.ChamsRenderer;
import ru.wexside.misc.GlowEspRenderer;
import ru.wexside.misc.WorldBoxSettings;
import ru.wexside.model.esp.EspTargetClassifier;
import ru.wexside.model.esp.EspTargetType;
import ru.wexside.render.BoxEspEntry;
import ru.wexside.render.BoxEspRenderer;
import ru.wexside.render.RenderCamera;
import ru.wexside.render.RenderProjection;
import ru.wexside.render.WorldSkeletonRenderer;

public final class EspRenderCoordinator {
   private final WorldSkeletonRenderer worldSkeletonRenderer;
   private final class_310 mc = class_310.method_1551();
   private final ChamsRenderer chamsRenderer;
   private final NameplateRenderer nameplateRenderer;
   private final GlowEspRenderer glowEspRenderer;
   private final EspFeatureRegistry espFeatures;
   private static final float BOX_FILL_ALPHA = 0.25F;
   private final ServerItemCooldownOverlay serverItemCooldownOverlay;

   public EspRenderCoordinator(EventBus eventBus, EspFeatureRegistry espFeatures) {
      this.espFeatures = espFeatures;
      eventBus.subscribe(WorldRenderEvent.class, this::renderWorldBoxes);
      this.nameplateRenderer = new NameplateRenderer(eventBus, espFeatures);
      this.serverItemCooldownOverlay = new ServerItemCooldownOverlay(eventBus, espFeatures);
      this.chamsRenderer = new ChamsRenderer(eventBus, espFeatures);
      this.glowEspRenderer = new GlowEspRenderer(eventBus, espFeatures);
      this.worldSkeletonRenderer = new WorldSkeletonRenderer(eventBus, espFeatures);
   }

   private void renderWorldBoxes(WorldRenderEvent event) {
      if (this.espFeatures.hasEnabledWorldBox()) {
         class_746 player2 = this.mc.field_1724;
         class_638 world2 = this.mc.field_1687;
         class_243 vec = RenderCamera.position();
         if (player2 != null && world2 != null && vec != null) {
            class_4604 frustum2 = RenderProjection.frustum();
            float tickProgress = event.getFloatType();
            List<BoxEspEntry> entries = new ArrayList<>();

            for(class_1297 entity : world2.method_18112()) {
               if (entity.method_5805()) {
                  EspTargetType targetType = EspTargetClassifier.targetType(entity, player2);
                  if (targetType != null) {
                     WorldBoxSettings settings = this.espFeatures.getWorldBoxSettings(targetType, EspTargetClassifier.relation(entity));
                     if (settings != null && settings.isEnabled() && RenderProjection.isVisible(entity, frustum2)) {
                        class_238 box = this.getExpandedBoundingBox(entity, tickProgress, this.getExpansion(settings.getScale()));
                        entries.add(new BoxEspEntry(box, settings.getColor(), false, settings.isDepthTestEnabled()));
                     }
                  }
               }
            }

            if (!entries.isEmpty()) {
               Matrix4f matrix = new Matrix4f(event.getMatrices().method_23760().method_23761());
               long animationTime = System.currentTimeMillis();
               this.renderEntries(entries, true, matrix, vec, animationTime);
               this.renderEntries(entries, false, matrix, vec, animationTime);
            }
         }
      }
   }

   private void renderEntries(List<BoxEspEntry> entries, boolean depthTest, Matrix4f matrix, class_243 cameraPosition, long animationTime) {
      if (entries.stream().anyMatch(entryx -> entryx.depthTest() == depthTest)) {
         boolean previousDepthTest = org.lwjgl.opengl.GL11.glIsEnabled(2929);
         boolean previousDepthMask = org.lwjgl.opengl.GL11.glGetBoolean(2930);
         boolean previousBlend = org.lwjgl.opengl.GL11.glIsEnabled(3042);

         try {
            com.mojang.blaze3d.opengl.GlStateManager._enableBlend();
            com.mojang.blaze3d.opengl.GlStateManager._blendFuncSeparate(770, 771, 770, 771);
            com.mojang.blaze3d.opengl.GlStateManager._depthMask(false);
            if (depthTest) {
               com.mojang.blaze3d.opengl.GlStateManager._enableDepthTest();
               class_1921 fillLayer = class_12249.method_76023();
               class_287 fillBuffer = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1576);

               for(BoxEspEntry entry : entries) {
                  if (entry.depthTest() == depthTest) {
                     BoxEspRenderer.fill(fillBuffer, matrix, cameraPosition, entry.box(), ColorUtils.multiplyAlpha(entry.color(), 0.25F));
                  }
               }

               class_9801 builtFill = fillBuffer.method_60794();
               if (builtFill != null) {
                  fillLayer.method_60895(builtFill);
               }
            } else {
               com.mojang.blaze3d.opengl.GlStateManager._disableDepthTest();
            }

            class_1921 outlineLayer = depthTest ? class_12249.method_76015() : class_12249.method_76668();
            class_287 outlineBuffer = class_289.method_1348().method_60827(class_5596.field_29344, class_290.field_1576);

            for(BoxEspEntry entry : entries) {
               if (entry.depthTest() == depthTest) {
                  BoxEspRenderer.outline(outlineBuffer, matrix, cameraPosition, entry.box(), entry.color());
               }
            }

            class_9801 builtOutline = outlineBuffer.method_60794();
            if (builtOutline != null) {
               outlineLayer.method_60895(builtOutline);
            }
         } finally {
            if (previousDepthTest) {
               com.mojang.blaze3d.opengl.GlStateManager._enableDepthTest();
            } else {
               com.mojang.blaze3d.opengl.GlStateManager._disableDepthTest();
            }
            com.mojang.blaze3d.opengl.GlStateManager._depthMask(previousDepthMask);
            if (previousBlend) {
               com.mojang.blaze3d.opengl.GlStateManager._enableBlend();
            } else {
               com.mojang.blaze3d.opengl.GlStateManager._disableBlend();
            }
         }
      }
   }

   private class_238 getExpandedBoundingBox(class_1297 entity, float tickProgress, float expansion) {
      class_243 interpolatedPosition = entity.method_30950(tickProgress);
      class_238 box = entity.method_5829()
         .method_989(
            interpolatedPosition.field_1352 - entity.method_23317(),
            interpolatedPosition.field_1351 - entity.method_23318(),
            interpolatedPosition.field_1350 - entity.method_23321()
         );
      return expansion > 0.0F ? box.method_1014((double)expansion) : box;
   }

   private float getExpansion(int scale) {
      return (float)(Math.max(1, scale) - 1) * 0.05F;
   }
}
