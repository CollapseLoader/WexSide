package ru.wexside.util;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.wexside.misc.MeshModel;
import ru.wexside.misc.SpatialTransform;
import ru.wexside.render.ModelRenderBatch;

public final class ModelRenderQueue {
   private final List<ModelRenderBatch> batches = new ArrayList<>();
   private static final SpatialTransform IDENTITY_TRANSFORM = new SpatialTransform(0.0, 0.0, 0.0, 1.0F, 1.0F, 1.0F);

   public boolean isActive() {
      return this.batches.isEmpty();
   }

   public void update() {
      this.batches.clear();
   }

   private static void process(Matrix4f[] matrix4fArray, double[] dArray) {
      for(Matrix4f matrix4f : matrix4fArray) {
         if (matrix4f != null) {
            matrix4f.setTranslation(
               (float)((double)matrix4f.m30() - dArray[0]), (float)((double)matrix4f.m31() - dArray[1]), (float)((double)matrix4f.m32() - dArray[2])
            );
         }
      }
   }

   private Matrix4f process2(SpatialTransform spatialTransform) {
      Matrix4f matrix4f = new Matrix4f();
      if (spatialTransform.yawDegrees() != 0.0F) {
         matrix4f.rotateY((float)Math.toRadians((double)spatialTransform.yawDegrees()));
      }

      if (spatialTransform.pitchDegrees() != 0.0F) {
         matrix4f.rotateX((float)Math.toRadians((double)spatialTransform.pitchDegrees()));
      }

      if (spatialTransform.rollDegrees() != 0.0F) {
         matrix4f.rotateZ((float)Math.toRadians((double)spatialTransform.rollDegrees()));
      }

      return matrix4f.scale(spatialTransform.scaleX(), spatialTransform.scaleY(), spatialTransform.scaleZ());
   }

   public List<ModelRenderBatch> getBatches() {
      return this.batches;
   }

   private Matrix4f[] process3(List<Matrix4f> list, int n) {
      Matrix4f[] matrix4fArray = new Matrix4f[n];

      for(int i = 0; i < n; ++i) {
         Matrix4fc matrix4fc = list != null && i < list.size() ? (Matrix4fc)list.get(i) : null;
         matrix4fArray[i] = matrix4fc != null ? new Matrix4f(matrix4fc) : new Matrix4f();
      }

      return matrix4fArray;
   }

   private double process4(Matrix4f[] matrix4fArray) {
      if (matrix4fArray != null && matrix4fArray.length != 0) {
         double d = 1.0;

         for(Matrix4f matrix4f : matrix4fArray) {
            if (matrix4f != null) {
               double d2 = (double)matrix4f.m30();
               double d3 = (double)matrix4f.m31();
               double d4 = (double)matrix4f.m32();
               d = Math.max(d, Math.sqrt(d2 * d2 + d3 * d3 + d4 * d4) + 1.0);
            }
         }

         return d;
      } else {
         return 1.0;
      }
   }

   private static double[] process5(Matrix4f[] matrix4fArray) {
      double d = 0.0;
      double d2 = 0.0;
      double d3 = 0.0;
      int n = 0;

      for(Matrix4f matrix4f : matrix4fArray) {
         if (matrix4f != null) {
            d += (double)matrix4f.m30();
            d2 += (double)matrix4f.m31();
            d3 += (double)matrix4f.m32();
            ++n;
         }
      }

      return n == 0 ? new double[]{0.0, 0.0, 0.0} : new double[]{d / (double)n, d2 / (double)n, d3 / (double)n};
   }

   private void process6(List<InlineMesh> list, Matrix4f[] matrix4fArray, double d, double d2, double d3, int n, ModelRenderOptions modelRenderOptions) {
      if (list != null && !list.isEmpty()) {
         ModelRenderOptions modelRenderOptions2 = modelRenderOptions != null ? modelRenderOptions : ModelRenderOptions.getAlternateRenderOptions();
         this.batches
            .add(new ModelRenderBatch(list, matrix4fArray, n, modelRenderOptions2.process8(n), d, d2, d3, this.process4(matrix4fArray), modelRenderOptions2));
      }
   }

   public void process7(MeshModel meshModel, SpatialTransform spatialTransform, int n, ModelRenderOptions modelRenderOptions) {
      if (meshModel != null) {
         this.process8(meshModel.getList(), null, spatialTransform, n, modelRenderOptions);
      }
   }

   private void process8(List<InlineMesh> list, List<Matrix4f> list2, SpatialTransform spatialTransform, int n, ModelRenderOptions modelRenderOptions) {
      if (list != null && !list.isEmpty()) {
         SpatialTransform spatialTransform2 = spatialTransform != null ? spatialTransform : IDENTITY_TRANSFORM;
         Matrix4f matrix4f = this.process2(spatialTransform2);
         Matrix4f[] matrix4fArray = new Matrix4f[list.size()];

         for(int i = 0; i < list.size(); ++i) {
            Matrix4f matrix4f2 = new Matrix4f(matrix4f);
            Matrix4fc matrix4fc;
            if (list2 != null && i < list2.size() && (matrix4fc = (Matrix4fc)list2.get(i)) != null) {
               matrix4f2.mul(matrix4fc);
            }

            matrix4fArray[i] = matrix4f2;
         }

         this.process6(list, matrix4fArray, spatialTransform2.centerX(), spatialTransform2.centerY(), spatialTransform2.centerZ(), n, modelRenderOptions);
      }
   }

   public void processModelInstances(MeshModel model, List<Matrix4f> transforms, SpatialTransform transform, int color, ModelRenderOptions options) {
      if (model != null && model.getList() != null && !model.getList().isEmpty()) {
         SpatialTransform spatialTransform = transform != null ? transform : IDENTITY_TRANSFORM;
         Matrix4f base = this.process2(spatialTransform);
         int meshCount = model.getList().size();
         if (transforms == null || transforms.isEmpty()) {
            this.process8(model.getList(), null, spatialTransform, color, options);
         } else {
            for(Matrix4fc instanceTransform : transforms) {
               Matrix4f[] matrix4fArray = new Matrix4f[meshCount];

               for(int i = 0; i < meshCount; ++i) {
                  Matrix4f meshMatrix = new Matrix4f(base);
                  if (instanceTransform != null) {
                     meshMatrix.mul(instanceTransform);
                  }
                  matrix4fArray[i] = meshMatrix;
               }

               double d = matrix4fArray[0] != null ? (double)matrix4fArray[0].m30() : 0.0;
               double d2 = matrix4fArray[0] != null ? (double)matrix4fArray[0].m31() : 0.0;
               double d3 = matrix4fArray[0] != null ? (double)matrix4fArray[0].m32() : 0.0;
               this.process6(model.getList(), matrix4fArray, d, d2, d3, color, options);
            }
         }
      }
   }

   public void process10(List<InlineMesh> list, List<Matrix4f> list2, int n, ModelRenderOptions modelRenderOptions) {
      if (list != null && !list.isEmpty()) {
         Matrix4f[] matrix4fArray = this.process3(list2, list.size());
         double[] dArray = process5(matrix4fArray);
         process(matrix4fArray, dArray);
         this.process6(list, matrix4fArray, dArray[0], dArray[1], dArray[2], n, modelRenderOptions);
      }
   }
}
