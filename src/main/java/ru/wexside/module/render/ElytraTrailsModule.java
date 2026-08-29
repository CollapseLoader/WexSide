package ru.wexside.module.render;

import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_12249;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;

public final class ElytraTrailsModule extends Module implements ConfigSerializable {
   private static final double MIN_DISTANCE = 3.0E-5;
   private static final int MIN_POINTS = 4;
   private final BooleanSetting enabledSetting;
   private final ColorSetting color;
   private final List<ElytraTrailsModule.TrailPoint> trail = new ArrayList<>();

   public ElytraTrailsModule(EventBus eventBus) {
      super(eventBus, "elytra_trails", "Elytra Trails", "След за игроком при полёте на элитрах", ModuleCategory.valueOf("RENDER"));
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Включить след при полёте на элитрах")
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
   }

   @Override
   protected void initialize() {
      this.listen(WorldRenderEvent.class, this::onWorldRender);
      this.listen(WorldSessionEvent.class, event -> this.trail.clear());
   }

   private void onWorldRender(WorldRenderEvent event) {
      if (!this.enabledSetting.isEnabled()) {
         this.trail.clear();
      } else {
         class_310 client = class_310.method_1551();
         if (client.field_1687 != null && client.field_1724 != null && client.field_1724.method_6101()) {
            class_243 pos = client.field_1724.method_30950(event.getFloatType());
            if (this.trail.isEmpty()) {
               this.trail.add(new ElytraTrailsModule.TrailPoint(pos, pos, false));
            } else {
               ElytraTrailsModule.TrailPoint last = this.trail.getLast();
               if (pos.method_1025(last.to) > 3.0E-5) {
                  this.trail.add(new ElytraTrailsModule.TrailPoint(last.to, pos, true));
               }
            }

            this.trail.removeIf(ElytraTrailsModule.TrailPoint::expired);
            this.renderTrail(event);
         } else {
            this.trail.clear();
         }
      }
   }

   private void renderTrail(WorldRenderEvent event) {
      class_310 client = class_310.method_1551();
      if (client.field_1724 != null && client.field_1724.method_6128() && !client.field_1690.method_31044().method_31034() && this.trail.size() >= 4) {
         boolean cullEnabled = GL46.glIsEnabled(2884);
         boolean depthMask = GL46.glGetBoolean(2930);
         GL46.glDisable(2884);
         GL46.glDepthMask(false);

         try {
            class_4184 camera = client.field_1773.method_19418();
            class_243 cameraPos = camera.method_71156();
            float[] rgba = this.colorComponents(this.color.getColor());
            Matrix4f matrix = new Matrix4f().rotation(camera.method_23767());
            class_287 consumer = class_289.method_1348().method_60827(class_5596.field_27382, class_290.field_1576);

            for(int i = 1; i < this.trail.size() - 2; ++i) {
               ElytraTrailsModule.TrailPoint prev = this.trail.get(Math.max(0, i - 1));
               ElytraTrailsModule.TrailPoint current = this.trail.get(i);
               ElytraTrailsModule.TrailPoint next = this.trail.get(i + 1);
               ElytraTrailsModule.TrailPoint after = this.trail.get(Math.min(this.trail.size() - 1, i + 2));
               if (current.valid && next.valid && !(current.from.method_1025(current.to) <= 1.0E-7) && !(next.from.method_1025(next.to) <= 1.0E-7)) {
                  ElytraTrailsModule.TrailVertex left = this.buildVertex(current, prev, next, i, cameraPos, event.getFloatType(), rgba, 0);
                  ElytraTrailsModule.TrailVertex right = this.buildVertex(next, current, after, i + 1, cameraPos, event.getFloatType(), rgba, 1);
                  if (!(left.alpha <= 0.001F) && !(right.alpha <= 0.001F)) {
                     this.emitRibbon(consumer, matrix, left, right);
                  }
               }
            }

            class_12249.method_76023().method_60895(consumer.method_60800());
         } finally {
            GL46.glDepthMask(depthMask);
            if (cullEnabled) {
               GL46.glEnable(2884);
            } else {
               GL46.glDisable(2884);
            }
         }
      }
   }

   private ElytraTrailsModule.TrailVertex buildVertex(
      ElytraTrailsModule.TrailPoint point,
      ElytraTrailsModule.TrailPoint prev,
      ElytraTrailsModule.TrailPoint next,
      int index,
      class_243 cameraPos,
      float tickDelta,
      float[] rgba,
      int layer
   ) {
      class_243 pos = point.lerp(tickDelta);
      class_243 prevPos = prev.lerp(tickDelta);
      class_243 nextPos = next.lerp(tickDelta);
      class_243 tangent = nextPos.method_1020(prevPos);
      if (tangent.method_1027() < 1.0E-5) {
         tangent = point.to.method_1020(point.from);
      }

      if (tangent.method_1027() < 1.0E-5) {
         tangent = new class_243(0.0, 0.0, 1.0);
      }

      tangent = tangent.method_1029();
      class_243 side = new class_243(-tangent.field_1350, 0.0, tangent.field_1352);
      side = side.method_1027() < 1.0E-5 ? new class_243(1.0, 0.0, 0.0) : side.method_1029();
      class_243 relative = pos.method_1020(cameraPos);
      float progress = point.progress();
      float fade = point.fade();
      float time = (float)class_310.method_1551().field_1687.method_75260();
      float wave = class_3532.method_15374((double)(time * 0.15F + (float)index * 0.5F)) * 0.15F * fade;
      float alpha;
      float blue;
      float green;
      float red;
      float width;
      if (layer == 0) {
         width = 0.2F + 0.2F * progress + wave * 0.1F;
         float dim = progress * 0.3F;
         red = rgba[0] * (1.0F - dim);
         green = rgba[1] * (1.0F - dim);
         blue = rgba[2] * (1.0F - dim);
         alpha = fade * rgba[3];
      } else if (layer == 1) {
         width = 0.2F + (class_3532.method_15374((double)(time * 0.2F)) * 0.1F + 0.1F) * fade + wave * 0.1F;
         float boost = progress * 0.2F;
         red = class_3532.method_15363(rgba[0] + boost, 0.0F, 1.0F);
         green = class_3532.method_15363(rgba[1] + boost, 0.0F, 1.0F);
         blue = class_3532.method_15363(rgba[2] + boost, 0.0F, 1.0F);
         alpha = fade * rgba[3];
      } else {
         width = 0.1F + wave * 0.05F;
         red = rgba[0];
         green = rgba[1];
         blue = rgba[2];
         alpha = class_3532.method_15363(fade * rgba[3] * 0.3F, 0.0F, 0.3F);
      }

      return new ElytraTrailsModule.TrailVertex(
         (float)relative.field_1352,
         (float)relative.field_1351,
         (float)relative.field_1350,
         side,
         Math.max(width, 0.001F),
         red,
         green,
         blue,
         class_3532.method_15363(alpha, 0.0F, 1.0F)
      );
   }

   private void emitRibbon(class_287 consumer, Matrix4f matrix, ElytraTrailsModule.TrailVertex left, ElytraTrailsModule.TrailVertex right) {
      class_243 a = left.side.method_1021((double)(-left.width));
      class_243 b = left.side.method_1021((double)left.width);
      class_243 c = right.side.method_1021((double)right.width);
      class_243 d = right.side.method_1021((double)(-right.width));
      this.vertex(
         consumer,
         matrix,
         left.x + (float)a.field_1352,
         left.y + (float)a.field_1351,
         left.z + (float)a.field_1350,
         left.red,
         left.green,
         left.blue,
         left.alpha
      );
      this.vertex(
         consumer,
         matrix,
         left.x + (float)b.field_1352,
         left.y + (float)b.field_1351,
         left.z + (float)b.field_1350,
         left.red,
         left.green,
         left.blue,
         left.alpha
      );
      this.vertex(
         consumer,
         matrix,
         right.x + (float)c.field_1352,
         right.y + (float)c.field_1351,
         right.z + (float)c.field_1350,
         right.red,
         right.green,
         right.blue,
         right.alpha
      );
      this.vertex(
         consumer,
         matrix,
         right.x + (float)d.field_1352,
         right.y + (float)d.field_1351,
         right.z + (float)d.field_1350,
         right.red,
         right.green,
         right.blue,
         right.alpha
      );
   }

   private void vertex(class_287 consumer, Matrix4f matrix, float x, float y, float z, float red, float green, float blue, float alpha) {
      consumer.method_22918(matrix, x, y, z).method_22915(red, green, blue, alpha);
   }

   private float[] colorComponents(int color) {
      return new float[]{
         (float)(color >> 16 & 0xFF) / 255.0F, (float)(color >> 8 & 0xFF) / 255.0F, (float)(color & 0xFF) / 255.0F, (float)(color >>> 24 & 0xFF) / 255.0F
      };
   }

   static final class TrailPoint {
      final class_243 from;
      final class_243 to;
      final boolean valid;

      TrailPoint(class_243 from, class_243 to, boolean valid) {
         this.from = from;
         this.to = to;
         this.valid = valid;
      }

      class_243 lerp(float tickDelta) {
         return new class_243(
            class_3532.method_16436((double)tickDelta, this.from.field_1352, this.to.field_1352),
            class_3532.method_16436((double)tickDelta, this.from.field_1351, this.to.field_1351),
            class_3532.method_16436((double)tickDelta, this.from.field_1350, this.to.field_1350)
         );
      }

      float progress() {
         return class_3532.method_15363((float)this.from.method_1022(this.to) * 4.0F, 0.0F, 1.0F);
      }

      float fade() {
         return class_3532.method_15363(1.0F - this.progress(), 0.0F, 1.0F);
      }

      boolean expired() {
         return !this.valid;
      }
   }

   static final class TrailVertex {
      final float x;
      final float y;
      final float z;
      final class_243 side;
      final float width;
      final float red;
      final float green;
      final float blue;
      final float alpha;

      TrailVertex(float x, float y, float z, class_243 side, float width, float red, float green, float blue, float alpha) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.side = side;
         this.width = width;
         this.red = red;
         this.green = green;
         this.blue = blue;
         this.alpha = alpha;
      }
   }
}
