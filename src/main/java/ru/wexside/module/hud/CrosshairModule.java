package ru.wexside.module.hud;

import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_239;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3966;
import net.minecraft.class_408;
import net.minecraft.class_5498;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.misc.TransitionAnimation;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.Easing;
import ru.wexside.util.GuiDrawApi;

public final class CrosshairModule extends Module implements ConfigSerializable {
   private static volatile CrosshairModule instance;
   private static final float OUTLINE_SIZE = 1.0F;
   private static final float ATTACK_WAVE_SCALE = 5.0F;
   private final BooleanSetting enabledSetting;
   private final ColorSetting color;
   private final BooleanSetting rayTrace;
   private final ColorSetting hoverColor;
   private final BooleanSetting tLike;
   private final BooleanSetting dot;
   private final BooleanSetting displayCooldown;
   private final NumberSetting length;
   private final NumberSetting gap;
   private final TransitionAnimation hoverAnimation;

   public CrosshairModule(EventBus eventBus) {
      super(eventBus, "crosshair", "Crosshair", "Кастомный прицел", ModuleCategory.valueOf("DISPLAY"));
      instance = this;
      this.hoverAnimation = new TransitionAnimation(Easing.LINEAR);
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Кастомный прицел")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      ColorSetting crosshairColor = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(0)
            .name("Color")
            .id("color")
            .description("Цвет прицела")
            .aliases("color", "цвет"))
         .build();
      this.applyPalette(crosshairColor);
      this.color = crosshairColor;
      this.registerSetting(crosshairColor);
      this.rayTrace = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("RayTrace")
            .id("ray_trace")
            .description("Меняет цвет при наведении на Entity")
            .aliases("raytrace", "наведение"))
         .build();
      this.registerSetting(this.rayTrace);
      ColorSetting hover = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(0)
            .name("RayTrace Color")
            .id("hover_color")
            .description("Цвет при наведении на Entity")
            .aliases("hover color", "цвет наведения")
            .visibleWhen(this.rayTrace::isEnabled))
         .build();
      this.applyPalette(hover);
      this.hoverColor = hover;
      this.registerSetting(hover);
      this.tLike = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("T-Like")
            .id("t_like")
            .description("Форма прицела T-образная")
            .aliases("tlike", "т-образный"))
         .build();
      this.registerSetting(this.tLike);
      this.dot = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Dot")
            .id("dot")
            .description("Точка в центре прицела")
            .aliases("dot", "точка"))
         .build();
      this.registerSetting(this.dot);
      this.displayCooldown = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Display Cooldown")
            .id("display_cooldown")
            .description("Перезарядка атаки на прицеле"))
         .build();
      this.registerSetting(this.displayCooldown);
      this.length = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 8.0)
            .defaultValue(4.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Length")
            .id("length")
            .description("Длина линий прицела")
            .aliases("length", "длина"))
         .build();
      this.registerSetting(this.length);
      this.gap = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 12.0)
            .defaultValue(3.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Gap")
            .id("gap")
            .description("Расстояние между линиями прицела"))
         .build();
      this.registerSetting(this.gap);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.onTick());
      this.listen(HudRenderEvent.class, event -> this.onRender());
   }

   private void onTick() {
      if (!this.enabledSetting.isEnabled()) {
         this.hoverAnimation.setActive(false);
      } else {
         this.hoverAnimation.setActive(this.isHoveringLivingEntity());
      }
   }

   private void onRender() {
      if (this.enabledSetting.isEnabled() && !shouldHideCustomCrosshair()) {
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         if (player != null && client.field_1690 != null && client.field_1690.method_31044() == class_5498.field_26664) {
            float scale = (float)client.method_22683().method_4495();
            float centerX = (float)client.method_22683().method_4486() / 2.0F;
            float centerY = (float)client.method_22683().method_4502() / 2.0F;
            float lineLength = (float)this.length.getValue();
            float cooldownExpansion = 0.0F;
            if (this.displayCooldown.isEnabled()) {
               float tickDelta = client.method_61966().method_60637(true);
               float cooldown = player.method_7261(tickDelta);
               cooldownExpansion = class_3532.method_15374((double)(cooldown * cooldown * (float) Math.PI)) * 5.0F;
            }

            float lineGap = (float)this.gap.getValue() + cooldownExpansion;
            int rgb = this.color.getColor(0.0F);
            float hoverMix = this.hoverAnimation.getPrimaryProgress();
            if (hoverMix > 0.0F && this.rayTrace.isEnabled()) {
               rgb = ColorUtils.lerp(rgb, this.hoverColor.getColor(1.0F), (double)hoverMix);
            }

            int outline = ColorUtils.lightContrastColor;
            GuiDrawApi renderer = WexSideClient.getHudRenderer();
            Matrix4f matrix = new Matrix4f().scale(scale);
            renderer.begin();

            try {
               if (this.dot.isEnabled()) {
                  this.drawQuad(renderer, matrix, centerX - 1.0F, centerY - 1.0F, centerX + 1.0F, centerY + 1.0F, outline);
                  this.drawQuad(renderer, matrix, centerX - 0.5F, centerY - 0.5F, centerX + 0.5F, centerY + 0.5F, rgb);
               }

               if (!this.tLike.isEnabled()) {
                  this.drawLine(renderer, matrix, centerX, centerY - lineGap - lineLength, centerX, centerY - lineGap, rgb, outline);
               }

               this.drawLine(renderer, matrix, centerX, centerY + lineGap, centerX, centerY + lineGap + lineLength, rgb, outline);
               this.drawLine(renderer, matrix, centerX - lineGap - lineLength, centerY, centerX - lineGap, centerY, rgb, outline);
               this.drawLine(renderer, matrix, centerX + lineGap, centerY, centerX + lineGap + lineLength, centerY, rgb, outline);
            } finally {
               renderer.end();
            }
         }
      }
   }

   private boolean isHoveringLivingEntity() {
      class_239 hit = class_310.method_1551().field_1765;
      if (hit instanceof class_3966 entityHit) {
         class_1297 entity = entityHit.method_17782();
         return entity instanceof class_1309;
      } else {
         return false;
      }
   }

   private static boolean shouldHideCustomCrosshair() {
      return class_310.method_1551().field_1755 instanceof class_408;
   }

   public static boolean isEnabled3() {
      if (shouldHideCustomCrosshair()) {
         return true;
      } else {
         CrosshairModule module = instance;
         if (module != null && module.enabledSetting.isEnabled()) {
            class_310 client = class_310.method_1551();
            return client.field_1690 != null && client.field_1690.method_31044() == class_5498.field_26664;
         } else {
            return false;
         }
      }
   }

   private void drawQuad(GuiDrawApi renderer, Matrix4f matrix, float x1, float y1, float x2, float y2, int color) {
      renderer.fillRectangle(matrix, x1, y1, x2 - x1, y2 - y1, color);
   }

   private void drawLine(GuiDrawApi renderer, Matrix4f matrix, float x1, float y1, float x2, float y2, int color, int outline) {
      this.drawQuad(renderer, matrix, x1 - 0.5F, y1 - 0.5F, x2 + 0.5F, y2 + 0.5F, outline);
      this.drawQuad(renderer, matrix, x1, y1, x2, y2, color);
   }

   private void applyPalette(ColorSetting colorSetting) {
      colorSetting.setPrimaryColor(0, -11753627);
      colorSetting.setPrimaryColor(1, -1543135);
      colorSetting.setPrimaryColor(2, -9279489);
      colorSetting.setPrimaryColor(3, -46001);
      colorSetting.setPrimaryColor(4, -13218);
      colorSetting.setPrimaryColor(5, -10582785);
      colorSetting.setPrimaryColor(6, -2732032);
   }
}
