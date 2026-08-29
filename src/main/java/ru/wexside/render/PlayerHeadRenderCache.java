package ru.wexside.render;

import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.Map.Entry;

public final class PlayerHeadRenderCache extends LinkedHashMap<UUID, IconAtlasEntry> {
   private final int capacity;

   public PlayerHeadRenderCache(int capacity) {
      super(capacity, 0.75F, true);
      this.capacity = capacity;
   }

   @Override
   protected boolean removeEldestEntry(Entry<UUID, IconAtlasEntry> eldest) {
      if (this.size() <= this.capacity) {
         return false;
      } else {
         eldest.getValue().update2();
         return true;
      }
   }
}
