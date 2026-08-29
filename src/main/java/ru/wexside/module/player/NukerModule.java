package ru.wexside.module.player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_1268;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_636;
import net.minecraft.class_746;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.AttackUrgency;
import ru.wexside.misc.BlockBreakingAccessor;
import ru.wexside.misc.CorrectionMode;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.Angle;
import ru.wexside.util.RotationController;
import ru.wexside.util.RotationIntent;

public class NukerModule extends Module implements ConfigSerializable {
   private static final String ROTATION_OWNER = "Simple";
   private final BooleanSetting enabledSetting;
   private final NumberSetting speed;
   private final NumberSetting height;
   private final NumberSetting blocks;
   private final BooleanSetting rotate;
   private final List<class_2338> targets = new ArrayList();
   private boolean sessionActive;
   private boolean rotating;

   public NukerModule(EventBus eventBus) {
      super(eventBus, "nuker", "Nuker", "Ломает блоки вокруг игрока", ModuleCategory.valueOf("PLAYER"));
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
      this.speed = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 100.0)
            .defaultValue(70.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .showMarkers()
            .name("Speed")
            .id("speed")
            .description("Ускорение (в % от изначальной скорости)"))
         .build();
      this.registerSetting(this.speed);
      this.height = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 10.0)
            .defaultValue(3.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .showMarkers()
            .name("Height")
            .id("height")
            .description("Сколько блоков в высоту ломать")
            .aliases("height", "высота"))
         .build();
      this.registerSetting(this.height);
      this.blocks = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 30.0)
            .defaultValue(1.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .showMarkers()
            .name("Multiply-Blocks")
            .id("blocks")
            .description("Сколько блоков ломать одновременно"))
         .build();
      this.registerSetting(this.blocks);
      this.rotate = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Rotate")
            .id("rotate")
            .description("Плавно поворачиваться на ломаемый блок"))
         .build();
      this.registerSetting(this.rotate);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, this::setFloatType);
      this.listen(WorldSessionEvent.class, event -> this.reset());
   }

   private void setFloatType(ClientTickEvent event) {
      class_310 client = class_310.method_1551();
      class_746 player = client.field_1724;
      class_636 interactions = client.field_1761;
      if (this.enabledSetting.isEnabled() && player != null && client.field_1687 != null && interactions != null) {
         if (!this.sessionActive) {
            this.sessionActive = true;
            this.targets.clear();
         }

         BlockBreakingAccessor breaking = (BlockBreakingAccessor)interactions;
         breaking.setBreakingCooldown(0);
         float speedBonus = (float)this.speed.getIntValue() / 100.0F;
         if (this.targets.isEmpty()) {
            this.scanTargets(player);
         }

         class_2338 lookTarget = null;
         Iterator<class_2338> iterator = this.targets.iterator();

         while(iterator.hasNext()) {
            class_2338 pos = (class_2338)iterator.next();
            if (client.field_1687.method_8320(pos).method_26215()) {
               iterator.remove();
            } else {
               if (lookTarget == null) {
                  lookTarget = pos;
               }

               player.method_6104(class_1268.field_5808);
               interactions.method_2902(pos, class_2350.field_11036);
               float progress = breaking.getBreakingProgress();
               if (progress > 1.0F - speedBonus && progress < 0.99F) {
                  breaking.setBreakingProgress(0.99F);
               }
            }
         }

         if (this.rotate.isEnabled() && lookTarget != null) {
            this.lookAt(player, lookTarget);
         } else {
            this.releaseLook();
         }
      } else {
         this.sessionActive = false;
         this.releaseLook();
      }
   }

   private void reset() {
      this.targets.clear();
      this.sessionActive = false;
      this.releaseLook();
   }

   private void scanTargets(class_746 player) {
      int limit = this.blocks.getIntValue();
      int minY = (int)Math.floor(player.method_23318());
      int maxY = minY + this.height.getIntValue() - 1;
      double x = player.method_23317();
      double z = player.method_23321();
      class_310 client = class_310.method_1551();

      for(int y = minY; y <= maxY; ++y) {
         for(int offsetX = -3; offsetX <= 3; ++offsetX) {
            for(int offsetZ = -3; offsetZ <= 3; ++offsetZ) {
               if (this.targets.size() >= limit) {
                  return;
               }

               if (!(Math.sqrt((double)offsetX * (double)offsetX + (double)offsetZ * (double)offsetZ) > 4.0)) {
                  class_2338 pos = class_2338.method_49637(x + (double)offsetX, (double)y, z + (double)offsetZ);
                  class_2680 state = client.field_1687.method_8320(pos);
                  if (!state.method_26215() && !this.unbreakable(state, pos) && this.hasLineOfSight(player, pos)) {
                     this.targets.add(pos);
                  }
               }
            }
         }
      }
   }

   private class_243 blockCenter(class_2338 pos) {
      return new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 0.5, (double)pos.method_10260() + 0.5);
   }

   private boolean rotationOwnedByOther(RotationController rotations, class_746 player) {
      RotationIntent intent = rotations.empty();
      return intent != null && intent.hasTarget() && intent.target() != player;
   }

   private boolean hasLineOfSight(class_746 player, class_2338 pos) {
      class_3959 context = new class_3959(player.method_33571(), this.blockCenter(pos), class_3960.field_17558, class_242.field_1348, player);
      class_3965 hit = class_310.method_1551().field_1687.method_17742(context);
      return hit.method_17783() == class_240.field_1332 && hit.method_17777().equals(pos);
   }

   private boolean unbreakable(class_2680 state, class_2338 pos) {
      return state.method_26204() == class_2246.field_9987 || state.method_26214(class_310.method_1551().field_1687, pos) < 0.0F;
   }

   private void lookAt(class_746 player, class_2338 pos) {
      RotationController rotations = WexSideClient.getRotationController();
      if (rotations != null && !this.rotationOwnedByOther(rotations, player)) {
         Angle angle = Angle.fromVectors(player.method_33571(), this.blockCenter(pos));
         rotations.process2(new RotationIntent(player, null, angle, AttackUrgency.HIT, CorrectionMode.FREE, true), "Simple");
         this.rotating = true;
         Angle applied = rotations.getAngle();
         if (applied != null) {
            player.method_36456(applied.getYaw());
            player.method_36457(applied.getPitch());
         }
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
