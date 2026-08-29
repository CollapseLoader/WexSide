package ru.wexside.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import ru.wexside.config.ConfigSerializable;

public class ConfigEntryCodec implements ConfigCatalogStorage {
   private final List<ConfigSerializable> entries;

   public ConfigEntryCodec(List<ConfigSerializable> entries) {
      this.entries = entries;
   }

   @Override
   public List<ConfigFileEntry> serializeEntries() throws IOException {
      List<ConfigFileEntry> serializedEntries = new ArrayList<>();

      for(ConfigSerializable entry : this.entries) {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         entry.writeConfig(new DataOutputStream(buffer));
         serializedEntries.add(new ConfigFileEntry(entry.getConfigId(), Base64.getEncoder().encodeToString(buffer.toByteArray())));
      }

      return serializedEntries;
   }

   @Override
   public void applyEntries(List<ConfigFileEntry> entries) throws IOException {
      if (entries != null && !entries.isEmpty()) {
         LinkedHashMap<String, ConfigSerializable> entriesById = new LinkedHashMap<>();

         for(ConfigSerializable entry : this.entries) {
            entriesById.put(entry.getConfigId(), entry);
         }

         for(ConfigFileEntry serializedEntry : entries) {
            if (serializedEntry != null && serializedEntry.path() != null && serializedEntry.data() != null) {
               ConfigSerializable target = entriesById.get(serializedEntry.path());
               if (target != null) {
                  try {
                     byte[] payload = Base64.getDecoder().decode(serializedEntry.data());
                     target.readConfig(new DataInputStream(new ByteArrayInputStream(payload)));
                  } catch (RuntimeException | IOException var7) {
                  }
               }
            }
         }
      }
   }
}
