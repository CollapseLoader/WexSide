package ru.wexside.notification;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import net.minecraft.class_1109;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.util.GuiDrawApi;

public final class NotificationCenter {
   private static final int MAX_VISIBLE = 5;
   private static final int MAX_RETAINED = 8;
   private static final float ROW_HEIGHT = 17.5F;
   private static final long DEFAULT_DURATION_MILLIS = 3000L;
   private final NotificationPreferences preferences;
   private final LinkedList<NotificationToast> notifications = new LinkedList<>();

   public NotificationCenter(NotificationPreferences preferences) {
      this.preferences = Objects.requireNonNull(preferences, "preferences");
   }

   public void push(NotificationFactory factory) {
      class_310 client = class_310.method_1551();
      if (!client.method_18854()) {
         client.execute(() -> this.push(factory));
      } else if (client.field_1724 != null && this.preferences.isEnabled() && this.preferences.isCategoryVisible(factory.category())) {
         NotificationToast incoming = factory.create(3000L);
         if (incoming.key() != null) {
            for(NotificationToast existing : this.notifications) {
               if (Objects.equals(existing.key(), incoming.key())) {
                  existing.updateContent(incoming.icon(), incoming.accentColor(), incoming.parts());
                  this.playSound(factory.category());
                  return;
               }
            }
         }

         this.notifications.addLast(incoming);
         int active = 0;

         for(NotificationToast notification : this.notifications) {
            if (!notification.isDismissing()) {
               if (++active > 5) {
                  notification.beginDismiss();
               }
            }
         }

         while(this.notifications.size() > 8) {
            this.notifications.removeFirst().remove();
         }

         this.playSound(factory.category());
      }
   }

   public boolean accepts(NotificationCategory category) {
      return this.preferences.isEnabled() && this.preferences.isCategoryVisible(category);
   }

   public void render(float tickDelta) {
      class_310 client = class_310.method_1551();
      if (client.field_1724 != null && !this.notifications.isEmpty()) {
         GuiDrawApi renderer = WexSideClient.getHudRenderer();
         if (renderer != null) {
            Iterator<NotificationToast> iterator = this.notifications.iterator();
            int row = 0;
            float startY = (float)client.method_22683().method_4502() * 0.5F + 20.0F * tickDelta;
            float centerX = (float)client.method_22683().method_4486() * 0.5F;
            Matrix4f matrix = new Matrix4f().scale((float)client.method_22683().method_4495());
            renderer.begin();

            try {
               while(iterator.hasNext()) {
                  NotificationToast notification = iterator.next();
                  notification.moveTo(startY + (float)row * 17.5F * tickDelta, tickDelta);
                  if (notification.isExpired()) {
                     iterator.remove();
                  } else {
                     notification.render(renderer, matrix, centerX, tickDelta);
                     ++row;
                  }
               }
            } finally {
               renderer.end();
            }
         }
      }
   }

   private void playSound(NotificationCategory category) {
      if (this.preferences.isSoundEnabled(category)) {
         class_310.method_1551()
            .method_1483()
            .method_4873(class_1109.method_4758((class_3414)class_3417.field_15015.comp_349(), Math.max(0.0F, Math.min(1.0F, this.preferences.soundVolume()))));
      }
   }
}
