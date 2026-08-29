package ru.wexside.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2350.class_2351;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.misc.ConsumableEspEffect;
import ru.wexside.misc.ConsumablesEspSettings;
import ru.wexside.misc.PlacementPreview;
import ru.wexside.render.world.ConsumableAreaRenderer;
import ru.wexside.setting.ColorSetting;

public final class PlacementConsumableEspRenderer implements ConsumableEspEffect {
   private final Map<UUID, PlacementPreview> map2 = new HashMap<>();
   static final int slot = 5;
   private final ConsumablesEspSettings consumablesEspSettings;
   static final float value = 22.5F;
   static final int slot2 = 3;
   static final int slot3 = 3;
   static final int slot4 = 5;

   public PlacementConsumableEspRenderer(ConsumablesEspSettings consumablesEspSettings) {
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

   private void process2(UUID uUID, class_1657 player, class_2338 pos, ColorSetting colorSetting, long l) {
      int n3 = 2;
      float f = player.method_36455();
      float f2 = this.process4(player.method_36454());
      boolean bl2 = this.process9(f2);
      int n;
      int n2;
      boolean bl;
      if (f < -45.0F) {
         bl = true;
         n2 = pos.method_10264() + 4;
         n = n2 - 1;
      } else if (f > 45.0F) {
         bl = true;
         n2 = pos.method_10264() - 1;
         n = n2 - 1;
      } else {
         bl = false;
         n2 = -1;
         n = 4;
      }

      if (bl) {
         class_238 box = this.process10(
            (double)(pos.method_10263() - n3),
            (double)n2,
            (double)(pos.method_10260() - n3),
            (double)(pos.method_10263() + n3 + 1),
            (double)n,
            (double)(pos.method_10260() + n3 + 1)
         );
         this.process7(uUID, pos, box);
         class_238 box2 = this.map2.get(uUID).box();
         if (box2 != null) {
            this.process5(box2, colorSetting, l);
         }
      } else if (bl2) {
         this.process6(uUID, player, pos, f2, n2, n, n3, colorSetting, l);
      } else {
         class_2350 process17 = player.method_5735();
         class_2338 pos2 = pos.method_10079(process17, 3);
         int n4 = pos2.method_10264() + n2;
         int n5 = pos2.method_10264() + n;
         class_238 box3 = process17.method_10166() == class_2351.field_11048
            ? this.process10(
               (double)pos2.method_10263(),
               (double)n4,
               (double)(pos2.method_10260() - n3),
               (double)(pos2.method_10263() + 1),
               (double)n5,
               (double)(pos2.method_10260() + n3 + 1)
            )
            : this.process10(
               (double)(pos2.method_10263() - n3),
               (double)n4,
               (double)pos2.method_10260(),
               (double)(pos2.method_10263() + n3 + 1),
               (double)n5,
               (double)(pos2.method_10260() + 1)
            );
         this.process7(uUID, pos2, box3);
         class_238 box4 = this.map2.get(uUID).box();
         if (box4 != null) {
            this.process5(box4, colorSetting, l);
         }
      }
   }

   @Override
   public boolean process3(class_1792 iiIilIIilI2) {
      return iiIilIIilI2 == class_1802.field_22021 || iiIilIIilI2 == class_1802.field_8614;
   }

   private float process4(float f) {
      float f2 = f % 360.0F;
      if (f2 < 0.0F) {
         f2 += 360.0F;
      }

      return f2;
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

   private void process6(UUID uUID, class_1657 player, class_2338 pos, float f, int n, int n2, int n3, ColorSetting colorSetting, long l) {
      class_2338 pos2 = pos.method_10079(player.method_5735(), 0);
      int n4 = pos2.method_10264() + n;
      int n5 = pos2.method_10264() + n2;
      int n6 = 0;
      int n7 = 0;
      if (f < 67.5F) {
         n6 = 1;
         n7 = 1;
         pos2 = pos2.method_10069(-2, 0, 2);
      } else if (f >= 112.5F && f < 157.5F) {
         n6 = -1;
         n7 = 1;
         pos2 = pos2.method_10069(-2, 0, -2);
      } else if (f >= 202.5F && f < 247.5F) {
         n6 = -1;
         n7 = -1;
         pos2 = pos2.method_10069(2, 0, -2);
      } else if (f >= 292.5F || f < 337.5F) {
         n6 = 1;
         n7 = -1;
         pos2 = pos2.method_10069(2, 0, 2);
      }

      this.process7(uUID, pos2, null);
      class_243 vec = this.map2.get(uUID).center();
      if (vec != null) {
         for(int i = -n3; i <= n3; ++i) {
            double d = vec.field_1352 + (double)(i * n6);
            double d2 = vec.field_1350 + (double)(i * n7);
            class_238 box = this.process10(d, (double)n4, d2, d + 1.0, (double)n5, d2 + 1.0);
            this.process5(box, colorSetting, l);
         }
      }
   }

   private void process7(UUID uUID2, class_2338 pos, class_238 box) {
      this.map2.computeIfAbsent(uUID2, uUID -> new PlacementPreview(pos, box)).update(pos, box);
   }

   @Override
   public void process5(WorldRenderEvent floatTypeEvent2, class_1657 player, ColorSetting colorSetting) {
      this.process8(player, colorSetting);
   }

   private void process8(class_1657 player, ColorSetting colorSetting) {
      class_1792 iiIilIIilI2 = player.method_6047().method_7909();
      boolean bl2 = iiIilIIilI2 == class_1802.field_22021;
      boolean bl = iiIilIIilI2 == class_1802.field_8614;
      if (!bl2 && !bl) {
         this.setUUID(player.method_5667());
      } else {
         long l = System.currentTimeMillis();
         class_2338 pos = player.method_24515();
         if (bl) {
            this.process11(player.method_5667(), pos, colorSetting, l);
         } else {
            this.process2(player.method_5667(), player, pos, colorSetting, l);
         }
      }
   }

   private boolean process9(float f) {
      for(float f2 : new float[]{45.0F, 135.0F, 225.0F, 315.0F}) {
         if (Math.abs(f - f2) < 22.5F) {
            return true;
         }
      }

      return false;
   }

   private class_238 process10(double d, double d2, double d3, double d4, double d5, double d6) {
      return new class_238(Math.min(d, d4), Math.min(d2, d5), Math.min(d3, d6), Math.max(d, d4), Math.max(d2, d5), Math.max(d3, d6));
   }

   private void process11(UUID uUID, class_2338 pos, ColorSetting colorSetting, long l) {
      int n = 1;
      class_238 box = this.process10(
         (double)(pos.method_10263() - n),
         (double)(pos.method_10264() - n + 1),
         (double)(pos.method_10260() - n),
         (double)(pos.method_10263() + n + 1),
         (double)(pos.method_10264() + n + 2),
         (double)(pos.method_10260() + n + 1)
      );
      this.process7(uUID, pos, box);
      class_238 box2 = this.map2.get(uUID).box();
      if (box2 != null) {
         this.process5(box2, colorSetting, l);
      }
   }
}
