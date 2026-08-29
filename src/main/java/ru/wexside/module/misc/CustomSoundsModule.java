package ru.wexside.module.misc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.class_1113;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_1113.class_1114;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.setting.NumberUnit;
import ru.wexside.util.CustomSoundLibrary;

public final class CustomSoundsModule extends Module implements ConfigSerializable {
   static volatile CustomSoundsModule customSoundsModule2;
   private final Map<String, String> displayNames = new LinkedHashMap<>();
   private final Map<String, ModeSetting> soundSettings = new LinkedHashMap<>();
   private final Map<String, Map<String, String>> soundFilesByCategory = new LinkedHashMap<>();
   private final Map<String, NumberSetting> volumeSettings = new LinkedHashMap<>();
   private final MultiSelectSetting categories;
   private final BooleanSetting enabledSetting;
   private final CustomSoundLibrary soundLibrary;

   public CustomSoundsModule(EventBus eventBus) {
      super(eventBus, "custom_sounds", "Custom Sounds", "Замена ванильных звуков своими файлами", ModuleCategory.valueOf("MISC"));
      customSoundsModule2 = this;
      this.soundLibrary = this.createSoundLibrary();
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Кастомные звуки вместо ванильных")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      MultiSelectSetting categoriesSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("Нет файлов")
            .selectAll(false)
            .optionListEnabled(false)
            .name("Categories")
            .id("categories")
            .description("Какие звуки подменять"))
         .build();
      categoriesSetting.setOptions(new String[0]);
      this.categories = categoriesSetting;
      this.registerSetting(categoriesSetting);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.refreshSoundSettings());
   }

   private CustomSoundLibrary createSoundLibrary() {
      WexSideClient client = WexSideClient.getInstance();
      if (client != null && client.getConfigDirectory() != null) {
         try {
            return new CustomSoundLibrary(client.getConfigDirectory().resolve("client").resolve("sounds"));
         } catch (IOException var3) {
            return null;
         }
      } else {
         return null;
      }
   }

   private void refreshSoundSettings() {
      if (this.soundLibrary != null) {
         this.soundLibrary.update();

         for(String soundId : this.soundLibrary.getList()) {
            if (!this.displayNames.containsKey(soundId)) {
               String displayName = this.uniqueDisplayName(formatDisplayName(soundId));
               this.displayNames.put(soundId, displayName);
               this.registerSoundSettings(soundId, displayName);
            }
         }

         ArrayList<String> availableCategories = new ArrayList<>();

         for(Entry<String, String> entry : this.displayNames.entrySet()) {
            String soundId = entry.getKey();
            Map<String, String> files = buildDisplayNameMap(this.soundLibrary.process5(soundId));
            this.soundFilesByCategory.put(soundId, files);
            ModeSetting modeSetting = this.soundSettings.get(soundId);
            if (modeSetting != null) {
               modeSetting.setOptions(files.keySet().toArray(new String[0]));
            }

            if (!files.isEmpty()) {
               availableCategories.add(entry.getValue());
            }
         }

         String[] stringArray;
         if (availableCategories.isEmpty()) {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = "Нет файлов";
         } else {
            stringArray = availableCategories.toArray(new String[0]);
         }

         this.categories.setOptions(stringArray);
      }
   }

   private void registerSoundSettings(String soundId, String displayName) {
      String settingId = sanitizeSettingId(soundId);
      Map<String, String> files = buildDisplayNameMap(this.soundLibrary.process5(soundId));
      this.soundFilesByCategory.put(soundId, files);
      String defaultFile = files.isEmpty() ? "" : files.keySet().iterator().next();
      ModeSetting soundSetting = ((ModeSettingBuilder)((ModeSettingBuilder)((ModeSettingBuilder)((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
                     .id("sound_" + settingId))
                  .name(displayName + " Sound"))
               .description("Файл для: " + displayName))
            .options(files.keySet().toArray(new String[0]))
            .defaultOption(defaultFile)
            .visibleWhen(() -> this.isCategoryEnabled(soundId)))
         .build();
      NumberSetting volumeSetting = ((NumberSettingBuilder)((NumberSettingBuilder)((NumberSettingBuilder)((NumberSettingBuilder)NumberSetting.builder()
                     .id("volume_" + settingId))
                  .name(displayName + " Volume"))
               .description("Громкость: " + displayName))
            .range(1.0, 100.0)
            .defaultValue(100.0)
            .precision(0)
            .formatter(NumberUnit.PERCENT)
            .visibleWhen(() -> this.isCategoryEnabled(soundId)))
         .build();
      this.soundSettings.put(soundId, soundSetting);
      this.volumeSettings.put(soundId, volumeSetting);
      this.registerSetting(soundSetting);
      this.registerSetting(volumeSetting);
   }

   private String uniqueDisplayName(String baseName) {
      if (!this.displayNames.containsValue(baseName)) {
         return baseName;
      } else {
         int suffix = 2;

         while(this.displayNames.containsValue(baseName + " [" + suffix + "]")) {
            ++suffix;
         }

         return baseName + " [" + suffix + "]";
      }
   }

   private static String sanitizeSettingId(String soundId) {
      return soundId.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "_");
   }

   private static String formatDisplayName(String soundId) {
      String[] parts = soundId.split("\\.");
      String label = parts.length >= 2 ? parts[parts.length - 2] + " " + parts[parts.length - 1] : parts[parts.length - 1];
      return titleCase(label.replace('_', ' '));
   }

   private static String titleCase(String value) {
      StringBuilder builder = new StringBuilder(value.length());
      boolean capitalizeNext = true;

      for(int index = 0; index < value.length(); ++index) {
         char character = value.charAt(index);
         if (capitalizeNext && Character.isLetter(character)) {
            builder.append(Character.toUpperCase(character));
            capitalizeNext = false;
         } else {
            builder.append(character);
         }

         if (character == ' ' || character == '-') {
            capitalizeNext = true;
         }
      }

      return builder.toString();
   }

   private boolean isCategoryEnabled(String soundId) {
      String displayName = this.displayNames.get(soundId);
      return displayName != null && this.categories.getSelectedOptions().contains(displayName);
   }

   private String resolveReplacementFile(String soundId) {
      Map<String, String> files = this.soundFilesByCategory.get(soundId);
      ModeSetting modeSetting = this.soundSettings.get(soundId);
      if (files != null && !files.isEmpty()) {
         String selected;
         if (modeSetting != null && (selected = files.get(modeSetting.getSelectedOption())) != null && this.soundLibrary.process7(selected)) {
            return selected;
         } else {
            List<String> available = this.soundLibrary.process5(soundId);
            return available.isEmpty() ? null : available.get(0);
         }
      } else {
         return null;
      }
   }

   private float volumeFor(String soundId) {
      NumberSetting volumeSetting = this.volumeSettings.get(soundId);
      double percent = volumeSetting == null ? 100.0 : volumeSetting.getValue();
      return (float)Math.max(0.0, Math.min(1.0, percent / 100.0));
   }

   public static boolean compute8(class_1113 instance, long seed) {
      CustomSoundsModule module = customSoundsModule2;
      if (module != null && module.soundLibrary != null && instance != null && module.enabledSetting.isEnabled()) {
         class_2960 soundId = instance.method_4775();
         String normalizedId = "minecraft".equals(soundId.method_12836()) ? soundId.method_12832() : soundId.toString();
         if (module.soundLibrary.process2(normalizedId) && module.isCategoryEnabled(normalizedId)) {
            String replacement = module.resolveReplacementFile(normalizedId);
            if (replacement == null) {
               return false;
            } else {
               float volume = module.volumeFor(normalizedId);
               class_310 client = class_310.method_1551();
               class_243 cameraPos = client.field_1773.method_19418().method_71156();
               boolean positional = cameraPos != null && !instance.method_4787() && instance.method_4777() != class_1114.field_5478;
               if (positional) {
                  module.soundLibrary
                     .process6(
                        replacement,
                        instance.method_4784(),
                        instance.method_4779(),
                        instance.method_4778(),
                        cameraPos.field_1352,
                        cameraPos.field_1351,
                        cameraPos.field_1350,
                        volume,
                        seed
                     );
               } else {
                  module.soundLibrary.process(replacement, volume, seed);
               }

               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static Map<String, String> buildDisplayNameMap(List<String> files) {
      LinkedHashMap<String, String> mapped = new LinkedHashMap<>();

      for(String file : files) {
         int slash = file.indexOf(47);
         String baseName = slash >= 0 ? file.substring(slash + 1) : file;
         Object displayName = baseName;
         int suffix = 2;

         while(mapped.containsKey(displayName)) {
            displayName = baseName + " (" + suffix++ + ")";
         }

         mapped.put((String)displayName, file);
      }

      return mapped;
   }
}
