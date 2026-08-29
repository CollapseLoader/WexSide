package ru.wexside.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.setting.SettingKeybind;

public class KeybindRegistry implements ConfigSerializable {
   private final List<KeybindBinding> bindings = new ArrayList<>();
   private final KeybindInputBridge inputBridge;
   private final KeybindDispatcher dispatcher = new KeybindDispatcher(this.bindings);

   @Override
   public String getConfigId() {
      return "$binds";
   }

   @Override
   public void writeConfig(DataOutputStream dataOutputStream) throws IOException {
      List<SettingKeybind> settingKeybinds = new ArrayList<>();

      for(KeybindBinding binding : this.bindings) {
         if (binding instanceof SettingKeybind settingKeybind && !binding.getBindInput().isUnbound() && !settingKeybind.getSetting().getConfigId().isBlank()) {
            settingKeybinds.add(settingKeybind);
         }
      }

      dataOutputStream.writeInt(settingKeybinds.size());

      for(SettingKeybind keybind : settingKeybinds) {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         keybind.writeConfig(new DataOutputStream(buffer));
         byte[] payload = buffer.toByteArray();
         dataOutputStream.writeUTF(keybind.getSetting().getConfigId());
         dataOutputStream.writeInt(payload.length);
         dataOutputStream.write(payload);
      }
   }

   @Override
   public void readConfig(DataInputStream dataInputStream) throws IOException {
      LinkedHashMap<String, SettingKeybind> keybindsBySettingId = new LinkedHashMap<>();

      for(KeybindBinding binding : this.bindings) {
         if (binding instanceof SettingKeybind keybind) {
            keybind.resetBindingState();
            if (!keybind.getSetting().getConfigId().isBlank()) {
               keybindsBySettingId.put(keybind.getSetting().getConfigId(), keybind);
            }
         }
      }

      int entryCount = dataInputStream.readInt();
      List<KeybindRegistry.BindPayload> payloads = new ArrayList<>();

      for(int index = 0; index < entryCount; ++index) {
         String settingId = dataInputStream.readUTF();
         int payloadLength = dataInputStream.readInt();
         if (payloadLength < 0 || payloadLength > dataInputStream.available()) {
            throw new IOException("Payload length " + payloadLength + " for bind " + settingId + " does not fit the config");
         }

         byte[] payload = new byte[payloadLength];
         dataInputStream.readFully(payload);
         payloads.add(new KeybindRegistry.BindPayload(settingId, payload));
      }

      for(KeybindRegistry.BindPayload payload : payloads) {
         SettingKeybind settingKeybind = keybindsBySettingId.get(payload.settingId());
         if (settingKeybind != null) {
            try {
               settingKeybind.readConfig(new DataInputStream(new ByteArrayInputStream(payload.data())));
            } catch (RuntimeException | IOException var9) {
            }
         }
      }
   }

   public KeybindDispatcher getDispatcher() {
      return this.dispatcher;
   }

   public void unregister(KeybindBinding binding) {
      if (binding != null) {
         this.bindings.remove(binding);
      }
   }

   public List<KeybindBinding> getBindings() {
      return List.copyOf(this.bindings);
   }

   public void register(KeybindBinding binding) {
      if (binding != null && !this.bindings.contains(binding)) {
         this.bindings.add(binding);
      }
   }

   public void resetSettingKeybinds() {
      for(KeybindBinding binding : this.bindings) {
         if (binding instanceof SettingKeybind settingKeybind) {
            settingKeybind.resetBindingState();
         }
      }
   }

   public KeybindInputBridge getInputBridge() {
      return this.inputBridge;
   }

   public KeybindRegistry(EventBus eventBus) {
      this.inputBridge = new KeybindInputBridge(eventBus, this.dispatcher);
   }

   private static record BindPayload(String settingId, byte[] data) {
   }
}
