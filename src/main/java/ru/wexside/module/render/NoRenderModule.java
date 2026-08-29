package ru.wexside.module.render;

import java.util.List;
import net.minecraft.class_1291;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_310;
import net.minecraft.class_6880;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.OverlayRenderEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;

public final class NoRenderModule extends Module implements ConfigSerializable {
   static final String DYNAMIC_FOV = "Dynamic-FOV";
   static final String FLUID_ZOOM = "Fluid-Zoom";
   static final String FIRE_OVERLAY = "Fire-Overlay";
   static final String SCOREBOARD = "Scoreboard";
   static final String BLOCK_OVERLAY = "Block-Overlay";
   static final String TOTEM_OVERLAY = "Totem-Overlay";
   static final String GLOW = "Glow";
   static final String CAMERA_HURT = "Camera-Hurt";
   static final String VIEW_BOBBING = "View-Bobbing";
   static final String EFFECTS_ICONS = "Effects-Icons";
   static final String BAD_EFFECTS = "Bad-Effects";
   static final String BOSS_BAR = "Boss-Bar";
   static final String UNDER_LAVA = "Under-Lava";
   static final String WORLD_LAVA = "World-Lava";
   private static volatile NoRenderModule instance;
   private final BooleanSetting enabledSetting;
   private final MultiSelectSetting remove;
   private boolean lavaHidden;

   public NoRenderModule(EventBus eventBus) {
      super(eventBus, "no_render", "No Render", "Отключение элементов рендера", ModuleCategory.valueOf("RENDER"));
      instance = this;
      this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Enabled")
            .id("enabled")
            .description("Включить отключение элементов рендера")
            .withKeybind()
            .toggle())
         .build();
      this.registerSetting(this.enabledSetting);
      MultiSelectSetting removeSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
            .options(
               "Fire-Overlay",
               "Block-Overlay",
               "Camera-Hurt",
               "Totem-Overlay",
               "Effects-Icons",
               "Bad-Effects",
               "Boss-Bar",
               "Scoreboard",
               "Glow",
               "Fluid-Zoom",
               "Under-Lava",
               "World-Lava",
               "View-Bobbing",
               "Dynamic-FOV"
            )
            .selectAll(false)
            .optionListEnabled(false)
            .name("Remove")
            .id("remove")
            .description("Отключаемые элементы"))
         .build();
      this.remove = removeSetting;
      this.registerSetting(removeSetting);
   }

   @Override
   protected void initialize() {
      this.listen(OverlayRenderEvent.class, this::onOverlay);
      this.listen(ClientTickEvent.class, this::onTick);
   }

   public static boolean isEnabled() {
      return compute("Dynamic-FOV");
   }

   public static boolean isEnabled2() {
      return compute("Bad-Effects");
   }

   public static boolean isViewBobbingDisabled() {
      return compute("View-Bobbing");
   }

   public static boolean isFluidZoomDisabled() {
      return compute("Fluid-Zoom");
   }

   public static boolean isUnderLavaDisabled() {
      return compute("Under-Lava");
   }

   public static boolean isWorldLavaDisabled() {
      return compute("World-Lava");
   }

   public static boolean compute(String option) {
      NoRenderModule module = instance;
      return module != null && module.enabledSetting.isEnabled() && module.remove.getSelectedOptions().contains(option);
   }

   private void onTick(ClientTickEvent event) {
      class_310 client = class_310.method_1551();
      if (client.field_1724 != null && client.field_1687 != null) {
         boolean hideLava = this.enabledSetting.isEnabled() && this.remove.getSelectedOptions().contains("World-Lava");
         if (hideLava != this.lavaHidden) {
            if (client.field_1769 != null) {
               client.field_1769.method_3279();
            }

            this.lavaHidden = hideLava;
         }

         if (this.enabledSetting.isEnabled()) {
            List<String> selected = this.remove.getSelectedOptions();
            if (selected.contains("Bad-Effects")) {
               this.stripEffect(client.field_1724, class_1294.field_5916);
               this.stripEffect(client.field_1724, class_1294.field_5919);
               this.stripEffect(client.field_1724, class_1294.field_38092);
            }

            if (selected.contains("Glow")) {
               for(class_1297 entity : client.field_1687.method_18112()) {
                  if (entity.method_5851()) {
                     entity.method_5834(false);
                  }
               }
            }
         }
      }
   }

   private void stripEffect(class_746 player, class_6880<class_1291> effect) {
      if (player.method_6059(effect)) {
         player.method_6016(effect);
      }
   }

   private void onOverlay(OverlayRenderEvent event) {
      if (this.enabledSetting.isEnabled()) {
         String option = switch(event.type()) {
            case FIRE -> "Fire-Overlay";
            case BLOCK -> "Block-Overlay";
            case CAMERA_HURT -> "Camera-Hurt";
            case TOTEM -> "Totem-Overlay";
            case STATUS_EFFECTS -> "Effects-Icons";
            case BOSS_BAR -> "Boss-Bar";
            case SCOREBOARD -> "Scoreboard";
            default -> throw new MatchException(null, null);
         };
         if (option != null && this.remove.getSelectedOptions().contains(option)) {
            event.update();
         }
      }
   }
}
