package ru.wexside.render.world;

import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_12249;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import org.joml.Matrix4f;

public final class WorldLineRenderer {
   private WorldLineRenderer() {
   }

   public static void draw(List<WorldLineRenderer.Segment> segments) {
      if (segments != null && !segments.isEmpty()) {
         class_310 client = class_310.method_1551();
         if (client.field_1687 != null) {
            class_4184 camera = client.field_1773.method_19418();
            class_243 cameraPosition = camera.method_71156();
            Matrix4f matrix = new Matrix4f().rotation(camera.method_23767());
            class_287 buffer = class_289.method_1348().method_60827(class_5596.field_29344, class_290.field_1576);

            for(WorldLineRenderer.Segment segment : segments) {
               vertex(buffer, matrix, cameraPosition, segment.start(), segment.startColor());
               vertex(buffer, matrix, cameraPosition, segment.end(), segment.endColor());
            }

            class_12249.method_76015().method_60895(buffer.method_60800());
         }
      }
   }

   public static void drawEntityBox(class_1297 entity, int color) {
      if (entity != null) {
         class_238 box = entity.method_5829().method_1014(0.025);
         class_243 p000 = new class_243(box.field_1323, box.field_1322, box.field_1321);
         class_243 p001 = new class_243(box.field_1323, box.field_1322, box.field_1324);
         class_243 p010 = new class_243(box.field_1323, box.field_1325, box.field_1321);
         class_243 p011 = new class_243(box.field_1323, box.field_1325, box.field_1324);
         class_243 p100 = new class_243(box.field_1320, box.field_1322, box.field_1321);
         class_243 p101 = new class_243(box.field_1320, box.field_1322, box.field_1324);
         class_243 p110 = new class_243(box.field_1320, box.field_1325, box.field_1321);
         class_243 p111 = new class_243(box.field_1320, box.field_1325, box.field_1324);
         List<WorldLineRenderer.Segment> lines = new ArrayList<>(12);
         add(lines, p000, p001, color);
         add(lines, p001, p101, color);
         add(lines, p101, p100, color);
         add(lines, p100, p000, color);
         add(lines, p010, p011, color);
         add(lines, p011, p111, color);
         add(lines, p111, p110, color);
         add(lines, p110, p010, color);
         add(lines, p000, p010, color);
         add(lines, p001, p011, color);
         add(lines, p100, p110, color);
         add(lines, p101, p111, color);
         draw(lines);
      }
   }

   private static void add(List<WorldLineRenderer.Segment> lines, class_243 start, class_243 end, int color) {
      lines.add(new WorldLineRenderer.Segment(start, end, color, color));
   }

   private static void vertex(class_287 buffer, Matrix4f matrix, class_243 camera, class_243 point, int color) {
      buffer.method_22918(
            matrix, (float)(point.field_1352 - camera.field_1352), (float)(point.field_1351 - camera.field_1351), (float)(point.field_1350 - camera.field_1350)
         )
         .method_1336(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24 & 0xFF);
   }

   public static record Segment(class_243 start, class_243 end, int startColor, int endColor) {
   }
}
