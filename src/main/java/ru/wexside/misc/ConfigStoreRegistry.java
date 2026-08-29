package ru.wexside.misc;

public interface ConfigStoreRegistry {
   <T extends ConfigStore> T getStore(Class<T> var1);

   void registerStore(ConfigStore var1);
}
