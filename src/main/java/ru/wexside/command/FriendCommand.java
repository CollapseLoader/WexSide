package ru.wexside.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import ru.wexside.WexSideClient;
import ru.wexside.misc.ClientChat;
import ru.wexside.misc.FriendList;

public class FriendCommand extends Command {
   public FriendCommand() {
      super("friend", "Управление списком друзей", "friend", "fr", "frnd");
   }

   @Override
   public String getUsage() {
      return ".friend <add|remove|list|clear> [name]";
   }

   @Override
   public void execute(String... stringArray) throws CommandUsageException {
      if (stringArray.length < 1) {
         throw new CommandUsageException(this);
      } else {
         FriendList friendList2 = WexSideClient.getFriends();
         if (friendList2 == null) {
            ClientChat.send("FriendService недоступен");
         } else {
            String var3 = stringArray[0].toLowerCase();
            switch(var3) {
               case "add":
                  if (stringArray.length < 2) {
                     throw new CommandUsageException(this);
                  }

                  if (friendList2.add(stringArray[1])) {
                     String string = stringArray[1];
                     ClientChat.send("Добавил " + string + " в друзья");
                  } else {
                     String string = stringArray[1];
                     ClientChat.send(string + " уже в друзьях");
                  }
                  break;
               case "remove":
               case "del":
               case "rm":
                  if (stringArray.length < 2) {
                     throw new CommandUsageException(this);
                  }

                  if (friendList2.remove(stringArray[1])) {
                     String string = stringArray[1];
                     ClientChat.send("Удалил " + string + " из друзей");
                  } else {
                     String string = stringArray[1];
                     ClientChat.send(string + " не в друзьях");
                  }
                  break;
               case "list":
               case "ls":
                  Collection<String> collection = friendList2.getNames();
                  if (collection.isEmpty()) {
                     ClientChat.send("Список друзей пуст");
                  } else {
                     String string = String.join(", ", collection);
                     int n = collection.size();
                     ClientChat.send("Друзья (" + n + "): " + string);
                  }
                  break;
               case "clear":
                  friendList2.clear();
                  ClientChat.send("Список друзей очищен");
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
         if (n == 1 && stringArray.length >= 1) {
            String string = stringArray[0].toLowerCase(Locale.ROOT);
            if (string.equals("add")) {
               return PlayerSuggestions.onlinePlayerNames();
            }

            if (string.equals("remove") || string.equals("del") || string.equals("rm")) {
               FriendList friendList2 = WexSideClient.getFriends();
               return (List<String>)(friendList2 == null ? List.of() : new ArrayList<>(friendList2.getNames()));
            }
         }

         return List.of();
      }
   }
}
