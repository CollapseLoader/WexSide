package ru.wexside.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.wexside.render.model.BuiltInMesh;
import ru.wexside.render.model.MeshProgramCache;

public final class LazyMeshModel {
   private boolean failed;
   private MeshModel meshModel;
   private final BuiltInMesh mesh;
   private static final Logger LOGGER = LoggerFactory.getLogger(LazyMeshModel.class);

   private LazyMeshModel(BuiltInMesh mesh) {
      this.mesh = mesh;
   }

   public boolean load() {
      if (this.meshModel != null) {
         return true;
      } else if (this.failed) {
         return false;
      } else {
         try {
            this.meshModel = MeshProgramCache.get(this.mesh);
            return true;
         } catch (Throwable var2) {
            this.failed = true;
            LOGGER.error("Failed to load model program {}", this.mesh != null ? this.mesh.name() : "null", var2);
            return false;
         }
      }
   }

   public boolean hasFailed() {
      return this.failed;
   }

   public MeshModel getMeshModel() {
      this.load();
      return this.meshModel;
   }

   public static LazyMeshModel create(BuiltInMesh mesh) {
      LazyMeshModel model = new LazyMeshModel(mesh);
      model.load();
      return model;
   }

   public boolean isLoaded() {
      return this.load();
   }
}
