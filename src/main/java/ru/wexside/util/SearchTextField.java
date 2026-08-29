package ru.wexside.util;

import java.util.function.BooleanSupplier;
import org.joml.Matrix4f;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseHitTest;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.TextInputController;
import ru.wexside.misc.TextInputModel;
import ru.wexside.misc.TextLayoutUtils;
import ru.wexside.misc.ThemeColors;
import ru.wexside.ui.GuiBounds;

public class SearchTextField
   extends AbstractTextInput
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider,
   MouseHitTest {
   public static final float value = 128.0F;
   private float activeAnimation;
   private float caretAnimation;
   private float horizontalScroll;
   private final boolean enabled;
   private final TextInputModel callback15;
   private final String string4;
   public static final int slot = 48;
   private final BooleanSupplier booleanSupplier;
   private final TextInputController textInputController;

   public SearchTextField(TextInputModel callback15, BooleanSupplier booleanSupplier, String string, boolean bl) {
      super((Runnable)null);
      this.callback15 = callback15;
      this.string4 = string;
      this.enabled = bl;
      this.booleanSupplier = booleanSupplier;
      this.textInputController = new TextInputController(callback15);
      this.getBounds().setSize(128.0F, 11.5F);
   }

   @Override
   public void update() {
      if (!this.isActive2()) {
         this.textInputController.blur();
      }

      this.textInputController.tick();
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (!this.isActive2()) {
         return false;
      } else if (this.getBounds().contains((float)n, (float)n2)) {
         this.callback15.setText("");
         return true;
      } else {
         return this.textInputController.onMousePressed(this.getBounds(), n, n2, n3);
      }
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      this.textInputController.blurIfOutside(this.getBounds(), n, n2);
   }

   @Override
   public boolean onCharTyped(char c) {
      return this.textInputController.onCharTyped(c);
   }

   @Override
   public void update2() {
      this.textInputController.blur();
      super.update2();
   }

   @Override
   public boolean onKeyPressed(int n) {
      if (!this.textInputController.isFocused()) {
         return false;
      } else if (n != 256 && n != 257 && n != 335) {
         return this.textInputController.onKeyPressed(n);
      } else {
         this.textInputController.blur();
         return true;
      }
   }

   public void update4() {
      this.textInputController.focus();
   }

   private GuiBounds getClearButtonBounds() {
      if (!this.hasClearButton()) {
         return new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F);
      } else {
         GuiBounds bounds2 = super.getBounds();
         float f = FontRegistry.font3.process3("d", 6.0F) + 4.0F;
         return new GuiBounds(bounds2.getX() + bounds2.getWidth() - f, bounds2.getY(), f, 11.5F);
      }
   }

   private boolean hasClearButton() {
      return this.enabled && !this.textInputController.getText().isEmpty();
   }

   @Override
   protected float getActiveAnimation() {
      this.activeAnimation = FrameInterpolator.lerpTowards(this.activeAnimation, this.textInputController.isFocused() ? 1.0F : 0.0F, 25.0F);
      return this.activeAnimation;
   }

   @Override
   protected int process4(float f, float f2) {
      return ColorUtils.withAlpha(ColorUtils.lerp(ThemeColors.borderPrimary(), ThemeColors.accent(), (double)this.activeAnimation), 255.0F * f2);
   }

   @Override
   protected void process7(GuiDrawApi drawApi, Matrix4f matrix4f, GuiBounds bounds2, int n, float f) {
      float f3 = bounds2.getX() + 4.0F;
      float f4 = bounds2.getY() + (11.5F - FontRegistry.font3.process4("Ф", 6.0F)) / 2.0F;
      FontRegistry.font3.process5(matrix4f, drawApi, "Ф", f3, f4, 6.0F, n);
      float f5 = f3 + FontRegistry.font3.process3("Ф", 6.0F) + 3.0F;
      float f6 = bounds2.getX() + bounds2.getWidth() - 4.0F - f5;
      String string = this.textInputController.getText();
      boolean bl = this.textInputController.isFocused();
      if (this.hasClearButton()) {
         float f2 = FontRegistry.font3.process3("d", 6.0F);
         f6 -= f2 + 3.0F;
         FontRegistry.font3
            .process5(
               matrix4f,
               drawApi,
               "d",
               bounds2.getX() + bounds2.getWidth() - 4.0F - f2,
               bounds2.getY() + (11.5F - FontRegistry.font3.process4("d", 6.0F)) / 2.0F,
               6.0F,
               n
            );
      }

      if (!bl) {
         String string2 = string.isEmpty() ? this.string4 : TextLayoutUtils.trimToWidth(string, FontRegistry.font2, 6.0F, f6);
         int n2 = string.isEmpty() ? n : ColorUtils.withAlpha(ThemeColors.textPrimary(), 255.0F * f);
         float f7 = bounds2.getY() + (11.5F - FontRegistry.font2.process4(string2, 6.0F)) / 2.0F;
         FontRegistry.font2.process2(matrix4f, drawApi, string2, f5, f7, 6.0F, n2);
         this.horizontalScroll = 0.0F;
      } else {
         float f2 = FontRegistry.font2.process3("|", 6.0F);
         float f8 = FontRegistry.font2.process3(string, 6.0F) + f2;
         this.horizontalScroll = FrameInterpolator.lerpTowards(this.horizontalScroll, Math.max(0.0F, f8 - f6), 30.0F);
         this.caretAnimation = FrameInterpolator.lerpTowards(this.caretAnimation, this.textInputController.isAllSelected() ? 1.0F : 0.0F, 30.0F);
         float f9 = f5 - this.horizontalScroll;
         float f10 = bounds2.getY() + (11.5F - FontRegistry.font2.process4(string, 6.0F)) / 2.0F;
         int n3 = ThemeColors.textPrimary();
         int n4 = ColorUtils.withAlpha(ColorUtils.lerp(n3, ThemeColors.adjustForTheme(n3), (double)this.caretAnimation), 255.0F * f);
         drawApi.beginStencil(1);
         drawApi.fillRectangle(matrix4f, f5, bounds2.getY(), f6, 11.5F, ColorUtils.withAlpha(-1, 0.0F));
         drawApi.applyStencilMask(1);
         FontRegistry.font2.process2(matrix4f, drawApi, string, f9, f10, 6.0F, n4);
         if (this.textInputController.isCaretVisible()) {
            FontRegistry.font2
               .process2(matrix4f, drawApi, "|", f9 + FontRegistry.font2.process3(string, 6.0F), f10, 6.0F, ColorUtils.withAlpha(n3, 255.0F * f));
         }

         drawApi.endStencil();
      }
   }

   @Override
   public float getWidth() {
      return 128.0F;
   }

   public boolean isActive() {
      return this.textInputController.isFocused();
   }

   @Override
   public boolean isActive2() {
      return this.booleanSupplier.getAsBoolean();
   }
}
