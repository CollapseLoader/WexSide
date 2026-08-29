package ru.wexside.command;

import java.util.List;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.misc.ClientChat;
import ru.wexside.misc.Gps;

public final class GpsCommand extends Command {
   public GpsCommand() {
      super("gps", "GPS-стрелка к координатам", "gps", "navigation");
   }

   @Override
   public String getUsage() {
      return ".gps <x> <z>  |  .gps (без аргументов — отключить)";
   }

   @Override
   public void execute(String... args) throws CommandUsageException {
      if (args.length == 0) {
         Gps.clear();
         ClientChat.send("GPS отключён");
      } else if (args.length != 2) {
         throw new CommandUsageException(this);
      } else {
         try {
            int x = Integer.parseInt(args[0].trim());
            int z = Integer.parseInt(args[1].trim());
            Gps.set(x, z);
            ClientChat.send("GPS установлен: " + x + ", " + z);
         } catch (NumberFormatException var4) {
            throw new CommandUsageException(this);
         }
      }
   }

   @Override
   public List<String> complete(int index, String[] args) {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return List.of();
      } else if (index == 0) {
         return List.of(String.valueOf(player.method_31477()));
      } else {
         return index == 1 ? List.of(String.valueOf(player.method_31479())) : List.of();
      }
   }
}
