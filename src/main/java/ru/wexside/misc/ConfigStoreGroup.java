package ru.wexside.misc;

import java.io.IOException;
import java.util.List;

public class ConfigStoreGroup implements ConfigStoreGroupLifecycle {
   private final List<ConfigStore> stores;

   public ConfigStoreGroup(List<ConfigStore> list) {
      this.stores = list;
   }

   @Override
   public void saveAll() {
      for(ConfigStore store : this.stores) {
         try {
            store.save();
         } catch (IOException var4) {
            throw new IllegalStateException("Failed to save configuration", var4);
         }
      }
   }

   @Override
   public void loadAll() {
      this.stores.forEach(ConfigStore::load);
   }
}
