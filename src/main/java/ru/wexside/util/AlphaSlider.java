package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.setting.ColorSetting;
import ru.wexside.ui.GuiBounds;

public final class AlphaSlider
   extends AbstractColorSlider
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final float value = 3.0F;

   public AlphaSlider(GuiBounds bounds2, ColorSetting colorSetting, SliderMarkerRenderer sliderMarkerRenderer) {
      super(bounds2, colorSetting, sliderMarkerRenderer);
   }

   @Override
   protected float getMarkerY() {
      return this.getBounds().getY() + this.getBounds().getHeight() / 2.0F;
   }

   @Override
   protected void process(float f, float f2) {
      float[] fArray = this.getHsbComponents();
      this.process6(fArray[0], fArray[1], fArray[2], Math.round(this.process8(f / this.getBounds().getWidth()) * 255.0F));
   }

   @Override
   protected void setMatrix4f(Matrix4f matrix4f) {
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      GuiBounds bounds2 = this.getBounds();
      float f = bounds2.getX();
      float f2 = bounds2.getY();
      float f3 = bounds2.getWidth();
      float f4 = bounds2.getHeight();
      int n = this.getBaseColor() | 0xFF000000;
      int n2 = n & 16777215;
      drawApi.drawRoundedRectangleGradient(matrix4f, f, f2, f3, f4, 3.0F, n2, n, n, n2);
   }

   @Override
   protected float getMarkerX() {
      return this.getBounds().getX() + (float)this.getIntType2() / 255.0F * this.getBounds().getWidth();
   }
}
