package ru.wexside.module.player;

import net.minecraft.class_1268;
import net.minecraft.class_1536;
import net.minecraft.class_1802;
import net.minecraft.class_2596;
import net.minecraft.class_2767;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_636;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.ElapsedTimer;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public class AutoFishModule extends Module implements ConfigSerializable {
   private static final long RECAST_DELAY_MS = 1000L;
   private static final double BITE_DISTANCE_SQ = 9.0;
   private final BooleanSetting enabledSetting;
   private final ElapsedTimer cooldown = new ElapsedTimer();
   private double splashX;
   private double splashY;
   private double splashZ;
   private volatile boolean bitePending;

   public AutoFishModule(EventBus eventBus) {
      super(eventBus, "auto_fish", "Auto Fish", "Автоматическая рыбалка", ModuleCategory.valueOf("PLAYER"));
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
      this.listen(ClientTickEvent.class, this::onTick);
      this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
      this.listen(WorldSessionEvent.class, event -> this.reset());
   }

   private void onIncomingPacket(IncomingPacketEvent event) {
      if (this.enabledSetting.isEnabled()) {
         class_2596<?> packet = event.getPacket();
         if (packet instanceof class_2767 soundPacket) {
            if (((class_3414)soundPacket.method_11894().comp_349()).equals(class_3417.field_14660)) {
               this.splashX = soundPacket.method_11890();
               this.splashY = soundPacket.method_11889();
               this.splashZ = soundPacket.method_11893();
               this.bitePending = true;
            }
         }
      }
   }

   private void onTick(ClientTickEvent event) {
      class_746 player = class_310.method_1551().field_1724;
      if (this.enabledSetting.isEnabled()
         && player != null
         && class_310.method_1551().field_1687 != null
         && !AutoEatModule.eating
         && player.method_6047().method_31574(class_1802.field_8378)) {
         if (this.bitePending) {
            this.bitePending = false;
            class_1536 bobber = player.field_7513;
            if (bobber != null && bobber.method_73189().method_1028(this.splashX, this.splashY, this.splashZ) < 9.0) {
               this.useRod(player);
               this.cooldown.update();
               return;
            }
         }

         if (player.field_7513 == null && this.cooldown.process(1000L)) {
            this.useRod(player);
            this.cooldown.update();
         }
      } else {
         this.reset();
      }
   }

   private void useRod(class_746 player) {
      class_636 interactions = class_310.method_1551().field_1761;
      if (interactions != null) {
         interactions.method_2919(player, class_1268.field_5808);
         player.method_6104(class_1268.field_5808);
      }
   }

   private void reset() {
      this.bitePending = false;
   }
}
