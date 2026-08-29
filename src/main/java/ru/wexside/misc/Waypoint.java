package ru.wexside.misc;

public record Waypoint(String name, int x, int y, int z, WaypointType type) {
   public Waypoint(String name, int x, int y, int z, WaypointType type) {
      type = type == null ? WaypointType.WAYPOINT : type;
      this.name = name;
      this.x = x;
      this.y = y;
      this.z = z;
      this.type = type;
   }

   public Waypoint(String name, int x, int y, int z) {
      this(name, x, y, z, WaypointType.WAYPOINT);
   }
}
