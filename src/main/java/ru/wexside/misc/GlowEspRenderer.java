package ru.wexside.misc;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.model.esp.EspRelation;
import ru.wexside.model.esp.EspTargetClassifier;
import ru.wexside.render.GlowCompositeMode;
import ru.wexside.render.GlowEspEffect;
import ru.wexside.render.RenderCamera;
import ru.wexside.util.EspFeatureRegistry;

public final class GlowEspRenderer {
   private final EspFeatureRegistry espFeatures;
   private static volatile GlowEspRenderer rendererInstance;
   private final EnumMap<EspRelation, List<class_1297>> entitiesByRelation;
   private final GlowEspEffect glowEspEffect;

   public GlowEspRenderer(EventBus eventBus, EspFeatureRegistry espFeatures) {
      this.espFeatures = espFeatures;
      this.glowEspEffect = new GlowEspEffect();
      this.entitiesByRelation = new EnumMap<>(EspRelation.class);

      for(EspRelation relation : EspRelation.values()) {
         this.entitiesByRelation.put(relation, new ArrayList());
      }

      rendererInstance = this;
      eventBus.subscribe(WorldRenderEvent.class, this::renderGlow);
      eventBus.subscribe(WorldSessionEvent.class, this::onWorldSessionChanged);
   }

   private void onWorldSessionChanged(WorldSessionEvent event) {
      this.glowEspEffect.releaseFramebuffers();
   }

   private void renderGlow(WorldRenderEvent event) {
      class_310 mc = class_310.method_1551();
      if (mc.field_1687 == null || mc.field_1724 == null || RenderCamera.position() == null) {
         this.glowEspEffect.resetFrameState();
      } else if (!this.espFeatures.hasEnabledGlow()) {
         this.glowEspEffect.resetFrameState();
      } else {
         this.clearEntityQueue();
         float tickProgress = event.getTickDelta();

         for(class_1297 entity : mc.field_1687.method_18112()) {
            if (entity instanceof class_1657 player && entity != mc.field_1724 && player.method_5805()) {
               EspRelation relation = EspTargetClassifier.relation(entity);
               GlowEspSettings settings = this.espFeatures.getGlowSettings(relation);
               if (settings != null && settings.isEnabled()) {
                  double maximumDistance = settings.getMaximumDistance();
                  if (!(mc.field_1724.method_73189().method_1025(entity.method_73189()) > maximumDistance * maximumDistance)) {
                     this.entitiesByRelation.get(relation).add(entity);
                  }
               }
            }
         }

         Map<Float, List<EspRelation>> relationsByRadius = new LinkedHashMap<>();

         for(EspRelation relation : EspRelation.values()) {
            GlowEspSettings settings = this.espFeatures.getGlowSettings(relation);
            if (!this.entitiesByRelation.get(relation).isEmpty() && settings != null && settings.isEnabled()) {
               relationsByRadius.computeIfAbsent(settings.getRadius(), ignored -> new ArrayList()).add(relation);
            }
         }

         boolean renderedAnyGroup = false;

         for(Entry<Float, List<EspRelation>> entry : relationsByRadius.entrySet()) {
            if (this.glowEspEffect.prepareFrame()) {
               boolean renderedGroup = false;
               List<EspRelation> relations = entry.getValue();

               for(int index = relations.size() - 1; index >= 0; --index) {
                  EspRelation relation = relations.get(index);
                  renderedGroup |= this.glowEspEffect
                     .renderEntities(
                        event.getMatrices(), this.entitiesByRelation.get(relation), tickProgress, this.espFeatures.getGlowSettings(relation).getColor(), false
                     );
               }

               if (renderedGroup) {
                  this.glowEspEffect.composite(entry.getKey(), GlowCompositeMode.BOTH);
                  renderedAnyGroup = true;
               } else {
                  this.glowEspEffect.resetFrameState();
               }
            }
         }

         if (!renderedAnyGroup) {
            this.glowEspEffect.resetFrameState();
         }

         this.clearEntityQueue();
      }
   }

   public static void renderPendingGlow() {
   }

   private void clearEntityQueue() {
      for(List<class_1297> entities : this.entitiesByRelation.values()) {
         entities.clear();
      }
   }
}
