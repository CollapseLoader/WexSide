package ru.wexside.module.misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_1113;
import net.minecraft.class_1140;
import net.minecraft.class_1144;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_5250;
import net.minecraft.class_2558.class_10609;
import net.minecraft.class_2568.class_10613;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.BlockedSoundList;
import ru.wexside.misc.ClientChat;
import ru.wexside.misc.SoundMapAccessor;
import ru.wexside.misc.SoundSystemAccessor;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;

public final class SoundRemoverModule extends Module implements ConfigSerializable {
   static volatile SoundRemoverModule soundRemoverModule2;
   private static final Map<String, String[]> SOUND_GROUPS = buildSoundGroups();
   private final BooleanSetting enabledSetting;
   private final MultiSelectSetting groups;
   private final BooleanSetting logSounds;
   private final Set<String> loggedSoundIds = new HashSet<>();
   private String lastSyncSignature;

   public SoundRemoverModule(EventBus eventBus) {
      super(
         eventBus,
         "sound_remover",
         "Sound Remover",
         "Заглушает любые звуки по отдельности: группы ванильных + кастомные серверные",
         ModuleCategory.valueOf("MISC")
      );
      soundRemoverModule2 = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Заглушать выбранные звуки")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      MultiSelectSetting groupsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options(
               "Взрывы",
               "Фейерверки",
               "Опыт и уровни",
               "Урон",
               "Смерть",
               "Эндер Дракон",
               "Варден",
               "Визер",
               "Гаст/огненный шар",
               "Молнии и гроза",
               "Погода и дождь",
               "Шаги",
               "Интерфейс",
               "Голоса мобов",
               "Двери и люки",
               "Сундуки/хранилища",
               "Порталы"
            )
            .selectAll(false)
            .optionListEnabled(false)
            .name("Groups")
            .id("groups")
            .description("Группы ванильных звуков для заглушения")
            .aliases("groups", "группы"))
         .build();
      this.groups = groupsSetting;
      this.registerSetting(groupsSetting);
      this.logSounds = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Log sounds")
            .id("log_sounds")
            .description("Печатать в чат каждый слышимый звук с кнопкой [заглушить] — для поиска id кастомных серверных звуков"))
         .build();
      this.registerSetting(this.logSounds);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, this::onClientTick);
   }

   private void onClientTick(ClientTickEvent event) {
      String enabledFlag = this.enabledSetting.isEnabled() ? "1" : "0";
      String signature = enabledFlag + "|" + String.join(",", this.groups.getSelectedOptions());
      if (!signature.equals(this.lastSyncSignature)) {
         this.lastSyncSignature = signature;
         if (this.enabledSetting.isEnabled()) {
            this.muteActiveSounds();
         }
      }
   }

   private void muteActiveSounds() {
      class_310 client = class_310.method_1551();
      class_1144 soundManager = client.method_1483();
      if (soundManager instanceof SoundSystemAccessor) {
         SoundSystemAccessor callback54 = (SoundSystemAccessor)soundManager;
         class_1140 soundEngine = callback54.getSoundSystem();
         if (soundEngine instanceof SoundMapAccessor) {
            SoundMapAccessor callback38 = (SoundMapAccessor)soundEngine;
            Map<class_1113, ?> activeSounds = callback38.getMap();
            if (activeSounds != null && !activeSounds.isEmpty()) {
               ArrayList<class_1113> instances;
               try {
                  instances = new ArrayList();

                  for(class_1113 key : activeSounds.keySet()) {
                     if (key instanceof class_1113) {
                        instances.add(key);
                     }
                  }
               } catch (RuntimeException var12) {
                  return;
               }

               HashSet<class_2960> blockedIds = new HashSet();

               for(class_1113 instance : instances) {
                  class_2960 id = instance.method_4775();
                  if (id != null && shouldBlock(id)) {
                     blockedIds.add(id);
                  }
               }

               for(class_2960 id : blockedIds) {
                  soundManager.method_4875(id, null);
               }
            }
         }
      }
   }

   public static void handle(class_2960 soundId, boolean muted) {
      SoundRemoverModule module = soundRemoverModule2;
      if (module != null && soundId != null && module.enabledSetting.isEnabled()) {
         if (!module.logSounds.isEnabled()) {
            module.loggedSoundIds.clear();
         } else {
            String normalized = soundId.toString().toLowerCase(Locale.ROOT);
            if (module.loggedSoundIds.add(normalized)) {
               class_5250 message = buildLogMessage(soundId.toString(), muted);
               class_310.method_1551().execute(() -> ClientChat.send(message));
            }
         }
      }
   }

   public static boolean compute4(class_2960 soundId) {
      SoundRemoverModule module = soundRemoverModule2;
      if (module != null && soundId != null && module.enabledSetting.isEnabled()) {
         String normalized = soundId.toString().toLowerCase(Locale.ROOT);
         if (matchesSelectedGroups(module.groups.getSelectedOptions(), normalized)) {
            return true;
         } else {
            BlockedSoundList customBlocked = WexSideClient.getBlockedSoundList();
            return customBlocked != null && customBlocked.contains(normalized);
         }
      } else {
         return false;
      }
   }

   private static boolean matchesSelectedGroups(List<String> groups, String soundId) {
      if (groups != null && !groups.isEmpty()) {
         for(String group : groups) {
            if (matchesGroup(group, soundId)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static boolean matchesGroup(String group, String soundId) {
      String[] fragments = (String[])SOUND_GROUPS.get(group);
      return fragments != null && containsAny(soundId, fragments);
   }

   private static boolean containsAny(String soundId, String... fragments) {
      for(String fragment : fragments) {
         if (soundId.contains(fragment)) {
            return true;
         }
      }

      return false;
   }

   private static class_5250 buildLogMessage(String soundId, boolean muted) {
      class_5250 message = class_2561.method_43470(soundId).method_27692(class_124.field_1080);
      message.method_10852(class_2561.method_43470(" "));
      if (muted) {
         message.method_10852(clickableText("[вернуть]", class_124.field_1060, ".sound remove " + soundId, "Кликни, чтобы вернуть звук"));
      } else {
         message.method_10852(clickableText("[заглушить]", class_124.field_1054, ".sound add " + soundId, "Кликни, чтобы заглушить звук"));
      }

      return message;
   }

   private static class_5250 clickableText(String label, class_124 color, String command, String hover) {
      return class_2561.method_43470(label)
         .method_10862(
            class_2583.field_24360
               .method_10977(color)
               .method_30938(true)
               .method_10958(new class_10609(command))
               .method_10949(new class_10613(class_2561.method_43470(hover)))
         );
   }

   private static boolean shouldBlock(class_2960 soundId) {
      return compute4(soundId);
   }

   private static Map<String, String[]> buildSoundGroups() {
      LinkedHashMap<String, String[]> groups = new LinkedHashMap<>();
      groups.put("Взрывы", new String[]{"explode", "explosion", "tnt", "creeper", "blast"});
      groups.put("Фейерверки", new String[]{"firework", "fireworks"});
      groups.put("Опыт и уровни", new String[]{"experience", "levelup", "orb.pickup", "random.levelup"});
      groups.put("Урон", new String[]{"hurt", "damage", "fall", "hit", "injured"});
      groups.put("Смерть", new String[]{"death", "die"});
      groups.put("Эндер Дракон", new String[]{"dragon", "enderdragon"});
      groups.put("Варден", new String[]{"warden"});
      groups.put("Визер", new String[]{"wither"});
      groups.put("Гаст/огненный шар", new String[]{"ghast", "fireball", "blaze"});
      groups.put("Молнии и гроза", new String[]{"thunder", "lightning"});
      groups.put("Погода и дождь", new String[]{"weather", "rain", "ambient.weather"});
      groups.put("Шаги", new String[]{"step", "footstep", "walk"});
      groups.put("Интерфейс", new String[]{"ui.", "click", "button", "inventory", "screen", "toast"});
      groups.put("Голоса мобов", new String[]{"entity.", "mob", "ambient"});
      groups.put("Двери и люки", new String[]{"door", "trapdoor", "fence_gate", "gate"});
      groups.put("Сундуки/хранилища", new String[]{"chest", "shulker", "barrel", "ender_chest"});
      groups.put("Порталы", new String[]{"portal", "warp"});
      return Map.copyOf(groups);
   }
}
