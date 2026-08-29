package ru.wexside.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.misc.ClientChat;
import ru.wexside.misc.Waypoint;
import ru.wexside.misc.WaypointStore;

public final class WaypointCommand extends Command {
   public WaypointCommand() {
      super("waypoint", "Управление вейпоинтами", "waypoint", "way");
   }

   @Override
   public String getUsage() {
      return ".waypoint <add <name> <x> <y> <z> | remove <name> | clear>";
   }

   @Override
   public void execute(String... stringArray) throws CommandUsageException {
      if (stringArray.length < 1) {
         throw new CommandUsageException(this);
      } else {
         WaypointStore waypointStore2 = WexSideClient.getWaypointStore();
         if (waypointStore2 == null) {
            ClientChat.send("WaypointService недоступен");
         } else {
            String var3 = stringArray[0].toLowerCase(Locale.ROOT);
            switch(var3) {
               case "add":
                  if (stringArray.length != 5) {
                     throw new CommandUsageException(this);
                  }

                  String string = stringArray[1];

                  int n2;
                  int n3;
                  int n;
                  try {
                     n3 = Integer.parseInt(stringArray[2]);
                     n2 = Integer.parseInt(stringArray[3]);
                     n = Integer.parseInt(stringArray[4]);
                  } catch (NumberFormatException var13) {
                     throw new CommandUsageException(this);
                  }

                  waypointStore2.add(new Waypoint(string, n3, n2, n));
                  ClientChat.send("Вейпоинт " + string + " добавлен (" + n3 + ", " + n2 + ", " + n + ")");
                  break;
               case "remove":
               case "del":
               case "rm":
                  if (stringArray.length != 2) {
                     throw new CommandUsageException(this);
                  }

                  String stringR = stringArray[1];
                  if (waypointStore2.removeByName(stringR)) {
                     ClientChat.send("Вейпоинт " + stringR + " удалён");
                  } else {
                     ClientChat.send("Вейпоинт " + stringR + " не найден");
                  }
                  break;
               case "clear":
                  waypointStore2.clear();
                  ClientChat.send("Список вейпоинтов очищен");
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
         return List.of("add", "remove", "clear");
      } else if (stringArray.length < 1) {
         return List.of();
      } else {
         String string = stringArray[0].toLowerCase(Locale.ROOT);
         if (n == 1 && (string.equals("remove") || string.equals("del") || string.equals("rm"))) {
            WaypointStore waypointStore2 = WexSideClient.getWaypointStore();
            if (waypointStore2 == null) {
               return List.of();
            } else {
               ArrayList<String> arrayList = new ArrayList<>();

               for(Waypoint waypoint2 : waypointStore2.getWaypoints()) {
                  arrayList.add(waypoint2.name());
               }

               return arrayList;
            }
         } else if (string.equals("add") && n >= 2 && n <= 4) {
            class_746 player2 = class_310.method_1551().field_1724;
            if (player2 == null) {
               return List.of();
            } else {
               int n2 = switch(n) {
                  case 2 -> player2.method_31477();
                  case 3 -> player2.method_31478();
                  default -> player2.method_31479();
               };
               return List.of(String.valueOf(n2));
            }
         } else {
            return List.of();
         }
      }
   }
}
