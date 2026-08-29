package ru.wexside.misc;

import net.minecraft.class_1657;
import net.minecraft.class_310;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.model.esp.EspTargetClassifier;
import ru.wexside.render.ChamsEffect;
import ru.wexside.render.ChamsSettings;
import ru.wexside.render.RenderCamera;
import ru.wexside.util.EspFeatureRegistry;

public final class ChamsRenderer {
   private static final int MODEL_ESP_MATERIAL_MODE = 5;
   private static volatile ChamsRenderer instance;
   private final EspFeatureRegistry espFeatures;
   private final ChamsEffect chamsEffect;

   public ChamsRenderer(EventBus eventBus, EspFeatureRegistry espFeatures) {
      this.espFeatures = espFeatures;
      this.chamsEffect = new ChamsEffect();
      instance = this;
      eventBus.subscribe(WorldRenderEvent.class, this::renderPlayerChams);
      eventBus.subscribe(WorldSessionEvent.class, this::onWorldSessionChanged);
   }

   private void onWorldSessionChanged(WorldSessionEvent event) {
      this.chamsEffect.releaseFramebuffers();
   }

   private ChamsSettings getSettings(class_1657 player) {
      return this.espFeatures.getChamsSettings(EspTargetClassifier.relation(player));
   }

   private ModelEspSettings getModelSettings(class_1657 player) {
      return this.espFeatures.getModelEspSettings(EspTargetClassifier.relation(player));
   }

   private boolean hasEnabledEffects() {
      return this.espFeatures.hasEnabledChams() || this.espFeatures.hasEnabledModelEsp();
   }

   private void renderPlayerChams(WorldRenderEvent event) {
      class_310 mc = class_310.method_1551();
      if (this.hasEnabledEffects() && mc.field_1687 != null && mc.field_1724 != null && RenderCamera.position() != null) {
         boolean framePrepared = false;
         boolean renderedAnyPlayer = false;

         for(class_1657 player : mc.field_1687.method_18456()) {
            if (player.method_5805() && player != mc.field_1724) {
               ChamsSettings chamsSettings = this.getSettings(player);
               if (chamsSettings != null && chamsSettings.isEnabled()) {
                  boolean renderVisibleParts = chamsSettings.isVisibleFillEnabled();
                  boolean renderHiddenParts = chamsSettings.isHiddenFillEnabled();
                  if (renderVisibleParts || renderHiddenParts) {
                     if (!framePrepared) {
                        this.chamsEffect.prepareFrame();
                        framePrepared = true;
                     }

                     this.chamsEffect
                        .renderEntity(
                           event.getMatrices(),
                           player,
                           event.getFloatType(),
                           chamsSettings.getVisibleColor(),
                           chamsSettings.getHiddenColor(),
                           renderVisibleParts,
                           renderHiddenParts,
                           chamsSettings.getMaterialModeIndex()
                        );
                     renderedAnyPlayer = true;
                  }
               }

               ModelEspSettings modelSettings = this.getModelSettings(player);
               if (modelSettings != null && modelSettings.isEnabled() && modelSettings.isModelStyle()) {
                  if (!framePrepared) {
                     this.chamsEffect.prepareFrame();
                     framePrepared = true;
                  }

                  this.chamsEffect
                     .renderEntity(
                        event.getMatrices(),
                        player,
                        event.getFloatType(),
                        modelSettings.getOutlineColor(),
                        modelSettings.getFillColor(),
                        true,
                        modelSettings.isFillEnabled(),
                        5
                     );
                  renderedAnyPlayer = true;
               }
            }
         }

         if (!renderedAnyPlayer) {
            this.chamsEffect.resetFrameState();
         }
      } else {
         this.chamsEffect.resetFrameState();
      }
   }

   public static void presentPendingChams() {
      ChamsRenderer renderer = instance;
      if (renderer != null && renderer.hasEnabledEffects()) {
         renderer.chamsEffect.present();
      }
   }
}
