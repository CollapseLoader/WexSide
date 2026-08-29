package ru.wexside.module.render;

import java.util.List;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.ThemeColors;
import ru.wexside.misc.Waypoint;
import ru.wexside.misc.WaypointStore;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.render.RenderProjection;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.MsdfFontRenderer;

public final class WaypointsModule extends Module implements ConfigSerializable {
   private static final String CLOCK_ICON = "h";
   private static final String DISTANCE_ICON = "Щ";
   private static final String ELLIPSIS = "...";
   private static final float CARD_HEIGHT = 30.0F;
   private static final float CARD_RADIUS = 8.0F;
   private static final float MIN_WIDTH = 80.0F;
   private static final float MAX_WIDTH = 120.0F;
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("Отображает метки в мире")
         .withKeybind()
         .toggle())
      .build();

   public WaypointsModule(EventBus eventBus) {
      super(eventBus, "waypoints", "Waypoints", "Отображает метки в мире", ModuleCategory.valueOf("RENDER"));
      this.registerSetting(this.enabledSetting);
   }

   @Override
   protected void initialize() {
      this.listen(HudRenderEvent.class, this::onHudRender);
   }

   private void onHudRender(HudRenderEvent event) {
      if (this.enabledSetting.isEnabled()) {
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         WaypointStore store = WexSideClient.getWaypointStore();
         class_243 cameraPos = client.field_1773.method_19418().method_71156();
         if (player != null && client.field_1687 != null && store != null && cameraPos != null) {
            List<Waypoint> waypoints = store.getWaypoints();
            if (!waypoints.isEmpty()) {
               int titleColor = ThemeColors.textPrimary();
               int nameColor = ThemeColors.textPrimary();
               int accent = ThemeColors.textSecondary();
               int chip = ThemeColors.separator();
               float scale = (float)client.method_22683().method_4495();
               float inverseScale = 2.0F / scale;
               Matrix4f matrix = new Matrix4f().scale(scale);
               GuiDrawApi renderer = WexSideClient.getHudRenderer();
               class_243 playerPos = new class_243(player.method_23317(), player.method_23318(), player.method_23321());
               renderer.begin();

               try {
                  for(Waypoint waypoint : waypoints) {
                     class_243 world = new class_243((double)waypoint.x() + 0.5, (double)waypoint.y() + 0.5, (double)waypoint.z() + 0.5);
                     Vector2f screen = this.worldToScreen(world);
                     if (screen != null) {
                        String name = waypoint.name();
                        String kind = String.valueOf(waypoint.type()).toLowerCase().contains("event") ? "Event" : "Waypoint";
                        int meters = (int)playerPos.method_1022(world);
                        String distance = meters + "m";
                        float nameWidth = FontRegistry.font4.process3(name, 6.5F);
                        float distanceWidth = FontRegistry.font5.process3(distance, 5.5F);
                        float chipWidth = 18.5F + distanceWidth;
                        float contentWidth = 5.0F + nameWidth + 6.0F + chipWidth;
                        float width = Math.max(80.0F, Math.min(120.0F, contentWidth));
                        float x = screen.x - width / 2.0F;
                        float y = screen.y - 15.0F;
                        float nameBudget = width - 5.0F - 6.0F - chipWidth;
                        String clipped = this.clip(FontRegistry.font4, name, 6.5F, nameBudget);
                        renderer.drawRoundedRectangle(matrix, x, y, width, 30.0F, 8.0F, chip);
                        FontRegistry.font7.process2(matrix, renderer, kind, x + 5.0F, y + 4.75F, 6.75F, titleColor);
                        FontRegistry.font3.process5(matrix, renderer, "h", x + width - 11.0F, y + 4.0F, 6.0F, titleColor);
                        FontRegistry.font4.process2(matrix, renderer, clipped, x + 5.0F, y + 30.0F - 12.5F, 6.5F, nameColor);
                        renderer.drawRoundedOutline(
                           matrix, x + width - 18.5F - distanceWidth, y + 30.0F - 14.0F, 13.5F + distanceWidth, 10.0F, 6.0F, inverseScale, chip
                        );
                        FontRegistry.font3.process5(matrix, renderer, "Щ", x + width - 15.0F - distanceWidth, y + 30.0F - 11.0F, 4.0F, accent);
                        FontRegistry.font5.process2(matrix, renderer, distance, x + width - 8.0F - distanceWidth, y + 30.0F - 12.5F, 5.5F, accent);
                     }
                  }
               } finally {
                  renderer.end();
               }
            }
         }
      }
   }

   private String clip(MsdfFontRenderer font, String text, float size, float maxWidth) {
      if (text != null && !text.isEmpty() && !(maxWidth <= 0.0F)) {
         if (font.process3(text, size) <= maxWidth) {
            return text;
         } else {
            float ellipsisWidth = font.process3("...", size);
            if (maxWidth <= ellipsisWidth) {
               return "...";
            } else {
               int end = text.length();

               while(end > 0 && font.process3(text.substring(0, end), size) + ellipsisWidth > maxWidth) {
                  --end;
               }

               return text.substring(0, end) + "...";
            }
         }
      } else {
         return "";
      }
   }

   private Vector2f worldToScreen(class_243 world) {
      return RenderProjection.project(world);
   }
}
