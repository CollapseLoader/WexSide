package ru.wexside.module.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_12249;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL46;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.event.WorldSessionEvent;
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
import ru.wexside.util.ColorUtils;

public final class TrailsModule extends Module implements ConfigSerializable {
   private static final int MAX_LINEAR_POINTS = 400;
   private static final int MAX_PARTICLES = 2000;
   private static final int MAX_WALK_ITERATIONS = 4000;
   private static final double MIN_SEGMENT = 4.0E-4;
   private static final double MAX_SEGMENT = 1024.0;
   private static final double PARTICLE_STEP = 0.1;
   private final BooleanSetting enabledSetting;
   private final ColorSetting color;
   private final BooleanSetting hideFirstPerson;
   private final ModeSetting renderType;
   private final ModeSetting particleType;
   private final BooleanSetting glow;
   private final NumberSetting lifeTime;
   private final NumberSetting count;
   private final NumberSetting size;
   private final NumberSetting spawnScale;
   private final List<TrailsModule.LinearTrailPoint> linearTrail = new ArrayList<>();
   private final List<TrailsModule.TrailParticle> particles = new ArrayList<>();
   private class_243 lastLinearPos;
   private double particleX;
   private double particleY;
   private double particleZ;
   private boolean particleStarted;
   private long lastNanoTime;

   public TrailsModule(EventBus eventBus) {
      super(eventBus, "trails", "Trails", "След за игроком", ModuleCategory.valueOf("RENDER"));
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Включить след за игроком")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      ColorSetting colorSetting = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(0)
            .name("Color")
            .id("color")
            .description("Цвет следа")
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
      this.hideFirstPerson = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Hide in first person")
            .id("hide_first_person")
            .description("Не рисовать след от первого лица"))
         .build();
      this.registerSetting(this.hideFirstPerson);
      this.renderType = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Linear", "Particles")
            .defaultOption("Particles")
            .name("Render type")
            .id("render_type")
            .description("Тип отрисовки следа"))
         .build();
      this.registerSetting(this.renderType);
      this.particleType = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Cross", "Dollar", "Star", "Bloom", "Snowflake", "Line", "Light")
            .defaultOption("Cross")
            .name("Particle type")
            .id("particle_type")
            .description("Текстура частицы")
            .visibleWhen(this::particleMode))
         .build();
      this.registerSetting(this.particleType);
      this.glow = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Glowing")
            .id("glow")
            .description("Свечение текстуры частицы")
            .visibleWhen(this::particleMode))
         .build();
      this.registerSetting(this.glow);
      this.lifeTime = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 5.0)
            .defaultValue(1.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Life time")
            .id("life_time")
            .description("Время жизни частицы")
            .visibleWhen(this::particleMode))
         .build();
      this.registerSetting(this.lifeTime);
      this.count = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 5.0)
            .defaultValue(5.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Count")
            .id("count")
            .description("Количество частиц за шаг")
            .aliases("count", "количество")
            .visibleWhen(this::particleMode))
         .build();
      this.registerSetting(this.count);
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
            .aliases("size", "размер")
            .visibleWhen(this::particleMode))
         .build();
      this.registerSetting(this.size);
      this.spawnScale = ((NumberSettingBuilder)NumberSetting.builder()
            .range(10.0, 100.0)
            .defaultValue(50.0)
            .multiplier(0.005)
            .precision(0)
            .animationSpeed(20.0F)
            .markers(10.0)
            .snapTo(10.0)
            .name("Spawn scale")
            .id("spawn_scale")
            .description("Начальный масштаб частицы")
            .visibleWhen(this::particleMode))
         .build();
      this.registerSetting(this.spawnScale);
   }

   @Override
   protected void initialize() {
      this.listen(WorldRenderEvent.class, this::onWorldRender);
      this.listen(WorldSessionEvent.class, event -> this.clear());
   }

   private void onWorldRender(WorldRenderEvent event) {
      if (!this.enabledSetting.isEnabled()) {
         this.clear();
      } else {
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         if (client.field_1687 != null && player != null) {
            class_243 pos = this.lerpedPos(player, event.getFloatType());
            if (this.particleMode()) {
               this.updateParticles(pos, player.method_17682() * 0.45F);
            } else {
               this.updateLinearTrail(pos);
            }
         } else {
            this.clear();
         }
      }
   }

   private void updateLinearTrail(class_243 pos) {
      if (this.lastLinearPos == null) {
         this.lastLinearPos = pos;
      }

      this.ageLinear(this.frameDelta());
      double distance = pos.method_1025(this.lastLinearPos);
      if (distance >= 1024.0) {
         this.lastLinearPos = pos;
      } else if (distance > 4.0E-4) {
         this.linearTrail.add(new TrailsModule.LinearTrailPoint(this.lastLinearPos, pos));
         this.trimLinear();
         this.lastLinearPos = pos;
      }

      this.renderLinear();
   }

   private void updateParticles(class_243 pos, float height) {
      this.lastLinearPos = null;
      if (!this.particleStarted) {
         this.particleX = pos.field_1352;
         this.particleY = pos.field_1351;
         this.particleZ = pos.field_1350;
         this.particleStarted = true;
      }

      this.expireParticles();
      this.walkParticles(pos, height);
      this.renderParticles();
   }

   private void walkParticles(class_243 target, float height) {
      double dx = target.field_1352 - this.particleX;
      double dy = target.field_1351 - this.particleY;
      double dz = target.field_1350 - this.particleZ;
      double distanceSq = dx * dx + dy * dy + dz * dz;
      if (distanceSq >= 1024.0) {
         this.particleX = target.field_1352;
         this.particleY = target.field_1351;
         this.particleZ = target.field_1350;
      } else {
         int iterations = 0;
         while(distanceSq >= 1.0E-4 && iterations < 4000) {
            ++iterations;
            double distance = Math.sqrt(distanceSq);
            double moveFraction = Math.min(PARTICLE_STEP / distance, 1.0);
            this.particleX += dx * moveFraction;
            this.particleY += dy * moveFraction;
            this.particleZ += dz * moveFraction;
            this.spawnParticles(this.particleX, this.particleY, this.particleZ, height);
            dx = target.field_1352 - this.particleX;
            dy = target.field_1351 - this.particleY;
            dz = target.field_1350 - this.particleZ;
            distanceSq = dx * dx + dy * dy + dz * dz;
         }
         if (iterations >= 4000) {
            this.particleX = target.field_1352;
            this.particleY = target.field_1351;
            this.particleZ = target.field_1350;
         }
      }
   }

   private void spawnParticles(double x, double y, double z, float height) {
      ThreadLocalRandom random = ThreadLocalRandom.current();
      float size = height * (float)this.size.getValue();
      float scale = (float)this.spawnScale.getValue();

      if (this.particles.size() >= 2000) {
         return;
      }
      int toSpawn = Math.min(this.count.getIntValue(), 2000 - this.particles.size());
      for(int i = 0; i < toSpawn; ++i) {
         this.particles.add(new TrailsModule.TrailParticle(x, y + 0.4, z, size, scale, 0.1, random));
      }
   }

   private void expireParticles() {
      long now = System.currentTimeMillis();
      long lifetime = (long)(this.lifeTime.getValue() * 1000.0);
      boolean paused = class_310.method_1551().method_1493();
      this.particles.removeIf(particle -> {
         if (particle.isExpired(now, lifetime)) {
            return true;
         } else {
            particle.tick(paused);
            return false;
         }
      });
   }

   private void renderParticles() {
      if (!this.particles.isEmpty() && !this.hideInFirstPerson()) {
         long now = System.currentTimeMillis();
         long lifetime = (long)(this.lifeTime.getValue() * 1000.0);
         boolean glow = this.glow.isEnabled();
         WexsideHitParticles texture = WexsideHitParticles.process2(this.particleType.getSelectedOption());
         SpriteAtlasRegion sprite = texture.process3(glow);
         class_2960 atlas = WexsideHitParticles.getParticleTexture();
         float invLifetime = 1.0F / (float)lifetime;
         float count = Math.max(1.0F, (float)this.particles.size() - 1.0F);

         for(int i = 0; i < this.particles.size(); ++i) {
            TrailsModule.TrailParticle particle = this.particles.get(i);
            long age = now - particle.spawnTime;
            if (age <= lifetime) {
               float progress = (float)age * invLifetime;
               float fadeIn = clamp01(progress / 0.18F);
               float scale = this.overshoot(fadeIn, 1.28F);
               float rise = this.easeOutCubic(progress);
               float tail = progress <= 0.56F ? 1.0F : 1.0F - this.cubic(clamp01((progress - 0.56F) / 0.44F));
               float alpha = this.amount11(0.8F, 1.0F, scale * 0.22F) * tail;
               float pulse = 0.9F + 0.1F * class_3532.method_15374((double)(particle.phase + (float)age * 0.008333334F * 1.6F));
               float size = particle.baseSize * this.amount11(particle.spawnScale, 1.0F, scale) * (1.0F - progress * 0.12F) * pulse * particle.speed;
               double x = particle.x
                  + particle.driftX * (double)rise * 0.09
                  + particle.liftX * (double)this.easeOutCubic(clamp01((progress - 0.68F) / 0.32F)) * 0.18;
               double y = particle.y + 0.82 + particle.liftY * (double)rise + particle.driftY * (double)rise * 0.09 * 0.45;
               double z = particle.z
                  + particle.driftZ * (double)rise * 0.09
                  + particle.liftZ * (double)this.easeOutCubic(clamp01((progress - 0.68F) / 0.32F)) * 0.05;
               float rotation = particle.rotation * (1.0F - progress * 0.78F) + class_3532.method_15374((double)(particle.phase + progress * 6.0F)) * 5.5F;
               int tint = ColorUtils.multiplyAlpha(this.sampleColor((float)i / count), alpha);
               float particleSize = size * 0.6666667F;
               ParticleBillboardRenderer.draw(x, y, z, particleSize, particleSize, tint, atlas, false, rotation, sprite.minU(), sprite.minV(), sprite.maxU(), sprite.maxV());
            }
         }
      }
   }

   private void renderLinear() {
      if (!this.linearTrail.isEmpty() && !this.hideInFirstPerson()) {
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         if (player != null) {
            float height = player.method_17682() - 0.05F - (player.method_6115() ? 0.1F : 0.0F);
            float step = 1.0F / Math.max((float)this.linearTrail.size() * 2.0F, 1.0F);
            boolean cull = GL46.glIsEnabled(2884);
            boolean blend = GL46.glIsEnabled(3042);
            boolean depthTest = GL46.glIsEnabled(2929);
            boolean depthMask = GL11.glGetBoolean(2930);
            GL46.glDisable(2884);
            GL46.glEnable(3042);
            GL46.glDepthMask(false);

            try {
               class_4184 camera = client.field_1773.method_19418();
               class_243 cameraPos = camera.method_71156();
               Matrix4f viewMatrix = new Matrix4f().rotation(camera.method_23767());
               class_287 fill = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1576);
               class_287 lines = class_289.method_1348().method_60827(class_5596.field_29344, class_290.field_1576);

               for(int i = 0; i < this.linearTrail.size(); ++i) {
                  this.drawLinearSegment(this.linearTrail.get(i), (float)i * step, height, cameraPos, viewMatrix, fill, lines);
               }

               class_12249.method_76023().method_60895(fill.method_60800());
               class_12249.method_76015().method_60895(lines.method_60800());
            } finally {
               setCap(2884, cull);
               setCap(3042, blend);
               setCap(2929, depthTest);
               GlStateManager._depthMask(depthMask);
            }
         }
      }
   }

   private void drawLinearSegment(
      TrailsModule.LinearTrailPoint point, float gradient, float height, class_243 cameraPos, Matrix4f viewMatrix, class_287 fill, class_287 lines
   ) {
      float width = clamp01((float)(point.age * 0.02));
      float strength = clamp01((1.0F - width) * 1.8F);
      if (!(strength <= 0.01F)) {
         int color = ColorUtils.multiplyAlpha(this.sampleColor(gradient), strength);
         int nextColor = ColorUtils.multiplyAlpha(this.sampleColor(Math.min(1.0F, gradient + 0.08F)), strength);
         class_243 from = point.from.method_1031(0.0, 0.15, 0.0);
         class_243 to = point.to.method_1031(0.0, 0.15, 0.0);
         class_243 topTo = point.to.method_1031(0.0, (double)height, 0.0);
         class_243 topFrom = point.from.method_1031(0.0, (double)height, 0.0);
         this.quad(fill, viewMatrix, cameraPos, from, to, topTo, topFrom, color, nextColor);
         this.quad(fill, viewMatrix, cameraPos, topFrom, topTo, to, from, color, nextColor);
         this.line(lines, viewMatrix, cameraPos, from, to, color, nextColor);
      }
   }

   private void ageLinear(double delta) {
      Iterator<TrailsModule.LinearTrailPoint> iterator = this.linearTrail.iterator();

      while(iterator.hasNext()) {
         TrailsModule.LinearTrailPoint point = iterator.next();
         point.age += delta * 20.0;
         if (point.age >= 50.0) {
            iterator.remove();
         }
      }
   }

   private void trimLinear() {
      int overflow = this.linearTrail.size() - 400;
      if (overflow > 0) {
         this.linearTrail.subList(0, overflow).clear();
      }
   }

   private void quad(class_287 consumer, Matrix4f viewMatrix, class_243 cameraPos, class_243 a, class_243 b, class_243 c, class_243 d, int colorA, int colorB) {
      this.vertex(consumer, viewMatrix, cameraPos, a, colorA);
      this.vertex(consumer, viewMatrix, cameraPos, b, colorB);
      this.vertex(consumer, viewMatrix, cameraPos, c, colorB);
      this.vertex(consumer, viewMatrix, cameraPos, d, colorA);
   }

   private void line(class_287 consumer, Matrix4f viewMatrix, class_243 cameraPos, class_243 a, class_243 b, int colorA, int colorB) {
      this.vertex(consumer, viewMatrix, cameraPos, a, colorA);
      this.vertex(consumer, viewMatrix, cameraPos, b, colorB);
   }

   private void vertex(class_287 consumer, Matrix4f viewMatrix, class_243 cameraPos, class_243 pos, int color) {
      float px = (float)(pos.field_1352 - cameraPos.field_1352);
      float py = (float)(pos.field_1351 - cameraPos.field_1351);
      float pz = (float)(pos.field_1350 - cameraPos.field_1350);
      consumer.method_22918(viewMatrix, px, py, pz)
         .method_1336(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24 & 0xFF);
   }

   private void vertex(class_287 consumer, Matrix4f matrix, float x, float y, float z, int color) {
      consumer.method_22918(matrix, x, y, z).method_1336(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24 & 0xFF);
   }

   private int sampleColor(float t) {
      t = clamp01(t);
      if (this.color.isAstolfoMode()) {
         return this.color.getEditingColor(t);
      } else {
         int from = this.color.getPrimaryColor();
         int to = this.color.isDoubleColorMode() ? this.color.getSecondaryColor() : from;
         return ColorUtils.lerp(from, to, (double)t);
      }
   }

   private class_243 lerpedPos(class_746 player, float tickDelta) {
      return new class_243(
         class_3532.method_16436((double)tickDelta, player.field_6038, player.method_23317()),
         class_3532.method_16436((double)tickDelta, player.field_5971, player.method_23318()),
         class_3532.method_16436((double)tickDelta, player.field_5989, player.method_23321())
      );
   }

   private boolean hideInFirstPerson() {
      class_310 client = class_310.method_1551();
      return this.hideFirstPerson.isEnabled() && client.field_1690 != null && client.field_1690.method_31044().method_31034();
   }

   private boolean particleMode() {
      return "Particles".equals(this.renderType.getSelectedOption());
   }

   private void clear() {
      this.linearTrail.clear();
      this.particles.clear();
      this.lastLinearPos = null;
      this.particleStarted = false;
      this.lastNanoTime = 0L;
   }

   private double frameDelta() {
      long now = System.nanoTime();
      long previous = this.lastNanoTime;
      this.lastNanoTime = now;
      if (previous == 0L) {
         return 0.016666666666666666;
      } else {
         double delta = (double)(now - previous) * 1.0E-9;
         return Double.isFinite(delta) && !(delta < 0.0) ? Math.min(delta, 0.05) : 0.016666666666666666;
      }
   }

   private static float clamp01(float value) {
      return class_3532.method_15363(value, 0.0F, 1.0F);
   }

   private static void setCap(int cap, boolean enabled) {
      if (enabled) {
         GL11.glEnable(cap);
      } else {
         GL11.glDisable(cap);
      }
   }

   private float easeOutCubic(float value) {
      float inverse = 1.0F - clamp01(value);
      return 1.0F - inverse * inverse * inverse;
   }

   private float overshoot(float value, float amount) {
      float t = clamp01(value) - 1.0F;
      float s = amount + 0.8F;
      return 1.0F + t * t * ((s + 1.0F) * t + s);
   }

   private float cubic(float value) {
      float t = clamp01(value);
      return t * t * t;
   }

   private float amount11(float from, float to, float t) {
      return from + (to - from) * t;
   }

   static final class LinearTrailPoint {
      final class_243 from;
      final class_243 to;
      double age;

      LinearTrailPoint(class_243 from, class_243 to) {
         this.from = from;
         this.to = to;
      }
   }

   static final class TrailParticle {
      final double x;
      final double y;
      final double z;
      final float baseSize;
      final float spawnScale;
      final float speed;
      final float phase;
      float rotation;
      final double driftX;
      final double driftY;
      final double driftZ;
      final double liftX;
      final double liftY;
      final double liftZ;
      final long spawnTime;

      TrailParticle(double x, double y, double z, float baseSize, float spawnScale, double speed, ThreadLocalRandom random) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.baseSize = baseSize;
         this.spawnScale = spawnScale;
         this.speed = (float)speed;
         this.phase = random.nextFloat() * ((float) (Math.PI * 2));
         this.rotation = random.nextFloat() * 360.0F;
         this.driftX = random.nextDouble(-0.02, 0.02);
         this.driftY = random.nextDouble(0.0, 0.02);
         this.driftZ = random.nextDouble(-0.02, 0.02);
         this.liftX = random.nextDouble(-0.01, 0.01);
         this.liftY = random.nextDouble(0.0, 0.04);
         this.liftZ = random.nextDouble(-0.01, 0.01);
         this.spawnTime = System.currentTimeMillis();
      }

      boolean isExpired(long now, long lifetime) {
         return now - this.spawnTime > lifetime;
      }

      void tick(boolean paused) {
         if (!paused) {
            this.rotation += 0.5F;
         }
      }
   }
}
