package ru.wexside.module.player;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1661;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_746;
import net.minecraft.class_2828.class_2830;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.AttackUrgency;
import ru.wexside.misc.CorrectionMode;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.util.Angle;
import ru.wexside.util.RotationController;
import ru.wexside.util.RotationIntent;

public class AntiAFKModule extends Module implements ConfigSerializable {
   private static final int SWAP_INTERVAL = 5;
   private static final int CYCLE_TICKS = 1200;
   private static final float YAW_STEP = 6.2F;
   private static final int JUMP_INTERVAL = 40;
   private static final int MESSAGE_INTERVAL = 400;
   private static final int FLAG_INTERVAL = 600;
   private static final String ROTATION_OWNER = "Simple";
   private static final String JUMP = "Jump";
   private static final String ROTATION = "Rotation";
   private static final String JUMP_ROTATION = "Jump+Rotation";
   private static final String MESSAGE = "Message";
   private static final String SWAP_SLOT = "Swap-Slot";
   private static final String ANTI_CHEAT_FLAG = "Anti-Cheat-Flag";
   private static final String STRAFE = "Strafe";
   private static final String CHAT_MESSAGE = "/fackatrongeralol";
   private final BooleanSetting enabledSetting;
   private final MultiSelectSetting actions;
   private int tickCounter;
   private int rememberedSlot = -1;
   private float yawOffset;
   private boolean jumpHeld;
   private boolean strafeHeld;
   private boolean rotating;

   public AntiAFKModule(EventBus eventBus) {
      super(eventBus, "anti_afk", "Anti AFK", "Предотвращает отключение за бездействие", ModuleCategory.valueOf("PLAYER"));
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
      MultiSelectSetting actionsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("Jump", "Rotation", "Jump+Rotation", "Message", "Swap-Slot", "Anti-Cheat-Flag", "Strafe")
            .selectAll(false)
            .optionListEnabled(false)
            .name("Actions")
            .id("actions")
            .description("Действия для предотвращения AFK")
            .aliases("actions", "действия"))
         .build();
      this.actions = actionsSetting;
      this.registerSetting(actionsSetting);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, this::onTick);
      this.listen(WorldSessionEvent.class, event -> this.reset());
   }

   private void onTick(ClientTickEvent event) {
      class_746 player = class_310.method_1551().field_1724;
      if (this.enabledSetting.isEnabled() && player != null && class_310.method_1551().field_1687 != null) {
         this.tickCounter = (this.tickCounter + 1) % 1200;
         List<String> selected = this.actions.getSelectedOptions();
         boolean jumpRotation = selected.contains("Jump+Rotation");
         boolean rotateOrStrafe = selected.contains("Rotation") || selected.contains("Strafe") || jumpRotation;
         if (!selected.contains("Jump") && !rotateOrStrafe) {
            this.releaseJump();
         } else if (this.tickCounter % 40 == 0) {
            class_310.method_1551().field_1690.field_1903.method_23481(true);
            this.jumpHeld = true;
         } else {
            this.releaseJump();
         }

         if (jumpRotation) {
            this.spin(player);
         } else {
            this.releaseLook();
         }

         if (selected.contains("Message") && this.tickCounter % 400 == 0) {
            this.sendMessage(player);
         }

         if (selected.contains("Swap-Slot")) {
            this.swapSlot(player);
         } else {
            this.restoreSlot();
         }

         if (selected.contains("Anti-Cheat-Flag") && this.tickCounter % 600 == 0) {
            this.sendFlagPacket(player);
         }

         if (selected.contains("Strafe")) {
            class_310.method_1551().field_1690.field_1913.method_23481(true);
            this.strafeHeld = true;
         } else {
            this.releaseStrafe();
         }
      } else {
         this.reset();
      }
   }

   private void reset() {
      this.tickCounter = 0;
      this.yawOffset = 0.0F;
      this.releaseAll();
   }

   private void releaseAll() {
      this.restoreSlot();
      this.releaseStrafe();
      this.releaseJump();
      this.releaseLook();
   }

   private void sendFlagPacket(class_746 player) {
      class_634 network = player.field_3944;
      if (network != null) {
         Angle angle = this.currentAngle(player);
         network.method_52787(
            new class_2830(player.method_23317() + 1.0, player.method_23318() + 1.0, player.method_23321() + 1.0, angle.getYaw(), angle.getPitch(), true, false)
         );
      }
   }

   private void swapSlot(class_746 player) {
      class_1661 inventory = player.method_31548();
      if (this.rememberedSlot == -1) {
         this.rememberedSlot = inventory.method_67532();
      }

      if (this.tickCounter % 5 == 0) {
         inventory.method_61496(ThreadLocalRandom.current().nextInt(9));
      }
   }

   private void sendMessage(class_746 player) {
      class_634 network = player.field_3944;
      if (network != null) {
         if ("/fackatrongeralol".startsWith("/")) {
            network.method_45730("/fackatrongeralol".substring(1));
         } else {
            network.method_45729("/fackatrongeralol");
         }
      }
   }

   private void restoreSlot() {
      if (this.rememberedSlot != -1) {
         class_746 player = class_310.method_1551().field_1724;
         if (player != null) {
            player.method_31548().method_61496(this.rememberedSlot);
         }

         this.rememberedSlot = -1;
      }
   }

   private boolean rotationOwnedByOther(RotationController rotations, class_746 player) {
      RotationIntent intent = rotations.empty();
      return intent != null && intent.hasTarget() && intent.target() != player;
   }

   private void spin(class_746 player) {
      RotationController rotations = WexSideClient.getRotationController();
      if (rotations != null && !this.rotationOwnedByOther(rotations, player)) {
         this.yawOffset = (this.yawOffset + 6.2F) % 360.0F;
         rotations.process2(
            new RotationIntent(player, null, new Angle(this.yawOffset, player.method_36455()), AttackUrgency.HIT, CorrectionMode.FREE, true), "Simple"
         );
         this.rotating = true;
         Angle applied = rotations.getAngle();
         if (applied != null) {
            player.method_36456(applied.getYaw());
         }
      }
   }

   private Angle currentAngle(class_746 player) {
      RotationController rotations = WexSideClient.getRotationController();
      return rotations != null && rotations.isActive() && rotations.getAngle() != null
         ? rotations.getAngle()
         : new Angle(player.method_36454(), player.method_36455());
   }

   private void releaseJump() {
      if (this.jumpHeld) {
         class_310.method_1551().field_1690.field_1903.method_23481(false);
         this.jumpHeld = false;
      }
   }

   private void releaseStrafe() {
      if (this.strafeHeld) {
         class_310.method_1551().field_1690.field_1913.method_23481(false);
         this.strafeHeld = false;
      }
   }

   private void releaseLook() {
      if (this.rotating) {
         this.rotating = false;
         RotationController rotations = WexSideClient.getRotationController();
         class_746 player = class_310.method_1551().field_1724;
         if (rotations != null && rotations.isActive() && !this.rotationOwnedByOther(rotations, player)) {
            rotations.update3();
         }
      }
   }
}
