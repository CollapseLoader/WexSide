package ru.wexside.misc;

import java.util.ArrayList;
import java.util.List;
import ru.wexside.WexSideClient;
import ru.wexside.command.BlockespCommand;
import ru.wexside.command.Command;
import ru.wexside.command.ConfigCommand;
import ru.wexside.command.FriendCommand;
import ru.wexside.command.GpsCommand;
import ru.wexside.command.HelpCommand;
import ru.wexside.command.MacroCommand;
import ru.wexside.command.ReconnectCommand;
import ru.wexside.command.SoundCommand;
import ru.wexside.command.StaffCommand;
import ru.wexside.command.WaypointCommand;

public class CommandManager {
   private ChatCommandDispatcher dispatcher;
   private final List<Command> commands = new ArrayList<>();

   public List<Command> getCommands() {
      return this.commands;
   }

   public void initializeCommands() {
      this.commands.clear();
      this.commands.add(new HelpCommand(this.commands));
      this.commands.add(new ConfigCommand(WexSideClient.getConfigManager()));
      this.commands.add(new FriendCommand());
      this.commands.add(new BlockespCommand());
      this.commands.add(new StaffCommand());
      this.commands.add(new WaypointCommand());
      this.commands.add(new GpsCommand());
      this.commands.add(new MacroCommand());
      this.commands.add(new ReconnectCommand());
      this.commands.add(new SoundCommand());
      this.dispatcher = new ChatCommandDispatcher(WexSideClient.getEventBus(), this.commands);
   }

   public ChatCommandDispatcher getChatCommandDispatcher() {
      return this.dispatcher;
   }
}
