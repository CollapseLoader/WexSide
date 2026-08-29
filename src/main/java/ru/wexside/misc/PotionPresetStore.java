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

public final class PotionPresetStore extends JsonConfigStore implements ConfigStore {
   private List<PotionPreset> presets = new ArrayList<>();

   public PotionPresetStore(File file, Gson gson2) {
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

            this.presets.clear();
            if (json2.has("presets") && json2.get("presets").isJsonArray()) {
               for(JsonElement iiIiiliiiI2 : json2.getAsJsonArray("presets")) {
                  try {
                     JsonObject json3 = iiIiiliiiI2.getAsJsonObject();
                     ArrayList<String> arrayList = new ArrayList<>();
                     if (json3.has("potions") && json3.get("potions").isJsonArray()) {
                        for(JsonElement iiIiiliiiI3 : json3.getAsJsonArray("potions")) {
                           arrayList.add(iiIiiliiiI3.getAsString());
                        }
                     }

                     this.presets
                        .add(
                           new PotionPreset(
                              json3.get("name").getAsString(),
                              json3.has("bind") ? json3.get("bind").getAsInt() : 0,
                              json3.has("favorite") && json3.get("favorite").getAsBoolean(),
                              arrayList
                           )
                        );
                  } catch (RuntimeException var10) {
                  }
               }
            }

            if (config.needsMigration()) {
               try {
                  this.save();
               } catch (IOException var9) {
                  throw new IllegalStateException("Failed to migrate potion presets", var9);
               }
            }
         } catch (RuntimeException var11) {
         }
      }
   }

   @Override
   public void save() throws IOException {
      JsonObject json2 = new JsonObject();
      json2.addProperty("magic", "wexside");
      json2.addProperty("version", 1);
      JsonArray iIIiliiIiI2 = new JsonArray();

      for(PotionPreset preset : this.presets) {
         JsonObject json3 = new JsonObject();
         json3.addProperty("name", preset.name());
         json3.addProperty("bind", preset.keyCode());
         json3.addProperty("favorite", preset.favorite());
         JsonArray iIIiliiIiI3 = new JsonArray();

         for(String string : preset.potionIds()) {
            iIIiliiIiI3.add(string == null ? "" : string);
         }

         json3.add("potions", iIIiliiIiI3);
         iIIiliiIiI2.add(json3);
      }

      json2.add("presets", iIIiliiIiI2);
      EncryptedConfigIO.writeConfig(this.file, json2, Set.of("magic", "version"), this.gson2);
   }

   public void setList(List<PotionPreset> presets) {
      this.presets = presets == null ? new ArrayList<>() : new ArrayList<>(presets);
   }

   public List<PotionPreset> getList() {
      return this.presets;
   }
}
