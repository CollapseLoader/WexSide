package ru.wexside.command;

import java.util.List;
import ru.wexside.misc.ClientChat;

public final class HelpCommand extends Command {
   private final List<Command> field24;

   public HelpCommand(List<Command> list) {
      super("help", "Показывает список доступных команд", "help");
      this.field24 = list;
   }

   @Override
   public String getUsage() {
      return ".help";
   }

   @Override
   public void execute(String... stringArray) throws CommandUsageException {
      ClientChat.send("Список доступных команд:");

      for(Command command : this.field24) {
         String string = command.getCommandAliases().length == 0 ? command.getCommandName() : command.getCommandAliases()[0];
         String string2 = command.getCommandDescription();
         ClientChat.send("." + string + " - " + string2);
      }
   }
}
