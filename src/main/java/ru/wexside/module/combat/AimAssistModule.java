package ru.wexside.module.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1294;
import net.minecraft.class_1309;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_408;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.AttackWindowEvent;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.ReachHelper;
import ru.wexside.misc.TargetFilter;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.Angle;
import ru.wexside.util.TargetSelector;

public class AimAssistModule extends Module implements ConfigSerializable {
   private static final String MODE_ASSIST = "Доводка";
   private static final String MODE_HELPER = "Помощник";
   private static final String MODE_CAPTURE = "Захват";
   private static final String MODE_DIRECT = "Прямой";
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("Доводит камеру до цели")
         .withKeybind()
         .toggle())
      .build();
   private final ModeSetting mode;
   private final BooleanSetting magnet;
   private final NumberSetting magnetStrength;
   private final NumberSetting yawSpeed;
   private final NumberSetting pitchSpeed;
   private final NumberSetting captureSpeedNear;
   private final NumberSetting captureSpeedFar;
   private final NumberSetting captureZone;
   private final NumberSetting shakeStrength;
   private final NumberSetting aimHeight;
   private final NumberSetting attackRange;
   private final NumberSetting rotationRange;
   private final NumberSetting fov;
   private final MultiSelectSetting targets;
   private final ModeSetting sorting;
   private final BooleanSetting throughWalls;
   private final BooleanSetting focusOne;
   private final BooleanSetting noGui;
   private final BooleanSetting inInventory;
   private TargetSelector targetSelector;
   private class_1309 target;
   private class_243 smoothedDelta;
   private int onTargetHold;
   private int trackedId = -1;
   private int shakeTargetId = -1;
   private int wanderTargetId = -1;
   private long lastFrameNanos;
   private long nextShakeNanos;
   private boolean applied;
   private float lastYaw;
   private float lastPitch;
   private float mouseYaw;
   private float mousePitch;
   private float mouseActivity;
   private float yawQuantError;
   private float pitchQuantError;
   private float captureYawStep;
   private float capturePitchStep;
   private float assistGain = 0.36F;
   private float onTargetScale = 1.0F;
   private float magnetLock;
   private float magnetAmount;
   private float wanderX;
   private float wanderZ;
   private float shakeYaw;
   private float shakePitch;

   public AimAssistModule(EventBus eventBus) {
      super(eventBus, "aim_assist", "Aim Assist", "Помощник прицеливания", ModuleCategory.valueOf("COMBAT"));
      this.registerSetting(this.enabledSetting);
      this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Доводка", "Помощник", "Захват", "Прямой")
            .defaultOption("Доводка")
            .name("Режим")
            .id("mode")
            .description("Как именно тянуть камеру")
            .withKeybind())
         .build();
      this.registerSetting(this.mode);
      this.magnet = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Магнит")
            .id("magnet")
            .description("Тянет только пока ты сам ведёшь мышкой и не больше, чем на твой же сдвиг")
            .aliases("magnet", "магнит")
            .visibleWhen(() -> "Помощник".equals(this.mode.getSelectedOption())))
         .build();
      this.registerSetting(this.magnet);
      this.magnetStrength = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.1, 2.5)
            .defaultValue(0.9999999999999999)
            .multiplier(1.0)
            .precision(2)
            .animationSpeed(20.0F)
            .name("Сила магнита")
            .id("magnet_strength")
            .description("Во сколько раз магнит может усилить твой собственный сдвиг")
            .aliases("magnet strength", "сила магнита")
            .visibleWhen(() -> "Помощник".equals(this.mode.getSelectedOption()) && this.magnet.isEnabled()))
         .build();
      this.registerSetting(this.magnetStrength);
      this.yawSpeed = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 180.0)
            .defaultValue(60.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Скорость по горизонтали")
            .id("yaw_speed")
            .description("Предел довода по yaw"))
         .build();
      this.registerSetting(this.yawSpeed);
      this.pitchSpeed = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 180.0)
            .defaultValue(30.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Скорость по вертикали")
            .id("pitch_speed")
            .description("Предел довода по pitch"))
         .build();
      this.registerSetting(this.pitchSpeed);
      this.captureSpeedNear = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 180.0)
            .defaultValue(25.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Скорость вблизи прицела")
            .id("capture_speed_near")
            .description("Скорость, когда цель почти под прицелом")
            .visibleWhen(() -> "Захват".equals(this.mode.getSelectedOption())))
         .build();
      this.registerSetting(this.captureSpeedNear);
      this.captureSpeedFar = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 180.0)
            .defaultValue(90.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Скорость вдали от прицела")
            .id("capture_speed_far")
            .description("Скорость, когда цель далеко от прицела")
            .visibleWhen(() -> "Захват".equals(this.mode.getSelectedOption())))
         .build();
      this.registerSetting(this.captureSpeedFar);
      this.captureZone = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.5, 90.0)
            .defaultValue(20.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Зона перехода")
            .id("capture_zone")
            .description("Угол, на котором скорость переходит от дальней к ближней")
            .visibleWhen(() -> "Захват".equals(this.mode.getSelectedOption())))
         .build();
      this.registerSetting(this.captureZone);
      this.shakeStrength = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 10.0)
            .defaultValue(3.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Дрожание")
            .id("shake_strength")
            .description("Живость прицела: блуждание точки, случайные смещения и ослабление довода на цели"))
         .build();
      this.registerSetting(this.shakeStrength);
      this.aimHeight = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.18, 0.92)
            .defaultValue(0.6)
            .multiplier(1.0)
            .precision(2)
            .animationSpeed(20.0F)
            .name("Высота точки")
            .id("aim_height")
            .description("Доля хитбокса снизу вверх, в которую целиться")
            .aliases("height", "высота"))
         .build();
      this.registerSetting(this.aimHeight);
      this.attackRange = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 6.0)
            .defaultValue(3.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Дистанция удара")
            .id("attack_range")
            .description("Нижняя граница луча проверки прицела"))
         .build();
      this.registerSetting(this.attackRange);
      this.rotationRange = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 12.0)
            .defaultValue(5.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Дистанция наведения")
            .id("rotation_range")
            .description("Дальность поиска цели и верхняя граница луча проверки"))
         .build();
      this.registerSetting(this.rotationRange);
      this.fov = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 360.0)
            .defaultValue(120.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("FOV")
            .id("fov")
            .description("Сектор поиска цели"))
         .build();
      this.registerSetting(this.fov);
      MultiSelectSetting targetsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("Players", "Friends", "Bots", "Naked", "Invisibles", "Animals", "Mobs", "Villagers")
            .selectAll(false)
            .optionListEnabled(false)
            .name("Цели")
            .id("targets")
            .description("Типы целей для наведения"))
         .build();
      this.targets = targetsSetting;
      this.registerSetting(targetsSetting);
      this.sorting = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Health", "Distance", "Crosshair")
            .defaultOption("Distance")
            .name("Сортировка")
            .id("sorting")
            .description("Как выбирать цель из нескольких"))
         .build();
      this.registerSetting(this.sorting);
      this.throughWalls = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Сквозь стены")
            .id("through_walls")
            .description("Наводиться на цель за блоками")
            .aliases("walls", "стены"))
         .build();
      this.registerSetting(this.throughWalls);
      this.focusOne = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Фокус на одном")
            .id("focus_one")
            .description("Держать одну цель, пока она валидна, вместо перевыбора каждый тик")
            .aliases("focus", "фокус"))
         .build();
      this.registerSetting(this.focusOne);
      this.noGui = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Не водить в GUI")
            .id("no_gui")
            .description("Отпускать камеру, пока открыт экран. Чат экраном не считается"))
         .build();
      this.registerSetting(this.noGui);
      this.inInventory = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Водить в инвентаре")
            .id("in_inventory")
            .description("Исключение для инвентаря и сундуков")
            .aliases("inventory", "инвентарь")
            .visibleWhen(this.noGui::isEnabled))
         .build();
      this.registerSetting(this.inInventory);
   }

   @Override
   protected void initialize() {
      this.targetSelector = new TargetSelector();
      this.listen(ClientTickEvent.class, event -> this.selectTarget());
      this.listen(AttackWindowEvent.class, this::onAttackWindow);
   }

   private void rollShake(class_1309 entity, boolean captured) {
      long now = System.nanoTime();
      if (captured || this.shakeTargetId != entity.method_5628() || now >= this.nextShakeNanos) {
         this.shakeTargetId = entity.method_5628();
         float strength = class_3532.method_15363(this.shakeStrength.getFloatValue() / 10.0F, 0.0F, 1.0F);
         float yawRange = 0.012F + strength * 0.03F;
         float pitchRange = 0.008F + strength * 0.024F;
         this.shakeYaw = ThreadLocalRandom.current().nextFloat(-yawRange, yawRange);
         this.shakePitch = ThreadLocalRandom.current().nextFloat(-pitchRange, pitchRange);
         this.nextShakeNanos = now + ThreadLocalRandom.current().nextLong(340000000L, 760000000L);
      }
   }

   private static class_243 predictClimb(class_1309 entity, class_243 motion) {
      class_243 look = entity.method_5720();
      float pitchRad = entity.method_36455() * (float) (Math.PI / 180.0);
      double horizontalLook = Math.sqrt(look.field_1352 * look.field_1352 + look.field_1350 * look.field_1350);
      double motionLength = motion.method_37267();
      double gravity = effectiveGravity(entity);
      double pitchCosSq = class_3532.method_33723(Math.cos((double)pitchRad));
      motion = motion.method_1031(0.0, gravity * (-1.0 + pitchCosSq * 0.75), 0.0);
      if (motion.field_1351 < 0.0 && horizontalLook > 0.0) {
         double lift = motion.field_1351 * -0.1 * pitchCosSq;
         motion = motion.method_1031(look.field_1352 * lift / horizontalLook, lift, look.field_1350 * lift / horizontalLook);
      }

      if (pitchRad < 0.0F && horizontalLook > 0.0) {
         double boost = motionLength * (double)(-class_3532.method_15374((double)pitchRad)) * 0.04;
         motion = motion.method_1031(-look.field_1352 * boost / horizontalLook, boost * 3.2, -look.field_1350 * boost / horizontalLook);
      }

      if (horizontalLook > 0.0) {
         motion = motion.method_1031(
            (look.field_1352 / horizontalLook * motionLength - motion.field_1352) * 0.1,
            0.0,
            (look.field_1350 / horizontalLook * motionLength - motion.field_1350) * 0.1
         );
      }

      return motion.method_18805(0.99, 0.98, 0.99);
   }

   private boolean blockedByScreen() {
      if (!this.noGui.isEnabled()) {
         return false;
      } else {
         class_437 screen = class_310.method_1551().field_1755;
         if (screen != null && !(screen instanceof class_408)) {
            return !this.inInventory.isEnabled() || !(screen instanceof class_465);
         } else {
            return false;
         }
      }
   }

   private void onAttackWindow(AttackWindowEvent event) {
      if (event.isPre() || event.isPost()) {
         class_746 player = class_310.method_1551().field_1724;
         class_1309 target = this.target;
         if (this.enabledSetting.isEnabled() && player != null && player.method_5805() && target != null && !this.blockedByScreen()) {
            float dt = this.frameDelta();
            float yaw = player.method_36454();
            float pitch = player.method_36455();
            this.updateWander(dt, target);
            boolean captured = this.captureTarget(target.method_5628());
            float mouseWeight = this.updateMouse(yaw, pitch, dt);
            String mode = this.mode.getSelectedOption();
            boolean assist = "Доводка".equals(mode);
            boolean helper = "Помощник".equals(mode);
            boolean capture = "Захват".equals(mode);
            boolean smooth = assist || helper;
            this.rollShake(target, captured);
            class_243 aimPoint = this.aimPoint(player, target);
            if (target.method_6101()) {
               aimPoint = aimPoint.method_1019(predictClimb(target, target.method_18798()));
            }

            class_243 eye = player.method_33571();
            class_243 delta = aimPoint.method_1020(eye).method_1031((double)this.wanderX, 0.0, (double)this.wanderZ);
            class_243 look = smooth ? this.smoothDelta(delta, dt, captured) : delta;
            if (!smooth) {
               this.smoothedDelta = null;
               this.onTargetHold = 0;
               this.onTargetScale = 1.0F;
            }

            Angle angle = Angle.fromDelta(look);
            float targetYaw = angle.getYaw();
            class_238 box = target.method_5829();
            double horizontal = Math.sqrt(look.field_1352 * look.field_1352 + look.field_1350 * look.field_1350);
            float maxPitch = (float)(-Math.toDegrees(Math.atan2(box.field_1325 - eye.field_1351 - 0.2, horizontal)));
            float minPitch = (float)(-Math.toDegrees(Math.atan2(box.field_1322 - eye.field_1351 - 0.2, horizontal)));
            float targetPitch = class_3532.method_15363(angle.getPitch(), maxPitch, minPitch);
            float yawDelta = class_3532.method_15393(targetYaw - yaw);
            float pitchDelta = class_3532.method_15393(targetPitch - pitch);
            float yawCap = Math.max(1.0F, this.yawSpeed.getFloatValue()) * 2.8F;
            float pitchCap = Math.max(1.0F, this.pitchSpeed.getFloatValue()) * 2.2F;
            float yawFactor = expApproach(dt, 2.8F, class_3532.method_15363(this.yawSpeed.getFloatValue() / 60.0F, 0.0F, 4.2F) * 4.4F);
            float pitchFactor = expApproach(dt, 2.4F, class_3532.method_15363(this.pitchSpeed.getFloatValue() / 30.0F, 0.0F, 4.4F) * 3.8F);
            float yawStep = class_3532.method_15363(yawDelta * yawFactor, -yawCap * dt, yawCap * dt);
            float pitchStep = class_3532.method_15363(pitchDelta * pitchFactor, -pitchCap * dt, pitchCap * dt);
            float nextPitch;
            float nextYaw;
            if (helper && this.magnet.isEnabled()) {
               boolean onTarget = this.lookingAt(player, target, yaw, pitch);
               float[] magnet = this.magnetAim(yaw, pitch, yawDelta, pitchDelta, dt, captured, onTarget);
               nextYaw = magnet[0];
               nextPitch = magnet[1];
            } else if (capture) {
               if (captured) {
                  this.captureYawStep = 0.0F;
                  this.capturePitchStep = 0.0F;
               }

               float error = Math.abs(yawDelta) + Math.abs(pitchDelta) * 0.7F;
               float zone = Math.max(0.5F, this.captureZone.getFloatValue());
               float t = class_3532.method_15363(error / zone, 0.0F, 1.0F);
               float speed = class_3532.method_16439(
                  t, Math.max(1.0F, this.captureSpeedNear.getFloatValue()), Math.max(1.0F, this.captureSpeedFar.getFloatValue())
               );
               float capYaw = speed * 2.8F;
               float capPitch = speed * 2.2F;
               float captureYawFactor = expApproach(dt, 2.8F, class_3532.method_15363(speed / 60.0F, 0.0F, 4.2F) * 4.4F);
               float capturePitchFactor = expApproach(dt, 2.4F, class_3532.method_15363(speed / 30.0F, 0.0F, 4.4F) * 3.8F);
               float stepYaw = class_3532.method_15363(yawDelta * captureYawFactor, -capYaw * dt, capYaw * dt);
               float stepPitch = class_3532.method_15363(pitchDelta * capturePitchFactor, -capPitch * dt, capPitch * dt);
               stepYaw = stepYaw * 0.7F + this.captureYawStep * 0.3F;
               stepPitch = stepPitch * 0.7F + this.capturePitchStep * 0.3F;
               this.captureYawStep = stepYaw;
               this.capturePitchStep = stepPitch;
               nextYaw = yaw + stepYaw;
               nextPitch = pitch + stepPitch;
            } else if (assist) {
               float gain = this.assistGain(yawDelta, pitchDelta, mouseWeight);
               this.assistGain = captured
                  ? gain
                  : class_3532.method_16439(class_3532.method_15363(expApproach(dt, 0.0F, 16.0F), 0.12F, 0.5F), this.assistGain, gain);
               boolean onTarget = this.lookingAt(player, target, yaw, pitch);
               if (onTarget) {
                  this.onTargetHold = 5;
               } else if (this.onTargetHold > 0) {
                  --this.onTargetHold;
               }

               float scale = !onTarget && this.onTargetHold <= 0 ? 1.0F : this.onTargetShakeScale();
               this.onTargetScale = captured
                  ? scale
                  : class_3532.method_16439(class_3532.method_15363(expApproach(dt, 0.0F, 8.0F), 0.04F, 0.22F), this.onTargetScale, scale);
               nextYaw = yaw + yawStep * this.onTargetScale * this.assistGain;
               nextPitch = pitch + pitchStep * this.onTargetScale * this.assistGain;
            } else {
               nextYaw = yaw + yawStep;
               nextPitch = pitch + pitchStep;
            }

            double frame = frameTime();
            nextYaw = this.quantize(yaw, nextYaw, frame, true);
            nextPitch = class_3532.method_15363(this.quantize(pitch, class_3532.method_15363(nextPitch, -90.0F, 90.0F), frame, false), -90.0F, 90.0F);
            player.method_36456(nextYaw);
            player.method_36457(nextPitch);
            this.lastYaw = nextYaw;
            this.lastPitch = nextPitch;
            this.applied = true;
         } else {
            this.lastFrameNanos = 0L;
            this.resetAssist();
         }
      }
   }

   private float updateMouse(float yaw, float pitch, float dt) {
      if (!this.applied) {
         this.mouseYaw = 0.0F;
         this.mousePitch = 0.0F;
         this.mouseActivity = 0.0F;
         return 0.0F;
      } else {
         this.mouseYaw = class_3532.method_15393(yaw - this.lastYaw);
         this.mousePitch = class_3532.method_15363(pitch - this.lastPitch, -45.0F, 45.0F);
         float deadzone = (float)Math.max(0.004, frameTime() * 0.08);
         float motion = Math.abs(this.mouseYaw) + Math.abs(this.mousePitch) * 1.35F;
         float active = class_3532.method_15363((motion - deadzone) / 0.62F, 0.0F, 1.0F);
         this.mouseActivity = class_3532.method_16439(class_3532.method_15363(expApproach(dt, 0.0F, 18.0F), 0.16F, 0.48F), this.mouseActivity, active);
         return this.mouseActivity;
      }
   }

   private class_243 smoothDelta(class_243 delta, float dt, boolean captured) {
      if (!captured && this.smoothedDelta != null) {
         float t = class_3532.method_15363(expApproach(dt, 0.0F, 13.0F), 0.06F, 0.28F);
         this.smoothedDelta = this.smoothedDelta.method_1019(delta.method_1020(this.smoothedDelta).method_1021((double)t));
         return this.smoothedDelta;
      } else {
         this.smoothedDelta = delta;
         return delta;
      }
   }

   private float frameDelta() {
      long now = System.nanoTime();
      if (this.lastFrameNanos == 0L) {
         this.lastFrameNanos = now;
         return 0.016666668F;
      } else {
         float dt = (float)(now - this.lastFrameNanos) / 1.0E9F;
         this.lastFrameNanos = now;
         return class_3532.method_15363(dt, 0.004166667F, 0.033333335F);
      }
   }

   private void resetAssist() {
      this.wanderTargetId = -1;
      this.wanderX = 0.0F;
      this.wanderZ = 0.0F;
      this.trackedId = -1;
      this.onTargetHold = 0;
      this.onTargetScale = 1.0F;
      this.assistGain = 0.36F;
      this.captureYawStep = 0.0F;
      this.capturePitchStep = 0.0F;
      this.smoothedDelta = null;
      this.shakeTargetId = -1;
      this.nextShakeNanos = 0L;
      this.shakeYaw = 0.0F;
      this.shakePitch = 0.0F;
      this.magnetLock = 0.0F;
      this.magnetAmount = 0.0F;
      this.lastYaw = 0.0F;
      this.lastPitch = 0.0F;
      this.mouseYaw = 0.0F;
      this.mousePitch = 0.0F;
      this.mouseActivity = 0.0F;
      this.applied = false;
      this.yawQuantError = 0.0F;
      this.pitchQuantError = 0.0F;
   }

   private float onTargetShakeScale() {
      return class_3532.method_15363(class_3532.method_15363(this.shakeStrength.getFloatValue(), 0.0F, 10.0F) / 10.0F * 0.22F, 0.0F, 0.22F);
   }

   private static float expApproach(float dt, float base, float extra) {
      return 1.0F - (float)Math.exp((double)(-dt * (base + extra)));
   }

   private float quantize(float from, float to, double step, boolean yawAxis) {
      if (step <= 0.0) {
         return to;
      } else {
         float error = yawAxis ? this.yawQuantError : this.pitchQuantError;
         float delta = to - from + error;
         float snapped = (float)((double)Math.round((double)delta / step) * step);
         float leftover = class_3532.method_15363(delta - snapped, (float)(-step), (float)step);
         if (yawAxis) {
            this.yawQuantError = leftover;
         } else {
            this.pitchQuantError = leftover;
         }

         return from + snapped;
      }
   }

   private float assistGain(float yawDelta, float pitchDelta, float mouseWeight) {
      float mouse = Math.abs(this.mouseYaw) + Math.abs(this.mousePitch) * 1.35F;
      if (mouse <= 0.002F) {
         return 0.36F;
      } else {
         float aligned = this.mouseYaw * yawDelta + this.mousePitch * pitchDelta * 0.72F;
         float step = Math.max(0.001F, Math.abs(yawDelta) + Math.abs(pitchDelta) * 0.72F);
         float match = class_3532.method_15363(aligned / Math.max(0.001F, mouse * step), -1.0F, 1.0F);
         float scale = match > 0.0F ? class_3532.method_16439(match, 0.62F, 1.0F) : 0.3F;
         return class_3532.method_15363((0.42F + mouseWeight * 0.86F) * scale, 0.24F, 1.22F);
      }
   }

   private static double effectiveGravity(class_1309 entity) {
      double gravity = 0.08;
      boolean falling = entity.method_18798().field_1351 <= 0.0;
      return falling && entity.method_6059(class_1294.field_5906) ? Math.min(gravity, 0.01) : gravity;
   }

   private boolean lookingAt(class_746 player, class_1309 entity, float yaw, float pitch) {
      float range = (float)Math.min(
         (double)this.rotationRange.getFloatValue(), Math.max((double)this.attackRange.getFloatValue(), (double)player.method_5739(entity) + 0.75)
      );
      if (ReachHelper.raycastEntity(entity, new Angle(yaw, pitch), range, this.throughWalls.isEnabled()) != null) {
         return true;
      } else {
         class_243 eye = player.method_33571();
         class_243 end = eye.method_1019(class_243.method_1030(pitch, yaw).method_1021((double)range));
         return entity.method_5829().method_1009(0.08, 0.22, 0.08).method_992(eye, end).isPresent();
      }
   }

   private void selectTarget() {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null && !player.method_5805()) {
         this.enabledSetting.setEnabled(false);
      }

      if (this.enabledSetting.isEnabled() && player != null && class_310.method_1551().field_1687 != null) {
         if (!this.focusOne.isEnabled()) {
            this.targetSelector.update();
         }

         TargetFilter filter = new TargetFilter(this.targets.getSelectedOptions(), "Legit", this.fov.getIntValue());
         this.target = this.targetSelector.process(filter, this.rotationRange.getFloatValue(), this.sorting.getSelectedOption(), this.throughWalls.isEnabled());
      } else {
         this.targetSelector.update();
         this.target = null;
      }
   }

   private class_243 aimPoint(class_746 player, class_1309 entity) {
      class_238 box = entity.method_5829();
      class_243 center = box.method_1005();
      class_243 eye = player.method_33571();
      class_243 offset = new class_243(center.field_1352 - eye.field_1352, 0.0, center.field_1350 - eye.field_1350);
      class_243 side = offset.method_1027() > 1.0E-5 ? new class_243(-offset.field_1350, 0.0, offset.field_1352).method_1029() : class_243.field_1353;
      float strength = class_3532.method_15363(this.shakeStrength.getFloatValue() / 10.0F, 0.0F, 1.0F);
      float wobble = (float)Math.sin((double)System.nanoTime() * 1.0E-9 * 1.35 + (double)((float)entity.method_5628() * 0.37F)) * 0.006F * strength;
      double y = box.field_1322
         + (double)(entity.method_17682() * class_3532.method_15363(this.aimHeight.getFloatValue(), 0.18F, 0.92F))
         + (double)this.shakePitch;
      return new class_243(
         center.field_1352 + side.field_1352 * (double)(this.shakeYaw + wobble),
         class_3532.method_15350(y, box.field_1322 + 0.18, box.field_1325 - 0.08),
         center.field_1350 + side.field_1350 * (double)(this.shakeYaw + wobble)
      );
   }

   private void updateWander(float dt, class_1309 entity) {
      int id = entity.method_5628();
      if (this.wanderTargetId != id) {
         this.wanderX = 0.0F;
         this.wanderZ = 0.0F;
         this.wanderTargetId = id;
      }

      float strength;
      if ((strength = class_3532.method_15363(this.shakeStrength.getFloatValue(), 0.0F, 10.0F)) <= 0.001F) {
         this.wanderX = 0.0F;
         this.wanderZ = 0.0F;
      } else {
         double time = (double)System.nanoTime() * 1.0E-9;
         float phase = (float)(id & 1023) * 0.173F;
         float amplitude = strength * 0.013F;
         float x = (float)(Math.sin(time * 1.47 + (double)phase) + Math.sin(time * 2.13 + (double)(phase * 0.41F)) * 0.32F) * amplitude;
         float z = (float)(Math.cos(time * 1.31 + (double)(phase * 0.7F)) + Math.sin(time * 1.91 + (double)phase + 1.4F) * 0.28F) * amplitude;
         float t = class_3532.method_15363(expApproach(dt, 0.0F, 5.2F), 0.025F, 0.16F);
         this.wanderX = class_3532.method_16439(t, this.wanderX, class_3532.method_15363(x, -amplitude, amplitude));
         this.wanderZ = class_3532.method_16439(t, this.wanderZ, class_3532.method_15363(z, -amplitude, amplitude));
      }
   }

   private float[] magnetAim(float yaw, float pitch, float yawDelta, float pitchDelta, float dt, boolean captured, boolean onTarget) {
      if (captured) {
         this.magnetLock = 0.0F;
         this.magnetAmount = 0.0F;
      }

      float mouse;
      if ((mouse = Math.abs(this.mouseYaw) + Math.abs(this.mousePitch)) < (float)Math.max(0.02, frameTime() * 0.5)) {
         this.magnetAmount *= 0.6F;
         return new float[]{yaw, pitch};
      } else {
         float absYaw = Math.abs(yawDelta);
         float absPitch = Math.abs(pitchDelta);
         float error = absYaw + absPitch * 0.62F;
         float fov = class_3532.method_15363(this.fov.getFloatValue() * 0.5F, 24.0F, 180.0F);
         if (!onTarget && error > fov) {
            this.magnetAmount *= 0.6F;
            return new float[]{yaw, pitch};
         } else {
            float step = Math.max(0.001F, absYaw + absPitch);
            float aligned = this.mouseYaw * yawDelta + this.mousePitch * pitchDelta;
            float match = class_3532.method_15363(aligned / (mouse * step), 0.0F, 1.0F);
            float lock = onTarget ? class_3532.method_15363(1.0F - error / 8.5F, 0.35F, 1.0F) : 0.0F;
            this.magnetLock = class_3532.method_16439(
               class_3532.method_15363(expApproach(dt, 0.0F, onTarget ? 7.5F : 4.2F), 0.02F, onTarget ? 0.2F : 0.12F), this.magnetLock, lock
            );
            float strength = class_3532.method_15363(this.magnetStrength.getFloatValue(), 0.1F, 2.5F);
            float yawScale = class_3532.method_15363(this.yawSpeed.getFloatValue() / 60.0F, 0.5F, 3.0F);
            float wanted = class_3532.method_15363(0.42F * yawScale * strength, 0.0F, 0.95F) * match * class_3532.method_16439(this.magnetLock, 1.0F, 0.45F);
            float approach = wanted > this.magnetAmount
               ? class_3532.method_15363(expApproach(dt, 0.0F, 42.0F), 0.2F, 0.85F)
               : class_3532.method_15363(expApproach(dt, 0.0F, 12.0F), 0.08F, 0.4F);
            this.magnetAmount = class_3532.method_16439(approach, this.magnetAmount, wanted);
            float addYaw = yawDelta * this.magnetAmount;
            float addPitch = pitchDelta * this.magnetAmount * 0.92F;
            float clampScale = class_3532.method_15363(0.85F * strength, 0.3F, 1.7F);
            float yawLimit = Math.abs(this.mouseYaw) * clampScale;
            float pitchLimit = Math.abs(this.mousePitch) * clampScale + Math.abs(this.mouseYaw) * 0.12F;
            addYaw = clampToDelta(class_3532.method_15363(addYaw, -yawLimit, yawLimit), yawDelta);
            addPitch = clampToDelta(class_3532.method_15363(addPitch, -pitchLimit, pitchLimit), pitchDelta);
            return new float[]{yaw + addYaw, pitch + addPitch};
         }
      }
   }

   private static float clampToDelta(float value, float delta) {
      float abs = Math.abs(delta);
      return abs <= 0.001F ? 0.0F : class_3532.method_15363(value, -abs, abs);
   }

   private boolean captureTarget(int id) {
      if (this.trackedId == id) {
         return false;
      } else {
         this.trackedId = id;
         this.onTargetHold = 0;
         this.onTargetScale = 1.0F;
         this.smoothedDelta = null;
         this.magnetLock = 0.0F;
         this.magnetAmount = 0.0F;
         return true;
      }
   }

   private static double frameTime() {
      return (double)class_310.method_1551().method_61966().method_60636();
   }
}
