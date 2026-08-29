package ru.wexside.module.misc;

import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EntityAttackEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.ClientChat;
import ru.wexside.misc.FriendList;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BindSetting;
import ru.wexside.setting.BindSettingBuilder;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class FriendsModule extends Module implements ConfigSerializable {
   private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder()
         .value(false)
         .defaultValue(false)
         .name("Enabled")
         .id("enabled")
         .description("Добавление друзей биндом и отключение урона по друзьям")
         .withKeybind()
         .toggle())
      .build();
   private final BooleanSetting clickFriend;
   private final BindSetting friendKey;
   private final BooleanSetting noFriendDamage;

   public FriendsModule(EventBus eventBus) {
      super(eventBus, "friends", "Friends", "Добавление друзей биндом и отключение урона по друзьям", ModuleCategory.valueOf("MISC"));
      this.registerSetting(this.enabledSetting);
      this.clickFriend = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("Click Friend")
            .id("click_friend")
            .description("Добавлять/удалять друга биндом по наводимому игроку"))
         .build();
      this.registerSetting(this.clickFriend);
      this.friendKey = ((BindSettingBuilder)BindSetting.getBindSettingBuilder()
            .keyboard(0)
            .onReleased(this::onFriendKeyPress)
            .name("Friend Key")
            .id("friend_key")
            .description("Бинд добавления/удаления друга")
            .aliases("friend key", "бинд друга")
            .visibleWhen(this.clickFriend::isEnabled))
         .build();
      this.registerSetting(this.friendKey);
      this.noFriendDamage = ((BooleanSettingBuilder)BooleanSetting.builder()
            .value(true)
            .defaultValue(false)
            .name("No Friend Damage")
            .id("no_friend_damage")
            .description("Отменяет атаку по игрокам из списка друзей")
            .aliases("no friend damage", "без урона по друзьям"))
         .build();
      this.registerSetting(this.noFriendDamage);
   }

   @Override
   protected void initialize() {
      this.listen(EntityAttackEvent.class, this::onEntityAttack);
   }

   private void onFriendKeyPress(BindSetting ignored) {
      if (this.enabledSetting.isEnabled() && this.clickFriend.isEnabled()) {
         class_310 client = class_310.method_1551();
         class_1297 target = client.field_1692;
         if (target instanceof class_1657 player && player != client.field_1724) {
            FriendList friends = WexSideClient.getFriends();
            if (friends == null) {
               return;
            }

            String name = player.method_5477().getString();
            if (friends.contains(name)) {
               friends.remove(name);
               ClientChat.send("Друг " + name + " удалён из списка друзей");
            } else {
               friends.add(name);
               ClientChat.send("Друг " + name + " добавлен в список друзей");
            }

            return;
         }
      }
   }

   private void onEntityAttack(EntityAttackEvent event) {
      if (this.enabledSetting.isEnabled() && this.noFriendDamage.isEnabled()) {
         class_1297 entity = event.getEntity();
         if (entity instanceof class_1657 && this.isFriend(entity)) {
            event.update();
         }
      }
   }

   private boolean isFriend(class_1297 entity) {
      FriendList friends = WexSideClient.getFriends();
      return friends != null && friends.contains(entity.method_5477().getString());
   }
}
