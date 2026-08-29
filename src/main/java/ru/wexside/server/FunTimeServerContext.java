package ru.wexside.server;

import java.util.Locale;
import net.minecraft.class_266;
import net.minecraft.class_269;
import net.minecraft.class_310;
import net.minecraft.class_345;
import net.minecraft.class_642;
import net.minecraft.class_746;
import net.minecraft.class_8646;
import net.minecraft.class_9013;
import ru.wexside.misc.BossBarMapAccessor;

public final class FunTimeServerContext {
   private static final class_310 CLIENT = class_310.method_1551();
   private static final long PVP_STATUS_GRACE_PERIOD_MS = 3000L;
   private static volatile long lastPvpStatusTime;

   private FunTimeServerContext() {
   }

   public static int getBalance() {
      class_746 player = CLIENT.field_1724;
      if (player != null && CLIENT.field_1687 != null) {
         try {
            class_269 scoreboard = CLIENT.field_1687.method_8428();
            class_266 objective = scoreboard.method_1189(class_8646.field_45157);
            if (objective == null) {
               return -1;
            } else {
               class_9013 score = scoreboard.method_55430(player, objective);
               return score == null ? -1 : score.method_55397();
            }
         } catch (RuntimeException var4) {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public static boolean isOnHub() {
      if (CLIENT.field_1705 == null) {
         return false;
      } else {
         BossBarMapAccessor bossBars = (BossBarMapAccessor)CLIENT.field_1705.method_1740();

         for(class_345 bossBar : bossBars.getMap().values()) {
            if (bossBar.method_5414().getString().equals("Вы играете на ФанТайм!")) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isConnected() {
      class_642 server = CLIENT.method_1558();
      return server != null && server.field_3761 != null && server.field_3761.toLowerCase(Locale.ROOT).contains("funtime");
   }

   public static boolean isPvpLocked() {
      if (CLIENT.field_1687 != null && CLIENT.field_1724 != null) {
         BossBarMapAccessor bossBars = (BossBarMapAccessor)CLIENT.field_1705.method_1740();

         for(class_345 bossBar : bossBars.getMap().values()) {
            String title = bossBar.method_5414().getString().toLowerCase(Locale.ROOT);
            if (title.contains("pvp") || title.contains("пвп")) {
               lastPvpStatusTime = System.currentTimeMillis();
               return true;
            }
         }

         return lastPvpStatusTime != 0L && System.currentTimeMillis() - lastPvpStatusTime < 3000L;
      } else {
         lastPvpStatusTime = 0L;
         return false;
      }
   }

   public static int getAnarchyNumber() {
      if (CLIENT.field_1687 == null) {
         return -1;
      } else {
         try {
            class_266 objective = CLIENT.field_1687.method_8428().method_1170("TAB-Scoreboard");
            if (objective == null) {
               return -1;
            } else {
               String title = objective.method_1114().getString();
               int markerIndex = title.indexOf("Анархия-");
               if (markerIndex < 0) {
                  return -1;
               } else {
                  String suffix = title.substring(markerIndex + "Анархия-".length()).trim();
                  StringBuilder number = new StringBuilder();

                  for(int index = 0; index < suffix.length() && Character.isDigit(suffix.charAt(index)); ++index) {
                     number.append(suffix.charAt(index));
                  }

                  return number.isEmpty() ? -1 : Integer.parseInt(number.toString());
               }
            }
         } catch (RuntimeException var6) {
            return -1;
         }
      }
   }

   public static void reset() {
      lastPvpStatusTime = 0L;
   }
}
