package ru.wexside.misc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class FriendListStore extends JsonConfigStore implements ConfigStore {
   private final Set<String> field20 = ConcurrentHashMap.newKeySet();

   public FriendListStore(File file, Gson gson2) {
      super(file, gson2);
   }

   @Override
   public void load() {
      ConfigReadResult config = EncryptedConfigIO.readConfig(this.file, this.gson2);
      JsonObject json2 = config.json();
      if (json2.has("magic") && "wexside".equals(json2.get("magic").getAsString())) {
         this.field20.clear();
         if (json2.has("friends") && json2.get("friends").isJsonArray()) {
            for(JsonElement iiIiiliiiI2 : json2.getAsJsonArray("friends")) {
               if (!iiIiiliiiI2.isJsonNull()) {
                  this.field20.add(iiIiiliiiI2.getAsString());
               }
            }
         }

         if (config.needsMigration()) {
            try {
               this.save();
            } catch (IOException var5) {
               throw new IllegalStateException("Failed to migrate friend list", var5);
            }
         }
      }
   }

   @Override
   public void save() throws IOException {
      JsonObject json2 = new JsonObject();
      json2.addProperty("magic", "wexside");
      json2.addProperty("version", 1);
      JsonArray iIIiliiIiI2 = new JsonArray();
      Iterator<String> iterator = this.field20.iterator();

      while(iterator.hasNext()) {
         iIIiliiIiI2.add(iterator.next());
      }

      json2.add("friends", iIIiliiIiI2);
      EncryptedConfigIO.writeConfig(this.file, json2, Set.of("magic", "version"), this.gson2);
   }

   public Set<String> getSet() {
      return this.field20;
   }
}
