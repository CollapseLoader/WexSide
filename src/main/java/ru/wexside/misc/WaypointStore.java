package ru.wexside.misc;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class WaypointStore {
   private final WaypointConfigStore configStore;

   public WaypointStore(WaypointConfigStore waypointConfigStore) {
      this.configStore = waypointConfigStore;
   }

   public void clear() {
      if (!this.configStore.getList().isEmpty()) {
         this.configStore.getList().clear();
         this.persist();
      }
   }

   public List<Waypoint> getWaypoints() {
      return Collections.unmodifiableList(this.configStore.getList());
   }

   public boolean removeByName(String name) {
      if (name == null) {
         return false;
      } else {
         boolean removed = this.configStore.getList().removeIf(waypoint -> waypoint.name().equalsIgnoreCase(name));
         if (removed) {
            this.persist();
         }

         return removed;
      }
   }

   public void add(Waypoint waypoint) {
      this.configStore.getList().add(waypoint);
      this.persist();
   }

   public boolean containsName(String name) {
      if (name == null) {
         return false;
      } else {
         for(Waypoint waypoint : this.configStore.getList()) {
            if (waypoint.name().equalsIgnoreCase(name)) {
               return true;
            }
         }

         return false;
      }
   }

   private void persist() {
      try {
         this.configStore.save();
      } catch (IOException var2) {
      }
   }
}
