package ru.wexside.module.hud;

import java.util.ArrayList;
import net.minecraft.class_1041;
import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1934;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.misc.BakedIconEntry;
import ru.wexside.misc.HotbarSlotRenderer;
import ru.wexside.misc.ThemeColors;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.render.BakedItemIcon;
import ru.wexside.render.ItemIconCache;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public class HotbarModule extends Module implements ConfigSerializable {
   private static final float HOTBAR_WIDTH = 180.0F;
   private static final float SLOT_SIZE = 20.0F;
   private static final float ICON_SIZE = 16.0F;
   private static final float SLOT_DEPTH = 10.0F;
   private static final float BOTTOM_MARGIN = 3.0F;
   private static final float HOTBAR_HEIGHT = 20.0F;
   private static final float OFFHAND_GAP = 4.0F;
   private static final int HOTBAR_SLOTS = 9;
   static volatile HotbarModule hotbarModule2;
   private final BooleanSetting enabledSetting;
   private final HotbarSlotRenderer offhandRenderer;
   private final ItemIconCache iconCache;
   private final HotbarSlotRenderer[] slotRenderers;

   public HotbarModule(EventBus eventBus) {
      super(eventBus, "hotbar", "Hotbar", "Кастомный хотбар", ModuleCategory.valueOf("DISPLAY"));
      hotbarModule2 = this;
      this.iconCache = new ItemIconCache();
      this.offhandRenderer = new HotbarSlotRenderer(0);
      this.slotRenderers = new HotbarSlotRenderer[9];

      for(int slot = 0; slot < 9; ++slot) {
         this.slotRenderers[slot] = new HotbarSlotRenderer(slot);
      }

      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Кастомный хотбар")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
   }

   @Override
   protected void initialize() {
      this.listen(HudRenderEvent.class, event -> this.render());
   }

   private void render() {
      if (isEnabled()) {
         class_746 player = getPlayer();
         GuiDrawApi renderer = WexSideClient.getHudRenderer();
         if (player != null && renderer != null) {
            class_1661 inventory = player.method_31548();
            int selectedSlot = inventory.method_67532();
            class_1799 offhandStack = player.method_6079();
            boolean hasOffhand = !offhandStack.method_7960();
            class_1268 activeHand = player.method_6115() ? player.method_6058() : null;
            class_1041 window = class_310.method_1551().method_22683();
            float hotbarX = (float)window.method_4486() / 2.0F - 90.0F;
            float hotbarY = (float)window.method_4502() - 3.0F - 20.0F;
            boolean offhandOnLeft = player.method_6068() == class_1306.field_6183;
            float offhandX = offhandOnLeft ? hotbarX - 4.0F - 20.0F : hotbarX + 180.0F + 4.0F;
            this.iconCache.update2();
            BakedItemIcon[] slotIcons = new BakedItemIcon[9];

            for(int slot = 0; slot < 9; ++slot) {
               slotIcons[slot] = this.iconCache.process(inventory.method_5438(slot));
            }

            BakedItemIcon offhandIcon = hasOffhand ? this.iconCache.process(offhandStack) : null;
            ArrayList<BakedIconEntry> queuedDraws = new ArrayList<>();
            this.iconCache.process2(2.0F, queuedDraws);
            if (!queuedDraws.isEmpty()) {
               WexSideClient.getRenderPipeline2().setList(queuedDraws);
            }

            Matrix4f matrix = new Matrix4f().scale(2.0F);
            renderer.begin();

            try {
               this.drawSlotBackground(renderer, matrix, hotbarX, hotbarY, 180.0F);

               for(int slot = 0; slot < 9; ++slot) {
                  float slotX = hotbarX + (float)slot * 20.0F;
                  this.slotRenderers[slot]
                     .process(
                        renderer,
                        matrix,
                        this.iconCache,
                        slotIcons[slot],
                        inventory.method_5438(slot),
                        slotX,
                        hotbarY,
                        20.0F,
                        16.0F,
                        10.0F,
                        slot == selectedSlot,
                        activeHand == class_1268.field_5808 && slot == selectedSlot
                     );
               }

               if (hasOffhand) {
                  this.drawSlotBackground(renderer, matrix, offhandX, hotbarY, 20.0F);
                  this.offhandRenderer
                     .process(
                        renderer,
                        matrix,
                        this.iconCache,
                        offhandIcon,
                        offhandStack,
                        offhandX,
                        hotbarY,
                        20.0F,
                        16.0F,
                        10.0F,
                        false,
                        activeHand == class_1268.field_5810
                     );
               }
            } finally {
               renderer.end();
               this.iconCache.update();
            }
         }
      }
   }

   public static boolean isEnabled() {
      HotbarModule module = hotbarModule2;
      if (module != null && module.enabledSetting.isEnabled()) {
         class_310 client = class_310.method_1551();
         class_315 options = client.field_1690;
         if (options != null && !options.field_1842) {
            if (client.field_1761 != null && client.field_1761.method_2920() == class_1934.field_9219) {
               return false;
            } else {
               return getPlayer() != null;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static float getFloatType() {
      if (!isEnabled()) {
         return 0.0F;
      } else {
         float scale = (float)class_310.method_1551().method_22683().method_4495();
         return 46.0F / scale - 22.0F;
      }
   }

   private static class_746 getPlayer() {
      return class_310.method_1551().field_1724;
   }

   private void drawSlotBackground(GuiDrawApi renderer, Matrix4f matrix, float x, float y, float width) {
      renderer.drawRoundedRectangle(matrix, x, y, width, 20.0F, 10.0F, ColorUtils.multiplyAlpha(ThemeColors.visualizerSlot(), 1.0F));
   }
}
