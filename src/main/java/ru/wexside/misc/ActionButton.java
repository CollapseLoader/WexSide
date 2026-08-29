package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class ActionButton
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider,
   MouseHitTest {
   private float value2;
   private final String string3;
   private final String string4;
   private final Runnable runnable;

   public ActionButton(String string, String string2, float f, float f2, Runnable runnable) {
      super(new GuiBounds(0.0F, 0.0F, f, f2));
      this.string3 = string;
      this.string4 = string2;
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
      GuiInteractionState guiInteractionState = GuiInteractionState.getInstance();
      this.value2 = FrameInterpolator.lerpTowards(
         this.value2, this.process13(guiInteractionState.getScaledMouseX(), guiInteractionState.getScaledMouseY()) ? 1.0F : 0.0F, 20.0F
      );
      int n = ColorUtils.lerp(ThemeColors.formatFieldFill(), ThemeColors.backgroundHover(), (double)this.value2);
      int n2 = ColorUtils.lerp(ThemeColors.borderPrimary(), ThemeColors.borderStrong(), (double)this.value2);
      drawApi.drawRoundedRectangleOutlined(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 8.0F, 1.0F, n, n2);
      int n3 = ThemeColors.textSecondary();
      float f2 = FontRegistry.font4.process3(this.string3, 6.0F);
      float f3 = this.string4 == null ? 0.0F : FontRegistry.font3.process3(this.string4, 6.0F);
      float f4 = f2 + (this.string4 == null ? 0.0F : 2.0F + f3);
      float f5 = bounds2.getX() + (bounds2.getWidth() - f4) / 2.0F;
      float f6 = bounds2.getY() + bounds2.getHeight() / 2.0F;
      FontRegistry.font4.process2(matrix4f, drawApi, this.string3, f5, f6 - FontRegistry.font4.process4(this.string3, 6.0F) / 2.0F, 6.0F, n3);
      if (this.string4 != null) {
         FontRegistry.font3
            .process5(matrix4f, drawApi, this.string4, f5 + f2 + 2.0F, f6 - FontRegistry.font3.process14(this.string4.charAt(0), 0.0F, 6.0F), 6.0F, n3);
      }

      return bounds2.getY() + bounds2.getHeight();
   }

   @Override
   public boolean process13(int n, int n2) {
      GuiBounds bounds2 = this.getBounds();
      float f = this.getAbsoluteX();
      float f2 = this.getAbsoluteY();
      return (float)n >= f && (float)n <= f + bounds2.getWidth() && (float)n2 >= f2 && (float)n2 <= f2 + bounds2.getHeight();
   }
}
