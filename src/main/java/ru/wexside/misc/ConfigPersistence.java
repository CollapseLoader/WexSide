package ru.wexside.misc;

import java.io.IOException;
import java.util.List;
import ru.wexside.config.ConfigSerializable;

public interface ConfigPersistence {
   void register(ConfigSerializable var1);

   void applyEntries(List<ConfigFileEntry> var1) throws IOException;

   List<ConfigFileEntry> serializeEntries() throws IOException;
}
