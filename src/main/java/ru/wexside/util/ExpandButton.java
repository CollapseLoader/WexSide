package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
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

public final class ExpandButton
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private boolean expanded = false;
   private float value;
   private boolean interactive = true;

   public ExpandButton(GuiBounds bounds2) {
      super(bounds2);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (!this.interactive) {
         return false;
      } else if (!this.getBounds().contains((float)n, (float)n2)) {
         return false;
      } else {
         if (n3 == 0) {
            this.expanded = !this.expanded;
         }

         return true;
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      this.value = FrameInterpolator.lerpTowards(this.value, this.expanded ? 1.0F : 0.0F, 15.0F);
      drawApi.drawRoundedRectangleOutlined(
         matrix4f,
         bounds2.getX(),
         bounds2.getY(),
         bounds2.getWidth(),
         bounds2.getHeight(),
         7.0F,
         1.0F,
         ColorUtils.withAlpha(ThemeColors.backgroundPrimary(), 0.0F),
         ThemeColors.borderPrimary()
      );
      float f2 = 6.0F;
      float f3 = FontRegistry.font3.process3("F", f2);
      float f4 = FontRegistry.font3.process4("F", f2);
      float f5 = bounds2.getX() + (bounds2.getWidth() - f3) / 2.0F;
      float f6 = bounds2.getY() + (bounds2.getHeight() - f4) / 2.0F;
      float f7 = bounds2.getX() + bounds2.getWidth() / 2.0F;
      float f8 = bounds2.getY() + bounds2.getHeight() / 2.0F;
      Matrix4f matrix4f2 = new Matrix4f(matrix4f)
         .translate(f7, f8, 0.0F)
         .rotateZ((float)Math.toRadians((double)(90.0F * this.value)))
         .translate(-f7, -f8, 0.0F);
      FontRegistry.font3.process5(matrix4f2, drawApi, "F", f5, f6, f2, ThemeColors.textMuted());
      return bounds2.getY() + bounds2.getHeight();
   }

   public boolean isActive() {
      return this.expanded;
   }

   public float getFloatType() {
      return this.value;
   }

   public void setExpanded(boolean expanded) {
      this.expanded = expanded;
   }

   public void setInteractive(boolean interactive) {
      this.interactive = interactive;
   }

   @Override
   public boolean isActive2() {
      return this.interactive;
   }
}
