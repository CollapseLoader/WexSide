package ru.wexside.notification;

public interface NotificationPreferences {
   boolean isCategoryVisible(NotificationCategory var1);

   boolean isSoundEnabled(NotificationCategory var1);

   float soundVolume();

   boolean isEnabled();
}
