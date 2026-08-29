package ru.wexside.util;

import java.util.Arrays;
import net.minecraft.class_1309;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import org.joml.Matrix4f;
import ru.wexside.animation.TimedPulse;
import ru.wexside.event.EntityAttackEvent;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.misc.LazyMeshModel;
import ru.wexside.misc.MeshModel;
import ru.wexside.misc.PreviousValueTransition;
import ru.wexside.misc.SpatialTransform;
import ru.wexside.misc.TargetChange;
import ru.wexside.misc.TargetEspEffect;
import ru.wexside.misc.TargetTransition;
import ru.wexside.misc.TransitionPhase;
import ru.wexside.module.render.TargetEspRenderer;
import ru.wexside.render.model.BuiltInMesh;

public final class SwordTargetEspRenderer extends TargetEspRenderer implements TargetEspEffect {
   private final Matrix4f[] matrix4f;
   private static final SpatialTransform IDENTITY_TRANSFORM = new SpatialTransform(0.0, 0.0, 0.0, 1.0F, 1.0F, 1.0F);
   static final String string = "sword";
   static final int slot = 7;
   static final float value6 = 0.5F;
   static final float value7 = 0.12F;
   private final Matrix4f matrix4f2;
   private final TimedPulse attackPulse;
   private final PreviousValueTransition<SwordTargetEspRenderer.TargetGeometry> previousGeometry;
   static final int slot2 = -65476;
   static final float value8 = 1.12F;
   static final float value9 = 1.28F;
   static final long member8917 = 300L;
   static final long member7850 = 240L;
   private MeshModel orbitModel;
   static final double value10 = 0.2;
   static final float value11 = -90.0F;
   private final float[] value12;
   private final ModelRenderQueue modelRenderQueue;
   private static final WorldMeshBatchRenderer MODEL_RENDERER = new WorldMeshBatchRenderer("target-sword-orbit");
   private final double[] value13;
   private final LazyMeshModel lazyMeshModel = LazyMeshModel.create(BuiltInMesh.SWORD);
   static final long member12443 = 380L;
   private final ModelRenderOptions modelRenderOptions;
   private final TargetTransition<class_1309> targetTransition;
   static final float value14 = 0.78F;
   static final boolean flag = true;
   static final double value15 = 0.0021;
   private final double[] value16;
   static final double value17 = 1.4;
   static final long member8766 = 380L;
   private SwordTargetEspRenderer.TargetGeometry lastGeometry;

   public SwordTargetEspRenderer() {
      this.modelRenderQueue = new ModelRenderQueue();
      this.modelRenderOptions = ModelRenderOptions.process4(true).process10(false);
      this.targetTransition = new TargetTransition();
      this.attackPulse = new TimedPulse();
      this.previousGeometry = new PreviousValueTransition<>();
      this.value13 = new double[7];
      this.value16 = new double[7];
      this.value12 = new float[7];
      this.matrix4f = new Matrix4f[7];
      this.matrix4f2 = new Matrix4f().scaling(0.0F);

      for(int i = 0; i < 7; ++i) {
         this.matrix4f[i] = new Matrix4f();
      }
   }

   private int process(int n, int n2, long l, int n3) {
      float f = 0.5F + 0.5F * AnimationMath.sin((float)l * 0.0042F + (float)n3 * 0.9F);
      return AnimationMath.lerpColor(n, n2, f * 0.48F);
   }

   private SwordTargetEspRenderer.TargetGeometry captureGeometry(class_1309 entity2, float f) {
      if (entity2 != null && entity2.method_5805()) {
         class_238 box = entity2.method_5829();
         return new SwordTargetEspRenderer.TargetGeometry(
            this.interpolatedPosition(entity2, f), (double)entity2.method_17682(), box.field_1320 - box.field_1323, box.field_1324 - box.field_1321
         );
      } else {
         return null;
      }
   }

   @Override
   public void setEntityAttackEvent(EntityAttackEvent iIiiiilIiIEvent) {
      if (iIiiiilIiIEvent.getEntity() != null) {
         this.attackPulse.trigger();
      }
   }

   @Override
   public void setWorldRenderEvent(WorldRenderEvent floatTypeEvent2) {
      class_310 mc = this.client();
      if (this.canRender(mc)) {
         this.process5(this.target(mc, class_1309.class), floatTypeEvent2.getFloatType());
         if (!this.targetTransition.isEmpty()) {
            float f = this.getFloatType();
            class_1309 entity2 = (class_1309)this.targetTransition.current();
            if (!(f <= 0.0F) && entity2 != null) {
               SwordTargetEspRenderer.TargetGeometry geometry = this.captureGeometry(entity2, floatTypeEvent2.getFloatType());
               if (geometry != null) {
                  this.lastGeometry = geometry;
               } else {
                  geometry = this.lastGeometry;
               }

               if (geometry == null) {
                  this.update2();
               } else {
                  geometry = this.interpolatePreviousGeometry(geometry);
                  this.update3();
                  if (this.lazyMeshModel.isLoaded() && this.orbitModel != null) {
                     long l = System.currentTimeMillis();
                     float f2 = this.getFloatType2();
                     float f3 = 1.0F + AnimationMath.sin((float)l * 0.004F) * 0.025F;
                     double d = Math.max(geometry.width(), geometry.depth()) * 0.5 + 0.2;
                     double d2 = (double)l * 0.0021 % (Math.PI * 2);
                     double d3 = geometry.position().field_1351 + geometry.height() / 1.4 - 0.06F;
                     class_243 vec = this.cameraPosition();
                     int n2 = 0;
                     double d4 = Double.MAX_VALUE;

                     for(int n = 0; n < 7; ++n) {
                        double d5 = d2 + (Math.PI * 2) * (double)n / 7.0;
                        double d6 = Math.cos(d5);
                        double d7 = Math.sin(d5);
                        double d8 = d + (double)AnimationMath.sin((float)l * 0.003F + (float)n * 0.7F) * 0.025;
                        double d9 = geometry.position().field_1352 + d6 * d8;
                        double d10 = geometry.position().field_1350 + d7 * d8;
                        this.value13[n] = d9;
                        this.value16[n] = d10;
                        this.value12[n] = (float)Math.toDegrees(-Math.atan2(d6, -d7));
                        double d11 = vec.method_1028(d9, d3, d10);
                        if (d11 < d4) {
                           d4 = d11;
                           n2 = n;
                        }
                     }

                     this.modelRenderQueue.update();
                     boolean var42 = this.attackPulse.isActive(l, 300L);
                     float f4 = var42 ? this.attackPulse.progress(l, 300L) : 0.0F;
                     int n3 = this.primaryColor();
                     float f5 = 0.12F;
                     double d12 = 0.0;
                     double d13 = 0.0;
                     double d14 = 0.0;
                     float f6 = 0.0F;
                     float f7 = 0.0F;
                     boolean bl = false;

                     for(int i = 0; i < 7; ++i) {
                        float f8 = this.process4(i);
                        float f9 = f * f8;
                        float f10 = 0.12F * f2 * f3;
                        int n4 = this.process(this.primaryColor(), this.secondaryColor(), l, i);
                        float f11 = AnimationMath.sin((float)l * 0.005F + (float)i * 0.65F) * 0.032F;
                        float f12 = AnimationMath.sin((float)l * 0.004F + (float)i) * 2.5F;
                        if (var42 && i == n2) {
                           float f13 = f4 < 0.5F ? f4 / 0.5F : (f4 - 0.5F) / 0.5F;
                           float f14 = AnimationMath.easeInOutSine(f13);
                           f10 = f4 < 0.5F ? AnimationMath.lerp(f10, 0.17663999F, f14) : AnimationMath.lerp(0.17663999F, f10, f14);
                           n4 = f4 < 0.5F
                              ? AnimationMath.lerpColor(this.primaryColor(), -65476, f14)
                              : AnimationMath.lerpColor(-65476, this.primaryColor(), f14);
                           n3 = n4;
                           f5 = f10;
                           d12 = this.value13[i];
                           d13 = d3 + (double)f11;
                           d14 = this.value16[i];
                           f6 = this.value12[i];
                           f7 = f12;
                           bl = true;
                           this.matrix4f[i].set(this.matrix4f2);
                        } else {
                           this.process3(this.matrix4f[i], this.value13[i], d3 + (double)f11, this.value16[i], f10, -f10, f10, this.value12[i], -90.0F, f12);
                        }

                        n4 = AnimationMath.applyOpacity(n4, f9, 0.5F);
                     }

                     this.modelRenderQueue
                        .processModelInstances(
                           this.orbitModel,
                           Arrays.asList(this.matrix4f),
                           IDENTITY_TRANSFORM,
                           AnimationMath.applyOpacity(this.primaryColor(), f, 0.5F),
                           this.modelRenderOptions
                        );
                     if (var42 && bl) {
                        this.modelRenderQueue
                           .process7(
                              this.lazyMeshModel.getMeshModel(),
                              new SpatialTransform(d12, d13, d14, f5, -f5, f5, f6, -90.0F, f7),
                              AnimationMath.applyOpacity(n3, f, 0.5F),
                              this.modelRenderOptions
                           );
                     }

                     if (!this.modelRenderQueue.isActive()) {
                        MODEL_RENDERER.process10(floatTypeEvent2.getMatrices(), mc.method_22940().method_23000(), vec, this.modelRenderQueue);
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void update() {
      this.update2();
      this.modelRenderQueue.update();
   }

   private void process3(Matrix4f matrix4f, double d, double d2, double d3, float f, float f2, float f3, float f4, float f5, float f6) {
      matrix4f.identity()
         .translate((float)d, (float)d2, (float)d3)
         .rotateY((float)Math.toRadians((double)f4))
         .rotateX((float)Math.toRadians((double)f5))
         .rotateZ((float)Math.toRadians((double)f6))
         .scale(f, f2, f3);
   }

   private float process4(int n) {
      if (this.targetTransition.phase() != TransitionPhase.APPEARING) {
         return 1.0F;
      } else {
         float f = (float)n * 0.035F;
         float f2 = this.targetTransition.progress(380L);
         float f3 = Math.max(0.0F, Math.min(1.0F, (f2 - f) / Math.max(0.001F, 1.0F - f)));
         return AnimationMath.easeInOutSine(f3);
      }
   }

   private void process5(class_1309 entity2, float f) {
      TargetChange<class_1309> targetChange = this.targetTransition.updateTarget(entity2);
      if (targetChange.changed()) {
         SwordTargetEspRenderer.TargetGeometry oldGeometry = this.lastGeometry;
         SwordTargetEspRenderer.TargetGeometry newGeometry = this.captureGeometry((class_1309)this.targetTransition.current(), f);
         if (oldGeometry != null && newGeometry != null) {
            this.previousGeometry.remember(oldGeometry);
         }
      }
   }

   private float getFloatType() {
      if (this.targetTransition.phase() == TransitionPhase.IDLE) {
         return 1.0F;
      } else if (this.targetTransition.phase() == TransitionPhase.APPEARING) {
         float f = this.targetTransition.progress(380L);
         if (f >= 1.0F) {
            this.targetTransition.finishAppearance();
         }

         return AnimationMath.easeInOutSine(f);
      } else {
         float f = this.targetTransition.progress(240L);
         if (f >= 1.0F) {
            this.update2();
            return 0.0F;
         } else {
            return 1.0F - AnimationMath.smoothStep(f);
         }
      }
   }

   private SwordTargetEspRenderer.TargetGeometry interpolatePreviousGeometry(SwordTargetEspRenderer.TargetGeometry geometry) {
      if (!this.previousGeometry.hasValue()) {
         return geometry;
      } else {
         float f = this.previousGeometry.progress(380L);
         if (f >= 1.0F) {
            this.previousGeometry.clear();
            return geometry;
         } else {
            float f2 = AnimationMath.easeOut(f, 1.12F);
            SwordTargetEspRenderer.TargetGeometry previous = this.previousGeometry.get();
            class_243 start = previous.position();
            class_243 end = geometry.position();
            return new SwordTargetEspRenderer.TargetGeometry(
               new class_243(
                  (double)AnimationMath.lerp((float)start.field_1352, (float)end.field_1352, f2),
                  (double)AnimationMath.lerp((float)start.field_1351, (float)end.field_1351, f2),
                  (double)AnimationMath.lerp((float)start.field_1350, (float)end.field_1350, f2)
               ),
               (double)AnimationMath.lerp((float)previous.height(), (float)geometry.height(), f2),
               (double)AnimationMath.lerp((float)previous.width(), (float)geometry.width(), f2),
               (double)AnimationMath.lerp((float)previous.depth(), (float)geometry.depth(), f2)
            );
         }
      }
   }

   private void update2() {
      this.targetTransition.reset();
      this.attackPulse.reset();
      this.previousGeometry.reset();
      this.lastGeometry = null;
   }

   private void update3() {
      if (this.lazyMeshModel.isLoaded() && this.lazyMeshModel.getMeshModel() != null && !this.lazyMeshModel.getMeshModel().getList().isEmpty()) {
         if (this.orbitModel == null) {
            this.orbitModel = this.lazyMeshModel.getMeshModel();
         }
      }
   }

   private float getFloatType2() {
      if (this.targetTransition.phase() == TransitionPhase.IDLE) {
         return 1.0F;
      } else {
         return this.targetTransition.phase() == TransitionPhase.APPEARING
            ? AnimationMath.easeOut(this.targetTransition.progress(380L), 1.28F)
            : AnimationMath.lerp(1.0F, 0.78F, AnimationMath.smoothStep(this.targetTransition.progress(240L)));
      }
   }

   private static record TargetGeometry(class_243 position, double height, double width, double depth) {
   }
}
