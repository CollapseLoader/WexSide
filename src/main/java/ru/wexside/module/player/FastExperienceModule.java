package ru.wexside.module.player;

import net.minecraft.class_1268;
import net.minecraft.class_1779;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.ItemUseCooldownAccessor;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class FastExperienceModule extends Module implements ConfigSerializable {
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("")
         .withKeybind()
         .toggle())
      .build();

   public FastExperienceModule(EventBus eventBus) {
      super(eventBus, "fast_experience", "Fast Experience", "Ускоряет использование пузырьков опыта", ModuleCategory.valueOf("PLAYER"));
      this.registerSetting(this.enabledSetting);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.onTick());
   }

   private void onTick() {
      if (this.enabledSetting.isEnabled()) {
         class_746 player = class_310.method_1551().field_1724;
         if (player != null) {
            for(class_1268 hand : class_1268.values()) {
               class_1799 stack = player.method_5998(hand);
               if (stack.method_7909() instanceof class_1779 || stack.method_31574(class_1802.field_8287)) {
                  ((ItemUseCooldownAccessor)class_310.method_1551()).setItemUseCooldown(0);
                  return;
               }
            }
         }
      }
   }
}
