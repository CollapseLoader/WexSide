package ru.wexside.notification;

import java.util.List;
import java.util.function.IntSupplier;

public record TextNotification(NotificationCategory category, Object key, String icon, String text, IntSupplier color) implements NotificationFactory {
   public TextNotification(NotificationCategory category, Object key, String icon, String text) {
      this(category, key, icon, text, () -> -1);
   }

   @Override
   public NotificationToast create(long durationMillis) {
      return new NotificationToast(this.category, this.key, this.icon, this.color, List.of(NotificationPart.text(this.text, this.color)), durationMillis);
   }
}
