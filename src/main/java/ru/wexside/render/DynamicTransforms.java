package ru.wexside.render;

import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.util.List;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import org.joml.Matrix4f;
import ru.wexside.misc.SpatialTransform;
import ru.wexside.util.InlineMesh;

public final class DynamicTransforms {
   public void process8(
      List<InlineMesh> meshes,
      class_243 cameraPosition,
      int fillColor,
      int outlineColor,
      List<SpatialTransform> transforms,
      boolean drawOutline,
      float outlineWidth,
      float animationTime,
      int animationMode
   ) {
      class_310 client = class_310.method_1551();
      if (client != null && meshes != null && !meshes.isEmpty() && cameraPosition != null && transforms != null && !transforms.isEmpty()) {
         int animatedFillColor = this.animateColor(fillColor, animationTime, animationMode);
         int animatedOutlineColor = this.animateColor(outlineColor, animationTime, animationMode);
         class_287 fillBuffer = class_289.method_1348().method_60827(class_5596.field_27379, class_290.field_1576);
         class_287 outlineBuffer = drawOutline ? class_289.method_1348().method_60827(class_5596.field_29344, class_290.field_1576) : null;

         for(SpatialTransform transform : transforms) {
            Matrix4f modelMatrix = this.createModelMatrix(transform, cameraPosition);

            for(InlineMesh mesh : meshes) {
               this.emitTriangles(fillBuffer, modelMatrix, mesh, animatedFillColor);
               if (outlineBuffer != null) {
                  this.emitOutline(outlineBuffer, modelMatrix, mesh, animatedOutlineColor);
               }
            }
         }

         this.draw(class_12249.method_76023(), fillBuffer);
         if (outlineBuffer != null) {
            this.draw(class_12249.method_76015(), outlineBuffer);
         }
      }
   }

   private Matrix4f createModelMatrix(SpatialTransform transform, class_243 cameraPosition) {
      return new Matrix4f()
         .translate(
            (float)(transform.centerX() - cameraPosition.field_1352),
            (float)(transform.centerY() - cameraPosition.field_1351),
            (float)(transform.centerZ() - cameraPosition.field_1350)
         )
         .rotateY((float)Math.toRadians((double)transform.yawDegrees()))
         .rotateX((float)Math.toRadians((double)transform.pitchDegrees()))
         .rotateZ((float)Math.toRadians((double)transform.rollDegrees()))
         .scale(transform.scaleX(), transform.scaleY(), transform.scaleZ());
   }

   private void emitTriangles(class_287 output, Matrix4f matrix, InlineMesh mesh, int color) {
      float[] positions = mesh.getFloatType();
      if (positions != null && positions.length >= 3) {
         int[] indices = mesh.getIntType2();
         float[] vertexColors = mesh.getFloatType4();
         if (indices == null) {
            for(int vertex = 0; vertex < positions.length / 3; ++vertex) {
               this.emitVertex(output, matrix, positions, vertex, color, vertexColors);
            }
         } else {
            for(int vertex : indices) {
               this.emitVertex(output, matrix, positions, vertex, color, vertexColors);
            }
         }
      }
   }

   private void emitOutline(class_287 output, Matrix4f matrix, InlineMesh mesh, int color) {
      float[] positions = mesh.getFloatType();
      int[] edges = mesh.getIntType();
      if (positions != null && edges != null) {
         for(int vertex : edges) {
            this.emitVertex(output, matrix, positions, vertex, color, null);
         }
      }
   }

   private void emitVertex(class_287 output, Matrix4f matrix, float[] positions, int vertex, int color, float[] vertexColors) {
      int positionOffset = vertex * 3;
      if (positionOffset >= 0 && positionOffset + 2 < positions.length) {
         int red = color >> 16 & 0xFF;
         int green = color >> 8 & 0xFF;
         int blue = color & 0xFF;
         int alpha = color >>> 24;
         int colorOffset = vertex * 4;
         if (vertexColors != null && colorOffset + 3 < vertexColors.length) {
            red = this.multiplyColor(red, vertexColors[colorOffset]);
            green = this.multiplyColor(green, vertexColors[colorOffset + 1]);
            blue = this.multiplyColor(blue, vertexColors[colorOffset + 2]);
            alpha = this.multiplyColor(alpha, vertexColors[colorOffset + 3]);
         }

         output.method_22918(matrix, positions[positionOffset], positions[positionOffset + 1], positions[positionOffset + 2])
            .method_1336(red, green, blue, alpha);
      }
   }

   private int multiplyColor(int channel, float multiplier) {
      return Math.clamp((long)Math.round((float)channel * multiplier), 0, 255);
   }

   private int animateColor(int color, float time, int mode) {
      if (mode <= 0) {
         return color;
      } else {
         float frequency = switch(mode) {
            case 1 -> 0.65F;
            case 2 -> 1.15F;
            case 3 -> 1.75F;
            case 4 -> 0.9F;
            default -> 2.25F;
         };
         float opacity = 0.72F + 0.28F * (float)Math.sin((double)(time * frequency));
         int alpha = this.multiplyColor(color >>> 24, opacity);
         return color & 16777215 | alpha << 24;
      }
   }

   private void draw(class_1921 layer, class_287 buffer) {
      layer.method_60895(buffer.method_60800());
   }
}
