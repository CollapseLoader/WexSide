package ru.wexside.misc;

import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_239;
import net.minecraft.class_310;
import net.minecraft.class_3966;

public final class CrosshairTarget {
   public class_1309 process(TargetFilter filter) {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null && mc.field_1687 != null) {
         class_239 crosshair = mc.field_1765;
         if (!(crosshair instanceof class_3966)) {
            return null;
         } else {
            class_3966 hit = (class_3966)crosshair;
            class_1297 entity = hit.method_17782();
            if (!(entity instanceof class_1309)) {
               return null;
            } else {
               class_1309 living = (class_1309)entity;
               return !filter.matches(living) ? null : living;
            }
         }
      } else {
         return null;
      }
   }
}
