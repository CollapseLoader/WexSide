package ru.wexside.module.render;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.misc.SpriteAtlasRegion;
import ru.wexside.misc.WexsideHitParticles;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.render.ParticleBillboardRenderer;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class WorldParticlesModule extends Module implements ConfigSerializable {
   private static final double DEFAULT_DELTA = 0.016666666666666666;
   private final BooleanSetting enabledSetting;
   private final ColorSetting color;
   private final NumberSetting spawnCount;
   private final NumberSetting maxCount;
   private final NumberSetting range;
   private final NumberSetting size;
   private final NumberSetting strength;
   private final NumberSetting duration;
   private final BooleanSetting glowing;
   private final BooleanSetting onlyMove;
   private final ModeSetting direction;
   private final ModeSetting particleType;
   private final List<WorldParticlesModule.Particle> particles = new ArrayList<>();
   private long lastNanoTime;
   private double spawnAccumulator;

   public WorldParticlesModule(EventBus eventBus) {
      super(eventBus, "world_particles", "World Particles", "Окружающие игрока парящие частицы", ModuleCategory.valueOf("RENDER"));
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Включить окружающие частицы")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      ColorSetting colorSetting = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(0)
            .name("Color")
            .id("color")
            .description("Цвет частиц")
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
      this.spawnCount = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 20.0)
            .defaultValue(5.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Spawn count")
            .id("spawn_count")
            .description("Сколько частиц спавнить за раз"))
         .build();
      this.registerSetting(this.spawnCount);
      this.maxCount = ((NumberSettingBuilder)NumberSetting.builder()
            .range(50.0, 300.0)
            .defaultValue(150.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .snapTo(25.0)
            .name("Max count")
            .id("max_count")
            .description("Максимальное число живых частиц одновременно"))
         .build();
      this.registerSetting(this.maxCount);
      this.range = ((NumberSettingBuilder)NumberSetting.builder()
            .range(2.0, 24.0)
            .defaultValue(8.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .showMarkers()
            .name("Distance")
            .id("range")
            .description("Радиус появления частиц вокруг игрока"))
         .build();
      this.registerSetting(this.range);
      this.size = ((NumberSettingBuilder)NumberSetting.builder()
            .range(10.0, 100.0)
            .defaultValue(50.0)
            .multiplier(0.005)
            .precision(0)
            .animationSpeed(20.0F)
            .markers(10.0)
            .snapTo(10.0)
            .name("Size")
            .id("size")
            .description("Размер частицы")
            .aliases("size", "размер"))
         .build();
      this.registerSetting(this.size);
      this.strength = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 16.0)
            .defaultValue(4.0)
            .multiplier(0.05)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Move strength")
            .id("strength")
            .description("Сила движения частиц"))
         .build();
      this.registerSetting(this.strength);
      this.duration = ((NumberSettingBuilder)NumberSetting.builder()
            .range(10.0, 50.0)
            .defaultValue(30.0)
            .multiplier(100.0)
            .precision(0)
            .animationSpeed(20.0F)
            .snapTo(10.0)
            .name("Life time")
            .id("duration")
            .description("Время жизни частицы"))
         .build();
      this.registerSetting(this.duration);
      this.glowing = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Glowing")
            .id("glowing")
            .description("Свечение текстуры частицы"))
         .build();
      this.registerSetting(this.glowing);
      this.onlyMove = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Only move")
            .id("only_move")
            .description("Спавнить частицы только при движении игрока"))
         .build();
      this.registerSetting(this.onlyMove);
      this.direction = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Up", "Down", "Random")
            .defaultOption("Up")
            .name("Direction")
            .id("direction")
            .description("Направление движения частиц")
            .aliases("direction", "направление"))
         .build();
      this.registerSetting(this.direction);
      this.particleType = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Cross", "Dollar", "Star", "Bloom", "Snowflake", "Line", "Light")
            .defaultOption("Cross")
            .name("Particle type")
            .id("particle_type")
            .description("Текстура частицы"))
         .build();
      this.registerSetting(this.particleType);
   }

   @Override
   protected void initialize() {
      this.listen(WorldRenderEvent.class, this::onWorldRender);
   }

   private void onWorldRender(WorldRenderEvent event) {
      if (!this.enabledSetting.isEnabled()) {
         this.clear();
      } else {
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         if (client.field_1687 != null && player != null) {
            double delta = this.frameDelta();
            class_243 pos = this.lerpedPos(player, event.getFloatType());
            boolean moving = player.method_18798().method_37268() > 1.0E-4;
            this.tickParticles(delta);
            this.spawnAround(pos, delta, moving);
            this.trimOverflow();
            this.renderParticles();
         } else {
            this.resetClock();
         }
      }
   }

   private void spawnAround(class_243 origin, double delta, boolean moving) {
      if (!this.onlyMove.isEnabled() || moving) {
         int cap = this.maxCount.getIntValue();
         if (this.particles.size() < cap) {
            this.spawnAccumulator += delta * 20.0;
            int ticks = (int)this.spawnAccumulator;
            if (ticks > 0) {
               this.spawnAccumulator -= (double)ticks;
               int spawn = Math.min(cap - this.particles.size(), ticks * this.spawnCount.getIntValue());

               for(int i = 0; i < spawn; ++i) {
                  this.particles.add(this.createParticle(origin));
               }
            }
         }
      }
   }

   private WorldParticlesModule.Particle createParticle(class_243 origin) {
      ThreadLocalRandom random = ThreadLocalRandom.current();
      double radius = this.range.getValue();
      double strength = this.strength.getValue();
      float size = 0.05F + this.size.getFloatValue() * 0.35F;
      double offsetX = random.nextDouble(-radius, radius);
      double offsetZ = random.nextDouble(-radius, radius);
      String dir = this.direction.getSelectedOption();
      double vz;
      double vy;
      double vx;
      double z;
      double y;
      double x;
      if ("Up".equals(dir)) {
         x = origin.field_1352 + offsetX;
         y = origin.field_1351 + random.nextDouble(0.0, 0.35) + (double)size;
         z = origin.field_1350 + offsetZ;
         vx = random.nextDouble(-0.02, 0.02) * 0.01;
         vy = random.nextDouble(0.02, strength) * 0.01;
         vz = random.nextDouble(-0.02, 0.02) * 0.01;
      } else if ("Down".equals(dir)) {
         x = origin.field_1352 + offsetX;
         y = origin.field_1351 + random.nextDouble(1.0, radius) + (double)size;
         z = origin.field_1350 + offsetZ;
         vx = random.nextDouble(-0.02, 0.02) * 0.01;
         vy = -random.nextDouble(0.02, strength) * 0.01;
         vz = random.nextDouble(-0.02, 0.02) * 0.01;
      } else {
         x = origin.field_1352 + offsetX;
         y = origin.field_1351 + random.nextDouble(-radius * 0.5, radius * 0.5) + (double)size;
         z = origin.field_1350 + offsetZ;
         vx = random.nextDouble(-strength, strength) * 0.01;
         vy = random.nextDouble(-strength, strength) * 0.01;
         vz = random.nextDouble(-strength, strength) * 0.01;
      }

      float rotation = (float)((double)Math.round(random.nextDouble(0.0, 360.0) / 15.0) * 15.0);
      return new WorldParticlesModule.Particle(x, y, z, vx, vy, vz, this.sampleColor(random.nextFloat()), size, rotation);
   }

   private void tickParticles(double delta) {
      long lifetime = this.duration.getLongValue();
      double step = Math.max(delta * 60.0, 1.0);

      for(int i = this.particles.size() - 1; i >= 0; --i) {
         WorldParticlesModule.Particle particle = this.particles.get(i);
         if (particle.isDead(lifetime)) {
            this.particles.remove(i);
         } else {
            particle.tick(step);
         }
      }
   }

   private void renderParticles() {
      long lifetime = this.duration.getLongValue();
      boolean glow = this.glowing.isEnabled();
      WexsideHitParticles texture = WexsideHitParticles.process2(this.particleType.getSelectedOption());
      SpriteAtlasRegion sprite = texture.process3(glow);
      class_2960 atlas = WexsideHitParticles.getParticleTexture();

      for(WorldParticlesModule.Particle particle : this.particles) {
         float life = particle.life(lifetime);
         if (!(life <= 0.02F)) {
            int tint = withAlpha(particle.color, class_3532.method_15340((int)(life * 255.0F), 0, 255));
            ParticleBillboardRenderer.draw(particle.x, particle.y, particle.z, particle.size, particle.size, tint, atlas, false, particle.rotation, sprite.minU(), sprite.minV(), sprite.maxU(), sprite.maxV());
         }
      }
   }

   private class_243 lerpedPos(class_746 player, float tickDelta) {
      return new class_243(
         class_3532.method_16436((double)tickDelta, player.field_6038, player.method_23317()),
         class_3532.method_16436((double)tickDelta, player.field_5971, player.method_23318()),
         class_3532.method_16436((double)tickDelta, player.field_5989, player.method_23321())
      );
   }

   private int sampleColor(float t) {
      if (this.color.isAstolfoMode()) {
         return this.color.getEditingColor(t);
      } else {
         return this.color.isDoubleColorMode() ? lerpColor(this.color.getPrimaryColor(), this.color.getSecondaryColor(), t) : this.color.getPrimaryColor();
      }
   }

   private static int lerpColor(int from, int to, float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      int a = (int)((float)(from >>> 24 & 0xFF) + (float)((to >>> 24 & 0xFF) - (from >>> 24 & 0xFF)) * t);
      int r = (int)((float)(from >> 16 & 0xFF) + (float)((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * t);
      int g = (int)((float)(from >> 8 & 0xFF) + (float)((to >> 8 & 0xFF) - (from >> 8 & 0xFF)) * t);
      int b = (int)((float)(from & 0xFF) + (float)((to & 0xFF) - (from & 0xFF)) * t);
      return a << 24 | r << 16 | g << 8 | b;
   }

   private static int withAlpha(int color, int alpha) {
      return color & 16777215 | alpha << 24;
   }

   private void trimOverflow() {
      int overflow = this.particles.size() - this.maxCount.getIntValue();
      if (overflow > 0) {
         this.particles.subList(0, overflow).clear();
      }
   }

   private void clear() {
      this.particles.clear();
      this.resetClock();
   }

   private void resetClock() {
      this.spawnAccumulator = 0.0;
      this.lastNanoTime = 0L;
   }

   private double frameDelta() {
      long now = System.nanoTime();
      long previous = this.lastNanoTime;
      this.lastNanoTime = now;
      if (previous == 0L) {
         return 0.016666666666666666;
      } else {
         double delta = (double)(now - previous) / 1.0E9;
         return Double.isFinite(delta) && !(delta < 0.0) ? Math.min(delta, 0.05) : 0.016666666666666666;
      }
   }

   static final class Particle {
      double x;
      double y;
      double z;
      double vx;
      double vy;
      double vz;
      final int color;
      float size;
      float rotation;
      long age;

      Particle(double x, double y, double z, double vx, double vy, double vz, int color, float size, float rotation) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.vx = vx;
         this.vy = vy;
         this.vz = vz;
         this.color = color;
         this.size = size;
         this.rotation = rotation;
      }

      boolean isDead(long lifetime) {
         return this.age >= lifetime;
      }

      float life(long lifetime) {
         return lifetime <= 0L ? 0.0F : class_3532.method_15363(1.0F - (float)this.age / (float)lifetime, 0.0F, 1.0F);
      }

      void tick(double step) {
         this.x += this.vx * step;
         this.y += this.vy * step;
         this.z += this.vz * step;
         ++this.age;
         ++this.rotation;
      }
   }
}
