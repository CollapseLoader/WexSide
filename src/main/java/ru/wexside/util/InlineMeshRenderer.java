package ru.wexside.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4604;
import net.minecraft.class_7833;
import net.minecraft.class_4597.class_4598;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.wexside.misc.SpatialTransform;
import ru.wexside.render.RenderProjection;

public class InlineMeshRenderer {
   public InlineMeshRenderer(String name) {
   }

   public InlineMeshRenderer(String name, boolean cull) {
   }

   public InlineMeshRenderer(String name, class_2960 vertexShader, class_2960 fragmentShader, boolean cull) {
   }

   public InlineMeshRenderer(String name, class_2960 vertexShader, class_2960 fragmentShader) {
   }

   public void process3(
      class_4587 matrices,
      class_4598 consumers,
      class_243 cameraPosition,
      List<InlineMesh> meshes,
      List<float[]> positionOverrides,
      List<Matrix4f> localTransforms,
      int color,
      List<SpatialTransform> transforms,
      ModelRenderOptions style
   ) {
      if (meshes != null && !meshes.isEmpty() && transforms != null && !transforms.isEmpty()) {
         class_243 camera = cameraPosition == null ? class_243.field_1353 : cameraPosition;
         ModelRenderOptions renderStyle = style == null ? ModelRenderOptions.getAlternateRenderOptions() : style;
         List<SpatialTransform> visibleTransforms = visibleTransforms(transforms);
         if (!visibleTransforms.isEmpty()) {
            class_1921 fillLayer = class_12249.method_76023();
            class_4588 fill = consumers.method_73477(fillLayer);

            for(SpatialTransform transform : visibleTransforms) {
               matrices.method_22903();
               matrices.method_22904(transform.centerX() - camera.field_1352, transform.centerY() - camera.field_1351, transform.centerZ() - camera.field_1350);
               applyRotation(matrices, transform);
               matrices.method_22905(transform.scaleX(), transform.scaleY(), transform.scaleZ());
               emitFilledMeshes(fill, meshes, positionOverrides, localTransforms, matrices.method_23760().method_23761(), color);
               matrices.method_22909();
            }

            consumers.method_22994(fillLayer);
            emitTransformedOutlines(matrices, consumers, camera, meshes, positionOverrides, localTransforms, color, visibleTransforms, renderStyle);
         }
      }
   }

   public void process11(
      class_4587 matrices,
      class_4598 consumers,
      class_243 cameraPosition,
      List<InlineMesh> meshes,
      List<float[]> positionOverrides,
      List<Matrix4f> localTransforms,
      int color,
      List<Matrix4f> worldTransforms,
      ModelRenderOptions style
   ) {
      if (meshes != null && !meshes.isEmpty() && worldTransforms != null && !worldTransforms.isEmpty()) {
         class_243 camera = cameraPosition == null ? class_243.field_1353 : cameraPosition;
         ModelRenderOptions renderStyle = style == null ? ModelRenderOptions.getAlternateRenderOptions() : style;
         Matrix4f base = new Matrix4f(matrices.method_23760().method_23761())
            .translate((float)(-camera.field_1352), (float)(-camera.field_1351), (float)(-camera.field_1350));
         class_1921 fillLayer = class_12249.method_76023();
         class_4588 fill = consumers.method_73477(fillLayer);

         for(Matrix4f worldTransform : worldTransforms) {
            if (worldTransform != null) {
               emitFilledMeshes(fill, meshes, positionOverrides, localTransforms, new Matrix4f(base).mul(worldTransform), color);
            }
         }

         consumers.method_22994(fillLayer);
         if (renderStyle.isActive3()) {
            int outlineColor = renderStyle.process8(color);
            class_1921 outlineLayer = renderStyle.isActive2() ? class_12249.method_76015() : class_12249.method_76668();
            class_4588 outline = consumers.method_73477(outlineLayer);

            for(Matrix4f worldTransform : worldTransforms) {
               if (worldTransform != null) {
                  emitMeshOutlines(
                     outline, meshes, positionOverrides, localTransforms, new Matrix4f(base).mul(worldTransform), outlineColor, renderStyle.getFloatType()
                  );
               }
            }

            consumers.method_22994(outlineLayer);
         }
      }
   }

   private static void emitTransformedOutlines(
      class_4587 matrices,
      class_4598 consumers,
      class_243 camera,
      List<InlineMesh> meshes,
      List<float[]> positionOverrides,
      List<Matrix4f> localTransforms,
      int color,
      List<SpatialTransform> transforms,
      ModelRenderOptions style
   ) {
      if (style.isActive3()) {
         class_1921 layer = style.isActive2() ? class_12249.method_76015() : class_12249.method_76668();
         class_4588 outline = consumers.method_73477(layer);
         int outlineColor = style.process8(color);

         for(SpatialTransform transform : transforms) {
            matrices.method_22903();
            matrices.method_22904(transform.centerX() - camera.field_1352, transform.centerY() - camera.field_1351, transform.centerZ() - camera.field_1350);
            applyRotation(matrices, transform);
            matrices.method_22905(transform.scaleX(), transform.scaleY(), transform.scaleZ());
            emitMeshOutlines(outline, meshes, positionOverrides, localTransforms, matrices.method_23760().method_23761(), outlineColor, style.getFloatType());
            matrices.method_22909();
         }

         consumers.method_22994(layer);
      }
   }

   private static void applyRotation(class_4587 matrices, SpatialTransform transform) {
      if (transform.yawDegrees() != 0.0F) {
         matrices.method_22907(class_7833.field_40716.rotationDegrees(transform.yawDegrees()));
      }

      if (transform.pitchDegrees() != 0.0F) {
         matrices.method_22907(class_7833.field_40714.rotationDegrees(transform.pitchDegrees()));
      }

      if (transform.rollDegrees() != 0.0F) {
         matrices.method_22907(class_7833.field_40718.rotationDegrees(transform.rollDegrees()));
      }
   }

   private static List<SpatialTransform> visibleTransforms(List<SpatialTransform> transforms) {
      class_4604 frustum = RenderProjection.frustum();
      if (frustum == null) {
         return transforms.stream().filter(Objects::nonNull).toList();
      } else {
         ArrayList<SpatialTransform> visible = new ArrayList<>(transforms.size());

         for(SpatialTransform transform : transforms) {
            if (transform != null) {
               double radius = (double)Math.max(Math.max(Math.abs(transform.scaleX()), Math.abs(transform.scaleY())), Math.abs(transform.scaleZ()));
               class_238 bounds = new class_238(
                  transform.centerX() - radius,
                  transform.centerY() - radius,
                  transform.centerZ() - radius,
                  transform.centerX() + radius,
                  transform.centerY() + radius,
                  transform.centerZ() + radius
               );
               if (frustum.method_23093(bounds)) {
                  visible.add(transform);
               }
            }
         }

         return visible;
      }
   }

   private static void emitFilledMeshes(
      class_4588 vertices, List<InlineMesh> meshes, List<float[]> positionOverrides, List<Matrix4f> localTransforms, Matrix4fc baseMatrix, int fallbackColor
   ) {
      for(int meshIndex = 0; meshIndex < meshes.size(); ++meshIndex) {
         InlineMesh mesh = meshes.get(meshIndex);
         if (mesh != null) {
            float[] positions = positions(mesh, positionOverrides, meshIndex);
            int[] indices = mesh.getIntType2();
            if (positions != null && indices != null) {
               Matrix4f matrix = localMatrix(baseMatrix, localTransforms, meshIndex);

               for(int index = 0; index + 2 < indices.length; index += 3) {
                  emitVertex(vertices, matrix, positions, mesh.getFloatType4(), indices[index], fallbackColor);
                  emitVertex(vertices, matrix, positions, mesh.getFloatType4(), indices[index + 1], fallbackColor);
                  emitVertex(vertices, matrix, positions, mesh.getFloatType4(), indices[index + 2], fallbackColor);
                  emitVertex(vertices, matrix, positions, mesh.getFloatType4(), indices[index + 2], fallbackColor);
               }
            }
         }
      }
   }

   private static void emitMeshOutlines(
      class_4588 vertices,
      List<InlineMesh> meshes,
      List<float[]> positionOverrides,
      List<Matrix4f> localTransforms,
      Matrix4fc baseMatrix,
      int color,
      float lineWidth
   ) {
      for(int meshIndex = 0; meshIndex < meshes.size(); ++meshIndex) {
         InlineMesh mesh = meshes.get(meshIndex);
         if (mesh != null) {
            float[] positions = positions(mesh, positionOverrides, meshIndex);
            int[] edges = mesh.getIntType();
            if (positions != null) {
               if (edges == null || edges.length < 2) {
                  edges = triangleEdges(mesh.getIntType2());
               }

               Matrix4f matrix = localMatrix(baseMatrix, localTransforms, meshIndex);

               for(int index = 0; index + 1 < edges.length; index += 2) {
                  emitLine(vertices, matrix, positions, edges[index], edges[index + 1], color, lineWidth);
               }
            }
         }
      }
   }

   private static float[] positions(InlineMesh mesh, List<float[]> overrides, int index) {
      float[] override;
      return overrides != null && index < overrides.size() && (override = (float[])overrides.get(index)) != null && override.length >= 3
         ? override
         : mesh.getFloatType();
   }

   private static Matrix4f localMatrix(Matrix4fc base, List<Matrix4f> transforms, int index) {
      Matrix4f matrix = new Matrix4f(base);
      if (transforms != null && index < transforms.size() && transforms.get(index) != null) {
         matrix.mul((Matrix4fc)transforms.get(index));
      }

      return matrix;
   }

   private static void emitVertex(class_4588 vertices, Matrix4fc matrix, float[] positions, float[] colors, int vertexIndex, int fallbackColor) {
      int offset = vertexIndex * 3;
      if (offset >= 0 && offset + 2 < positions.length) {
         vertices.method_22918(matrix, positions[offset], positions[offset + 1], positions[offset + 2])
            .method_39415(vertexColor(colors, vertexIndex, fallbackColor));
      }
   }

   private static int vertexColor(float[] colors, int vertexIndex, int fallbackColor) {
      if (colors == null) {
         return fallbackColor;
      } else {
         int offset = vertexIndex * 4;
         if (offset + 3 >= colors.length) {
            return fallbackColor;
         } else {
            int red = Math.round(Math.clamp(colors[offset], 0.0F, 1.0F) * 255.0F);
            int green = Math.round(Math.clamp(colors[offset + 1], 0.0F, 1.0F) * 255.0F);
            int blue = Math.round(Math.clamp(colors[offset + 2], 0.0F, 1.0F) * 255.0F);
            int alpha = Math.round(Math.clamp(colors[offset + 3], 0.0F, 1.0F) * 255.0F);
            return alpha << 24 | red << 16 | green << 8 | blue;
         }
      }
   }

   private static void emitLine(class_4588 vertices, Matrix4fc matrix, float[] positions, int first, int second, int color, float width) {
      int a = first * 3;
      int b = second * 3;
      if (a >= 0 && b >= 0 && a + 2 < positions.length && b + 2 < positions.length) {
         float dx = positions[b] - positions[a];
         float dy = positions[b + 1] - positions[a + 1];
         float dz = positions[b + 2] - positions[a + 2];
         float length = (float)Math.sqrt((double)(dx * dx + dy * dy + dz * dz));
         if (length > 0.0F) {
            dx /= length;
            dy /= length;
            dz /= length;
         }

         vertices.method_22918(matrix, positions[a], positions[a + 1], positions[a + 2]).method_39415(color).method_22914(dx, dy, dz).method_75298(width);
         vertices.method_22918(matrix, positions[b], positions[b + 1], positions[b + 2]).method_39415(color).method_22914(dx, dy, dz).method_75298(width);
      }
   }

   private static int[] triangleEdges(int[] triangles) {
      if (triangles == null) {
         return new int[0];
      } else {
         int[] edges = new int[triangles.length / 3 * 6];
         int target = 0;

         for(int index = 0; index + 2 < triangles.length; index += 3) {
            int a = triangles[index];
            int b = triangles[index + 1];
            int c = triangles[index + 2];
            edges[target++] = a;
            edges[target++] = b;
            edges[target++] = b;
            edges[target++] = c;
            edges[target++] = c;
            edges[target++] = a;
         }

         return edges;
      }
   }
}
