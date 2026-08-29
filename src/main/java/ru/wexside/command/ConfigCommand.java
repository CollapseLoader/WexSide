package ru.wexside.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_5250;
import net.minecraft.class_2558.class_10609;
import net.minecraft.class_2568.class_10613;
import ru.wexside.misc.ClientChat;
import ru.wexside.misc.ConfigManager;

public final class ConfigCommand extends Command {
   private static final String string6 = ".wex";
   ConfigManager field24;
   private static final int slot = 32;

   public ConfigCommand(ConfigManager configManager2) {
      super("cfg", "Управляет конфигами клиента", "cfg", "config");
      this.field24 = configManager2;
   }

   private void setString(String string) {
      if (!this.field24.profileExists(string)) {
         ClientChat.send("Конфиг не найден: " + string);
      } else {
         this.field24.loadProfile(string);
         ClientChat.send("Конфиг загружен: " + string);

         try {
            this.update();
         } catch (IOException var4) {
            ClientChat.send("Не удалось обновить список конфигов: " + var4.getMessage());
         }
      }
   }

   private void update() throws IOException {
      List<String> list = this.field24.listProfiles();
      if (list.isEmpty()) {
         ClientChat.send("Конфиги не найдены");
      } else {
         ClientChat.send("Доступные конфиги:");
         String string = process2(this.field24.getCurrentProfileName());

         for(String string2 : list) {
            String string3 = process2(string2);
            boolean bl = string3.equals(string);
            ClientChat.send(this.process(string3, bl));
         }
      }
   }

   @Override
   public String getUsage() {
      return ".cfg <save/load/clear/list/dir> [name]";
   }

   @Override
   public void execute(String... stringArray) throws CommandUsageException {
      if (stringArray.length == 0) {
         throw new CommandUsageException(this);
      } else {
         String string = stringArray[0].toLowerCase(Locale.ROOT);

         try {
            switch(string) {
               case "clear":
                  this.update3();
                  return;
               case "list":
                  this.update();
                  return;
               case "dir":
                  this.update2();
                  return;
               default:
                  String string2 = stringArray.length > 1 ? String.join(" ", Arrays.copyOfRange(stringArray, 1, stringArray.length)).trim() : "";
                  if (string2.isBlank()) {
                     String string4 = this.field24.getCurrentProfileName();
                     string2 = string4 != null && !string4.isBlank() ? string4 : "default";
                  }

                  switch(string) {
                     case "save":
                        this.setString2(string2);
                        break;
                     case "load":
                        this.setString(string2);
                        break;
                     default:
                        throw new CommandUsageException(this);
                  }
            }
         } catch (IOException var7) {
            String string5 = var7.getMessage();
            ClientChat.send("Ошибка конфига: " + string5);
         }
      }
   }

   private void update2() throws IOException {
      this.field24.openConfigFolder();
      ClientChat.send("Папка конфигов открыта");
   }

   private class_5250 process(String string, boolean bl) {
      class_2583 actionStyle = class_2583.field_24360
         .method_10958(new class_10609(".cfg load " + string))
         .method_10949(new class_10613(class_2561.method_43470("Нажмите, чтобы загрузить " + string)));
      class_5250 configName = class_2561.method_43470(string).method_10862(actionStyle);
      class_5250 status = bl
         ? class_2561.method_43470(" (Загружен)").method_27692(class_124.field_1060)
         : class_2561.method_43470(" (Загрузить)").method_27692(class_124.field_1080);
      return class_2561.method_43470("- ").method_10852(configName).method_10852(status);
   }

   @Override
   public List<String> complete(int n, String[] stringArray) {
      if (n == 0) {
         return List.of("save", "load", "clear", "list", "dir");
      } else if (n == 1 && stringArray.length >= 1 && "load".equalsIgnoreCase(stringArray[0])) {
         try {
            ArrayList<String> arrayList = new ArrayList<>();

            for(String string : this.field24.listProfiles()) {
               arrayList.add(process2(string));
            }

            return arrayList;
         } catch (IOException var6) {
            return List.of();
         }
      } else {
         return List.of();
      }
   }

   private void update3() throws IOException {
      this.field24.resetProfile();
      ClientChat.send("Конфиг сброшен");
   }

   private void setString2(String string) throws IOException {
      if (string.length() > 32) {
         ClientChat.send("Название конфига не должно превышать 32 символов");
      } else {
         this.field24.saveProfile(string);
         ClientChat.send("Конфиг сохранён: " + string);
      }
   }

   private static String process2(String string) {
      if (string == null) {
         return null;
      } else {
         return string.endsWith(".wex") ? string.substring(0, string.length() - ".wex".length()) : string;
      }
   }
}
