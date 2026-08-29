package ru.wexside.prediction;

import net.minecraft.class_1799;
import ru.wexside.render.BakedItemIcon;

public final class LandingTimeLabel {
   public final float x;
   public final float y;
   public final float width;
   public final float height;
   public final float iconX;
   public final float iconY;
   public final float textX;
   public final float textY;
   public final String text;
   public final class_1799 item;
   public BakedItemIcon bakedIcon;

   public LandingTimeLabel(float x, float y, float width, float height, float iconX, float iconY, float textX, float textY, String text, class_1799 item) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.iconX = iconX;
      this.iconY = iconY;
      this.textX = textX;
      this.textY = textY;
      this.text = text;
      this.item = item;
   }
}
