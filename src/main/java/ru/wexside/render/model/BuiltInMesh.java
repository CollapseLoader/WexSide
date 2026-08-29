package ru.wexside.render.model;

import ru.wexside.misc.ArrowMeshDefinition;
import ru.wexside.misc.CubeMeshDefinition;
import ru.wexside.misc.CylinderMeshDefinition;
import ru.wexside.misc.MarkerMeshDefinition;
import ru.wexside.misc.MeshDefinition;
import ru.wexside.misc.PlayerMeshDefinition;
import ru.wexside.misc.SkullMeshDefinition;
import ru.wexside.misc.SwordMeshDefinition;
import ru.wexside.misc.TotemMeshDefinition;
import ru.wexside.misc.TridentMeshDefinition;

public enum BuiltInMesh {
   CUBE(new CubeMeshDefinition()),
   ARROW(new ArrowMeshDefinition()),
   PLAYER(new PlayerMeshDefinition()),
   TOTEM(new TotemMeshDefinition()),
   TRIDENT(new TridentMeshDefinition()),
   SKULL(new SkullMeshDefinition()),
   SWORD(new SwordMeshDefinition()),
   CYLINDER(new CylinderMeshDefinition()),
   MARKER(new MarkerMeshDefinition());

   private final MeshDefinition definition;

   private BuiltInMesh(MeshDefinition definition) {
      this.definition = definition;
   }

   public MeshDefinition definition() {
      return this.definition;
   }
}
