package ru.wexside.util;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import org.joml.Matrix4f;
import ru.wexside.misc.TextureResource;

public final class GuiPhotoBanner {
   private final TextureResource photo;
   private final LongSupplier clock;
   private final Easing enterEasing;
   private final Easing exitEasing;
   private final IntSupplier backgroundColor;
   private final IntSupplier borderColor;
   private final long cycleDurationNanos = 10000000000L;
   private final long enterDurationNanos = 650000000L;
   private final long holdDurationNanos = 2700000000L;
   private final long exitDurationNanos = 650000000L;
   private final float cardWidth = 70.0F;
   private final float cardHeight = 93.0F;
   private long openedAtNanos;
   private boolean open;

   public GuiPhotoBanner(TextureResource photo, LongSupplier clock, Easing enterEasing, Easing exitEasing, IntSupplier backgroundColor, IntSupplier borderColor) {
      this.photo = photo;
      this.clock = clock;
      this.enterEasing = enterEasing;
      this.exitEasing = exitEasing;
      this.backgroundColor = backgroundColor;
      this.borderColor = borderColor;
   }

   public void onGuiOpened() {
      this.openedAtNanos = this.clock.getAsLong();
      this.open = true;
   }

   public void onGuiClosed() {
      this.open = false;
   }

   public void render(GuiDrawApi renderer, Matrix4f matrix, float panelWidth, float panelHeight) {
      float visibility = this.visibilityAt(this.clock.getAsLong());
      if (!(visibility <= 0.001F)) {
         float visibleX = panelWidth - 70.0F - 7.0F;
         float hiddenX = panelWidth + 3.0F;
         float x = hiddenX + (visibleX - hiddenX) * visibility;
         float y = panelHeight - 93.0F - 7.0F;
         renderer.drawRoundedRectangle(matrix, x - 2.0F, y - 2.0F, 70.0F + 4.0F, 93.0F + 4.0F, 11.0F, this.backgroundColor.getAsInt());
         renderer.drawRoundedOutline(matrix, x - 2.0F, y - 2.0F, 70.0F + 4.0F, 93.0F + 4.0F, 11.0F, 0.75F, this.borderColor.getAsInt());
         int texture = renderer.bindTexture(this.photo.getTextureId(), this.photo.getWidth(), this.photo.getHeight());
         renderer.drawRoundedTextureTinted(matrix, x, y, 70.0F, 93.0F, 9.0F, texture, -1);
      }
   }

   private float visibilityAt(long nowNanos) {
      return 0.0F;
   }

   private float visibilityAtEnabled(long nowNanos) {
      if (!this.open) {
         return 0.0F;
      } else {
         long elapsed = Math.max(0L, nowNanos - this.openedAtNanos) % 10000000000L;
         if (elapsed < 650000000L) {
            return this.enterEasing.apply((float)elapsed / 6.5E8F);
         } else {
            long exitStartsAt = 650000000L + 2700000000L;
            if (elapsed < exitStartsAt) {
               return 1.0F;
            } else if (elapsed < exitStartsAt + 650000000L) {
               float progress = (float)(elapsed - exitStartsAt) / 6.5E8F;
               return 1.0F - this.exitEasing.apply(progress);
            } else {
               return 0.0F;
            }
         }
      }
   }
}
