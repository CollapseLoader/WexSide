package ru.wexside.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_2350.class_2351;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import ru.wexside.event.TotemPopEvent;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.misc.SpriteAtlasRegion;
import ru.wexside.misc.TotemEffectRenderer;
import ru.wexside.misc.WexsideHitParticles;
import ru.wexside.render.ParticleBillboardRenderer;

public final class Snowflake implements TotemEffectRenderer {
   private static final double FIXED_STEP = 0.016666666666666666;
   private final List<Snowflake.TotemParticle> particles = new ArrayList<>();
   private final TotemEffectSettings settings;
   private long previousFrameNanos;
   private double pendingTicks;

   public Snowflake(TotemEffectSettings settings) {
      this.settings = settings;
   }

   private void renderParticles(float tickDelta) {
      for(Snowflake.TotemParticle particle : this.particles) {
         if (!(particle.alpha <= 0.02F)) {
            double x = class_3532.method_16436((double)tickDelta, particle.previousPosition.field_1352, particle.position.field_1352);
            double y = class_3532.method_16436((double)tickDelta, particle.previousPosition.field_1351, particle.position.field_1351);
            double z = class_3532.method_16436((double)tickDelta, particle.previousPosition.field_1350, particle.position.field_1350);
            SpriteAtlasRegion sprite = particle.style.texture().getSpriteAtlasRegion();
            int color = ColorUtils.withAlpha(
               particle.color, (float)class_3532.method_15340((int)((float)(particle.color >>> 24 & 0xFF) * particle.alpha), 0, 255)
            );
            ParticleBillboardRenderer.draw(
               x,
               y,
               z,
               particle.size,
               particle.size,
               color,
               WexsideHitParticles.getParticleTexture(),
               false,
               particle.initialRotation + particle.rotationSpeed * (float)particle.age,
               sprite.minU(),
               sprite.minV(),
               sprite.maxU(),
               sprite.maxV()
            );
         }
      }
   }

   private Snowflake.ParticleStyle selectedStyle() {
      String var1 = this.settings.getString();

      return switch(var1) {
         case "Mini-star" -> new Snowflake.ParticleStyle(WexsideHitParticles.STAR, 0.7F);
         case "Snowflake" -> new Snowflake.ParticleStyle(WexsideHitParticles.SNOWFLAKE, 1.0F);
         case "Dollar" -> new Snowflake.ParticleStyle(WexsideHitParticles.DOLLAR, 1.0F);
         case "Cross" -> new Snowflake.ParticleStyle(WexsideHitParticles.CROSS, 1.0F);
         default -> new Snowflake.ParticleStyle(WexsideHitParticles.STAR, 1.0F);
      };
   }

   private class_243 randomVelocity(ThreadLocalRandom random) {
      double speed = this.settings.getDoubleType();
      if (this.settings.isActive()) {
         return new class_243(random.nextDouble(-speed, speed), random.nextDouble(-speed, speed + 0.2), random.nextDouble(-speed, speed));
      } else {
         double azimuth = random.nextDouble() * Math.PI * 2.0;
         double polar = random.nextDouble() * Math.PI;
         return new class_243(speed * Math.cos(azimuth) * Math.sin(polar), speed * Math.cos(polar) + 0.05, speed * Math.sin(azimuth) * Math.sin(polar));
      }
   }

   private double frameSeconds() {
      long now = System.nanoTime();
      if (this.previousFrameNanos == 0L) {
         this.previousFrameNanos = now;
         return 0.016666666666666666;
      } else {
         double seconds = (double)(now - this.previousFrameNanos) / 1.0E9;
         this.previousFrameNanos = now;
         return Math.clamp(seconds, 0.0, 0.05);
      }
   }

   private void simulate(class_310 client) {
      Iterator<Snowflake.TotemParticle> iterator = this.particles.iterator();

      while(iterator.hasNext()) {
         Snowflake.TotemParticle particle = iterator.next();
         class_2338 blockPos = class_2338.method_49638(particle.position);
         if (client.field_1687.method_8477(blockPos) && !(particle.position.field_1351 < 0.0) && !(particle.alpha <= 0.0F)) {
            particle.previousPosition = particle.position;
            ++particle.age;
            class_243 velocity = this.settings.isActive()
               ? particle.velocity.method_1021(0.99).method_1023(0.0, 0.03, 0.0)
               : particle.velocity.method_1021(0.96).method_1031(0.0, 0.003, 0.0);
            class_243 end = particle.position.method_1019(velocity);
            class_3965 collision = null;
            if (this.settings.isActive()) {
               class_3965 hit = client.field_1687
                  .method_17742(new class_3959(particle.position, end, class_3960.field_17558, class_242.field_1348, client.field_1724));
               if (hit.method_17783() == class_240.field_1332) {
                  class_2351 axis = hit.method_17780().method_10166();
                  velocity = new class_243(
                     axis == class_2351.field_11048 ? -velocity.field_1352 : velocity.field_1352,
                     axis == class_2351.field_11052 ? -velocity.field_1351 : velocity.field_1351,
                     axis == class_2351.field_11051 ? -velocity.field_1350 : velocity.field_1350
                  );
                  collision = hit;
               }
            }

            particle.position = collision != null ? collision.method_17784() : particle.position.method_1019(velocity);
            particle.velocity = velocity;
            particle.size *= 0.97F;
            if (particle.age >= this.settings.getIntType2()) {
               particle.alpha = Math.max(0.0F, particle.alpha - 0.03F);
            }
         } else {
            iterator.remove();
         }
      }
   }

   @Override
   public void renderWorld(WorldRenderEvent event) {
      class_310 client = class_310.method_1551();
      if (client.field_1687 == null || client.field_1724 == null) {
         this.update2();
      } else if (this.particles.isEmpty()) {
         this.previousFrameNanos = System.nanoTime();
      } else {
         this.pendingTicks += this.frameSeconds() * 20.0;
         int updates = Math.min(10, (int)this.pendingTicks);
         if (updates > 0) {
            this.pendingTicks -= (double)updates;

            for(int i = 0; i < updates; ++i) {
               this.simulate(client);
            }
         }

         this.renderParticles(event.getFloatType());
      }
   }

   @Override
   public void setTotemPopEvent(TotemPopEvent event) {
      ThreadLocalRandom random = ThreadLocalRandom.current();
      Snowflake.ParticleStyle style = this.selectedStyle();
      int count = this.settings.getIntType() + random.nextInt(4);
      int color = this.settings.getIntType3();

      for(int i = 0; i < count; ++i) {
         this.particles
            .add(
               new Snowflake.TotemParticle(
                  event.toVec3d(),
                  this.randomVelocity(random),
                  color,
                  (0.2F + random.nextFloat() * 0.15F) * style.scale(),
                  random.nextFloat() * 360.0F,
                  random.nextFloat() * 5.0F + 2.5F,
                  style
               )
            );
      }
   }

   @Override
   public void update2() {
      this.particles.clear();
      this.previousFrameNanos = 0L;
      this.pendingTicks = 0.0;
   }

   private static record ParticleStyle(WexsideHitParticles texture, float scale) {
   }

   private static final class TotemParticle {
      private final Snowflake.ParticleStyle style;
      private final int color;
      private class_243 position;
      private class_243 previousPosition;
      private class_243 velocity;
      private float size;
      private float alpha = 1.0F;
      private final float initialRotation;
      private final float rotationSpeed;
      private int age;

      private TotemParticle(
         class_243 position, class_243 velocity, int color, float size, float initialRotation, float rotationSpeed, Snowflake.ParticleStyle style
      ) {
         this.position = position;
         this.previousPosition = position;
         this.velocity = velocity;
         this.color = color;
         this.size = size;
         this.initialRotation = initialRotation;
         this.rotationSpeed = rotationSpeed;
         this.style = style;
      }
   }
}
