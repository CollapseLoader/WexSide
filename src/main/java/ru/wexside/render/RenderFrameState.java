package ru.wexside.render;

import net.minecraft.class_243;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class RenderFrameState {
   public static volatile class_243 cameraPosition;
   public static final Matrix4f projectionMatrix = new Matrix4f();
   public static final Matrix4f viewMatrix = new Matrix4f();

   private RenderFrameState() {
   }

   public static void update(class_243 cameraPosition, Matrix4fc projection, Matrix4fc view) {
      RenderFrameState.cameraPosition = cameraPosition;
      projectionMatrix.set(projection);
      viewMatrix.set(view);
   }

   public static void clear() {
      cameraPosition = null;
      projectionMatrix.identity();
      viewMatrix.identity();
   }
}
