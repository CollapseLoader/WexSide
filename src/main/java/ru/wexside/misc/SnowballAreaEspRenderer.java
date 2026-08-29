package ru.wexside.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.class_1657;
import net.minecraft.class_1680;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.render.world.ConsumableAreaRenderer;
import ru.wexside.render.world.SnowballImpactPredictor;
import ru.wexside.setting.ColorSetting;
import ru.wexside.util.ColorUtils;

public final class SnowballAreaEspRenderer implements ConsumableEspEffect {
   private final Map<UUID, PlacementPreview> map2 = new HashMap<>();
   static final int slot = 7;
   private final ConsumablesEspSettings consumablesEspSettings;

   public SnowballAreaEspRenderer(ConsumablesEspSettings consumablesEspSettings) {
      this.consumablesEspSettings = consumablesEspSettings;
   }

   @Override
   public void process(WorldRenderEvent floatTypeEvent2, class_1657 player, ColorSetting colorSetting) {
      this.process8(player, colorSetting);
   }

   @Override
   public void process2(WorldRenderEvent floatTypeEvent2, class_1680 snowballEntity, ColorSetting colorSetting) {
      if (!snowballEntity.method_5732()) {
         class_243 vec = SnowballImpactPredictor.predict(snowballEntity);
         if (vec != null) {
            class_2338 pos = class_2338.method_49638(vec);
            int n = 7;
            class_238 box = new class_238(
               (double)(pos.method_10263() - n),
               (double)pos.method_10264(),
               (double)(pos.method_10260() - n),
               (double)(pos.method_10263() + n + 1),
               (double)(pos.method_10264() + 1),
               (double)(pos.method_10260() + n + 1)
            );
            this.process5(box, colorSetting, this.getLongType());
         }
      }
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
      return iiIilIIilI2 == class_1802.field_8543;
   }

   private long getLongType() {
      return System.currentTimeMillis();
   }

   private void process5(class_238 box, ColorSetting colorSetting, long l) {
      ConsumableAreaRenderer.renderBox(
         box,
         ColorUtils.multiplyAlpha(ConsumablesEspSettings.process5(colorSetting, 0.0F), 0.2F),
         ColorUtils.multiplyAlpha(ConsumablesEspSettings.process5(colorSetting, 1.0F), 0.2F),
         ColorUtils.multiplyAlpha(ConsumablesEspSettings.process5(colorSetting, 0.0F), 0.65F),
         ColorUtils.multiplyAlpha(ConsumablesEspSettings.process5(colorSetting, 1.0F), 0.65F),
         l
      );
   }

   @Override
   public void process5(WorldRenderEvent floatTypeEvent2, class_1657 player, ColorSetting colorSetting) {
      this.process8(player, colorSetting);
   }

   private void process8(class_1657 player, ColorSetting colorSetting) {
      if (!this.process3(player.method_6047().method_7909())) {
         this.setUUID(player.method_5667());
      } else {
         class_243 vec = SnowballImpactPredictor.predict(player);
         if (vec == null) {
            this.update();
         } else {
            class_2338 pos = class_2338.method_49638(vec);
            int n = 3;
            class_238 box = new class_238(
               (double)(pos.method_10263() - n),
               (double)pos.method_10264(),
               (double)(pos.method_10260() - n),
               (double)(pos.method_10263() + n + 1),
               (double)(pos.method_10264() + 1),
               (double)(pos.method_10260() + n + 1)
            );
            this.map2.computeIfAbsent(player.method_5667(), uUID -> new PlacementPreview(pos, box)).update(pos, box);
            class_238 box2 = this.map2.get(player.method_5667()).box();
            if (box2 != null) {
               this.process5(box2, colorSetting, this.getLongType());
            }
         }
      }
   }
}
