package ru.wexside.notification;

import java.util.function.IntSupplier;

public record NotificationPart(String text, IntSupplier color) {
   public NotificationPart(String text, IntSupplier color) {
      String var3;
      this.text = var3 = text == null ? "" : text;
      this.color = color;
   }

   public static NotificationPart text(String text, IntSupplier color) {
      return new NotificationPart(text, color);
   }
}
