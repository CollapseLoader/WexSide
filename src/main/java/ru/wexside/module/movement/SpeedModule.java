package ru.wexside.module.movement;

import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;

public final class SpeedModule extends Module implements ConfigSerializable {
   public static volatile float value;
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("Увеличивает скорость перемещения")
         .withKeybind()
         .toggle())
      .build();
   private final ModeSetting mode;

   public SpeedModule(EventBus eventBus) {
      super(eventBus, "speed", "Speed", "Увеличивает скорость перемещения", ModuleCategory.valueOf("MOVEMENT"));
      this.registerSetting(this.enabledSetting);
      this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Sticky", "Collision", "Sneak")
            .defaultOption("Sticky")
            .name("Mode")
            .id("mode")
            .description("Метод ускорения"))
         .build();
      this.registerSetting(this.mode);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, this::onClientTick);
   }

   private void onClientTick(ClientTickEvent event) {
      value = 0.0F;
      if (this.enabledSetting.isEnabled()) {
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         if (player != null && client.field_1687 != null) {
            String var4 = this.mode.getSelectedOption();
            switch(var4) {
               case "Sneak":
                  this.applySneak(player);
                  break;
               case "Collision":
                  this.applyCollision(player, client.field_1687);
                  break;
               case "Sticky":
                  this.applySticky(player, client.field_1687);
            }
         }
      }
   }

   private void applySticky(class_746 player, class_638 world) {
      class_238 box = player.method_5829().method_1014(0.3);
      int nearby = 0;

      for(class_1297 entity : world.method_18112()) {
         if (!(entity instanceof class_746) && entity instanceof class_1309 && !(entity instanceof class_1531) && entity.method_5829().method_994(box)) {
            ++nearby;
         }
      }

      if (nearby > 0) {
         double yaw = Math.toRadians((double)player.method_36454());
         double boost = 0.08 * (double)nearby;
         class_243 velocity = player.method_18798();
         player.method_18800(velocity.field_1352 + -Math.sin(yaw) * boost, velocity.field_1351, velocity.field_1350 + Math.cos(yaw) * boost);
      }
   }

   private void applyCollision(class_746 player, class_638 world) {
      class_238 box = player.method_5829().method_1014(0.1);
      int armorStands = 0;
      int living = 0;

      for(class_1297 entity : world.method_18112()) {
         if (!entity.method_5732() && entity.method_5829().method_994(box)) {
            if (entity instanceof class_1531) {
               ++armorStands;
            }

            if (entity instanceof class_1309) {
               ++living;
            }
         }
      }

      if (armorStands > 1 || living > 1) {
         if (!player.method_24828()) {
            value = armorStands > 1 ? 1.0F / (float)armorStands : 0.16F;
         }
      }
   }

   private void applySneak(class_746 player) {
      if (player.method_6115()) {
         class_243 velocity = player.method_18798();
         player.method_18800(velocity.field_1352 * 1.5, velocity.field_1351, velocity.field_1350 * 1.5);
      }
   }
}
