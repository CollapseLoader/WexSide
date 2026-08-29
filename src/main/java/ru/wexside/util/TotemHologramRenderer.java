package ru.wexside.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_7833;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import ru.wexside.event.TotemPopEvent;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.misc.LazyMeshModel;
import ru.wexside.misc.SpriteAtlasRegion;
import ru.wexside.misc.TotemEffectRenderer;
import ru.wexside.misc.WexsideHitParticles;
import ru.wexside.render.ParticleBillboardRenderer;
import ru.wexside.render.RenderCamera;
import ru.wexside.render.model.BuiltInMesh;
import ru.wexside.render.model.MeshBuilder;

public final class TotemHologramRenderer implements TotemEffectRenderer {
   private final List<TotemHologramRenderer.TotemHologram> holograms;
   private TotemHologramRenderer.MeshParticleTemplate meshParticleTemplate;
   static final float value2 = 1.65F;
   private final SpriteAtlasRegion spriteAtlasRegion;
   static final float value3 = 0.05F;
   static final double value4 = 0.5;
   private final Random random;
   private List<InlineMesh> values3;
   static final float value5 = 0.7F;
   static final int slot = 500;
   private List<InlineMesh> values4;
   private double value6;
   private static final InlineMeshRenderer HOLOGRAM_RENDERER = new InlineMeshRenderer("totem-hologram");
   private final TotemEffectSettings totemEffectSettings;
   private List<InlineMesh> values5;
   static final float getColorChannel8 = 15.0F;
   static final float value7 = 0.3F;
   static final float value8 = 0.5F;
   static final double value9 = 10000.0;
   static final float value10 = 1.0F;
   private final LazyMeshModel lazyMeshModel = LazyMeshModel.create(BuiltInMesh.PLAYER);
   static final long member13120 = 1399L;

   public TotemHologramRenderer(TotemEffectSettings totemEffectSettings) {
      this.holograms = new ArrayList<>();
      this.spriteAtlasRegion = WexsideHitParticles.LIGHT.getSpriteAtlasRegion();
      this.random = new Random();
      this.totemEffectSettings = totemEffectSettings;
   }

   @Override
   public void renderWorld(WorldRenderEvent floatTypeEvent2) {
      class_310 mc = class_310.method_1551();
      if (mc.field_1687 != null && mc.field_1724 != null && RenderCamera.position() != null) {
         if (!this.holograms.isEmpty()) {
            if (this.isActive()) {
               long l = System.currentTimeMillis();

               for(int i = this.holograms.size() - 1; i >= 0; --i) {
                  TotemHologramRenderer.TotemHologram iliIllIll2 = this.holograms.get(i);
                  if (this.process3(mc, iliIllIll2, l)) {
                     this.holograms.remove(i);
                  } else {
                     this.process(floatTypeEvent2, mc, iliIllIll2, l);
                  }
               }
            }
         }
      } else {
         this.update2();
      }
   }

   @Override
   public void setTotemPopEvent(TotemPopEvent lIiillIliIEvent) {
      class_310 mc = class_310.method_1551();
      if (lIiillIliIEvent.getEntity() != null && mc.field_1724 != null) {
         class_243 vec = new class_243(
            lIiillIliIEvent.getEntity().method_23317(),
            lIiillIliIEvent.getEntity().method_23318() + (double)lIiillIliIEvent.getEntity().method_17682() * 0.5,
            lIiillIliIEvent.getEntity().method_23321()
         );
         class_243 vec2 = new class_243(
            mc.field_1724.method_23317(), mc.field_1724.method_23318() + (double)mc.field_1724.method_17682() * 0.5, mc.field_1724.method_23321()
         );
         class_243 vec3 = vec2.method_1020(vec);
         if (vec3.method_1027() <= 1.0E-6) {
            vec3 = new class_243(0.0, 0.0, 1.0);
         }

         this.holograms
            .add(new TotemHologramRenderer.TotemHologram(vec.method_1019(vec3.method_1029().method_1021(0.5)), System.currentTimeMillis(), lIiillIliIEvent));
         this.update();
      }
   }

   private void process(WorldRenderEvent floatTypeEvent2, class_310 mc, TotemHologramRenderer.TotemHologram hologram, long l) {
      class_243 vec = new class_243(
         mc.field_1724.method_23317(), mc.field_1724.method_23318() + (double)mc.field_1724.method_17682(), mc.field_1724.method_23321()
      );
      double d = vec.field_1352 - hologram.position.field_1352;
      double d2 = vec.field_1351 - hologram.position.field_1351;
      double d3 = vec.field_1350 - hologram.position.field_1350;
      float f = (float)Math.toDegrees(Math.atan2(d, d3));
      float f2 = (float)Math.toDegrees(-Math.atan2(d2, Math.sqrt(d * d + d3 * d3)));
      double d4 = hologram.progress(l);
      float f3 = this.process9((float)d4);
      float f4 = (float)((l - hologram.startedAt) % 3600L) / 10.0F;
      float f5 = (float)(1.0 + Math.sin((double)(f4 * 0.05F)) * 0.5);
      float f6 = (float)Math.sin((double)f3 * Math.PI * 6.0) * 0.35F;
      float f7 = (float)(0.2 * (1.0 - (double)f3));
      float f8 = f4 * 2.0F;
      float f9 = (float)Math.sin(d4 * Math.PI * 8.0) * 7.0F;
      float f10 = (this.random.nextFloat() - 0.5F) * 2.0F;
      float f11 = 1.65F * (float)(d4 < 0.5 ? 2.0 * d4 * (1.2 - d4) : 1.0 - (d4 - 0.5) * 2.0);
      class_243 vec2 = RenderCamera.position();
      class_4587 matrices2 = new class_4587();
      matrices2.method_22904(
         hologram.position.field_1352 - vec2.field_1352, hologram.position.field_1351 - vec2.field_1351, hologram.position.field_1350 - vec2.field_1350
      );
      matrices2.method_22907(class_7833.field_40716.rotationDegrees(f - 25.0F));
      matrices2.method_22907(class_7833.field_40714.rotationDegrees(f2));
      matrices2.method_22907(class_7833.field_40716.rotationDegrees(f4 * f5));
      matrices2.method_46416(0.0F, f6, 0.0F);
      matrices2.method_22904(Math.sin(Math.toRadians((double)f8)) * (double)f7, 0.0, Math.cos(Math.toRadians((double)f8)) * (double)f7);
      matrices2.method_22907(class_7833.field_40718.rotationDegrees(f9 + f10));
      matrices2.method_22905(f11, f11, f11);
      if (d4 < 0.3F) {
         matrices2.method_46416((this.random.nextFloat() - 0.5F) * 0.05F, (this.random.nextFloat() - 0.5F) * 0.05F, (this.random.nextFloat() - 0.5F) * 0.05F);
      }

      Matrix4f matrix4f = new Matrix4f(matrices2.method_23760().method_23761());
      float f12 = d4 < 0.3F ? 0.0F : class_3532.method_15363((float)((d4 - 0.3F) / 0.19999999F), 0.0F, 1.0F);
      float f13 = d4 >= 0.5 ? class_3532.method_15363((float)((d4 - 0.5) / 0.19999999F), 0.0F, 1.0F) : 0.0F;
      float f14 = d4 >= 0.7F ? class_3532.method_15363((float)((d4 - 0.7F) / 0.3F), 0.0F, 1.0F) : 0.0F;
      float f15 = d4 >= 0.5 ? 1.0F - f13 : 1.0F;
      float f16 = d4 >= 0.5 ? f13 * (1.0F - f14) : 0.0F;
      int n = this.totemEffectSettings.getIntType3();
      if (f15 > 0.0F) {
         if (d4 < 0.3F) {
            HOLOGRAM_RENDERER.process11(
               floatTypeEvent2.getMatrices(),
               mc.method_22940().method_23000(),
               class_243.field_1353,
               this.values4,
               null,
               null,
               this.process10(n, f15 * 2.5F),
               List.of(matrix4f),
               ModelRenderOptions.getDefaultRenderOptions().process10(true)
            );
         } else {
            Matrix4f matrix4f2 = new Matrix4f(matrix4f).translate(1.0F * f12, 0.0F, 0.0F).rotateZ((float)Math.toRadians((double)(15.0F * f12)));
            Matrix4f matrix4f3 = new Matrix4f(matrix4f).translate(-1.0F * f12, 0.0F, 0.0F).rotateZ((float)Math.toRadians((double)(-15.0F * f12)));
            if (!this.values3.isEmpty()) {
               HOLOGRAM_RENDERER.process11(
                  floatTypeEvent2.getMatrices(),
                  mc.method_22940().method_23000(),
                  class_243.field_1353,
                  this.values3,
                  null,
                  null,
                  this.process10(n, f15 * 2.5F),
                  List.of(matrix4f2),
                  ModelRenderOptions.getDefaultRenderOptions().process10(true)
               );
            }

            if (!this.values5.isEmpty()) {
               HOLOGRAM_RENDERER.process11(
                  floatTypeEvent2.getMatrices(),
                  mc.method_22940().method_23000(),
                  class_243.field_1353,
                  this.values5,
                  null,
                  null,
                  this.process10(n, f15),
                  List.of(matrix4f3),
                  ModelRenderOptions.getDefaultRenderOptions().process10(true)
               );
            }

            if (f16 > 0.0F) {
               hologram.initializeParticles(this.meshParticleTemplate, this.value6);
               class_243 vec3 = hologram.attractionPoint();
               float f17 = 0.022F * 2.0F * (1.0F - f14);
               float f18 = f14 * 2.0F + f13 * 0.2F;
               float f19 = f13 + f14;
               this.process7(hologram.startedAt, hologram.leftParticles, matrix4f3, vec3, n, f16, f18, f19, f14, f17, 0);
               this.process7(hologram.startedAt, hologram.rightParticles, matrix4f2, vec3, n, f16, f18, f19, f14, f17, hologram.leftParticles.length);
            }
         }
      }
   }

   private boolean process2(float[] fArray, int n, int n2, int n3, double d, boolean bl) {
      return this.process8(fArray[n * 3], d, bl) && this.process8(fArray[n2 * 3], d, bl) && this.process8(fArray[n3 * 3], d, bl);
   }

   private boolean process3(class_310 mc, TotemHologramRenderer.TotemHologram hologram, long l) {
      if (l - hologram.startedAt > 1399L) {
         return true;
      } else {
         class_243 vec = mc.field_1724 != null
            ? new class_243(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321())
            : class_243.field_1353;
         return vec.method_1025(hologram.position) > 10000.0;
      }
   }

   private List<InlineMesh> process4(List<InlineMesh> list, double d, boolean bl) {
      ArrayList<InlineMesh> arrayList = new ArrayList<>();

      for(InlineMesh inlineMesh : list) {
         int[] nArray = inlineMesh.getIntType2();
         if (nArray != null && nArray.length != 0) {
            ArrayList<Integer> arrayList2 = new ArrayList<>();
            float[] fArray = inlineMesh.getFloatType();

            for(int n2 = 0; n2 + 2 < nArray.length; n2 += 3) {
               int n = nArray[n2];
               int n3 = nArray[n2 + 1];
               int n4 = nArray[n2 + 2];
               if (this.process2(fArray, n, n3, n4, d, bl)) {
                  arrayList2.add(n);
                  arrayList2.add(n3);
                  arrayList2.add(n4);
               }
            }

            if (!arrayList2.isEmpty()) {
               int[] nArray2 = new int[arrayList2.size()];

               for(int n = 0; n < arrayList2.size(); ++n) {
                  nArray2[n] = arrayList2.get(n);
               }

               arrayList.add(
                  MeshBuilder.create(inlineMesh.getFloatType(), inlineMesh.getFloatType3(), inlineMesh.getFloatType2(), nArray2, inlineMesh.getFloatType4())
               );
            }
         }
      }

      return arrayList;
   }

   private class_243 process5(Matrix4f matrix4f, double d, double d2, double d3) {
      Vector4f vector4f = new Vector4f((float)d, (float)d2, (float)d3, 1.0F);
      matrix4f.transform(vector4f);
      return new class_243((double)vector4f.x(), (double)vector4f.y(), (double)vector4f.z());
   }

   private double process6(List<InlineMesh> list) {
      double d = Double.POSITIVE_INFINITY;
      double d2 = Double.NEGATIVE_INFINITY;

      for(InlineMesh inlineMesh : list) {
         float[] fArray = inlineMesh.getFloatType();

         for(int n = 0; n + 2 < fArray.length; n += 3) {
            d = Math.min(d, (double)fArray[n]);
            d2 = Math.max(d2, (double)fArray[n]);
         }
      }

      return (d + d2) * 0.5;
   }

   private void update() {
      int n = this.holograms.size() - 12;
      if (n > 0) {
         this.holograms.subList(0, n).clear();
      }
   }

   private void process7(
      long l,
      TotemHologramRenderer.MeshParticle[] iIililiIIIArray,
      Matrix4f matrix4f,
      class_243 vec,
      int n,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      int n2
   ) {
      if (iIililiIIIArray.length != 0 && !(f5 <= 0.001F)) {
         class_243 vec2 = RenderCamera.position();

         for(int i = 0; i < iIililiIIIArray.length; ++i) {
            TotemHologramRenderer.MeshParticle particle = iIililiIIIArray[i];
            Random random = new Random(l + (long)n2 + (long)i);
            double d = particle.position.field_1352 + particle.direction.field_1352 * (double)f2 + (double)((random.nextFloat() - 0.5F) * 0.5F * f3);
            double d2 = particle.position.field_1351
               + particle.direction.field_1351 * (double)f2
               + (double)((random.nextFloat() - 0.5F) * 0.5F * f3)
               - (double)(f4 * 2.0F);
            double d3 = particle.position.field_1350 + particle.direction.field_1350 * (double)f2 + (double)((random.nextFloat() - 0.5F) * 0.5F * f3);
            class_243 vec4 = this.process5(matrix4f, d, d2, d3).method_1031(vec2.field_1352, vec2.field_1351, vec2.field_1350);
            if (vec != null) {
               class_243 vec3 = vec.method_1020(vec4);
               if (vec3.method_1027() > 1.0E-6) {
                  vec4 = vec4.method_1019(vec3.method_1029().method_1021((double)(f4 * 2.0F)));
               }
            }

            ParticleBillboardRenderer.draw(
               vec4.field_1352,
               vec4.field_1351,
               vec4.field_1350,
               f5,
               f5,
               this.process10(n, f),
               WexsideHitParticles.getParticleTexture(),
               false,
               0.0F,
               this.spriteAtlasRegion.minU(),
               this.spriteAtlasRegion.minV(),
               this.spriteAtlasRegion.maxU(),
               this.spriteAtlasRegion.maxV()
            );
         }
      }
   }

   private boolean isActive() {
      if (!this.lazyMeshModel.isLoaded() || this.lazyMeshModel.getMeshModel() == null || this.lazyMeshModel.getMeshModel().getList().isEmpty()) {
         return false;
      } else if (this.values4 != null) {
         return true;
      } else {
         this.values4 = List.copyOf(this.lazyMeshModel.getMeshModel().getList());
         this.value6 = this.process6(this.values4);
         this.values5 = this.process4(this.values4, this.value6, false);
         this.values3 = this.process4(this.values4, this.value6, true);
         this.meshParticleTemplate = TotemHologramRenderer.MeshParticleTemplate.from(this.values4);
         return true;
      }
   }

   private boolean process8(float f, double d, boolean bl) {
      return bl ? (double)f >= d : (double)f < d;
   }

   private float process9(float f) {
      float f2 = class_3532.method_15363(f, 0.0F, 1.0F);
      return f2 < 0.5F ? 4.0F * f2 * f2 * f2 : 1.0F - (float)Math.pow((double)(-2.0F * f2 + 2.0F), 3.0) * 0.5F;
   }

   private int process10(int n, float f) {
      int n2 = class_3532.method_15340((int)((float)(n >>> 24 & 0xFF) * f), 0, 255);
      return ColorUtils.withAlpha(n, (float)n2);
   }

   @Override
   public void update2() {
      this.holograms.clear();
   }

   private static record MeshParticle(class_243 position, class_243 direction) {
   }

   private static record MeshParticleTemplate(List<TotemHologramRenderer.MeshParticle> particles) {
      private static TotemHologramRenderer.MeshParticleTemplate from(List<InlineMesh> meshes) {
         ArrayList<TotemHologramRenderer.MeshParticle> particles = new ArrayList<>();

         for(InlineMesh mesh : meshes) {
            float[] positions = mesh.getFloatType();
            float[] normals = mesh.getFloatType3();

            for(int index = 0; index + 2 < positions.length; index += 3) {
               class_243 position = new class_243((double)positions[index], (double)positions[index + 1], (double)positions[index + 2]);
               class_243 direction = normals != null && index + 2 < normals.length
                  ? new class_243((double)normals[index], (double)normals[index + 1], (double)normals[index + 2])
                  : (position.method_1027() > 1.0E-7 ? position.method_1029() : class_243.field_1353);
               particles.add(new TotemHologramRenderer.MeshParticle(position, direction));
            }
         }

         if (particles.size() > 500) {
            int step = Math.max(1, particles.size() / 500);
            ArrayList<TotemHologramRenderer.MeshParticle> sampled = new ArrayList<>(500);

            for(int index = 0; index < particles.size() && sampled.size() < 500; index += step) {
               sampled.add(particles.get(index));
            }

            particles = sampled;
         }

         return new TotemHologramRenderer.MeshParticleTemplate(List.copyOf(particles));
      }

      private TotemHologramRenderer.MeshParticle[] leftOf(double splitX) {
         return this.particles.stream().filter(particle -> particle.position().field_1352 < splitX).toArray(x$0 -> new TotemHologramRenderer.MeshParticle[x$0]);
      }

      private TotemHologramRenderer.MeshParticle[] rightOf(double splitX) {
         return this.particles
            .stream()
            .filter(particle -> particle.position().field_1352 >= splitX)
            .toArray(x$0 -> new TotemHologramRenderer.MeshParticle[x$0]);
      }
   }

   private static final class TotemHologram {
      private final class_243 position;
      private final long startedAt;
      private final TotemPopEvent event;
      private TotemHologramRenderer.MeshParticle[] leftParticles = new TotemHologramRenderer.MeshParticle[0];
      private TotemHologramRenderer.MeshParticle[] rightParticles = new TotemHologramRenderer.MeshParticle[0];
      private boolean particlesInitialized;

      private TotemHologram(class_243 position, long startedAt, TotemPopEvent event) {
         this.position = position;
         this.startedAt = startedAt;
         this.event = event;
      }

      private double progress(long now) {
         return class_3532.method_15350((double)(now - this.startedAt) / 1399.0, 0.0, 1.0);
      }

      private void initializeParticles(TotemHologramRenderer.MeshParticleTemplate template, double splitX) {
         if (!this.particlesInitialized && template != null) {
            this.leftParticles = template.leftOf(splitX);
            this.rightParticles = template.rightOf(splitX);
            this.particlesInitialized = true;
         }
      }

      private class_243 attractionPoint() {
         return this.event.getEntity() == null
            ? this.position
            : this.event.getEntity().method_30950(1.0F).method_1031(0.0, (double)this.event.getEntity().method_17682() * 0.5, 0.0);
      }
   }
}
