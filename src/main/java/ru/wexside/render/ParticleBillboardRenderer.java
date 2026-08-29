package ru.wexside.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4588;
import net.minecraft.class_4597.class_4598;
import org.joml.Matrix4f;

public final class ParticleBillboardRenderer {
   private ParticleBillboardRenderer() {
   }

   public static void draw(
      double x,
      double y,
      double z,
      float width,
      float height,
      int color,
      class_2960 texture,
      boolean cutout,
      float rotationDegrees,
      float u1,
      float v1,
      float u2,
      float v2
   ) {
      class_310 client = class_310.method_1551();
      if (client.field_1687 != null && texture != null) {
         class_243 cameraPosition = client.field_1773.method_19418().method_71156();
         Matrix4f transform = new Matrix4f()
            .translation((float)(x - cameraPosition.field_1352), (float)(y - cameraPosition.field_1351), (float)(z - cameraPosition.field_1350))
            .rotate(client.field_1773.method_19418().method_23767())
            .rotateZ((float)Math.toRadians((double)rotationDegrees));
         class_1921 layer = cutout ? class_12249.method_75994(texture) : class_12249.method_76000(texture);
         class_4598 consumers = client.method_22940().method_23000();
         class_4588 vertices = consumers.method_73477(layer);
         boolean wasDepthTest = org.lwjgl.opengl.GL11.glIsEnabled(2929);
         boolean wasDepthWrite = org.lwjgl.opengl.GL11.glGetBoolean(2930);
         try {
            GlStateManager._enableDepthTest();
            GlStateManager._depthMask(true);
            float halfWidth = width * 0.5F;
         float halfHeight = height * 0.5F;
         int overlay = 0x00A000A0;
         int light = 0x00F000F0;
         vertices.method_22918(transform, -halfWidth, -halfHeight, 0.0F).method_39415(color).method_22913(u1, v2).method_22922(overlay).method_60803(light).method_22914(0.0F, 1.0F, 0.0F);
         vertices.method_22918(transform, halfWidth, -halfHeight, 0.0F).method_39415(color).method_22913(u2, v2).method_22922(overlay).method_60803(light).method_22914(0.0F, 1.0F, 0.0F);
         vertices.method_22918(transform, halfWidth, halfHeight, 0.0F).method_39415(color).method_22913(u2, v1).method_22922(overlay).method_60803(light).method_22914(0.0F, 1.0F, 0.0F);
         vertices.method_22918(transform, -halfWidth, halfHeight, 0.0F).method_39415(color).method_22913(u1, v1).method_22922(overlay).method_60803(light).method_22914(0.0F, 1.0F, 0.0F);
         consumers.method_22994(layer);
         } finally {
            setCap(2929, wasDepthTest);
            GlStateManager._depthMask(wasDepthWrite);
         }
      }
   }

   private static void setCap(int cap, boolean enabled) {
      if (enabled) {
         org.lwjgl.opengl.GL11.glEnable(cap);
      } else {
         org.lwjgl.opengl.GL11.glDisable(cap);
      }
   }
}
