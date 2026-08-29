package ru.wexside.misc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.Set;

public final class UserCacheStore extends JsonConfigStore implements ConfigStore {
   private String lastNickname;
   private String lastLoadedConfig;

   public UserCacheStore(File file, Gson gson2) {
      super(file, gson2);
   }

   @Override
   public void load() {
      ConfigReadResult config = EncryptedConfigIO.readConfig(this.file, this.gson2);
      if (config != null && config.json() != null) {
         JsonObject json2 = config.json();
         if (json2.has("magic") && "wexside".equals(json2.get("magic").getAsString())) {
            if (json2.has("lastNickname") && !json2.get("lastNickname").isJsonNull()) {
               this.lastNickname = json2.get("lastNickname").getAsString();
            }

            if (json2.has("lastLoadedConfig") && !json2.get("lastLoadedConfig").isJsonNull()) {
               this.lastLoadedConfig = json2.get("lastLoadedConfig").getAsString();
            }

            if (config.needsMigration()) {
               try {
                  this.save();
               } catch (IOException var4) {
                  throw new IllegalStateException("Failed to migrate config metadata", var4);
               }
            }
         }
      }
   }

   @Override
   public void save() throws IOException {
      JsonObject json2 = new JsonObject();
      json2.addProperty("magic", "wexside");
      json2.addProperty("version", 1);
      if (this.lastNickname != null && !this.lastNickname.isBlank()) {
         json2.addProperty("lastNickname", this.lastNickname);
      }

      if (this.lastLoadedConfig != null && !this.lastLoadedConfig.isBlank()) {
         json2.addProperty("lastLoadedConfig", this.lastLoadedConfig);
      }

      EncryptedConfigIO.writeConfig(this.file, json2, Set.of("magic", "version"), this.gson2);
   }

   public String getLastNickname() {
      return this.lastNickname;
   }

   public void setLastNickname(String lastNickname) {
      this.lastNickname = lastNickname;
   }

   public void setLastLoadedConfig(String lastLoadedConfig) {
      this.lastLoadedConfig = lastLoadedConfig;
   }

   public String getLastLoadedConfig() {
      return this.lastLoadedConfig;
   }
}
