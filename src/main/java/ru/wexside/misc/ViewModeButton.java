package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class ViewModeButton
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private boolean enabled;
   private float value;
   public static final float value2 = 18.5F;
   public static final float value3 = 50.0F;
   private final String string3;
   private final String string4;

   public ViewModeButton(String string, String string2) {
      super(new GuiBounds(0.0F, 0.0F, 50.0F, 18.5F));
      this.string3 = string;
      this.string4 = string2;
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
      return this.getBounds().contains((float)n, (float)n2);
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      this.value = FrameInterpolator.lerpTowards(this.value, this.enabled ? 1.0F : 0.0F, 30.0F);
      int n = ColorUtils.lerp(ColorUtils.withAlpha(ThemeColors.accent(), 0.0F), ThemeColors.accent(), (double)this.value);
      int n2 = ColorUtils.lerp(ThemeColors.borderStrong(), ColorUtils.withAlpha(ThemeColors.borderStrong(), 0.0F), (double)this.value);
      int n3 = ColorUtils.lerp(ThemeColors.textSecondary(), ThemeColors.backgroundControl(), (double)this.value);
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
         n2
      );
      float f4 = 7.0F;
      float f5 = 5.5F;
      float f6 = this.string4 != null && !this.string4.isEmpty() ? FontRegistry.font3.process3(this.string4, f4) : 0.0F;
      float f7 = FontRegistry.font2.process3(this.string3, f5);
      float f8 = FontRegistry.font2.process4(this.string3, f5);
      if (f6 > 0.0F) {
         float f3 = bounds2.getX() + (bounds2.getWidth() - f6) / 2.0F;
         float f2 = bounds2.getY() + 2.0F;
         FontRegistry.font3.process5(matrix4f, drawApi, this.string4, f3, f2, f4, n3);
      }

      float f3 = bounds2.getX() + (bounds2.getWidth() - f7) / 2.0F;
      float f2 = bounds2.getY() + bounds2.getHeight() - f8 - 2.0F;
      FontRegistry.font2.process2(matrix4f, drawApi, this.string3, f3, f2, f5, n3);
      return bounds2.getY() + bounds2.getHeight();
   }

   public String getString() {
      return this.string4;
   }

   public String getString2() {
      return this.string3;
   }

   public boolean isActive() {
      return this.enabled;
   }

   public float getFloatType() {
      return this.value;
   }
}
