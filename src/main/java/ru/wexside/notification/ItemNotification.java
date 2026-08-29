package ru.wexside.notification;

import java.util.List;
import net.minecraft.class_1799;

public final class ItemNotification implements NotificationFactory {
   private final class_1799 stack;

   public ItemNotification(class_1799 stack) {
      this.stack = stack == null ? class_1799.field_8037 : stack.method_7972();
   }

   public class_1799 stack() {
      return this.stack;
   }

   @Override
   public NotificationCategory category() {
      return NotificationCategory.ITEM;
   }

   @Override
   public NotificationToast create(long durationMillis) {
      return new NotificationToast(
         this.category(), this.stack, "i", () -> -1, List.of(NotificationPart.text(this.stack.method_7964().getString(), () -> -1)), durationMillis
      );
   }
}
