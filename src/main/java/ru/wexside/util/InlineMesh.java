package ru.wexside.util;

import java.util.Arrays;

public final class InlineMesh {
   private final float[] positions;
   private final float[] normals;
   private final float[] textureCoordinates;
   private final int[] triangleIndices;
   private final int[] outlineEdges;
   private final float[] colors;

   public InlineMesh(float[] fArray, float[] fArray2, float[] fArray3, int[] nArray, int[] nArray2, float[] fArray4) {
      this.positions = fArray;
      this.normals = fArray2;
      this.textureCoordinates = fArray3;
      this.triangleIndices = nArray;
      this.outlineEdges = nArray2;
      this.colors = fArray4;
   }

   public InlineMesh(float[] fArray, float[] fArray2, float[] fArray3, int[] nArray, int[] nArray2) {
      this(fArray, fArray2, fArray3, nArray, nArray2, null);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof InlineMesh)) {
         return false;
      } else {
         InlineMesh inlineMesh = (InlineMesh)object;
         return Arrays.equals(this.positions, inlineMesh.positions)
            && Arrays.equals(this.normals, inlineMesh.normals)
            && Arrays.equals(this.textureCoordinates, inlineMesh.textureCoordinates)
            && Arrays.equals(this.triangleIndices, inlineMesh.triangleIndices)
            && Arrays.equals(this.outlineEdges, inlineMesh.outlineEdges)
            && Arrays.equals(this.colors, inlineMesh.colors);
      }
   }

   @Override
   public String toString() {
      return "InlineMesh[positions="
         + Arrays.toString(this.positions)
         + ", normals="
         + Arrays.toString(this.normals)
         + ", texCoords="
         + Arrays.toString(this.textureCoordinates)
         + ", indices="
         + Arrays.toString(this.triangleIndices)
         + ", outlineEdges="
         + Arrays.toString(this.outlineEdges)
         + ", colors="
         + Arrays.toString(this.colors)
         + "]";
   }

   @Override
   public int hashCode() {
      int result = Arrays.hashCode(this.positions);
      result = 31 * result + Arrays.hashCode(this.normals);
      result = 31 * result + Arrays.hashCode(this.textureCoordinates);
      result = 31 * result + Arrays.hashCode(this.triangleIndices);
      result = 31 * result + Arrays.hashCode(this.outlineEdges);
      return 31 * result + Arrays.hashCode(this.colors);
   }

   public int[] getIntType() {
      return this.outlineEdges;
   }

   public float[] getFloatType() {
      return this.positions;
   }

   public float[] getFloatType2() {
      return this.textureCoordinates;
   }

   public float[] getFloatType3() {
      return this.normals;
   }

   public int[] getIntType2() {
      return this.triangleIndices;
   }

   public float[] getFloatType4() {
      return this.colors;
   }
}
