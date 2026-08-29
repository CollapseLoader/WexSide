package ru.wexside.module.player;

import net.minecraft.class_310;
import net.minecraft.class_418;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class AutoRespawnModule extends Module implements ConfigSerializable {
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("")
         .withKeybind()
         .toggle())
      .build();

   public AutoRespawnModule(EventBus eventBus) {
      super(eventBus, "auto_respawn", "Auto Respawn", "Автоматически возрождает после смерти", ModuleCategory.valueOf("PLAYER"));
      this.registerSetting(this.enabledSetting);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.onTick());
   }

   private void onTick() {
      if (this.enabledSetting.isEnabled()) {
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         if (player != null && client.field_1755 instanceof class_418) {
            player.method_7331();
            client.method_1507(null);
         }
      }
   }
}
