package ru.wexside.misc;

import java.io.IOException;
import java.util.List;

public interface ConfigCatalogStorage {
   List<ConfigFileEntry> serializeEntries() throws IOException;

   void applyEntries(List<ConfigFileEntry> var1) throws IOException;
}
