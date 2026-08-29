package ru.wexside.util;

import java.awt.Color;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.PreparedLayer;
import ru.wexside.setting.ColorSetting;
import ru.wexside.ui.GuiBounds;

public final class HueSlider
   extends AbstractColorSlider
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final float value = 3.0F;

   public HueSlider(GuiBounds bounds2, ColorSetting colorSetting, SliderMarkerRenderer sliderMarkerRenderer) {
      super(bounds2, colorSetting, sliderMarkerRenderer);
   }

   @Override
   protected float getMarkerY() {
      return this.getBounds().getY() + this.getBounds().getHeight() / 2.0F;
   }

   @Override
   protected void process(float f, float f2) {
      if (this.isActive()) {
         this.setFloatType2(this.process5(f, this.getBounds().getWidth()));
      } else {
         float[] fArray = this.getHsbComponents();
         this.process6(this.process5(f, this.getBounds().getWidth()), fArray[1], fArray[2], this.getIntType2());
      }
   }

   @Override
   protected void setMatrix4f(Matrix4f matrix4f) {
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      GuiBounds bounds2 = this.getBounds();
      float f = bounds2.getX();
      float f2 = bounds2.getY();
      float f3 = bounds2.getWidth();
      float f4 = bounds2.getHeight();
      PreparedLayer preparedLayer = drawApi.prepareDedicatedLayer(matrix4f, f, f2, f3, f4, 0.0F);
      drawApi.beginLayerFrame(preparedLayer.getTexture());
      Matrix4f matrix4f2 = new Matrix4f(preparedLayer.getContentMatrix()).translate(preparedLayer.contentX(), preparedLayer.contentY(), 0.0F);
      if (this.isActive()) {
         this.process3(drawApi, matrix4f2, 0.0F, 0.0F, f3, f4);
      } else {
         this.process2(drawApi, matrix4f2, 0.0F, 0.0F, f3, f4);
      }

      drawApi.endLayerFrame();
      drawApi.drawPreparedLayerRounded(matrix4f, preparedLayer, 3.0F, -1);
   }

   @Override
   protected float getMarkerX() {
      return this.isActive()
         ? this.getBounds().getX() + this.getFloatType4() * this.getBounds().getWidth()
         : this.getBounds().getX() + this.getHsbComponents()[0] * this.getBounds().getWidth();
   }

   @Override
   protected int getDisplayColor() {
      if (this.isActive()) {
         return super.getDisplayColor();
      } else {
         float[] fArray = this.getHsbComponents();
         return Color.HSBtoRGB(fArray[0], 1.0F, 1.0F) | 0xFF000000;
      }
   }

   private void process2(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4) {
      int[] nArray = new int[]{-65536, -256, -16711936, -16711681, -16776961, -65281, -65536};
      int n = nArray.length - 1;

      for(int i = 0; i < n; ++i) {
         float f5 = f + f3 * ((float)i / (float)n);
         float f6 = f + f3 * ((float)(i + 1) / (float)n);
         float f7 = f6 - f5;
         drawApi.drawColorGradient(matrix4f, f5, f2, f7, f4, nArray[i], nArray[i + 1], nArray[i + 1], nArray[i]);
      }
   }

   private void process3(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4) {
      int n = 18;

      for(int i = 0; i < n; ++i) {
         float f5 = (float)i / (float)n;
         float f6 = (float)(i + 1) / (float)n;
         float f7 = f + f3 * f5;
         float f8 = f + f3 * f6;
         float f9 = f8 - f7;
         int n2 = this.process9(f5) | 0xFF000000;
         int n3 = this.process9(f6) | 0xFF000000;
         drawApi.drawColorGradient(matrix4f, f7, f2, f9, f4, n2, n3, n3, n2);
      }
   }
}
