package ru.wexside.render.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import ru.wexside.util.InlineMesh;

public final class MeshBuilder {
   private static final float POSITION_PRECISION = 100000.0F;
   private static final float SMOOTH_EDGE_DOT_THRESHOLD = 0.995F;

   private MeshBuilder() {
   }

   public static InlineMesh create(float[] positions, float[] normals, float[] textureCoordinates, int[] triangleIndices, float[] vertexColors) {
      validate(positions, normals, textureCoordinates, triangleIndices, vertexColors);
      float[] positionCopy = Arrays.copyOf(positions, positions.length);
      float[] normalCopy = normals == null ? null : Arrays.copyOf(normals, normals.length);
      float[] textureCopy = textureCoordinates == null ? null : Arrays.copyOf(textureCoordinates, textureCoordinates.length);
      int[] indexCopy = triangleIndices == null ? null : Arrays.copyOf(triangleIndices, triangleIndices.length);
      float[] colorCopy = vertexColors == null ? null : Arrays.copyOf(vertexColors, vertexColors.length);
      return new InlineMesh(positionCopy, normalCopy, textureCopy, indexCopy, buildOutlineIndices(positionCopy, indexCopy), colorCopy);
   }

   private static void validate(float[] positions, float[] normals, float[] textureCoordinates, int[] triangleIndices, float[] vertexColors) {
      if (positions != null && positions.length != 0 && positions.length % 3 == 0) {
         int vertexCount = positions.length / 3;
         if (normals != null && normals.length != positions.length) {
            throw new IllegalArgumentException("Mesh normals must match the position count");
         } else if (textureCoordinates != null && textureCoordinates.length != vertexCount * 2) {
            throw new IllegalArgumentException("Mesh texture coordinates must contain one UV pair per vertex");
         } else if (vertexColors != null && vertexColors.length != vertexCount * 4) {
            throw new IllegalArgumentException("Mesh colors must contain one RGBA value per vertex");
         } else if (triangleIndices != null) {
            if (triangleIndices.length != 0 && triangleIndices.length % 3 == 0) {
               for(int index : triangleIndices) {
                  if (index < 0 || index >= vertexCount) {
                     throw new IllegalArgumentException("Mesh index out of bounds: " + index);
                  }
               }
            } else {
               throw new IllegalArgumentException("Mesh indices must contain triangle triplets");
            }
         }
      } else {
         throw new IllegalArgumentException("Mesh positions must contain XYZ triplets");
      }
   }

   private static int[] buildOutlineIndices(float[] positions, int[] triangleIndices) {
      HashMap<MeshBuilder.EdgeKey, MeshBuilder.EdgeInfo> edges = new HashMap<>();
      if (triangleIndices != null) {
         for(int offset = 0; offset < triangleIndices.length; offset += 3) {
            addTriangle(edges, positions, triangleIndices[offset], triangleIndices[offset + 1], triangleIndices[offset + 2]);
         }
      } else {
         int vertexCount = positions.length / 3;

         for(int vertex = 0; vertex + 2 < vertexCount; vertex += 3) {
            addTriangle(edges, positions, vertex, vertex + 1, vertex + 2);
         }
      }

      int[] outlineIndices = new int[edges.size() * 2];
      int offset = 0;

      for(MeshBuilder.EdgeInfo edge : edges.values()) {
         if (edge.visible()) {
            outlineIndices[offset++] = edge.firstIndex;
            outlineIndices[offset++] = edge.secondIndex;
         }
      }

      return Arrays.copyOf(outlineIndices, offset);
   }

   private static void addTriangle(Map<MeshBuilder.EdgeKey, MeshBuilder.EdgeInfo> edges, float[] positions, int first, int second, int third) {
      MeshBuilder.FaceNormal normal = calculateNormal(positions, first, second, third);
      addEdge(edges, positions, first, second, normal);
      addEdge(edges, positions, second, third, normal);
      addEdge(edges, positions, third, first, normal);
   }

   private static void addEdge(
      Map<MeshBuilder.EdgeKey, MeshBuilder.EdgeInfo> edges, float[] positions, int firstIndex, int secondIndex, MeshBuilder.FaceNormal normal
   ) {
      MeshBuilder.EdgeKey key = MeshBuilder.EdgeKey.of(positionKey(positions, firstIndex), positionKey(positions, secondIndex));
      edges.computeIfAbsent(key, ignored -> new MeshBuilder.EdgeInfo(firstIndex, secondIndex)).addFace(normal);
   }

   private static MeshBuilder.FaceNormal calculateNormal(float[] positions, int first, int second, int third) {
      int b = second * 3;
      int a = first * 3;
      float abY = positions[b + 1] - positions[a + 1];
      int c = third * 3;
      float acZ = positions[c + 2] - positions[a + 2];
      float abZ = positions[b + 2] - positions[a + 2];
      float acY = positions[c + 1] - positions[a + 1];
      float x = abY * acZ - abZ * acY;
      float acX = positions[c] - positions[a];
      float abX = positions[b] - positions[a];
      float y = abZ * acX - abX * acZ;
      float z = abX * acY - abY * acX;
      float lengthSquared = x * x + y * y + z * z;
      if (lengthSquared <= 1.0E-1F) {
         return new MeshBuilder.FaceNormal(0.0F, 1.0F, 0.0F);
      } else {
         float inverseLength = (float)(1.0 / Math.sqrt((double)lengthSquared));
         return new MeshBuilder.FaceNormal(x * inverseLength, y * inverseLength, z * inverseLength);
      }
   }

   private static MeshBuilder.PositionKey positionKey(float[] positions, int vertexIndex) {
      int offset = vertexIndex * 3;
      return new MeshBuilder.PositionKey(
         Math.round(positions[offset] * 100000.0F), Math.round(positions[offset + 1] * 100000.0F), Math.round(positions[offset + 2] * 100000.0F)
      );
   }

   private static final class EdgeInfo {
      private final int firstIndex;
      private final int secondIndex;
      private MeshBuilder.FaceNormal firstNormal;
      private int faceCount;
      private boolean hardEdge;

      private EdgeInfo(int firstIndex, int secondIndex) {
         this.firstIndex = firstIndex;
         this.secondIndex = secondIndex;
      }

      private void addFace(MeshBuilder.FaceNormal normal) {
         if (this.faceCount == 0) {
            this.firstNormal = normal;
         } else if (this.firstNormal.dot(normal) < 0.995F) {
            this.hardEdge = true;
         }

         ++this.faceCount;
      }

      private boolean visible() {
         return this.faceCount == 1 || this.hardEdge;
      }
   }

   private static record EdgeKey(MeshBuilder.PositionKey first, MeshBuilder.PositionKey second) {
      private static MeshBuilder.EdgeKey of(MeshBuilder.PositionKey first, MeshBuilder.PositionKey second) {
         return first.compareTo(second) <= 0 ? new MeshBuilder.EdgeKey(first, second) : new MeshBuilder.EdgeKey(second, first);
      }
   }

   private static record FaceNormal(float x, float y, float z) {
      private float dot(MeshBuilder.FaceNormal other) {
         return this.x * other.x + this.y * other.y + this.z * other.z;
      }
   }

   private static record PositionKey(int x, int y, int z) implements Comparable<MeshBuilder.PositionKey> {
      public int compareTo(MeshBuilder.PositionKey other) {
         int xComparison = Integer.compare(this.x, other.x);
         if (xComparison != 0) {
            return xComparison;
         } else {
            int yComparison = Integer.compare(this.y, other.y);
            return yComparison != 0 ? yComparison : Integer.compare(this.z, other.z);
         }
      }
   }
}
