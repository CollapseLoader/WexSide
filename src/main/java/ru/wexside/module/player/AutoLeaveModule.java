package ru.wexside.module.player;

import net.minecraft.class_124;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_419;
import net.minecraft.class_442;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.FriendList;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.server.FunTimeServerContext;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.util.entity.NpcDetector;

public class AutoLeaveModule extends Module implements ConfigSerializable {
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("")
         .withKeybind()
         .toggle())
      .build();
   private final BooleanSetting ignoreFriends;

   public AutoLeaveModule(EventBus eventBus) {
      super(eventBus, "auto_leave", "Auto Leave", "Автоматически выходит с сервера при обнаружении игроков поблизости", ModuleCategory.valueOf("PLAYER"));
      this.registerSetting(this.enabledSetting);
      this.ignoreFriends = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Ignore Friends")
            .id("ignore_friends")
            .description("Игнорировать друзей")
            .aliases("friends", "друзья"))
         .build();
      this.registerSetting(this.ignoreFriends);
   }

   @Override
   protected void initialize() {
      this.listen(ClientTickEvent.class, this::onTick);
   }

   private void onTick(ClientTickEvent event) {
      class_310 client = class_310.method_1551();
      if (this.enabledSetting.isEnabled() && client.field_1687 != null && client.field_1724 != null) {
         for(class_1657 other : client.field_1687.method_18456()) {
            if (other != client.field_1724 && (!this.ignoreFriends.isEnabled() || !this.isFriend(other)) && !this.isNpc(other)) {
               if (FunTimeServerContext.isPvpLocked()) {
                  return;
               }

               this.leave();
               return;
            }
         }
      }
   }

   private void leave() {
      this.enabledSetting.setEnabled(false);
      class_310 client = class_310.method_1551();
      class_2561 reason = class_2561.method_43470("AutoLeave");
      client.method_76795(new class_419(new class_442(), reason, reason), false);
   }

   private boolean isFriend(class_1657 player) {
      FriendList friends = WexSideClient.getFriends();
      return friends != null && friends.contains(class_124.method_539(player.method_5477().getString()));
   }

   private boolean isNpc(class_1657 player) {
      NpcDetector npcDetector = WexSideClient.getNpcDetector();
      return npcDetector != null && npcDetector.isNpc(player);
   }
}
