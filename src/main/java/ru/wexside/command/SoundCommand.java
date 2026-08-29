package ru.wexside.command;

import java.util.List;
import java.util.Locale;
import net.minecraft.class_124;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BlockedSoundList;
import ru.wexside.misc.ClientChat;

public final class SoundCommand extends Command {
   public SoundCommand() {
      super("sound", "Управление заглушаемыми звуками (кастомные/серверные id)", "sound", "sounds", "звук", "звуки");
   }

   @Override
   public String getUsage() {
      return ".sound <add <id> | remove <id> | list | clear>";
   }

   @Override
   public void execute(String... stringArray) throws CommandUsageException {
      if (stringArray.length < 1) {
         throw new CommandUsageException(this);
      } else {
         BlockedSoundList blockedSoundList2 = WexSideClient.getBlockedSoundList();
         if (blockedSoundList2 == null) {
            ClientChat.send("SoundRemoverService недоступен");
         } else {
            String var3 = stringArray[0].toLowerCase(Locale.ROOT);
            switch(var3) {
               case "add":
                  if (stringArray.length != 2) {
                     throw new CommandUsageException(this);
                  }

                  String string = this.process(stringArray[1]);
                  if (string == null) {
                     String string2 = stringArray[1];
                     String string3 = String.valueOf(class_124.field_1061);
                     ClientChat.send("Неверный id звука: " + string3 + string2);
                     return;
                  }

                  boolean bl = blockedSoundList2.add(string);
                  this.setString(string);
                  if (bl) {
                     String string4 = String.valueOf(class_124.field_1070);
                     String string6 = String.valueOf(class_124.field_1061);
                     ClientChat.send("Звук " + string6 + string + string4 + " добавлен в заглушённые.");
                  } else {
                     String string7 = String.valueOf(class_124.field_1070);
                     String string9 = String.valueOf(class_124.field_1061);
                     ClientChat.send("Звук " + string9 + string + string7 + " уже заглушён.");
                  }
                  break;
               case "remove":
               case "del":
               case "rm":
                  if (stringArray.length != 2) {
                     throw new CommandUsageException(this);
                  }

                  String stringR = this.process(stringArray[1]);
                  if (stringR != null && blockedSoundList2.remove(stringR)) {
                     String string10 = String.valueOf(class_124.field_1070);
                     String string12 = String.valueOf(class_124.field_1061);
                     ClientChat.send("Звук " + string12 + stringR + string10 + " убран из заглушённых.");
                  } else {
                     String string13 = String.valueOf(class_124.field_1070);
                     String string14 = stringArray[1];
                     String string15 = String.valueOf(class_124.field_1061);
                     ClientChat.send("Звука " + string15 + string14 + string13 + " нет в списке.");
                  }
                  break;
               case "clear":
                  blockedSoundList2.clear();
                  ClientChat.send("Список заглушённых звуков очищен.");
                  break;
               case "list":
               case "ls":
                  List<String> list = blockedSoundList2.getBlockedSounds();
                  if (list.isEmpty()) {
                     ClientChat.send("Список заглушённых звуков пуст.");
                  } else {
                     int n = list.size();
                     ClientChat.send("Заглушённые звуки (" + n + "):");

                     for(String string16 : list) {
                        String string17 = String.valueOf(class_124.field_1061);
                        ClientChat.send(string17 + string16);
                     }
                  }
                  break;
               default:
                  throw new CommandUsageException(this);
            }
         }
      }
   }

   @Override
   public List<String> complete(int n, String[] stringArray) {
      if (n == 0) {
         return List.of("add", "remove", "list", "clear");
      } else {
         String string;
         if (n == 1
            && stringArray.length >= 1
            && ((string = stringArray[0].toLowerCase(Locale.ROOT)).equals("remove") || string.equals("del") || string.equals("rm"))) {
            BlockedSoundList blockedSoundList2 = WexSideClient.getBlockedSoundList();
            return blockedSoundList2 == null ? List.of() : blockedSoundList2.getBlockedSounds();
         } else {
            return List.of();
         }
      }
   }

   private String process(String string) {
      if (string != null && !string.isBlank()) {
         class_2960 identifier = class_2960.method_12829(string.trim().toLowerCase(Locale.ROOT));
         return identifier == null ? null : identifier.toString();
      } else {
         return null;
      }
   }

   private void setString(String string) {
      class_2960 identifier = class_2960.method_12829(string);
      if (identifier != null) {
         class_310 mc = class_310.method_1551();
         mc.method_1483().method_4875(identifier, null);
      }
   }
}
