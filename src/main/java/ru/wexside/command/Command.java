package ru.wexside.command;

import java.util.List;

public abstract class Command {
   private final String cmd;
   private final String[] field16;
   private final String field20;

   protected Command(String string, String string2, String... stringArray) {
      this.field20 = string;
      this.cmd = string2;
      String[] stringArray2;
      if (stringArray.length == 0) {
         String[] stringArray3 = new String[1];
         stringArray2 = stringArray3;
         stringArray3[0] = string;
      } else {
         stringArray2 = stringArray;
      }

      this.field16 = stringArray2;
   }

   public abstract String getUsage();

   public abstract void execute(String... var1) throws CommandUsageException;

   public String[] getCommandAliases() {
      return this.field16;
   }

   public String getCommandDescription() {
      return this.cmd;
   }

   public String getCommandName() {
      return this.field20;
   }

   public List<String> complete(int n, String[] stringArray) {
      return List.of();
   }
}
