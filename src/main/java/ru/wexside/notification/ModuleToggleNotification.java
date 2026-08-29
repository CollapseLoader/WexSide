package ru.wexside.notification;

import java.util.List;
import ru.wexside.misc.ThemeColors;
import ru.wexside.util.ColorUtils;

public record ModuleToggleNotification(String moduleName, boolean enabled) implements NotificationFactory {
   @Override
   public NotificationCategory category() {
      return NotificationCategory.MODULE;
   }

   @Override
   public NotificationToast create(long durationMillis) {
      int accent = this.enabled ? ColorUtils.rgba(38, 198, 140, 255) : ColorUtils.rgba(255, 82, 82, 255);
      return new NotificationToast(
         this.category(),
         List.of(this.category(), this.moduleName),
         "M",
         () -> accent,
         List.of(
            NotificationPart.text("Функция ", ThemeColors::hudTextPrimary),
            NotificationPart.text(this.moduleName, ThemeColors::accent),
            NotificationPart.text(this.enabled ? " включена" : " выключена", ThemeColors::hudTextPrimary)
         ),
         durationMillis
      );
   }
}
