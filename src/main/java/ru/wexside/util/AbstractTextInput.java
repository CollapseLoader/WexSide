package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.input.InputBindings;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.GuiInteractionState;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseHitTest;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.ThemeColors;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;

public abstract class AbstractTextInput
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider,
   MouseHitTest {
   public static final float HEIGHT = 11.5F;
   protected float visibilityAnimation = 1.0F;
   protected boolean enabled;
   protected final Runnable runnable;
   protected float clickAnimation;
   protected float hoverAnimation;

   protected AbstractTextInput(Runnable runnable) {
      super(new GuiBounds(0.0F, 0.0F, 0.0F, 11.5F));
      this.runnable = runnable;
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (n3 == 0 && this.isActive2() && this.getBounds().contains((float)n, (float)n2)) {
         this.enabled = true;
         if (this.runnable != null) {
            this.runnable.run();
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public final float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      GuiInteractionState guiInteractionState = GuiInteractionState.getInstance();
      boolean bl = this.isActive2();
      if (this.enabled && !InputBindings.isMouseButtonPressed(0)) {
         this.enabled = false;
      }

      this.visibilityAnimation = FrameInterpolator.lerpTowards(this.visibilityAnimation, bl ? 1.0F : 0.0F, 45.0F);
      this.hoverAnimation = FrameInterpolator.lerpTowards(
         this.hoverAnimation, bl && this.process13(guiInteractionState.getScaledMouseX(), guiInteractionState.getScaledMouseY()) ? 1.0F : 0.0F, 20.0F
      );
      this.clickAnimation = FrameInterpolator.lerpTowards(this.clickAnimation, this.enabled ? 1.0F : 0.0F, 30.0F);
      bounds2.setSize(this.getWidth(), 11.5F);
      if (this.visibilityAnimation <= 0.01F) {
         return bounds2.getY() + 11.5F;
      } else {
         float f2 = Math.max(this.hoverAnimation, Math.max(this.clickAnimation, this.getActiveAnimation()));
         this.process5(drawApi, matrix4f, bounds2, this.visibilityAnimation);
         drawApi.drawRoundedRectangleOutlined(
            matrix4f,
            bounds2.getX(),
            bounds2.getY(),
            bounds2.getWidth(),
            11.5F,
            7.0F,
            1.0F,
            this.process6(f2, this.visibilityAnimation),
            this.process4(f2, this.visibilityAnimation)
         );
         this.process7(
            drawApi, matrix4f, bounds2, ColorUtils.withAlpha(ThemeColors.textSecondary(), 255.0F * this.visibilityAnimation), this.visibilityAnimation
         );
         return bounds2.getY() + 11.5F;
      }
   }

   protected float getActiveAnimation() {
      return 0.0F;
   }

   protected int process4(float f, float f2) {
      return ColorUtils.withAlpha(ThemeColors.borderPrimary(), 255.0F * f2);
   }

   protected void process5(GuiDrawApi drawApi, Matrix4f matrix4f, GuiBounds bounds2, float f) {
   }

   protected int process6(float f, float f2) {
      return ColorUtils.multiplyAlpha(ThemeColors.backgroundHover(), f * f2);
   }

   protected abstract void process7(GuiDrawApi var1, Matrix4f var2, GuiBounds var3, int var4, float var5);

   public abstract float getWidth();

   @Override
   public boolean process13(int n, int n2) {
      GuiBounds bounds2 = this.getBounds();
      float f = this.getAbsoluteX();
      float f2 = this.getAbsoluteY();
      return (float)n >= f && (float)n <= f + bounds2.getWidth() && (float)n2 >= f2 && (float)n2 <= f2 + bounds2.getHeight();
   }

   @Override
   public boolean isActive2() {
      return true;
   }
}
