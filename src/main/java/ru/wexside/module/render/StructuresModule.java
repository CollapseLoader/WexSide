package ru.wexside.module.render;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2637;
import net.minecraft.class_2673;
import net.minecraft.class_2675;
import net.minecraft.class_310;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.event.IncomingPacketEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.ThemeColors;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.notification.ItemNotification;
import ru.wexside.notification.NotificationCenter;
import ru.wexside.render.ItemIconCache;
import ru.wexside.render.RenderProjection;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.util.GuiDrawApi;

public final class StructuresModule extends Module implements ConfigSerializable {
   private static final int TIMER_COLOR = -45747;
   private static final int NAME_COLOR = -1710619;
   private static final double MAX_DISTANCE_SQ = 2500.0;
   private static final long SETTLE_NS = 150000000L;
   private static final long DEFAULT_DURATION_MS = 30000L;
   private static final float CARD_HEIGHT = 16.0F;
   private static final float TITLE_SIZE = 7.0F;
   private static final float ICON_SIZE = 11.0F;
   private final BooleanSetting enabledSetting;
   private final BooleanSetting world;
   private final ItemIconCache icons = new ItemIconCache();
   private final List<StructuresModule.StructureMarker> markers = new ArrayList<>();
   private final Set<class_2338> pendingBlocks = new HashSet();
   private final List<StructuresModule.WorldEventHit> pendingEvents = new ArrayList<>();
   private volatile StructuresModule.ParticleBurst pendingBurst;
   private volatile long burstStartedAt;

   public StructuresModule(EventBus eventBus) {
      super(eventBus, "structures", "Structures", "Показывает время действия построек (трапки/пласты)", ModuleCategory.valueOf("RENDER"));
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Таймер действия трапок и пластов в мире")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      this.world = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("World")
            .id("world")
            .description("Рисовать таймер на самом строении"))
         .build();
      this.registerSetting(this.world);
   }

   @Override
   protected void initialize() {
      this.listen(IncomingPacketEvent.class, this::onPacket);
      this.listen(ClientTickEvent.class, this::onTick);
      this.listen(HudRenderEvent.class, this::onHudRender);
      this.listen(WorldSessionEvent.class, event -> this.reset());
   }

   private void onPacket(IncomingPacketEvent event) {
      if (this.enabledSetting.isEnabled()) {
         class_2596<?> packet = event.getPacket();
         if (packet instanceof class_2675 particle) {
            class_2338 pos = class_2338.method_49637(particle.method_11544() + 0.5, particle.method_11547() + 0.5, particle.method_11546() + 0.5)
               .method_10062();
            if (this.pendingBurst == null) {
               this.burstStartedAt = System.nanoTime();
            }

            this.pendingBurst = new StructuresModule.ParticleBurst(pos, particle.method_11551());
         } else if (packet instanceof class_2637 delta) {
            if (this.pendingBurst != null) {
               delta.method_30621((posx, state) -> {
                  if (!state.method_27852(class_2246.field_10124)) {
                     this.pendingBlocks.add(posx.method_10062());
                  }
               });
            }
         } else {
            if (packet instanceof class_2673 worldEvent && this.pendingBurst != null) {
               this.pendingEvents.add(new StructuresModule.WorldEventHit(worldEvent.method_11531(), worldEvent.method_11532()));
            }
         }
      }
   }

   private void onTick(ClientTickEvent event) {
      if (this.enabledSetting.isEnabled()) {
         StructuresModule.ParticleBurst burst = this.pendingBurst;
         if (burst != null && System.nanoTime() - this.burstStartedAt >= 150000000L) {
            HashSet<class_2338> blocks = new HashSet(this.pendingBlocks);
            ArrayList<StructuresModule.WorldEventHit> extras = new ArrayList<>(this.pendingEvents);
            this.pendingBlocks.clear();
            this.pendingEvents.clear();
            this.pendingBurst = null;
            this.finishBurst(burst, blocks, extras);
         }

         this.markers.removeIf(StructuresModule.StructureMarker::expired);
      }
   }

   private void reset() {
      this.pendingBurst = null;
      this.pendingBlocks.clear();
      this.pendingEvents.clear();
      this.markers.clear();
      this.icons.update3();
   }

   private void onHudRender(HudRenderEvent event) {
      if (this.enabledSetting.isEnabled() && this.world.isEnabled()) {
         class_310 client = class_310.method_1551();
         class_243 cameraPos = client.field_1773.method_19418().method_71156();
         if (client.field_1724 != null && client.field_1687 != null && cameraPos != null && !this.markers.isEmpty()) {
            ArrayList<StructuresModule.ScreenMarker> onScreen = new ArrayList<>();

            for(StructuresModule.StructureMarker marker : this.markers) {
               if (!marker.expired()) {
                  double dx = marker.x - cameraPos.field_1352;
                  double dy = marker.y - cameraPos.field_1351;
                  double dz = marker.z - cameraPos.field_1350;
                  if (!(dx * dx + dy * dy + dz * dz > 2500.0)) {
                     Vector2f screen = this.worldToScreen(marker.x, marker.y, marker.z);
                     if (screen != null) {
                        onScreen.add(new StructuresModule.ScreenMarker(screen.x, screen.y, marker));
                     }
                  }
               }
            }

            if (!onScreen.isEmpty()) {
               float scale = (float)client.method_22683().method_4495();
               this.icons.update2();

               for(StructuresModule.ScreenMarker screenMarker : onScreen) {
                  screenMarker.icon = this.icons.process(screenMarker.marker.icon);
               }

               ArrayList commands = new ArrayList();
               this.icons.process2(scale, commands);
               if (!commands.isEmpty()) {
                  WexSideClient.getRenderPipeline2().setList(commands);
               }

               Matrix4f matrix = new Matrix4f().scale(scale);
               GuiDrawApi renderer = WexSideClient.getHudRenderer();
               renderer.begin();

               try {
                  for(StructuresModule.ScreenMarker screenMarker : onScreen) {
                     this.drawMarker(renderer, matrix, screenMarker);
                  }
               } finally {
                  renderer.end();
               }

               this.icons.update();
            }
         }
      } else {
         this.icons.update3();
      }
   }

   private void finishBurst(StructuresModule.ParticleBurst burst, Set<class_2338> blocks, List<StructuresModule.WorldEventHit> extras) {
      if (class_310.method_1551().field_1687 != null) {
         class_243 center = this.centroid(blocks);
         if (center != null) {
            class_1799 icon = new class_1799(class_1802.field_20384);
            String name = extras.isEmpty() ? "Trap" : "Plast";
            this.markers
               .add(
                  new StructuresModule.StructureMarker(center.field_1352, center.field_1351, center.field_1350, name, icon, System.currentTimeMillis() + 30000L)
               );
            NotificationCenter overlays = WexSideClient.getNotificationCenter();
            if (overlays != null) {
               overlays.push(new ItemNotification(icon));
            }
         }
      }
   }

   private void drawMarker(GuiDrawApi renderer, Matrix4f matrix, StructuresModule.ScreenMarker screenMarker) {
      StructuresModule.StructureMarker marker = screenMarker.marker;
      String name = marker.name;
      int seconds = marker.remainingSeconds();
      String timer = seconds + "с";
      float nameWidth = FontRegistry.font2.process3(name + " ", 7.0F);
      float timerWidth = FontRegistry.font2.process3(timer, 7.0F);
      float width = 19.0F + nameWidth + timerWidth + 5.0F;
      float x = screenMarker.x - width / 2.0F;
      float y = screenMarker.y - 8.0F;
      renderer.drawRoundedRectangle(matrix, x, y, width, 16.0F, 8.0F, ThemeColors.separator());
      float iconX = x + 5.0F;
      float iconY = screenMarker.y - 5.5F;
      this.icons.process3(renderer, matrix, screenMarker.icon, iconX, iconY, 11.0F);
      float textX = iconX + 11.0F + 3.0F;
      float textY = screenMarker.y - 3.5F;
      FontRegistry.font2.process2(matrix, renderer, name + " ", textX, textY, 7.0F, -1710619);
      FontRegistry.font2.process2(matrix, renderer, timer, textX + nameWidth, textY, 7.0F, -45747);
   }

   private class_243 centroid(Set<class_2338> blocks) {
      if (blocks.isEmpty()) {
         return null;
      } else {
         double x = 0.0;
         double y = 0.0;
         double z = 0.0;

         for(class_2338 pos : blocks) {
            x += (double)pos.method_10263() + 0.5;
            y += (double)pos.method_10264() + 0.5;
            z += (double)pos.method_10260() + 0.5;
         }

         int count = blocks.size();
         return new class_243(x / (double)count, y / (double)count, z / (double)count);
      }
   }

   private Vector2f worldToScreen(double x, double y, double z) {
      return RenderProjection.project(new class_243(x, y, z));
   }

   private static final class ParticleBurst {
      final class_2338 pos;
      final Object parameters;

      ParticleBurst(class_2338 pos, Object parameters) {
         this.pos = pos;
         this.parameters = parameters;
      }
   }

   private static final class ScreenMarker {
      final float x;
      final float y;
      final StructuresModule.StructureMarker marker;
      Object icon;

      ScreenMarker(float x, float y, StructuresModule.StructureMarker marker) {
         this.x = x;
         this.y = y;
         this.marker = marker;
      }
   }

   private static final class StructureMarker {
      final double x;
      final double y;
      final double z;
      final String name;
      final class_1799 icon;
      final long expiresAt;

      StructureMarker(double x, double y, double z, String name, class_1799 icon, long expiresAt) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.name = name;
         this.icon = icon;
         this.expiresAt = expiresAt;
      }

      boolean expired() {
         return System.currentTimeMillis() >= this.expiresAt;
      }

      int remainingSeconds() {
         return (int)Math.max(0L, (this.expiresAt - System.currentTimeMillis()) / 1000L);
      }
   }

   private static record WorldEventHit(class_2338 pos, int eventId) {
   }
}
