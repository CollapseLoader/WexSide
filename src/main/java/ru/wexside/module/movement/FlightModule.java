package ru.wexside.module.movement;

import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_636;
import net.minecraft.class_638;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.AttackUrgency;
import ru.wexside.misc.CorrectionMode;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.module.ModuleManager;
import ru.wexside.module.combat.AttackAuraModule;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.Setting;
import ru.wexside.util.Angle;
import ru.wexside.util.RotationController;
import ru.wexside.util.RotationIntent;

public final class FlightModule extends Module implements ConfigSerializable {
   private static final long PLACE_INTERVAL_MS = 20L;
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("Позволяет летать на FT (требуется голова в руке)")
         .withKeybind()
         .toggle())
      .build();
   private long lastPlaceTime;
   private class_2338 columnPos;

   public FlightModule(EventBus eventBus) {
      super(eventBus, "flight", "Flight", "Позволяет летать на FT (требуется голова в руке)", ModuleCategory.valueOf("MOVEMENT"));
      this.registerSetting(this.enabledSetting);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, this::onClientTick);
      this.listen(WorldSessionEvent.class, event -> {
         this.columnPos = null;
         this.lastPlaceTime = 0L;
      });
   }

   private void onClientTick(ClientTickEvent event) {
      if (!this.enabledSetting.isEnabled()) {
         this.columnPos = null;
         this.releaseLook();
      } else {
         this.disableAura();
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         class_638 world = client.field_1687;
         if (player != null && world != null) {
            class_1799 stack = player.method_6047();
            if (stack.method_7960()) {
               this.releaseLook();
            } else {
               if (this.columnPos == null) {
                  class_2350 facing = player.method_5735();
                  class_243 ahead = new class_243(player.method_23317(), player.method_23318(), player.method_23321())
                     .method_1031((double)facing.method_10148(), 0.0, (double)facing.method_10165());
                  this.columnPos = class_2338.method_49638(ahead);
               }

               boolean placed = false;

               for(class_2338 pos = this.columnPos; pos.method_10264() < world.method_31607() + world.method_31605(); pos = pos.method_10084()) {
                  if (world.method_8320(pos).method_26215() && !world.method_8320(pos.method_10074()).method_26215()) {
                     long now = System.currentTimeMillis();
                     if (now - this.lastPlaceTime >= 20L) {
                        class_243 hitPos = class_243.method_24953(pos);
                        class_3965 hit = new class_3965(hitPos, class_2350.field_11036, pos.method_10074(), false);
                        this.applyLook(player, Angle.fromVectors(player.method_33571(), hitPos));
                        class_636 interactions = client.field_1761;
                        if (interactions != null) {
                           interactions.method_2896(player, class_1268.field_5808, hit);
                        }

                        this.lastPlaceTime = now;
                        placed = true;
                     }
                     break;
                  }
               }

               if (!placed) {
                  this.releaseLook();
               }
            }
         }
      }
   }

   private void applyLook(class_746 player, Angle angle) {
      RotationController rotations = WexSideClient.getRotationController();
      if (rotations != null) {
         RotationIntent intent = new RotationIntent(player, null, angle, AttackUrgency.HIT, CorrectionMode.FOCUSED, false);
         rotations.process2(intent, "Simple");
      }
   }

   private void releaseLook() {
      RotationController rotations = WexSideClient.getRotationController();
      if (rotations != null && rotations.isActive()) {
         RotationIntent intent = rotations.empty();
         if (intent != null && intent.target() == class_310.method_1551().field_1724) {
            rotations.update3();
         }
      }
   }

   private void disableAura() {
      ModuleManager moduleManager = WexSideClient.getInstance().getModuleManager();
      if (moduleManager != null) {
         AttackAuraModule attackAura = moduleManager.getModule(AttackAuraModule.class);
         if (attackAura != null && attackAura.isActive()) {
            Setting toggle = attackAura.getToggleSetting();
            if (toggle instanceof BooleanSetting enabled) {
               enabled.setEnabled(false);
            }
         }
      }
   }
}
