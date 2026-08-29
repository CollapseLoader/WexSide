package ru.wexside.render;

import net.minecraft.class_12249;
import net.minecraft.class_1657;
import net.minecraft.class_1921;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597.class_4598;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import ru.wexside.misc.CaptureFramebuffer;
import ru.wexside.misc.ModelEspSettings;

public final class SkeletonPreviewRenderer {
   private final CaptureFramebuffer framebuffer = new CaptureFramebuffer();

   public void close() {
      this.framebuffer.method_1238();
   }

   public int render(
      OffscreenRenderManager pipeline,
      class_1657 player,
      int framebufferWidth,
      int framebufferHeight,
      float centerX,
      float centerY,
      float scale,
      float verticalOffset,
      float bodyYaw,
      ModelEspSettings settings
   ) {
      if (pipeline != null && player != null && settings != null && framebufferWidth > 0 && framebufferHeight > 0) {
         int color = settings.getOutlineColor();
         pipeline.process3(
            this.framebuffer,
            framebufferWidth,
            framebufferHeight,
            centerX,
            centerY,
            scale,
            verticalOffset,
            bodyYaw,
            (matrices, consumers) -> drawSkeleton(matrices, consumers, player, color, settings.isModelStyle())
         );
         return this.framebuffer.getIntType();
      } else {
         return 0;
      }
   }

   private static void drawSkeleton(class_4587 matrices, class_4598 consumers, class_1657 player, int color, boolean modelStyle) {
      class_1921 layer = class_12249.method_76668();
      class_4588 vertices = consumers.method_73477(layer);
      Matrix4f matrix = new Matrix4f(matrices.method_23760().method_23761());
      float bodyLean = player.method_5715() ? 0.16F : 0.0F;
      Vector3f hips = new Vector3f(0.0F, 0.72F, bodyLean);
      Vector3f chest = new Vector3f(0.0F, 1.35F, bodyLean);
      Vector3f neck = new Vector3f(0.0F, 1.52F, bodyLean);
      Vector3f head = new Vector3f(0.0F, 1.78F, bodyLean);
      Vector3f leftShoulder = new Vector3f(0.34F, 1.38F, bodyLean);
      Vector3f rightShoulder = new Vector3f(-0.34F, 1.38F, bodyLean);
      Vector3f leftHand = new Vector3f(0.48F, 0.82F, bodyLean);
      Vector3f rightHand = new Vector3f(-0.48F, 0.82F, bodyLean);
      Vector3f leftHip = new Vector3f(0.18F, 0.7F, bodyLean);
      Vector3f rightHip = new Vector3f(-0.18F, 0.7F, bodyLean);
      Vector3f leftFoot = new Vector3f(0.2F, 0.02F, 0.0F);
      Vector3f rightFoot = new Vector3f(-0.2F, 0.02F, 0.0F);
      line(vertices, matrix, hips, chest, color);
      line(vertices, matrix, chest, neck, color);
      line(vertices, matrix, neck, head, color);
      line(vertices, matrix, leftShoulder, rightShoulder, color);
      line(vertices, matrix, leftShoulder, leftHand, color);
      line(vertices, matrix, rightShoulder, rightHand, color);
      line(vertices, matrix, leftHip, rightHip, color);
      line(vertices, matrix, leftHip, leftFoot, color);
      line(vertices, matrix, rightHip, rightFoot, color);
      if (modelStyle) {
         line(vertices, matrix, leftShoulder, leftHip, color);
         line(vertices, matrix, rightShoulder, rightHip, color);
         line(vertices, matrix, leftHand, new Vector3f(0.58F, 0.55F, bodyLean), color);
         line(vertices, matrix, rightHand, new Vector3f(-0.58F, 0.55F, bodyLean), color);
      }

      consumers.method_22994(layer);
   }

   private static void line(class_4588 vertices, Matrix4f matrix, Vector3f from, Vector3f to, int color) {
      Vector3f direction = new Vector3f(to).sub(from);
      if (direction.lengthSquared() > 0.0F) {
         direction.normalize();
      }

      vertices.method_22918(matrix, from.x, from.y, from.z).method_39415(color).method_22914(direction.x, direction.y, direction.z).method_75298(2.0F);
      vertices.method_22918(matrix, to.x, to.y, to.z).method_39415(color).method_22914(direction.x, direction.y, direction.z).method_75298(2.0F);
   }
}
