package ru.wexside.module.hud;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.class_1041;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1676;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.misc.ArrowStyle;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FriendList;
import ru.wexside.misc.Gps;
import ru.wexside.misc.HandledScreenAccessor;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.ModeSetting;
import ru.wexside.setting.ModeSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.entity.NpcDetector;

public final class ArrowsModule extends Module implements ConfigSerializable {
   private static final String GROUP_PLAYERS = "Players";
   private static final String GROUP_ENTITIES = "Entities";
   private static final String GROUP_ITEMS = "Items";
   private static final String GROUP_FRIENDS = "Friends";
   private static final float GPS_CLAMP_PADDING = 48.0F;
   private static final float ENTITY_CLAMP_PADDING = 28.0F;
   private static final float TARGET_CLAMP_PADDING = 20.0F;
   private static final float DISTANCE_TEXT_SIZE = 6.5F;
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("Стрелки к объектам на экране")
         .withKeybind()
         .toggle())
      .build();
   private final ModeSetting style;
   private final MultiSelectSetting groups;
   private final NumberSetting size;
   private final NumberSetting radius;
   private final BooleanSetting showDistance;
   private final BooleanSetting showHealth;
   private final BooleanSetting overlap;
   private final NumberSetting threshold;
   private final ColorSetting playersColor;
   private final ColorSetting entitiesColor;
   private final ColorSetting itemsColor;
   private final ColorSetting friendsColor;
   private final ColorSetting gpsColor;

   public ArrowsModule(EventBus eventBus) {
      super(eventBus, "arrows", "Arrows", "Стрелки-указатели к объектам и GPS-координатам", ModuleCategory.valueOf("DISPLAY"), "arrows", "стрелки");
      this.registerSetting(this.enabledSetting);
      this.style = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder()
            .options("Triangle", "Classic")
            .defaultOption("Classic")
            .name("Style")
            .id("style")
            .description("Текстура стрелки"))
         .build();
      this.registerSetting(this.style);
      MultiSelectSetting groupsSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("Players", "Entities", "Items", "Friends")
            .selectAll(false)
            .optionListEnabled(false)
            .name("Groups")
            .id("groups")
            .description("Какие цели показывать"))
         .build();
      groupsSetting.setOptions(new String[]{"Players", "Entities"});
      this.groups = groupsSetting;
      this.registerSetting(groupsSetting);
      this.size = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.5, 1.0)
            .defaultValue(0.7)
            .multiplier(1.0)
            .precision(1)
            .animationSpeed(20.0F)
            .name("Size")
            .id("size")
            .description("Размер стрелок"))
         .build();
      this.registerSetting(this.size);
      this.radius = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 10.0)
            .defaultValue(4.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Radius")
            .id("radius")
            .description("Радиус круга стрелок"))
         .build();
      this.registerSetting(this.radius);
      this.showDistance = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Show Distance")
            .id("show_distance")
            .description("Отображать дистанцию"))
         .build();
      this.registerSetting(this.showDistance);
      this.showHealth = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Show Health")
            .id("show_health")
            .description("Отображать здоровье"))
         .build();
      this.registerSetting(this.showHealth);
      this.overlap = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Hide Overlap")
            .id("overlap")
            .description("Скрывать перекрывающиеся стрелки"))
         .build();
      this.registerSetting(this.overlap);
      this.threshold = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 100.0)
            .defaultValue(50.0)
            .multiplier(0.01)
            .precision(0)
            .animationSpeed(20.0F)
            .markers(10.0)
            .name("Threshold")
            .id("threshold")
            .description("Сила скрытия: выше — прячет даже при слабом перекрытии стрелок")
            .aliases("threshold", "порог")
            .visibleWhen(this.overlap::isEnabled))
         .build();
      this.registerSetting(this.threshold);
      ColorSetting players = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(3)
            .name("Players Color")
            .id("players_color")
            .description("Цвет стрелки, подсвечивающий игрока")
            .visibleWhen(() -> this.groups.getSelectedOptions().contains("Players")))
         .build();
      this.applyPalette(players);
      this.playersColor = players;
      this.registerSetting(players);
      ColorSetting entities = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(4)
            .name("Entities Color")
            .id("entities_color")
            .description("Цвет стрелки, подсвечивающий моба")
            .visibleWhen(() -> this.groups.getSelectedOptions().contains("Entities")))
         .build();
      this.applyPalette(entities);
      this.entitiesColor = entities;
      this.registerSetting(entities);
      ColorSetting items = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(5)
            .name("Items Color")
            .id("items_color")
            .description("Цвет стрелки, подсвечивающий предмет")
            .visibleWhen(() -> this.groups.getSelectedOptions().contains("Items")))
         .build();
      this.applyPalette(items);
      this.itemsColor = items;
      this.registerSetting(items);
      ColorSetting friends = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(6)
            .name("Friends Color")
            .id("friends_color")
            .description("Цвет стрелки, подсвечивающий друга")
            .visibleWhen(() -> this.groups.getSelectedOptions().contains("Friends")))
         .build();
      this.applyPalette(friends);
      this.friendsColor = friends;
      this.registerSetting(friends);
      ColorSetting gps = ((ColorSettingBuilder)ColorSetting.builder().selectedIndex(2).name("GPS Color").id("gps_color").description("Цвет GPS-стрелки"))
         .build();
      this.applyPalette(gps);
      this.gpsColor = gps;
      this.registerSetting(gps);
   }

   @Override
   protected void initialize() {
      this.listen(HudRenderEvent.class, this::onRender);
   }

   private void onRender(HudRenderEvent event) {
      boolean enabled = this.enabledSetting.isEnabled();
      if (enabled || Gps.isActive()) {
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         class_638 world = client.field_1687;
         if (player != null && world != null) {
            class_1041 window = client.method_22683();
            float scale = (float)window.method_4495();
            float centerX = (float)window.method_4486() / 2.0F;
            float centerY = (float)window.method_4502() / 2.0F;
            float tickDelta = client.method_61966().method_60637(true);
            class_4184 camera = client.field_1773.method_19418();
            class_243 cameraPos = camera.method_71156();
            if (cameraPos != null) {
               float yaw = class_3532.method_17821(tickDelta, player.field_5982, player.method_36454());
               double yawRadians = Math.toRadians((double)yaw);
               double forwardX = Math.cos(yawRadians);
               double forwardZ = Math.sin(yawRadians);
               float arrowSize = 18.0F * this.size.getFloatValue();
               class_437 screen = client.field_1755;
               boolean containerOpen = screen instanceof class_465;
               int containerX = 0;
               int containerY = 0;
               int containerWidth = 0;
               int containerHeight = 0;
               float orbitRadius;
               if (containerOpen) {
                  HandledScreenAccessor container = (HandledScreenAccessor)screen;
                  containerX = container.getContainerX();
                  containerY = container.getContainerY();
                  containerWidth = container.getContainerWidth();
                  containerHeight = container.getContainerHeight();
                  orbitRadius = (float)Math.max(containerWidth, containerHeight) / 2.0F;
               } else {
                  orbitRadius = (float)(40.0 + this.radius.getValue() * 3.0);
               }

               float entityOrbitRadius = orbitRadius * 1.5F;
               float gpsOrbitRadius = entityOrbitRadius + 28.0F;
               boolean showPlayers = this.groups.getSelectedOptions().contains("Players");
               boolean showEntities = this.groups.getSelectedOptions().contains("Entities");
               boolean showItems = this.groups.getSelectedOptions().contains("Items");
               boolean showFriends = this.groups.getSelectedOptions().contains("Friends");
               ArrayList<ArrowsModule.EntityTarget> targets = new ArrayList<>();
               if (enabled && (showPlayers || showEntities || showItems || showFriends)) {
                  FriendList friends = WexSideClient.getFriends();
                  NpcDetector npcDetector = WexSideClient.getNpcDetector();

                  for(class_1297 entity : world.method_18112()) {
                     ArrowsModule.ArrowTargetKind kind;
                     if (entity != player
                        && entity.method_5805()
                        && (!(entity instanceof class_1309 living) || npcDetector == null || !npcDetector.isNpc(living))
                        && (kind = this.classify(entity, friends)) != null
                        && this.isGroupEnabled(kind, showPlayers, showEntities, showItems, showFriends)) {
                        targets.add(new ArrowsModule.EntityTarget(entity, kind, (double)player.method_5739(entity)));
                     }
                  }

                  targets.sort(Comparator.comparingDouble(ArrowsModule.EntityTarget::distance));
               }

               ArrayList<ArrowsModule.ArrowRenderState> arrows = new ArrayList<>();
               boolean hideOverlap = this.overlap.isEnabled();
               float overlapThreshold = 1.0F - this.threshold.getFloatValue();

               for(int index = 0; index < targets.size(); ++index) {
                  ArrowsModule.EntityTarget target = targets.get(index);
                  class_243 entityPos = target.entity.method_30950(tickDelta);
                  float angle = computeAngle(entityPos.field_1352 - cameraPos.field_1352, entityPos.field_1350 - cameraPos.field_1350, forwardX, forwardZ);
                  float[] position = computeOrbitPosition(
                     angle, centerX, centerY, entityOrbitRadius, containerOpen, containerX, containerY, containerWidth, containerHeight, 20.0F
                  );
                  float x = position[0];
                  float y = position[1];
                  if (!hideOverlap || !overlapsExisting(arrows, x, y, arrowSize, overlapThreshold)) {
                     float colorMix = targets.size() > 1 ? (float)index / (float)targets.size() : 0.0F;
                     float healthRatio = -1.0F;
                     if (this.showHealth.isEnabled()) {
                        class_1297 entity = target.entity;
                        if (target.entity instanceof class_1309) {
                           class_1309 living = (class_1309)entity;
                           healthRatio = class_3532.method_15363(living.method_6032() / Math.max(living.method_6032(), living.method_6063()), 0.0F, 1.0F);
                        }
                     }

                     arrows.add(
                        new ArrowsModule.ArrowRenderState(
                           x, y, angle, this.colorFor(target.kind, colorMix), (int)target.distance, healthRatio, this.showDistance.isEnabled()
                        )
                     );
                  }
               }

               if (Gps.isActive()) {
                  float gpsAngle = computeAngle((double)Gps.getX() - cameraPos.field_1352, (double)Gps.getZ() - cameraPos.field_1350, forwardX, forwardZ);
                  float[] gpsPosition = computeOrbitPosition(
                     gpsAngle, centerX, centerY, gpsOrbitRadius, containerOpen, containerX, containerY, containerWidth, containerHeight, 48.0F
                  );
                  double dx = (double)Gps.getX() - player.method_23317();
                  double dz = (double)Gps.getZ() - player.method_23321();
                  int gpsDistance = (int)Math.sqrt(dx * dx + dz * dz);
                  arrows.add(new ArrowsModule.ArrowRenderState(gpsPosition[0], gpsPosition[1], gpsAngle, this.gpsColor.getColor(), gpsDistance, -1.0F, true));
               }

               if (!arrows.isEmpty()) {
                  ArrowStyle arrowStyle = ArrowStyle.fromDisplayName(this.style.getSelectedOption());
                  Matrix4f baseMatrix = new Matrix4f().scale(scale);
                  GuiDrawApi renderer = WexSideClient.getHudRenderer();
                  renderer.begin();

                  try {
                     int textureId = renderer.bindTexture(
                        arrowStyle.getTexture().getTextureId(), arrowStyle.getTexture().getWidth(), arrowStyle.getTexture().getHeight()
                     );
                     float halfSize = arrowSize / 2.0F;

                     for(ArrowsModule.ArrowRenderState arrow : arrows) {
                        Matrix4f arrowMatrix = new Matrix4f().scale(scale).translate(arrow.x, arrow.y, 0.0F).rotateZ(arrow.angle);
                        renderer.drawTexture(arrowMatrix, -halfSize, -halfSize, arrowSize, arrowSize, 0.0F, 0.0F, 1.0F, 1.0F, textureId, arrow.color);
                     }

                     for(ArrowsModule.ArrowRenderState arrow : arrows) {
                        if (arrow.healthRatio >= 0.0F) {
                           this.drawHealthBar(renderer, baseMatrix, arrow.x, arrow.y - arrowSize * 0.5F, arrowSize, arrow.healthRatio);
                        }

                        if (arrow.showDistance) {
                           this.drawDistance(renderer, baseMatrix, arrow.x, arrow.y + arrowSize * 0.45F, arrow.distanceMeters);
                        }
                     }
                  } finally {
                     renderer.end();
                  }
               }
            }
         }
      }
   }

   private ArrowsModule.ArrowTargetKind classify(class_1297 entity, FriendList friends) {
      if (entity instanceof class_1531 || entity instanceof class_1676 || entity instanceof class_1308) {
         return ArrowsModule.ArrowTargetKind.ENTITIES;
      } else if (entity instanceof class_1657 player) {
         boolean isFriend = friends != null && friends.contains(class_124.method_539(player.method_5477().getString()));
         return isFriend ? ArrowsModule.ArrowTargetKind.FRIENDS : ArrowsModule.ArrowTargetKind.PLAYERS;
      } else {
         return entity instanceof class_1542 ? ArrowsModule.ArrowTargetKind.ITEMS : null;
      }
   }

   private boolean isGroupEnabled(ArrowsModule.ArrowTargetKind kind, boolean players, boolean entities, boolean items, boolean friends) {
      return switch(kind.ordinal()) {
         case 0 -> players;
         case 1 -> friends;
         case 2 -> entities;
         case 3 -> items;
         default -> throw new MatchException(null, null);
      };
   }

   private int colorFor(ArrowsModule.ArrowTargetKind kind, float mix) {
      return switch(kind.ordinal()) {
         case 0 -> this.playersColor.getColor(mix);
         case 1 -> this.friendsColor.getColor(mix);
         case 2 -> this.entitiesColor.getColor(mix);
         case 3 -> this.itemsColor.getColor(mix);
         default -> throw new MatchException(null, null);
      };
   }

   private static float computeAngle(double deltaX, double deltaZ, double forwardX, double forwardZ) {
      double rotatedX = -(deltaZ * forwardX - deltaX * forwardZ);
      double rotatedZ = -(deltaX * forwardX + deltaZ * forwardZ);
      return (float)Math.atan2(rotatedX, rotatedZ);
   }

   private static float[] computeOrbitPosition(
      float angle,
      float centerX,
      float centerY,
      float radius,
      boolean clampToContainer,
      int containerX,
      int containerY,
      int containerWidth,
      int containerHeight,
      float padding
   ) {
      float x = centerX + radius * (float)Math.cos((double)angle);
      float y = centerY + radius * (float)Math.sin((double)angle);
      if (clampToContainer) {
         x = class_3532.method_15363(x, (float)containerX - padding, (float)(containerX + containerWidth) + padding);
         y = class_3532.method_15363(y, (float)containerY - padding, (float)(containerY + containerHeight) + padding);
      }

      return new float[]{x, y};
   }

   private static float overlapRatio(float left, float top, float size, float otherLeft, float otherTop, float otherSize) {
      float overlapWidth = Math.max(0.0F, Math.min(left + size, otherLeft + otherSize) - Math.max(left, otherLeft));
      float overlapArea = overlapWidth * Math.max(0.0F, Math.min(top + size, otherTop + otherSize) - Math.max(top, otherTop));
      return overlapArea == 0.0F ? 0.0F : overlapArea / Math.min(size * size, otherSize * otherSize);
   }

   private static boolean overlapsExisting(List<ArrowsModule.ArrowRenderState> arrows, float x, float y, float size, float threshold) {
      float half = size / 2.0F;
      float left = x - half;
      float top = y - half;

      for(ArrowsModule.ArrowRenderState existing : arrows) {
         float existingLeft = existing.x - half;
         float existingTop = existing.y - half;
         if (overlapRatio(left, top, size, existingLeft, existingTop, size) > threshold) {
            return true;
         }
      }

      return false;
   }

   private void drawDistance(GuiDrawApi renderer, Matrix4f matrix, float x, float y, int meters) {
      String text = meters + "m";
      float width = FontRegistry.font5.process3(text, 6.5F);
      FontRegistry.font5.process2(matrix, renderer, text, x - width / 2.0F, y, 6.5F, -1);
   }

   private void drawHealthBar(GuiDrawApi renderer, Matrix4f matrix, float x, float y, float size, float ratio) {
      float barWidth = size * 0.7F;
      float barHeight = 1.5F;
      float barX = x - barWidth / 2.0F;
      int fillColor = ratio > 0.5F ? -16711936 : (ratio > 0.25F ? -256 : -65536);
      renderer.fillRectangle(matrix, barX, y, barWidth, barHeight, Integer.MIN_VALUE);
      renderer.fillRectangle(matrix, barX, y, barWidth * ratio, barHeight, fillColor);
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

   private static record ArrowRenderState(float x, float y, float angle, int color, int distanceMeters, float healthRatio, boolean showDistance) {
   }

   private static enum ArrowTargetKind {
      PLAYERS,
      FRIENDS,
      ENTITIES,
      ITEMS;
   }

   private static record EntityTarget(class_1297 entity, ArrowsModule.ArrowTargetKind kind, double distance) {
   }
}
