package ru.wexside.render;

import net.minecraft.class_12249;
import net.minecraft.class_1657;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_9799;
import net.minecraft.class_4597.class_4598;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.misc.ModelEspSettings;
import ru.wexside.model.esp.EspTargetClassifier;
import ru.wexside.util.EspFeatureRegistry;

public final class WorldSkeletonRenderer {
   private final EspFeatureRegistry espSettings;
   private final class_9799 bufferAllocator = new class_9799(65536);
   private final class_4598 vertexConsumers = class_4597.method_22991(this.bufferAllocator);

   public WorldSkeletonRenderer(EventBus eventBus, EspFeatureRegistry espSettings) {
      this.espSettings = espSettings;
      eventBus.subscribe(WorldRenderEvent.class, this::render);
   }

   private void render(WorldRenderEvent event) {
      class_310 client = class_310.method_1551();
      class_243 camera = RenderCamera.position();
      if (this.espSettings.hasEnabledModelEsp() && client.field_1687 != null && client.field_1724 != null && camera != null) {
         Matrix4f matrix = new Matrix4f(event.getMatrices().method_23760().method_23761());
         this.renderSkeletons(matrix, client, camera, event.getFloatType());
      }
   }

   private void renderSkeletons(Matrix4f matrix, class_310 client, class_243 camera, float tickProgress) {
      class_1921 layer = class_12249.method_76668();
      class_4588 vertices = null;

      for(class_1657 player : client.field_1687.method_18456()) {
         if (player != client.field_1724 && player.method_5805()) {
            ModelEspSettings settings = this.settingsFor(player);
            if (settings != null && settings.isEnabled() && !settings.isModelStyle()) {
               if (vertices == null) {
                  vertices = this.vertexConsumers.method_73477(layer);
               }

               drawPlayer(vertices, matrix, player, camera, tickProgress, settings.getOutlineColor());
            }
         }
      }

      if (vertices != null) {
         this.vertexConsumers.method_22994(layer);
      }
   }

   private ModelEspSettings settingsFor(class_1657 player) {
      return this.espSettings.getModelEspSettings(EspTargetClassifier.relation(player));
   }

   private static void drawPlayer(class_4588 vertices, Matrix4f matrix, class_1657 player, class_243 camera, float tickProgress, int color) {
      class_243 position = player.method_30950(tickProgress).method_1020(camera);
      float yaw = (float)Math.toRadians((double)(-player.method_61415(tickProgress)));
      float sin = (float)Math.sin((double)yaw);
      float cos = (float)Math.cos((double)yaw);
      float lean = player.method_5715() ? 0.18F : 0.0F;
      Vector3f hips = point(position, 0.0F, 0.72F, lean, sin, cos);
      Vector3f chest = point(position, 0.0F, 1.35F, lean, sin, cos);
      Vector3f neck = point(position, 0.0F, 1.52F, lean, sin, cos);
      Vector3f head = point(position, 0.0F, 1.79F, lean, sin, cos);
      Vector3f leftShoulder = point(position, 0.34F, 1.38F, lean, sin, cos);
      Vector3f rightShoulder = point(position, -0.34F, 1.38F, lean, sin, cos);
      Vector3f leftHand = point(position, 0.5F, 0.82F, lean, sin, cos);
      Vector3f rightHand = point(position, -0.5F, 0.82F, lean, sin, cos);
      Vector3f leftHip = point(position, 0.18F, 0.7F, lean, sin, cos);
      Vector3f rightHip = point(position, -0.18F, 0.7F, lean, sin, cos);
      Vector3f leftFoot = point(position, 0.2F, 0.02F, 0.0F, sin, cos);
      Vector3f rightFoot = point(position, -0.2F, 0.02F, 0.0F, sin, cos);
      line(vertices, matrix, hips, chest, color, 2.0F);
      line(vertices, matrix, chest, neck, color, 2.0F);
      line(vertices, matrix, neck, head, color, 2.0F);
      line(vertices, matrix, leftShoulder, rightShoulder, color, 2.0F);
      line(vertices, matrix, leftShoulder, leftHand, color, 2.0F);
      line(vertices, matrix, rightShoulder, rightHand, color, 2.0F);
      line(vertices, matrix, leftHip, rightHip, color, 2.0F);
      line(vertices, matrix, leftHip, leftFoot, color, 2.0F);
      line(vertices, matrix, rightHip, rightFoot, color, 2.0F);
   }

   private static Vector3f point(class_243 origin, float localX, float localY, float localZ, float sin, float cos) {
      float rotatedX = localX * cos - localZ * sin;
      float rotatedZ = localX * sin + localZ * cos;
      return new Vector3f((float)origin.field_1352 + rotatedX, (float)origin.field_1351 + localY, (float)origin.field_1350 + rotatedZ);
   }

   private static void line(class_4588 vertices, Matrix4f matrix, Vector3f from, Vector3f to, int color, float width) {
      Vector3f direction = new Vector3f(to).sub(from);
      if (direction.lengthSquared() > 0.0F) {
         direction.normalize();
      }

      vertices.method_22918(matrix, from.x, from.y, from.z).method_39415(color).method_22914(direction.x, direction.y, direction.z).method_75298(width);
      vertices.method_22918(matrix, to.x, to.y, to.z).method_39415(color).method_22914(direction.x, direction.y, direction.z).method_75298(width);
   }
}
