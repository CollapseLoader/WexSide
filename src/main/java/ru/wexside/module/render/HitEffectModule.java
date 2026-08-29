package ru.wexside.module.render;

import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_12249;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_9801;
import org.joml.Matrix4f;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EntityAttackEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class HitEffectModule extends Module implements ConfigSerializable {
   private static final int GHOST_LAYERS = 3;
   private static final float LAYER_GAP = 0.08F;
   private static final double LAYER_SPACING = 0.12;
   private static final double RISE = 0.18;
   private static final int MAX_GHOSTS = 24;
   private static final class_243 FALLBACK_LOOK = new class_243(0.0, 0.0, 1.0);
   private final BooleanSetting enabledSetting;
   private final ColorSetting color;
   private final NumberSetting lifeTime;
   private final NumberSetting distance;
   private final BooleanSetting filled;
   private final BooleanSetting ignoreDepth;
   private final List<HitEffectModule.Ghost> ghosts = new ArrayList<>();

   public HitEffectModule(EventBus eventBus) {
      super(eventBus, "hit_effect", "Hit Effect", "Призрачный след модели игрока при ударе", ModuleCategory.valueOf("RENDER"));
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Включить призрачный след при ударе по игроку")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      ColorSetting colorSetting = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(0)
            .name("Color")
            .id("color")
            .description("Цвет призрака")
            .aliases("color", "цвет"))
         .build();
      colorSetting.setPrimaryColor(0, -11753627);
      colorSetting.setPrimaryColor(1, -1543135);
      colorSetting.setPrimaryColor(2, -9279489);
      colorSetting.setPrimaryColor(3, -46001);
      colorSetting.setPrimaryColor(4, -13218);
      colorSetting.setPrimaryColor(5, -10582785);
      colorSetting.setPrimaryColor(6, -2732032);
      this.color = colorSetting;
      this.registerSetting(colorSetting);
      this.lifeTime = ((NumberSettingBuilder)NumberSetting.builder()
            .range(10.0, 30.0)
            .defaultValue(20.0)
            .multiplier(50.0)
            .precision(0)
            .animationSpeed(20.0F)
            .snapTo(5.0)
            .name("Life time")
            .id("life_time")
            .description("Время жизни следа"))
         .build();
      this.registerSetting(this.lifeTime);
      this.distance = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.5, 3.0)
            .defaultValue(2.0)
            .multiplier(1.0)
            .precision(1)
            .animationSpeed(20.0F)
            .showMarkers()
            .snapTo(0.5)
            .name("Distance")
            .id("distance")
            .description("Дальность вылета следа"))
         .build();
      this.registerSetting(this.distance);
      this.filled = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Filled")
            .id("filled")
            .description("Заливать модель призрака"))
         .build();
      this.registerSetting(this.filled);
      this.ignoreDepth = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Ignore depth")
            .id("ignore_depth")
            .description("Рисовать сквозь стены"))
         .build();
      this.registerSetting(this.ignoreDepth);
   }

   @Override
   protected void initialize() {
      this.listen(EntityAttackEvent.class, this::onEntityAttack);
      this.listen(WorldRenderEvent.class, this::onWorldRender);
   }

   private void onEntityAttack(EntityAttackEvent event) {
      class_310 client = class_310.method_1551();
      if (this.enabledSetting.isEnabled() && client.field_1724 != null && client.field_1687 != null) {
         class_1297 target = event.getEntity();
         class_1657 player;
         if (target instanceof class_1657 && (player = (class_1657)target) != client.field_1724 && player.method_5805() && !player.method_5732()) {
            this.ghosts.add(new HitEffectModule.Ghost(player, this.lookDirection(client), System.currentTimeMillis()));
            this.trimOverflow();
         }
      }
   }

   private void onWorldRender(WorldRenderEvent event) {
      class_310 client = class_310.method_1551();
      if (!this.enabledSetting.isEnabled()) {
         this.ghosts.clear();
      } else {
         class_4184 camera = client.field_1773.method_19418();
         class_243 cameraPos = camera.method_71156();
         if (client.field_1687 != null && client.field_1724 != null && cameraPos != null) {
            if (!this.ghosts.isEmpty()) {
               long now = System.currentTimeMillis();
               Iterator<HitEffectModule.Ghost> iterator = this.ghosts.iterator();
               Matrix4f matrix = new Matrix4f().rotation(camera.method_23767());
               class_287 fill = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1576);
               class_287 outline = class_289.method_1348().method_60827(class_5596.field_29344, class_290.field_1576);

               while(iterator.hasNext()) {
                  HitEffectModule.Ghost ghost = iterator.next();
                  if (ghost.isExpired(now, this.lifetimeMs())) {
                     iterator.remove();
                  } else {
                     this.drawGhost(ghost, now, cameraPos, matrix, fill, outline);
                  }
               }

               class_9801 fillBuffer = fill.method_60794();
               if (fillBuffer != null) {
                  class_12249.method_76023().method_60895(fillBuffer);
               }

               class_9801 outlineBuffer = outline.method_60794();
               if (outlineBuffer != null) {
                  class_12249.method_76015().method_60895(outlineBuffer);
               }
            }
         } else {
            this.ghosts.clear();
         }
      }
   }

   private void drawGhost(HitEffectModule.Ghost ghost, long now, class_243 cameraPos, Matrix4f matrix, class_287 fill, class_287 outline) {
      float progress = ghost.progress(now, this.lifetimeMs());

      for(int layer = 2; layer >= 0; --layer) {
         float delayed = Math.max(0.0F, progress - (float)layer * 0.08F);
         float fade = 1.0F - progress;
         fade *= fade;
         if (!((fade = fade * (1.0F - (float)layer * 0.2F)) <= 0.025F)) {
            float eased = this.easeOutCubic(delayed);
            double travel = this.distance.getValue() * (double)eased - (double)layer * 0.12;
            double rise = 0.18 * (double)this.sineIn(delayed);
            double wobble = Math.sin((double)(delayed + (float)layer * 0.18F) * Math.PI * 2.0) * 0.025;
            class_243 pos = ghost.origin.method_1019(ghost.look.method_1021(travel)).method_1031(0.0, rise + wobble, 0.0);
            float scale = 1.0F + 0.03F * (1.0F - delayed) + (float)layer * 0.012F;
            int outlineAlpha = class_3532.method_15340((int)(210.0F * fade), 0, 255);
            int fillAlpha = class_3532.method_15340((int)(110.0F * fade), 0, 255);
            int base = this.color.getColor();
            class_238 box = this.playerBox(ghost, pos, scale);
            if (this.filled.isEnabled() && fillAlpha > 3) {
               this.drawFilledBox(fill, matrix, cameraPos, box, withAlpha(base, fillAlpha));
            }

            if (outlineAlpha > 3) {
               this.drawBoxOutline(outline, matrix, cameraPos, box, withAlpha(base, outlineAlpha));
            }
         }
      }
   }

   private class_238 playerBox(HitEffectModule.Ghost ghost, class_243 pos, float scale) {
      double half = 0.3 * (double)scale;
      double height = (double)(ghost.height * scale);
      return new class_238(pos.field_1352 - half, pos.field_1351, pos.field_1350 - half, pos.field_1352 + half, pos.field_1351 + height, pos.field_1350 + half);
   }

   private void drawFilledBox(class_287 consumer, Matrix4f matrix, class_243 cameraPos, class_238 box, int color) {
      this.quad(
         consumer,
         matrix,
         cameraPos,
         box.field_1323,
         box.field_1322,
         box.field_1321,
         box.field_1320,
         box.field_1322,
         box.field_1321,
         box.field_1320,
         box.field_1322,
         box.field_1324,
         box.field_1323,
         box.field_1322,
         box.field_1324,
         color
      );
      this.quad(
         consumer,
         matrix,
         cameraPos,
         box.field_1323,
         box.field_1325,
         box.field_1321,
         box.field_1323,
         box.field_1325,
         box.field_1324,
         box.field_1320,
         box.field_1325,
         box.field_1324,
         box.field_1320,
         box.field_1325,
         box.field_1321,
         color
      );
      this.quad(
         consumer,
         matrix,
         cameraPos,
         box.field_1323,
         box.field_1322,
         box.field_1321,
         box.field_1323,
         box.field_1325,
         box.field_1321,
         box.field_1320,
         box.field_1325,
         box.field_1321,
         box.field_1320,
         box.field_1322,
         box.field_1321,
         color
      );
      this.quad(
         consumer,
         matrix,
         cameraPos,
         box.field_1323,
         box.field_1322,
         box.field_1324,
         box.field_1320,
         box.field_1322,
         box.field_1324,
         box.field_1320,
         box.field_1325,
         box.field_1324,
         box.field_1323,
         box.field_1325,
         box.field_1324,
         color
      );
      this.quad(
         consumer,
         matrix,
         cameraPos,
         box.field_1323,
         box.field_1322,
         box.field_1321,
         box.field_1323,
         box.field_1322,
         box.field_1324,
         box.field_1323,
         box.field_1325,
         box.field_1324,
         box.field_1323,
         box.field_1325,
         box.field_1321,
         color
      );
      this.quad(
         consumer,
         matrix,
         cameraPos,
         box.field_1320,
         box.field_1322,
         box.field_1321,
         box.field_1320,
         box.field_1325,
         box.field_1321,
         box.field_1320,
         box.field_1325,
         box.field_1324,
         box.field_1320,
         box.field_1322,
         box.field_1324,
         color
      );
   }

   private void drawBoxOutline(class_287 consumer, Matrix4f matrix, class_243 cameraPos, class_238 box, int color) {
      this.line(consumer, matrix, cameraPos, box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1322, box.field_1321, color);
      this.line(consumer, matrix, cameraPos, box.field_1320, box.field_1322, box.field_1321, box.field_1320, box.field_1322, box.field_1324, color);
      this.line(consumer, matrix, cameraPos, box.field_1320, box.field_1322, box.field_1324, box.field_1323, box.field_1322, box.field_1324, color);
      this.line(consumer, matrix, cameraPos, box.field_1323, box.field_1322, box.field_1324, box.field_1323, box.field_1322, box.field_1321, color);
      this.line(consumer, matrix, cameraPos, box.field_1323, box.field_1325, box.field_1321, box.field_1320, box.field_1325, box.field_1321, color);
      this.line(consumer, matrix, cameraPos, box.field_1320, box.field_1325, box.field_1321, box.field_1320, box.field_1325, box.field_1324, color);
      this.line(consumer, matrix, cameraPos, box.field_1320, box.field_1325, box.field_1324, box.field_1323, box.field_1325, box.field_1324, color);
      this.line(consumer, matrix, cameraPos, box.field_1323, box.field_1325, box.field_1324, box.field_1323, box.field_1325, box.field_1321, color);
      this.line(consumer, matrix, cameraPos, box.field_1323, box.field_1322, box.field_1321, box.field_1323, box.field_1325, box.field_1321, color);
      this.line(consumer, matrix, cameraPos, box.field_1320, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1321, color);
      this.line(consumer, matrix, cameraPos, box.field_1320, box.field_1322, box.field_1324, box.field_1320, box.field_1325, box.field_1324, color);
      this.line(consumer, matrix, cameraPos, box.field_1323, box.field_1322, box.field_1324, box.field_1323, box.field_1325, box.field_1324, color);
   }

   private void quad(
      class_287 consumer,
      Matrix4f matrix,
      class_243 cameraPos,
      double x1,
      double y1,
      double z1,
      double x2,
      double y2,
      double z2,
      double x3,
      double y3,
      double z3,
      double x4,
      double y4,
      double z4,
      int color
   ) {
      this.vertex(consumer, matrix, cameraPos, x1, y1, z1, color);
      this.vertex(consumer, matrix, cameraPos, x2, y2, z2, color);
      this.vertex(consumer, matrix, cameraPos, x3, y3, z3, color);
      this.vertex(consumer, matrix, cameraPos, x4, y4, z4, color);
   }

   private void line(class_287 consumer, Matrix4f matrix, class_243 cameraPos, double x1, double y1, double z1, double x2, double y2, double z2, int color) {
      this.vertex(consumer, matrix, cameraPos, x1, y1, z1, color);
      this.vertex(consumer, matrix, cameraPos, x2, y2, z2, color);
   }

   private void vertex(class_287 consumer, Matrix4f matrix, class_243 cameraPos, double x, double y, double z, int color) {
      int alpha = color >>> 24 & 0xFF;
      if (alpha == 0) {
         alpha = 255;
      }

      consumer.method_22918(matrix, (float)(x - cameraPos.field_1352), (float)(y - cameraPos.field_1351), (float)(z - cameraPos.field_1350))
         .method_1336(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, alpha);
   }

   private void trimOverflow() {
      int overflow = this.ghosts.size() - 24;
      if (overflow > 0) {
         this.ghosts.subList(0, overflow).clear();
      }
   }

   private class_243 lookDirection(class_310 client) {
      class_243 look = client.field_1724 != null ? client.field_1724.method_5828(1.0F) : class_243.field_1353;
      return look.method_1027() <= 1.0E-6 ? FALLBACK_LOOK.method_1029() : look.method_1029();
   }

   private float sineIn(float value) {
      return (float)Math.sin((double)class_3532.method_15363(value, 0.0F, 1.0F) * Math.PI * 0.5);
   }

   private float easeOutCubic(float value) {
      float clamped = class_3532.method_15363(value, 0.0F, 1.0F);
      float inverse = 1.0F - clamped;
      return 1.0F - inverse * inverse * inverse;
   }

   private long lifetimeMs() {
      return Math.max(1L, this.lifeTime.getLongValue());
   }

   private static int withAlpha(int color, int alpha) {
      return color & 16777215 | alpha << 24;
   }

   static final class Ghost {
      final class_243 origin;
      final class_243 look;
      final float height;
      final long spawnTime;

      Ghost(class_1657 player, class_243 look, long spawnTime) {
         this.origin = new class_243(player.method_23317(), player.method_23318(), player.method_23321());
         this.look = look;
         this.height = player.method_17682();
         this.spawnTime = spawnTime;
      }

      boolean isExpired(long now, long lifetime) {
         return now - this.spawnTime >= lifetime;
      }

      float progress(long now, long lifetime) {
         return class_3532.method_15363((float)(now - this.spawnTime) / (float)lifetime, 0.0F, 1.0F);
      }
   }
}
