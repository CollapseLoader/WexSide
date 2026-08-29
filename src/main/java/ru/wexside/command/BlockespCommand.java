package ru.wexside.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2960;
import net.minecraft.class_7923;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BlockEspStore;
import ru.wexside.misc.ClientChat;

public final class BlockespCommand extends Command {
   private static final int slot = -1;

   public BlockespCommand() {
      super("blockesp", "Управление списком блоков для Block ESP", "blockesp", "be");
   }

   @Override
   public String getUsage() {
      return ".blockesp <add <blockId> [hexColor] | remove <blockId> | list | clear>";
   }

   @Override
   public void execute(String... stringArray) throws CommandUsageException {
      if (stringArray.length < 1) {
         throw new CommandUsageException(this);
      } else {
         BlockEspStore blockEspStore2 = WexSideClient.getBlockEspStore();
         if (blockEspStore2 == null) {
            ClientChat.send("BlockEspService недоступен");
         } else {
            String var3 = stringArray[0].toLowerCase(Locale.ROOT);
            switch(var3) {
               case "add":
                  if (stringArray.length < 2) {
                     throw new CommandUsageException(this);
                  }

                  String string = this.process3(stringArray[1]);
                  if (string == null) {
                     String string2 = stringArray[1];
                     ClientChat.send("Неизвестный блок: " + string2);
                     return;
                  }

                  int n = -1;
                  if (stringArray.length >= 3) {
                     Integer n2 = this.process(stringArray[2]);
                     if (n2 == null) {
                        String string3 = stringArray[2];
                        ClientChat.send("Неверный hex-цвет: " + string3 + " (примеры: ff0000, ff00ff00)");
                        return;
                     }

                     n = n2;
                  }

                  boolean bl = blockEspStore2.contains(string);
                  blockEspStore2.put(string, n);
                  String string4 = this.process4(n);
                  String string6 = bl ? "Обновил" : "Добавил";
                  ClientChat.send(string6 + " блок " + string + " (#" + string4 + ")");
                  break;
               case "remove":
               case "del":
               case "rm":
                  if (stringArray.length < 2) {
                     throw new CommandUsageException(this);
                  }

                  String string7 = this.process3(stringArray[1]);
                  String stringR = string7 != null ? string7 : stringArray[1].trim().toLowerCase(Locale.ROOT);
                  if (blockEspStore2.remove(stringR)) {
                     ClientChat.send("Удалил блок " + stringR);
                  } else {
                     ClientChat.send(stringR + " не в списке");
                  }
                  break;
               case "list":
               case "ls":
                  Map<String, Integer> map = blockEspStore2.getBlocks();
                  if (map.isEmpty()) {
                     ClientChat.send("Список блоков пуст");
                  } else {
                     int nMap = map.size();
                     StringBuilder stringBuilder = new StringBuilder("Блоки (" + nMap + "): ");
                     boolean blFirst = true;

                     for(Entry<String, Integer> entry : map.entrySet()) {
                        if (!blFirst) {
                           stringBuilder.append(", ");
                        }

                        stringBuilder.append(entry.getKey()).append(" #").append(this.process4(entry.getValue()));
                        blFirst = false;
                     }

                     ClientChat.send(stringBuilder.toString());
                  }
                  break;
               case "clear":
                  blockEspStore2.clear();
                  ClientChat.send("Список блоков очищен");
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
      } else if (stringArray.length < 1) {
         return List.of();
      } else {
         String string = stringArray[0].toLowerCase(Locale.ROOT);
         if (n == 1 && string.equals("add")) {
            ArrayList<String> arrayList = new ArrayList<>();

            for(class_2960 identifier : class_7923.field_41175.method_10235()) {
               arrayList.add(this.process5(identifier));
            }

            return arrayList;
         } else if (n == 2 && string.equals("add")) {
            return List.of("ffffff", "ff0000", "00ff00", "0000ff", "ffff00", "00ffff", "ff00ff");
         } else if (n == 1 && (string.equals("remove") || string.equals("del") || string.equals("rm"))) {
            BlockEspStore blockEspStore2 = WexSideClient.getBlockEspStore();
            if (blockEspStore2 == null) {
               return List.of();
            } else {
               ArrayList<String> arrayList = new ArrayList<>();

               for(String string2 : blockEspStore2.getBlocks().keySet()) {
                  arrayList.add(this.process2(string2));
               }

               return arrayList;
            }
         } else {
            return List.of();
         }
      }
   }

   private Integer process(String string) {
      String string2 = string.trim();
      if (string2.startsWith("#")) {
         string2 = string2.substring(1);
      } else if (string2.startsWith("0x") || string2.startsWith("0X")) {
         string2 = string2.substring(2);
      }

      if (string2.length() != 6 && string2.length() != 8) {
         return null;
      } else {
         try {
            long l = Long.parseLong(string2, 16);
            return string2.length() == 6 ? (int)(4278190080L | l) : (int)l;
         } catch (NumberFormatException var5) {
            return null;
         }
      }
   }

   private String process2(String string) {
      String string2 = "minecraft:";
      return string.startsWith(string2) ? string.substring(string2.length()) : string;
   }

   private String process3(String string) {
      class_2960 identifier = class_2960.method_12829(string.trim().toLowerCase(Locale.ROOT));
      if (identifier == null) {
         return null;
      } else {
         Optional optional = class_7923.field_41175.method_17966(identifier);
         return !optional.isEmpty() && optional.get() != class_2246.field_10124
            ? class_7923.field_41175.method_10221((class_2248)optional.get()).toString()
            : null;
      }
   }

   private String process4(int n) {
      return String.format("%08X", n);
   }

   private String process5(class_2960 identifier) {
      return "minecraft".equals(identifier.method_12836()) ? identifier.method_12832() : identifier.toString();
   }
}
