package ru.wexside.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import net.minecraft.class_1309;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public final class AimCalculator {
   private static final double HITBOX_MARGIN = 0.05;
   private static final double JITTER_DECAY = 0.85;
   private static final double JITTER_STRENGTH = 0.04;
   private static final double MAX_JITTER = 0.18;
   private static final double EPSILON = 1.0E-7;
   private static final int RANGE_SEARCH_STEPS = 8;
   private final Random random = new Random();
   private class_243 jitter = class_243.field_1353;

   public class_243 calculateAimPoint(class_1309 target, Angle look, float attackRange, float searchRange, boolean throughWalls) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null && target != null && look != null) {
         class_243 eye = player.method_33571();
         class_238 hitbox = shrinkHitbox(target.method_5829());
         if (hitbox.method_1006(eye)) {
            return eye.method_1019(look.toVec3d().method_1021(0.5));
         } else {
            class_243 closest = closestPoint(hitbox, eye);
            double closestDistance = eye.method_1022(closest);
            if (closestDistance > (double)searchRange) {
               return null;
            } else {
               Angle boundedLook = clampLookToHitbox(eye, hitbox, look);
               class_243 aimPoint = (class_243)raycastHitbox(hitbox, eye, boundedLook, searchRange).orElse(closest);
               boolean targetWithinAttackRange = closestDistance <= (double)attackRange;
               if (targetWithinAttackRange && eye.method_1022(aimPoint) > (double)attackRange) {
                  aimPoint = findPointWithinAttackRange(hitbox, eye, look, closest, attackRange, searchRange);
               }

               aimPoint = this.applyJitter(hitbox, eye, aimPoint, attackRange, searchRange, targetWithinAttackRange);
               return !throughWalls && !isVisible(player, eye, aimPoint) && isVisible(player, eye, closest) ? closest : aimPoint;
            }
         }
      } else {
         return null;
      }
   }

   private class_243 applyJitter(class_238 hitbox, class_243 eye, class_243 aimPoint, float attackRange, float searchRange, boolean targetWithinAttackRange) {
      this.jitter = this.jitter
         .method_1021(0.85)
         .method_1031(this.random.nextGaussian() * 0.04, this.random.nextGaussian() * 0.04, this.random.nextGaussian() * 0.04);
      this.jitter = limitLength(this.jitter, 0.18);
      class_243 jittered = closestPoint(hitbox, aimPoint.method_1019(this.jitter));
      if (!targetWithinAttackRange) {
         return jittered;
      } else {
         Optional<class_243> intersection = raycastHitbox(hitbox, eye, Angle.fromVectors(eye, jittered), searchRange);
         return intersection.isPresent() && eye.method_1022((class_243)intersection.get()) <= (double)attackRange ? jittered : aimPoint;
      }
   }

   private static class_243 findPointWithinAttackRange(
      class_238 hitbox, class_243 eye, Angle currentLook, class_243 closest, float attackRange, float searchRange
   ) {
      Angle closestAngle = Angle.fromVectors(eye, closest);
      float lower = 0.0F;
      float upper = 1.0F;

      for(int step = 0; step < 8; ++step) {
         float factor = (lower + upper) * 0.5F;
         Optional<class_243> intersection = raycastHitbox(hitbox, eye, interpolate(currentLook, closestAngle, factor), searchRange);
         if (intersection.isPresent() && eye.method_1022((class_243)intersection.get()) <= (double)attackRange) {
            upper = factor;
         } else {
            lower = factor;
         }
      }

      return (class_243)raycastHitbox(hitbox, eye, interpolate(currentLook, closestAngle, upper), searchRange).orElse(closest);
   }

   private static Angle clampLookToHitbox(class_243 eye, class_238 hitbox, Angle look) {
      float yaw = clampYawToHitbox(eye, hitbox, look.getYaw());
      float pitch = clampPitchToHitbox(eye, hitbox, yaw, look.getPitch());
      return new Angle(yaw, pitch);
   }

   private static float clampYawToHitbox(class_243 eye, class_238 hitbox, float yaw) {
      if (eye.field_1352 >= hitbox.field_1323
         && eye.field_1352 <= hitbox.field_1320
         && eye.field_1350 >= hitbox.field_1321
         && eye.field_1350 <= hitbox.field_1324) {
         return yaw;
      } else {
         float[] corners = new float[]{
            yawTo(eye, hitbox.field_1323, hitbox.field_1321),
            yawTo(eye, hitbox.field_1323, hitbox.field_1324),
            yawTo(eye, hitbox.field_1320, hitbox.field_1321),
            yawTo(eye, hitbox.field_1320, hitbox.field_1324)
         };
         Arrays.sort(corners);
         int arcStartIndex = 0;
         float largestGap = corners[0] + 360.0F - corners[3];

         for(int index = 1; index < corners.length; ++index) {
            float gap = corners[index] - corners[index - 1];
            if (gap > largestGap) {
               largestGap = gap;
               arcStartIndex = index;
            }
         }

         float arcStart = corners[arcStartIndex];
         float arcEnd = corners[(arcStartIndex + corners.length - 1) % corners.length];
         float arcLength = positiveDegrees(arcEnd - arcStart);
         float position = positiveDegrees(yaw - arcStart);
         if (position <= arcLength) {
            return yaw;
         } else {
            return position - arcLength <= 360.0F - position ? arcEnd : arcStart;
         }
      }
   }

   private static float clampPitchToHitbox(class_243 eye, class_238 hitbox, float yaw, float pitch) {
      class_243 horizontalDirection = new Angle(yaw, 0.0F).toVec3d();
      double[] xRange = slabRange(eye.field_1352, horizontalDirection.field_1352, hitbox.field_1323, hitbox.field_1320);
      double[] zRange = slabRange(eye.field_1350, horizontalDirection.field_1350, hitbox.field_1321, hitbox.field_1324);
      if (xRange != null && zRange != null) {
         double entry = Math.max(0.0, Math.max(xRange[0], zRange[0]));
         double exit = Math.min(xRange[1], zRange[1]);
         if (exit < entry) {
            return pitch;
         } else {
            double minY = hitbox.field_1322 - eye.field_1351;
            double maxY = hitbox.field_1325 - eye.field_1351;
            float minPitch = Float.MAX_VALUE;
            float maxPitch = -Float.MAX_VALUE;

            for(double distance : new double[]{entry, exit}) {
               for(double height : new double[]{minY, maxY}) {
                  float candidate = (float)(-Math.toDegrees(Math.atan2(height, distance)));
                  minPitch = Math.min(minPitch, candidate);
                  maxPitch = Math.max(maxPitch, candidate);
               }
            }

            return class_3532.method_15363(pitch, minPitch, maxPitch);
         }
      } else {
         return pitch;
      }
   }

   private static double[] slabRange(double origin, double direction, double min, double max) {
      if (!(Math.abs(direction) < 1.0E-7)) {
         double first = (min - origin) / direction;
         double second = (max - origin) / direction;
         return new double[]{Math.min(first, second), Math.max(first, second)};
      } else {
         return origin >= min && origin <= max ? new double[]{0.0, Double.MAX_VALUE} : null;
      }
   }

   private static Optional<class_243> raycastHitbox(class_238 hitbox, class_243 eye, Angle angle, float distance) {
      return hitbox.method_992(eye, eye.method_1019(angle.toVec3d().method_1021((double)distance)));
   }

   private static Angle interpolate(Angle from, Angle to, float factor) {
      float yaw = from.getYaw() + class_3532.method_15393(to.getYaw() - from.getYaw()) * factor;
      float pitch = from.getPitch() + (to.getPitch() - from.getPitch()) * factor;
      return new Angle(class_3532.method_15393(yaw), pitch);
   }

   private static float yawTo(class_243 eye, double x, double z) {
      return class_3532.method_15393((float)(Math.toDegrees(Math.atan2(z - eye.field_1350, x - eye.field_1352)) - 90.0));
   }

   private static float positiveDegrees(float angle) {
      float normalized = angle % 360.0F;
      return normalized < 0.0F ? normalized + 360.0F : normalized;
   }

   private static class_243 limitLength(class_243 vector, double maximum) {
      double length = vector.method_1033();
      return length <= maximum ? vector : vector.method_1021(maximum / length);
   }

   private static class_243 closestPoint(class_238 box, class_243 point) {
      return new class_243(
         class_3532.method_15350(point.field_1352, box.field_1323, box.field_1320),
         class_3532.method_15350(point.field_1351, box.field_1322, box.field_1325),
         class_3532.method_15350(point.field_1350, box.field_1321, box.field_1324)
      );
   }

   private static boolean isVisible(class_746 player, class_243 start, class_243 end) {
      class_638 world = class_310.method_1551().field_1687;
      if (world == null) {
         return false;
      } else {
         class_3959 context = new class_3959(start, end, class_3960.field_17558, class_242.field_1348, player);
         return world.method_17742(context).method_17783() == class_240.field_1333;
      }
   }

   private static class_238 shrinkHitbox(class_238 box) {
      double x = Math.min(0.05, box.method_17939() / 4.0);
      double y = Math.min(0.05, box.method_17940() / 4.0);
      double z = Math.min(0.05, box.method_17941() / 4.0);
      return box.method_1009(-x, -y, -z);
   }
}
