package ru.wexside.notification;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1799;
import net.minecraft.class_1839;
import net.minecraft.class_1844;
import net.minecraft.class_5251;
import net.minecraft.class_9334;
import ru.wexside.misc.ThemeColors;

public final class ItemUseNotification implements NotificationFactory {
   private final String playerName;
   private final boolean self;
   private final class_1799 stack;
   private final class_1839 action;

   public ItemUseNotification(String playerName, boolean self, class_1799 stack, class_1839 action) {
      this.playerName = playerName == null ? "" : playerName;
      this.self = self;
      this.stack = stack == null ? class_1799.field_8037 : stack.method_7972();
      this.action = action == null ? class_1839.field_8952 : action;
   }

   @Override
   public NotificationCategory category() {
      return this.self ? NotificationCategory.SELF_ITEM_USE : NotificationCategory.OTHER_ITEM_USE;
   }

   @Override
   public NotificationToast create(long durationMillis) {
      ArrayList<NotificationPart> parts = new ArrayList<>();
      parts.add(NotificationPart.text(this.self ? "Вы использовали " : this.playerName + " использовал ", ThemeColors::hudTextPrimary));
      parts.add(NotificationPart.text(this.stack.method_7964().getString(), this::itemNameColor));
      class_1293 effect = this.firstPotionEffect();
      if (effect != null) {
         parts.add(
            NotificationPart.text(", получив эффект " + ((class_1291)effect.method_5579().comp_349()).method_5560().getString(), ThemeColors::hudTextPrimary)
         );
      }

      Object key = this.self ? null : List.of(this.category(), this.playerName);
      return new NotificationToast(this.category(), key, "+", () -> -1, parts, durationMillis);
   }

   private class_1293 firstPotionEffect() {
      if (this.action != class_1839.field_8950 && this.action != class_1839.field_8946) {
         return null;
      } else {
         class_1844 potion = (class_1844)this.stack.method_58694(class_9334.field_49651);
         if (potion == null) {
            return null;
         } else {
            return potion.method_57397().iterator().hasNext() ? (class_1293)potion.method_57397().iterator().next() : null;
         }
      }
   }

   private int itemNameColor() {
      class_5251 color = this.stack.method_7964().method_10866().method_10973();
      return color == null ? ThemeColors.accent() : 0xFF000000 | color.method_27716();
   }
}
