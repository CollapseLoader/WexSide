package ru.wexside.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import ru.wexside.misc.ConfigManager;
import ru.wexside.misc.ConfigReadResult;
import ru.wexside.misc.EncryptedConfigIO;
import ru.wexside.misc.TextureResource;

public final class LocalConfigCatalog {
   private final Gson gson = new Gson();
   private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
   private final ConfigManager profiles;
   private final List<LocalConfigEntry> entries = new ArrayList<>();
   private int revision;

   public LocalConfigCatalog(ConfigManager profiles) {
      this.profiles = profiles;
   }

   public void refresh() {
      this.closeAvatars();
      this.entries.clear();
      File directory = this.profiles == null ? null : this.profiles.getConfigDirectory();
      if (directory != null && directory.isDirectory()) {
         File[] files = directory.listFiles((ignored, name) -> name != null && name.endsWith(".wex"));
         if (files != null) {
            Arrays.sort(files, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));

            for(File file : files) {
               if (file.isFile()) {
                  this.entries.add(this.readEntry(file));
               }
            }
         }
      }

      ++this.revision;
   }

   public List<LocalConfigEntry> entries() {
      return Collections.unmodifiableList(this.entries);
   }

   public int revision() {
      return this.revision;
   }

   public boolean delete(LocalConfigEntry entry) {
      if (entry != null && entry.file() != null && entry.file().isFile()) {
         try {
            boolean deleted = this.profiles != null && this.profiles.deleteProfile(entry.name());
            if (!deleted) {
               return false;
            } else {
               this.refresh();
               return true;
            }
         } catch (Exception var3) {
            return false;
         }
      } else {
         return false;
      }
   }

   private LocalConfigEntry readEntry(File file) {
      String name = stripExtension(file.getName());
      String author = "";
      String updatedAt = this.dateFormat.format(new Date(file.lastModified()));
      String server = "Общий";
      TextureResource avatar = null;

      try {
         ConfigReadResult result = EncryptedConfigIO.readConfig(file, this.gson);
         JsonObject json = result.json();
         if (json != null) {
            author = stringValue(json, "author", author);
            updatedAt = stringValue(json, "updateDate", updatedAt);
            server = stringValue(json, "server", stringValue(json, "name", server));
            avatar = decodeAvatar(stringValue(json, "avatar", ""));
         }
      } catch (Exception var9) {
      }

      return new LocalConfigEntry(name, author, updatedAt, server, file, avatar);
   }

   private void closeAvatars() {
      for(LocalConfigEntry entry : this.entries) {
         if (entry.avatar() != null) {
            entry.avatar().close();
         }
      }
   }

   private static String stringValue(JsonObject object, String key, String fallback) {
      return object.has(key) && !object.get(key).isJsonNull() ? object.get(key).getAsString() : fallback;
   }

   private static String stripExtension(String name) {
      int separator = name.lastIndexOf(46);
      return separator < 0 ? name : name.substring(0, separator);
   }

   private static TextureResource decodeAvatar(String encoded) {
      try {
         byte[] bytes = Base64.getDecoder().decode(encoded);
         if (bytes.length == 0) {
            return null;
         } else {
            ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
            buffer.put(bytes).flip();
            return new TextureResource(buffer);
         }
      } catch (IllegalArgumentException var3) {
         return null;
      }
   }
}
