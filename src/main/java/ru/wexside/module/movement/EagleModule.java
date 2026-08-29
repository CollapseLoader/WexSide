package ru.wexside.module.movement;

import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import net.minecraft.class_3736;
import net.minecraft.class_746;
import net.minecraft.class_3675.class_306;
import net.minecraft.class_3675.class_307;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.input.InputBindings;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public class EagleModule extends Module implements ConfigSerializable {
   private boolean autoSneaking;
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("")
         .withKeybind()
         .toggle())
      .build();

   public EagleModule(EventBus eventBus) {
      super(eventBus, "eagle", "Eagle", "Автоматически включает приседание на краю блока", ModuleCategory.valueOf("MOVEMENT"));
      this.registerSetting(this.enabledSetting);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, this::onClientTick);
   }

   private void onClientTick(ClientTickEvent event) {
      if (!this.enabledSetting.isEnabled()) {
         if (this.autoSneaking) {
            this.restoreSneak();
            this.autoSneaking = false;
         }
      } else {
         this.autoSneaking = true;
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         if (player != null && client.field_1687 != null) {
            if (!player.field_3913.field_54155.comp_3163() && player.method_24828()) {
               class_2338 pos = class_2338.method_49637(player.method_23317(), player.method_23318() - 0.1, player.method_23321());
               class_2248 block = client.field_1687.method_8320(pos).method_26204();
               client.field_1690.field_1832.method_23481(block == class_2246.field_10124 || block instanceof class_3736);
            }
         }
      }
   }

   private void restoreSneak() {
      class_304 sneak = class_310.method_1551().field_1690.field_1832;
      class_306 key = class_3675.method_15981(sneak.method_1428());
      boolean pressed = key.method_1442() == class_307.field_1672
         ? InputBindings.isMouseButtonPressed(key.method_1444())
         : InputBindings.isKeyPressed(key.method_1444());
      sneak.method_23481(pressed);
   }
}
