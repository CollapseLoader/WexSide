package ru.wexside.module.hud;

import net.minecraft.class_1937;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_408;
import net.minecraft.class_5321;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.HudRenderEvent;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.util.GuiDrawApi;

public final class PositionModule extends Module implements ConfigSerializable {
   private static final float TEXT_SIZE = 7.0F;
   private static final float CHAT_OFFSET = 14.0F;
   private static final float CHAT_LERP_SPEED = 30.0F;
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("Отображает координаты и скорость игрока")
         .withKeybind()
         .toggle())
      .build();
   private float chatOffset;

   public PositionModule(EventBus eventBus) {
      super(eventBus, "position", "Position", "Отображает координаты и скорость игрока", ModuleCategory.valueOf("DISPLAY"));
      this.registerSetting(this.enabledSetting);
   }

   @Override
   protected void initialize() {
      this.listen(HudRenderEvent.class, event -> this.onRender());
   }

   private void onRender() {
      if (this.enabledSetting.isEnabled()) {
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         class_638 world = client.field_1687;
         if (player != null && world != null) {
            String coordinates = this.formatCoordinates(player, world);
            double deltaX = player.method_23317() - player.field_6014;
            double deltaZ = player.method_23321() - player.field_5969;
            String speed = String.format("%.2f", Math.hypot(deltaX, deltaZ) * 20.0);
            String speedLine = "bps: " + speed;
            float scale = (float)client.method_22683().method_4495();
            float height = (float)client.method_22683().method_4502();
            this.chatOffset = FrameInterpolator.lerpTowards(this.chatOffset, client.field_1755 instanceof class_408 ? 14.0F : 0.0F, 30.0F);
            GuiDrawApi renderer = WexSideClient.getHudRenderer();
            Matrix4f matrix = new Matrix4f().scale(scale);
            renderer.begin();

            try {
               FontRegistry.font9.process2(matrix, renderer, coordinates, 3.0F, height - 10.0F - this.chatOffset, 7.0F, -1);
               FontRegistry.font9.process2(matrix, renderer, speedLine, 3.0F, height - 18.0F - this.chatOffset, 7.0F, -1);
            } finally {
               renderer.end();
            }
         }
      }
   }

   private String formatCoordinates(class_746 player, class_638 world) {
      int x = player.method_31477();
      int y = player.method_31478();
      int z = player.method_31479();
      class_5321<class_1937> dimension = world.method_27983();
      class_2960 dimensionId = dimension.method_29177();
      boolean nether = dimensionId.method_12832().contains("nether");
      int altX = nether ? x * 8 : Math.floorDiv(x, 8);
      int altZ = nether ? z * 8 : Math.floorDiv(z, 8);
      return String.format("xyz: %d, %d, %d (%d, %d)", x, y, z, altX, altZ);
   }
}
