package ru.wexside.module.combat;

import net.minecraft.class_2596;
import net.minecraft.class_2664;
import net.minecraft.class_2743;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;

public class VelocityModule extends Module implements ConfigSerializable {
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

   public VelocityModule(EventBus eventBus) {
      super(eventBus, "velocity", "Velocity", "Уменьшает отталкивание от ударов", ModuleCategory.valueOf("COMBAT"));
      this.registerSetting(this.enabledSetting);
      this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Packet", "Legit")
            .defaultOption("Packet")
            .name("Mode")
            .id("mode")
            .description("Метод уменьшения отталкивания"))
         .build();
      this.registerSetting(this.mode);
   }

   @Override
   protected void initialize() {
      this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
   }

   private void onIncomingPacket(IncomingPacketEvent event) {
      if (this.enabledSetting.isEnabled()) {
         class_746 player = class_310.method_1551().field_1724;
         if (player != null) {
            class_2596<?> packet = event.getPacket();
            if (!"Packet".equals(this.mode.getSelectedOption())) {
               if ("Legit".equals(this.mode.getSelectedOption()) && packet instanceof class_2743 velocity && velocity.method_11818() == player.method_5628()) {
                  player.method_18799(player.method_18798().method_18805(0.6, 1.0, 0.6));
               }
            } else {
               if (packet instanceof class_2743 velocity && velocity.method_11818() == player.method_5628()) {
                  event.update();
                  return;
               }

               if (packet instanceof class_2664) {
                  event.update();
               }
            }
         }
      }
   }
}
