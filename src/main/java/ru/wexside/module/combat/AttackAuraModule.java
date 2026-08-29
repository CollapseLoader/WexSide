package ru.wexside.module.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2678;
import net.minecraft.class_2743;
import net.minecraft.class_2749;
import net.minecraft.class_2767;
import net.minecraft.class_2828;
import net.minecraft.class_310;
import net.minecraft.class_3489;
import net.minecraft.class_746;
import net.minecraft.class_2828.class_2831;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.AttackWindowEvent;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.event.OutgoingPacketEvent;
import ru.wexside.event.PreAttackEvent;
import ru.wexside.misc.AttackOptions;
import ru.wexside.misc.AttackUrgency;
import ru.wexside.misc.CorrectionMode;
import ru.wexside.misc.HotbarTracker;
import ru.wexside.misc.RaycastMode;
import ru.wexside.misc.ReachHelper;
import ru.wexside.misc.RotationApplyResult;
import ru.wexside.misc.ShieldBreaker;
import ru.wexside.misc.SprintReset;
import ru.wexside.misc.SprintResetMode;
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
import ru.wexside.util.AimCalculator;
import ru.wexside.util.Angle;
import ru.wexside.util.AuraProcessor;
import ru.wexside.util.CriticalsHandler;
import ru.wexside.util.RotationController;
import ru.wexside.util.RotationIntent;
import ru.wexside.util.TargetSelector;

public class AttackAuraModule extends Module implements ConfigSerializable {
   private static volatile AttackAuraModule instance;
   private final BooleanSetting enabledSetting;
   private final NumberSetting range;
   private final NumberSetting additionalRange;
   private final MultiSelectSetting targets;
   private final ModeSetting sorting;
   private final ModeSetting rotationMode;
   private final NumberSetting fov;
   private final ModeSetting attackMode;
   private final NumberSetting cps;
   private final ModeSetting earlyHit;
   private final NumberSetting accuracy;
   private final ModeSetting correctionMode;
   private final ModeSetting sprintReset;
   private final BooleanSetting noSprintNear;
   private final NumberSetting noSprintDistance;
   private final BooleanSetting ignoreWhileUsing;
   private final BooleanSetting throughWalls;
   private final BooleanSetting criticals;
   private final BooleanSetting jumpOnly;
   private final NumberSetting maceFallDistance;
   private final BooleanSetting swordOnly;
   private final BooleanSetting breakShield;
   private final BooleanSetting desyncShield;
   private final BooleanSetting tpsSync;
   private final HotbarTracker hotbarTracker = new HotbarTracker();
   private final Map<Integer, Integer> hitDelayHistogram = new HashMap<>();
   private boolean lastEnabledState;
   private int warmupTicks;
   private TargetSelector targetSelector;
   private AimCalculator aimCalculator;
   private CriticalsHandler criticalsHandler;
   private AuraProcessor auraProcessor;
   private class_1309 target;
   private boolean rotationReady;
   private boolean adaptiveReady;
   private boolean calibratingAdaptive;
   private boolean heardHitSound;
   private int silenceTicks;
   private int quietTicks;
   private int calibrationTicks;
   private int learnedHitDelay;
   private int packetsThisWindow;

   public AttackAuraModule(EventBus eventBus) {
      super(eventBus, "attack_aura", "Attack Aura", "Автоматически бьёт игроков вокруг", ModuleCategory.valueOf("COMBAT"));
      instance = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Test killaura module")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      this.range = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 12.0)
            .defaultValue(3.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .showMarkers()
            .name("Максимальное расстояние")
            .id("range")
            .description("Максимальная дистанция для атаки"))
         .build();
      this.registerSetting(this.range);
      this.additionalRange = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 6.0)
            .defaultValue(2.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .showMarkers()
            .name("Дополнительное расстояние")
            .id("additional_range")
            .description("Дополнительная дистанция поиска целей и вращения"))
         .build();
      this.registerSetting(this.additionalRange);
      MultiSelectSetting targetsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("Players", "Friends", "Bots", "Naked", "Invisibles", "Animals", "Mobs", "Villagers")
            .selectAll(false)
            .optionListEnabled(false)
            .name("Цели")
            .id("targets")
            .description("Типы целей для атаки")
            .withKeybind())
         .build();
      this.targets = targetsSetting;
      this.registerSetting(targetsSetting);
      this.sorting = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Health", "Distance", "Crosshair")
            .defaultOption("Distance")
            .name("Сортировка")
            .id("sorting")
            .description("Сортировка целей для атаки")
            .withKeybind())
         .build();
      this.registerSetting(this.sorting);
      this.rotationMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Simple", "FT Snap", "RW", "Spooky", "Spooky Test")
            .defaultOption("Simple")
            .name("Режим")
            .id("mode")
            .description("Режим вращения")
            .withKeybind())
         .build();
      this.registerSetting(this.rotationMode);
      this.fov = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 360.0)
            .defaultValue(180.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("FOV")
            .id("fov")
            .description("Поле зрения для поиска цели")
            .visibleWhen(() -> "FT Snap".equals(this.rotationMode.getSelectedOption())))
         .build();
      this.registerSetting(this.fov);
      this.attackMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("1.9+", "1.8")
            .defaultOption("1.9+")
            .name("Режим атаки")
            .id("attack_mode")
            .description("Режим атаки")
            .withKeybind())
         .build();
      this.registerSetting(this.attackMode);
      this.cps = ((NumberSettingBuilder)NumberSetting.builder()
            .range(5.0, 20.0)
            .defaultValue(15.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("CPS")
            .id("cps")
            .description("Количество кликов в секунду")
            .visibleWhen(() -> "1.8".equals(this.attackMode.getSelectedOption())))
         .build();
      this.registerSetting(this.cps);
      this.earlyHit = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Всегда", "Адаптивный", "Отключен")
            .defaultOption("Адаптивный")
            .name("Ранний удар")
            .id("early_hit")
            .description("Модуль бьёт раньше ванильного удара\nРекомендуется адаптивный режим, всегда - на страх и риск")
            .withKeybind())
         .build();
      this.registerSetting(this.earlyHit);
      this.accuracy = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 100.0)
            .defaultValue(100.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Точность")
            .id("accuracy")
            .description("Шанс попасть при ударе"))
         .build();
      this.registerSetting(this.accuracy);
      this.correctionMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Focused", "Free", "Lock")
            .defaultOption("Focused")
            .name("Режим коррекции")
            .id("correction_mode")
            .description("Режим коррекции движения")
            .withKeybind())
         .build();
      this.registerSetting(this.correctionMode);
      this.sprintReset = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Packet", "Semi-Legit", "Legit")
            .defaultOption("Semi-Legit")
            .name("Сброс спринта")
            .id("sprint_reset")
            .description("Режим сброса спринта")
            .withKeybind())
         .build();
      this.registerSetting(this.sprintReset);
      this.noSprintNear = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Без спринта у цели")
            .id("no_sprint_near")
            .description("Отключение спринта рядом с целью")
            .withKeybind()
            .visibleWhen(() -> "Legit".equals(this.sprintReset.getSelectedOption())))
         .build();
      this.registerSetting(this.noSprintNear);
      this.noSprintDistance = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 5.0)
            .defaultValue(3.0)
            .multiplier(1.0)
            .precision(1)
            .animationSpeed(20.0F)
            .showMarkers()
            .snapTo(0.5)
            .name("Дистанция без спринта")
            .id("no_sprint_distance")
            .description("Расстояние до цели для отключения спринта")
            .visibleWhen(() -> "Legit".equals(this.sprintReset.getSelectedOption()) && this.noSprintNear.isEnabled()))
         .build();
      this.registerSetting(this.noSprintDistance);
      this.ignoreWhileUsing = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Игнор при использовании")
            .id("ignore_while_using")
            .description("Отключить модуль, пока вы используете предмет")
            .withKeybind())
         .build();
      this.registerSetting(this.ignoreWhileUsing);
      this.throughWalls = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Сквозь стены")
            .id("through_walls")
            .description("Позволяет атаковать сквозь стены")
            .withKeybind())
         .build();
      this.registerSetting(this.throughWalls);
      this.criticals = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Криты")
            .id("criticals")
            .description("Атака только критическим уроном")
            .withKeybind())
         .build();
      this.registerSetting(this.criticals);
      this.jumpOnly = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Только при прыжке")
            .id("jump_only")
            .description("Наносит критический урон только\nпри зажатой кнопке прыжка")
            .withKeybind()
            .visibleWhen(() -> this.criticals.isEnabled()))
         .build();
      this.registerSetting(this.jumpOnly);
      this.maceFallDistance = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.5, 8.0)
            .defaultValue(1.6)
            .multiplier(1.0)
            .precision(1)
            .animationSpeed(20.0F)
            .snapTo(0.1)
            .name("Булава: мин. падение")
            .id("mace_fall_distance")
            .description("Дистанция падения, с которой булава бьёт смэшем"))
         .build();
      this.registerSetting(this.maceFallDistance);
      this.swordOnly = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Только меч")
            .id("sword_only")
            .description("Бить только мечом")
            .withKeybind())
         .build();
      this.registerSetting(this.swordOnly);
      this.breakShield = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Ломать щит")
            .id("break_shield")
            .description("Ломать щиты противников")
            .withKeybind())
         .build();
      this.registerSetting(this.breakShield);
      this.desyncShield = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Десинхр. щита")
            .id("desync_shield")
            .description("Десинхронизация щита")
            .withKeybind())
         .build();
      this.registerSetting(this.desyncShield);
      this.tpsSync = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("TPS Sync")
            .id("tps_sync")
            .description("Синхронизирует кулдаун с TPS сервера")
            .withKeybind())
         .build();
      this.registerSetting(this.tpsSync);
   }

   @Override
   protected void initialize() {
      this.targetSelector = new TargetSelector();
      this.aimCalculator = new AimCalculator();
      this.criticalsHandler = new CriticalsHandler();
      this.auraProcessor = new AuraProcessor(this.criticalsHandler);
      this.listen(ClientTickEvent.class, event -> this.onClientTick());
      this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
      this.listen(OutgoingPacketEvent.class, this::onOutgoingPacket);
      this.listen(AttackWindowEvent.class, this::onAttackWindow);
      this.listen(PreAttackEvent.class, this::onPreAttack);
   }

   public boolean isActive() {
      return this.enabledSetting.isEnabled();
   }

   public class_1309 getLivingEntity() {
      return this.target;
   }

   public class_1309 getTarget() {
      return this.target;
   }

   public static boolean isActive4() {
      AttackAuraModule module = instance;
      return module != null && module.enabledSetting.isEnabled() && module.target != null;
   }

   public static boolean hasTarget() {
      return isActive4();
   }

   private void onClientTick() {
      this.criticalsHandler.tick();
      this.auraProcessor.tick();
      SprintReset.setActive(false);
      ShieldBreaker.reset();
      RotationController rotations = WexSideClient.getRotationController();
      if (rotations != null) {
         boolean isEnabled = this.enabledSetting.isEnabled() && class_310.method_1551().field_1724 != null;
         if (isEnabled != this.lastEnabledState) {
            this.lastEnabledState = isEnabled;
            if (isEnabled) {
               this.warmupTicks = 4;
            }
         }

         if (isEnabled) {
            class_746 player = class_310.method_1551().field_1724;
            TargetFilter filter = new TargetFilter(this.targets.getSelectedOptions(), this.sorting.getSelectedOption(), this.fov.getIntValue());
            float searchRange = this.range.getFloatValue() + this.additionalRange.getFloatValue();
            if (this.auraProcessor.getMaceCheck().isHoldingMace(player)) {
               searchRange = Math.max(searchRange, this.range.getFloatValue() + 1.5F);
            }

            class_1309 selected = this.targetSelector.findTarget(filter, searchRange, this.sorting.getSelectedOption(), this.throughWalls.isEnabled());
            if (selected == null) {
               this.clearTarget();
               if (this.warmupTicks <= 0) {
                  this.holdIdleRotation(rotations);
               }
            } else {
               this.tryBreakShield(player);
               SprintReset.setActive(this.shouldBlockSprint(selected));
               class_243 aimPoint = this.aimCalculator
                  .calculateAimPoint(selected, this.currentLook(), this.range.getFloatValue(), searchRange, this.throughWalls.isEnabled());
               if (aimPoint == null) {
                  this.clearTarget();
                  if (this.warmupTicks <= 0) {
                     this.holdIdleRotation(rotations);
                  }
               } else if (this.warmupTicks > 0) {
                  this.warmupTicks--;
                  this.target = selected;
                  this.rotationReady = false;
               } else {
                  class_243 eyes = class_310.method_1551().field_1724.method_33571();
                  Angle targetAngle = Angle.fromVectors(eyes, aimPoint);
                  CorrectionMode correction = CorrectionMode.fromName(this.correctionMode.getSelectedOption());
                  AttackUrgency urgency = this.resolveAttackUrgency(selected, targetAngle);
                  RotationIntent intent = new RotationIntent(selected, aimPoint, targetAngle, urgency, correction, false);
                  RotationApplyResult result = rotations.apply(intent, this.rotationMode.getSelectedOption());
                  this.target = selected;
                  this.rotationReady = result.isReady();
                  if (urgency == AttackUrgency.HIT && this.rotationReady) {
                     rotations.markAttacked();
                  }
               }
            }
         } else {
            this.warmupTicks = 0;
            this.targetSelector.reset();
            this.clearTarget();
            this.stopRotations(rotations);
         }
      }
   }

   private void tryAttack(boolean ready) {
      if (this.enabledSetting.isEnabled() && this.target != null) {
         class_746 player = class_310.method_1551().field_1724;
         if (player != null && !player.method_7325()) {
            if (!this.ignoreWhileUsing.isEnabled() || !class_310.method_1551().field_1690.field_1904.method_1434()) {
               if (!this.swordOnly.isEnabled() || this.isHoldingSword()) {
                  this.auraProcessor.getHitCooldown().setAttackRequested(ready);
                  this.auraProcessor.getHitCooldown().setTpsSync(this.tpsSync.isEnabled());
                  Angle look = WexSideClient.getRotationController().getAngle();
                  if (look != null) {
                     AttackOptions options = new AttackOptions(
                        look,
                        this.range.getFloatValue(),
                        this.cps.getIntValue(),
                        true,
                        true,
                        SprintResetMode.process(this.sprintReset.getSelectedOption()),
                        this.criticals.isEnabled(),
                        this.throughWalls.isEnabled() ? RaycastMode.THROUGH_WALLS : RaycastMode.VISIBLE,
                        "1.8".equals(this.attackMode.getSelectedOption()),
                        this.accuracy.getValue(),
                        this.breakShield.isEnabled(),
                        this.desyncShield.isEnabled(),
                        this.jumpOnly.isEnabled(),
                        (float)this.maceFallDistance.getValue()
                     );
                     if (this.auraProcessor.attack(this.target, options)) {
                        WexSideClient.getRotationController().notifyHit();
                     }
                  }
               }
            }
         }
      }
   }

   private void onAttackWindow(AttackWindowEvent event) {
      if (event.isPre()) {
         this.updateAdaptiveWindow();
      }

      String mode = this.earlyHit.getSelectedOption();
      if ("Отключен".equals(mode)) {
         if (event.isPre()) {
            this.tryAttack(true);
         }
      } else if ("Всегда".equals(mode)) {
         this.tryAttack(!event.isPost());
      } else {
         if (this.isAdaptiveEarlyHit()) {
            this.tryAttack(event.isPre());
         } else {
            this.tryAttack(!event.isPost());
         }
      }
   }

   private boolean isAdaptiveEarlyHit() {
      return "Адаптивный".equals(this.earlyHit.getSelectedOption()) && this.adaptiveReady;
   }

   private void updateAdaptiveWindow() {
      boolean wasReady = this.adaptiveReady;
      this.tickAdaptiveReady();
      if (this.isAdaptiveEarlyHit()) {
         if (!wasReady) {
            this.calibratingAdaptive = true;
            this.calibrationTicks = 0;
            this.learnedHitDelay = 0;
            this.hitDelayHistogram.clear();
         } else if (this.calibratingAdaptive) {
            this.hitDelayHistogram.merge(this.packetsThisWindow, 1, Integer::sum);
            if (++this.calibrationTicks >= 40) {
               this.learnedHitDelay = this.mostCommonHitDelay();
               this.calibratingAdaptive = false;
            }
         }

         this.packetsThisWindow = 0;
         this.auraProcessor.getHitCooldown().setVanillaHitsAllowed(false);
      } else {
         this.packetsThisWindow = 0;
         this.auraProcessor.getHitCooldown().setVanillaHitsAllowed(true);
      }
   }

   private void tickAdaptiveReady() {
      if (!this.adaptiveReady) {
         if (this.heardHitSound) {
            this.quietTicks = 0;
            if (++this.silenceTicks >= 50) {
               this.adaptiveReady = true;
            }
         } else if (++this.quietTicks >= 4) {
            this.silenceTicks = 0;
         }
      }

      this.heardHitSound = false;
   }

   private void onIncomingPacket(IncomingPacketEvent event) {
      if (event.getPacket() instanceof class_2749) {
         this.criticalsHandler.getVelocityTracker().reset();
      }

      class_2596<?> packet = event.getPacket();
      if (packet instanceof class_2743 velocityPacket) {
         class_746 player = class_310.method_1551().field_1724;
         if (player != null && velocityPacket.method_11818() == player.method_5628()) {
            this.criticalsHandler.getVelocityTracker().setYVelocity(velocityPacket.method_73085().field_1351);
         }
      }

      if (event.getPacket() instanceof class_2767) {
         this.heardHitSound = true;
      } else if (event.getPacket() instanceof class_2678) {
         this.criticalsHandler.getVelocityTracker().reset();
         this.adaptiveReady = false;
         this.silenceTicks = 0;
         this.quietTicks = 0;
         this.heardHitSound = false;
         this.calibratingAdaptive = false;
         this.calibrationTicks = 0;
         this.learnedHitDelay = 0;
         this.packetsThisWindow = 0;
         this.hitDelayHistogram.clear();
         this.auraProcessor.getHitCooldown().setVanillaHitsAllowed(true);
      }
   }

   private void onOutgoingPacket(OutgoingPacketEvent event) {
      class_2596<?> packet = event.getPacket();
      if (packet instanceof class_2831 lookAndMove) {
         this.criticalsHandler.getVelocityTracker().onMovementPacket(lookAndMove);
      }

      if (event.getPacket() instanceof class_2828) {
         if (this.isAdaptiveEarlyHit()) {
            ++this.packetsThisWindow;
            if (!this.calibratingAdaptive && this.learnedHitDelay > 0 && this.packetsThisWindow >= this.learnedHitDelay) {
               this.auraProcessor.getHitCooldown().setVanillaHitsAllowed(true);
            }
         }
      }
   }

   private void onPreAttack(PreAttackEvent event) {
      RotationController rotations = WexSideClient.getRotationController();
      if (this.enabledSetting.isEnabled() && rotations != null && rotations.ticksSinceHit() < 4) {
         event.cancel();
      }

      if (!event.isCancelled()) {
         this.criticalsHandler.onAttack();
      }
   }

   private AttackUrgency resolveAttackUrgency(class_1309 selected, Angle targetAngle) {
      if (!"FT Snap".equals(this.rotationMode.getSelectedOption())) {
         return AttackUrgency.SKIP;
      } else {
         class_746 player = class_310.method_1551().field_1724;
         if (player == null) {
            return AttackUrgency.SKIP;
         } else if (this.auraProcessor.getMaceCheck().isHoldingMace(player)) {
            if (!this.auraProcessor.getHitCooldown().isActive()) {
               return AttackUrgency.SKIP;
            } else if (ReachHelper.raycastEntity(selected, targetAngle, this.range.getFloatValue(), this.throughWalls.isEnabled()) != selected) {
               return AttackUrgency.SKIP;
            } else {
               boolean falling = !player.method_24828() && !player.method_6101() && player.field_6017 > 0.0;
               return falling ? AttackUrgency.HIT : AttackUrgency.SKIP;
            }
         } else {
            boolean cooldownReady = "1.8".equals(this.attackMode.getSelectedOption()) || player.method_7261(0.5F) >= 0.9F;
            if (!cooldownReady) {
               return AttackUrgency.SKIP;
            } else if (!this.auraProcessor.getHitCooldown().isActive()) {
               return AttackUrgency.SKIP;
            } else if (ReachHelper.raycastEntity(selected, targetAngle, this.range.getFloatValue(), this.throughWalls.isEnabled()) != selected) {
               return AttackUrgency.SKIP;
            } else {
               RaycastMode raycastMode = this.throughWalls.isEnabled() ? RaycastMode.THROUGH_WALLS : RaycastMode.VISIBLE;
               return this.criticalsHandler.process(this.criticals.isEnabled(), raycastMode) ? AttackUrgency.HIT : AttackUrgency.SKIP;
            }
         }
      }
   }

   private boolean shouldBlockSprint(class_1309 selected) {
      return this.shouldStopSprintNearTarget(selected) || this.shouldStopSprintForMace(selected) || this.shouldStopSprintForShieldBreak(selected);
   }

   private boolean shouldStopSprintNearTarget(class_1309 selected) {
      if (!this.noSprintNear.isEnabled()) {
         return false;
      } else if (!"Legit".equals(this.sprintReset.getSelectedOption())) {
         return false;
      } else {
         class_746 player = class_310.method_1551().field_1724;
         return player != null && TargetSelector.distanceTo(selected) <= (double)this.noSprintDistance.getFloatValue();
      }
   }

   private boolean shouldStopSprintForMace(class_1309 selected) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null && this.auraProcessor.getMaceCheck().isHoldingMace(player)) {
         return TargetSelector.distanceTo(selected) <= (double)(this.range.getFloatValue() + 1.5F);
      } else {
         return false;
      }
   }

   private boolean shouldStopSprintForShieldBreak(class_1309 selected) {
      if ("Legit".equals(this.sprintReset.getSelectedOption()) && ShieldBreaker.isActive()) {
         class_746 player = class_310.method_1551().field_1724;
         if (player == null) {
            return false;
         } else if (!this.auraProcessor.getHitCooldown().isActive()) {
            return false;
         } else {
            return !(TargetSelector.distanceTo(selected) > (double)this.range.getFloatValue());
         }
      } else {
         return false;
      }
   }

   private void tryBreakShield(class_746 player) {
      if ("Legit".equals(this.sprintReset.getSelectedOption()) || this.auraProcessor.getMaceCheck().isHoldingMace(player)) {
         class_1799 predicted;
         try {
            predicted = this.hotbarTracker.simulate(1);
         } catch (Throwable var6) {
            return;
         }

         if (predicted != null) {
            int maxUseTime = predicted.method_7935(player);
            double useProgress = maxUseTime > 0 ? (double)player.method_6048() / (double)maxUseTime : 0.0;
            ShieldBreaker.consider(player.method_6039(), useProgress);
         }
      }
   }

   private void stopRotations(RotationController rotations) {
      if (rotations.hasRotation()) {
         rotations.reset();
      }
   }

   private void holdIdleRotation(RotationController rotations) {
      if (rotations.hasRotation()) {
         rotations.apply(RotationIntent.empty(), this.rotationMode.getSelectedOption());
      }
   }

   private void clearTarget() {
      this.target = null;
      this.rotationReady = false;
   }

   private Angle currentLook() {
      Angle rotation = WexSideClient.getRotationController().getAngle();
      if (rotation != null) {
         return rotation;
      } else {
         class_746 player = class_310.method_1551().field_1724;
         return player != null ? new Angle(player.method_36454(), player.method_36455()) : Angle.ZERO;
      }
   }

   private boolean isHoldingSword() {
      class_746 player = class_310.method_1551().field_1724;
      return player != null && player.method_6047().method_31573(class_3489.field_42611);
   }

   private int mostCommonHitDelay() {
      int delay = 0;
      int bestCount = -1;

      for(Entry<Integer, Integer> entry : this.hitDelayHistogram.entrySet()) {
         int packets = entry.getKey();
         int count = entry.getValue();
         if (count >= bestCount && (count != bestCount || packets > delay)) {
            delay = packets;
            bestCount = count;
         }
      }

      return delay;
   }
}
