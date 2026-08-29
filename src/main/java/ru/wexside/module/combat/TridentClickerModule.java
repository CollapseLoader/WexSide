package ru.wexside.module.combat;

import java.util.List;
import net.minecraft.class_1309;
import net.minecraft.class_1802;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.AttackInvoker;
import ru.wexside.misc.ReachHelper;
import ru.wexside.misc.TargetFilter;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.Angle;
import ru.wexside.util.TargetSelector;

public class TridentClickerModule extends Module implements ConfigSerializable {
   private static final float RANGE = 3.0F;
   private static final long CLICK_INTERVAL_MS = 450L;
   private static final List<String> TARGET_TYPES = List.of("Players");
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("")
         .withKeybind()
         .toggle())
      .build();
   private final NumberSetting cps;
   private final ModeSetting sorting;
   private TargetSelector targetSelector;
   private long lastClickTime;

   public TridentClickerModule(EventBus eventBus) {
      super(eventBus, "trident_clicker", "Trident Clicker", "Закликивание трезубцем", ModuleCategory.valueOf("COMBAT"));
      this.registerSetting(this.enabledSetting);
      this.cps = ((NumberSettingBuilder)NumberSetting.builder()
            .range(5.0, 200.0)
            .defaultValue(80.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("CPS")
            .id("cps")
            .description("Количество кликов в секунду"))
         .build();
      this.registerSetting(this.cps);
      this.sorting = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Health", "Distance", "Crosshair")
            .defaultOption("Distance")
            .name("Сортировка")
            .id("sorting")
            .description("Сортировка целей для атаки"))
         .build();
      this.registerSetting(this.sorting);
   }

   @Override
   protected void initialize() {
      this.targetSelector = new TargetSelector();
      this.listen(ClientTickEvent.class, event -> this.onTick());
   }

   private void onTick() {
      class_310 client = class_310.method_1551();
      class_746 player = client.field_1724;
      if (this.enabledSetting.isEnabled() && player != null && client.field_1687 != null) {
         if (!player.method_6047().method_31574(class_1802.field_8547)) {
            this.targetSelector.update();
         } else {
            TargetFilter filter = new TargetFilter(TARGET_TYPES, null, 0);
            class_1309 target = this.targetSelector.process(filter, 3.0F, this.sorting.getSelectedOption(), true);
            if (target != null) {
               Angle look = new Angle(player.method_36454(), player.method_36455());
               if (ReachHelper.raycastEntity(target, look, 3.0F, true) == target) {
                  long now = System.currentTimeMillis();
                  if (now - this.lastClickTime >= 450L) {
                     AttackInvoker clicks = (AttackInvoker)client;
                     int count = this.cps.getIntValue();

                     for(int i = 0; i < count; ++i) {
                        clicks.invokeAttack();
                     }

                     this.lastClickTime = now;
                  }
               }
            }
         }
      } else {
         this.targetSelector.update();
      }
   }
}
