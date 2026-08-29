package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class LabeledSegmentOption
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private Runnable runnable = () -> {
   };
   private final float titleHeight;
   private boolean enabled;
   private final float titleWidth;
   private final float optionHeight;
   private float selectionAnimation;
   private final String string3;
   private final String string4;

   public LabeledSegmentOption(String string, String string2, float f) {
      super(new GuiBounds(0.0F, 0.0F, 0.0F, f));
      this.string3 = string;
      this.string4 = string2;
      this.titleWidth = FontRegistry.font3.process3(string, 6.0F);
      this.titleHeight = FontRegistry.font3.process4(string, 6.0F);
      this.optionHeight = FontRegistry.font4.process4(string2, 6.0F);
      float f2 = FontRegistry.font4.process3(string2, 6.0F);
      float f3 = 4.0F + this.titleWidth + 2.0F + f2 + 4.0F;
      this.getBounds().setSize(f3, f);
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
         this.runnable.run();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      this.selectionAnimation = FrameInterpolator.lerpTowards(this.selectionAnimation, this.enabled ? 1.0F : 0.0F, 25.0F);
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
      if (this.selectionAnimation > 0.001F) {
         drawApi.drawRoundedRectangle(
            matrix4f,
            bounds2.getX(),
            bounds2.getY(),
            bounds2.getWidth(),
            bounds2.getHeight(),
            7.0F,
            ColorUtils.withAlpha(ThemeColors.accent(), 255.0F * this.selectionAnimation)
         );
      }

      int n = ColorUtils.lerp(ThemeColors.textSecondary(), ThemeColors.backgroundControl(), (double)this.selectionAnimation);
      float f2 = bounds2.getX() + 4.0F;
      float f3 = bounds2.getY() + (bounds2.getHeight() - this.titleHeight) / 2.0F;
      FontRegistry.font3.process5(matrix4f, drawApi, this.string3, f2, f3, 6.0F, n);
      float f4 = f2 + this.titleWidth + 2.0F;
      float f5 = bounds2.getY() + (bounds2.getHeight() - this.optionHeight) / 2.0F;
      FontRegistry.font4.process2(matrix4f, drawApi, this.string4, f4, f5, 6.0F, n);
      return bounds2.getY() + bounds2.getHeight();
   }

   public boolean isActive() {
      return this.enabled;
   }

   public void setRunnable(Runnable runnable) {
      this.runnable = runnable == null ? () -> {
      } : runnable;
   }
}
