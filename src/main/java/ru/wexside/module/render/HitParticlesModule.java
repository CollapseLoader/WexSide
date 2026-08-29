package ru.wexside.module.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import org.lwjgl.opengl.GL11;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EntityAttackEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.misc.SpriteAtlasRegion;
import ru.wexside.misc.WexsideHitParticles;
import ru.wexside.render.ParticleBillboardRenderer;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class HitParticlesModule extends Module implements ConfigSerializable {
   private static final double GRAVITY = 0.015;
   private static final double DRAG = 0.96;
   private static volatile HitParticlesModule instance;
   private final BooleanSetting enabledSetting;
   private final BooleanSetting removeDefault;
   private final ColorSetting color;
   private final NumberSetting count;
   private final NumberSetting maxCount;
   private final NumberSetting strength;
   private final NumberSetting size;
   private final NumberSetting duration;
   private final BooleanSetting glowing;
   private final ModeSetting particleTexture;
   private final ModeSetting animationType;
   private final List<HitParticlesModule.Particle> particles = new ArrayList<>();
   private long lastNanoTime;
   private double tickAccumulator;

   public HitParticlesModule(EventBus eventBus) {
      super(eventBus, "hit_particles", "Hit Particles", "Эффект частиц при ударе по сущности", ModuleCategory.valueOf("RENDER"));
      instance = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Включить эффект частиц при ударе по сущности")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      this.removeDefault = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Remove Default")
            .id("remove_default")
            .description("Убирает ванильные частицы удара"))
         .build();
      this.registerSetting(this.removeDefault);
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
      this.count = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 10.0)
            .defaultValue(5.0)
            .multiplier(2.5)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Spawn count")
            .id("count")
            .description("Количество частиц, появляющихся при ударе"))
         .build();
      this.registerSetting(this.count);
      this.maxCount = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 16.0)
            .defaultValue(4.0)
            .multiplier(32.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Max count")
            .id("max_count")
            .description("Максимальное число живых частиц одновременно"))
         .build();
      this.registerSetting(this.maxCount);
      this.strength = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 16.0)
            .defaultValue(4.0)
            .multiplier(0.05)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Move strength")
            .id("strength")
            .description("Сила начального разлёта частиц"))
         .build();
      this.registerSetting(this.strength);
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
      this.duration = ((NumberSettingBuilder)NumberSetting.builder()
            .range(10.0, 50.0)
            .defaultValue(30.0)
            .multiplier(1.0)
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
      this.particleTexture = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Cross", "Dollar", "Star", "Bloom", "Snowflake", "Line", "Light")
            .defaultOption("Cross")
            .name("Texture")
            .id("particle_texture")
            .description("Текстура частицы"))
         .build();
      this.registerSetting(this.particleTexture);
      this.animationType = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Default", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
            .defaultOption("Default")
            .name("Type animation")
            .id("animation_type")
            .description("Анимация"))
         .build();
      this.registerSetting(this.animationType);
   }

   @Override
   protected void initialize() {
      this.listen(EntityAttackEvent.class, this::onEntityAttack);
      this.listen(WorldRenderEvent.class, this::onWorldRender);
   }

   public static boolean isEnabled() {
      HitParticlesModule module = instance;
      return module != null && module.enabledSetting.isEnabled() && module.removeDefault.isEnabled();
   }

   private void onEntityAttack(EntityAttackEvent event) {
      if (instance != this) {
         return;
      }

      if (this.enabledSetting.isEnabled()) {
         class_310 client = class_310.method_1551();
         if (client.field_1687 != null && client.field_1724 != null) {
            class_1297 target = event.getEntity();
            if (target instanceof class_1309) {
               class_1309 living = (class_1309)target;
               this.spawnParticles(new class_243(living.method_23317(), living.method_23318() + (double)living.method_17682() * 0.5, living.method_23321()));
            }
         }
      }
   }

   private void onWorldRender(WorldRenderEvent event) {
      if (instance != this) {
         return;
      }

      if (!this.enabledSetting.isEnabled()) {
         this.clearParticles();
      } else {
         class_310 client = class_310.method_1551();
         if (client.field_1687 != null && client.field_1724 != null) {
            this.stepPhysics();
            this.trimOverflow();
            this.renderParticles((float)this.tickAccumulator);
         } else {
            this.resetClock();
         }
      }
   }

   private void spawnParticles(class_243 origin) {
      ThreadLocalRandom random = ThreadLocalRandom.current();
      int room = Math.max(0, this.maxCount.getIntValue() - this.particles.size());
      int spawn = Math.min(room, this.count.getIntValue() + random.nextInt(4));

      for(int i = 0; i < spawn; ++i) {
         float particleSize = random.nextFloat() * 0.15F + this.size.getFloatValue() * 0.7F;
         double spread = this.strength.getValue();
         class_243 velocity = new class_243(random.nextDouble(-spread, spread), random.nextDouble(-spread, spread + 0.1), random.nextDouble(-spread, spread));
         float rotation = (float)((double)Math.round(random.nextDouble(0.0, 360.0) / 15.0) * 15.0);
         this.particles
            .add(new HitParticlesModule.Particle(origin, velocity, this.sampleColor(random.nextFloat()), particleSize, rotation, this.animationIndex()));
      }
   }

   private void stepPhysics() {
      double delta = this.frameDelta();
      this.tickAccumulator += delta * 20.0;
      int steps = (int)this.tickAccumulator;
      if (steps > 0) {
         this.tickAccumulator -= (double)steps;
         if (steps > 10) {
            steps = 10;
            this.tickAccumulator = 0.0;
         }

         int lifetime = this.duration.getIntValue();

         for(int i = 0; i < steps; ++i) {
            for(int p = this.particles.size() - 1; p >= 0; --p) {
               HitParticlesModule.Particle particle = this.particles.get(p);
               if (particle.isDead(lifetime)) {
                  this.particles.remove(p);
               } else {
                  particle.tick(1.0);
               }
            }
         }
      }
   }

   private void renderParticles(float tickDelta) {
      boolean glow = this.glowing.isEnabled();
      WexsideHitParticles texture = WexsideHitParticles.process2(this.particleTexture.getSelectedOption());
      SpriteAtlasRegion sprite = texture.getSpriteAtlasRegion2();
      int lifetime = this.duration.getIntValue();
      class_2960 atlas = WexsideHitParticles.getParticleTexture();
      boolean wasDepthTest = GL11.glIsEnabled(2929);
      boolean wasDepthWrite = GL11.glGetBoolean(2930);
      boolean wasBlend = GL11.glIsEnabled(3042);
      int previousBlendSrc = GL11.glGetInteger(3041);
      int previousBlendDst = GL11.glGetInteger(3040);
      int previousBlendSrcAlpha = GL11.glGetInteger(32970);
      int previousBlendDstAlpha = GL11.glGetInteger(32969);
      try {
         GlStateManager._enableDepthTest();
         GlStateManager._depthMask(true);
         GlStateManager._enableBlend();
         GlStateManager._blendFuncSeparate(770, 771, 770, 771);

         for(HitParticlesModule.Particle particle : this.particles) {
            float life = particle.life(lifetime);
            if (!(life <= 0.02F)) {
               class_243 previous = particle.previous;
               class_243 current = particle.current;
               double x = class_3532.method_16436((double)tickDelta, previous.field_1352, current.field_1352);
               double y = class_3532.method_16436((double)tickDelta, previous.field_1351, current.field_1351);
               double z = class_3532.method_16436((double)tickDelta, previous.field_1350, current.field_1350);
               int tint = withAlpha(particle.color, class_3532.method_15340((int)(life * 255.0F), 0, 255));
               ParticleBillboardRenderer.draw(x, y, z, particle.size, particle.size, tint, atlas, false, particle.rotation, sprite.minU(), sprite.minV(), sprite.maxU(), sprite.maxV());
            }
         }
      } finally {
         setCap(2929, wasDepthTest);
         GlStateManager._depthMask(wasDepthWrite);
         setCap(3042, wasBlend);
         GlStateManager._blendFuncSeparate(previousBlendSrc, previousBlendDst, previousBlendSrcAlpha, previousBlendDstAlpha);
      }
   }

   private void trimOverflow() {
      int overflow = this.particles.size() - this.maxCount.getIntValue();
      if (overflow > 0) {
         this.particles.subList(0, overflow).clear();
      }
   }

   private void clearParticles() {
      this.particles.clear();
      this.resetClock();
   }

   private void resetClock() {
      this.tickAccumulator = 0.0;
      this.lastNanoTime = 0L;
   }

   private double frameDelta() {
      long now = System.nanoTime();
      long previous = this.lastNanoTime;
      this.lastNanoTime = now;
      if (previous == 0L) {
         return 0.05;
      } else {
         double delta = (double)(now - previous) / 1.0E9;
         return Double.isFinite(delta) && !(delta < 0.0) ? Math.min(delta, 0.25) : 0.05;
      }
   }

   private static void setCap(int cap, boolean enabled) {
      if (enabled) {
         GL11.glEnable(cap);
      } else {
         GL11.glDisable(cap);
      }
   }

   private int animationIndex() {
      String value = this.animationType.getSelectedOption();
      if (value == null) {
         return 0;
      } else {
         return switch(value) {
            case "0" -> 1;
            case "1" -> 2;
            case "2" -> 3;
            case "3" -> 4;
            case "4" -> 5;
            case "5" -> 6;
            case "6" -> 7;
            case "7" -> 8;
            case "8" -> 9;
            case "9" -> 10;
            default -> 0;
         };
      }
   }

   private int sampleColor(float t) {
      float clamped = class_3532.method_15363(t, 0.0F, 1.0F);
      if (this.color.isAstolfoMode()) {
         return this.color.getEditingColor(clamped);
      } else {
         int from = this.color.getPrimaryColor();
         int to = this.color.isDoubleColorMode() ? this.color.getSecondaryColor() : from;
         return lerpColor(from, to, clamped);
      }
   }

   private static int lerpColor(int from, int to, float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      int a1 = from >>> 24 & 0xFF;
      int r1 = from >> 16 & 0xFF;
      int g1 = from >> 8 & 0xFF;
      int b1 = from & 0xFF;
      int a2 = to >>> 24 & 0xFF;
      int r2 = to >> 16 & 0xFF;
      int g2 = to >> 8 & 0xFF;
      int b2 = to & 0xFF;
      int a = (int)((float)a1 + (float)(a2 - a1) * t);
      int r = (int)((float)r1 + (float)(r2 - r1) * t);
      int g = (int)((float)g1 + (float)(g2 - g1) * t);
      int b = (int)((float)b1 + (float)(b2 - b1) * t);
      return a << 24 | r << 16 | g << 8 | b;
   }

   private static int withAlpha(int color, int alpha) {
      return color & 16777215 | alpha << 24;
   }

   static final class Particle {
      class_243 previous;
      class_243 current;
      class_243 velocity;
      final int color;
      float size;
      float rotation;
      final int animation;
      int age;

      Particle(class_243 origin, class_243 velocity, int color, float size, float rotation, int animation) {
         this.previous = origin;
         this.current = origin;
         this.velocity = velocity;
         this.color = color;
         this.size = size;
         this.rotation = rotation;
         this.animation = animation;
         this.age = 0;
      }

      boolean isDead(int lifetime) {
         return this.age >= lifetime;
      }

      float life(int lifetime) {
         return lifetime <= 0 ? 0.0F : class_3532.method_15363(1.0F - (float)this.age / (float)lifetime, 0.0F, 1.0F);
      }

      void tick(double delta) {
         this.previous = this.current;
         this.current = this.current.method_1019(this.velocity.method_1021(delta));
         this.velocity = this.velocity.method_1021(0.96).method_1031(0.0, -0.015 * delta, 0.0);
         ++this.age;
         if (this.animation > 0) {
            this.rotation += (float)this.animation * 4.5F;
            this.size *= 0.985F;
         }
      }
   }
}
