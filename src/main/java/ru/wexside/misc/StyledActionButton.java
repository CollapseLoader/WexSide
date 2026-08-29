package ru.wexside.misc;

import java.util.function.IntSupplier;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class StyledActionButton
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider,
   MouseHitTest {
   private float value;
   private final String string2;
   private final IntSupplier intSupplier;
   private final Runnable runnable;
   private final float value2;
   private final IntSupplier intSupplier2;
   public static final float value3 = 14.0F;
   private final float value4 = 7.0F;
   private final float value5;

   public StyledActionButton(String string, IntSupplier intSupplier, IntSupplier intSupplier2, Runnable runnable) {
      super(new GuiBounds(0.0F, 0.0F, 0.0F, 14.0F));
      this.value2 = 6.5F;
      this.value5 = 0.1F;
      this.string2 = string;
      this.intSupplier = intSupplier;
      this.intSupplier2 = intSupplier2;
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
      this.value = FrameInterpolator.lerpTowards(
         this.value, this.process13(guiInteractionState.getScaledMouseX(), guiInteractionState.getScaledMouseY()) ? 1.0F : 0.0F, 20.0F
      );
      int n = ColorUtils.lerp(this.intSupplier.getAsInt(), ColorUtils.lerp(this.intSupplier.getAsInt(), -1, 0.1F), (double)this.value);
      drawApi.drawRoundedRectangle(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 7.0F, n);
      float f2 = FontRegistry.font2.process3(this.string2, 6.5F);
      float f3 = FontRegistry.font2.process4(this.string2, 6.5F);
      FontRegistry.font2
         .process2(
            matrix4f,
            drawApi,
            this.string2,
            bounds2.getX() + (bounds2.getWidth() - f2) / 2.0F,
            bounds2.getY() + (bounds2.getHeight() - f3) / 2.0F,
            6.5F,
            this.intSupplier2.getAsInt()
         );
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
