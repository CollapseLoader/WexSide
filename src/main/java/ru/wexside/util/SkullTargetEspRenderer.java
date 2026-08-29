package ru.wexside.util;

import net.minecraft.class_1309;
import net.minecraft.class_243;
import net.minecraft.class_310;
import ru.wexside.animation.TimedPulse;
import ru.wexside.event.EntityAttackEvent;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.misc.LazyMeshModel;
import ru.wexside.misc.SpatialTransform;
import ru.wexside.misc.TargetEspEffect;
import ru.wexside.misc.TargetTransition;
import ru.wexside.misc.TransitionPhase;
import ru.wexside.module.render.TargetEspRenderer;
import ru.wexside.render.model.BuiltInMesh;

public final class SkullTargetEspRenderer extends TargetEspRenderer implements TargetEspEffect {
   private final LazyMeshModel lazyMeshModel = LazyMeshModel.create(BuiltInMesh.SKULL);
   static final long member12318 = 240L;
   static final float value = 0.16F;
   private static final WorldMeshBatchRenderer MODEL_RENDERER = new WorldMeshBatchRenderer("target-skull");
   private final TimedPulse attackPulse;
   static final long member12527 = 320L;
   static final long member6565 = 200L;
   static final float value2 = 0.23F;
   static final int slot = -65536;
   static final float value4 = 0.34F;
   static final float value5 = 0.14F;
   static final boolean flag = true;
   static final float value6 = 1.3F;
   private final ModelRenderQueue modelRenderQueue = new ModelRenderQueue();
   static final float value7 = 0.65F;
   private final TargetTransition<class_1309> targetTransition2;
   private final ModelRenderOptions modelRenderOptions = ModelRenderOptions.process4(true).process10(false);
   static final float value8 = 0.5F;

   public SkullTargetEspRenderer() {
      this.targetTransition2 = new TargetTransition();
      this.attackPulse = new TimedPulse();
   }

   @Override
   public void setEntityAttackEvent(EntityAttackEvent iIiiiilIiIEvent) {
      if (iIiiiilIiIEvent.getEntity() instanceof class_1309) {
         this.attackPulse.trigger();
      }
   }

   @Override
   public void setWorldRenderEvent(WorldRenderEvent floatTypeEvent2) {
      class_310 mc = this.client();
      if (this.canRender(mc)) {
         this.targetTransition2.updateTarget((class_1309)this.target(mc, class_1309.class));
         if (!this.targetTransition2.isEmpty()) {
            float f = this.getTransitionOpacity();
            class_1309 entity2 = (class_1309)this.targetTransition2.current();
            if (!(f <= 0.0F) && entity2 != null) {
               if (this.lazyMeshModel.isLoaded() && this.lazyMeshModel.getMeshModel() != null && !this.lazyMeshModel.getMeshModel().getList().isEmpty()) {
                  this.modelRenderQueue.update();
                  this.process2(entity2, floatTypeEvent2.getFloatType(), this.getModelScale(), f);
                  if (!this.modelRenderQueue.isActive()) {
                     MODEL_RENDERER.process10(floatTypeEvent2.getMatrices(), mc.method_22940().method_23000(), this.cameraPosition(), this.modelRenderQueue);
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

   private void update2() {
      this.targetTransition2.reset();
      this.attackPulse.reset();
   }

   private int process3(int n, int n2, long l) {
      float f = 0.5F + 0.5F * AnimationMath.sin((float)l * 0.004F);
      return AnimationMath.lerpColor(n, n2, f * 0.36F);
   }

   private float getModelScale() {
      if (this.targetTransition2.phase() == TransitionPhase.IDLE) {
         return 0.23F;
      } else {
         float f = this.targetTransition2.progress(320L);
         return this.targetTransition2.phase() == TransitionPhase.APPEARING
            ? AnimationMath.lerp(0.34F, 0.23F, AnimationMath.easeOut(f, 1.3F))
            : AnimationMath.lerp(0.23F, 0.34F, AnimationMath.smoothStep(f));
      }
   }

   private float getTransitionOpacity() {
      if (this.targetTransition2.phase() == TransitionPhase.IDLE) {
         return 1.0F;
      } else {
         float f = this.targetTransition2.progress(320L);
         if (this.targetTransition2.phase() == TransitionPhase.APPEARING) {
            if (f >= 1.0F) {
               this.targetTransition2.finishAppearance();
            }

            return AnimationMath.easeInOutSine(f);
         } else if (f >= 1.0F) {
            this.update2();
            return 0.0F;
         } else {
            return 1.0F - AnimationMath.smoothStep(f);
         }
      }
   }

   private float process(long l) {
      long l2 = this.attackPulse.elapsed(l);
      if (l2 < 240L) {
         float f = (float)l2 / 240.0F;
         float f2 = (float)Math.sin((double)f * Math.PI) * (1.0F - f);
         return -f2 * 0.16F;
      } else {
         return 0.0F;
      }
   }

   private void process2(class_1309 entity2, float f, float f2, float f3) {
      long l = System.currentTimeMillis();
      class_243 vec = this.interpolatedPosition(entity2, f);
      class_243 vec2 = this.cameraPosition();
      double d = vec.field_1352;
      double d2 = vec.field_1351 + (double)entity2.method_17682() + 0.25;
      double d3 = vec.field_1350;
      float f4 = AnimationMath.sin((float)l * 0.0045F) * 0.045F;
      float f5 = this.process(l);
      class_243 vec3 = this.process4(l);
      class_243 vec4 = new class_243(d + vec3.field_1352, d2 + (double)f4 + (double)f5, d3 + vec3.field_1350);
      double d4 = vec2.field_1352 - vec4.field_1352;
      double d5 = vec2.field_1351 - vec4.field_1351;
      double d6 = vec2.field_1350 - vec4.field_1350;
      float f6 = (float)Math.toDegrees(Math.atan2(d4, d6));
      float f7 = (float)Math.toDegrees(-Math.atan2(d5, Math.sqrt(d4 * d4 + d6 * d6)));
      float f8 = 1.0F + AnimationMath.sin((float)l * 0.004F) * 0.035F;
      float f9 = f2 * f8;
      int n = this.process3(this.primaryColor(), this.secondaryColor(), l);
      if (this.attackPulse.isActive(l, 200L)) {
         float f10 = this.attackPulse.progress(l, 200L);
         n = f10 < 0.5F
            ? AnimationMath.lerpColor(this.primaryColor(), -65536, f10 / 0.5F)
            : AnimationMath.lerpColor(-65536, this.primaryColor(), (f10 - 0.5F) / 0.5F);
      }

      this.modelRenderQueue
         .process7(
            this.lazyMeshModel.getMeshModel(),
            new SpatialTransform(vec4.field_1352, vec4.field_1351, vec4.field_1350, f9, f9, f9, f6, f7 + 90.0F, 0.0F),
            AnimationMath.applyOpacity(n, f3, 0.5F),
            this.modelRenderOptions
         );
   }

   private class_243 process4(long l) {
      long l2 = this.attackPulse.elapsed(l);
      if (l2 < 200L) {
         float f = (float)l2 / 200.0F;
         float f2 = (float)Math.sin((double)f * Math.PI * 8.0) * (1.0F - f) * 0.14F;
         return new class_243((double)f2, 0.0, (double)(f2 * 0.65F));
      } else {
         return class_243.field_1353;
      }
   }
}
