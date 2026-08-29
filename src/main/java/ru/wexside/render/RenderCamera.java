package ru.wexside.render;

import net.minecraft.class_243;
import net.minecraft.class_310;

public final class RenderCamera {
   private RenderCamera() {
   }

   public static class_243 position() {
      class_310 client = class_310.method_1551();
      return client.field_1773.method_19418().method_71156();
   }
}
