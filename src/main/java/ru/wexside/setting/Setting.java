package ru.wexside.setting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.misc.VisibilityCondition;

public abstract class Setting implements ConfigSerializable {
   String description;
   SettingKeybind keybind;
   String id;
   VisibilityCondition visibility;
   List<String> aliases;
   Supplier<String> nameSupplier;
   List<Supplier<Boolean>> visibilityChecks;
   boolean hasKeybind;
   boolean toggle;
   String qualifiedId;

   protected Setting(SettingBuilder settingBuilder) {
      this.nameSupplier = Objects.requireNonNull(settingBuilder.nameSupplier(), "name must be set");
      boolean bl = settingBuilder.settingId() != null && !settingBuilder.settingId().isBlank();
      if (settingBuilder.hasDynamicName() && !bl) {
         throw new IllegalStateException("id must be set for a setting with a dynamic name");
      } else {
         this.id = this.sanitizeId(bl ? settingBuilder.settingId() : this.getDisplayName());
         this.description = settingBuilder.settingDescription();
         this.aliases = List.copyOf(settingBuilder.settingAliases());
         this.visibilityChecks = List.copyOf(settingBuilder.visibilityChecks());
         this.visibility = settingBuilder.visibilityRule();
         this.toggle = settingBuilder.createsToggle();
         this.hasKeybind = settingBuilder.createsKeybind();
      }
   }

   public void setToggle(boolean bl) {
      this.toggle = bl;
   }

   @Override
   public String getConfigId() {
      return this.qualifiedId;
   }

   public final void bindModuleId(String string) {
      if (string != null && !string.isBlank()) {
         String string2 = this.id;
         this.qualifiedId = string + "." + string2;
      } else {
         this.qualifiedId = this.id;
      }
   }

   private String sanitizeId(String string) {
      StringBuilder stringBuilder = new StringBuilder();
      boolean bl = false;

      for(int i = 0; i < string.length(); ++i) {
         char c = Character.toLowerCase(string.charAt(i));
         if (Character.isLetterOrDigit(c)) {
            stringBuilder.append(c);
            bl = false;
         } else if (!bl) {
            stringBuilder.append('_');
            bl = true;
         }
      }

      String string2 = stringBuilder.toString().replaceAll("^_+|_+$", "");
      if (!string2.isBlank()) {
         return string2;
      } else {
         throw new IllegalArgumentException("Setting id must not be blank");
      }
   }

   public boolean hasDescription() {
      return this.description != null && !this.description.isBlank();
   }

   public String getId() {
      return this.id;
   }

   @Override
   public final void writeConfig(DataOutputStream dataOutputStream) throws IOException {
      this.writeValue(dataOutputStream);
   }

   @Override
   public final void readConfig(DataInputStream dataInputStream) throws IOException {
      this.readValue(dataInputStream);
      if (this.hasKeybind) {
         this.getKeybind().readConfig(dataInputStream);
      }
   }

   public SettingKeybind getKeybind() {
      if (!this.hasKeybind) {
         return null;
      } else {
         if (this.keybind == null) {
            this.keybind = new SettingKeybind(this);
         }

         return this.keybind;
      }
   }

   public boolean isToggle() {
      return this.toggle;
   }

   public String getDisplayName() {
      return this.nameSupplier.get();
   }

   public List<String> getAliases() {
      return this.aliases;
   }

   public String getDescription() {
      return this.description;
   }

   public abstract Setting copy();

   public VisibilityCondition getVisibilityCondition() {
      return this.visibility;
   }

   public final byte[] copyPayload() {
      try {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         this.writeValue(new DataOutputStream(byteArrayOutputStream));
         return byteArrayOutputStream.toByteArray();
      } catch (IOException var3) {
         String string = this.getDisplayName();
         throw new IllegalStateException("Failed to save setting value snapshot for " + string, var3);
      }
   }

   protected abstract void readValue(DataInputStream var1) throws IOException;

   protected abstract void writeValue(DataOutputStream var1) throws IOException;

   public final void restorePayload(byte[] byArray) {
      try {
         byte[] byArray2 = byArray == null ? new byte[0] : byArray;
         this.readValue(new DataInputStream(new ByteArrayInputStream(byArray2)));
      } catch (IOException var4) {
         String string = this.getDisplayName();
         throw new IllegalStateException("Failed to load setting value snapshot for " + string, var4);
      }
   }

   public final boolean hasKeybind() {
      return this.hasKeybind;
   }

   public final boolean isVisible() {
      return this.visibilityChecks.stream().allMatch(Supplier::get) && (this.visibility == null || this.visibility.isActive());
   }

   public List<Supplier<Boolean>> getVisibilityChecks() {
      return this.visibilityChecks;
   }

   public byte[] togglePayload() {
      return this.copyPayload();
   }

   public Supplier<String> getNameSupplier() {
      return this.nameSupplier;
   }
}
