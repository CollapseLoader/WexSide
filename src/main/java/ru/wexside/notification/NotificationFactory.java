package ru.wexside.notification;

public interface NotificationFactory {
   default String soundId() {
      return null;
   }

   NotificationCategory category();

   NotificationToast create(long var1);
}
