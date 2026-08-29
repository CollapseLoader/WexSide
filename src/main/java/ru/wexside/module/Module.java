package ru.wexside.module;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.Event;
import ru.wexside.event.EventBus;
import ru.wexside.event.EventListener;
import ru.wexside.setting.Setting;

public abstract class Module implements ConfigSerializable {
   private final ModuleCategory category;
   private final List<Setting> settings = new ArrayList<>();
   private final EventBus eventBus;
   private final String id;
   private final String description;
   private Setting toggleSetting;
   private final List<String> aliases;
   private final String displayName;

   public Module(EventBus eventBus, String id, String displayName, String description, ModuleCategory category, String... aliases) {
      this.id = this.sanitizeId(id != null && !id.isBlank() ? id : displayName);
      this.displayName = displayName;
      this.description = description;
      this.aliases = List.of(aliases);
      this.category = category;
      this.eventBus = eventBus;
      this.initialize();
   }

   protected abstract void initialize();

   protected final <T extends Setting> T registerToggle(T setting) {
      setting.setToggle(true);
      this.assignToggleSetting(setting);
      return setting;
   }

   protected final void registerSetting(Setting setting) {
      this.assignToggleSetting(setting);
      setting.bindModuleId(this.id);
      this.settings.add(setting);
   }

   protected final <T extends Event> void listen(Class<T> eventType, EventListener<? super T> listener) {
      this.eventBus.subscribe(eventType, listener);
   }

   @Override
   public String getConfigId() {
      return this.id;
   }

   private void assignToggleSetting(Setting setting) {
      if (setting.isToggle()) {
         if (this.toggleSetting != null && this.toggleSetting != setting) {
            throw new IllegalStateException("Only one module toggler setting is allowed for module " + this.displayName);
         } else {
            this.toggleSetting = setting;
         }
      }
   }

   public boolean isToggleSetting(Setting setting) {
      return this.toggleSetting == setting;
   }

   private void readLegacyConfig(DataInputStream input) throws IOException {
      for(Setting setting : this.settings) {
         setting.readConfig(input);
      }
   }

   protected final <T extends Event> void listenPriority(Class<T> eventType, EventListener<? super T> listener, int priority) {
      this.eventBus.subscribe(eventType, listener, priority);
   }

   public Setting getToggleSetting() {
      return this.toggleSetting;
   }

   private String sanitizeId(String value) {
      StringBuilder sanitized = new StringBuilder();
      boolean previousWasSeparator = false;

      for(int index = 0; index < value.length(); ++index) {
         char character = Character.toLowerCase(value.charAt(index));
         if (Character.isLetterOrDigit(character)) {
            sanitized.append(character);
            previousWasSeparator = false;
         } else if (!previousWasSeparator) {
            sanitized.append('_');
            previousWasSeparator = true;
         }
      }

      String result = sanitized.toString().replaceAll("^_+|_+$", "");
      if (!result.isBlank()) {
         return result;
      } else {
         throw new IllegalArgumentException("Module id must not be blank for " + this.getClass().getName());
      }
   }

   public String getId() {
      return this.id;
   }

   public ModuleCategory getCategory() {
      return this.category;
   }

   public List<Setting> getSettings() {
      return this.settings;
   }

   @Override
   public void writeConfig(DataOutputStream output) throws IOException {
      output.writeInt(this.settings.size());

      for(Setting setting : this.settings) {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         setting.writeConfig(new DataOutputStream(buffer));
         byte[] payload = buffer.toByteArray();
         output.writeUTF(setting.getId());
         output.writeInt(payload.length);
         output.write(payload);
      }
   }

   private void readNamedConfig(DataInputStream input) throws IOException {
      LinkedHashMap<String, Setting> settingsById = new LinkedHashMap<>();

      for(Setting setting : this.settings) {
         settingsById.put(setting.getId(), setting);
      }

      int count = input.readInt();
      ArrayList<Module.ConfigEntry> entries = new ArrayList<>();

      for(int index = 0; index < count; ++index) {
         String id = input.readUTF();
         int length = input.readInt();
         if (length < 0 || length > input.available()) {
            throw new IOException("Payload length " + length + " for setting " + id + " does not fit the config");
         }

         byte[] payload = new byte[length];
         input.readFully(payload);
         entries.add(new Module.ConfigEntry(id, payload));
      }

      for(Module.ConfigEntry entry : entries) {
         Setting setting = settingsById.get(entry.id());
         if (setting != null) {
            try {
               setting.readConfig(new DataInputStream(new ByteArrayInputStream(entry.payload())));
            } catch (RuntimeException | IOException var9) {
               System.err.println("[WexSide] Failed to read config for setting '" + entry.id() + "' in module '" + this.id + "': " + var9.getMessage());
            }
         }
      }
   }

   @Override
   public void readConfig(DataInputStream input) throws IOException {
      byte[] payload = input.readAllBytes();

      try {
         this.readNamedConfig(new DataInputStream(new ByteArrayInputStream(payload)));
      } catch (RuntimeException | IOException var4) {
         this.readLegacyConfig(new DataInputStream(new ByteArrayInputStream(payload)));
      }
   }

   public EventBus getEventBus() {
      return this.eventBus;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public List<String> getAliases() {
      return this.aliases;
   }

   public String getDescription() {
      if (this.description != null && !this.description.isBlank()) {
         return this.description;
      } else {
         return this.toggleSetting != null && this.toggleSetting.hasDescription() ? this.toggleSetting.getDescription() : "";
      }
   }

   private static record ConfigEntry(String id, byte[] payload) {
   }
}
