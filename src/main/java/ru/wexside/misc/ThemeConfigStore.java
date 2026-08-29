package ru.wexside.misc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.Set;

public class ThemeConfigStore extends JsonConfigStore implements ConfigStore {
   public ThemeConfigStore(File file, Gson gson2) {
      super(file, gson2);
   }

   @Override
   public void load() {
      ConfigReadResult config = EncryptedConfigIO.readConfig(this.file, this.gson2);
      JsonObject json2 = config.json();
      if (json2.has("magic") && "wexside".equals(json2.get("magic").getAsString())) {
         if (json2.has("theme")) {
            ThemeManager.getThemeManager().selectImmediately(json2.get("theme").getAsString());
         }

         if (config.needsMigration()) {
            try {
               this.save();
            } catch (IOException var4) {
               throw new IllegalStateException("Failed to migrate theme configuration", var4);
            }
         }
      }
   }

   @Override
   public void save() throws IOException {
      JsonObject json2 = new JsonObject();
      json2.addProperty("magic", "wexside");
      json2.addProperty("version", 1);
      json2.addProperty("theme", ThemeManager.getThemeManager().getCurrentTheme().id());
      EncryptedConfigIO.writeConfig(this.file, json2, Set.of("magic", "version"), this.gson2);
   }
}
