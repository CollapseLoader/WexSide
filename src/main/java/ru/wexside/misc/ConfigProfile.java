package ru.wexside.misc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ConfigProfile extends JsonConfigStore {
   private static final String FORMAT = "wexside-profile";
   private final ConfigRegistry registry;
   private String displayName;

   public ConfigProfile(File file, Gson gson, ConfigRegistry registry) {
      super(file, gson);
      this.registry = registry;
   }

   @Override
   public void load() {
      if (!this.file.isFile()) {
         throw new IllegalStateException("Config profile does not exist: " + this.file);
      } else {
         ConfigReadResult result = EncryptedConfigIO.readConfig(this.file, this.gson2);
         JsonObject root = result.json();
         if (root.has("magic") && "wexside-profile".equals(root.get("magic").getAsString())) {
            this.displayName = root.has("name") && !root.get("name").isJsonNull() ? root.get("name").getAsString() : null;
            ArrayList<ConfigFileEntry> entries = new ArrayList<>();

            for(JsonElement element : root.has("entries") && root.get("entries").isJsonArray() ? root.getAsJsonArray("entries") : new JsonArray()) {
               JsonObject entry;
               if (element.isJsonObject() && (entry = element.getAsJsonObject()).has("path") && entry.has("data")) {
                  entries.add(new ConfigFileEntry(entry.get("path").getAsString(), entry.get("data").getAsString()));
               }
            }

            try {
               this.registry.applyEntries(entries);
            } catch (IOException var9) {
               throw new IllegalStateException("Failed to apply config profile " + this.file, var9);
            }

            if (result.needsMigration()) {
               try {
                  this.save();
               } catch (IOException var8) {
                  throw new IllegalStateException("Failed to migrate config profile " + this.file, var8);
               }
            }
         } else {
            throw new IllegalStateException("Invalid config profile format: " + this.file);
         }
      }
   }

   @Override
   public void save() throws IOException {
      JsonObject root = new JsonObject();
      root.addProperty("magic", "wexside-profile");
      root.addProperty("version", 1);
      if (this.displayName != null && !this.displayName.isBlank()) {
         root.addProperty("name", this.displayName);
      }

      JsonArray entries = new JsonArray();

      for(ConfigFileEntry entry : this.registry.serializeEntries()) {
         JsonObject value = new JsonObject();
         value.addProperty("path", entry.path());
         value.addProperty("data", entry.data());
         entries.add(value);
      }

      root.add("entries", entries);
      EncryptedConfigIO.writeConfig(this.file, root, Set.of("magic", "version", "name"), this.gson2);
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   public String readDisplayName() {
      if (!this.file.isFile()) {
         return null;
      } else {
         JsonObject root = EncryptedConfigIO.readConfig(this.file, this.gson2).json();
         if (root.has("magic") && "wexside-profile".equals(root.get("magic").getAsString())) {
            return root.has("name") && !root.get("name").isJsonNull() ? root.get("name").getAsString() : null;
         } else {
            return null;
         }
      }
   }

   public List<ConfigFileEntry> getEntries() {
      ConfigReadResult result = EncryptedConfigIO.readConfig(this.file, this.gson2);
      if (result != null && result.json().has("entries")) {
         ArrayList<ConfigFileEntry> entries = new ArrayList<>();

         for(JsonElement element : result.json().getAsJsonArray("entries")) {
            JsonObject entry = element.getAsJsonObject();
            entries.add(new ConfigFileEntry(entry.get("path").getAsString(), entry.get("data").getAsString()));
         }

         return entries;
      } else {
         return List.of();
      }
   }
}
