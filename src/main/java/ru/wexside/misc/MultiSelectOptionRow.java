package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.ui.GuiBounds;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public class MultiSelectOptionRow
   extends CompactOptionRow
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider,
   MouseHitTest {
   public MultiSelectOptionRow(String string, String string2, float f) {
      super(string, string2, f);
   }

   public MultiSelectOptionRow(String string, String string2) {
      super(string, string2);
   }

   @Override
   protected float getFloatType() {
      return 5.5F;
   }

   @Override
   protected void process(Matrix4f matrix4f, GuiDrawApi drawApi, GuiBounds bounds2) {
      float f = this.getFloatType2();
      float f2 = 7.5F;
      float f3 = bounds2.getX() + bounds2.getWidth() - 12.0F;
      float f4 = bounds2.getY() + (bounds2.getHeight() - f2) / 2.0F;
      int n = ColorUtils.lerp(ThemeColors.borderStrong(), ThemeColors.accent(), (double)f);
      drawApi.drawRoundedRectangle(matrix4f, f3, f4, f2, f2, 4.0F, n);
      float f5 = this.getFloatType();
      int n2 = ColorUtils.withAlpha(ThemeColors.backgroundControl(), 255.0F * f);
      float f6 = f3 + (f2 - FontRegistry.font3.process3("T", f5)) / 2.0F;
      float f7 = f4 + (f2 - FontRegistry.font3.process4("T", f5)) / 2.0F;
      FontRegistry.font3.process2(matrix4f, drawApi, "T", f6, f7, f5, n2);
   }
}
