package ru.wexside.module.player;

import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.FriendList;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BindSetting;
import ru.wexside.setting.BindSettingBuilder;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class AutoSpawnModule extends Module implements ConfigSerializable {
   private static final long COOLDOWN_MS = 9000L;
   private static final String MODE_AUTO = "Auto";
   private static final String MODE_KEY = "Key";
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("")
         .withKeybind()
         .toggle())
      .build();
   private final ModeSetting mode;
   private final BindSetting key;
   private final NumberSetting range;
   private long lastCommandAt;

   public AutoSpawnModule(EventBus eventBus) {
      super(eventBus, "auto_spawn", "Auto Spawn", "Автоматическая телепортация на спавн при обнаружении игрока поблизости", ModuleCategory.valueOf("PLAYER"));
      this.registerSetting(this.enabledSetting);
      this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Key", "Auto")
            .defaultOption("Key")
            .name("Mode")
            .id("mode")
            .description("Режим телепортации на спавн"))
         .build();
      this.registerSetting(this.mode);
      this.key = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .onReleased(this::onKeyPress)
            .name("Key")
            .id("key")
            .description("Клавиша телепортации на спавн")
            .visibleWhen(() -> "Key".equals(this.mode.getSelectedOption())))
         .build();
      this.registerSetting(this.key);
      this.range = ((NumberSettingBuilder)NumberSetting.builder()
            .range(16.0, 150.0)
            .defaultValue(50.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .showMarkers()
            .name("Range")
            .id("range")
            .description("Дистанция обнаружения игрока")
            .visibleWhen(() -> "Auto".equals(this.mode.getSelectedOption())))
         .build();
      this.registerSetting(this.range);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, this::onTick);
   }

   private void onTick(ClientTickEvent event) {
      if (this.enabledSetting.isEnabled() && "Auto".equals(this.mode.getSelectedOption())) {
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         if (player != null && client.field_1687 != null) {
            FriendList friends = WexSideClient.getFriends();

            for(class_1657 other : client.field_1687.method_18456()) {
               if (other != player
                  && (friends == null || !friends.contains(other.method_5477().getString()))
                  && (double)player.method_5739(other) < this.range.getValue()) {
                  this.sendSpawn();
                  return;
               }
            }
         }
      }
   }

   private void onKeyPress(BindSetting ignored) {
      if (this.enabledSetting.isEnabled() && "Key".equals(this.mode.getSelectedOption())) {
         this.sendSpawn();
      }
   }

   private void sendSpawn() {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null) {
         long now = System.currentTimeMillis();
         if (now - this.lastCommandAt >= 9000L) {
            player.field_3944.method_45730("spawn");
            this.lastCommandAt = now;
         }
      }
   }
}
