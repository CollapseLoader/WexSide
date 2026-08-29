package ru.wexside.module.movement;

import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;
import net.minecraft.class_2828.class_2831;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.OutgoingPacketEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public class AirStuckModule extends Module implements ConfigSerializable {
   private static volatile AirStuckModule instance;
   private final BooleanSetting enabledSetting;

   public AirStuckModule(EventBus eventBus) {
      super(eventBus, "air_stuck", "Air Stuck", "Остановка в воздухе", ModuleCategory.valueOf("MOVEMENT"));
      instance = this;
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
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, this::onClientTick);
      this.listen(OutgoingPacketEvent.class, this::onOutgoingPacket);
   }

   private void onClientTick(ClientTickEvent event) {
      class_746 player = class_310.method_1551().field_1724;
      if (this.isStuck(player)) {
         player.method_18799(class_243.field_1353);
      }
   }

   private void onOutgoingPacket(OutgoingPacketEvent event) {
      if (this.isStuck(class_310.method_1551().field_1724)) {
         if (event.getPacket() instanceof class_2831) {
            event.update();
         }
      }
   }

   private boolean isStuck(class_746 player) {
      return this.enabledSetting.isEnabled() && player != null && !player.method_24828();
   }

   public static boolean compute2(class_746 player) {
      AirStuckModule module = instance;
      return module != null && module.isStuck(player);
   }
}
