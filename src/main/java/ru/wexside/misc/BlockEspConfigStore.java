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

public final class BlockEspConfigStore extends JsonConfigStore implements ConfigStore {
   private final Map<String, Integer> field20 = new LinkedHashMap<>();
   static final int slot = -1;

   public BlockEspConfigStore(File file, Gson gson2) {
      super(file, gson2);
   }

   @Override
   public void load() {
      ConfigReadResult config = EncryptedConfigIO.readConfig(this.file, this.gson2);
      JsonObject json2 = config.json();
      if (json2.has("magic") && "wexside".equals(json2.get("magic").getAsString())) {
         this.field20.clear();
         if (json2.has("blocks") && json2.get("blocks").isJsonObject()) {
            for(Entry entry : json2.get("blocks").getAsJsonObject().entrySet()) {
               if (!((JsonElement)entry.getValue()).isJsonNull()) {
                  this.field20.put((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsInt());
               }
            }
         }

         if (config.needsMigration()) {
            try {
               this.save();
            } catch (IOException var5) {
               throw new IllegalStateException("Failed to migrate keybind configuration", var5);
            }
         }
      }
   }

   public Map<String, Integer> getMap() {
      return this.field20;
   }

   @Override
   public void save() throws IOException {
      JsonObject json2 = new JsonObject();
      json2.addProperty("magic", "wexside");
      json2.addProperty("version", 2);
      JsonObject json3 = new JsonObject();

      for(Entry<String, Integer> entry : this.field20.entrySet()) {
         json3.addProperty(entry.getKey(), entry.getValue());
      }

      json2.add("blocks", json3);
      EncryptedConfigIO.writeConfig(this.file, json2, Set.of("magic", "version"), this.gson2);
   }
}
