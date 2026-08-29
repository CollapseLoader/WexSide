package ru.wexside.module.hud;

import java.util.Objects;
import net.minecraft.class_1041;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_465;
import org.joml.Matrix3x2fStack;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.HandledScreenAccessor;
import ru.wexside.misc.NativeHandle;
import ru.wexside.misc.TransitionAnimation;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.Easing;

public final class AnimateModule extends Module implements ConfigSerializable {
   static volatile AnimateModule animateModule2;
   private final BooleanSetting enabledSetting;
   private final MultiSelectSetting targets;
   private final BooleanSetting bounce;
   private final NumberSetting duration;
   private final NumberSetting inputDuration;
   private final TransitionAnimation containerAnimation;
   private class_437 closingScreen;
   private class_437 openingScreen;
   private boolean chatOpen;
   private boolean renderingClosingScreen;
   private long tabAnimationStartMs;

   public AnimateModule(EventBus eventBus) {
      super(eventBus, "animate", "Animate", "Анимация открытия и закрытия экранов", ModuleCategory.valueOf("DISPLAY"));
      animateModule2 = this;
      this.containerAnimation = new TransitionAnimation(Easing.LINEAR);
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Анимирует экраны")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      MultiSelectSetting targetsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("Container", "Chat", "Tab")
            .selectAll(true)
            .optionListEnabled(false)
            .name("Targets")
            .id("targets")
            .description("Что анимировать"))
         .build();
      targetsSetting.setOptions(new String[]{"Container", "Chat", "Tab"});
      this.targets = targetsSetting;
      this.registerSetting(targetsSetting);
      this.bounce = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Bounce")
            .id("bounce")
            .description("Отскок")
            .aliases("bounce", "отскок")
            .visibleWhen(() -> this.targets.getSelectedOptions().contains("Container")))
         .build();
      this.registerSetting(this.bounce);
      this.duration = ((NumberSettingBuilder)NumberSetting.builder()
            .range(50.0, 300.0)
            .defaultValue(250.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .markers(50.0)
            .snapTo(50.0)
            .name("Duration")
            .id("duration")
            .description("Длительность анимации"))
         .build();
      this.registerSetting(this.duration);
      this.inputDuration = ((NumberSettingBuilder)NumberSetting.builder()
            .range(50.0, 300.0)
            .defaultValue(170.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .markers(50.0)
            .snapTo(10.0)
            .name("Input Duration")
            .id("input_duration")
            .description("Длительность анимации поля ввода чата")
            .aliases("input duration", "поле ввода")
            .visibleWhen(() -> this.targets.getSelectedOptions().contains("Chat")))
         .build();
      this.registerSetting(this.inputDuration);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.onTick());
   }

   private void onTick() {
      if (this.enabledSetting.isEnabled() && this.isTargetEnabled("Container")) {
         double step = 50.0 / Math.max(50.0, this.duration.getValue());
         class_310 client = class_310.method_1551();
         class_437 currentScreen = client.field_1755;
         if (currentScreen instanceof class_465) {
            if (this.openingScreen != currentScreen) {
               this.openingScreen = currentScreen;
               this.containerAnimation.reset();
            }

            this.closingScreen = null;
            this.containerAnimation.advance(true, step);
         } else if (currentScreen == null) {
            if (this.openingScreen != null) {
               this.closingScreen = this.openingScreen;
               this.openingScreen = null;
            }

            if (this.closingScreen != null) {
               this.containerAnimation.advance(false, step);
               if (this.containerAnimation.isAtStart()) {
                  this.closingScreen = null;
               }
            }
         } else {
            this.openingScreen = null;
            this.closingScreen = null;
         }
      } else {
         this.openingScreen = null;
         this.closingScreen = null;
         this.containerAnimation.finish();
      }
   }

   private boolean isTargetEnabled(String target) {
      return this.targets.getSelectedOptions().contains(target);
   }

   public static boolean compute2(class_437 screen) {
      AnimateModule module = animateModule2;
      if (module != null && module.enabledSetting.isEnabled()) {
         Objects.requireNonNull(module);
         if (!module.isTargetEnabled("Container")) {
            return false;
         } else {
            class_310 client = class_310.method_1551();
            if (client.field_1755 == screen && screen instanceof class_465) {
               if (module.openingScreen != screen) {
                  module.openingScreen = screen;
                  module.closingScreen = null;
                  module.containerAnimation.reset();
               }

               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public static float compute3(int height) {
      AnimateModule module = animateModule2;
      if (module != null && module.enabledSetting.isEnabled() && module.isTargetEnabled("Tab")) {
         float durationMs = (float)module.duration.getValue();
         if (durationMs <= 0.0F) {
            return 0.0F;
         } else {
            float progress = Math.min((float)(System.currentTimeMillis() - module.tabAnimationStartMs) / durationMs, 1.0F);
            float eased = Easing.EASE_OUT_CUBIC.apply(progress);
            return -(1.0F - eased) * (float)height * 0.4F;
         }
      } else {
         return 0.0F;
      }
   }

   public static float compute4(float width, NativeHandle chatHud) {
      if (!isEnabled2()) {
         return width;
      } else {
         float durationMs = getFloatType2();
         if (durationMs <= 0.0F) {
            return width;
         } else {
            float elapsed = (float)(System.currentTimeMillis() - chatHud.getLongType());
            float progress = Math.min(elapsed / durationMs, 1.0F);
            return Easing.LINEAR.apply(width * progress);
         }
      }
   }

   public static void onHudRender(class_332 context) {
      AnimateModule module = animateModule2;
      if (module != null && module.enabledSetting.isEnabled() && module.isTargetEnabled("Container")) {
         class_310 client = class_310.method_1551();
         if (module.closingScreen == null && module.openingScreen != null && client.field_1755 == null) {
            module.closingScreen = module.openingScreen;
            module.openingScreen = null;
         }

         class_437 screen = module.closingScreen;
         if (screen != null && client.field_1755 == null && screen instanceof class_465 handledScreen) {
            class_1041 window = client.method_22683();
            int mouseX = (int)client.field_1729.method_68879(window);
            int mouseY = (int)client.field_1729.method_68883(window);
            float tickDelta = client.method_61966().method_60637(true);
            float fade = compute5(module);
            if (fade > 0.0F) {
               int topColor = (int)(192.0F * fade) << 24 | 1052688;
               int bottomColor = (int)(208.0F * fade) << 24 | 1052688;
               context.method_25296(0, 0, handledScreen.field_22789, handledScreen.field_22790, topColor, bottomColor);
            }

            Matrix3x2fStack matrices = context.method_51448();
            matrices.pushMatrix();
            module.renderingClosingScreen = true;

            try {
               handle(matrices, (float)handledScreen.field_22789, (float)handledScreen.field_22790);
               ((HandledScreenAccessor)handledScreen).drawContainerBackground(context, tickDelta, mouseX, mouseY);
               handledScreen.method_25394(context, mouseX, mouseY, tickDelta);
            } catch (RuntimeException var15) {
               module.closingScreen = null;
            } finally {
               module.renderingClosingScreen = false;
               matrices.popMatrix();
            }
         }
      }
   }

   private boolean isAnimatingContainer() {
      if (!this.enabledSetting.isEnabled()) {
         return false;
      } else {
         class_310 client = class_310.method_1551();
         return this.openingScreen != null && client.field_1755 == this.openingScreen || this.renderingClosingScreen;
      }
   }

   public static float getFloatType() {
      AnimateModule module = animateModule2;
      return module == null ? 0.0F : (float)module.inputDuration.getValue();
   }

   public static void onBooleanType(boolean open) {
      AnimateModule module = animateModule2;
      if (module != null) {
         if (open && !module.chatOpen) {
            module.tabAnimationStartMs = System.currentTimeMillis();
         }

         module.chatOpen = open;
      }
   }

   public static void handle(Matrix3x2fStack matrices, float width, float height) {
      AnimateModule module = animateModule2;
      if (module != null) {
         float scale = module.getContainerScale();
         matrices.translate(width / 2.0F, height / 2.0F);
         matrices.scale(scale, scale);
         matrices.translate(-width / 2.0F, -height / 2.0F);
      }
   }

   public static float getFloatType2() {
      AnimateModule module = animateModule2;
      return module == null ? 0.0F : (float)module.duration.getValue();
   }

   private static float compute5(AnimateModule module) {
      float progress = module.containerAnimation.getPrimaryProgress();
      return Math.max(0.0F, Math.min(1.0F, progress));
   }

   public static int compute6(int x) {
      AnimateModule module = animateModule2;
      if (module != null && module.isAnimatingContainer()) {
         float center = (float)class_310.method_1551().method_22683().method_4486() / 2.0F;
         float scale = Math.max(0.001F, module.getContainerScale());
         return Math.round(center + ((float)x - center) / scale);
      } else {
         return x;
      }
   }

   public static int compute7(int y) {
      AnimateModule module = animateModule2;
      if (module != null && module.isAnimatingContainer()) {
         float center = (float)class_310.method_1551().method_22683().method_4502() / 2.0F;
         float scale = Math.max(0.001F, module.getContainerScale());
         return Math.round(center + ((float)y - center) / scale);
      } else {
         return y;
      }
   }

   private float getContainerScale() {
      float progress = this.containerAnimation.getPrimaryProgress();
      if (this.closingScreen != null) {
         return 1.0F - Easing.EASE_OUT_CUBIC.apply(1.0F - progress);
      } else {
         return this.bounce.isEnabled() ? Easing.EASE_OUT_BACK.apply(progress) : Easing.EASE_OUT_CUBIC.apply(progress);
      }
   }

   public static boolean isEnabled2() {
      AnimateModule module = animateModule2;
      if (module != null && module.enabledSetting.isEnabled()) {
         Objects.requireNonNull(module);
         return module.isTargetEnabled("Chat");
      } else {
         return false;
      }
   }

   public static float getFloatType4() {
      AnimateModule module = animateModule2;
      return module != null && module.isAnimatingContainer() ? module.getContainerScale() : 1.0F;
   }
}
