package ru.wexside.module.combat;

import java.util.List;
import net.minecraft.class_1268;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1812;
import net.minecraft.class_1844;
import net.minecraft.class_310;
import net.minecraft.class_636;
import net.minecraft.class_6880;
import net.minecraft.class_746;
import net.minecraft.class_9334;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.AttackUrgency;
import ru.wexside.misc.ClickPolicy;
import ru.wexside.misc.CorrectionMode;
import ru.wexside.misc.InventoryAction;
import ru.wexside.misc.InventoryTask;
import ru.wexside.misc.PotionEntry;
import ru.wexside.misc.SwapTiming;
import ru.wexside.misc.TaskFlag;
import ru.wexside.misc.TaskPriority;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.util.Angle;
import ru.wexside.util.HotbarSlotLock;
import ru.wexside.util.InventoryController;
import ru.wexside.util.RotationController;
import ru.wexside.util.RotationIntent;

public class AutoPotionModule extends Module implements ConfigSerializable {
   private static final String OWNER = "auto_potion";
   private final BooleanSetting enabledSetting;
   private final MultiSelectSetting potions;
   private final List<PotionEntry> potionPresets = List.of(
      new PotionEntry("Fire Resistance", class_1294.field_5918),
      new PotionEntry("Strength", class_1294.field_5910),
      new PotionEntry("Speed", class_1294.field_5904)
   );
   private int lookTicks;
   private long lastUseTime;
   private Angle throwAngle;

   public AutoPotionModule(EventBus eventBus) {
      super(eventBus, "auto_potion", "Auto Potion", "Бросает зелья под себя при отсутствии эффекта", ModuleCategory.valueOf("COMBAT"));
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
      MultiSelectSetting potionsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("Fire Resistance", "Strength", "Speed")
            .selectAll(false)
            .optionListEnabled(false)
            .name("Potions")
            .id("potions")
            .description("Список зелий для использования"))
         .build();
      this.potions = potionsSetting;
      this.registerSetting(potionsSetting);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.onTick());
   }

   private void onTick() {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null) {
         if (this.lookTicks > 0) {
            this.tickLook(player);
         } else if (this.enabledSetting.isEnabled()) {
            if (System.currentTimeMillis() - this.lastUseTime >= 250L) {
               if (player.method_24828()) {
                  int slot = this.findPotionSlot(player);
                  if (slot != -1) {
                     this.throwAngle = new Angle(player.method_36454(), 90.0F);
                     this.lookTicks = 5;
                     this.applyLook(player, this.throwAngle);
                     if (slot < 9) {
                        this.throwFromHotbar(player, slot);
                     } else {
                        this.throwFromInventory(player, slot);
                     }

                     this.lastUseTime = System.currentTimeMillis();
                  }
               }
            }
         }
      }
   }

   private void tickLook(class_746 player) {
      RotationController rotations = WexSideClient.getRotationController();
      if (rotations == null) {
         this.lookTicks = 0;
         this.throwAngle = null;
      } else {
         --this.lookTicks;
         if (this.lookTicks > 0 && this.throwAngle != null) {
            this.applyLook(player, this.throwAngle);
         } else {
            this.releaseLook();
         }
      }
   }

   private void applyLook(class_746 player, Angle angle) {
      RotationController rotations = WexSideClient.getRotationController();
      if (rotations != null) {
         rotations.process2(new RotationIntent(player, null, angle, AttackUrgency.HIT, CorrectionMode.FOCUSED, false), "FT Snap");
      }
   }

   private void releaseLook() {
      RotationController rotations = WexSideClient.getRotationController();
      if (rotations != null) {
         rotations.update3();
      }

      this.throwAngle = null;
   }

   private void throwFromHotbar(class_746 player, int slot) {
      class_1661 inventory = player.method_31548();
      int selected = inventory.method_67532();
      if (selected != slot) {
         inventory.method_61496(slot);
      }

      this.useMainHand();
      HotbarSlotLock hotbar = WexSideClient.getHotbarSlotLock();
      if (hotbar != null && selected != slot) {
         hotbar.process(selected, 400L);
      }
   }

   private void throwFromInventory(class_746 player, int slot) {
      InventoryController inventory = WexSideClient.getInventoryController();
      if (inventory != null) {
         int selected = player.method_31548().method_67532();
         int containerSlot = slot < 9 ? slot + 36 : slot;
         inventory.submit(this.task(inventory.process2(containerSlot, selected, this::useMainHand, SwapTiming.DEFAULT), ClickPolicy.VISIBLE));
      }
   }

   private void useMainHand() {
      class_746 player = class_310.method_1551().field_1724;
      class_636 interactionManager = class_310.method_1551().field_1761;
      if (player != null && interactionManager != null) {
         if (!player.method_6047().method_7960()) {
            interactionManager.method_2919(player, class_1268.field_5808);
            player.method_6104(class_1268.field_5808);
         }
      }
   }

   private int findPotionSlot(class_746 player) {
      class_1661 inventory = player.method_31548();
      List<String> selected = this.potions.getSelectedOptions();

      for(PotionEntry preset : this.potionPresets) {
         if (selected.contains(preset.getName()) && !player.method_6059(preset.getEffect())) {
            int slot = this.findMatchingPotion(inventory, preset.getEffect());
            if (slot != -1) {
               return slot;
            }
         }
      }

      return -1;
   }

   private int findMatchingPotion(class_1661 inventory, class_6880<class_1291> effect) {
      for(int slot = 0; slot < 36; ++slot) {
         class_1799 stack = inventory.method_5438(slot);
         if (!stack.method_7960() && stack.method_7909() instanceof class_1812) {
            class_1844 contents = (class_1844)stack.method_58694(class_9334.field_49651);
            if (contents != null) {
               for(class_1293 instance : contents.method_57397()) {
                  if (instance.method_5579().equals(effect)) {
                     return slot;
                  }
               }
            }
         }
      }

      return -1;
   }

   private InventoryTask task(InventoryAction action, ClickPolicy policy) {
      return InventoryTask.builder().action(action).owner("auto_potion").flag(TaskFlag.DEFAULT).policy(policy).priority(TaskPriority.NORMAL).build();
   }
}
