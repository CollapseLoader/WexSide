package ru.wexside.command;

import net.minecraft.class_310;
import net.minecraft.class_634;
import ru.wexside.WexSideClient;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.ClientChat;
import ru.wexside.misc.ElapsedTimer;
import ru.wexside.server.FunTimeServerContext;

public final class ReconnectCommand extends Command {
   private static final long HUB_DELAY_MS = 1000L;
   private static final long TIMEOUT_MS = 30000L;
   private final ElapsedTimer reconnectTimer = new ElapsedTimer();
   private volatile boolean reconnecting;
   private volatile int anarchyNumber = -1;

   public ReconnectCommand() {
      super("RCT", "Быстрый перезаход на ту же Анархию FT", "rct", "reconnect", "реконнект", "ркт");
      EventBus eventBus = WexSideClient.getEventBus();
      if (eventBus != null) {
         eventBus.subscribe(ClientTickEvent.class, this::onClientTick);
      }
   }

   @Override
   public String getUsage() {
      return ".rct";
   }

   @Override
   public void execute(String... stringArray) throws CommandUsageException {
      if (!FunTimeServerContext.isConnected()) {
         ClientChat.send("К сожалению .rct не работает на этом сервере.");
      } else {
         int currentAnarchy = FunTimeServerContext.getAnarchyNumber();
         if (currentAnarchy == -1) {
            ClientChat.send("Вы должны находиться на сервере Анархии.");
         } else if (FunTimeServerContext.isPvpLocked()) {
            ClientChat.send("Вы находитесь в режиме PVP.");
         } else {
            ClientChat.send("Перезахожу..");
            this.anarchyNumber = currentAnarchy;
            this.reconnectTimer.update();
            this.reconnecting = true;
            this.sendCommand("hub");
         }
      }
   }

   private void onClientTick(ClientTickEvent event) {
      if (this.reconnecting) {
         if (this.reconnectTimer.process(30000L)) {
            this.reconnecting = false;
            ClientChat.send("Не удалось перезайти (таймаут).");
         } else {
            class_310 mc = class_310.method_1551();
            if (mc.field_1724 != null && mc.method_1562() != null) {
               if (this.reconnectTimer.process(1000L) && FunTimeServerContext.isOnHub()) {
                  this.sendCommand("an" + this.anarchyNumber);
                  this.reconnecting = false;
               }
            }
         }
      }
   }

   private void sendCommand(String command) {
      class_634 networkHandler = class_310.method_1551().method_1562();
      if (networkHandler != null) {
         networkHandler.method_45730(command);
      }
   }
}
