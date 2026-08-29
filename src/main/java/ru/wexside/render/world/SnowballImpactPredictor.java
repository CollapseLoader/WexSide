package ru.wexside.render.world;

import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1680;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3959;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public final class SnowballImpactPredictor {
   private static final int MAX_TICKS = 120;
   private static final double DRAG = 0.99;
   private static final double GRAVITY = 0.03;

   private SnowballImpactPredictor() {
   }

   public static class_243 predict(class_1680 snowball) {
      return trace(snowball.method_73189(), snowball.method_18798(), snowball);
   }

   public static class_243 predict(class_1657 player) {
      class_243 start = player.method_33571().method_1019(player.method_5828(1.0F).method_1021(0.25));
      class_243 velocity = player.method_5828(1.0F).method_1029().method_1021(1.5).method_1019(player.method_18798());
      return trace(start, velocity, player);
   }

   private static class_243 trace(class_243 start, class_243 initialVelocity, class_1297 source) {
      class_310 client = class_310.method_1551();
      if (client.field_1687 == null) {
         return null;
      } else {
         class_243 position = start;
         class_243 velocity = initialVelocity;

         for(int tick = 0; tick < 120; ++tick) {
            class_243 next = position.method_1019(velocity);
            class_239 hit = client.field_1687.method_17742(new class_3959(position, next, class_3960.field_17558, class_242.field_1348, source));
            if (hit.method_17783() != class_240.field_1333) {
               return hit.method_17784();
            }

            position = next;
            velocity = velocity.method_1021(0.99).method_1031(0.0, -0.03, 0.0);
            if (next.field_1351 < (double)(client.field_1687.method_31607() - 8)) {
               break;
            }
         }

         return null;
      }
   }
}
