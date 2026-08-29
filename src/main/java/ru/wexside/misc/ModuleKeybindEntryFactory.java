package ru.wexside.misc;

import java.util.ArrayList;
import java.util.List;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleManager;
import ru.wexside.setting.BindSetting;
import ru.wexside.setting.Setting;
import ru.wexside.setting.SettingKeybind;
import ru.wexside.util.EspFeatureRegistry;
import ru.wexside.util.ModuleKeybindGroup;

public final class ModuleKeybindEntryFactory {
   private ModuleKeybindEntryFactory() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   private static List<KeybindDescriptor> process(Module module) {
      ArrayList<KeybindDescriptor> arrayList = new ArrayList<>();

      for(Setting setting : module.getSettings()) {
         if (setting instanceof BindSetting bindSetting) {
            arrayList.add(KeybindDescriptor.process2(bindSetting));
         }

         ConfigSerializable configSerializable;
         if (setting.hasKeybind() && (configSerializable = setting.getKeybind()) != null) {
            arrayList.add(KeybindDescriptor.process3((SettingKeybind)configSerializable));
         }
      }

      return arrayList;
   }

   public static List<ModuleKeybindGroup> process2(ModuleManager moduleManager) {
      ArrayList<ModuleKeybindGroup> arrayList = new ArrayList<>();
      ArrayList<Module> arrayList2 = new ArrayList<>(moduleManager.getModules());
      EspFeatureRegistry espFeatures = WexSideClient.getEspFeatureRegistry();
      if (espFeatures != null) {
         arrayList2.addAll(espFeatures.getModules());
      }

      for(Module module : arrayList2) {
         List<KeybindDescriptor> list = process(module);
         if (!list.isEmpty()) {
            arrayList.add(new ModuleKeybindGroup(module, list));
         }
      }

      return arrayList;
   }
}
