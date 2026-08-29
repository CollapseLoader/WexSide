package ru.wexside.module.render;

import java.util.List;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.misc.LazyMeshModel;
import ru.wexside.misc.SpatialTransform;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.module.combat.AttackAuraModule;
import ru.wexside.render.DynamicTransforms;
import ru.wexside.render.model.BuiltInMesh;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class BlockOverlayModule extends Module implements ConfigSerializable {
   private static final double MIN_SIZE = 1.0E-4;
   private static final double SNAP_DISTANCE = 6.0;
   private static final float OUTLINE_WIDTH = 2.0F;
   private static final float ANIM_STEP = 0.004F;
   private static final double MAX_LOOK_DISTANCE_SQ = 36.0;
   private static final String[] SHADER_MODES = new String[]{"Static", "Nebula", "Ribbons", "Liquid", "Aurora", "Plasma"};
   private static volatile BlockOverlayModule instance;
   private static final LazyMeshModel MODEL = LazyMeshModel.create(BuiltInMesh.CUBE);
   private static final DynamicTransforms TRANSFORMS = new DynamicTransforms();
   private final BooleanSetting enabledSetting;
   private final ColorSetting color;
   private final ModeSetting mode;
   private final NumberSetting smoothFactor;
   private final BooleanSetting outline;
   private class_238 smoothedBox;
   private float animation;

   public BlockOverlayModule(EventBus eventBus) {
      super(eventBus, "block_overlay", "Block Overlay", "Подсвечивает блок, на который наведён прицел", ModuleCategory.valueOf("RENDER"));
      instance = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      ColorSetting colorSetting = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(0)
            .name("Color")
            .id("color")
            .description("Цвет подсветки")
            .aliases("color", "цвет"))
         .build();
      colorSetting.setPrimaryColor(0, -2140275457);
      colorSetting.setPrimaryColor(1, -1543135);
      colorSetting.setPrimaryColor(2, -9279489);
      colorSetting.setPrimaryColor(3, -46001);
      colorSetting.setPrimaryColor(4, -13218);
      colorSetting.setPrimaryColor(5, -10582785);
      colorSetting.setPrimaryColor(6, -2732032);
      this.color = colorSetting;
      this.registerSetting(colorSetting);
      this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Static", "Nebula", "Ribbons", "Liquid", "Aurora", "Plasma")
            .defaultOption("Static")
            .name("Mode")
            .id("mode")
            .description("Режим шейдера подсветки")
            .aliases("mode", "режим"))
         .build();
      this.registerSetting(this.mode);
      this.smoothFactor = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 100.0)
            .defaultValue(5.0)
            .multiplier(0.001)
            .precision(0)
            .animationSpeed(20.0F)
            .markers(10.0)
            .snapTo(10.0)
            .name("Smooth factor")
            .id("smooth_factor")
            .description("Плавность перетекания подсветки между блоками")
            .aliases("smooth", "плавность"))
         .build();
      this.registerSetting(this.smoothFactor);
      this.outline = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Outline")
            .id("outline")
            .description("Обводка по контуру блока"))
         .build();
      this.registerSetting(this.outline);
   }

   @Override
   protected void initialize() {
      this.listen(WorldRenderEvent.class, this::onWorldRender);
   }

   public static boolean isEnabled() {
      BlockOverlayModule module = instance;
      return module != null && module.enabledSetting.isEnabled();
   }

   private void onWorldRender(WorldRenderEvent event) {
      class_310 client = class_310.method_1551();
      class_243 cameraPos = client.field_1773.method_19418().method_71156();
      if (!this.enabledSetting.isEnabled() || client.field_1724 == null || client.field_1687 == null || cameraPos == null) {
         this.smoothedBox = null;
      } else if (AttackAuraModule.isActive4()) {
         this.smoothedBox = null;
      } else {
         class_239 hit = client.field_1765;
         if (hit instanceof class_3965 blockHit && blockHit.method_17783() == class_240.field_1332) {
            class_2338 pos = blockHit.method_17777();
            class_265 shape = client.field_1687.method_8320(pos).method_26218(client.field_1687, pos);
            if (shape.method_1110()) {
               this.smoothedBox = null;
               return;
            }

            if (client.field_1724.method_5828(1.0F).method_1025(this.blockCenter(pos)) > 36.0) {
               this.smoothedBox = null;
               return;
            }

            if (MODEL.isLoaded() && MODEL.getMeshModel() != null && !MODEL.getMeshModel().getList().isEmpty()) {
               class_238 box = this.smooth(shape.method_1107().method_996(pos), this.smoothFactor.getValue());
               SpatialTransform transform = this.toTransform(box);
               if (transform == null) {
                  return;
               }

               int fill = this.color.getColor(0.0F);
               int line = fill & 16777215 | 0xFF000000;
               this.animation += 0.004F;
               if (this.animation > 1000.0F) {
                  this.animation = 0.0F;
               }

               TRANSFORMS.process8(
                  MODEL.getMeshModel().getList(), cameraPos, fill, line, List.of(transform), this.outline.isEnabled(), 2.0F, this.animation, this.shaderIndex()
               );
               return;
            }

            return;
         }

         this.smoothedBox = null;
      }
   }

   private class_243 blockCenter(class_2338 pos) {
      return new class_243((double)pos.method_10263() + 0.5, (double)pos.method_10264() + 0.5, (double)pos.method_10260() + 0.5);
   }

   private int shaderIndex() {
      String selected = this.mode.getSelectedOption();

      for(int i = 0; i < SHADER_MODES.length; ++i) {
         if (SHADER_MODES[i].equals(selected)) {
            return i;
         }
      }

      return 0;
   }

   private SpatialTransform toTransform(class_238 box) {
      if (box == null) {
         return null;
      } else {
         double sizeX = box.field_1320 - box.field_1323;
         double sizeY = box.field_1325 - box.field_1322;
         double sizeZ = box.field_1324 - box.field_1321;
         return !(sizeX <= 1.0E-4) && !(sizeY <= 1.0E-4) && !(sizeZ <= 1.0E-4)
            ? new SpatialTransform(
               (box.field_1323 + box.field_1320) * 0.5,
               (box.field_1322 + box.field_1325) * 0.5,
               (box.field_1321 + box.field_1324) * 0.5,
               (float)sizeX,
               (float)sizeY,
               (float)sizeZ
            )
            : null;
      }
   }

   private class_238 smooth(class_238 target, double factor) {
      if (target == null) {
         this.smoothedBox = null;
         return null;
      } else if (this.smoothedBox != null && !(this.centerDistance(this.smoothedBox, target) > 6.0)) {
         this.smoothedBox = new class_238(
            this.damp(this.smoothedBox.field_1323, target.field_1323, factor),
            this.damp(this.smoothedBox.field_1322, target.field_1322, factor),
            this.damp(this.smoothedBox.field_1321, target.field_1321, factor),
            this.damp(this.smoothedBox.field_1320, target.field_1320, factor),
            this.damp(this.smoothedBox.field_1325, target.field_1325, factor),
            this.damp(this.smoothedBox.field_1324, target.field_1324, factor)
         );
         return this.smoothedBox;
      } else {
         this.smoothedBox = target;
         return this.smoothedBox;
      }
   }

   private double damp(double current, double target, double factor) {
      double stiffness = 2.0 / Math.max(0.001, factor);
      double decay = Math.exp(-(stiffness * 0.016));
      double delta = current - target;
      return target + (delta + stiffness * delta * 0.016) * decay;
   }

   private double centerDistance(class_238 a, class_238 b) {
      double dx = (a.field_1323 + a.field_1320) * 0.5 - (b.field_1323 + b.field_1320) * 0.5;
      double dy = (a.field_1322 + a.field_1325) * 0.5 - (b.field_1322 + b.field_1325) * 0.5;
      double dz = (a.field_1321 + a.field_1324) * 0.5 - (b.field_1321 + b.field_1324) * 0.5;
      return Math.sqrt(dx * dx + dy * dy + dz * dz);
   }
}
