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

public final class MacroConfigStore extends JsonConfigStore implements ConfigStore {
   private final List<MacroDefinition> macros = new ArrayList<>();

   public MacroConfigStore(File file, Gson gson) {
      super(file, gson);
   }

   @Override
   public void load() {
      ConfigReadResult config = EncryptedConfigIO.readConfig(this.file, this.gson2);
      if (config != null) {
         JsonObject json2 = config.json();
         if (json2.has("magic") && "wexside".equals(json2.get("magic").getAsString())) {
            this.macros.clear();
            if (json2.has("macros") && json2.get("macros").isJsonArray()) {
               for(JsonElement iiIiiliiiI2 : json2.getAsJsonArray("macros")) {
                  String string;
                  JsonObject json3;
                  if (iiIiiliiiI2.isJsonObject()
                     && (json3 = iiIiiliiiI2.getAsJsonObject()).has("name")
                     && !(string = json3.get("name").getAsString()).isBlank()) {
                     String string2 = null;
                     if (json3.has("message")) {
                        string2 = json3.get("message").getAsString();
                     }

                     int n = -1;
                     if (json3.has("key")) {
                        n = json3.get("key").getAsInt();
                     }

                     MacroType macroType = MacroType.CHAT;
                     if (json3.has("type")) {
                        if ("COMMAND".equalsIgnoreCase(json3.get("type").getAsString())) {
                           macroType = MacroType.COMMAND;
                        }
                     } else if (string2.startsWith("/")) {
                        macroType = MacroType.COMMAND;
                     }

                     this.macros.add(new MacroDefinition(string, string2, n, macroType));
                  }
               }
            }

            if (config.needsMigration()) {
               try {
                  this.save();
               } catch (IOException var10) {
                  throw new IllegalStateException("Failed to migrate macro configuration", var10);
               }
            }
         }
      }
   }

   @Override
   public void save() throws IOException {
      JsonObject json2 = new JsonObject();
      json2.addProperty("magic", "wexside");
      json2.addProperty("version", 2);
      JsonArray iIIiliiIiI2 = new JsonArray();

      for(MacroDefinition macroDefinition : this.macros) {
         JsonObject json3 = new JsonObject();
         json3.addProperty("name", macroDefinition.getName());
         json3.addProperty("message", macroDefinition.getMessage());
         json3.addProperty("key", macroDefinition.getKeyCode());
         json3.addProperty("type", macroDefinition.getType().name());
         iIIiliiIiI2.add(json3);
      }

      json2.add("macros", iIIiliiIiI2);
      EncryptedConfigIO.writeConfig(this.file, json2, Set.of("magic", "version"), this.gson2);
   }

   public List<MacroDefinition> getMacros() {
      return this.macros;
   }
}
