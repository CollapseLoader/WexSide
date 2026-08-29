package ru.wexside.command;

public final class CommandUsageException extends RuntimeException {
   private final Command command;

   public CommandUsageException(Command command) {
      super(command == null ? "Invalid command arguments" : command.getUsage());
      this.command = command;
   }

   public Command getCommand() {
      return this.command;
   }
}
