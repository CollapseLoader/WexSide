package ru.wexside.render;

import java.io.IOException;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStopping;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Join;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents.BeforeBlockOutline;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents.EndExtraction;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents.EndMain;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_8143;
import net.minecraft.class_9779;
import ru.wexside.WexSideClient;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.DamageEvent;
import ru.wexside.event.DamageType;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.event.TotemPopEvent;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.ConfigManager;
import ru.wexside.module.hud.AnimateModule;
import ru.wexside.module.misc.AutoConfigSaveModule;
import ru.wexside.module.render.BlockOverlayModule;
import ru.wexside.module.render.ColorCorrectionModule;
import ru.wexside.module.render.TargetESPModule;
import ru.wexside.module.render.TracersModule;
import ru.wexside.ui.WexsideScreen;
import ru.wexside.util.ClientClock;

public final class ClientEventBridge {
   private static class_332 pendingHudContext;
   private static class_9779 pendingHudTickCounter;

   private ClientEventBridge() {
   }

   public static void register(EventBus eventBus) {
      eventBus.subscribe(IncomingPacketEvent.class, event -> onIncomingPacket(eventBus, event));
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> onEndTick(eventBus, client));
      ClientLifecycleEvents.CLIENT_STOPPING.register((ClientStopping)client -> saveActiveConfig());
      HudRenderCallback.EVENT.register((HudRenderCallback)(context, tickCounter) -> {
         RenderFrameClock.advanceFrame();
         pendingHudContext = context;
         pendingHudTickCounter = tickCounter;

         try {
            renderHudAfterVanillaGui();
         } catch (Throwable var4) {
            var4.printStackTrace();
         } finally {
            pendingHudContext = null;
            pendingHudTickCounter = null;
         }
      });
      WorldRenderEvents.END_EXTRACTION
         .register((EndExtraction)context -> RenderFrameState.update(context.camera().method_71156(), context.cullProjectionMatrix(), context.viewMatrix()));
      WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((BeforeBlockOutline)(context, outline) -> !BlockOverlayModule.isEnabled());
      WorldRenderEvents.END_MAIN.register((EndMain)context -> {
         eventBus.post(new WorldRenderEvent(context.matrices(), ClientClock.tickDelta()));
         TargetESPModule.tick3();
         ColorCorrectionModule.tick();
      });
      ClientPlayConnectionEvents.JOIN.register((Join)(handler, sender, client) -> eventBus.post(new WorldSessionEvent(WorldSessionEvent.Change.JOINED)));
      ClientPlayConnectionEvents.DISCONNECT.register((Disconnect)(handler, client) -> {
         RenderFrameState.clear();
         eventBus.post(new WorldSessionEvent(WorldSessionEvent.Change.DISCONNECTED));
      });
   }

   public static void renderHudAfterVanillaGui() {
      class_332 context = pendingHudContext;
      class_9779 tickCounter = pendingHudTickCounter;
      if (context == null || tickCounter == null) {
         return;
      }

      EventBus eventBus = WexSideClient.getEventBus();
      if (eventBus != null) {
         try {
            AnimateModule.onHudRender(context);
            if (!(class_310.method_1551().field_1755 instanceof WexsideScreen)) {
               eventBus.post(new HudRenderEvent(context, tickCounter));
            }
         } catch (Throwable var5) {
            var5.printStackTrace();
         }
      }
   }

   private static void onIncomingPacket(EventBus eventBus, IncomingPacketEvent event) {
      class_310 client = class_310.method_1551();
      if (client.field_1687 != null) {
         class_2596 source = event.getPacket();
         if (source instanceof class_2663 status && status.method_11470() == 35) {
            class_1297 entity = status.method_11469(client.field_1687);
            if (entity != null) {
               eventBus.post(
                  new TotemPopEvent(
                     entity,
                     new class_243(entity.method_23317(), entity.method_23318() + (double)entity.method_17682() * 0.5, entity.method_23321()),
                     System.currentTimeMillis()
                  )
               );
            }

            return;
         }

         if (client.field_1724 != null) {
            source = event.getPacket();
            if (source instanceof class_8143 damage && damage.comp_1267() == client.field_1724.method_5628()) {
               class_1282 damageSource = damage.method_49071(client.field_1687);
               DamageType type;
               if (damageSource.method_5526() != null && damageSource.method_5529() != null && damageSource.method_5526() != damageSource.method_5529()) {
                  type = DamageType.PROJECTILE;
               } else if (damageSource.method_5529() != null) {
                  type = DamageType.DIRECT;
               } else {
                  type = DamageType.ENVIRONMENTAL;
               }

               eventBus.post(new DamageEvent(type));
            }
         }
      }
   }

   private static void onEndTick(EventBus eventBus, class_310 client) {
      ClientClock.advanceTick();
      if (client.field_1724 != null && client.field_1687 != null) {
         eventBus.post(new ClientTickEvent());
      }
   }

   private static void saveActiveConfig() {
      if (AutoConfigSaveModule.isActive()) {
         ConfigManager configManager = WexSideClient.getConfigManager();
         if (configManager != null && !configManager.hasPendingImportedEntries()) {
            String profile = configManager.getCurrentProfileName();
            if (profile == null || profile.isBlank()) {
               profile = "default";
            }

            try {
               configManager.saveProfile(profile);
            } catch (IOException var3) {
               WexSideClient.getInstance().getLogger().warn("Unable to auto-save profile {}", profile, var3);
            }
         }
      }
   }
}
