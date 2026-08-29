package ru.wexside.util;

import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_310;
import ru.wexside.animation.TimedPulse;
import ru.wexside.event.EntityAttackEvent;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.misc.LazyMeshModel;
import ru.wexside.misc.PreviousValueTransition;
import ru.wexside.misc.SpatialTransform;
import ru.wexside.misc.TargetChange;
import ru.wexside.misc.TargetEspEffect;
import ru.wexside.misc.TargetTransition;
import ru.wexside.misc.TransitionPhase;
import ru.wexside.module.render.TargetEspRenderer;
import ru.wexside.render.model.BuiltInMesh;

public final class MarkerTargetEspRenderer extends TargetEspRenderer implements TargetEspEffect {
   private final LazyMeshModel lazyMeshModel = LazyMeshModel.create(BuiltInMesh.MARKER);
   static final long member5632 = 420L;
   private final PreviousValueTransition<MarkerTargetEspRenderer.TargetMarkerState> previousState;
   static final float value4 = 0.4F;
   private static final WorldMeshBatchRenderer MODEL_RENDERER = new WorldMeshBatchRenderer("target-marker");
   static final float value5 = 1.5F;
   static final float value6 = 0.42F;
   static final float value7 = 0.25F;
   private final ModelRenderOptions modelRenderOptions;
   private final TargetTransition<class_1297> targetTransition;
   private MarkerTargetEspRenderer.TargetMarkerState lastState;
   static final long member8917 = 320L;
   static final long member12527 = 300L;
   static final int slot = -65476;
   static final float value8 = 1.28F;
   private final ModelRenderQueue modelRenderQueue = new ModelRenderQueue();
   static final float value9 = 0.5F;
   private final TimedPulse attackPulse;
   static final float value10 = 1.16F;

   public MarkerTargetEspRenderer() {
      this.modelRenderOptions = ModelRenderOptions.process4(true).process10(false);
      this.targetTransition = new TargetTransition();
      this.attackPulse = new TimedPulse();
      this.previousState = new PreviousValueTransition<>();
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
         if (this.lazyMeshModel.isLoaded() && this.lazyMeshModel.getMeshModel() != null && !this.lazyMeshModel.getMeshModel().getList().isEmpty()) {
            this.process2(this.target(mc, class_1297.class), floatTypeEvent2.getFloatType());
            if (!this.targetTransition.isEmpty()) {
               float f = this.getTransitionOpacity();
               class_1297 target = (class_1297)this.targetTransition.current();
               if (!(f <= 0.0F) && target != null) {
                  MarkerTargetEspRenderer.TargetMarkerState state = this.captureState(target, floatTypeEvent2.getFloatType());
                  if (state != null) {
                     this.lastState = state;
                  } else {
                     state = this.lastState;
                  }

                  if (state == null) {
                     this.update2();
                  } else {
                     this.modelRenderQueue.update();
                     this.renderMarker(this.interpolatePreviousState(state), this.getMarkerScale(), f);
                     if (!this.modelRenderQueue.isActive()) {
                        MODEL_RENDERER.process10(floatTypeEvent2.getMatrices(), mc.method_22940().method_23000(), this.cameraPosition(), this.modelRenderQueue);
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

   private void update2() {
      this.targetTransition.reset();
      this.attackPulse.reset();
      this.previousState.reset();
      this.lastState = null;
   }

   private void renderMarker(MarkerTargetEspRenderer.TargetMarkerState state, float f, float f2) {
      long l = System.currentTimeMillis();
      class_243 vec = this.cameraPosition();
      double d = state.position().field_1352;
      double d2 = state.position().field_1351 + state.height() * 0.65;
      double d3 = state.position().field_1350;
      double d4 = vec.field_1352 - d;
      double d5 = vec.field_1351 - d2;
      double d6 = vec.field_1350 - d3;
      float f4 = (float)Math.toDegrees(Math.atan2(d4, d6));
      float f5 = (float)Math.toDegrees(-Math.atan2(d5, Math.sqrt(d4 * d4 + d6 * d6)));
      float f6 = 1.0F + AnimationMath.sin((float)l * 0.004F) * 0.035F;
      float f7 = AnimationMath.sin((float)l * 0.005F) * 0.035F;
      f *= f6;
      int n = this.process3(this.primaryColor(), this.secondaryColor(), l);
      if (this.attackPulse.isActive(l, 320L)) {
         float f3 = this.attackPulse.progress(l, 320L);
         float f8 = f3 < 0.5F ? f3 / 0.5F : (f3 - 0.5F) / 0.5F;
         float f9 = AnimationMath.easeInOutSine(f8);
         f = f3 < 0.5F ? AnimationMath.lerp(f, 0.4F, f9) : AnimationMath.lerp(0.4F, f, f9);
         n = f3 < 0.5F ? AnimationMath.lerpColor(n, -65476, f9) : AnimationMath.lerpColor(-65476, n, f9);
      }

      float f3 = (float)Math.sin((double)System.currentTimeMillis() / 666.6666666666666) * 360.0F;
      this.modelRenderQueue
         .process7(
            this.lazyMeshModel.getMeshModel(),
            new SpatialTransform(d, d2 + (double)f7, d3, f, f, f, f4, f5, f3),
            AnimationMath.applyOpacity(n, f2, 0.5F),
            this.modelRenderOptions
         );
   }

   private void process2(class_1297 entity2, float f) {
      TargetChange<class_1297> targetChange = this.targetTransition.updateTarget(entity2);
      if (targetChange.changed()) {
         MarkerTargetEspRenderer.TargetMarkerState oldState = this.lastState;
         MarkerTargetEspRenderer.TargetMarkerState newState = this.captureState((class_1297)this.targetTransition.current(), f);
         if (oldState != null && newState != null) {
            this.previousState.remember(oldState);
         }
      }
   }

   private int process3(int n, int n2, long l) {
      float f = 0.5F + 0.5F * AnimationMath.sin((float)l * 0.004F);
      return AnimationMath.lerpColor(n, n2, f * 0.45F);
   }

   private MarkerTargetEspRenderer.TargetMarkerState captureState(class_1297 entity2, float f) {
      return entity2 != null && entity2.method_5805()
         ? new MarkerTargetEspRenderer.TargetMarkerState(this.interpolatedPosition(entity2, f), (double)entity2.method_17682())
         : null;
   }

   private MarkerTargetEspRenderer.TargetMarkerState interpolatePreviousState(MarkerTargetEspRenderer.TargetMarkerState state) {
      if (!this.previousState.hasValue()) {
         return state;
      } else {
         float f = this.previousState.progress(420L);
         if (f >= 1.0F) {
            this.previousState.clear();
            return state;
         } else {
            float f2 = AnimationMath.easeOut(f, 1.16F);
            MarkerTargetEspRenderer.TargetMarkerState previous = this.previousState.get();
            class_243 start = previous.position();
            class_243 end = state.position();
            return new MarkerTargetEspRenderer.TargetMarkerState(
               new class_243(
                  (double)AnimationMath.lerp((float)start.field_1352, (float)end.field_1352, f2),
                  (double)AnimationMath.lerp((float)start.field_1351, (float)end.field_1351, f2),
                  (double)AnimationMath.lerp((float)start.field_1350, (float)end.field_1350, f2)
               ),
               (double)AnimationMath.lerp((float)previous.height(), (float)state.height(), f2)
            );
         }
      }
   }

   private float getMarkerScale() {
      if (this.targetTransition.phase() == TransitionPhase.IDLE) {
         return 0.25F;
      } else {
         float f = this.targetTransition.progress(300L);
         return this.targetTransition.phase() == TransitionPhase.APPEARING
            ? AnimationMath.lerp(0.42F, 0.25F, AnimationMath.easeOut(f, 1.28F))
            : AnimationMath.lerp(0.25F, 0.42F, AnimationMath.smoothStep(f));
      }
   }

   private float getTransitionOpacity() {
      if (this.targetTransition.phase() == TransitionPhase.IDLE) {
         return 1.0F;
      } else {
         float f = this.targetTransition.progress(300L);
         if (this.targetTransition.phase() == TransitionPhase.APPEARING) {
            if (f >= 1.0F) {
               this.targetTransition.finishAppearance();
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

   private static record TargetMarkerState(class_243 position, double height) {
   }
}
