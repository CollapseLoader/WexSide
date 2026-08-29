package ru.wexside.module.player;

import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_310;
import net.minecraft.class_5894;
import net.minecraft.class_642;
import net.minecraft.class_7439;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.ClientChat;
import ru.wexside.misc.ElapsedTimer;
import ru.wexside.misc.PasswordStore;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class AutoAuthModule extends Module implements ConfigSerializable {
   private static final String PASSWORD_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
   private static final long PROMPT_DELAY_MS = 1500L;
   private static final long COMMAND_COOLDOWN_MS = 3000L;
   private static final int PASSWORD_LENGTH = 10;
   private final BooleanSetting enabledSetting;
   private final BooleanSetting showPassword;
   private final ElapsedTimer commandCooldown = new ElapsedTimer();
   private final ElapsedTimer promptDelay = new ElapsedTimer();
   private final Queue<AutoAuthModule.AuthAction> pending = new ConcurrentLinkedQueue<>();
   private boolean waiting;
   private boolean pendingLogin;
   private boolean pendingRegister;

   public AutoAuthModule(EventBus eventBus) {
      super(eventBus, "auto_auth", "Auto Auth", "Автоматически авторизуется на сервере", ModuleCategory.valueOf("PLAYER"));
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Автоматически авторизуется на сервере")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      this.showPassword = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Show Password")
            .id("show_password")
            .description("Показывать сообщение с паролем в чате"))
         .build();
      this.registerSetting(this.showPassword);
   }

   @Override
   protected void initialize() {
      this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
      this.listen(ClientTickEvent.class, event -> this.onTick());
      this.listen(WorldSessionEvent.class, event -> {
         if (this.waiting) {
            this.promptDelay.update();
         }
      });
   }

   private void onTick() {
      if (!this.enabledSetting.isEnabled()) {
         this.reset();
      } else {
         AutoAuthModule.AuthAction action;
         while((action = this.pending.poll()) != null) {
            if (action == AutoAuthModule.AuthAction.REGISTER) {
               this.pendingRegister = true;
            } else {
               this.pendingLogin = true;
            }

            if (!this.waiting) {
               this.waiting = true;
               this.promptDelay.update();
            }
         }

         if (this.waiting) {
            if (this.promptDelay.process(1500L) && this.commandCooldown.process(3000L)) {
               boolean register = this.pendingRegister;
               boolean login = this.pendingLogin;
               this.pendingRegister = false;
               this.pendingLogin = false;
               this.waiting = false;
               class_310 client = class_310.method_1551();
               class_746 player = client.field_1724;
               if (player != null) {
                  PasswordStore passwords = WexSideClient.getPasswordStore();
                  if (passwords != null) {
                     String username = client.method_1548().method_1676();
                     String server = this.serverAddress();
                     if (register) {
                        String password = passwords.getPassword(server, username);
                        if (password == null || password.isEmpty()) {
                           password = this.generatePassword();
                           passwords.savePassword(server, username, password);
                           if (this.showPassword.isEnabled()) {
                              ClientChat.send("Ваш пароль: §c" + password + "§r. Успешно сохранён.");
                           } else {
                              ClientChat.send("Пароль сгенерирован и сохранён.");
                           }
                        }

                        player.field_3944.method_45730("register " + password + " " + password);
                        this.commandCooldown.update();
                     } else if (login) {
                        String saved = passwords.getPassword(server, username);
                        if (saved != null && !saved.isEmpty()) {
                           player.field_3944.method_45730("login " + saved);
                           this.commandCooldown.update();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void onIncomingPacket(IncomingPacketEvent event) {
      if (this.enabledSetting.isEnabled()) {
         String message = this.extractMessage(event.getPacket());
         if (message != null) {
            String lower = message.trim().toLowerCase(Locale.ROOT);
            if (lower.contains("/reg") || lower.contains("/register")) {
               this.pending.add(AutoAuthModule.AuthAction.REGISTER);
            } else if (lower.contains("/login") || lower.contains("/l ")) {
               this.pending.add(AutoAuthModule.AuthAction.LOGIN);
            }
         }
      }
   }

   private void reset() {
      this.pending.clear();
      this.pendingRegister = false;
      this.pendingLogin = false;
      this.waiting = false;
   }

   private String extractMessage(class_2596<?> packet) {
      class_2561 text = null;
      if (packet instanceof class_7439 chat) {
         text = chat.comp_763();
      } else if (packet instanceof class_5894 overlay) {
         text = overlay.comp_2279();
      }

      return text == null ? null : text.getString();
   }

   private String serverAddress() {
      class_642 server = class_310.method_1551().method_1558();
      return server != null && server.field_3761 != null ? server.field_3761 : "";
   }

   private String generatePassword() {
      ThreadLocalRandom random = ThreadLocalRandom.current();
      StringBuilder builder = new StringBuilder(10);

      for(int i = 0; i < 10; ++i) {
         builder.append(
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(random.nextInt("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".length()))
         );
      }

      return builder.toString();
   }

   private static enum AuthAction {
      REGISTER,
      LOGIN;
   }
}
