package ru.wexside.misc;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class BlockEspStore {
   private final BlockEspConfigStore configStore;

   public BlockEspStore(BlockEspConfigStore blockEspConfigStore) {
      this.configStore = blockEspConfigStore;
   }

   public Map<String, Integer> getBlocks() {
      return Collections.unmodifiableMap(this.configStore.getMap());
   }

   public boolean put(String blockId, int color) {
      if (blockId != null && !blockId.isBlank()) {
         Integer previousColor = this.configStore.getMap().put(blockId, color);
         boolean changed = previousColor == null || previousColor != color;
         if (changed) {
            this.persist();
         }

         return changed;
      } else {
         return false;
      }
   }

   public boolean contains(String blockId) {
      return blockId != null && this.configStore.getMap().containsKey(blockId);
   }

   public boolean remove(String blockId) {
      if (blockId == null) {
         return false;
      } else {
         boolean removed = this.configStore.getMap().remove(blockId) != null;
         if (removed) {
            this.persist();
         }

         return removed;
      }
   }

   public void clear() {
      if (!this.configStore.getMap().isEmpty()) {
         this.configStore.getMap().clear();
         this.persist();
      }
   }

   private void persist() {
      try {
         this.configStore.save();
      } catch (IOException var2) {
      }
   }
}
