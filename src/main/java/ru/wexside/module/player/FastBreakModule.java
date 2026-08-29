package ru.wexside.module.player;

import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_310;
import net.minecraft.class_636;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.BlockBreakingAccessor;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public class FastBreakModule extends Module implements ConfigSerializable {
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("")
         .withKeybind()
         .toggle())
      .build();
   private final NumberSetting speed;

   public FastBreakModule(EventBus eventBus) {
      super(eventBus, "fast_break", "Fast Break", "Увеличивает скорость ломания блоков", ModuleCategory.valueOf("PLAYER"));
      this.registerSetting(this.enabledSetting);
      this.speed = ((NumberSettingBuilder)NumberSetting.builder()
            .range(10.0, 100.0)
            .defaultValue(50.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .showMarkers()
            .name("Speed")
            .id("speed")
            .description("Ускорение (в % от изначальной скорости)"))
         .build();
      this.registerSetting(this.speed);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.setFloatType());
   }

   private void setFloatType() {
      if (this.enabledSetting.isEnabled()) {
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         class_636 interactions = client.field_1761;
         if (player != null && client.field_1687 != null && interactions != null) {
            BlockBreakingAccessor breaking = (BlockBreakingAccessor)interactions;
            breaking.setBreakingCooldown(0);
            float speedBonus = (float)this.speed.getIntValue() / 100.0F;
            float progress = breaking.getBreakingProgress();
            if (this.blockAt(0.0, player.method_18798().field_1351, 0.0) != class_2246.field_10343 && !player.method_24828()) {
               progress *= 5.0F;
               breaking.setBreakingProgress(progress);
               speedBonus -= 0.8F;
            }

            if (progress > 1.0F - speedBonus && progress < 0.99F) {
               breaking.setBreakingProgress(0.99F);
            }
         }
      }
   }

   private class_2248 blockAt(double offsetX, double offsetY, double offsetZ) {
      class_746 player = class_310.method_1551().field_1724;
      class_2338 pos = class_2338.method_49637(player.method_23317() + offsetX, player.method_23318() + offsetY, player.method_23321() + offsetZ);
      return class_310.method_1551().field_1687.method_8320(pos).method_26204();
   }
}
