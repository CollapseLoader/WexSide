package ru.wexside.module.misc;

import java.util.UUID;
import net.minecraft.class_2596;
import net.minecraft.class_2720;
import net.minecraft.class_2856;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_2856.class_2857;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class SRPSpooferModule extends Module implements ConfigSerializable {
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("")
         .withKeybind()
         .toggle())
      .build();

   public SRPSpooferModule(EventBus eventBus) {
      super(eventBus, "srp_spoofer", "SRP Spoofer", "Подтверждает ресурс-паки сервера без их загрузки", ModuleCategory.valueOf("MISC"));
      this.registerSetting(this.enabledSetting);
   }

   @Override
   protected void initialize() {
      this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
   }

   private void onIncomingPacket(IncomingPacketEvent event) {
      if (this.enabledSetting.isEnabled()) {
         class_2596<?> packet = event.getPacket();
         if (packet instanceof class_2720 resourcePackPacket) {
            class_634 networkHandler = class_310.method_1551().method_1562();
            if (networkHandler != null) {
               UUID packId = resourcePackPacket.comp_2158();
               networkHandler.method_52787(new class_2856(packId, class_2857.field_13016));
               networkHandler.method_52787(new class_2856(packId, class_2857.field_47704));
               networkHandler.method_52787(new class_2856(packId, class_2857.field_13017));
               event.cancel();
            }
         }
      }
   }
}
