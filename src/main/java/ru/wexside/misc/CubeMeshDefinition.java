package ru.wexside.misc;

import ru.wexside.util.BuiltInMeshDefinition;

public final class CubeMeshDefinition extends BuiltInMeshDefinition implements MeshDefinition {
   @Override
   protected int[] getAbstractInt() {
      return new int[]{0, 1, 2, 0, 2, 3, 4, 5, 6, 4, 6, 7, 8, 9, 10, 8, 10, 11, 12, 13, 14, 12, 14, 15, 16, 17, 18, 16, 18, 19, 20, 21, 22, 20, 22, 23};
   }

   @Override
   protected float[] getAbstractFloat() {
      return new float[]{
         -0.5F,
         -0.5F,
         0.5F,
         0.5F,
         -0.5F,
         0.5F,
         0.5F,
         0.5F,
         0.5F,
         -0.5F,
         0.5F,
         0.5F,
         0.5F,
         -0.5F,
         -0.5F,
         -0.5F,
         -0.5F,
         -0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         0.5F,
         0.5F,
         -0.5F,
         -0.5F,
         -0.5F,
         -0.5F,
         -0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         0.5F,
         0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         0.5F,
         0.5F,
         -0.5F,
         -0.5F,
         0.5F,
         0.5F,
         -0.5F,
         0.5F,
         0.5F,
         0.5F,
         -0.5F,
         0.5F,
         0.5F,
         0.5F,
         0.5F,
         0.5F,
         0.5F,
         0.5F,
         -0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         -0.5F,
         -0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         0.5F,
         -0.5F,
         -0.5F,
         0.5F
      };
   }

   @Override
   public String getString() {
      return "models/cube";
   }
}
