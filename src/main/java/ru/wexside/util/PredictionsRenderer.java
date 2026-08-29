package ru.wexside.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.class_12249;
import net.minecraft.class_1297;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1665;
import net.minecraft.class_1676;
import net.minecraft.class_1685;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1921;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3857;
import net.minecraft.class_4588;
import net.minecraft.class_742;
import net.minecraft.class_4597.class_4598;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import ru.wexside.WexSideClient;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.misc.BakedIconEntry;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.LazyMeshModel;
import ru.wexside.misc.SpatialTransform;
import ru.wexside.module.render.PredictionsModule;
import ru.wexside.prediction.ImpactQuad;
import ru.wexside.prediction.LandingPrediction;
import ru.wexside.prediction.LandingTimeLabel;
import ru.wexside.prediction.ModelOrientation;
import ru.wexside.prediction.PotionImpactMarker;
import ru.wexside.prediction.ProjectileImpact;
import ru.wexside.prediction.TrajectoryPrediction;
import ru.wexside.render.DepthSampler;
import ru.wexside.render.ItemIconCache;
import ru.wexside.render.RenderCamera;
import ru.wexside.render.RenderProjection;
import ru.wexside.render.model.BuiltInMesh;
import ru.wexside.render.world.WorldLineRenderer;

public final class PredictionsRenderer {
   private final TrajectoryPredictor trajectoryPredictor;
   private final Map<Integer, class_243> stablePotionImpactPositions;
   private static final WorldMeshBatchRenderer MODEL_RENDERER = new WorldMeshBatchRenderer("trajectory-prediction");
   private final LazyMeshModel arrowModel;
   private static final MsdfFontRenderer LANDING_LABEL_FONT = FontRegistry.boldText;
   private float animationTimeSeconds;
   private final LazyMeshModel roundProjectileModel;
   private final ModelRenderQueue modelRenderQueue;
   private final PredictionsModule predictionsModule;
   private final List<LandingPrediction> landingPredictions;
   private final List<PotionImpactMarker> potionImpactMarkers;
   private final ItemIconCache itemIconCache = new ItemIconCache();
   private final LazyMeshModel entityBoxModel;
   private final List<ImpactQuad> impactQuads;
   private float pulseScale = 1.0F;
   private final DepthSampler depthSampler;
   private static final class_2960 IMPACT_MARKER_TEXTURE = class_2960.method_60655("wexside", "textures/prediction/arrow_cross.png");
   private final LazyMeshModel tridentModel;

   public PredictionsRenderer(PredictionsModule predictionsModule) {
      this.landingPredictions = new ArrayList<>();
      this.impactQuads = new ArrayList<>();
      this.depthSampler = new DepthSampler();
      this.tridentModel = LazyMeshModel.create(BuiltInMesh.TRIDENT);
      this.roundProjectileModel = LazyMeshModel.create(BuiltInMesh.SKULL);
      this.arrowModel = LazyMeshModel.create(BuiltInMesh.ARROW);
      this.entityBoxModel = LazyMeshModel.create(BuiltInMesh.CUBE);
      this.modelRenderQueue = new ModelRenderQueue();
      this.potionImpactMarkers = new ArrayList<>();
      this.stablePotionImpactPositions = new HashMap<>();
      this.predictionsModule = predictionsModule;
      this.trajectoryPredictor = new TrajectoryPredictor();
   }

   private double[][] rotationYMatrix(float f) {
      double d = Math.toRadians((double)f);
      double d2 = Math.cos(d);
      double d3 = Math.sin(d);
      return new double[][]{{d2, 0.0, d3}, {0.0, 1.0, 0.0}, {-d3, 0.0, d2}};
   }

   private void addPotionImpactBox(class_243 vec) {
      if (this.predictionsModule.showEntityHitBox()) {
         class_310 mc = class_310.method_1551();
         if (mc.field_1687 != null) {
            for(class_1297 entity2 : mc.field_1687.method_18112()) {
               if (this.isLivingTarget(entity2)
                  && new class_243(entity2.method_23317(), entity2.method_23318(), entity2.method_23321()).method_1022(vec) <= 3.0) {
                  WorldLineRenderer.drawEntityBox(entity2, -45747);
               }
            }
         }
      }
   }

   private int withAlpha(int n, float f) {
      int n2 = Math.clamp((long)Math.round(f * 255.0F), 0, 255);
      return n2 << 24 | n & 16777215;
   }

   private void renderActiveProjectilePredictions(WorldRenderEvent floatTypeEvent2, class_310 mc) {
      HashSet<Integer> hashSet = new HashSet<>();

      for(class_1297 entity2 : mc.field_1687.method_18112()) {
         TrajectoryPrediction trajectoryPrediction;
         if (this.shouldTrackEntity(entity2) && (trajectoryPrediction = this.trajectoryPredictor.process2(entity2, floatTypeEvent2.getFloatType())) != null) {
            boolean bl = this.isOwnedByLocalPlayer(mc, entity2);
            int n = this.getEntityTrajectoryColor(entity2, bl);
            this.renderTrajectory(trajectoryPrediction, n, false, 0);
            ProjectileImpact illliiiilI2 = trajectoryPrediction.impact();
            if (illliiiilI2 != null) {
               class_1799 landingItem = this.getProjectileItemStack(entity2);
               if (this.predictionsModule.showLandingTime() && !landingItem.method_7960()) {
                  this.landingPredictions.add(new LandingPrediction(illliiiilI2.position(), trajectoryPrediction.flightTicks(), landingItem));
               }

               ProjectileType iiIlilIilI2;
               if ((iiIlilIilI2 = ProjectileType.fromEntity(entity2)) == ProjectileType.POTION) {
                  hashSet.add(entity2.method_5628());
                  class_243 vec = this.snapPotionImpactToGround(illliiiilI2.position(), illliiiilI2.face());
                  class_243 vec2 = (class_243)this.stablePotionImpactPositions.get(entity2.method_5628());
                  if (vec2 == null) {
                     if (entity2.method_18798().method_1027() < 0.05) {
                        continue;
                     }

                     vec2 = vec;
                     this.stablePotionImpactPositions.put(entity2.method_5628(), vec);
                  } else if (vec2.method_1025(vec) > 2.25) {
                     vec2 = vec;
                     this.stablePotionImpactPositions.put(entity2.method_5628(), vec);
                  }

                  this.potionImpactMarkers.add(new PotionImpactMarker(vec2, n));
                  if (bl) {
                     this.addPotionImpactBox(vec2);
                  }
               } else {
                  if (bl) {
                     this.markHitEntity(illliiiilI2);
                  }

                  if (iiIlilIilI2 != null && this.predictionsModule.shouldShowTrajectory(toProjectileKind(iiIlilIilI2), bl)) {
                     this.queueImpactModel(mc, iiIlilIilI2, illliiiilI2, n, floatTypeEvent2.getFloatType());
                  } else {
                     this.renderCircularImpactMarker(illliiiilI2.position(), illliiiilI2.face(), illliiiilI2.hitEntity());
                  }
               }
            }
         }
      }

      this.stablePotionImpactPositions.keySet().retainAll(hashSet);
   }

   private void flushModelQueue(WorldRenderEvent floatTypeEvent2, class_310 mc) {
      if (!this.modelRenderQueue.isActive()) {
         MODEL_RENDERER.process10(floatTypeEvent2.getMatrices(), mc.method_22940().method_23000(), this.toVec3d(), this.modelRenderQueue);
         this.modelRenderQueue.update();
      }
   }

   private void queuePotionAffectedEntityBoxes(class_310 mc, class_243 vec, int n, float f) {
      if (this.entityBoxModel.isLoaded() && this.entityBoxModel.getMeshModel() != null && !this.entityBoxModel.getMeshModel().getList().isEmpty()) {
         ModelRenderOptions modelRenderOptions = ModelRenderOptions.process4(true).process5(false).process7(2.2F).process10(true);
         int n2 = this.withScaledAlpha(n, 0.0F);

         for(class_1297 entity2 : mc.field_1687.method_18112()) {
            class_1309 entity3;
            if (entity2 instanceof class_1309
               && (entity3 = (class_1309)entity2).method_5805()
               && !(new class_243(entity3.method_23317(), entity3.method_23318(), entity3.method_23321()).method_1022(vec) > 3.0)) {
               class_238 box = entity3.method_5829();
               double d = class_3532.method_16436((double)f, entity3.field_6014, entity3.method_23317()) - entity3.method_23317();
               double d2 = class_3532.method_16436((double)f, entity3.field_6036, entity3.method_23318()) - entity3.method_23318();
               double d3 = class_3532.method_16436((double)f, entity3.field_5969, entity3.method_23321()) - entity3.method_23321();
               this.modelRenderQueue
                  .process7(
                     this.entityBoxModel.getMeshModel(),
                     new SpatialTransform(
                        (box.field_1323 + box.field_1320) / 2.0 + d,
                        box.field_1322 + d2 + 0.01F + 0.004F,
                        (box.field_1321 + box.field_1324) / 2.0 + d3,
                        (float)(box.field_1320 - box.field_1323 + 0.12F),
                        0.02F,
                        (float)(box.field_1324 - box.field_1321 + 0.12F)
                     ),
                     n2,
                     modelRenderOptions
                  );
            }
         }
      }
   }

   private double[][] rotationXMatrix(float f) {
      double d = Math.toRadians((double)f);
      double d2 = Math.cos(d);
      double d3 = Math.sin(d);
      return new double[][]{{1.0, 0.0, 0.0}, {0.0, d2, -d3}, {0.0, d3, d2}};
   }

   private ModelOrientation orientationAlongVelocity(class_243 vec) {
      float f = (float)Math.toDegrees(Math.atan2(vec.field_1352, vec.field_1350)) - 90.0F;
      float f2 = (float)Math.toDegrees(Math.atan2(vec.field_1351, Math.sqrt(vec.field_1352 * vec.field_1352 + vec.field_1350 * vec.field_1350))) + 90.0F;
      double[][] dArray = this.multiplyMatrices(this.multiplyMatrices(this.rotationYMatrix(f), this.rotationZMatrix(f2)), this.rotationXMatrix(90.0F));
      return this.matrixToOrientation(dArray);
   }

   private void renderLandingLabel(GuiDrawApi drawApi, Matrix4f matrix4f, LandingTimeLabel label) {
      this.itemIconCache.process3(drawApi, matrix4f, label.bakedIcon, label.iconX, label.iconY, 9.0F);
      LANDING_LABEL_FONT.process2(matrix4f, drawApi, label.text, label.textX, label.textY, 7.0F, -1);
   }

   private float impactOutlineWidth(float f) {
      float f2 = class_3532.method_15363(f, 0.0F, 1.0F);
      return 1.0F + 1.0F * f2;
   }

   private class_243 offsetOnImpactPlane(class_243 vec, class_2350 process17, double d, double d2) {
      return switch(process17.method_10166()) {
         case field_11052 -> vec.method_1031(d, 0.0, d2);
         case field_11051 -> vec.method_1031(d, d2, 0.0);
         case field_11048 -> vec.method_1031(0.0, d, d2);
         default -> throw new MatchException(null, null);
      };
   }

   private LandingTimeLabel createLandingTimeLabel(float f, float f2, String string, class_1799 stack) {
      float f3 = LANDING_LABEL_FONT.process3(string, 7.0F);
      float f4 = LANDING_LABEL_FONT.process4(string, 7.0F);
      float f5 = Math.max(9.0F, f4);
      float f6 = 14.0F + f3 + 3.0F;
      float f7 = f5 + 6.0F;
      float f8 = f - f6 / 2.0F;
      float f9 = f2 - f7 / 2.0F;
      float f10 = f8 + 3.0F;
      float f11 = f9 + (f7 - 9.0F) / 2.0F;
      float f12 = f10 + 9.0F + 2.0F;
      float f13 = f9 + (f7 - f4) / 2.0F;
      return new LandingTimeLabel(f8, f9, f6, f7, f10, f11, f12, f13, string, stack);
   }

   private void renderCircularImpactMarker(class_243 vec, class_2350 process17, class_1297 entity2) {
      if (this.predictionsModule.showImpactMarker() && vec != null && process17 != null) {
         int n = entity2 instanceof class_1309 ? -45747 : this.predictionsModule.getImpactColorArgb();
         double d = 0.95 + 0.05 * Math.sin((double)this.animationTimeSeconds * Math.PI);
         double d2 = 0.3 * d;
         double d3 = 0.1 * d;
         ArrayList<WorldLineRenderer.Segment> lines = new ArrayList<>(42);

         for(int i = 0; i < 40; ++i) {
            double d4 = (Math.PI * 2) * (double)i / 40.0;
            double d5 = (Math.PI * 2) * (double)(i + 1) / 40.0;
            lines.add(
               new WorldLineRenderer.Segment(
                  this.offsetOnImpactPlane(vec, process17, Math.cos(d4) * d2, Math.sin(d4) * d2),
                  this.offsetOnImpactPlane(vec, process17, Math.cos(d5) * d2, Math.sin(d5) * d2),
                  n,
                  n
               )
            );
         }

         switch(process17.method_10166()) {
            case field_11052:
               lines.add(new WorldLineRenderer.Segment(vec.method_1031(-d3, 0.0, 0.0), vec.method_1031(d3, 0.0, 0.0), n, n));
               lines.add(new WorldLineRenderer.Segment(vec.method_1031(0.0, 0.0, -d3), vec.method_1031(0.0, 0.0, d3), n, n));
               break;
            case field_11051:
               lines.add(new WorldLineRenderer.Segment(vec.method_1031(-d3, 0.0, 0.0), vec.method_1031(d3, 0.0, 0.0), n, n));
               lines.add(new WorldLineRenderer.Segment(vec.method_1031(0.0, -d3, 0.0), vec.method_1031(0.0, d3, 0.0), n, n));
               break;
            case field_11048:
               lines.add(new WorldLineRenderer.Segment(vec.method_1031(0.0, -d3, 0.0), vec.method_1031(0.0, d3, 0.0), n, n));
               lines.add(new WorldLineRenderer.Segment(vec.method_1031(0.0, 0.0, -d3), vec.method_1031(0.0, 0.0, d3), n, n));
         }

         WorldLineRenderer.draw(lines);
      }
   }

   private void renderImpactQuads(WorldRenderEvent floatTypeEvent2, class_310 mc) {
      if (!this.impactQuads.isEmpty()) {
         class_243 vec = this.toVec3d();
         Matrix4f matrix4f = new Matrix4f(floatTypeEvent2.getMatrices().method_23760().method_23761());
         class_4598 iIiIliiliI2 = mc.method_22940().method_23000();
         class_1921 enabled = class_12249.method_76000(IMPACT_MARKER_TEXTURE);
         class_4588 iliIIiiliI2 = iIiIliiliI2.method_73477(enabled);
         double d = 0.16 * (double)this.pulseScale;

         for(ImpactQuad marker : this.impactQuads) {
            int n = marker.color();
            int n2 = n >>> 24 & 0xFF;
            int n3 = n >> 16 & 0xFF;
            int n4 = n >> 8 & 0xFF;
            int n5 = n & 0xFF;
            class_243 vec2 = marker.position();
            class_2350 process17 = marker.face();
            class_243 vec3 = this.offsetOnImpactPlane(vec2, process17, -d, -d).method_1020(vec);
            class_243 vec4 = this.offsetOnImpactPlane(vec2, process17, d, -d).method_1020(vec);
            class_243 vec5 = this.offsetOnImpactPlane(vec2, process17, d, d).method_1020(vec);
            class_243 vec6 = this.offsetOnImpactPlane(vec2, process17, -d, d).method_1020(vec);
            this.writeImpactVertex(iliIIiiliI2, matrix4f, vec3, 0.0F, 0.0F, n3, n4, n5, n2);
            this.writeImpactVertex(iliIIiiliI2, matrix4f, vec4, 1.0F, 0.0F, n3, n4, n5, n2);
            this.writeImpactVertex(iliIIiiliI2, matrix4f, vec5, 1.0F, 1.0F, n3, n4, n5, n2);
            this.writeImpactVertex(iliIIiiliI2, matrix4f, vec3, 0.0F, 0.0F, n3, n4, n5, n2);
            this.writeImpactVertex(iliIIiiliI2, matrix4f, vec5, 1.0F, 1.0F, n3, n4, n5, n2);
            this.writeImpactVertex(iliIIiiliI2, matrix4f, vec6, 0.0F, 1.0F, n3, n4, n5, n2);
         }

         iIiIliiliI2.method_22994(enabled);
      }
   }

   public void onHudRender(HudRenderEvent gameEvent20) {
      class_310 mc = class_310.method_1551();
      if (!this.predictionsModule.isModuleEnabled() || mc.field_1724 == null || mc.field_1687 == null || RenderCamera.position() == null) {
         this.itemIconCache.update3();
      } else if (this.predictionsModule.showLandingTime() && !this.landingPredictions.isEmpty()) {
         ArrayList<LandingTimeLabel> arrayList = new ArrayList<>(this.landingPredictions.size());

         for(LandingPrediction prediction : this.landingPredictions) {
            Vector2f vector2f = RenderProjection.project(prediction.position());
            class_1799 stack;
            if (vector2f != null && !(stack = prediction.icon()).method_7960()) {
               float f2 = (float)Math.round((float)prediction.flightTicks() / 20.0F * 10.0F) / 10.0F;
               arrayList.add(this.createLandingTimeLabel(vector2f.x, vector2f.y, f2 + " сек.", stack));
            }
         }

         if (!arrayList.isEmpty()) {
            float f3 = (float)mc.method_22683().method_4495();
            this.itemIconCache.update2();

            for(LandingTimeLabel label : arrayList) {
               label.bakedIcon = this.itemIconCache.process(label.item);
            }

            ArrayList<BakedIconEntry> arrayList2 = new ArrayList<>();
            this.itemIconCache.process2(f3, arrayList2);
            if (!arrayList2.isEmpty()) {
               WexSideClient.getRenderPipeline2().setList(arrayList2);
            }

            GuiDrawApi drawApi = WexSideClient.getHudRenderer();
            Matrix4f matrix = new Matrix4f().scale(f3);
            float f = 2.0F / f3;
            drawApi.begin();

            try {
               for(LandingTimeLabel label : arrayList) {
                  drawApi.drawRoundedOutline(matrix, label.x, label.y, label.width, label.height, 8.0F, f, -1340597728);
                  this.renderLandingLabel(drawApi, matrix, label);
               }
            } finally {
               drawApi.end();
            }

            this.itemIconCache.update();
         }
      } else {
         this.itemIconCache.update3();
      }
   }

   public void onWorldRender(WorldRenderEvent floatTypeEvent2) {
      class_310 mc = class_310.method_1551();
      if (this.predictionsModule.isModuleEnabled() && mc.field_1724 != null && mc.field_1687 != null) {
         this.clearFrameData();
         float f = ((float)mc.field_1687.method_8532() + floatTypeEvent2.getFloatType()) / 20.0F;
         this.pulseScale = 0.95F + 0.05F * class_3532.method_15374((double)((float)((double)f * Math.PI)));
         this.animationTimeSeconds = f;
         this.renderHeldItemPredictions(floatTypeEvent2, mc);
         this.renderOtherPlayerPredictions(floatTypeEvent2, mc);
         this.renderActiveProjectilePredictions(floatTypeEvent2, mc);
         this.renderDroppedItemTrajectories(floatTypeEvent2, mc);
         this.queuePotionEffectVolumes(floatTypeEvent2, mc);
         this.flushModelQueue(floatTypeEvent2, mc);
         this.renderImpactQuads(floatTypeEvent2, mc);
         this.updatePotionDepthSampler();
      } else {
         this.clearFrameData();
         this.stablePotionImpactPositions.clear();
      }
   }

   public void resetSession() {
      this.clearFrameData();
      this.itemIconCache.update3();
   }

   private void writeImpactVertex(class_4588 iliIIiiliI2, Matrix4f matrix4f, class_243 vec, float f, float f2, int n, int n2, int n3, int n4) {
      iliIIiiliI2.method_22918(matrix4f, (float)vec.field_1352, (float)vec.field_1351, (float)vec.field_1350).method_1336(n, n2, n3, n4).method_22913(f, f2).method_22922(0x00A000A0).method_60803(0x00F000F0).method_22914(0.0F, 1.0F, 0.0F);
   }

   private void markHitEntity(ProjectileImpact impact) {
      if (this.predictionsModule.showEntityHitBox() && this.isLivingTarget(impact.hitEntity())) {
         WorldLineRenderer.drawEntityBox(impact.hitEntity(), -45747);
      }
   }

   private double[][] rotationZMatrix(float f) {
      double d = Math.toRadians((double)f);
      double d2 = Math.cos(d);
      double d3 = Math.sin(d);
      return new double[][]{{d2, -d3, 0.0}, {d3, d2, 0.0}, {0.0, 0.0, 1.0}};
   }

   private void addClassicArrowImpact(class_310 mc, ProjectileType iiIlilIilI2, ProjectileImpact illliiiilI2, int n, float f) {
      class_243 vec = this.trajectoryPredictor.process4(mc.field_1724, f);
      float f2 = this.calculateDistanceFade(vec.method_1022(illliiiilI2.position()));
      if (!(f2 < 0.02F)) {
         int n2 = illliiiilI2.hitEntity() != null ? -45747 : n;
         class_2350 process17 = illliiiilI2.face() != null ? illliiiilI2.face() : class_2350.field_11036;
         this.impactQuads.add(new ImpactQuad(this.adjustImpactPosition(iiIlilIilI2, illliiiilI2), process17, this.withAlpha(n2, f2)));
      }
   }

   private int withScaledAlpha(int n, float f) {
      int n2 = Math.max(0, Math.min(255, Math.round(f * 0.85F * 255.0F)));
      return n2 << 24 | n & 16777215;
   }

   private void renderTrajectory(TrajectoryPrediction prediction, int n, boolean bl, int n2) {
      List<class_243> list = prediction.points();
      if (list != null && list.size() >= 2) {
         int n3 = list.size() - 1;
         ArrayList<WorldLineRenderer.Segment> lines = new ArrayList<>(n3);

         for(int i = 0; i < n3; ++i) {
            if (i >= n2 && (!bl || i % 2 == 0)) {
               float f = Math.max(bl ? 0.16F : 0.08F, 1.0F - (float)i / (float)n3);
               float f2 = Math.max(bl ? 0.08F : 0.04F, 1.0F - (float)(i + 1) / (float)n3);
               if (bl) {
                  f *= 0.65F;
                  f2 *= 0.65F;
               }

               lines.add(new WorldLineRenderer.Segment((class_243)list.get(i), (class_243)list.get(i + 1), this.withAlpha(n, f), this.withAlpha(n, f2)));
            }
         }

         WorldLineRenderer.draw(lines);
      }
   }

   private class_243 adjustImpactPosition(ProjectileType iiIlilIilI2, ProjectileImpact illliiiilI2) {
      if (illliiiilI2.hitEntity() != null || illliiiilI2.face() == null) {
         return illliiiilI2.position();
      } else if (iiIlilIilI2 != ProjectileType.ARROW && iiIlilIilI2 != ProjectileType.CROSSBOW) {
         return illliiiilI2.position();
      } else {
         class_243 normal = class_243.method_24954(illliiiilI2.face().method_62675());
         return illliiiilI2.position().method_1019(normal.method_1021(0.018));
      }
   }

   private class_243 snapPotionImpactToGround(class_243 vec, class_2350 process17) {
      double d = process17 == class_2350.field_11036 ? vec.field_1351 + 0.003 : Math.floor(vec.field_1351) + 0.003;
      return new class_243(vec.field_1352, d, vec.field_1350);
   }

   private void queueImpactModel(class_310 mc, ProjectileType iiIlilIilI2, ProjectileImpact illliiiilI2, int n, float f) {
      if (illliiiilI2 != null) {
         if ((iiIlilIilI2 == ProjectileType.ARROW || iiIlilIilI2 == ProjectileType.CROSSBOW) && this.predictionsModule.isClassicArrowStyle()) {
            this.addClassicArrowImpact(mc, iiIlilIilI2, illliiiilI2, n, f);
         } else {
            float f2;
            float f3;
            ModelOrientation orientation;
            LazyMeshModel arrowModel;
            switch(iiIlilIilI2) {
               case TRIDENT:
                  if (!this.tridentModel.isLoaded() || this.tridentModel.getMeshModel() == null || this.tridentModel.getMeshModel().getList().isEmpty()) {
                     return;
                  }

                  arrowModel = this.tridentModel;
                  orientation = this.orientationAlongVelocity(illliiiilI2.velocityDirection());
                  f3 = 0.2F;
                  f2 = 0.5F;
                  break;
               case PEARL:
               case POTION:
                  if (!this.roundProjectileModel.isLoaded()
                     || this.roundProjectileModel.getMeshModel() == null
                     || this.roundProjectileModel.getMeshModel().getList().isEmpty()) {
                     return;
                  }

                  arrowModel = this.roundProjectileModel;
                  float f4 = (float)Math.toDegrees(Math.atan2(illliiiilI2.velocityDirection().field_1352, illliiiilI2.velocityDirection().field_1350)) - 90.0F;
                  orientation = this.orientationFromYawPitch(f4, 90.0F);
                  f3 = 0.3F;
                  f2 = 0.5F;
                  break;
               case ARROW:
               case CROSSBOW:
                  if (!this.arrowModel.isLoaded() || this.arrowModel.getMeshModel() == null || this.arrowModel.getMeshModel().getList().isEmpty()) {
                     return;
                  }

                  arrowModel = this.arrowModel;
                  orientation = this.orientationAlongVelocity(illliiiilI2.velocityDirection());
                  f3 = 0.1F;
                  f2 = 1.0F;
                  break;
               case ITEM:
                  return;
               default:
                  return;
            }

            class_243 vec = this.trajectoryPredictor.process4(mc.field_1724, f);
            float f5 = this.calculateDistanceFade(vec.method_1022(illliiiilI2.position()));
            if (!(f5 < 0.02F)) {
               float f6 = this.pulseScale;
               int n2 = illliiiilI2.hitEntity() != null ? -45747 : n;
               class_243 vec2 = this.adjustImpactPosition(iiIlilIilI2, illliiiilI2);
               this.modelRenderQueue
                  .process7(
                     arrowModel.getMeshModel(),
                     new SpatialTransform(
                        vec2.field_1352,
                        vec2.field_1351,
                        vec2.field_1350,
                        f3 * f6,
                        f3 * f6,
                        f3 * f6,
                        orientation.yaw(),
                        orientation.pitch(),
                        orientation.roll()
                     ),
                     this.withScaledAlpha(n2, f5 * f2),
                     ModelRenderOptions.process4(true).process7(this.impactOutlineWidth(f5 * f2)).process10(false)
                  );
            }
         }
      }
   }

   private boolean isOwnedByLocalPlayer(class_310 mc, class_1297 entity2) {
      if (mc.field_1724 == null) {
         return false;
      } else if (entity2 instanceof class_1676 projectileEntity) {
         return projectileEntity.method_24921() == mc.field_1724;
      } else {
         return false;
      }
   }

   private void renderDroppedItemTrajectories(WorldRenderEvent floatTypeEvent2, class_310 mc) {
      if (this.predictionsModule.isItemsEnabled()) {
         for(class_1297 entity2 : mc.field_1687.method_18112()) {
            TrajectoryPrediction trajectoryPrediction;
            class_1542 iIIIlIIIII2;
            if (entity2 instanceof class_1542
               && (iIIIlIIIII2 = (class_1542)entity2).method_5805()
               && !iIIIlIIIII2.method_24828()
               && !(iIIIlIIIII2.method_18798().method_1027() < 1.0E-5)
               && (trajectoryPrediction = this.trajectoryPredictor.process3(iIIIlIIIII2, floatTypeEvent2.getFloatType())) != null) {
               this.renderTrajectory(trajectoryPrediction, this.predictionsModule.getTrajectoryColor(toProjectileKind(ProjectileType.ITEM), true), true, 0);
            }
         }
      }
   }

   private ModelOrientation orientationFromYawPitch(float f, float f2) {
      return this.matrixToOrientation(this.multiplyMatrices(this.rotationYMatrix(f), this.rotationXMatrix(f2)));
   }

   private class_1799 getProjectileItemStack(class_1297 entity2) {
      if (entity2 instanceof class_1685) {
         return new class_1799(class_1802.field_8547);
      } else if (entity2 instanceof class_3857 thrownItemEntity) {
         return thrownItemEntity.method_7495();
      } else {
         return entity2 instanceof class_1665 ? new class_1799(class_1802.field_8107) : class_1799.field_8037;
      }
   }

   private void renderOtherPlayerPredictions(WorldRenderEvent floatTypeEvent2, class_310 mc) {
      for(class_742 player : mc.field_1687.method_18456()) {
         class_1799 stack;
         ProjectileType iiIlilIilI2;
         if (player != mc.field_1724
            && !(Math.abs(player.method_36455()) > 89.5F)
            && (iiIlilIilI2 = ProjectileType.fromItem((stack = player.method_6047()).method_7909())) != null
            && this.predictionsModule.shouldShowTrajectory(toProjectileKind(iiIlilIilI2), false)) {
            for(TrajectoryPrediction trajectoryPrediction : this.trajectoryPredictor.process(player, stack, floatTypeEvent2.getFloatType())) {
               ProjectileImpact illliiiilI2 = trajectoryPrediction.impact();
               if (illliiiilI2 != null) {
                  this.queueImpactModel(
                     mc,
                     iiIlilIilI2,
                     illliiiilI2,
                     this.predictionsModule.getTrajectoryColor(toProjectileKind(iiIlilIilI2), false),
                     floatTypeEvent2.getFloatType()
                  );
               }
            }
         }
      }
   }

   private void clearFrameData() {
      this.potionImpactMarkers.clear();
      this.landingPredictions.clear();
      this.impactQuads.clear();
   }

   private class_243 toVec3d() {
      return RenderCamera.position() != null ? RenderCamera.position() : class_243.field_1353;
   }

   private boolean isLivingTarget(class_1297 entity2) {
      if (entity2 != null && entity2.method_5805() && entity2 != class_310.method_1551().field_1724) {
         return entity2 instanceof class_1657 || entity2 instanceof class_1308;
      } else {
         return false;
      }
   }

   private float calculateDistanceFade(double d) {
      float f = (float)class_3532.method_15350((d - 0.5) / 13.5, 0.0, 1.0);
      return Math.min(f * f * (3.0F - 2.0F * f) * 2.5F, 1.0F);
   }

   private void renderHeldItemPredictions(WorldRenderEvent floatTypeEvent2, class_310 mc) {
      if (!(Math.abs(mc.field_1724.method_36455()) > 89.5F)) {
         class_1799 stack = getTrackedHeldStack(this.predictionsModule, mc.field_1724);
         if (!stack.method_7960()) {
            ProjectileType iiIlilIilI2 = ProjectileType.fromItem(stack.method_7909());
            if (iiIlilIilI2 != null) {
               List<TrajectoryPrediction> list = this.trajectoryPredictor.process(mc.field_1724, stack, floatTypeEvent2.getFloatType());
               boolean bl = !mc.field_1690.method_31044().method_31034();

               for(TrajectoryPrediction trajectoryPrediction : list) {
                  if (this.predictionsModule.showHeldItems()) {
                     this.renderTrajectory(
                        trajectoryPrediction, this.predictionsModule.getTrajectoryColor(toProjectileKind(iiIlilIilI2), true), false, bl ? 10 : 0
                     );
                  }

                  ProjectileImpact illliiiilI2;
                  if ((illliiiilI2 = trajectoryPrediction.impact()) != null) {
                     if (iiIlilIilI2 == ProjectileType.POTION) {
                        class_243 vec = this.snapPotionImpactToGround(illliiiilI2.position(), illliiiilI2.face());
                        this.potionImpactMarkers
                           .add(new PotionImpactMarker(vec, this.predictionsModule.getTrajectoryColor(toProjectileKind(iiIlilIilI2), true)));
                        this.addPotionImpactBox(vec);
                     } else {
                        this.markHitEntity(illliiiilI2);
                        if (this.predictionsModule.shouldShowTrajectory(toProjectileKind(iiIlilIilI2), true)) {
                           this.queueImpactModel(
                              mc,
                              iiIlilIilI2,
                              illliiiilI2,
                              this.predictionsModule.getTrajectoryColor(toProjectileKind(iiIlilIilI2), true),
                              floatTypeEvent2.getFloatType()
                           );
                        } else {
                           this.renderCircularImpactMarker(illliiiilI2.position(), illliiiilI2.face(), illliiiilI2.hitEntity());
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private double clampUnit(double d) {
      return Math.max(-1.0, Math.min(1.0, d));
   }

   private void updatePotionDepthSampler() {
      if (this.predictionsModule.isModuleEnabled() && this.predictionsModule.isPotionsEnabled() && !this.potionImpactMarkers.isEmpty()) {
         if (RenderCamera.position() != null) {
            class_310 mc = class_310.method_1551();
            if (mc.field_1687 != null && mc.field_1724 != null) {
               this.depthSampler.setList(this.potionImpactMarkers);
            }
         }
      }
   }

   private ModelOrientation matrixToOrientation(double[][] dArray) {
      double d = Math.asin(this.clampUnit(-dArray[1][2]));
      double d2 = Math.atan2(dArray[1][0], dArray[1][1]);
      double d3 = Math.atan2(dArray[0][2], dArray[2][2]);
      return new ModelOrientation((float)Math.toDegrees(d3), (float)Math.toDegrees(d), (float)Math.toDegrees(d2));
   }

   private void queuePotionEffectVolumes(WorldRenderEvent floatTypeEvent2, class_310 mc) {
      if (this.predictionsModule.isPotionsEnabled() && !this.potionImpactMarkers.isEmpty()) {
         for(PotionImpactMarker marker : this.potionImpactMarkers) {
            this.queuePotionAffectedEntityBoxes(mc, marker.position(), marker.color(), floatTypeEvent2.getFloatType());
         }
      }
   }

   private double[][] multiplyMatrices(double[][] dArray, double[][] dArray2) {
      double[][] dArray3 = new double[3][3];

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 3; ++j) {
            dArray3[i][j] = dArray[i][0] * dArray2[0][j] + dArray[i][1] * dArray2[1][j] + dArray[i][2] * dArray2[2][j];
         }
      }

      return dArray3;
   }

   public static PredictionsModule.ProjectileKind toProjectileKind(ProjectileType type) {
      if (type == null) {
         return PredictionsModule.ProjectileKind.UNKNOWN;
      } else {
         return switch(type) {
            case TRIDENT -> PredictionsModule.ProjectileKind.TRIDENT;
            case PEARL -> PredictionsModule.ProjectileKind.PEARL;
            case POTION -> PredictionsModule.ProjectileKind.POTION;
            case ARROW -> PredictionsModule.ProjectileKind.ARROW;
            case CROSSBOW -> PredictionsModule.ProjectileKind.CROSSBOW;
            case ITEM -> PredictionsModule.ProjectileKind.ITEM;
            default -> throw new MatchException(null, null);
         };
      }
   }

   public static class_1799 getTrackedHeldStack(PredictionsModule module, class_1657 player) {
      if (player == null) {
         return class_1799.field_8037;
      } else {
         class_1792 mainItem = player.method_6047().method_7909();
         if (module.isHeldItemCategoryEnabled(toProjectileKind(ProjectileType.fromItem(mainItem)))) {
            return player.method_6047();
         } else {
            class_1792 offItem = player.method_6079().method_7909();
            return module.isHeldItemCategoryEnabled(toProjectileKind(ProjectileType.fromItem(offItem))) ? player.method_6079() : class_1799.field_8037;
         }
      }
   }

   private boolean shouldTrackEntity(class_1297 entity) {
      ProjectileType type = ProjectileType.fromEntity(entity);
      if (type != null && type != ProjectileType.ITEM) {
         class_1685 slowEntity;
         if (entity instanceof class_1685 && (slowEntity = (class_1685)entity).method_18798().method_1027() < 1.0E-7) {
            return false;
         } else {
            PredictionsModule.ProjectileKind kind = toProjectileKind(type);

            return switch(kind) {
               case TRIDENT -> this.predictionsModule.isTridentEnabled();
               case PEARL -> this.predictionsModule.isPearlEnabled();
               case ARROW -> this.predictionsModule.isArrowEnabled() || this.predictionsModule.isCrossbowEnabled();
               case POTION -> this.predictionsModule.isPotionsEnabled();
               case CROSSBOW, ITEM, UNKNOWN -> false;
               default -> throw new MatchException(null, null);
            };
         }
      } else {
         return false;
      }
   }

   private int getEntityTrajectoryColor(class_1297 entity, boolean self) {
      ProjectileType type = ProjectileType.fromEntity(entity);
      return type == ProjectileType.ARROW && !this.predictionsModule.isArrowEnabled() && this.predictionsModule.isCrossbowEnabled()
         ? this.predictionsModule.getTrajectoryColor(PredictionsModule.ProjectileKind.CROSSBOW, self)
         : this.predictionsModule.getTrajectoryColor(toProjectileKind(type), self);
   }
}
