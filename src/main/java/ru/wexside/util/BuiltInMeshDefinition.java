package ru.wexside.util;

import java.util.List;
import ru.wexside.misc.MeshDefinition;
import ru.wexside.render.model.MeshBuilder;

public abstract class BuiltInMeshDefinition implements MeshDefinition {
   private List<InlineMesh> meshes;

   protected abstract int[] getAbstractInt();

   protected abstract float[] getAbstractFloat();

   protected float[] getFloatType() {
      return null;
   }

   protected float[] getFloatType2() {
      return null;
   }

   protected float[] getFloatType3() {
      return null;
   }

   protected static float[] repeatColor(int vertexCount, float red, float green, float blue, float alpha) {
      float[] colors = new float[Math.max(0, vertexCount) * 4];

      for(int index = 0; index < colors.length; index += 4) {
         colors[index] = red;
         colors[index + 1] = green;
         colors[index + 2] = blue;
         colors[index + 3] = alpha;
      }

      return colors;
   }

   @Override
   public final List<InlineMesh> getList() {
      if (this.meshes == null) {
         this.meshes = List.of(
            MeshBuilder.create(this.getAbstractFloat(), this.getFloatType(), this.getFloatType3(), this.getAbstractInt(), this.getFloatType2())
         );
      }

      return this.meshes;
   }
}
