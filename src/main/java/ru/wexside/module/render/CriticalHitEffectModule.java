package ru.wexside.module.render;

import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_12249;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4050;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import org.joml.Matrix4f;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EntityAttackEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.ColorUtils;

public final class CriticalHitEffectModule extends Module implements ConfigSerializable {
   private static final int MAX_BLOCKS = 420;
   private static final int MAX_GHOSTS = 24;
   private static final int MAX_SPARKS = 260;
   private static final float SPARK_CHANCE = 0.2F;
   private final BooleanSetting enabledSetting;
   private final ColorSetting color;
   private final NumberSetting lifeTime;
   private final NumberSetting rise;
   private final NumberSetting pulseRadius;
   private final BooleanSetting filled;
   private final BooleanSetting ignoreDepth;
   private final List<CriticalHitEffectModule.CritGhost> ghosts = new ArrayList<>();
   private final List<CriticalHitEffectModule.Spark> sparks = new ArrayList<>();
   private long lastSparkTime = Long.MIN_VALUE;

   public CriticalHitEffectModule(EventBus eventBus) {
      super(eventBus, "crit_effect", "CriticalHit Effect", "Эффект души и волна по блокам при крит-ударе", ModuleCategory.valueOf("RENDER"));
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Включить эффект при крит-ударе по игроку")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      ColorSetting colorSetting = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(0)
            .name("Color")
            .id("color")
            .description("Цвет души и волны")
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
      this.rise = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.5, 3.0)
            .defaultValue(1.5)
            .multiplier(1.0)
            .precision(1)
            .animationSpeed(20.0F)
            .markers(0.5)
            .snapTo(0.5)
            .name("Rise")
            .id("rise")
            .description("Высота подъёма души"))
         .build();
      this.registerSetting(this.rise);
      this.pulseRadius = ((NumberSettingBuilder)NumberSetting.builder()
            .range(3.0, 8.0)
            .defaultValue(5.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .markers(1.0)
            .name("Pulse radius")
            .id("pulse_radius")
            .description("Радиус волны по блокам"))
         .build();
      this.registerSetting(this.pulseRadius);
      this.filled = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Filled")
            .id("filled")
            .description("Заливать модель души"))
         .build();
      this.registerSetting(this.filled);
      this.ignoreDepth = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Ignore depth")
            .id("ignore_depth")
            .description("Рисовать эффект сквозь стены"))
         .build();
      this.registerSetting(this.ignoreDepth);
   }

   @Override
   protected void initialize() {
      this.listen(EntityAttackEvent.class, this::onEntityAttack);
      this.listen(WorldRenderEvent.class, this::onWorldRender);
      this.listen(WorldSessionEvent.class, event -> this.clear());
   }

   private void onEntityAttack(EntityAttackEvent event) {
      class_310 client = class_310.method_1551();
      if (this.enabledSetting.isEnabled() && client.field_1724 != null && client.field_1687 != null) {
         class_1297 target = event.getEntity();
         class_1657 player;
         if (target instanceof class_1657 && (player = (class_1657)target) != client.field_1724 && player.method_5805() && !player.method_5732()) {
            if (this.isCriticalHit(client)) {
               class_243 origin = this.groundPoint(client.field_1687, player);
               this.ghosts
                  .add(new CriticalHitEffectModule.CritGhost(player, origin, this.collectBlocks(client.field_1687, origin), System.currentTimeMillis()));
               this.trimGhosts();
            }
         }
      }
   }

   private void onWorldRender(WorldRenderEvent event) {
      class_310 client = class_310.method_1551();
      if (!this.enabledSetting.isEnabled()) {
         this.clear();
      } else {
         class_4184 camera = client.field_1773.method_19418();
         class_243 cameraPos = camera.method_71156();
         if (client.field_1687 != null && client.field_1724 != null && cameraPos != null) {
            if (!this.ghosts.isEmpty() || !this.sparks.isEmpty()) {
               long now = System.currentTimeMillis();
               this.tickSparks(now);
               boolean drawGhosts = !this.ghosts.isEmpty();
               Matrix4f matrix = new Matrix4f().rotation(camera.method_23767());
               class_287 fill = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1576);
               class_287 lines = class_289.method_1348().method_60827(class_5596.field_29344, class_290.field_1576);
               Iterator<CriticalHitEffectModule.CritGhost> iterator = this.ghosts.iterator();

               while(iterator.hasNext()) {
                  CriticalHitEffectModule.CritGhost ghost = iterator.next();
                  if (ghost.isExpired(now, this.lifetimeMs())) {
                     iterator.remove();
                  } else {
                     this.drawPulse(ghost, now, cameraPos, matrix, fill, lines);
                     if (drawGhosts) {
                        this.drawSoul(ghost, now, cameraPos, matrix, fill, lines);
                     }
                  }
               }

               this.drawSparks(cameraPos, matrix, fill);
               class_12249.method_76023().method_60895(fill.method_60800());
               class_12249.method_76015().method_60895(lines.method_60800());
            }
         } else {
            this.clear();
         }
      }
   }

   private void drawPulse(CriticalHitEffectModule.CritGhost ghost, long now, class_243 cameraPos, Matrix4f matrix, class_287 fill, class_287 lines) {
      if (!ghost.blocks.isEmpty()) {
         float progress = ghost.progress(now, this.lifetimeMs());
         float fade = 1.0F - progress;
         if (!((fade = fade * fade) <= 0.025F)) {
            float eased = this.easeOutCubic(progress);
            double radius = this.pulseRadius.getValue() * (double)eased;
            double band = (double)(0.95F + (1.0F - eased) * 0.22F);
            int base = this.color.getColor();
            int highlight = ColorUtils.lerp(base, -1, 0.38F);

            for(CriticalHitEffectModule.BlockSample sample : ghost.blocks) {
               float ring = 1.0F - (float)class_3532.method_15350(Math.abs(sample.distance - radius) / band, 0.0, 1.0);
               float strength;
               if (!(ring <= 0.04F) && !((strength = ring * fade) <= 0.02F)) {
                  if (!sample.sparked && radius + band * 0.45 >= sample.distance && ring > 0.55F) {
                     sample.sparked = true;
                     if (ThreadLocalRandom.current().nextFloat() < 0.2F) {
                        this.spawnSparks(sample, ghost.origin, strength, highlight);
                     }
                  }

                  int inner = ColorUtils.multiplyAlpha(base, Math.min(1.0F, strength * 0.38F * 0.72F));
                  int mid = ColorUtils.multiplyAlpha(highlight, Math.min(1.0F, strength * 0.22F));
                  int outer = ColorUtils.multiplyAlpha(highlight, Math.min(1.0F, strength * 0.11F));
                  int far = ColorUtils.multiplyAlpha(highlight, Math.min(1.0F, strength * 0.06F));
                  int edge = ColorUtils.multiplyAlpha(highlight, Math.min(1.0F, strength));
                  double inflate = 0.018 + (double)ring * 0.04;

                  for(class_238 local : sample.boxes) {
                     class_238 world = local.method_989((double)sample.pos.method_10263(), (double)sample.pos.method_10264(), (double)sample.pos.method_10260());
                     this.drawFilledBox(fill, matrix, cameraPos, world.method_1014(inflate * 2.8), far);
                     this.drawFilledBox(fill, matrix, cameraPos, world.method_1014(inflate * 1.8), outer);
                     this.drawFilledBox(fill, matrix, cameraPos, world.method_1014(inflate * 0.95), mid);
                     this.drawFilledBox(fill, matrix, cameraPos, world, inner);
                  }

                  this.drawBoxOutline(lines, matrix, cameraPos, class_238.method_30048(class_243.method_24953(sample.pos), 1.0, 1.0, 1.0), edge);
               }
            }
         }
      }
   }

   private void drawSoul(CriticalHitEffectModule.CritGhost ghost, long now, class_243 cameraPos, Matrix4f matrix, class_287 fill, class_287 lines) {
      float progress = ghost.progress(now, this.lifetimeMs());
      float eased = this.easeOutCubic(progress);
      float fade = 1.0F - progress;
      if (!((fade = fade * fade) <= 0.025F)) {
         double lift = this.rise.getValue() * (double)eased;
         class_243 pos = ghost.origin.method_1031(0.0, lift, 0.0);
         int base = this.color.getColor();
         int highlight = ColorUtils.lerp(base, -1, 0.26F);
         float scale = 1.0F + (1.0F - progress) * 0.05F + (float)Math.sin((double)progress * Math.PI) * 0.12F;
         int outlineAlpha = class_3532.method_15340((int)(230.0F * fade), 0, 255);
         int fillAlpha = class_3532.method_15340((int)(155.0F * fade), 0, 255);
         class_238 box = this.playerBox(ghost, pos, scale);
         if (this.filled.isEnabled() && fillAlpha > 3) {
            this.drawFilledBox(
               fill,
               matrix,
               cameraPos,
               box.method_1014(0.15),
               ColorUtils.withAlpha(highlight, (float)class_3532.method_15340((int)((float)fillAlpha * 0.34F), 0, 255))
            );
            this.drawFilledBox(
               fill,
               matrix,
               cameraPos,
               box.method_1014(0.07),
               ColorUtils.withAlpha(highlight, (float)class_3532.method_15340((int)((float)fillAlpha * 0.66F), 0, 255))
            );
            this.drawFilledBox(fill, matrix, cameraPos, box, ColorUtils.withAlpha(base, (float)fillAlpha));
         }

         if (outlineAlpha > 3) {
            this.drawBoxOutline(lines, matrix, cameraPos, box, ColorUtils.withAlpha(highlight, (float)outlineAlpha) & 16777215 | outlineAlpha << 24);
         }
      }
   }

   private void spawnSparks(CriticalHitEffectModule.BlockSample sample, class_243 origin, float strength, int color) {
      ThreadLocalRandom random = ThreadLocalRandom.current();
      class_243 away = new class_243(sample.centerX() - origin.field_1352, 0.0, sample.centerZ() - origin.field_1350);
      if (away.method_1027() <= 1.0E-6) {
         away = new class_243(random.nextDouble(-1.0, 1.0), 0.0, random.nextDouble(-1.0, 1.0));
      }

      away = away.method_1029();
      class_243 side = new class_243(-away.field_1350, 0.0, away.field_1352);
      boolean burst = random.nextFloat() < 0.1F;
      int count = burst ? 4 : 2;

      for(int i = 0; i < count; ++i) {
         double along = random.nextDouble(-0.22, 0.22);
         double out = random.nextDouble(-0.12, 0.12);
         class_243 pos = new class_243(
            sample.centerX() + side.field_1352 * along + away.field_1352 * out,
            sample.centerY() + random.nextDouble(0.02, 0.1),
            sample.centerZ() + side.field_1350 * along + away.field_1350 * out
         );
         class_243 velocity = away.method_1021(random.nextDouble(0.02, 0.1))
            .method_1019(side.method_1021(random.nextDouble(-0.05, 0.05)))
            .method_1031(0.0, random.nextDouble(0.04, 0.16), 0.0);
         int tint = ColorUtils.lerp(color, -1, (double)(random.nextFloat() * 0.45F));
         this.sparks
            .add(
               new CriticalHitEffectModule.Spark(
                  pos,
                  velocity,
                  tint,
                  0.028F + random.nextFloat() * 0.035F,
                  Math.min(1.0F, strength * (0.5F + random.nextFloat() * 0.28F)),
                  0.18F + random.nextFloat() * 0.22F
               )
            );
      }

      int overflow = this.sparks.size() - 260;
      if (overflow > 0) {
         this.sparks.subList(0, overflow).clear();
      }
   }

   private void tickSparks(long now) {
      if (this.lastSparkTime == Long.MIN_VALUE) {
         this.lastSparkTime = now;
      } else {
         double delta = Math.min((double)(now - this.lastSparkTime) / 1000.0, 0.05);
         this.lastSparkTime = now;

         for(int i = this.sparks.size() - 1; i >= 0; --i) {
            CriticalHitEffectModule.Spark spark = this.sparks.get(i);
            spark.tick(delta);
            if (spark.isDead()) {
               this.sparks.remove(i);
            }
         }
      }
   }

   private void drawSparks(class_243 cameraPos, Matrix4f matrix, class_287 fill) {
      if (!this.sparks.isEmpty()) {
         for(CriticalHitEffectModule.Spark spark : this.sparks) {
            if (!(spark.life <= 0.02F) && !(spark.size <= 0.004F)) {
               int tint = ColorUtils.multiplyAlpha(ColorUtils.lerp(spark.color, -1, 0.12F), Math.min(1.0F, spark.life));
               this.drawBillboard(fill, matrix, cameraPos, spark.pos, spark.size, tint);
            }
         }
      }
   }

   private List<CriticalHitEffectModule.BlockSample> collectBlocks(class_638 world, class_243 origin) {
      ArrayList<CriticalHitEffectModule.BlockSample> samples = new ArrayList<>();
      if (world == null) {
         return samples;
      } else {
         int extent = Math.max(2, class_3532.method_15357(this.pulseRadius.getValue()) + 2);
         class_2338 center = class_2338.method_49638(origin);
         double maxDistance = this.pulseRadius.getValue() + 0.95F + 1.0;

         for(int x = -extent; x <= extent; ++x) {
            for(int y = -extent; y <= extent; ++y) {
               for(int z = -extent; z <= extent; ++z) {
                  class_2338 pos = new class_2338(center.method_10263() + x, center.method_10264() + y, center.method_10260() + z);
                  class_2680 state = world.method_8320(pos);
                  class_265 shape = state.method_26218(world, pos);
                  List boxes;
                  double distance;
                  if (!shape.method_1110()
                     && this.hasAirNeighbor(world, pos)
                     && !((distance = this.sampleDistance(origin, pos, boxes = shape.method_1090())) > maxDistance)) {
                     samples.add(new CriticalHitEffectModule.BlockSample(pos, boxes, distance));
                  }
               }
            }
         }

         samples.sort(Comparator.comparingDouble(sample -> sample.distance));
         return samples.size() > 420 ? new ArrayList<>(samples.subList(0, 420)) : samples;
      }
   }

   private class_243 groundPoint(class_638 world, class_1657 player) {
      class_243 pos = new class_243(player.method_23317(), player.method_23318(), player.method_23321());
      if (world == null) {
         return pos;
      } else {
         int x = class_3532.method_15357(pos.field_1352);
         int z = class_3532.method_15357(pos.field_1350);
         int y = class_3532.method_15357(pos.field_1351 + 0.35);

         for(int i = 0; i <= 8; ++i) {
            class_2338 blockPos = new class_2338(x, y - i, z);
            class_265 shape = world.method_8320(blockPos).method_26218(world, blockPos);
            if (!shape.method_1110()) {
               double top = shape.method_1090().stream().mapToDouble(box -> box.field_1325).max().orElse(1.0);
               return new class_243(pos.field_1352, (double)blockPos.method_10264() + top + 0.008, pos.field_1350);
            }
         }

         return pos;
      }
   }

   private boolean hasAirNeighbor(class_638 world, class_2338 pos) {
      if (world == null) {
         return false;
      } else {
         for(class_2350 direction : class_2350.values()) {
            class_2338 neighbor = pos.method_10093(direction);
            if (world.method_8320(neighbor).method_26218(world, neighbor).method_1110()) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean isCriticalHit(class_310 client) {
      if (client.field_1724 == null) {
         return false;
      } else {
         return client.field_1724.field_6017 > 0.0
            && !client.field_1724.method_24828()
            && !client.field_1724.method_6101()
            && !client.field_1724.method_5799()
            && !client.field_1724.method_5869()
            && client.field_1724.method_18376() != class_4050.field_18079
            && !client.field_1724.method_5624()
            && !client.field_1724.method_6059(class_1294.field_5919)
            && client.field_1724.method_7261(0.5F) > 0.9F;
      }
   }

   private double sampleDistance(class_243 origin, class_2338 pos, List<class_238> boxes) {
      double y = (double)pos.method_10264() + boxes.stream().mapToDouble(box -> (box.field_1322 + box.field_1325) * 0.5).average().orElse(0.5);
      double dx = (double)pos.method_10263() + 0.5 - origin.field_1352;
      double dy = y - origin.field_1351;
      double dz = (double)pos.method_10260() + 0.5 - origin.field_1350;
      return Math.sqrt(dx * dx + dz * dz + dy * dy * 0.35);
   }

   private class_238 playerBox(CriticalHitEffectModule.CritGhost ghost, class_243 pos, float scale) {
      double half = 0.3 * (double)scale;
      return new class_238(
         pos.field_1352 - half,
         pos.field_1351,
         pos.field_1350 - half,
         pos.field_1352 + half,
         pos.field_1351 + (double)(ghost.height * scale),
         pos.field_1350 + half
      );
   }

   private void drawBillboard(class_287 consumer, Matrix4f matrix, class_243 cameraPos, class_243 pos, float size, int color) {
      float half = size * 0.5F;
      float x = (float)(pos.field_1352 - cameraPos.field_1352);
      float y = (float)(pos.field_1351 - cameraPos.field_1351);
      float z = (float)(pos.field_1350 - cameraPos.field_1350);
      this.vertex(consumer, matrix, x - half, y - half, z, color);
      this.vertex(consumer, matrix, x + half, y - half, z, color);
      this.vertex(consumer, matrix, x + half, y + half, z, color);
      this.vertex(consumer, matrix, x - half, y + half, z, color);
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

   private void vertex(class_287 consumer, Matrix4f matrix, float x, float y, float z, int color) {
      int alpha = color >>> 24 & 0xFF;
      if (alpha == 0) {
         alpha = 255;
      }

      consumer.method_22918(matrix, x, y, z).method_1336(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, alpha);
   }

   private float easeOutCubic(float value) {
      float clamped = class_3532.method_15363(value, 0.0F, 1.0F);
      float inverse = 1.0F - clamped;
      return 1.0F - inverse * inverse * inverse;
   }

   private void trimGhosts() {
      int overflow = this.ghosts.size() - 24;
      if (overflow > 0) {
         this.ghosts.subList(0, overflow).clear();
      }
   }

   private void clear() {
      this.ghosts.clear();
      this.sparks.clear();
      this.lastSparkTime = Long.MIN_VALUE;
   }

   private long lifetimeMs() {
      return Math.max(1L, this.lifeTime.getLongValue());
   }

   static final class BlockSample {
      final class_2338 pos;
      final List<class_238> boxes;
      final double distance;
      boolean sparked;

      BlockSample(class_2338 pos, List<class_238> boxes, double distance) {
         this.pos = pos;
         this.boxes = boxes;
         this.distance = distance;
      }

      double centerX() {
         return (double)this.pos.method_10263() + 0.5;
      }

      double centerY() {
         return (double)this.pos.method_10264() + 0.5;
      }

      double centerZ() {
         return (double)this.pos.method_10260() + 0.5;
      }
   }

   static final class CritGhost {
      final class_243 origin;
      final float height;
      final List<CriticalHitEffectModule.BlockSample> blocks;
      final long spawnTime;

      CritGhost(class_1657 player, class_243 origin, List<CriticalHitEffectModule.BlockSample> blocks, long spawnTime) {
         this.origin = origin;
         this.height = player.method_17682();
         this.blocks = blocks;
         this.spawnTime = spawnTime;
      }

      boolean isExpired(long now, long lifetime) {
         return now - this.spawnTime >= lifetime;
      }

      float progress(long now, long lifetime) {
         return class_3532.method_15363((float)(now - this.spawnTime) / (float)lifetime, 0.0F, 1.0F);
      }
   }

   static final class Spark {
      class_243 pos;
      class_243 velocity;
      final int color;
      float size;
      float life;
      final float fadeRate;

      Spark(class_243 pos, class_243 velocity, int color, float size, float life, float fadeRate) {
         this.pos = pos;
         this.velocity = velocity;
         this.color = color;
         this.size = size;
         this.life = life;
         this.fadeRate = fadeRate;
      }

      void tick(double delta) {
         this.pos = this.pos.method_1019(this.velocity.method_1021(delta * 20.0));
         this.velocity = this.velocity.method_1021(0.92).method_1031(0.0, -0.008 * delta * 20.0, 0.0);
         this.life -= this.fadeRate * (float)delta * 4.0F;
         this.size *= 0.97F;
      }

      boolean isDead() {
         return this.life <= 0.02F || this.size <= 0.004F;
      }
   }
}
