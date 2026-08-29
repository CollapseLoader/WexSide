package ru.wexside.module.combat;

import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_310;
import net.minecraft.class_638;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.FriendList;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class HitBoxesModule extends Module implements ConfigSerializable {
   private static final float SIZE_SCALE = 2.5F;
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("Расширяет хит-боксы сущностей")
         .withKeybind()
         .toggle())
      .build();
   private final BooleanSetting visible;
   private final NumberSetting size;
   private final BooleanSetting ignoreFriends;

   public HitBoxesModule(EventBus eventBus) {
      super(eventBus, "hit_boxes", "Hit Boxes", "Расширяет хит-боксы сущностей", ModuleCategory.valueOf("COMBAT"));
      this.registerSetting(this.enabledSetting);
      this.visible = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Visible")
            .id("visible")
            .description("Обводка хитбокса"))
         .build();
      this.registerSetting(this.visible);
      this.size = ((NumberSettingBuilder)NumberSetting.builder()
            .range(0.0, 3.0)
            .defaultValue(0.5)
            .multiplier(1.0)
            .precision(1)
            .animationSpeed(20.0F)
            .name("Size")
            .id("size")
            .description("Размер расширения хит-бокса")
            .aliases("size", "размер"))
         .build();
      this.registerSetting(this.size);
      this.ignoreFriends = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(false)
            .defaultValue(false)
            .name("Ignore Friends")
            .id("ignore_friends")
            .description("Не расширять хит-боксы друзей"))
         .build();
      this.registerSetting(this.ignoreFriends);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, this::onClientTick);
   }

   private void onClientTick(ClientTickEvent event) {
      if (this.enabledSetting.isEnabled()) {
         class_310 client = class_310.method_1551();
         class_638 world = client.field_1687;
         if (world != null && client.field_1724 != null) {
            float expand = (float)this.size.getValue() * 2.5F;

            for(class_1297 entity : world.method_18112()) {
               if (this.shouldExpand(entity, client.field_1724)) {
                  entity.method_5857(this.expandedBox(entity, expand));
               }
            }
         }
      }
   }

   private class_238 expandedBox(class_1297 entity, float expand) {
      class_238 box = entity.method_5829();
      return new class_238(
         entity.method_23317() - (double)expand,
         box.field_1322,
         entity.method_23321() - (double)expand,
         entity.method_23317() + (double)expand,
         box.field_1325,
         entity.method_23321() + (double)expand
      );
   }

   private boolean shouldExpand(class_1297 entity, class_1297 player) {
      if (entity == null || !entity.method_5805() || entity == player) {
         return false;
      } else if (!this.ignoreFriends.isEnabled()) {
         return true;
      } else {
         FriendList friends = WexSideClient.getFriends();
         return friends == null || !friends.contains(entity.method_5477().getString());
      }
   }
}
