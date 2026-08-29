package ru.wexside.module.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_1747;
import net.minecraft.class_1767;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2248;
import net.minecraft.class_243;
import net.minecraft.class_2480;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_7923;
import net.minecraft.class_9288;
import net.minecraft.class_9334;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.event.ItemHoverEvent;
import ru.wexside.event.TooltipRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.render.ItemIconCache;
import ru.wexside.render.RenderProjection;
import ru.wexside.setting.BindSetting;
import ru.wexside.setting.BindSettingBuilder;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.util.GuiDrawApi;

public final class ShulkerViewerModule extends Module implements ConfigSerializable {
   private static final float PANEL_WIDTH = 172.0F;
   private static final int DEFAULT_TINT = 9068428;
   private static final int COLUMNS = 9;
   private static volatile ShulkerViewerModule instance;
   private final BooleanSetting enabledSetting;
   private final BooleanSetting onGround;
   private final BooleanSetting onGroundTexture;
   private final BindSetting viewKey;
   private final ItemIconCache icons = new ItemIconCache();
   private class_1799 hoveredShulker;

   public ShulkerViewerModule(EventBus eventBus) {
      super(eventBus, "shulker_viewer", "Shulker Viewer", "Просмотр содержимого шалкера ", ModuleCategory.valueOf("RENDER"));
      instance = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Включить просмотр содержимого шалкеров")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      this.onGround = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("OnGround")
            .id("on_ground")
            .description("Предпросмотр шалкеров, лежащих в мире"))
         .build();
      this.registerSetting(this.onGround);
      this.onGroundTexture = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Texture")
            .id("on_ground_texture")
            .description("Текстурная рамка предпросмотра на земле")
            .aliases("texture", "текстура")
            .visibleWhen(this.onGround::isEnabled))
         .build();
      this.registerSetting(this.onGroundTexture);
      this.viewKey = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .name("View key")
            .id("view_key")
            .description("Удерживай клавишу, чтобы увидеть содержимое наведённого шалкера")
            .aliases("view key", "клавиша просмотра"))
         .build();
      this.registerSetting(this.viewKey);
   }

   @Override
   protected void initialize() {
      this.listen(ItemHoverEvent.class, this::onItemHover);
      this.listen(TooltipRenderEvent.class, this::onTooltip);
      this.listen(HudRenderEvent.class, this::onHudRender);
      this.listen(WorldSessionEvent.class, event -> this.icons.update3());
   }

   public static boolean compute(class_1799 stack) {
      if (stack != null && !stack.method_7960()) {
         class_2960 id = class_7923.field_41178.method_10221(stack.method_7909());
         String path = id == null ? "" : id.method_12832();
         return path.contains("shulker_box");
      } else {
         return false;
      }
   }

   public static String getString() {
      ShulkerViewerModule module = instance;
      return module != null && module.enabledSetting.isEnabled() && !module.viewKey.getBindInput().isUnbound() ? module.viewKey.getKeyDisplayName() : null;
   }

   private void onHudRender(HudRenderEvent event) {
      if (this.enabledSetting.isEnabled() && this.onGround.isEnabled()) {
         class_310 client = class_310.method_1551();
         class_243 cameraPos = client.field_1773.method_19418().method_71156();
         if (client.field_1687 != null && client.field_1724 != null && cameraPos != null) {
            ArrayList<ShulkerViewerModule.GroundPreview> previews = new ArrayList<>();

            for(class_1297 entity : client.field_1687.method_18112()) {
               if (entity instanceof class_1542 itemEntity) {
                  class_1799 stack = itemEntity.method_6983();
                  class_1767 color = this.shulkerColor(stack);
                  if ((color != null || this.isShulkerBlock(stack)) && !(cameraPos.method_1025(entity.method_73189()) > 1024.0)) {
                     class_9288 container = (class_9288)stack.method_58694(class_9334.field_49622);
                     if (container != null) {
                        List<class_1799> contents = container.method_57489().toList();
                        if (!contents.isEmpty()) {
                           Vector2f screen = this.worldToScreen(
                              entity.method_23317(), entity.method_23318() + (double)entity.method_17682() / 2.0, entity.method_23321()
                           );
                           if (screen != null) {
                              previews.add(new ShulkerViewerModule.GroundPreview(screen.x, screen.y, this.brighten(color), contents));
                           }
                        }
                     }
                  }
               }
            }

            if (!previews.isEmpty()) {
               float scale = (float)client.method_22683().method_4495();
               this.icons.update2();

               for(ShulkerViewerModule.GroundPreview preview : previews) {
                  for(class_1799 stack : preview.stacks) {
                     preview.icons.add(stack.method_7960() ? null : this.icons.process(stack));
                  }
               }

               ArrayList commands = new ArrayList();
               this.icons.process2(scale, commands);
               if (!commands.isEmpty()) {
                  WexSideClient.getRenderPipeline2().setList(commands);
               }

               GuiDrawApi renderer = WexSideClient.getHudRenderer();
               Matrix4f matrix = new Matrix4f().scale(scale);
               renderer.begin();

               try {
                  for(ShulkerViewerModule.GroundPreview preview : previews) {
                     this.drawPanel(renderer, matrix, preview);
                  }

                  for(ShulkerViewerModule.GroundPreview preview : previews) {
                     this.drawIcons(renderer, matrix, preview);
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

   private void onItemHover(ItemHoverEvent event) {
      class_1799 stack = event.getStack();
      if (this.enabledSetting.isEnabled() && compute(stack)) {
         this.hoveredShulker = stack;
         if (this.viewKey.isPressed()) {
            event.update();
         }
      }
   }

   private void onTooltip(TooltipRenderEvent event) {
      class_1799 stack = this.hoveredShulker;
      this.hoveredShulker = null;
      if (this.enabledSetting.isEnabled() && stack != null && this.viewKey.isPressed()) {
         class_9288 container = (class_9288)stack.method_58694(class_9334.field_49622);
         if (container != null) {
            this.drawTooltip(event.getDrawContext(), event.getIntType2(), event.getIntType(), stack, container.method_57489().toList());
         }
      }
   }

   private void drawTooltip(class_332 context, int mouseX, int mouseY, class_1799 stack, List<class_1799> contents) {
      class_310 client = class_310.method_1551();
      int rows = Math.max(3, (contents.size() + 9 - 1) / 9);
      int slots = rows * 9;
      int width = 174;
      int height = rows * 18 + 12;
      int screenWidth = context.method_51421();
      int screenHeight = context.method_51443();
      int x = mouseX + 8;
      int y = mouseY + 8;
      if (x + width > screenWidth) {
         x = mouseX - 8 - width;
      }

      if (y + height > screenHeight) {
         y = Math.max(0, screenHeight - height);
      }

      int tint = this.itemTint(stack);
      int fill = -436207616 | this.scaleRgb(tint, 0.35F);
      int slotFill = 872415231;
      context.method_25294(x, y, x + width, y + height, fill);
      context.method_25294(x, y, x + width, y + 1, 0xFF000000 | tint);
      context.method_25294(x, y + height - 1, x + width, y + height, 0xFF000000 | tint);
      context.method_25294(x, y, x + 1, y + height, 0xFF000000 | tint);
      context.method_25294(x + width - 1, y, x + width, y + height, 0xFF000000 | tint);

      for(int i = 0; i < slots; ++i) {
         int slotX = x + 6 + i % 9 * 18;
         int slotY = y + 6 + i / 9 * 18;
         context.method_25294(slotX, slotY, slotX + 16, slotY + 16, slotFill);
         if (i < contents.size()) {
            class_1799 item = (class_1799)contents.get(i);
            if (!item.method_7960()) {
               context.method_51427(item, slotX, slotY);
               context.method_51431(client.field_1772, item, slotX, slotY);
            }
         }
      }
   }

   private void drawPanel(GuiDrawApi renderer, Matrix4f matrix, ShulkerViewerModule.GroundPreview preview) {
      int rows = Math.max(3, (preview.stacks.size() + 9 - 1) / 9);
      float x = preview.x - 86.0F;
      float y = preview.y + 6.0F;
      float height = (float)(rows * 18 + 10);
      renderer.fillRectangle(matrix, x, y, 172.0F, height, -1072689136);
      int border = 0xFF000000 | preview.tint & 16777215;
      renderer.fillRectangle(matrix, x, y, 172.0F, 1.0F, border);
      renderer.fillRectangle(matrix, x, y + height - 1.0F, 172.0F, 1.0F, border);
      renderer.fillRectangle(matrix, x, y, 1.0F, height, border);
      renderer.fillRectangle(matrix, x + 172.0F - 1.0F, y, 1.0F, height, border);

      for(int row = 0; row < rows; ++row) {
         for(int column = 0; column < 9; ++column) {
            renderer.fillRectangle(matrix, x + 6.0F + (float)(18 * column), y + 6.0F + (float)(18 * row), 16.0F, 16.0F, 872415231);
         }
      }
   }

   private void drawIcons(GuiDrawApi renderer, Matrix4f matrix, ShulkerViewerModule.GroundPreview preview) {
      float x = preview.x - 86.0F;
      float y = preview.y + 6.0F;

      for(int i = 0; i < preview.stacks.size(); ++i) {
         Object icon = preview.icons.get(i);
         if (icon != null) {
            float slotX = x + 6.0F + (float)(18 * (i % 9));
            float slotY = y + 6.0F + (float)(18 * (i / 9));
            this.icons.process3(renderer, matrix, icon, slotX, slotY, 16.0F);
         }
      }
   }

   private class_1767 shulkerColor(class_1799 stack) {
      if (stack != null && !stack.method_7960()) {
         class_1792 item = stack.method_7909();
         if (item instanceof class_1747 blockItem) {
            class_2248 var5 = blockItem.method_7711();
            return var5 instanceof class_2480 shulker ? shulker.method_10528() : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private boolean isShulkerBlock(class_1799 stack) {
      if (stack != null && !stack.method_7960()) {
         class_1792 item = stack.method_7909();
         if (item instanceof class_1747 blockItem && blockItem.method_7711() instanceof class_2480) {
            return true;
         }

         return false;
      } else {
         return false;
      }
   }

   private int itemTint(class_1799 stack) {
      class_2960 id = class_7923.field_41178.method_10221(stack.method_7909());
      String path = id == null ? "" : id.method_12832();

      for(class_1767 color : class_1767.values()) {
         if (path.startsWith(color.method_15434() + "_")) {
            return color.method_16357() & 16777215;
         }
      }

      return 9068428;
   }

   private int brighten(class_1767 color) {
      int rgb = color != null ? color.method_7787() & 16777215 : 9068428;
      int red = Math.min(255, (rgb >> 16 & 0xFF) + 38);
      int green = Math.min(255, (rgb >> 8 & 0xFF) + 38);
      int blue = Math.min(255, (rgb & 0xFF) + 38);
      return 0xFF000000 | red << 16 | green << 8 | blue;
   }

   private int scaleRgb(int rgb, float factor) {
      int red = Math.round((float)(rgb >> 16 & 0xFF) * factor);
      int green = Math.round((float)(rgb >> 8 & 0xFF) * factor);
      int blue = Math.round((float)(rgb & 0xFF) * factor);
      return red << 16 | green << 8 | blue;
   }

   private Vector2f worldToScreen(double x, double y, double z) {
      return RenderProjection.project(new class_243(x, y, z));
   }

   private static final class GroundPreview {
      final float x;
      final float y;
      final int tint;
      final List<class_1799> stacks;
      final List<Object> icons = new ArrayList<>();

      GroundPreview(float x, float y, int tint, List<class_1799> contents) {
         this.x = x;
         this.y = y;
         this.tint = tint;
         this.stacks = contents;
      }
   }
}
