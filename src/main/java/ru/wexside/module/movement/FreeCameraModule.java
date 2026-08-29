package ru.wexside.module.movement;

import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_3532;
import net.minecraft.class_5498;
import net.minecraft.class_638;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public class FreeCameraModule extends Module implements ConfigSerializable {
   private static volatile FreeCameraModule instance;
   private float pitch;
   private float yaw;
   private final BooleanSetting enabledSetting;
   private final NumberSetting speed;
   private class_243 previousPos = class_243.field_1353;
   private class_638 trackedWorld;
   private class_5498 savedPerspective;
   private class_243 cameraPos = class_243.field_1353;
   private final NumberSetting motionY;
   private boolean active;

   public FreeCameraModule(EventBus eventBus) {
      super(eventBus, "free_camera", "Free Camera", "Позволяет свободно перемещаться камерой в мире", ModuleCategory.valueOf("MOVEMENT"));
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
      this.speed = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 10.0)
            .defaultValue(5.0)
            .multiplier(0.1)
            .precision(0)
            .animationSpeed(20.0F)
            .showMarkers()
            .name("Speed")
            .id("speed")
            .description("Скорость движения свободной камеры")
            .aliases("speed", "скорость"))
         .build();
      this.registerSetting(this.speed);
      this.motionY = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 10.0)
            .defaultValue(5.0)
            .multiplier(0.1)
            .precision(0)
            .animationSpeed(20.0F)
            .showMarkers()
            .name("Motion Y")
            .id("motion_y")
            .description("Скорость вертикального движения камеры")
            .aliases("motion y", "вертикальная скорость"))
         .build();
      this.registerSetting(this.motionY);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.tickCamera());
      this.listen(WorldSessionEvent.class, event -> this.shutdown());
   }

   public static float getFloatType() {
      FreeCameraModule module = instance;
      return module == null ? 0.0F : module.pitch;
   }

   private void disableCamera() {
      this.restorePerspective();
      this.active = false;
   }

   public static boolean isEnabled() {
      FreeCameraModule module = instance;
      return module != null && module.active;
   }

   public static void handle(double deltaX, double deltaY) {
      FreeCameraModule module = instance;
      if (module != null) {
         module.yaw += (float)(deltaX * 0.15);
         module.pitch = class_3532.method_15363(module.pitch + (float)(deltaY * 0.15), -90.0F, 90.0F);
      }
   }

   private void enableCamera() {
      class_310 client = class_310.method_1551();
      class_746 player = client.field_1724;
      if (player != null && client.field_1687 != null && player.method_5805()) {
         this.previousPos = this.cameraPos = player.method_33571();
         this.yaw = player.method_36454();
         this.pitch = player.method_36455();
         this.savedPerspective = client.field_1690.method_31044();
         client.field_1690.method_31043(class_5498.field_26664);
         this.trackedWorld = client.field_1687;
         this.active = true;
      } else {
         this.enabledSetting.setEnabled(false);
      }
   }

   private void shutdown() {
      this.restorePerspective();
      this.active = false;
      this.enabledSetting.setEnabled(false);
      this.enabledSetting.getKeybind().refreshToggleState();
   }

   public static float getFloatType2() {
      FreeCameraModule module = instance;
      return module == null ? 0.0F : module.yaw;
   }

   private void tickCamera() {
      class_310 client = class_310.method_1551();
      class_638 world = client.field_1687;
      if (this.trackedWorld != world) {
         this.trackedWorld = world;
         if (this.active) {
            this.shutdown();
            return;
         }
      }

      if (!this.active || client.field_1724 != null && client.field_1724.method_5805()) {
         if (this.enabledSetting.isEnabled() && !this.active) {
            this.enableCamera();
         } else if (!this.enabledSetting.isEnabled() && this.active) {
            this.disableCamera();
         }

         if (this.active) {
            this.moveCamera();
         }
      } else {
         this.shutdown();
      }
   }

   private void restorePerspective() {
      if (this.savedPerspective != null) {
         class_310.method_1551().field_1690.method_31043(this.savedPerspective);
         this.savedPerspective = null;
      }
   }

   public static class_243 compute(float tickDelta) {
      FreeCameraModule module = instance;
      return module == null
         ? class_243.field_1353
         : new class_243(
            class_3532.method_16436((double)tickDelta, module.previousPos.field_1352, module.cameraPos.field_1352),
            class_3532.method_16436((double)tickDelta, module.previousPos.field_1351, module.cameraPos.field_1351),
            class_3532.method_16436((double)tickDelta, module.previousPos.field_1350, module.cameraPos.field_1350)
         );
   }

   private void moveCamera() {
      this.previousPos = this.cameraPos;
      class_315 options = class_310.method_1551().field_1690;
      double forward = (double)((options.field_1894.method_1434() ? 1 : 0) - (options.field_1881.method_1434() ? 1 : 0));
      double strafe = (double)((options.field_1849.method_1434() ? 1 : 0) - (options.field_1913.method_1434() ? 1 : 0));
      double vertical = (double)((options.field_1903.method_1434() ? 1 : 0) - (options.field_1832.method_1434() ? 1 : 0));
      double speed = this.speed.getValue() * 3.0;
      double dx = 0.0;
      double dz = 0.0;
      if (forward != 0.0 || strafe != 0.0) {
         double yawRad = Math.toRadians((double)this.yaw);
         double sin = Math.sin(yawRad);
         double cos = Math.cos(yawRad);
         dx = -forward * sin - strafe * cos;
         dz = forward * cos - strafe * sin;
         double length = Math.sqrt(dx * dx + dz * dz);
         dx = dx / length * speed;
         dz = dz / length * speed;
      }

      double dy = vertical * this.motionY.getValue();
      this.cameraPos = this.cameraPos.method_1031(dx, dy, dz);
   }
}
