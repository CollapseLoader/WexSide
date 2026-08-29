package ru.wexside.module.render;

import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1753;
import net.minecraft.class_1764;
import net.minecraft.class_1792;
import net.minecraft.class_1835;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.FirstPersonItemTransformEvent;
import ru.wexside.event.FirstPersonSwingTransformEvent;
import ru.wexside.event.HandSwingSpeedEvent;
import ru.wexside.misc.HandRenderTransforms;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.module.combat.AttackAuraModule;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class HandModifyModule extends Module implements ConfigSerializable {
   private static volatile HandModifyModule instance;
   private final BooleanSetting enabledSetting;
   private final BooleanSetting positionHand;
   private final NumberSetting mainHandX;
   private final NumberSetting mainHandY;
   private final NumberSetting mainHandZ;
   private final NumberSetting offHandX;
   private final NumberSetting offHandY;
   private final NumberSetting offHandZ;
   private final BooleanSetting animationHand;
   private final ModeSetting swingType;
   private final BooleanSetting rotate;
   private final NumberSetting rotateSpeed;
   private final BooleanSetting attackAuraOnly;
   private final BooleanSetting staticPosition;
   private long rotateStartNanos;
   private AttackAuraModule attackAuraModule;

   public HandModifyModule(EventBus eventBus) {
      super(eventBus, "hand_modify", "Hand Modify", "Смещение позиции руки и кастомная анимация маха", ModuleCategory.valueOf("RENDER"));
      instance = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Включить модификацию руки")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      this.positionHand = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Position hand")
            .id("position_hand")
            .description("Смещать позицию руки"))
         .build();
      this.registerSetting(this.positionHand);
      this.mainHandX = this.offsetSetting("main_hand_x", "Main hand X");
      this.mainHandY = this.offsetSetting("main_hand_y", "Main hand Y");
      this.mainHandZ = this.offsetSetting("main_hand_z", "Main hand Z");
      this.offHandX = this.offsetSetting("off_hand_x", "Off hand X");
      this.offHandY = this.offsetSetting("off_hand_y", "Off hand Y");
      this.offHandZ = this.offsetSetting("off_hand_z", "Off hand Z");
      this.animationHand = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Animation hand")
            .id("animation_hand")
            .description("Кастомная анимация"))
         .build();
      this.registerSetting(this.animationHand);
      this.swingType = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Smooth", "Mode 2", "Mode 3", "Mode 4", "Mode 5", "Mode 6", "Mode 7", "Vanilla", "None")
            .defaultOption("Smooth")
            .name("Swing type")
            .id("swing_type")
            .description("Тип анимации")
            .visibleWhen(this.animationHand::isEnabled))
         .build();
      this.registerSetting(this.swingType);
      this.rotate = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Rotate")
            .id("rotate")
            .description("Вращать предмет в руке при ударе")
            .visibleWhen(this.animationHand::isEnabled))
         .build();
      this.registerSetting(this.rotate);
      this.rotateSpeed = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 5.0)
            .defaultValue(1.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Rotate speed")
            .id("rotate_speed")
            .description("Скорость вращения")
            .visibleWhen(() -> this.animationHand.isEnabled() && this.rotate.isEnabled()))
         .build();
      this.registerSetting(this.rotateSpeed);
      this.attackAuraOnly = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Attack aura only")
            .id("attack_aura_only")
            .description("Анимация только при включённой Attack Aura")
            .visibleWhen(this.animationHand::isEnabled))
         .build();
      this.registerSetting(this.attackAuraOnly);
      this.staticPosition = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Static hand position")
            .id("static_position")
            .description("Статичное положение руки"))
         .build();
      this.registerSetting(this.staticPosition);
   }

   @Override
   protected void initialize() {
      this.listen(FirstPersonItemTransformEvent.class, this::onHandPosition);
      this.listen(HandSwingSpeedEvent.class, this::onSwingSpeed);
      this.listen(FirstPersonSwingTransformEvent.class, this::onSwingAnimation);
   }

   public static boolean isStaticHandPositionEnabled() {
      HandModifyModule module = instance;
      return module != null && module.enabledSetting.isEnabled() && module.staticPosition.isEnabled();
   }

   private void onHandPosition(FirstPersonItemTransformEvent event) {
      if (this.enabledSetting.isEnabled() && this.positionHand.isEnabled()) {
         class_1268 hand = event.getHand();
         if (hand != class_1268.field_5808 || !(event.getStack().method_7909() instanceof class_1764)) {
            class_746 player = class_310.method_1551().field_1724;
            int side = sideMultiplier(player);
            Object matrices = event.getMatrices();
            if (hand == class_1268.field_5808) {
               this.applyOffset(matrices, side, this.mainHandX, this.mainHandY, this.mainHandZ);
            } else {
               this.applyOffset(matrices, side, this.offHandX, this.offHandY, this.offHandZ);
            }
         }
      }
   }

   private void onSwingAnimation(FirstPersonSwingTransformEvent event) {
      if (this.enabledSetting.isEnabled() && this.animationHand.isEnabled()) {
         if (event.getHand() == class_1268.field_5808) {
            String mode = this.swingType.getSelectedOption();
            if (mode != null && !"Vanilla".equals(mode)) {
               if (!this.attackAuraOnly.isEnabled() || this.isAttackAuraActive()) {
                  class_746 player = class_310.method_1551().field_1724;
                  if (player != null) {
                     class_1792 heldItem = player.method_6047().method_7909();
                     if (!(heldItem instanceof class_1764) && !(heldItem instanceof class_1753) && !(heldItem instanceof class_1835)) {
                        Object matrices = event.getMatrices();
                        float swingProgress = event.getSwingProgress();
                        float equipProgress = event.getEquipProgress();
                        int side = sideMultiplier(player);
                        float swingSin = class_3532.method_15374((double)(swingProgress * (float) Math.PI));
                        float sqrtSwing = class_3532.method_15374((double)(class_3532.method_15355(swingProgress) * (float) Math.PI));
                        switch(mode) {
                           case "None":
                              HandRenderTransforms.translateLocal(matrices, (float)side * 0.56F, -0.52F + equipProgress * -0.6F, -0.72F);
                              break;
                           case "Mode 2":
                              this.applyMode2(matrices, side, swingSin);
                              break;
                           case "Mode 3":
                              this.applyMode3(matrices, side, swingSin);
                              break;
                           case "Mode 4":
                              this.applyMode4(matrices, side, sqrtSwing);
                              break;
                           case "Mode 5":
                              this.applyMode5(matrices, side, sqrtSwing);
                              break;
                           case "Mode 6":
                              this.applyMode6(matrices, side, swingSin);
                              break;
                           case "Mode 7":
                              this.applyMode7(matrices, side, sqrtSwing);
                              break;
                           default:
                              this.applySmooth(matrices, side, swingProgress, equipProgress, sqrtSwing);
                        }

                        if (this.rotate.isEnabled()) {
                           this.applySpin(matrices, swingProgress);
                        }

                        event.update();
                     }
                  }
               }
            }
         }
      }
   }

   private void onSwingSpeed(HandSwingSpeedEvent event) {
      if (this.enabledSetting.isEnabled() && this.animationHand.isEnabled()) {
         event.setSpeedMultiplier(1.0F);
         event.update();
      }
   }

   private void applyOffset(Object matrices, int side, NumberSetting x, NumberSetting y, NumberSetting z) {
      HandRenderTransforms.translate(matrices, (double)side * x.getValue(), y.getValue(), z.getValue());
   }

   private void applyMode2(Object matrices, int side, float swingSin) {
      HandRenderTransforms.translateLocal(matrices, (float)side * 0.56F, -0.35F, -0.72F);
      HandRenderTransforms.scale(matrices, 0.8F, 0.8F, 0.8F);
      HandRenderTransforms.rotateX(matrices, (float)side * 80.0F);
      HandRenderTransforms.rotateY(matrices, (float)side * -57.0F);
      HandRenderTransforms.rotateZ(matrices, -80.0F - 70.0F * swingSin);
   }

   private void applyMode3(Object matrices, int side, float swingSin) {
      HandRenderTransforms.translateLocal(matrices, (float)side * 0.56F, -0.35F, -0.72F);
      HandRenderTransforms.scale(matrices, 0.8F, 0.8F, 0.8F);
      HandRenderTransforms.rotateX(matrices, (float)side * 80.0F);
      HandRenderTransforms.rotateY(matrices, (float)side * -35.0F);
      HandRenderTransforms.rotateZ(matrices, -70.0F - 70.0F * swingSin);
   }

   private void applyMode4(Object matrices, int side, float sqrtSwing) {
      HandRenderTransforms.translateLocal(matrices, (float)side * 0.56F, -0.4F, -0.72F);
      HandRenderTransforms.scale(matrices, 0.8F, 0.8F, 0.8F);
      HandRenderTransforms.rotateZ(matrices, 50.0F);
      HandRenderTransforms.rotateX(matrices, (float)side * -60.0F);
      HandRenderTransforms.rotateY(matrices, (float)side * (110.0F + 25.0F * sqrtSwing));
   }

   private void applyMode5(Object matrices, int side, float sqrtSwing) {
      HandRenderTransforms.translateLocal(matrices, (float)side * 0.56F, -0.52F, -0.72F);
      HandRenderTransforms.rotateZ(matrices, -60.0F * sqrtSwing);
      HandRenderTransforms.rotateYNegative(matrices, (float)side * -25.0F * sqrtSwing);
      HandRenderTransforms.rotateYNegativeRadians(matrices, (float)side * -0.2F * sqrtSwing);
   }

   private void applyMode6(Object matrices, int side, float swingSin) {
      HandRenderTransforms.translateLocal(matrices, (float)side * 0.56F, -0.35F, -0.72F);
      HandRenderTransforms.scale(matrices, 0.8F, 0.8F, 0.8F);
      HandRenderTransforms.rotateX(matrices, (float)side * 25.0F);
      HandRenderTransforms.rotateY(matrices, (float)side * -15.0F);
      HandRenderTransforms.rotateZ(matrices, -70.0F - 100.0F * swingSin);
   }

   private void applyMode7(Object matrices, int side, float sqrtSwing) {
      HandRenderTransforms.translateLocal(matrices, (float)side * 0.56F, -0.35F, -0.72F);
      HandRenderTransforms.scale(matrices, 0.8F, 0.8F, 0.8F);
      HandRenderTransforms.rotateX(matrices, (float)side * 80.0F);
      HandRenderTransforms.rotateY(matrices, (float)side * -15.0F);
      HandRenderTransforms.rotateZ(matrices, -100.0F - 70.0F * sqrtSwing);
   }

   private void applySmooth(Object matrices, int side, float swingProgress, float equipProgress, float sqrtSwing) {
      HandRenderTransforms.translateLocal(matrices, (float)side * 0.56F, -0.52F + equipProgress * -0.25F, -0.72F);
      HandRenderTransforms.rotateX(
         matrices, (float)side * (45.0F + class_3532.method_15374((double)(swingProgress * swingProgress * (float) Math.PI)) * -20.0F)
      );
      HandRenderTransforms.rotateY(matrices, (float)side * sqrtSwing * -20.0F);
      HandRenderTransforms.rotateZ(matrices, sqrtSwing * -80.0F);
      HandRenderTransforms.rotateX(matrices, (float)side * -45.0F);
   }

   private void applySpin(Object matrices, float swingProgress) {
      if (swingProgress > 0.0F) {
         long elapsedMs = (System.nanoTime() - this.rotateStartNanos) / 1000000L;
         int spinDegrees = (int)((double)elapsedMs * 2.4 / (double)this.rotateSpeed.getIntValue() % 720.0);
         if (spinDegrees >= 360) {
            spinDegrees -= 720;
         }

         HandRenderTransforms.rotateZSpin(matrices, (float)spinDegrees);
      } else {
         this.rotateStartNanos = System.nanoTime();
      }
   }

   private boolean isAttackAuraActive() {
      AttackAuraModule aura = this.attackAuraModule;
      if (aura == null) {
         if (WexSideClient.getInstance() == null || WexSideClient.getInstance().getModuleManager() == null) {
            return false;
         }

         this.attackAuraModule = aura = WexSideClient.getInstance().getModuleManager().getModule(AttackAuraModule.class);
      }

      return aura != null && aura.isActive();
   }

   private static int sideMultiplier(class_746 player) {
      class_1306 mainArm = player != null ? player.method_6068() : class_1306.field_6183;
      return mainArm == class_1306.field_6183 ? 1 : -1;
   }

   private NumberSetting offsetSetting(String id, String name) {
      NumberSetting setting = ((NumberSettingBuilder)NumberSetting.builder()
            .range(-1.0, 1.0)
            .defaultValue(0.0)
            .multiplier(1.0)
            .precision(2)
            .animationSpeed(20.0F)
            .name(name)
            .id(id)
            .description("Смещение руки по оси")
            .visibleWhen(this.positionHand::isEnabled))
         .build();
      this.registerSetting(setting);
      return setting;
   }
}
