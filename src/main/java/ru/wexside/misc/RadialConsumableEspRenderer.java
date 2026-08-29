package ru.wexside.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.render.world.ConsumableAreaRenderer;
import ru.wexside.setting.ColorSetting;
import ru.wexside.util.ColorUtils;

public final class RadialConsumableEspRenderer implements ConsumableEspEffect {
   private final Map<UUID, PlacementPreview> map2 = new HashMap<>();
   private final ConsumablesEspSettings consumablesEspSettings;
   static final int slot = 57;
   static final double value = 1.0;
   static final double value2 = 9.0;

   public RadialConsumableEspRenderer(ConsumablesEspSettings consumablesEspSettings) {
      this.consumablesEspSettings = consumablesEspSettings;
   }

   @Override
   public void process(WorldRenderEvent floatTypeEvent2, class_1657 player, ColorSetting colorSetting) {
      this.process8(player, colorSetting);
   }

   @Override
   public void update() {
      this.map2.clear();
   }

   @Override
   public void setSet(Set<UUID> set) {
      this.map2.keySet().removeIf(uUID -> !set.contains(uUID));
   }

   @Override
   public void setUUID(UUID uUID) {
      this.map2.remove(uUID);
   }

   @Override
   public boolean process3(class_1792 iiIilIIilI2) {
      return iiIilIIilI2 == class_1802.field_8450 || iiIilIIilI2 == class_1802.field_8479;
   }

   @Override
   public void process5(WorldRenderEvent floatTypeEvent2, class_1657 player, ColorSetting colorSetting) {
      this.process8(player, colorSetting);
   }

   private void process8(class_1657 player, ColorSetting colorSetting) {
      class_1792 iiIilIIilI2 = player.method_6047().method_7909();
      boolean bl2 = iiIilIIilI2 == class_1802.field_8450;
      boolean bl = iiIilIIilI2 == class_1802.field_8479;
      if (!bl2 && !bl) {
         this.setUUID(player.method_5667());
      } else {
         class_2338 pos = player.method_24515();
         this.map2.computeIfAbsent(player.method_5667(), uUID -> new PlacementPreview(pos, null)).update(pos, null);
         class_243 vec = this.map2.get(player.method_5667()).center();
         if (vec != null) {
            ConsumableAreaRenderer.renderCylinder(
               vec.field_1352,
               vec.field_1351,
               vec.field_1350,
               9.0,
               1.0,
               ColorUtils.multiplyAlpha(ConsumablesEspSettings.process5(colorSetting, 0.0F), 0.2F),
               ColorUtils.multiplyAlpha(ConsumablesEspSettings.process5(colorSetting, 1.0F), 0.2F),
               ColorUtils.multiplyAlpha(ConsumablesEspSettings.process5(colorSetting, 0.0F), 0.65F),
               ColorUtils.multiplyAlpha(ConsumablesEspSettings.process5(colorSetting, 1.0F), 0.65F),
               System.currentTimeMillis()
            );
         }
      }
   }
}
