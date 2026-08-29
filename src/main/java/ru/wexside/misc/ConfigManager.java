package ru.wexside.misc;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.minecraft.class_156;
import net.minecraft.class_310;
import ru.wexside.WexSideClient;
import ru.wexside.notification.NotificationCategory;
import ru.wexside.notification.NotificationCenter;
import ru.wexside.notification.TextNotification;

public class ConfigManager implements ConfigStoreRegistry {
   private final File clientDirectory;
   private final KeybindRegistry keybindRegistry;
   private final ConfigStoreGroupLifecycle baseStores;
   private boolean importedEntriesPending;
   private final List<ConfigStore> stores = new ArrayList<>();
   private final ConfigRegistry configRegistry;
   private String currentProfileName;
   private final Gson gson;

   public ConfigManager(File clientDirectory, Gson gson, ConfigRegistry configRegistry, KeybindRegistry keybindRegistry) {
      this.baseStores = new ConfigStoreGroup(this.stores);
      this.clientDirectory = clientDirectory;
      this.gson = gson;
      this.configRegistry = configRegistry;
      this.keybindRegistry = keybindRegistry;
      this.registerStore(new ThemeConfigStore(new File(clientDirectory, "theme.wex"), gson));
      this.registerStore(new UserCacheStore(new File(clientDirectory, "usercache.wex"), gson));
      this.registerStore(new FriendListStore(new File(clientDirectory, "friends.wex"), gson));
      this.registerStore(new BlockEspConfigStore(new File(clientDirectory, "blockesp.wex"), gson));
      this.registerStore(new StaffNameConfigStore(new File(clientDirectory, "staff.wex"), gson));
      this.registerStore(new WaypointConfigStore(new File(clientDirectory, "waypoints.wex"), gson));
      this.registerStore(new MacroConfigStore(new File(clientDirectory, "macros.wex"), gson));
      this.registerStore(new BlockedSoundStore(new File(clientDirectory, "sounds.wex"), gson));
      this.registerStore(new PasswordConfigStore(new File(clientDirectory, "autoauth.wex"), gson));
      this.registerStore(new PotionPresetStore(new File(clientDirectory, "potions.wex"), gson));
   }

   public void saveBaseStores() {
      this.baseStores.saveAll();
   }

   @Override
   public <T extends ConfigStore> T getStore(Class<T> clazz) {
      for(ConfigStore store : this.stores) {
         if (clazz == store.getClass()) {
            return clazz.cast(store);
         }
      }

      return null;
   }

   public boolean profileExists(String name) {
      return this.resolveProfileFile(name).exists();
   }

   public void openConfigFolder() throws IOException {
      File file = this.getConfigDirectory();
      if (!file.exists() && !file.mkdirs()) {
         throw new IOException("Не удалось создать папку конфигов");
      } else {
         try {
            class_156.method_668().method_672(file);
         } catch (Exception var3) {
            throw new IOException("Не удалось открыть папку конфигов", var3);
         }
      }
   }

   public String getCurrentProfileName() {
      return this.currentProfileName;
   }

   public void loadProfile(String name) {
      String profileName = this.normalizeProfileName(name);
      if (!this.profileExists(profileName)) {
         throw new IllegalArgumentException("Конфиг не найден: " + profileName);
      } else {
         this.keybindRegistry.resetSettingKeybinds();

         try {
            this.configRegistry.restoreBaseline();
         } catch (IOException var4) {
            throw new IllegalStateException("Не удалось восстановить настройки по умолчанию", var4);
         }

         try {
            this.openProfile(profileName).load();
         } catch (Exception var5) {
            try {
               this.configRegistry.restoreBaseline();
            } catch (IOException ignored) {
            }

            throw new IllegalStateException("Не удалось загрузить профиль " + profileName, var5);
         }

         this.currentProfileName = profileName;
         this.importedEntriesPending = false;
         this.saveUserCache();
         NotificationCenter notifications = WexSideClient.getNotificationCenter();
         if (notifications != null) {
            notifications.push(new TextNotification(NotificationCategory.SYSTEM, "config", "C", "Загружен профиль " + profileName));
         }
      }
   }

   public void saveProfile(String name) throws IOException {
      String profileName = this.normalizeProfileName(name);
      ConfigProfile profile = this.openProfile(profileName);
      String displayName = profile.readDisplayName();
      profile.setDisplayName(displayName != null ? displayName : "Общий");
      profile.save();
      this.currentProfileName = profileName;
      this.importedEntriesPending = false;
      this.saveUserCache();
   }

   public List<String> listProfiles() throws IOException {
      File configDirectory = this.getConfigDirectory();
      if (!configDirectory.exists()) {
         if (!configDirectory.mkdirs()) {
            throw new IOException("Не удалось создать папку конфигов");
         } else {
            return List.of();
         }
      } else {
         File[] profileFiles = configDirectory.listFiles((directory, fileNamex) -> fileNamex != null && fileNamex.endsWith(".wex"));
         if (profileFiles != null && profileFiles.length != 0) {
            Arrays.sort(profileFiles, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
            ArrayList<String> profileNames = new ArrayList<>(profileFiles.length);

            for(File profileFile : profileFiles) {
               if (profileFile != null && profileFile.isFile()) {
                  String fileName = profileFile.getName();
                  profileNames.add(fileName.substring(0, fileName.length() - ".wex".length()));
               }
            }

            return profileNames;
         } else {
            return List.of();
         }
      }
   }

   public void resetProfile() throws IOException {
      this.keybindRegistry.resetSettingKeybinds();
      this.configRegistry.restoreBaseline();
      this.currentProfileName = null;
      this.importedEntriesPending = false;
      this.saveUserCache();
   }

   public boolean hasPendingImportedEntries() {
      return this.importedEntriesPending;
   }

   public void profileRenamed(String oldName, String newName) {
      if (oldName != null) {
         String oldProfileName = this.normalizeProfileName(oldName);
         if (oldProfileName.equals(this.currentProfileName)) {
            this.currentProfileName = this.normalizeProfileName(newName);
            this.saveUserCache();
         }
      }
   }

   public void saveProfileWithDisplayName(String name, String displayName) throws IOException {
      String profileName = this.normalizeProfileName(name);
      ConfigProfile profile = this.openProfile(profileName);
      profile.setDisplayName(displayName);
      profile.save();
      this.currentProfileName = profileName;
      this.importedEntriesPending = false;
      this.saveUserCache();
   }

   private static String sanitizeProfileName(String requestedName) {
      String sanitizedName = requestedName == null ? "" : requestedName.trim();
      if (sanitizedName.endsWith(".wex")) {
         sanitizedName = sanitizedName.substring(0, sanitizedName.length() - ".wex".length());
      }

      sanitizedName = sanitizedName.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
      if (sanitizedName.isBlank()) {
         sanitizedName = "shared";
      }

      return sanitizedName.length() > 32 ? sanitizedName.substring(0, 32).trim() : sanitizedName;
   }

   private String getSessionUsername() {
      try {
         return class_310.method_1551().method_1548().method_1676();
      } catch (Exception var2) {
         return null;
      }
   }

   public void initialize() {
      this.baseStores.loadAll();

      try {
         this.configRegistry.captureBaseline();
      } catch (IOException var3) {
         throw new IllegalStateException("Failed to capture default configuration", var3);
      }

      UserCacheStore userCacheStore = this.getStore(UserCacheStore.class);
      String profileName = userCacheStore == null ? null : userCacheStore.getLastLoadedConfig();
      if (profileName == null || profileName.isBlank() || !this.profileExists(profileName)) {
         profileName = this.profileExists("default") ? "default" : null;
      }

      if (profileName != null) {
         this.loadProfile(profileName);
      }
   }

   private ConfigProfile openProfile(String name) {
      return new ConfigProfile(this.resolveProfileFile(name), this.gson, this.configRegistry);
   }

   public List<ConfigFileEntry> readProfileEntries(String name) {
      return this.openProfile(name).getEntries();
   }

   @Override
   public void registerStore(ConfigStore store) {
      this.stores.add(store);
   }

   public String importProfile(String requestedName, List<ConfigFileEntry> entries) throws IOException {
      String profileName = this.createUniqueProfileName(requestedName);
      this.keybindRegistry.resetSettingKeybinds();
      this.configRegistry.restoreBaseline();
      this.configRegistry.applyEntries(entries);
      this.saveProfileWithDisplayName(profileName, "Пати");
      return profileName;
   }

   public void profileDeleted(String name) {
      if (name != null && this.normalizeProfileName(name).equals(this.currentProfileName)) {
         this.currentProfileName = null;
         this.saveUserCache();
      }
   }

   public boolean deleteProfile(String name) throws IOException {
      File directory = this.getConfigDirectory().getCanonicalFile();
      File profile = this.resolveProfileFile(name).getCanonicalFile();
      if (!profile.toPath().startsWith(directory.toPath()) || !profile.isFile()) {
         return false;
      } else if (!profile.delete()) {
         throw new IOException("Не удалось удалить конфиг " + name);
      } else {
         this.profileDeleted(name);
         return true;
      }
   }

   private File resolveProfileFile(String name) {
      return new File(this.getConfigDirectory(), this.normalizeProfileName(name) + ".wex");
   }

   private String normalizeProfileName(String name) {
      return sanitizeProfileName(name != null && !name.isBlank() ? name : "default");
   }

   public void stageImportedEntries(List<ConfigFileEntry> entries) throws IOException {
      this.keybindRegistry.resetSettingKeybinds();
      this.configRegistry.restoreBaseline();
      this.configRegistry.applyEntries(entries);
      this.importedEntriesPending = true;
   }

   public String createUniqueProfileName(String requestedName) {
      String baseName = sanitizeProfileName(requestedName);
      if (!this.profileExists(baseName)) {
         return baseName;
      } else {
         int suffix = 2;

         while(suffix < 1000) {
            String candidate = baseName + " (" + suffix++ + ")";
            if (!this.profileExists(candidate)) {
               return candidate;
            }
         }

         return baseName + " (" + System.currentTimeMillis() + ")";
      }
   }

   public File getConfigDirectory() {
      return new File(this.clientDirectory, "config");
   }

   private void saveUserCache() {
      UserCacheStore userCacheStore = this.getStore(UserCacheStore.class);
      if (userCacheStore != null) {
         userCacheStore.setLastNickname(this.getSessionUsername());
         userCacheStore.setLastLoadedConfig(this.currentProfileName);

         try {
            userCacheStore.save();
         } catch (IOException var3) {
            WexSideClient.getInstance().getLogger().warn("Failed to save user cache", var3);
         }
      }
   }
}
