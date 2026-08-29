package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.ui.GuiBounds;
import ru.wexside.util.EspBoxBorderRenderer;
import ru.wexside.util.GuiDrawApi;

final class EspBoxDecorationRenderer {
   private static final float value = 0.6F;
   private static final float value2 = 0.7F;

   void member9165(GuiDrawApi drawApi, Matrix4f matrix4f, GuiBounds bounds2, BoxEspSettings rectangle) {
      float f = bounds2.getX();
      float f2 = bounds2.getY();
      float f3 = bounds2.getX() + bounds2.getWidth();
      float f4 = bounds2.getY() + bounds2.getHeight();
      if (rectangle.isBoxEnabled()) {
         int n = rectangle.getBoxColor(0.0F);
         int n2 = rectangle.getBoxColor(0.25F);
         int n3 = rectangle.getBoxColor(0.5F);
         int n4 = rectangle.getBoxColor(0.75F);
         if (rectangle.isCornerStyle()) {
            EspBoxBorderRenderer.process4(drawApi, matrix4f, f, f2, f3, f4, n, n2, n3, n4);
         } else {
            EspBoxBorderRenderer.process(drawApi, matrix4f, f, f2, f3, f4, n, n2, n3, n4);
         }
      }

      if (rectangle.isArmorBarEnabled()) {
         EspBoxBorderRenderer.process5(drawApi, matrix4f, f, f4 + 0.5F, f3, f4 + 2.0F + 0.5F, 1073741824);
         EspBoxBorderRenderer.process3(
            drawApi,
            matrix4f,
            f + 0.5F,
            f4 + 1.0F,
            f + 0.5F + (f3 - 0.5F - (f + 0.5F)) * 0.6F,
            f4 + 2.0F,
            rectangle.getArmorColor(0.25F),
            rectangle.getArmorColor(0.75F)
         );
      }

      if (rectangle.isHealthBarEnabled()) {
         EspBoxBorderRenderer.process5(drawApi, matrix4f, f - 2.5F, f2, f - 0.5F, f4, 1073741824);
         EspBoxBorderRenderer.process2(
            drawApi,
            matrix4f,
            f - 2.0F,
            f2 + 0.5F + (f4 - 1.0F - f2) - (f4 - 1.0F - f2) * 0.7F,
            f - 1.0F,
            f4 - 0.5F,
            rectangle.getHealthColor(0.0F),
            rectangle.getHealthColor(0.5F)
         );
      }
   }
}
