package ru.wexside.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_124;
import ru.wexside.WexSideClient;
import ru.wexside.input.InputBindings;
import ru.wexside.misc.ClientChat;
import ru.wexside.misc.MacroDefinition;
import ru.wexside.misc.MacroManager;
import ru.wexside.misc.MacroType;

public final class MacroCommand extends Command {
   private static final String string21 = " ";

   @Override
   public String getUsage() {
      return ".macro <add <chat|command> <name> <key> \"<message>\" | remove <name> | list | clear>";
   }

   @Override
   public void execute(String... stringArray) throws CommandUsageException {
      if (stringArray.length < 1) {
         throw new CommandUsageException(this);
      } else {
         MacroManager macroManager2 = WexSideClient.getMacroManager();
         if (macroManager2 == null) {
            ClientChat.send("MacroService недоступен");
         } else {
            String var3 = stringArray[0].toLowerCase(Locale.ROOT);
            switch(var3) {
               case "add":
                  if (stringArray.length < 5) {
                     throw new CommandUsageException(this);
                  }

                  MacroType macroType = this.parseType(stringArray[1]);
                  if (macroType == null) {
                     String string = String.valueOf(class_124.field_1070);
                     String string2 = String.valueOf(class_124.field_1061);
                     String string3 = String.valueOf(class_124.field_1070);
                     String string4 = String.valueOf(class_124.field_1061);
                     ClientChat.send("Тип должен быть " + string4 + "chat" + string3 + " или " + string2 + "command" + string + ".");
                     return;
                  }

                  String string = stringArray[2];
                  int n = InputBindings.keyCode(stringArray[3].toUpperCase(Locale.ROOT));
                  if (n == -1) {
                     ClientChat.send("Кнопка не найдена.");
                     return;
                  }

                  String string5 = this.process2(stringArray, 4);
                  if (string5 == null || string5.isBlank()) {
                     String string6 = String.valueOf(class_124.field_1070);
                     String string7 = String.valueOf(class_124.field_1061);
                     ClientChat.send("Сообщение должно быть в кавычках, например " + string7 + "\"im win ezz\"" + string6 + ".");
                     return;
                  }

                  if (macroManager2.process(string)) {
                     String string8 = String.valueOf(class_124.field_1070);
                     String string10 = String.valueOf(class_124.field_1061);
                     ClientChat.send("Макрос " + string10 + string + string8 + " уже есть в списке макросов.");
                     return;
                  }

                  macroManager2.process3(string, string5, n, macroType);
                  String string11 = this.describeType(macroType);
                  String string12 = String.valueOf(class_124.field_1070);
                  String string14 = String.valueOf(class_124.field_1061);
                  ClientChat.send("Макрос " + string14 + string + string12 + " (" + string11 + ") добавлен в список макросов.");
                  break;
               case "remove":
               case "del":
               case "rm":
                  if (stringArray.length != 2) {
                     throw new CommandUsageException(this);
                  }

                  String stringR = stringArray[1];
                  if (macroManager2.process2(stringR)) {
                     String string15 = String.valueOf(class_124.field_1070);
                     String string17 = String.valueOf(class_124.field_1061);
                     ClientChat.send("Макрос " + string17 + stringR + string15 + " убран из списка макросов.");
                  } else {
                     String string18 = String.valueOf(class_124.field_1070);
                     String string20 = String.valueOf(class_124.field_1061);
                     ClientChat.send("Макроса " + string20 + stringR + string18 + " нет в списке макросов.");
                  }
                  break;
               case "clear":
                  macroManager2.update();
                  ClientChat.send("Список макросов очищен.");
                  break;
               case "list":
               case "ls":
                  List<MacroDefinition> list = macroManager2.getList();
                  if (list.isEmpty()) {
                     ClientChat.send("Список макросов пуст.");
                  } else {
                     int nList = list.size();
                     ClientChat.send("Список макросов (" + nList + "):");

                     for(MacroDefinition macroDefinition : list) {
                        ClientChat.send(this.process4(macroDefinition));
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
      } else if (stringArray.length < 1) {
         return List.of();
      } else {
         String string = stringArray[0].toLowerCase(Locale.ROOT);
         if (n == 1 && string.equals("add")) {
            return List.of("chat", "command");
         } else if (n == 1 && (string.equals("remove") || string.equals("del") || string.equals("rm"))) {
            MacroManager macroManager2 = WexSideClient.getMacroManager();
            if (macroManager2 == null) {
               return List.of();
            } else {
               ArrayList<String> arrayList = new ArrayList<>();

               for(MacroDefinition macroDefinition : macroManager2.getList()) {
                  arrayList.add(macroDefinition.getName());
               }

               return arrayList;
            }
         } else {
            return n == 3 && string.equals("add") ? InputBindings.keyNames() : List.of();
         }
      }
   }

   private MacroType parseType(String string) {
      if (string == null) {
         return null;
      } else {
         String var2 = string.toLowerCase(Locale.ROOT);

         return switch(var2) {
            case "chat", "чат", "msg", "message" -> MacroType.CHAT;
            case "command", "cmd", "команда", "комманда" -> MacroType.COMMAND;
            default -> null;
         };
      }
   }

   private String process2(String[] stringArray, int n) {
      StringBuilder stringBuilder = new StringBuilder();

      for(int i = n; i < stringArray.length; ++i) {
         if (i > n) {
            stringBuilder.append(" ");
         }

         stringBuilder.append(stringArray[i]);
      }

      String string = stringBuilder.toString().trim();
      int n2;
      return string.length() >= 2 && string.charAt(0) == '"' && (n2 = string.lastIndexOf(34)) > 0 ? string.substring(1, n2) : string;
   }

   private String describeType(MacroType macroType) {
      return macroType == MacroType.COMMAND ? "команда" : "чат";
   }

   private String process4(MacroDefinition macroDefinition) {
      String string = InputBindings.keyName(macroDefinition.getKeyCode());
      String string2 = macroDefinition.getMessage();
      String string3 = this.describeType(macroDefinition.getType());
      String string5 = String.valueOf(class_124.field_1070);
      String string6 = macroDefinition.getName();
      String string7 = String.valueOf(class_124.field_1061);
      return string7 + string6 + string5 + " [" + string + "] (" + string3 + ") " + string2;
   }

   public MacroCommand() {
      super("macro", "Управление макросами (текст/команда по кнопке)", "macro", "macros");
   }
}
