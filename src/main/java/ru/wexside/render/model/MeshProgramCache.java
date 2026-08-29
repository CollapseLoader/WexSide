package ru.wexside.render.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.wexside.misc.MeshDefinition;
import ru.wexside.misc.MeshModel;

public final class MeshProgramCache {
   private static final Map<String, MeshModel> PROGRAMS = new HashMap<>();

   private MeshProgramCache() {
   }

   public static MeshModel get(BuiltInMesh mesh) {
      if (mesh == null) {
         throw new IllegalArgumentException("model is null");
      } else {
         return PROGRAMS.computeIfAbsent(mesh.name(), ignored -> compile(mesh.definition()));
      }
   }

   public static MeshModel get(MeshDefinition resource) {
      if (resource == null) {
         throw new IllegalArgumentException("resource is null");
      } else {
         return PROGRAMS.computeIfAbsent("inline:" + resource.getString(), ignored -> compile(resource));
      }
   }

   private static MeshModel compile(MeshDefinition resource) {
      try {
         return new MeshModel(resource.getString(), List.copyOf(resource.getList()));
      } catch (RuntimeException var2) {
         throw new IllegalStateException("Failed to load inline model program: " + resource.getString(), var2);
      }
   }
}
