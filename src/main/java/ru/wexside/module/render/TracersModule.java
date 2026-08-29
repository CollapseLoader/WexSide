package ru.wexside.module.render;

import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_12249;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.misc.FriendList;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.ColorSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.ColorUtils;

public final class TracersModule extends Module implements ConfigSerializable {
   private static final double LOOK_DISTANCE = 75.0;
   private static volatile TracersModule instance;
   private final BooleanSetting enabledSetting;
   private final MultiSelectSetting types;
   private final ColorSetting playersColor;
   private final ColorSetting entitiesColor;
   private final ColorSetting friendsColor;
   private final NumberSetting width;
   private final List<TracersModule.TracerLine> lines = new ArrayList<>();
   private Matrix4f drawMatrix;

   public TracersModule(EventBus eventBus) {
      super(eventBus, "tracers", "Tracers", "Линии до сущностей на экране", ModuleCategory.valueOf("RENDER"));
      instance = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Линии до объекта на экране")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      MultiSelectSetting typesSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("Players", "Entities", "Friends")
            .selectAll(false)
            .optionListEnabled(false)
            .name("Types")
            .id("types")
            .description("Выбранные сущности"))
         .build();
      this.types = typesSetting;
      this.registerSetting(typesSetting);
      ColorSetting players = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(0)
            .name("Players Color")
            .id("players_color")
            .description("Цвет линий до игроков")
            .visibleWhen(() -> this.hasType("Players")))
         .build();
      this.applyPalette(players);
      this.playersColor = players;
      this.registerSetting(players);
      ColorSetting entities = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(0)
            .name("Entities Color")
            .id("entities_color")
            .description("Цвет линий до мобов")
            .visibleWhen(() -> this.hasType("Entities")))
         .build();
      this.applyPalette(entities);
      this.entitiesColor = entities;
      this.registerSetting(entities);
      ColorSetting friends = ((ColorSettingBuilder)ColorSetting.builder()
            .selectedIndex(0)
            .name("Friends Color")
            .id("friends_color")
            .description("Цвет линий до друзей")
            .visibleWhen(() -> this.hasType("Friends")))
         .build();
      this.applyPalette(friends);
      this.friendsColor = friends;
      this.registerSetting(friends);
      this.width = ((NumberSettingBuilder)NumberSetting.builder()
            .range(1.0, 2.0)
            .defaultValue(1.0)
            .multiplier(1.0)
            .precision(1)
            .animationSpeed(20.0F)
            .snapTo(0.5)
            .name("Width")
            .id("width")
            .description("Толщина линий")
            .aliases("width", "толщина"))
         .build();
      this.registerSetting(this.width);
   }

   @Override
   protected void initialize() {
      this.listen(WorldRenderEvent.class, this::collectLines);
   }

   public static void tick2() {
      TracersModule module = instance;
      if (module != null) {
         module.drawLines();
      }
   }

   private void collectLines(WorldRenderEvent event) {
      this.lines.clear();
      this.drawMatrix = null;
      if (this.enabledSetting.isEnabled()) {
         class_310 client = class_310.method_1551();
         if (client.field_1687 != null && client.field_1724 != null) {
            class_243 cameraPos = client.field_1773.method_19418().method_71156();
            if (cameraPos != null) {
               boolean players = this.hasType("Players");
               boolean entities = this.hasType("Entities");
               boolean friends = this.hasType("Friends");
               if (players || entities || friends) {
                  int playerColor = this.playersColor.getColor(0.0F);
                  int entityColor = this.entitiesColor.getColor(0.0F);
                  int friendColor = this.friendsColor.getColor(0.0F);
                  float tickDelta = event.getFloatType();

                  for(class_1297 entity : client.field_1687.method_18112()) {
                     Integer color = this.colorFor(entity, players, entities, friends, playerColor, entityColor, friendColor);
                     if (color != null) {
                        class_243 offset = entity.method_30950(tickDelta).method_1031(0.0, (double)(entity.method_17682() / 2.0F), 0.0).method_1020(cameraPos);
                        this.lines.add(new TracersModule.TracerLine(offset, color));
                     }
                  }

                  this.drawMatrix = new Matrix4f(event.getMatrices().method_23760().method_23761());
               }
            }
         }
      }
   }

   private void drawLines() {
      if (this.drawMatrix == null || this.lines.isEmpty()) {
         this.lines.clear();
         return;
      }
      class_310 client = class_310.method_1551();
      if (client.field_1687 == null || client.field_1724 == null) {
         this.lines.clear();
         return;
      }
      class_4184 camera = client.field_1773.method_19418();
      class_243 cameraPos = camera.method_71156();
      class_243 look = class_243.method_1030(camera.method_19329(), camera.method_19330()).method_1021(75.0);
      Matrix4f matrix = this.drawMatrix;
      boolean previousDepthTest = org.lwjgl.opengl.GL11.glIsEnabled(2929);
      boolean previousDepthMask = org.lwjgl.opengl.GL11.glGetBoolean(2930);
      boolean previousBlend = org.lwjgl.opengl.GL11.glIsEnabled(3042);

      try {
         com.mojang.blaze3d.opengl.GlStateManager._enableDepthTest();
         com.mojang.blaze3d.opengl.GlStateManager._enableBlend();
         com.mojang.blaze3d.opengl.GlStateManager._blendFuncSeparate(770, 771, 770, 771);
         com.mojang.blaze3d.opengl.GlStateManager._depthMask(false);
         class_287 consumer = class_289.method_1348().method_60827(class_5596.field_29344, class_290.field_1576);

         for(TracersModule.TracerLine line : this.lines) {
            class_243 start = look.method_1020(cameraPos);
            class_243 delta = line.offset.method_1020(start);
            if (!(delta.method_1027() < 1.0E-4)) {
               int[] rgba = ColorUtils.unpackRgba(line.color);
               consumer.method_22918(matrix, (float)start.field_1352, (float)start.field_1351, (float)start.field_1350)
                  .method_1336(rgba[0], rgba[1], rgba[2], rgba[3]);
               consumer.method_22918(matrix, (float)line.offset.field_1352, (float)line.offset.field_1351, (float)line.offset.field_1350)
                  .method_1336(rgba[0], rgba[1], rgba[2], rgba[3]);
            }
         }

         class_12249.method_76015().method_60895(consumer.method_60800());
      } finally {
         if (previousDepthTest) {
            com.mojang.blaze3d.opengl.GlStateManager._enableDepthTest();
         } else {
            com.mojang.blaze3d.opengl.GlStateManager._disableDepthTest();
         }
         com.mojang.blaze3d.opengl.GlStateManager._depthMask(previousDepthMask);
         if (previousBlend) {
            com.mojang.blaze3d.opengl.GlStateManager._enableBlend();
         } else {
            com.mojang.blaze3d.opengl.GlStateManager._disableBlend();
         }
      }

      this.lines.clear();
   }

   private Integer colorFor(class_1297 entity, boolean players, boolean entities, boolean friends, int playerColor, int entityColor, int friendColor) {
      class_310 client = class_310.method_1551();
      if (entity == null || entity == client.field_1724 || entity == client.method_1560() || !entity.method_5805()) {
         return null;
      } else if (entity instanceof class_1657 player) {
         if (this.isFriend(player)) {
            return friends ? friendColor : null;
         } else {
            return players ? playerColor : null;
         }
      } else if (entity instanceof class_1309) {
         return entities ? entityColor : null;
      } else {
         return null;
      }
   }

   private boolean isFriend(class_1657 player) {
      FriendList friends = WexSideClient.getFriends();
      return friends != null && friends.contains(player.method_5477().getString());
   }

   private boolean hasType(String type) {
      return this.types.getSelectedOptions().contains(type);
   }

   private void applyPalette(ColorSetting setting) {
      setting.setPrimaryColor(0, -11753627);
      setting.setPrimaryColor(1, -1543135);
      setting.setPrimaryColor(2, -9279489);
      setting.setPrimaryColor(3, -46001);
      setting.setPrimaryColor(4, -13218);
      setting.setPrimaryColor(5, -10582785);
      setting.setPrimaryColor(6, -2732032);
   }

   private static record TracerLine(class_243 offset, int color) {
   }
}
