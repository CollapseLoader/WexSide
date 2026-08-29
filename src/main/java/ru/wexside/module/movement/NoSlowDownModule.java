package ru.wexside.module.movement;

import net.minecraft.class_1268;
import net.minecraft.class_1713;
import net.minecraft.class_1802;
import net.minecraft.class_1839;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_2815;
import net.minecraft.class_2868;
import net.minecraft.class_2886;
import net.minecraft.class_310;
import net.minecraft.class_636;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.DamageEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.event.MovementSlowdownEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.util.DamageTracker;

public final class NoSlowDownModule extends Module implements ConfigSerializable {
   private int spookyTicks;
   private final DamageTracker damageTracker = new DamageTracker();
   private final ModeSetting mode;
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("Убирает замедление при использовании предметов")
         .withKeybind()
         .toggle())
      .build();

   public NoSlowDownModule(EventBus eventBus) {
      super(eventBus, "no_slow_down", "No Slow Down", "Убирает замедление при использовании предметов", ModuleCategory.valueOf("MOVEMENT"));
      this.registerSetting(this.enabledSetting);
      this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("FT", "Crossbow", "Spooky-T", "Vanilla", "Matrix", "Grim", "Really-World")
            .defaultOption("FT")
            .name("Mode")
            .id("mode")
            .description("Метод устранения замедления"))
         .build();
      this.registerSetting(this.mode);
   }

   @Override
   protected void initialize() {
      this.listen(MovementSlowdownEvent.class, this::onSlowdown);
      this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
      this.listen(DamageEvent.class, this::onDamage);
   }

   private void onSlowdown(MovementSlowdownEvent event) {
      if (this.enabledSetting.isEnabled()) {
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         if (player != null && client.field_1687 != null) {
            if (player.method_6115()) {
               String var4 = this.mode.getSelectedOption();
               switch(var4) {
                  case "FT":
                     this.applyFunTime(event, player);
                     break;
                  case "Crossbow":
                     if (player.method_6047().method_7909() == class_1802.field_8399) {
                        event.update();
                     }
                     break;
                  case "Spooky-T":
                     this.applySpookyTime(event, player);
                     break;
                  case "Vanilla":
                     event.update();
                     break;
                  case "Matrix":
                     this.applyMatrix(event, player);
                     break;
                  case "Grim":
                     this.applyGrim(event, player);
                     break;
                  case "Really-World":
                     this.applyReallyWorld(event, player);
               }
            }
         }
      }
   }

   private void applyReallyWorld(MovementSlowdownEvent event, class_746 player) {
      this.damageTracker.consumeAfter(1500L);
      if (!this.skipOffhandConsume(player)) {
         if (!player.method_24828() || player.method_5799() || this.damageTracker.isDamageConfirmed()) {
            if (player.method_6058() == class_1268.field_5808) {
               this.sendInteract(player, class_1268.field_5810);
               event.update();
            } else {
               event.update();
               this.desyncSelectedSlot(player);
            }
         }
      }
   }

   private void onDamage(DamageEvent event) {
      if (this.enabledSetting.isEnabled()) {
         this.damageTracker.onDamage(event);
      }
   }

   private void applyMatrix(MovementSlowdownEvent event, class_746 player) {
      boolean falling = player.field_6017 > 0.725;
      event.update();
      if (player.method_24828() && !player.method_5799()) {
         if (player.field_6012 % 2 == 0) {
            float scale = player.field_3913.method_3128().field_1343 == 0.0F ? 0.5F : 0.4F;
            this.scaleHorizontal(player, scale);
         }
      } else if (falling) {
         float scale = player.field_6017 > 1.4 ? 0.95F : 0.97F;
         this.scaleHorizontal(player, scale);
      }
   }

   private void applyFunTime(MovementSlowdownEvent event, class_746 player) {
      if (!player.method_6101()) {
         if (player.method_6048() < 4) {
            class_636 interactions = class_310.method_1551().field_1761;
            if (interactions != null) {
               interactions.method_2906(0, 0, 0, class_1713.field_7790, player);
            }

            player.field_3944.method_52787(new class_2815(0));
         } else {
            event.update();
         }
      }
   }

   private void sendInteract(class_746 player, class_1268 hand) {
      player.field_3944.method_52787(new class_2886(hand, 0, player.method_36454(), player.method_36455()));
   }

   private void applySpookyTime(MovementSlowdownEvent event, class_746 player) {
      ++this.spookyTicks;
      if (this.spookyTicks % 2 == 0) {
         class_1268 hand = player.method_6030().method_7935(player) > 0 ? class_1268.field_5810 : class_1268.field_5808;
         this.sendInteract(player, hand);
         event.update();
      }
   }

   private boolean isMoving(class_746 player) {
      class_241 movement = player.field_3913.method_3128();
      return movement.field_1343 != 0.0F || movement.field_1342 != 0.0F;
   }

   private void scaleHorizontal(class_746 player, float scale) {
      class_243 velocity = player.method_18798();
      player.method_18800(velocity.field_1352 * (double)scale, velocity.field_1351, velocity.field_1350 * (double)scale);
   }

   private void applyGrim(MovementSlowdownEvent event, class_746 player) {
      if (!this.skipOffhandConsume(player)) {
         if (player.method_6058() == class_1268.field_5808) {
            this.sendInteract(player, class_1268.field_5810);
            event.update();
         } else {
            event.update();
            this.desyncSelectedSlot(player);
         }
      }
   }

   private boolean skipOffhandConsume(class_746 player) {
      class_1839 action = player.method_6079().method_7976();
      return (action == class_1839.field_8950 || action == class_1839.field_8946) && player.method_6058() == class_1268.field_5808;
   }

   private void desyncSelectedSlot(class_746 player) {
      if (this.isMoving(player)) {
         int selected = player.method_31548().method_67532();
         player.field_3944.method_52787(new class_2868(selected % 8 + 1));
         player.field_3944.method_52787(new class_2868(selected));
      }
   }

   private void onIncomingPacket(IncomingPacketEvent event) {
      if (this.enabledSetting.isEnabled()) {
         this.damageTracker.onIncomingPacket(event);
      }
   }
}
