package ru.wexside.module.movement;

import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BindSetting;
import ru.wexside.setting.BindSettingBuilder;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public class EdgeJumpModule extends Module implements ConfigSerializable {
   private final BindSetting key;
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("")
         .withKeybind()
         .toggle())
      .build();

   public EdgeJumpModule(EventBus eventBus) {
      super(eventBus, "edge_jump", "Edge Jump", "Прыжок с края блока", ModuleCategory.valueOf("MOVEMENT"));
      this.registerSetting(this.enabledSetting);
      this.key = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).name("Key").id("key").description("Клавиша для активации прыжка"))
         .build();
      this.registerSetting(this.key);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, this::onClientTick);
   }

   private void onClientTick(ClientTickEvent event) {
      if (this.enabledSetting.isEnabled() && this.key.isPressed()) {
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         if (player != null && client.field_1687 != null) {
            if (this.atEdge(player)) {
               player.method_6043();
            }
         }
      }
   }

   private boolean atEdge(class_746 player) {
      class_310 client = class_310.method_1551();
      return player.method_24828()
         && !client.field_1690.field_1903.method_1434()
         && client.field_1687.method_8587(player, player.method_5829().method_989(0.0, -0.99, 0.0));
   }
}
