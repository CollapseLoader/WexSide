package ru.wexside.module.player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1839;
import net.minecraft.class_1844;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2673;
import net.minecraft.class_310;
import net.minecraft.class_5250;
import net.minecraft.class_8042;
import net.minecraft.class_9334;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.ClientChat;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.util.ColorUtils;

public final class UseTrackerModule extends Module implements ConfigSerializable {
   static final String DRINK = "Drink";
   static final String SPLASH = "Splash";
   static final String USE = "Use";
   private static final long SPLASH_CACHE_NS = 2000000000L;
   private final BooleanSetting enabledSetting;
   private final MultiSelectSetting types;
   private final Map<Integer, UseTrackerModule.SplashCache> splashByPlayer = new HashMap<>();
   private final Map<Integer, UseTrackerModule.UseProgress> useByPlayer = new HashMap<>();

   public UseTrackerModule(EventBus eventBus) {
      super(eventBus, "use_tracker", "Use Tracker", "Использования предметов игроками в чат", ModuleCategory.valueOf("PLAYER"));
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Использования предметов игроками в чат")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      MultiSelectSetting typesSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("Use", "Drink", "Splash")
            .selectAll(true)
            .optionListEnabled(false)
            .name("Types")
            .id("types")
            .description("Какие использования писать в чат"))
         .build();
      typesSetting.setOptions(new String[]{"Use", "Drink", "Splash"});
      this.types = typesSetting;
      this.registerSetting(typesSetting);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.onTick());
      this.listen(IncomingPacketEvent.class, event -> this.onPacket(event.getPacket()));
      this.listen(WorldSessionEvent.class, event -> {
         this.splashByPlayer.clear();
         this.useByPlayer.clear();
      });
   }

   private void onTick() {
      if (this.enabledSetting.isEnabled()) {
         class_310 client = class_310.method_1551();
         if (client.field_1724 != null && client.field_1687 != null) {
            this.trackUses(client);
            if (this.types.getSelectedOptions().contains("Splash")) {
               this.cacheSplashPotions(client);
            }
         }
      }
   }

   private void onPacket(class_2596<?> packet) {
      if (this.enabledSetting.isEnabled() && this.types.getSelectedOptions().contains("Splash")) {
         class_310 client = class_310.method_1551();
         if (!client.method_18854()) {
            client.execute(() -> this.onPacket(packet));
         } else if (client.field_1687 != null) {
            if (!(packet instanceof class_8042)) {
               if (packet instanceof class_2673 worldEvent) {
                  this.onSplash(worldEvent, client);
               }
            } else {
               class_8042 bundle = (class_8042)packet;

               for(class_2596 nested : bundle.method_48324()) {
                  this.onPacket(nested);
               }
            }
         }
      }
   }

   private boolean isSplashPotion(class_1799 stack) {
      return stack != null && (stack.method_31574(class_1802.field_8436) || stack.method_31574(class_1802.field_8150));
   }

   private static class_5250 accent(String text) {
      return class_2561.method_43470(text).method_27694(style -> style.method_36139(ColorUtils.lightContrastColor & 16777215));
   }

   private class_1657 nearestThrower(class_310 client, double x, double y, double z) {
      class_1657 nearest = null;
      double best = 0.7;

      for(class_1657 player : client.field_1687.method_18456()) {
         class_243 look = player.method_5828(1.0F);
         class_243 handPos = player.method_33571().method_1019(look.method_1021(0.3)).method_1031(-look.field_1350 * 0.35, 0.0, look.field_1352 * 0.35);
         double distance = handPos.method_1028(x, y, z);
         if (!(distance >= best)) {
            best = distance;
            nearest = player;
         }
      }

      return nearest;
   }

   private void onSplash(class_2673 packet, class_310 client) {
      int eventId = packet.method_11532();
      if (eventId == 2002 || eventId == 2007) {
         class_2338 member2225 = packet.method_11531();
         class_1657 thrower = this.nearestThrower(
            client, (double)member2225.method_10263() + 0.5, (double)member2225.method_10264() + 0.5, (double)member2225.method_10260() + 0.5
         );
         if (thrower != null) {
            class_1799 stack = this.splashStack(thrower);
            if (!stack.method_7960()) {
               String name = potionName(stack);
               if (name == null) {
                  name = stack.method_7964().getString();
               }

               class_5250 message = class_2561.method_43473();
               if (thrower == client.field_1724) {
                  message.method_10852(class_2561.method_43470("Вы использовали "));
               } else {
                  message.method_10852(accent(thrower.method_5477().getString())).method_10852(class_2561.method_43470(" использовал "));
               }

               message.method_10852(accent(name));
               ClientChat.send(message);
            }
         }
      }
   }

   private class_1799 splashStack(class_1657 player) {
      class_1799 main = player.method_6047();
      if (this.isSplashPotion(main)) {
         return main;
      } else {
         UseTrackerModule.SplashCache cache = this.splashByPlayer.get(player.method_5628());
         return cache != null && System.nanoTime() - cache.timeNs <= 2000000000L ? cache.stack : class_1799.field_8037;
      }
   }

   private void cacheSplashPotions(class_310 client) {
      long now = System.nanoTime();
      HashSet<Integer> alive = new HashSet<>();

      for(class_1657 player : client.field_1687.method_18456()) {
         alive.add(player.method_5628());
         class_1799 stack = player.method_6047();
         if (this.isSplashPotion(stack)) {
            this.splashByPlayer.put(player.method_5628(), new UseTrackerModule.SplashCache(stack.method_7972(), now));
         }
      }

      this.splashByPlayer.entrySet().removeIf(entry -> !alive.contains(entry.getKey()) || now - entry.getValue().timeNs > 2000000000L);
   }

   private void announceUse(class_1657 player, boolean self, class_1799 stack, class_1839 action) {
      boolean drink = action == class_1839.field_8946;
      if (this.types.getSelectedOptions().contains(drink ? "Drink" : "Use")) {
         class_5250 message = class_2561.method_43473();
         if (self) {
            message.method_10852(class_2561.method_43470(drink ? "Вы выпили " : "Вы съели "));
         } else {
            message.method_10852(accent(player.method_5477().getString())).method_10852(class_2561.method_43470(drink ? " выпил " : " съел "));
         }

         message.method_10852(accent(stack.method_7964().getString()));
         ClientChat.send(message);
      }
   }

   private void trackUses(class_310 client) {
      HashSet<Integer> alive = new HashSet<>();

      for(class_1657 player : client.field_1687.method_18456()) {
         alive.add(player.method_5628());
         this.useByPlayer.computeIfAbsent(player.method_5628(), id -> new UseTrackerModule.UseProgress()).tick(player, player == client.field_1724);
      }

      this.useByPlayer.keySet().removeIf(id -> !alive.contains(id));
   }

   private static String potionName(class_1799 stack) {
      class_1844 contents = (class_1844)stack.method_58694(class_9334.field_49651);
      return contents == null ? null : stack.method_7964().getString();
   }

   private static record SplashCache(class_1799 stack, long timeNs) {
   }

   private final class UseProgress {
      private int useTime;
      private int maxUseTime;
      private class_1799 stack = class_1799.field_8037;

      void tick(class_1657 player, boolean self) {
         if (player.method_6115()) {
            this.stack = player.method_6030().method_7972();
            this.useTime = player.method_6048();
            this.maxUseTime = this.stack.method_7935(player);
         } else {
            if (this.useTime > 0 && this.maxUseTime > 0 && this.useTime >= this.maxUseTime - 1 && !this.stack.method_7960()) {
               UseTrackerModule.this.announceUse(player, self, this.stack, this.stack.method_7976());
            }

            this.useTime = 0;
            this.maxUseTime = 0;
            this.stack = class_1799.field_8037;
         }
      }
   }
}
