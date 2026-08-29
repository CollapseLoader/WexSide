package ru.wexside.util;

import net.minecraft.class_12249;
import net.minecraft.class_1309;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597.class_4598;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import ru.wexside.animation.TimedPulse;
import ru.wexside.event.EntityAttackEvent;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.misc.PreviousValueTransition;
import ru.wexside.misc.TargetChange;
import ru.wexside.misc.TargetEspEffect;
import ru.wexside.misc.TargetTransition;
import ru.wexside.misc.TransitionPhase;
import ru.wexside.module.render.TargetEspRenderer;

public final class CylinderTargetEspRenderer extends TargetEspRenderer implements TargetEspEffect {
   static final float value4 = 1.0F;
   static final float value5 = 0.8F;
   static final float value6 = 1.0F;
   static final int slot = 360;
   private double value7;
   static final int slot2 = 360;
   static final float value8 = 4.4F;
   static final float value9 = 0.86F;
   static final long member8917 = 260L;
   static final long member12527 = 300L;
   private final PreviousValueTransition<CylinderTargetEspRenderer.TargetBounds> previousBounds;
   static final double value10 = 1.0E-9;
   static final float value11 = 1.16F;
   static final float value12 = 1.0F;
   private CylinderTargetEspRenderer.TargetBounds lastBounds;
   static final long member5632 = 420L;
   static final double value13 = 0.45;
   static final double value14 = 0.05;
   private final TargetTransition<class_1309> targetTransition2 = new TargetTransition();
   private double value15;
   static final float value16 = 0.01F;
   private long longType;
   static final float value17 = 1.12F;
   static final double value18 = 0.016666666666666666;
   private final TimedPulse attackPulse = new TimedPulse();
   static final int slot3 = -42663;

   public CylinderTargetEspRenderer() {
      this.previousBounds = new PreviousValueTransition<>();
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
         this.process5(this.target(mc, class_1309.class), floatTypeEvent2.getFloatType());
         if (!this.targetTransition2.isEmpty()) {
            float f = this.getTransitionOpacity();
            class_1309 entity2 = (class_1309)this.targetTransition2.current();
            if (!(f <= 0.0F) && entity2 != null) {
               CylinderTargetEspRenderer.TargetBounds bounds = this.captureBounds(entity2, floatTypeEvent2.getFloatType());
               if (bounds != null) {
                  this.lastBounds = bounds;
               } else {
                  bounds = this.lastBounds;
               }

               if (bounds == null) {
                  this.update2();
               } else {
                  this.update3();
                  this.renderRing(
                     floatTypeEvent2.getMatrices(),
                     mc.method_22940().method_23000(),
                     this.interpolatePreviousBounds(bounds),
                     floatTypeEvent2.getFloatType(),
                     this.getRadiusScale(),
                     f
                  );
               }
            }
         }
      }
   }

   @Override
   public void update() {
      this.update2();
   }

   private void process5(class_1309 entity2, float f) {
      TargetChange<class_1309> targetChange = this.targetTransition2.updateTarget(entity2);
      if (targetChange.changed()) {
         CylinderTargetEspRenderer.TargetBounds oldBounds = this.lastBounds;
         CylinderTargetEspRenderer.TargetBounds newBounds = this.captureBounds((class_1309)this.targetTransition2.current(), f);
         if (oldBounds != null && newBounds != null) {
            this.previousBounds.remember(oldBounds);
         }
      }
   }

   private void update2() {
      this.targetTransition2.reset();
      this.attackPulse.reset();
      this.previousBounds.reset();
      this.lastBounds = null;
      this.value7 = 0.0;
      this.value15 = 0.0;
      this.longType = 0L;
   }

   private int process(int n, int n2, float f) {
      return ColorUtils.lerp(this.process8(n, n2, f), -1, 0.2F);
   }

   private double process2(double d) {
      return Math.abs(1.0 + Math.sin(d)) * 0.5;
   }

   private float getRadiusScale() {
      if (this.targetTransition2.phase() == TransitionPhase.IDLE) {
         return 1.0F;
      } else {
         float f = this.targetTransition2.progress(300L);
         return this.targetTransition2.phase() == TransitionPhase.APPEARING
            ? AnimationMath.lerp(0.86F, 1.0F, AnimationMath.easeOut(f, 1.16F))
            : AnimationMath.lerp(1.0F, 0.86F, AnimationMath.smoothStep(f));
      }
   }

   private CylinderTargetEspRenderer.TargetBounds captureBounds(class_1309 entity2, float f) {
      if (entity2 != null && entity2.method_5805()) {
         class_238 box = entity2.method_5829();
         return new CylinderTargetEspRenderer.TargetBounds(
            this.interpolatedPosition(entity2, f), (double)entity2.method_17682(), box.field_1320 - box.field_1323, box.field_1324 - box.field_1321
         );
      } else {
         return null;
      }
   }

   private void renderRing(class_4587 matrices2, class_4598 iIiIliiliI2, CylinderTargetEspRenderer.TargetBounds bounds, float f, float f2, float f3) {
      matrices2.method_22903();
      class_243 camera = this.cameraPosition();
      matrices2.method_22904(
         bounds.position().field_1352 - camera.field_1352, bounds.position().field_1351 - camera.field_1351, bounds.position().field_1350 - camera.field_1350
      );
      Matrix4f matrix4f = matrices2.method_23760().method_23761();
      class_4588 iliIIiiliI2 = iIiIliiliI2.method_73477(class_12249.method_76023());
      long l = System.currentTimeMillis();
      float f8 = this.attackPulse.isActive(l, 260L) ? 1.0F - Math.abs(this.attackPulse.progress(l, 260L) * 2.0F - 1.0F) : 0.0F;
      double d = this.value15 + (this.value7 - this.value15) * (double)f;
      double d2 = this.process2(d - 0.45);
      double d3 = this.process2(d);
      float f9 = (float)(Math.max(bounds.width(), 0.1) * 0.8F) * f2;
      float f10 = (float)(d2 * bounds.height());
      float f11 = (float)(d3 * bounds.height());
      int n = Math.max(0, Math.min(255, Math.round(f3 * 1.0F * 255.0F)));
      int n2 = Math.max(0, Math.min(255, Math.round(f3 * 0.01F * 255.0F)));
      int n3 = Math.max(0, Math.min(255, Math.round(f3 * 255.0F)));

      for(int i = 0; i < 360; ++i) {
         float f12 = (float)((Math.PI * 2) * (double)i / 360.0);
         float f13 = (float)((Math.PI * 2) * (double)(i + 1) / 360.0);
         float f7 = (float)Math.cos((double)f12) * f9;
         float f6 = (float)Math.sin((double)f12) * f9;
         float f5 = (float)Math.cos((double)f13) * f9;
         float f4 = (float)Math.sin((double)f13) * f9;
         int n4 = this.process7(this.process8(i, 360, 4.4F), f8);
         int n5 = this.process7(this.process8(i + 1, 360, 4.4F), f8);
         iliIIiiliI2.method_22918(matrix4f, f7, f11, f6).method_39415(ColorUtils.withAlpha(n4, (float)n));
         iliIIiiliI2.method_22918(matrix4f, f7, f10, f6).method_39415(ColorUtils.withAlpha(n4, (float)n2));
         iliIIiiliI2.method_22918(matrix4f, f5, f10, f4).method_39415(ColorUtils.withAlpha(n5, (float)n2));
         iliIIiiliI2.method_22918(matrix4f, f5, f11, f4).method_39415(ColorUtils.withAlpha(n5, (float)n));
      }

      class_4588 iliIIiiliI3 = iIiIliiliI2.method_73477(class_12249.method_76668());
      Vector3f vector3f = new Vector3f(0.0F, 1.0F, 0.0F);

      for(int i = 0; i < 360; ++i) {
         float f7 = (float)((Math.PI * 2) * (double)i / 360.0);
         float f6 = (float)((Math.PI * 2) * (double)(i + 1) / 360.0);
         float f5 = (float)Math.cos((double)f7) * f9;
         float f4 = (float)Math.sin((double)f7) * f9;
         float f14 = (float)Math.cos((double)f6) * f9;
         float f15 = (float)Math.sin((double)f6) * f9;
         int n6 = this.process7(this.process(i, 360, 4.4F), f8);
         int n7 = this.process7(this.process(i + 1, 360, 4.4F), f8);
         iliIIiiliI3.method_22918(matrix4f, f5, f11, f4)
            .method_39415(ColorUtils.withAlpha(n6, (float)n3))
            .method_61959(matrices2.method_23760(), vector3f)
            .method_75298(1.0F);
         iliIIiiliI3.method_22918(matrix4f, f14, f11, f15)
            .method_39415(ColorUtils.withAlpha(n7, (float)n3))
            .method_61959(matrices2.method_23760(), vector3f)
            .method_75298(1.0F);
      }

      iIiIliiliI2.method_22993();
      matrices2.method_22909();
   }

   private CylinderTargetEspRenderer.TargetBounds interpolatePreviousBounds(CylinderTargetEspRenderer.TargetBounds bounds) {
      if (!this.previousBounds.hasValue()) {
         return bounds;
      } else {
         float f = this.previousBounds.progress(420L);
         if (f >= 1.0F) {
            this.previousBounds.clear();
            return bounds;
         } else {
            float f2 = AnimationMath.easeOut(f, 1.12F);
            CylinderTargetEspRenderer.TargetBounds previous = this.previousBounds.get();
            class_243 from = previous.position();
            class_243 to = bounds.position();
            return new CylinderTargetEspRenderer.TargetBounds(
               new class_243(
                  (double)AnimationMath.lerp((float)from.field_1352, (float)to.field_1352, f2),
                  (double)AnimationMath.lerp((float)from.field_1351, (float)to.field_1351, f2),
                  (double)AnimationMath.lerp((float)from.field_1350, (float)to.field_1350, f2)
               ),
               (double)AnimationMath.lerp((float)previous.height(), (float)bounds.height(), f2),
               (double)AnimationMath.lerp((float)previous.width(), (float)bounds.width(), f2),
               (double)AnimationMath.lerp((float)previous.depth(), (float)bounds.depth(), f2)
            );
         }
      }
   }

   private void update3() {
      long l = System.nanoTime();
      double d;
      if (this.longType == 0L) {
         d = 0.016666666666666666;
      } else {
         d = Math.min((double)(l - this.longType) * 1.0E-9, 0.05);
         if (!Double.isFinite(d) || d < 0.0) {
            d = 0.016666666666666666;
         }
      }

      this.longType = l;
      this.value15 = this.value7;
      this.value7 += d * 4.4F;
   }

   private int process7(int n, float f) {
      return f <= 0.0F ? n : ColorUtils.lerp(n, -42663, (double)f);
   }

   private float getTransitionOpacity() {
      if (this.targetTransition2.phase() == TransitionPhase.IDLE) {
         return 1.0F;
      } else {
         float f = this.targetTransition2.progress(300L);
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

   private int process8(int n, int n2, float f) {
      long l = System.currentTimeMillis();
      float f2 = (float)l / (1000.0F / f) % (float)n2;
      int n3 = (int)(((float)n + f2) % (float)n2);
      float f3 = (float)n3 / (float)n2;
      return f3 < 0.5F
         ? ColorUtils.lerp(this.primaryColor(), this.secondaryColor(), (double)(f3 * 2.0F))
         : ColorUtils.lerp(this.secondaryColor(), this.primaryColor(), (double)((f3 - 0.5F) * 2.0F));
   }

   private static record TargetBounds(class_243 position, double height, double width, double depth) {
   }
}
