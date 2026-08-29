package ru.wexside.module.hud;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class BetterChatModule extends Module implements ConfigSerializable {
   private static volatile BetterChatModule instance;
   private final BooleanSetting enabledSetting;
   private String lastMessage;
   private int repeatCount;

   public BetterChatModule(EventBus eventBus) {
      super(eventBus, "better_chat", "Better Chat", "Схлопывает повторяющиеся сообщения в чате", ModuleCategory.valueOf("DISPLAY"));
      instance = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Схлопывает повторяющиеся сообщения в чате со счётчиком")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
   }

   @Override
   protected void initialize() {
      this.listen(WorldSessionEvent.class, event -> {
         this.lastMessage = null;
         this.repeatCount = 0;
      });
   }

   public static int compute(String message) {
      BetterChatModule module = instance;
      if (module == null) {
         return 0;
      } else if (module.enabledSetting.isEnabled() && !message.isEmpty() && message.equals(module.lastMessage)) {
         ++module.repeatCount;
         return module.repeatCount;
      } else {
         module.lastMessage = message;
         module.repeatCount = 1;
         return 0;
      }
   }
}
