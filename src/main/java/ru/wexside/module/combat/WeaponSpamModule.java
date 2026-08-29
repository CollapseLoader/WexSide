package ru.wexside.module.combat;

import java.util.List;
import net.minecraft.class_1753;
import net.minecraft.class_1764;
import net.minecraft.class_1799;
import net.minecraft.class_1835;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class WeaponSpamModule extends Module implements ConfigSerializable {
   private static final String BOW = "Bow";
   private static final String CROSSBOW = "Crossbow";
   private static final String TRIDENT = "Trident";
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("Флудит выстрелами из оружия")
         .withKeybind()
         .toggle())
      .build();
   private final MultiSelectSetting weapons;
   private final NumberSetting delay;

   public WeaponSpamModule(EventBus eventBus) {
      super(eventBus, "weapon_spam", "Weapon Spam", "Флудит выстрелами из оружия", ModuleCategory.valueOf("COMBAT"));
      this.registerSetting(this.enabledSetting);
      MultiSelectSetting weaponSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("Bow", "Crossbow", "Trident")
            .selectAll(false)
            .optionListEnabled(false)
            .name("Weapons")
            .id("weapons")
            .description("Типы оружия"))
         .build();
      this.weapons = weaponSetting;
      this.registerSetting(weaponSetting);
      this.delay = ((NumberSettingBuilder)NumberSetting.builder()
            .range(3.0, 20.0)
            .defaultValue(5.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Delay")
            .id("delay")
            .description("Задержка на лук (тики)")
            .visibleWhen(() -> this.weapons.getSelectedOptions().contains("Bow")))
         .build();
      this.registerSetting(this.delay);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.onTick());
   }

   private void onTick() {
      class_310 client = class_310.method_1551();
      class_746 player = client.field_1724;
      if (this.enabledSetting.isEnabled() && player != null && client.field_1761 != null) {
         if (player.method_6115()) {
            class_1799 stack = player.method_6030();
            List<String> selected = this.weapons.getSelectedOptions();
            int useTime = player.method_6048();
            if (selected.contains("Bow") && stack.method_7909() instanceof class_1753 && useTime >= this.delay.getIntValue()) {
               client.field_1761.method_2897(player);
            } else if (selected.contains("Crossbow") && stack.method_7909() instanceof class_1764) {
               if (useTime >= class_1764.method_7775(stack, player) && !class_1764.method_7781(stack)) {
                  client.field_1761.method_2897(player);
               }
            } else {
               if (selected.contains("Trident") && stack.method_7909() instanceof class_1835 && useTime >= 10) {
                  client.field_1761.method_2897(player);
               }
            }
         }
      }
   }
}
