package ru.wexside.misc;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class PasswordConfigStore extends JsonConfigStore implements ConfigStore {
   private final Map<String, String> passwords = new LinkedHashMap<>();

   public PasswordConfigStore(File file, Gson gson2) {
      super(file, gson2);
   }

   @Override
   public void load() {
      ConfigReadResult config = EncryptedConfigIO.readConfig(this.file, this.gson2);
      if (config != null) {
         try {
            JsonObject json2 = config.json();
            if (json2 == null || !json2.has("magic") || !"wexside".equals(json2.get("magic").getAsString())) {
               return;
            }

            this.passwords.clear();
            if (json2.has("accounts") && json2.get("accounts").isJsonObject()) {
               for(Entry entry : json2.getAsJsonObject("accounts").entrySet()) {
                  JsonElement passwordElement = (JsonElement)entry.getValue();
                  if (passwordElement != null && passwordElement.isJsonPrimitive()) {
                     String accountKey = (String)entry.getKey();
                     String password = passwordElement.getAsString();
                     if (accountKey != null && !accountKey.isBlank() && password != null && !password.isBlank()) {
                        this.passwords.put(accountKey, password);
                     }
                  }
               }
            }

            if (config.needsMigration()) {
               try {
                  this.save();
               } catch (IOException var8) {
               }
            }
         } catch (RuntimeException var9) {
         }
      }
   }

   @Override
   public void save() throws IOException {
      JsonObject json2 = new JsonObject();
      json2.addProperty("magic", "wexside");
      json2.addProperty("version", 1);
      JsonObject json3 = new JsonObject();

      for(Entry<String, String> entry : this.passwords.entrySet()) {
         json3.addProperty(entry.getKey(), entry.getValue());
      }

      json2.add("accounts", json3);
      EncryptedConfigIO.writeConfig(this.file, json2, Set.of("magic", "version"), this.gson2);
   }

   public Map<String, String> getPasswords() {
      return this.passwords;
   }
}
