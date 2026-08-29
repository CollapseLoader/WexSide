package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.ThemeColors;
import ru.wexside.ui.GuiBounds;

public final class ToggleIndicatorRenderer {
   private float value;
   private final float value2 = 30.0F;
   private final float value3 = 0.75F;

   public void process(Matrix4f matrix4f, GuiDrawApi drawApi, GuiBounds bounds2, int n, boolean bl) {
      float f;
      float f2 = f = Math.min(bounds2.getWidth(), bounds2.getHeight());
      float f3 = f / 2.0F;
      float f4 = bounds2.getX() + bounds2.getWidth() / 2.0F;
      float f5 = bounds2.getY() + bounds2.getHeight() / 2.0F;
      float f6 = Math.max(1.0F, f * 0.18F);
      this.value = FrameInterpolator.lerpTowards(this.value, bl ? 1.0F : 0.0F, 30.0F);
      drawApi.drawRoundedRectangleBordered(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), f2, 0.0F, n);
      drawApi.drawCircleSector(matrix4f, f4, f5, 0.0F, 360.0F, f6, f3, ColorUtils.withAlpha(ThemeColors.backgroundControl(), 255.0F * this.value), 0.75F);
   }
}
