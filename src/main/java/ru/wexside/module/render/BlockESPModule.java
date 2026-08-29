package ru.wexside.module.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.class_12249;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_7923;
import net.minecraft.class_2338.class_2339;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.BlockEspStore;
import ru.wexside.misc.ElapsedTimer;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class BlockESPModule extends Module implements ConfigSerializable {
   private static final long SCAN_INTERVAL_MS = 1000L;
   private final BooleanSetting enabledSetting;
   private final NumberSetting scanRadius;
   private final ElapsedTimer scanTimer = new ElapsedTimer();
   private List<BlockESPModule.BlockHit> hits = new ArrayList<>();

   public BlockESPModule(EventBus eventBus) {
      super(eventBus, "block_esp", "Block ESP", "Подсвечивает заданные блоки в мире", ModuleCategory.valueOf("RENDER"));
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Подсветка заданных блоков.")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      this.scanRadius = ((NumberSettingBuilder)NumberSetting.builder()
            .range(5.0, 32.0)
            .defaultValue(16.0)
            .multiplier(1.0)
            .precision(0)
            .animationSpeed(20.0F)
            .name("Scan Radius")
            .id("scan_radius")
            .description("Радиус сканирования блоков")
            .aliases("scan radius", "радиус"))
         .build();
      this.registerSetting(this.scanRadius);
   }

   @Override
   protected void initialize() {
      this.listen(WorldRenderEvent.class, this::onWorldRender);
      this.listen(WorldSessionEvent.class, event -> this.hits = new ArrayList<>());
   }

   private void onWorldRender(WorldRenderEvent event) {
      if (this.enabledSetting.isEnabled()) {
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         class_638 world = client.field_1687;
         class_4184 camera = client.field_1773.method_19418();
         class_243 cameraPos = camera.method_71156();
         if (player != null && world != null && cameraPos != null) {
            List<BlockESPModule.BlockHit> scanned = this.cachedHits();
            if (!scanned.isEmpty()) {
               ArrayList<BlockESPModule.VisibleBox> boxes = new ArrayList<>();

               for(BlockESPModule.BlockHit hit : scanned) {
                  class_238 box = this.blockBox(hit.pos());
                  if (!(cameraPos.method_1025(box.method_1005()) > 4096.0)) {
                     boxes.add(new BlockESPModule.VisibleBox(box, hit.color()));
                  }
               }

               if (!boxes.isEmpty()) {
                  boolean wasDepthTest = GL11.glIsEnabled(2929);
                  boolean wasDepthWrite = GL11.glGetBoolean(2930);
                  boolean wasBlend = GL11.glIsEnabled(3042);
                  try {
                     GlStateManager._enableDepthTest();
                     GlStateManager._depthMask(true);
                     GlStateManager._enableBlend();
                     GlStateManager._blendFuncSeparate(770, 771, 770, 771);
                     Matrix4f matrix = new Matrix4f().rotation(camera.method_23767());
                     class_287 consumer = class_289.method_1348().method_60827(class_5596.field_29344, class_290.field_1576);

                     for(BlockESPModule.VisibleBox box : boxes) {
                        this.drawBox(consumer, matrix, cameraPos, box.box(), box.color());
                     }

                     class_12249.method_76015().method_60895(consumer.method_60800());
                  } finally {
                     setCap(2929, wasDepthTest);
                     GlStateManager._depthMask(wasDepthWrite);
                     setCap(3042, wasBlend);
                  }
               }
            }
         }
      }
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
      consumer.method_22918(matrix, (float)(x1 - cameraPos.field_1352), (float)(y1 - cameraPos.field_1351), (float)(z1 - cameraPos.field_1350))
         .method_1336(red, green, blue, alpha);
      consumer.method_22918(matrix, (float)(x2 - cameraPos.field_1352), (float)(y2 - cameraPos.field_1351), (float)(z2 - cameraPos.field_1350))
         .method_1336(red, green, blue, alpha);
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

   private Map<class_2248, Integer> watchedBlocks() {
      HashMap<class_2248, Integer> map = new HashMap<>();
      BlockEspStore store = WexSideClient.getBlockEspStore();
      if (store == null) {
         return map;
      } else {
         for(Entry<String, Integer> entry : store.getBlocks().entrySet()) {
            class_2960 id = class_2960.method_12829(entry.getKey());
            if (id != null) {
               class_7923.field_41175.method_17966(id).ifPresent(block -> map.put(block, entry.getValue()));
            }
         }

         return map;
      }
   }

   private List<BlockESPModule.BlockHit> cachedHits() {
      if (this.scanTimer.process(1000L)) {
         this.hits = this.scanHits();
         this.scanTimer.update();
      }

      return this.hits;
   }

   private List<BlockESPModule.BlockHit> scanHits() {
      ArrayList<BlockESPModule.BlockHit> found = new ArrayList<>();
      class_310 client = class_310.method_1551();
      class_638 world = client.field_1687;
      class_746 player = client.field_1724;
      if (world != null && player != null) {
         Map<class_2248, Integer> colors = this.watchedBlocks();
         if (colors.isEmpty()) {
            return found;
         } else {
            int radius = this.scanRadius.getIntValue();
            class_2338 origin = player.method_24515();
            int minY = Math.max(origin.method_10264() - radius, world.method_31607());
            int maxY = Math.min(origin.method_10264() + radius, world.method_31607() + world.method_31605() - 1);
            class_2339 cursor = new class_2339();

            for(int x = -radius; x <= radius; ++x) {
               for(int z = -radius; z <= radius; ++z) {
                  for(int y = minY; y <= maxY; ++y) {
                     cursor.method_10103(origin.method_10263() + x, y, origin.method_10260() + z);
                     Integer color = colors.get(world.method_8320(cursor).method_26204());
                     if (color != null) {
                        found.add(new BlockESPModule.BlockHit(cursor.method_10062(), color));
                     }
                  }
               }
            }

            return found;
         }
      } else {
         return found;
      }
   }

   private static void setCap(int cap, boolean enabled) {
      if (enabled) {
         GL11.glEnable(cap);
      } else {
         GL11.glDisable(cap);
      }
   }

   private static record BlockHit(class_2338 pos, int color) {
   }

   private static record VisibleBox(class_238 box, int color) {
   }
}
