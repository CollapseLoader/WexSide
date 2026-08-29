package ru.wexside.util;

import java.util.Comparator;
import net.minecraft.class_1297;
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
import ru.wexside.misc.TargetFilter;

public final class TargetSelector {
   private class_1309 selectedTarget;

   public class_1309 findTarget(TargetFilter filter, float range, String sorting, boolean throughWalls) {
      return this.process(filter, range, sorting, throughWalls);
   }

   public class_1309 process(TargetFilter filter, float range, String sorting, boolean throughWalls) {
      class_310 client = class_310.method_1551();
      class_746 player = client.field_1724;
      class_638 world = client.field_1687;
      if (player != null && world != null) {
         class_243 eyePosition = player.method_33571();
         if (this.isValid(this.selectedTarget, player, filter, eyePosition, range)) {
            return this.selectedTarget;
         } else {
            Comparator<class_1309> comparator = createComparator(player, eyePosition, sorting);
            class_1309 bestVisible = null;
            class_1309 bestOverall = null;

            for(class_1297 candidate : world.method_18112()) {
               if (candidate instanceof class_1309 living && this.isValid(living, player, filter, eyePosition, range)) {
                  if (bestOverall == null || comparator.compare(living, bestOverall) < 0) {
                     bestOverall = living;
                  }

                  if (!throughWalls
                     && hasLineOfSight(world, player, eyePosition, living)
                     && (bestVisible == null || comparator.compare(living, bestVisible) < 0)) {
                     bestVisible = living;
                  }
               }
            }

            this.selectedTarget = bestVisible != null ? bestVisible : bestOverall;
            return this.selectedTarget;
         }
      } else {
         this.selectedTarget = null;
         return null;
      }
   }

   public class_1309 getLivingEntity() {
      return this.selectedTarget;
   }

   public void reset() {
      this.selectedTarget = null;
   }

   public void update() {
      this.reset();
   }

   public static double distanceTo(class_1309 entity) {
      class_746 player = class_310.method_1551().field_1724;
      return player == null ? Double.MAX_VALUE : distanceToBounds(player.method_33571(), entity);
   }

   public static double process5(class_1309 entity) {
      return distanceTo(entity);
   }

   private boolean isValid(class_1309 entity, class_746 player, TargetFilter filter, class_243 origin, float range) {
      return entity != null && entity != player && entity.method_5805() && filter.matches(entity) && distanceToBounds(origin, entity) <= (double)range;
   }

   private static boolean hasLineOfSight(class_638 world, class_746 player, class_243 origin, class_1309 target) {
      class_243 destination = closestPoint(origin, target.method_5829());
      class_3959 context = new class_3959(origin, destination, class_3960.field_17558, class_242.field_1348, player);
      return world.method_17742(context).method_17783() == class_240.field_1333;
   }

   private static Comparator<class_1309> createComparator(class_746 player, class_243 origin, String sorting) {
      Comparator<class_1309> distance = Comparator.comparingDouble(entity -> distanceToBounds(origin, entity));

      return switch(sorting) {
         case "Health" -> Comparator.comparingDouble(class_1309::method_6032).thenComparing(distance);
         case "Crosshair" -> Comparator.comparingDouble((class_1309 entity) -> crosshairDeviation(player, origin, entity)).thenComparing(distance);
         default -> distance.thenComparingDouble(class_1309::method_6032);
      };
   }

   private static double crosshairDeviation(class_746 player, class_243 origin, class_1309 target) {
      class_243 direction = closestPoint(origin, target.method_5829()).method_1020(origin);
      return direction.method_1027() < 1.0E-7 ? -1.0 : 1.0 - player.method_5828(1.0F).method_1029().method_1026(direction.method_1029());
   }

   private static double distanceToBounds(class_243 origin, class_1309 entity) {
      return origin.method_1022(closestPoint(origin, entity.method_5829()));
   }

   private static class_243 closestPoint(class_243 point, class_238 box) {
      return new class_243(
         class_3532.method_15350(point.field_1352, box.field_1323, box.field_1320),
         class_3532.method_15350(point.field_1351, box.field_1322, box.field_1325),
         class_3532.method_15350(point.field_1350, box.field_1321, box.field_1324)
      );
   }
}
