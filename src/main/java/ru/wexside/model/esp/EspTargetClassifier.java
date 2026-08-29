package ru.wexside.model.esp;

import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.misc.FriendList;

public final class EspTargetClassifier {
   private EspTargetClassifier() {
   }

   public static EspTargetType targetType(class_1297 entity, class_746 localPlayer) {
      if (entity == localPlayer) {
         return EspTargetType.SELF;
      } else if (entity instanceof class_1657) {
         return EspTargetType.PLAYERS;
      } else {
         return entity instanceof class_1542 ? EspTargetType.ITEMS : EspTargetType.ENTITIES;
      }
   }

   public static EspRelation relation(class_1297 entity) {
      if (entity instanceof class_1657 player) {
         FriendList friends = WexSideClient.getFriends();
         if (friends != null && friends.contains(player.method_5477().getString())) {
            return EspRelation.FRIEND;
         }
      }

      return EspRelation.DEFAULT;
   }
}
