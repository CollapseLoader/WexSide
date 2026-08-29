package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class SelectableButton
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private float value;
   private final Runnable runnable;
   private final String string3;
   private final float value2;
   private final float value3;
   private final float value4;
   private boolean enabled;
   private final float value5;
   private final String string4;
   private final float value6;
   private final float value7 = 7.0F;

   public SelectableButton(String string, String string2, float f, float f2, Runnable runnable) {
      super(new GuiBounds(0.0F, 0.0F, f, f2));
      this.value6 = 0.75F;
      this.value3 = 6.0F;
      this.value2 = 2.5F;
      this.value4 = 6.0F;
      this.value5 = 15.0F;
      this.string3 = string == null ? "" : string;
      this.string4 = string2 == null ? "" : string2;
      this.runnable = runnable;
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
      if (n3 == 0 && this.getBounds().contains((float)n, (float)n2)) {
         if (this.runnable != null) {
            this.runnable.run();
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      this.value = FrameInterpolator.lerpTowards(this.value, this.enabled ? 1.0F : 0.0F, 15.0F);
      int n = ColorUtils.lerp(ThemeColors.controlFill(), ThemeColors.accent(), (double)this.value);
      int n2 = ColorUtils.lerp(ThemeColors.borderPrimary(), ColorUtils.withAlpha(ThemeColors.accent(), 0.0F), (double)this.value);
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
      float f2 = FontRegistry.font3.process3(this.string3, 6.0F);
      float f3 = FontRegistry.font3.process4(this.string3, 6.0F);
      float f4 = FontRegistry.font2.process3(this.string4, 6.0F);
      float f5 = FontRegistry.font2.process4(this.string4, 6.0F);
      float f6 = f2 + 2.5F + f4;
      float f7 = bounds2.getX() + (bounds2.getWidth() - f6) / 2.0F;
      FontRegistry.font3.process5(matrix4f, drawApi, this.string3, f7, bounds2.getY() + (bounds2.getHeight() - f3) / 2.0F, 6.0F, n3);
      FontRegistry.font2.process2(matrix4f, drawApi, this.string4, f7 + f2 + 2.5F, bounds2.getY() + (bounds2.getHeight() - f5) / 2.0F, 6.0F, n3);
      return bounds2.getY() + bounds2.getHeight();
   }
}
