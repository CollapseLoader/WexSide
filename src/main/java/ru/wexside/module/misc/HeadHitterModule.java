package ru.wexside.module.misc;

import net.minecraft.class_2246;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_746;
import net.minecraft.class_2338.class_2339;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public class HeadHitterModule extends Module implements ConfigSerializable {
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("")
         .withKeybind()
         .toggle())
      .build();

   public HeadHitterModule(EventBus eventBus) {
      super(eventBus, "head_hitter", "Head Hitter", "Подпрыгивает при касании блока головой", ModuleCategory.valueOf("MISC"));
      this.registerSetting(this.enabledSetting);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, event -> this.onTick());
   }

   private void onTick() {
      if (this.enabledSetting.isEnabled()) {
         class_310 client = class_310.method_1551();
         class_746 player = client.field_1724;
         if (player != null && client.field_1687 != null) {
            class_238 box = player.method_5829().method_1011(-0.02);
            class_238 head = new class_238(box.field_1323, box.field_1325 - 0.4, box.field_1321, box.field_1320, box.field_1325, box.field_1324);
            double jump;
            if (this.collides(head, client)) {
               jump = 0.41;
            } else {
               if (!this.collides(box, client)) {
                  return;
               }

               jump = 0.42;
            }

            WexSideClient.getTickOverride().setBooleanType(true);
            class_243 velocity = player.method_18798();
            player.method_18800(velocity.field_1352, jump, velocity.field_1350);
         }
      }
   }

   private boolean collides(class_238 box, class_310 client) {
      class_238 search = box.method_1009(0.03, 0.0, 0.03);
      int minX = class_3532.method_15357(search.field_1323);
      int minY = class_3532.method_15357(search.field_1322);
      int minZ = class_3532.method_15357(search.field_1321);
      int maxX = class_3532.method_15357(search.field_1320);
      int maxY = class_3532.method_15357(search.field_1325);
      int maxZ = class_3532.method_15357(search.field_1324);
      class_2339 pos = new class_2339();

      for(int x = minX; x <= maxX; ++x) {
         for(int y = minY; y <= maxY; ++y) {
            for(int z = minZ; z <= maxZ; ++z) {
               pos.method_10103(x, y, z);
               class_2680 state = client.field_1687.method_8320(pos);
               if (!state.method_26215()) {
                  class_265 shape = state.method_26220(client.field_1687, pos);
                  if (!shape.method_1110()) {
                     for(class_238 part : shape.method_1090()) {
                        class_238 worldBox = part.method_989((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260());
                        if (worldBox.method_994(search) && (!state.method_26216() || state.method_26204() == class_2246.field_10343)) {
                           return true;
                        }
                     }
                  }
               }
            }
         }
      }

      return false;
   }
}
