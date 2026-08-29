package ru.wexside.module.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1839;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.BakedIconEntry;
import ru.wexside.misc.FontRegistry;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.render.BakedItemIcon;
import ru.wexside.render.ItemIconCache;
import ru.wexside.render.RenderProjection;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.util.GuiDrawApi;

public final class DurationVisualiserModule extends Module implements ConfigSerializable {
   private static final float ICON_SIZE = 20.0F;
   private static final float ICON_HALF = 10.0F;
   private static final float RING_RADIUS = 10.0F;
   private static final float RING_WIDTH = 3.0F;
   private static final float LABEL_SIZE = 7.0F;
   private static final float LABEL_OFFSET = 14.0F;
   private static final int BACKDROP_COLOR = 1073741824;
   private static final int RING_COLOR = -1;
   private static final double TRACK_DISTANCE_SQ = 1024.0;
   private final BooleanSetting enabledSetting;
   private final ItemIconCache iconCache = new ItemIconCache();
   private final Map<class_1657, DurationVisualiserModule.UseTracker> trackers = new HashMap<>();

   public DurationVisualiserModule(EventBus eventBus) {
      super(
         eventBus, "duration_visualiser", "Duration Visualiser", "Показывает время использования предмета у соседних игроков", ModuleCategory.valueOf("RENDER")
      );
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Визуализирует время использования предмета")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
   }

   @Override
   protected void initialize() {
      this.listen(HudRenderEvent.class, this::onHudRender);
      this.listen(WorldSessionEvent.class, event -> {
         this.trackers.clear();
         this.iconCache.update3();
      });
   }

   private void onHudRender(HudRenderEvent event) {
      if (!this.enabledSetting.isEnabled()) {
         if (!this.trackers.isEmpty()) {
            this.trackers.clear();
         }

         this.iconCache.update3();
      } else {
         class_310 client = class_310.method_1551();
         if (client.field_1724 != null && client.field_1687 != null) {
            this.updateTrackers(client.field_1724);
            Matrix4f viewProjection = RenderProjection.viewProjectionMatrix();
            float scale = (float)client.method_22683().method_4495();
            Matrix4f guiMatrix = new Matrix4f().scale(scale);
            long now = System.currentTimeMillis();
            ArrayList<DurationVisualiserModule.HudEntry> entries = new ArrayList<>();
            Iterator<Entry<class_1657, DurationVisualiserModule.UseTracker>> iterator = this.trackers.entrySet().iterator();

            while(iterator.hasNext()) {
               Entry<class_1657, DurationVisualiserModule.UseTracker> entry = iterator.next();
               class_1657 player = (class_1657)entry.getKey();
               DurationVisualiserModule.UseTracker tracker = entry.getValue();
               long remaining = tracker.expiresAt - now;
               if (remaining > 0L && player.method_6115()) {
                  Vector2f screen = RenderProjection.projectEntityCenter(player, viewProjection);
                  if (screen != null) {
                     entries.add(new DurationVisualiserModule.HudEntry(screen.x, screen.y, remaining, tracker.totalDuration, player.method_6030()));
                  }
               } else {
                  iterator.remove();
               }
            }

            if (!entries.isEmpty()) {
               this.iconCache.update2();

               for(DurationVisualiserModule.HudEntry hudEntry : entries) {
                  hudEntry.icon = this.iconCache.process(hudEntry.stack);
               }

               ArrayList<BakedIconEntry> bakes = new ArrayList<>();
               this.iconCache.process2(scale, bakes);
               if (!bakes.isEmpty()) {
                  WexSideClient.getRenderPipeline2().setList(bakes);
               }

               GuiDrawApi renderer = WexSideClient.getHudRenderer();
               renderer.begin();

               try {
                  for(DurationVisualiserModule.HudEntry hudEntry : entries) {
                     float x = hudEntry.x;
                     float y = hudEntry.y;
                     renderer.drawRoundedRectangleBordered(guiMatrix, x - 10.0F, y - 10.0F, 20.0F, 20.0F, 20.0F * scale, 0.0F, 1073741824);
                     if (hudEntry.icon != null) {
                        this.iconCache.process3(renderer, guiMatrix, hudEntry.icon, x - 5.6F, y - 5.6F, 11.2F);
                     }

                     float progress = (float)hudEntry.remaining / (float)hudEntry.totalDuration;
                     renderer.drawCircle(guiMatrix, x, y, 0.0F, progress * 360.0F, 3.0F, 10.0F, -1);
                     String label = String.format(Locale.US, "%.1f сек.", (float)hudEntry.remaining / 1000.0F);
                     float width = FontRegistry.font6.process3(label, 7.0F);
                     FontRegistry.font6.process2(guiMatrix, renderer, label, x - width / 2.0F, y + 14.0F, 7.0F, -1);
                  }
               } finally {
                  renderer.end();
               }

               this.iconCache.update();
            }
         }
      }
   }

   private void updateTrackers(class_746 local) {
      class_310 client = class_310.method_1551();

      for(class_1657 player : client.field_1687.method_18456()) {
         if (!(player.method_5858(local) > 1024.0) && player.method_6115()) {
            class_1799 stack = player.method_6030();
            class_1839 action = stack.method_7976();
            if (!stack.method_7960() && (action == class_1839.field_8950 || action == class_1839.field_8946)) {
               int maxUseTime = stack.method_7935(player);
               int useTime = player.method_6048();
               if (useTime == maxUseTime - 1) {
                  long duration = (long)maxUseTime * 50L;
                  this.trackers.put(player, new DurationVisualiserModule.UseTracker(System.currentTimeMillis() + duration, duration));
               }
            } else {
               this.trackers.remove(player);
            }
         } else {
            this.trackers.remove(player);
         }
      }
   }

   static final class HudEntry {
      final float x;
      final float y;
      final long remaining;
      final long totalDuration;
      final class_1799 stack;
      BakedItemIcon icon;

      HudEntry(float x, float y, long remaining, long totalDuration, class_1799 stack) {
         this.x = x;
         this.y = y;
         this.remaining = remaining;
         this.totalDuration = totalDuration;
         this.stack = stack;
      }
   }

   static final class UseTracker {
      final long expiresAt;
      final long totalDuration;

      UseTracker(long expiresAt, long totalDuration) {
         this.expiresAt = expiresAt;
         this.totalDuration = totalDuration;
      }
   }
}
