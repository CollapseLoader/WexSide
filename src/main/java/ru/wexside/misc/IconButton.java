package ru.wexside.misc;

import java.util.function.BooleanSupplier;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.input.InputBindings;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public class IconButton
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final String primaryLabel;
   private float value;
   private float value2;
   private boolean enabled;
   private final Runnable runnable;
   private final BooleanSupplier booleanSupplier;
   private final String secondaryLabel;

   public IconButton(GuiBounds bounds2, String string, String string2, Runnable runnable) {
      this(bounds2, string, string2, runnable, null);
   }

   public IconButton(GuiBounds bounds2, String string, Runnable runnable) {
      this(bounds2, string, null, runnable, null);
   }

   public IconButton(GuiBounds bounds2, String string) {
      this(bounds2, string, null, null, null);
   }

   public IconButton(GuiBounds bounds2, String string, String string2, Runnable runnable, BooleanSupplier booleanSupplier) {
      super(bounds2);
      this.primaryLabel = string;
      this.secondaryLabel = string2;
      this.runnable = runnable;
      this.booleanSupplier = booleanSupplier;
   }

   public IconButton(GuiBounds bounds2, String string, Runnable runnable, BooleanSupplier booleanSupplier) {
      this(bounds2, string, null, runnable, booleanSupplier);
   }

   public void setFloatType(float f) {
      this.value2 = f;
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (this.getBounds().contains((float)n, (float)n2)) {
         if (this.booleanSupplier == null) {
            this.enabled = true;
         }

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
      if (this.booleanSupplier != null) {
         this.enabled = this.booleanSupplier.getAsBoolean();
      } else if (this.enabled && !InputBindings.isMouseButtonPressed(0)) {
         this.enabled = false;
      }

      this.value = FrameInterpolator.lerpTowards(this.value, this.enabled ? 1.0F : 0.0F, 30.0F);
      int n = ColorUtils.lerp(ColorUtils.withAlpha(ThemeColors.borderSoft(), 0.0F), ThemeColors.borderSoft(), (double)this.value);
      float f2 = this.getBounds().getX();
      drawApi.drawRoundedRectangleBordered(
         matrix4f, f2, this.getBounds().getY() + 0.25F, this.getBounds().getWidth(), this.getBounds().getHeight() - 0.25F, 8.0F, 0.0F, n
      );
      drawApi.drawRoundedOutline(
         matrix4f, f2, this.getBounds().getY(), this.getBounds().getWidth(), this.getBounds().getHeight(), 8.0F, 1.25F, ThemeColors.borderPrimary()
      );
      String string = this.value2 > 0.5F && this.secondaryLabel != null ? this.secondaryLabel : this.primaryLabel;
      float f3 = FontRegistry.font3.process3(string, 7.0F);
      float f4 = FontRegistry.font3.process4(string, 7.0F);
      int n2 = ColorUtils.lerp(ThemeColors.textMuted(), ThemeColors.textPrimary(), (double)this.value);
      FontRegistry.font3
         .process5(
            matrix4f,
            drawApi,
            string,
            f2 + this.getBounds().getWidth() / 2.0F - f3 / 2.0F,
            this.getBounds().getY() + this.getBounds().getHeight() / 2.0F - f4 / 2.0F,
            7.0F,
            n2
         );
      return bounds2.getY() + bounds2.getHeight();
   }
}
