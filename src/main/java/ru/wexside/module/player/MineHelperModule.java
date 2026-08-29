package ru.wexside.module.player;

import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_12249;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_2338.class_2339;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.misc.ElapsedTimer;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.render.BakedItemIcon;
import ru.wexside.render.ItemIconCache;
import ru.wexside.render.RenderProjection;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;
import ru.wexside.util.GuiDrawApi;

public class MineHelperModule extends Module implements ConfigSerializable {
   private static final int SCAN_SLICES = 2;
   private static final long SCAN_INTERVAL_MS = 1000L;
   private static final Map<class_2248, MineHelperModule.OreGroup> ORES = new HashMap<>();
   private final BooleanSetting enabledSetting;
   private final MultiSelectSetting ores;
   private final NumberSetting scanRadius;
   private final BooleanSetting renderMaterial;
   private final ItemIconCache iconCache = new ItemIconCache();
   private final ElapsedTimer scanTimer = new ElapsedTimer();
   private List<MineHelperModule.OreHit> scanBuffer;
   private List<MineHelperModule.OreHit> found = List.of();
   private class_2338 scanOrigin;
   private int scanOffset;
   private int scanExtent;

   public MineHelperModule(EventBus eventBus) {
      super(eventBus, "mine_helper", "Mine Helper", "Сканирует территорию в поиске руд", ModuleCategory.valueOf("PLAYER"));
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      MultiSelectSetting oresSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options("Diamond", "Emerald", "Gold", "Iron", "Debris", "Lapis", "Quartz", "Redstone", "Coal")
            .selectAll(true)
            .optionListEnabled(false)
            .name("Ores")
            .id("ores")
            .description("Какие руды искать")
            .aliases("ores", "руды"))
         .build();
      oresSetting.setOptions(new String[]{"Diamond", "Emerald", "Gold", "Iron", "Debris", "Lapis", "Quartz", "Redstone", "Coal"});
      this.ores = oresSetting;
      this.registerSetting(oresSetting);
      this.scanRadius = ((NumberSettingBuilder)NumberSetting.builder()
            .range(5.0, 30.0)
            .defaultValue(15.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Scan Radius")
            .id("scan_radius")
            .description("Радиус сканирования")
            .aliases("scan radius", "радиус"))
         .build();
      this.registerSetting(this.scanRadius);
      this.renderMaterial = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Render Material")
            .id("render_material")
            .description("Отображать выпадаемый материал над рудой")
            .aliases("render material", "материал"))
         .build();
      this.registerSetting(this.renderMaterial);
   }

   @Override
   protected void initialize() {
      this.listen(WorldRenderEvent.class, this::onWorldRender);
      this.listen(HudRenderEvent.class, event -> this.onHudRender());
   }

   private List<MineHelperModule.OreHit> visibleOres() {
      this.scanSlice();
      return this.found;
   }

   private void drawBox(class_287 consumer, Matrix4f matrix, class_243 cameraPos, class_238 box, int color) {
      double minX = box.field_1323;
      double minY = box.field_1322;
      double minZ = box.field_1321;
      double maxX = box.field_1320;
      double maxY = box.field_1325;
      double maxZ = box.field_1324;
      this.drawLine(consumer, matrix, cameraPos, minX, minY, minZ, maxX, minY, minZ, color);
      this.drawLine(consumer, matrix, cameraPos, maxX, minY, minZ, maxX, minY, maxZ, color);
      this.drawLine(consumer, matrix, cameraPos, maxX, minY, maxZ, minX, minY, maxZ, color);
      this.drawLine(consumer, matrix, cameraPos, minX, minY, maxZ, minX, minY, minZ, color);
      this.drawLine(consumer, matrix, cameraPos, minX, maxY, minZ, maxX, maxY, minZ, color);
      this.drawLine(consumer, matrix, cameraPos, maxX, maxY, minZ, maxX, maxY, maxZ, color);
      this.drawLine(consumer, matrix, cameraPos, maxX, maxY, maxZ, minX, maxY, maxZ, color);
      this.drawLine(consumer, matrix, cameraPos, minX, maxY, maxZ, minX, maxY, minZ, color);
      this.drawLine(consumer, matrix, cameraPos, minX, minY, minZ, minX, maxY, minZ, color);
      this.drawLine(consumer, matrix, cameraPos, maxX, minY, minZ, maxX, maxY, minZ, color);
      this.drawLine(consumer, matrix, cameraPos, maxX, minY, maxZ, maxX, maxY, maxZ, color);
      this.drawLine(consumer, matrix, cameraPos, minX, minY, maxZ, minX, maxY, maxZ, color);
   }

   private void drawLine(class_287 consumer, Matrix4f matrix, class_243 cameraPos, double x1, double y1, double z1, double x2, double y2, double z2, int color) {
      int red = color >> 16 & 0xFF;
      int green = color >> 8 & 0xFF;
      int blue = color & 0xFF;
      int alpha = color >>> 24 & 0xFF;
      if (alpha == 0) {
         alpha = 255;
      }

      consumer.method_22918(matrix, (float)(x1 - cameraPos.field_1352), (float)(y1 - cameraPos.field_1351), (float)(z1 - cameraPos.field_1350))
         .method_1336(red, green, blue, alpha);
      consumer.method_22918(matrix, (float)(x2 - cameraPos.field_1352), (float)(y2 - cameraPos.field_1351), (float)(z2 - cameraPos.field_1350))
         .method_1336(red, green, blue, alpha);
   }

   private void onWorldRender(WorldRenderEvent event) {
      class_310 client = class_310.method_1551();
      class_4184 camera = client.field_1773.method_19418();
      class_243 cameraPos = camera.method_71156();
      if (this.enabledSetting.isEnabled() && client.field_1687 != null && client.field_1724 != null && cameraPos != null) {
         List<String> selected = this.ores.getSelectedOptions();
         ArrayList<MineHelperModule.EspBox> boxes = new ArrayList<>();

         for(MineHelperModule.OreHit hit : this.visibleOres()) {
            class_238 box = this.blockBox(hit.pos());
            if (selected.contains(hit.group().name()) && this.inView(box, cameraPos)) {
               boxes.add(new MineHelperModule.EspBox(box, hit.group().color()));
            }
         }

         if (!boxes.isEmpty()) {
            Matrix4f matrix = new Matrix4f().rotation(camera.method_23767());
            class_287 consumer = class_289.method_1348().method_60827(class_5596.field_29344, class_290.field_1576);

            for(MineHelperModule.EspBox box : boxes) {
               this.drawBox(consumer, matrix, cameraPos, box.box(), box.color());
            }

            class_12249.method_76015().method_60895(consumer.method_60800());
         }
      }
   }

   private void onHudRender() {
      if (this.enabledSetting.isEnabled() && this.renderMaterial.isEnabled()) {
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         class_4184 camera = client.field_1773.method_19418();
         class_243 cameraPos = camera.method_71156();
         if (client.field_1687 != null && player != null && cameraPos != null) {
            List<String> selected = this.ores.getSelectedOptions();
            Matrix4f projection = RenderProjection.viewProjectionMatrix();
            this.iconCache.update2();
            ArrayList<MineHelperModule.ScreenIcon> icons = new ArrayList<>();

            for(MineHelperModule.OreHit hit : this.visibleOres()) {
               if (selected.contains(hit.group().name())) {
                  class_2338 pos = hit.pos();
                  double x = (double)pos.method_10263() + 0.5;
                  double y = (double)pos.method_10264() + 0.5;
                  double z = (double)pos.method_10260() + 0.5;
                  class_238 box = this.blockBox(pos);
                  if (this.inView(box, cameraPos)) {
                     Vector2f screen = this.worldToScreen(x, y, z, projection);
                     if (screen != null) {
                        float size = this.iconSize(player.method_5649(x, y, z));
                        icons.add(new MineHelperModule.ScreenIcon(screen.x, screen.y, size, hit.group().icon()));
                     }
                  }
               }
            }

            if (icons.isEmpty()) {
               this.iconCache.update();
            } else {
               float scale = (float)client.method_22683().method_4495();
               ArrayList<BakedItemIcon> bakedIcons = new ArrayList<>();

               for(MineHelperModule.ScreenIcon icon : icons) {
                  bakedIcons.add(this.iconCache.process(icon.stack()));
               }

               ArrayList commands = new ArrayList();
               this.iconCache.process2(scale, commands);
               if (!commands.isEmpty()) {
                  WexSideClient.getRenderPipeline2().setList(commands);
               }

               GuiDrawApi renderer = WexSideClient.getHudRenderer();
               Matrix4f scaled = new Matrix4f().scale(scale);
               renderer.begin();

               try {
                  for(int index = 0; index < icons.size(); ++index) {
                     MineHelperModule.ScreenIcon icon = icons.get(index);
                     this.iconCache
                        .process3(renderer, scaled, bakedIcons.get(index), icon.x() - icon.size() / 2.0F, icon.y() - icon.size() / 2.0F, icon.size());
                  }
               } finally {
                  renderer.end();
               }

               this.iconCache.update();
            }
         }
      } else {
         this.iconCache.update3();
      }
   }

   private float iconSize(double squaredDistance) {
      double distance = Math.max(Math.sqrt(squaredDistance), 4.0);
      return class_3532.method_15363((float)(104.0 / distance), 5.0F, 22.0F);
   }

   private static void registerOre(String name, int color, class_1792 icon, class_2248... blocks) {
      MineHelperModule.OreGroup group = new MineHelperModule.OreGroup(name, color | 0xFF000000, new class_1799(icon));

      for(class_2248 block : blocks) {
         ORES.put(block, group);
      }
   }

   private void scanSlice() {
      class_310 client = class_310.method_1551();
      class_638 world = client.field_1687;
      class_746 player = client.field_1724;
      if (world != null && player != null) {
         if (this.scanOrigin == null) {
            if (!this.scanTimer.process(1000L)) {
               return;
            }

            this.scanOrigin = player.method_24515();
            this.scanExtent = this.scanRadius.getIntValue();
            this.scanOffset = -this.scanExtent;
            this.scanBuffer = new ArrayList<>();
         }

         class_2339 mutable = new class_2339();

         for(int slices = 0; slices < 2 && this.scanOffset <= this.scanExtent; ++this.scanOffset) {
            int x = this.scanOrigin.method_10263() + this.scanOffset;

            for(int yOff = -this.scanExtent; yOff <= this.scanExtent; ++yOff) {
               for(int zOff = -this.scanExtent; zOff <= this.scanExtent; ++zOff) {
                  mutable.method_10103(x, this.scanOrigin.method_10264() + yOff, this.scanOrigin.method_10260() + zOff);
                  MineHelperModule.OreGroup group = ORES.get(world.method_8320(mutable).method_26204());
                  if (group != null) {
                     this.scanBuffer.add(new MineHelperModule.OreHit(mutable.method_10062(), group));
                  }
               }
            }

            ++slices;
         }

         if (this.scanOffset > this.scanExtent) {
            this.found = this.scanBuffer;
            this.scanBuffer = null;
            this.scanOrigin = null;
            this.scanTimer.update();
         }
      }
   }

   private class_238 blockBox(class_2338 pos) {
      return new class_238(
         (double)pos.method_10263(),
         (double)pos.method_10264(),
         (double)pos.method_10260(),
         (double)pos.method_10263() + 1.0,
         (double)pos.method_10264() + 1.0,
         (double)pos.method_10260() + 1.0
      );
   }

   private boolean inView(class_238 box, class_243 cameraPos) {
      return cameraPos.method_1025(box.method_1005()) < 4096.0;
   }

   private Vector2f worldToScreen(double x, double y, double z, Matrix4f matrix) {
      return RenderProjection.project(x, y, z, matrix);
   }

   static {
      registerOre("Diamond", 4910553, class_1802.field_8477, class_2246.field_10442, class_2246.field_29029);
      registerOre("Emerald", 1564002, class_1802.field_8687, class_2246.field_10013, class_2246.field_29220);
      registerOre("Gold", 16576075, class_1802.field_8695, class_2246.field_10571, class_2246.field_29026, class_2246.field_23077);
      registerOre("Iron", 14200723, class_1802.field_8620, class_2246.field_10212, class_2246.field_29027);
      registerOre("Debris", 6637376, class_1802.field_22021, class_2246.field_22109);
      registerOre("Lapis", 1395125, class_1802.field_8759, class_2246.field_10090, class_2246.field_29028);
      registerOre("Quartz", 15130844, class_1802.field_8155, class_2246.field_10213);
      registerOre("Redstone", 16711680, class_1802.field_8725, class_2246.field_10080, class_2246.field_29030);
      registerOre("Coal", 3552822, class_1802.field_8713, class_2246.field_10418, class_2246.field_29219);
   }

   private static record EspBox(class_238 box, int color) {
   }

   private static record OreGroup(String name, int color, class_1799 icon) {
   }

   private static record OreHit(class_2338 pos, MineHelperModule.OreGroup group) {
   }

   private static record ScreenIcon(float x, float y, float size, class_1799 stack) {
   }
}
