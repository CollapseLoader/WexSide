package ru.wexside.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import ru.wexside.misc.VisibilityCondition;

public abstract class SettingBuilder {
   private String description = "";
   private boolean toggle;
   private Supplier<String> nameSupplier;
   private boolean keybind;
   private final List<Supplier<Boolean>> visibilityChecks = new ArrayList<>();
   private String id;
   private final List<String> aliases = new ArrayList<>();
   private VisibilityCondition visibilityCondition;
   private boolean dynamicName;

   public SettingBuilder name(String name) {
      this.nameSupplier = () -> name;
      return this.self();
   }

   public SettingBuilder id(String id) {
      this.id = id;
      return this.self();
   }

   public SettingBuilder description(String description) {
      this.description = description;
      return this.self();
   }

   public SettingBuilder withKeybind() {
      this.keybind = true;
      return this.self();
   }

   public SettingBuilder aliases(String... aliases) {
      if (aliases == null) {
         return this.self();
      } else {
         for(String alias : aliases) {
            if (alias != null && !alias.isBlank() && !this.aliases.contains(alias)) {
               this.aliases.add(alias);
            }
         }

         return this.self();
      }
   }

   public SettingBuilder visibleWhen(Supplier<Boolean> condition) {
      this.visibilityChecks.add(condition);
      return this.self();
   }

   public SettingBuilder visibility(VisibilityCondition visibilityCondition) {
      this.visibilityCondition = visibilityCondition;
      return this.self();
   }

   public SettingBuilder keybind(boolean enabled) {
      this.keybind = enabled;
      return this.self();
   }

   public SettingBuilder dynamicName(Supplier<String> nameSupplier) {
      this.nameSupplier = nameSupplier;
      this.dynamicName = true;
      return this.self();
   }

   protected SettingBuilder self() {
      return this;
   }

   public SettingBuilder toggle(boolean enabled) {
      this.toggle = enabled;
      return this.self();
   }

   public SettingBuilder toggle() {
      this.toggle = true;
      return this.self();
   }

   final Supplier<String> nameSupplier() {
      return this.nameSupplier;
   }

   final String settingId() {
      return this.id;
   }

   final String settingDescription() {
      return this.description;
   }

   final List<String> settingAliases() {
      return this.aliases;
   }

   final List<Supplier<Boolean>> visibilityChecks() {
      return this.visibilityChecks;
   }

   final VisibilityCondition visibilityRule() {
      return this.visibilityCondition;
   }

   final boolean createsToggle() {
      return this.toggle;
   }

   final boolean createsKeybind() {
      return this.keybind;
   }

   final boolean hasDynamicName() {
      return this.dynamicName;
   }
}
