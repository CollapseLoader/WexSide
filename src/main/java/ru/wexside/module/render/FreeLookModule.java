package ru.wexside.module.render;

import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_5498;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class FreeLookModule extends Module implements ConfigSerializable {
   private static volatile FreeLookModule instance;
   private final BooleanSetting enabledSetting;
   private boolean wasEnabled;
   private boolean looking;
   private float yaw;
   private float pitch;
   private class_5498 savedPerspective;

   public FreeLookModule(EventBus eventBus) {
      super(eventBus, "free_look", "Free Look", "Свободный обзор", ModuleCategory.valueOf("RENDER"));
      instance = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Включить свободный обзор")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.tickLook());
      this.listen(WorldRenderEvent.class, event -> this.applyPerspective());
   }

   public static boolean isEnabled() {
      FreeLookModule module = instance;
      return module != null && module.looking;
   }

   public static float getFloatType() {
      FreeLookModule module = instance;
      return module == null ? 0.0F : module.pitch;
   }

   public static float getFloatType2() {
      FreeLookModule module = instance;
      return module == null ? 0.0F : module.yaw;
   }

   public static void handle(double deltaX, double deltaY) {
      FreeLookModule module = instance;
      if (module != null) {
         module.yaw += (float)(deltaX * 0.15);
         module.pitch = class_3532.method_15363(module.pitch + (float)(deltaY * 0.15), -90.0F, 90.0F);
      }
   }

   private void applyPerspective() {
      if (this.looking) {
         class_310.method_1551().field_1690.method_31043(class_5498.field_26665);
      }
   }

   private void tickLook() {
      class_310 client = class_310.method_1551();
      class_746 player = client.field_1724;
      if (!this.enabledSetting.isEnabled()) {
         if (this.wasEnabled) {
            this.stopLooking();
            this.wasEnabled = false;
         }
      } else {
         this.wasEnabled = true;
         if (player != null && client.field_1687 != null) {
            if (!this.looking) {
               this.yaw = player.method_36454();
               this.pitch = player.method_36455();
               this.savedPerspective = client.field_1690.method_31044();
               this.looking = true;
            }
         } else {
            this.looking = false;
         }
      }
   }

   private void stopLooking() {
      if (this.savedPerspective != null) {
         class_310.method_1551().field_1690.method_31043(this.savedPerspective);
      }

      this.looking = false;
      this.savedPerspective = null;
   }
}
