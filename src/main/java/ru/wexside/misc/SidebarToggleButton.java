package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.input.InputBindings;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class SidebarToggleButton
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private float value;
   private final ModuleBrowser moduleBrowser;
   private final String string;
   private boolean enabled;
   private float value2;
   private final String string2 = "w";

   public SidebarToggleButton(GuiBounds bounds2, ModuleBrowser moduleBrowser) {
      super(bounds2);
      this.string = "Свернуть всё";
      this.moduleBrowser = moduleBrowser;
      this.value = moduleBrowser.isActive2() ? 1.0F : 0.0F;
      this.getBounds().setSize(this.getFloatType(), 11.5F);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (!this.moduleBrowser.isActive2()) {
         return false;
      } else if (!this.getBounds().contains((float)n, (float)n2)) {
         return false;
      } else {
         if (n3 == 0) {
            this.enabled = true;
            this.moduleBrowser.update3();
         }

         return true;
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      if (this.enabled && !InputBindings.isMouseButtonPressed(0)) {
         this.enabled = false;
      }

      this.value = FrameInterpolator.lerpTowards(this.value, this.moduleBrowser.isActive2() ? 1.0F : 0.0F, 45.0F);
      this.value2 = FrameInterpolator.lerpTowards(this.value2, this.enabled ? 1.0F : 0.0F, 30.0F);
      if (this.value <= 0.01F) {
         return bounds2.getY() + bounds2.getHeight();
      } else {
         int n = (int)(255.0F * this.value);
         int n2 = (int)(255.0F * this.value2 * this.value);
         int n3 = ColorUtils.withAlpha(ThemeColors.backgroundHover(), (float)n2);
         drawApi.drawRoundedRectangle(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 7.0F, n3);
         drawApi.drawRoundedRectangleOutlined(
            matrix4f,
            bounds2.getX(),
            bounds2.getY(),
            bounds2.getWidth(),
            bounds2.getHeight(),
            7.0F,
            1.0F,
            ColorUtils.withAlpha(ThemeColors.backgroundPrimary(), 0.0F),
            ColorUtils.withAlpha(ThemeColors.borderPrimary(), (float)n)
         );
         float f2 = 5.75F;
         float f3 = 5.75F;
         float f4 = FontRegistry.font3.process3("w", f2);
         float f5 = FontRegistry.font2.process3("Свернуть всё", f3);
         float f6 = f4 + 3.0F + f5;
         float f7 = bounds2.getX() + (bounds2.getWidth() - f6) / 2.0F;
         float f8 = bounds2.getY() + (bounds2.getHeight() - FontRegistry.font3.process4("w", f2)) / 2.0F;
         float f9 = f7 + f4 + 3.0F;
         float f10 = bounds2.getY() + (bounds2.getHeight() - FontRegistry.font2.process4("Свернуть всё", f3)) / 2.0F;
         FontRegistry.font3.process5(matrix4f, drawApi, "w", f7, f8, f2, ColorUtils.withAlpha(ThemeColors.textSecondary(), (float)n));
         FontRegistry.font2.process2(matrix4f, drawApi, "Свернуть всё", f9, f10, f3, ColorUtils.withAlpha(ThemeColors.textSecondary(), (float)n));
         return bounds2.getY() + bounds2.getHeight();
      }
   }

   public float getFloatType() {
      float f = 5.75F;
      float f2 = 5.75F;
      float f3 = FontRegistry.font3.process3("w", f) + 3.0F + FontRegistry.font2.process3("Свернуть всё", f2);
      return f3 + 6.0F;
   }
}
