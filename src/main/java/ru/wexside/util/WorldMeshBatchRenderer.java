package ru.wexside.util;

import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_290;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597.class_4598;
import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import ru.wexside.misc.ModelSurfaceMode;
import ru.wexside.render.ModelRenderBatch;

public final class WorldMeshBatchRenderer {
   public WorldMeshBatchRenderer(String debugName) {
   }

   public void process10(class_4587 matrices, class_4598 consumers, class_243 cameraPosition, ModelRenderQueue queue) {
      if (matrices != null && queue != null && !queue.isActive()) {
         class_243 camera = cameraPosition != null ? cameraPosition : class_243.field_1353;
         class_1921 fillLayer = class_12249.method_76023();
         class_1921 outlineLayer = class_12249.method_76015();
         class_9799 allocator = new class_9799(2097152);
         class_287 fillBuilder = new class_287(allocator, class_5596.field_27379, class_290.field_1576);
         class_287 outlineBuilder = new class_287(allocator, class_5596.field_29344, class_290.field_1576);
         class_4588 fill = fillBuilder;
         class_4588 outline = outlineBuilder;
         boolean hasFill = false;
         boolean hasOutline = false;

         for(ModelRenderBatch batch : queue.getBatches()) {
            ModelRenderOptions options = batch.options();
            if (options.getModelSurfaceMode() != ModelSurfaceMode.HIDDEN) {
               Matrix4f base = new Matrix4f(matrices.method_23760().method_23761())
                  .translate((float)(batch.x() - camera.field_1352), (float)(batch.y() - camera.field_1351), (float)(batch.z() - camera.field_1350));

               for(int i = 0; i < batch.meshes().size(); ++i) {
                  InlineMesh mesh = batch.meshes().get(i);
                  Matrix4f model = new Matrix4f(base);
                  if (batch.transforms() != null && i < batch.transforms().length && batch.transforms()[i] != null) {
                     model.mul(batch.transforms()[i]);
                  }

                  if (emitTriangles(fill, model, mesh, batch.color())) {
                     hasFill = true;
                  }

                  if (options.isActive3() && emitOutline(outline, model, mesh, options.process8(batch.outlineColor()), options.getFloatType())) {
                     hasOutline = true;
                  }
               }
            }
         }

         if (hasFill) {
            fillLayer.method_60895(fillBuilder.method_60800());
         }

         if (hasOutline) {
            outlineLayer.method_60895(outlineBuilder.method_60800());
         }
      }
   }

   private static boolean emitTriangles(class_4588 output, Matrix4f matrix, InlineMesh mesh, int color) {
      float[] positions = mesh.getFloatType();
      int[] indices = mesh.getIntType2();
      if (positions != null) {
         int emitted = 0;
         if (indices == null) {
            for(int vertex = 0; vertex < positions.length / 3; ++vertex) {
               if (emitVertex(output, matrix, positions, vertex, color, 1.0F)) {
                  ++emitted;
               }
            }
         } else {
            for(int vertex : indices) {
               if (emitVertex(output, matrix, positions, vertex, color, 1.0F)) {
                  ++emitted;
               }
            }
         }

         return emitted > 0;
      } else {
         return false;
      }
   }

   private static boolean emitOutline(class_4588 output, Matrix4f matrix, InlineMesh mesh, int color, float lineWidth) {
      float[] positions = mesh.getFloatType();
      int[] edges = mesh.getIntType();
      if (positions != null && edges != null) {
         int emitted = 0;

         for(int vertex : edges) {
            if (emitVertex(output, matrix, positions, vertex, color, lineWidth)) {
               ++emitted;
            }
         }

         return emitted > 0;
      } else {
         return false;
      }
   }

   private static boolean emitVertex(class_4588 output, Matrix4f matrix, float[] positions, int vertex, int color, float lineWidth) {
      int offset = vertex * 3;
      if (offset >= 0 && offset + 2 < positions.length) {
         output.method_22918(matrix, positions[offset], positions[offset + 1], positions[offset + 2]).method_39415(color).method_75298(lineWidth);
         return true;
      } else {
         return false;
      }
   }
}
