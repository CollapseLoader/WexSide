package ru.wexside.misc;

import java.util.Arrays;
import java.util.List;
import ru.wexside.command.Command;
import ru.wexside.command.CommandUsageException;
import ru.wexside.event.EventBus;
import ru.wexside.event.OutgoingChatEvent;
import ru.wexside.module.misc.CommandCharacterModule;

public class ChatCommandDispatcher {
   private final List<Command> field12;

   public ChatCommandDispatcher(EventBus eventBus, List<Command> list) {
      eventBus.subscribe(OutgoingChatEvent.class, this::member5787);
      this.field12 = list;
   }

   public String getString() {
      return CommandCharacterModule.getString();
   }

   void member5787(OutgoingChatEvent gameEvent15) {
      String string2 = gameEvent15.getString();
      String string;
      if (string2.startsWith(string = this.getString())) {
         gameEvent15.update();
         String[] stringArray = string2.split(" ");

         for(Command command : this.field12) {
            String[] stringArray2 = command.getCommandAliases();
            int n = stringArray2.length;

            for(int i = 0; i < n; ++i) {
               String string3;
               String string4 = string3 = stringArray2[i];
               if (stringArray[0].equalsIgnoreCase(string + string4)) {
                  try {
                     command.execute(Arrays.copyOfRange(stringArray, 1, stringArray.length));
                  } catch (CommandUsageException var15) {
                     String string6 = var15.getMessage();
                     ClientChat.send("Неверный синтаксис команды: " + string6);
                  } catch (RuntimeException var16) {
                     String string7 = var16.getMessage();
                     ClientChat.send("Ошибка выполнения команды: " + string7);
                  }

                  return;
               }
            }
         }

         ClientChat.send("Команда не найдена.");
      }
   }
}
