package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.ColorModeLabels;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.ThemeColors;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;

final class ColorModeOptionButton
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final float value = 7.0F;
   private final float value2 = 0.75F;
   private final float value3;
   private final ColorModeLabels colorModeLabels;
   private final float value4 = 6.5F;
   private boolean enabled;
   private float value5;

   public ColorModeOptionButton(GuiBounds bounds2, ColorModeLabels colorModeLabels) {
      super(bounds2);
      this.value3 = 30.0F;
      this.colorModeLabels = colorModeLabels;
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void setBooleanType(boolean bl) {
      this.enabled = bl;
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      return n3 == 0 && this.getBounds().contains((float)n, (float)n2);
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      this.value5 = FrameInterpolator.lerpTowards(this.value5, this.enabled ? 1.0F : 0.0F, 30.0F);
      int n = ColorUtils.withAlpha(ThemeColors.backgroundHover(), (float)Math.round(255.0F * this.value5));
      int n2 = ColorUtils.lerp(ThemeColors.textMuted(), ThemeColors.textPrimary(), (double)this.value5);
      drawApi.drawRoundedRectangle(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 7.0F, n);
      drawApi.drawRoundedRectangleOutlined(
         matrix4f,
         bounds2.getX(),
         bounds2.getY(),
         bounds2.getWidth(),
         bounds2.getHeight(),
         7.0F,
         0.75F,
         ColorUtils.withAlpha(ThemeColors.backgroundPrimary(), 0.0F),
         ThemeColors.borderPrimary()
      );
      float f2 = FontRegistry.font3.process3(this.colorModeLabels.getString2(), 6.5F);
      float f3 = FontRegistry.font3.process4(this.colorModeLabels.getString2(), 6.5F);
      float f4 = bounds2.getX() + (bounds2.getWidth() - f2) / 2.0F;
      float f5 = bounds2.getY() + (bounds2.getHeight() - f3) / 2.0F;
      FontRegistry.font3.process5(matrix4f, drawApi, this.colorModeLabels.getString2(), f4, f5, 6.5F, n2);
      return bounds2.getX() + bounds2.getWidth();
   }

   public ColorModeLabels getColorModeLabels() {
      return this.colorModeLabels;
   }
}
