package ru.wexside.module.player;

import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_310;
import net.minecraft.class_7438;
import net.minecraft.class_7439;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class AutoTPAcceptModule extends Module implements ConfigSerializable {
   private static final String[] REQUEST_HINTS = new String[]{
      "телепортироваться", "хочет телепортироваться", "просит телепортироваться", "requested to teleport", "has requested teleport", "tpa"
   };
   private final BooleanSetting enabledSetting;
   private final BooleanSetting onlyFriends;
   private final Queue<String> pending = new ConcurrentLinkedQueue<>();

   public AutoTPAcceptModule(EventBus eventBus) {
      super(eventBus, "auto_tp_accept", "Auto TPAccept", "Автоматически принимает запросы на телепортацию", ModuleCategory.valueOf("PLAYER"));
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      this.onlyFriends = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Only-Friends")
            .id("only_friends")
            .description("Принимать запросы только от друзей"))
         .build();
      this.registerSetting(this.onlyFriends);
   }

   @Override
   protected void initialize() {
      this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
      this.listen(ClientTickEvent.class, event -> this.onTick());
   }

   private void onTick() {
      if (!this.enabledSetting.isEnabled()) {
         this.pending.clear();
      } else {
         boolean accept = false;

         String message;
         while((message = this.pending.poll()) != null) {
            if (!this.onlyFriends.isEnabled() || this.isFriendRequest(message)) {
               accept = true;
            }
         }

         if (accept) {
            this.accept();
         }
      }
   }

   private void onIncomingPacket(IncomingPacketEvent event) {
      if (this.enabledSetting.isEnabled()) {
         String message = this.textOf(event.getPacket());
         if (message != null && this.isTeleportRequest(message)) {
            this.pending.add(message);
         }
      }
   }

   private String textOf(class_2596<?> packet) {
      class_2561 text = null;
      if (packet instanceof class_7439 gameMessage) {
         text = gameMessage.comp_763();
      } else if (packet instanceof class_7438 chatMessage) {
         text = chatMessage.comp_1103();
      }

      return text == null ? null : text.getString();
   }

   private String stripColor(String message) {
      StringBuilder builder = new StringBuilder(message.length());
      char[] chars = message.toCharArray();

      for(int i = 0; i < chars.length; ++i) {
         char ch = chars[i];
         if (ch == 167) {
            ++i;
         } else {
            builder.append(ch);
         }
      }

      return builder.toString();
   }

   private boolean isFriendRequest(String message) {
      if (WexSideClient.getFriends() == null) {
         return false;
      } else {
         String stripped = this.stripColor(message).toLowerCase(Locale.ROOT);

         for(String friend : WexSideClient.getFriends().getNames()) {
            if (stripped.contains(friend)) {
               return true;
            }
         }

         return false;
      }
   }

   private void accept() {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null) {
         player.field_3944.method_45730("tpaccept");
      }
   }

   private boolean isTeleportRequest(String message) {
      String lower = message.toLowerCase(Locale.ROOT);

      for(String hint : REQUEST_HINTS) {
         if (lower.contains(hint)) {
            return true;
         }
      }

      return false;
   }
}
