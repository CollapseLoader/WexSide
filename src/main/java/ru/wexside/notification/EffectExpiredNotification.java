package ru.wexside.notification;

import java.util.List;
import net.minecraft.class_1291;
import net.minecraft.class_6880;
import ru.wexside.misc.ThemeColors;

public record EffectExpiredNotification(class_6880<class_1291> effect) implements NotificationFactory {
   @Override
   public NotificationCategory category() {
      return NotificationCategory.EFFECT_EXPIRED;
   }

   @Override
   public NotificationToast create(long durationMillis) {
      String name = ((class_1291)this.effect.comp_349()).method_5560().getString();
      return new NotificationToast(
         this.category(),
         List.of(this.category(), this.effect),
         "E",
         ThemeColors::hudTextPrimary,
         List.of(
            NotificationPart.text("Эффект ", ThemeColors::hudTextPrimary),
            NotificationPart.text(name, ThemeColors::accent),
            NotificationPart.text(" закончился", ThemeColors::hudTextPrimary)
         ),
         durationMillis
      );
   }
}
