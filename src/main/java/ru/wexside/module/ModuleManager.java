package ru.wexside.module;

import java.util.ArrayList;
import java.util.List;
import ru.wexside.event.EventBus;
import ru.wexside.misc.ConfigRegistry;
import ru.wexside.misc.KeybindRegistry;
import ru.wexside.module.combat.AimAssistModule;
import ru.wexside.module.combat.AttackAuraModule;
import ru.wexside.module.combat.AutoArmorModule;
import ru.wexside.module.combat.AutoGAppleModule;
import ru.wexside.module.combat.AutoPotionModule;
import ru.wexside.module.combat.AutoSwapModule;
import ru.wexside.module.combat.AutoTotemModule;
import ru.wexside.module.combat.ClickPearlModule;
import ru.wexside.module.combat.ElytraHelperModule;
import ru.wexside.module.combat.HitBoxesModule;
import ru.wexside.module.combat.ItemHelperModule;
import ru.wexside.module.combat.MaceHelperModule;
import ru.wexside.module.combat.NoEntityTraceModule;
import ru.wexside.module.combat.TapeMouseModule;
import ru.wexside.module.combat.TridentClickerModule;
import ru.wexside.module.combat.TriggerBotModule;
import ru.wexside.module.combat.VelocityModule;
import ru.wexside.module.combat.WeaponSpamModule;
import ru.wexside.module.hud.AnimateModule;
import ru.wexside.module.hud.ArrowsModule;
import ru.wexside.module.hud.AuctionHelperModule;
import ru.wexside.module.hud.BetterChatModule;
import ru.wexside.module.hud.CrosshairModule;
import ru.wexside.module.hud.ExtraTabModule;
import ru.wexside.module.hud.HUDModule;
import ru.wexside.module.hud.HealthHelperModule;
import ru.wexside.module.hud.HotbarModule;
import ru.wexside.module.hud.PositionModule;
import ru.wexside.module.misc.AlternativeMenuKeyModule;
import ru.wexside.module.misc.AutoConfigSaveModule;
import ru.wexside.module.misc.CommandCharacterModule;
import ru.wexside.module.misc.CustomSoundsModule;
import ru.wexside.module.misc.FriendsModule;
import ru.wexside.module.misc.HologramOptimizerModule;
import ru.wexside.module.misc.PotionCombinerModule;
import ru.wexside.module.misc.SRPSpooferModule;
import ru.wexside.module.misc.ServerJoinerModule;
import ru.wexside.module.misc.SoundRemoverModule;
import ru.wexside.module.movement.AirStuckModule;
import ru.wexside.module.movement.AutoJumpModule;
import ru.wexside.module.movement.AutoSprintModule;
import ru.wexside.module.movement.EagleModule;
import ru.wexside.module.movement.EdgeJumpModule;
import ru.wexside.module.movement.FlightModule;
import ru.wexside.module.movement.FreeCameraModule;
import ru.wexside.module.movement.GuiMoveModule;
import ru.wexside.module.movement.NoJumpDelayModule;
import ru.wexside.module.movement.NoSlowDownModule;
import ru.wexside.module.movement.SpeedModule;
import ru.wexside.module.movement.TimerModule;
import ru.wexside.module.player.AntiAFKModule;
import ru.wexside.module.player.AutoAuthModule;
import ru.wexside.module.player.AutoEatModule;
import ru.wexside.module.player.AutoEventModule;
import ru.wexside.module.player.AutoFishModule;
import ru.wexside.module.player.AutoLeaveModule;
import ru.wexside.module.player.AutoRespawnModule;
import ru.wexside.module.player.AutoSpawnModule;
import ru.wexside.module.player.AutoTPAcceptModule;
import ru.wexside.module.player.AutoToolModule;
import ru.wexside.module.player.ChestStealerModule;
import ru.wexside.module.player.FakePlayerModule;
import ru.wexside.module.player.FastBreakModule;
import ru.wexside.module.player.FastExperienceModule;
import ru.wexside.module.player.FastPlaceModule;
import ru.wexside.module.player.ItemScrollerModule;
import ru.wexside.module.player.MineHelperModule;
import ru.wexside.module.player.NameProtectModule;
import ru.wexside.module.player.NoInteractModule;
import ru.wexside.module.player.NoPushModule;
import ru.wexside.module.player.NukerModule;
import ru.wexside.module.player.OpenWallsModule;
import ru.wexside.module.player.ServerHelperModule;
import ru.wexside.module.player.UseTrackerModule;
import ru.wexside.module.render.AmbientModule;
import ru.wexside.module.render.AspectRatioModule;
import ru.wexside.module.render.BlockESPModule;
import ru.wexside.module.render.BlockOverlayModule;
import ru.wexside.module.render.BrightnessModule;
import ru.wexside.module.render.CameraModule;
import ru.wexside.module.render.ColorCorrectionModule;
import ru.wexside.module.render.ConsumablesESPModule;
import ru.wexside.module.render.CriticalHitEffectModule;
import ru.wexside.module.render.DurationVisualiserModule;
import ru.wexside.module.render.ElytraTrailsModule;
import ru.wexside.module.render.FreeLookModule;
import ru.wexside.module.render.HandAuraModule;
import ru.wexside.module.render.HandModifyModule;
import ru.wexside.module.render.HitEffectModule;
import ru.wexside.module.render.HitParticlesModule;
import ru.wexside.module.render.ItemPhysicModule;
import ru.wexside.module.render.NoRenderModule;
import ru.wexside.module.render.SeeInvisiblesModule;
import ru.wexside.module.render.ShulkerViewerModule;
import ru.wexside.module.render.StructuresModule;
import ru.wexside.module.render.TargetESPModule;
import ru.wexside.module.render.TotemEffectsModule;
import ru.wexside.module.render.TracersModule;
import ru.wexside.module.render.TrailsModule;
import ru.wexside.module.render.WaypointsModule;
import ru.wexside.module.render.WorldParticlesModule;
import ru.wexside.setting.BindSetting;
import ru.wexside.setting.Setting;

public class ModuleManager {
   private final List<Module> modules = new ArrayList<>();

   public List<Module> getModules() {
      return this.modules;
   }

   public <T extends Module> T getModule(Class<T> clazz) {
      for(Module module : this.modules) {
         if (clazz.isInstance(module)) {
            return clazz.cast(module);
         }
      }

      return null;
   }

   public void registerConfigEntries(ConfigRegistry configRegistry) {
      this.modules.forEach(configRegistry::register);
   }

   public void registerDefaults(EventBus eventBus) {
      this.modules.add(new AttackAuraModule(eventBus));
      this.modules.add(new AimAssistModule(eventBus));
      this.modules.add(new TriggerBotModule(eventBus));
      this.modules.add(new TapeMouseModule(eventBus));
      this.modules.add(new TridentClickerModule(eventBus));
      this.modules.add(new NoEntityTraceModule(eventBus));
      this.modules.add(new HitBoxesModule(eventBus));
      this.modules.add(new AutoArmorModule(eventBus));
      this.modules.add(new ItemHelperModule(eventBus));
      this.modules.add(new AutoSwapModule(eventBus));
      this.modules.add(new AutoTotemModule(eventBus));
      this.modules.add(new MaceHelperModule(eventBus));
      this.modules.add(new AutoPotionModule(eventBus));
      this.modules.add(new AutoGAppleModule(eventBus));
      this.modules.add(new ElytraHelperModule(eventBus));
      this.modules.add(new ClickPearlModule(eventBus));
      this.modules.add(new VelocityModule(eventBus));
      this.modules.add(new WeaponSpamModule(eventBus));
      this.modules.add(new AutoSprintModule(eventBus));
      this.modules.add(new AutoJumpModule(eventBus));
      this.modules.add(new NoJumpDelayModule(eventBus));
      this.modules.add(new GuiMoveModule(eventBus));
      this.modules.add(new EagleModule(eventBus));
      this.modules.add(new TimerModule(eventBus));
      this.modules.add(new FreeCameraModule(eventBus));
      this.modules.add(new EdgeJumpModule(eventBus));
      this.modules.add(new AirStuckModule(eventBus));
      this.modules.add(new NoSlowDownModule(eventBus));
      this.modules.add(new SpeedModule(eventBus));
      this.modules.add(new FlightModule(eventBus));
      this.modules.add(new BrightnessModule(eventBus));
      this.modules.add(new AmbientModule(eventBus));
      this.modules.add(new HitParticlesModule(eventBus));
      this.modules.add(new BlockESPModule(eventBus));
      this.modules.add(new TracersModule(eventBus));
      this.modules.add(new HitEffectModule(eventBus));
      this.modules.add(new CriticalHitEffectModule(eventBus));
      this.modules.add(new TotemEffectsModule(eventBus));
      this.modules.add(new TargetESPModule(eventBus));
      this.modules.add(new ColorCorrectionModule(eventBus));
      this.modules.add(new ItemPhysicModule(eventBus));
      this.modules.add(new WorldParticlesModule(eventBus));
      this.modules.add(new ElytraTrailsModule(eventBus));
      this.modules.add(new HandModifyModule(eventBus));
      this.modules.add(new SeeInvisiblesModule(eventBus));
      this.modules.add(new AspectRatioModule(eventBus));
      this.modules.add(new TrailsModule(eventBus));
      this.modules.add(new NoRenderModule(eventBus));
      this.modules.add(new WaypointsModule(eventBus));
      this.modules.add(new DurationVisualiserModule(eventBus));
      this.modules.add(new StructuresModule(eventBus));
      this.modules.add(new ConsumablesESPModule(eventBus));
      this.modules.add(new ShulkerViewerModule(eventBus));
      this.modules.add(new FreeLookModule(eventBus));
      this.modules.add(new CameraModule(eventBus));
      this.modules.add(new BlockOverlayModule(eventBus));
      this.modules.add(new HandAuraModule(eventBus));
      this.modules.add(new HologramOptimizerModule(eventBus));
      this.modules.add(new FakePlayerModule(eventBus));
      this.modules.add(new AutoEventModule(eventBus));
      this.modules.add(new FriendsModule(eventBus));
      this.modules.add(new FastBreakModule(eventBus));
      this.modules.add(new FastPlaceModule(eventBus));
      this.modules.add(new NoPushModule(eventBus));
      this.modules.add(new NoInteractModule(eventBus));
      this.modules.add(new UseTrackerModule(eventBus));
      this.modules.add(new ItemScrollerModule(eventBus));
      this.modules.add(new AutoRespawnModule(eventBus));
      this.modules.add(new AutoTPAcceptModule(eventBus));
      this.modules.add(new AutoAuthModule(eventBus));
      this.modules.add(new AutoSpawnModule(eventBus));
      this.modules.add(new AutoLeaveModule(eventBus));
      this.modules.add(new OpenWallsModule(eventBus));
      this.modules.add(new FastExperienceModule(eventBus));
      this.modules.add(new ServerJoinerModule(eventBus));
      this.modules.add(new AntiAFKModule(eventBus));
      this.modules.add(new NameProtectModule(eventBus));
      this.modules.add(new MineHelperModule(eventBus));
      this.modules.add(new NukerModule(eventBus));
      this.modules.add(new SRPSpooferModule(eventBus));
      this.modules.add(new AutoEatModule(eventBus));
      this.modules.add(new AutoFishModule(eventBus));
      this.modules.add(new ChestStealerModule(eventBus));
      this.modules.add(new CrosshairModule(eventBus));
      this.modules.add(new HotbarModule(eventBus));
      this.modules.add(new PositionModule(eventBus));
      this.modules.add(new ArrowsModule(eventBus));
      this.modules.add(new HUDModule(eventBus));
      this.modules.add(new AnimateModule(eventBus));
      this.modules.add(new BetterChatModule(eventBus));
      this.modules.add(new ExtraTabModule(eventBus));
      this.modules.add(new AuctionHelperModule(eventBus));
      this.modules.add(new ServerHelperModule(eventBus));
      this.modules.add(new PotionCombinerModule(eventBus));
      this.modules.add(new SoundRemoverModule(eventBus));
      this.modules.add(new CustomSoundsModule(eventBus));
      this.modules.add(new CommandCharacterModule(eventBus));
      this.modules.add(new AlternativeMenuKeyModule(eventBus));
      this.modules.add(new AutoConfigSaveModule(eventBus));
      this.modules.add(new AutoToolModule(eventBus));
      this.modules.add(new HealthHelperModule(eventBus));
   }

   public void registerKeybindSettings(KeybindRegistry keybindRegistry) {
      for(Module module : this.modules) {
         for(Setting setting : module.getSettings()) {
            if (setting instanceof BindSetting bindSetting) {
               keybindRegistry.register(bindSetting.getKeybindBinding());
            } else if (setting.hasKeybind()) {
               keybindRegistry.register(setting.getKeybind());
            }
         }
      }
   }
}
