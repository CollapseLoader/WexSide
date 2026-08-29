package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.AbstractColorTextEditor;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.ColorTextFormat;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.setting.ColorSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;

public final class ColorTextEditor
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private ColorTextFormat colorTextFormat;
   private final float value = 30.0F;
   private final RgbaColorEditor rgbaColorEditor;
   private float value2;
   private final HexColorEditor hexColorEditor;

   public ColorTextEditor(GuiBounds bounds2, ColorSetting colorSetting, ColorTextFormat colorTextFormat) {
      super(bounds2);
      this.hexColorEditor = new HexColorEditor(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F), colorSetting);
      this.rgbaColorEditor = new RgbaColorEditor(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F), colorSetting);
      this.colorTextFormat = colorTextFormat;
      this.value2 = colorTextFormat == ColorTextFormat.RGBA ? 1.0F : 0.0F;
      this.addChild(this.hexColorEditor);
      this.addChild(this.rgbaColorEditor);
      this.update4();
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
      this.process6(this.colorTextFormat).update();
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (!this.getBounds().contains((float)n, (float)n2)) {
         return false;
      } else {
         int n4 = (int)((float)n - this.getBounds().getX());
         int n5 = (int)((float)n2 - this.getBounds().getY());
         return this.process6(this.colorTextFormat).onMousePressed(n4, n5, n3) || this.getBounds().contains((float)n, (float)n2);
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      this.update4();
      this.value2 = FrameInterpolator.lerpTowards(this.value2, this.colorTextFormat == ColorTextFormat.RGBA ? 1.0F : 0.0F, 30.0F);
      Matrix4f matrix4f2 = new Matrix4f(matrix4f).translate(this.getBounds().getX(), this.getBounds().getY(), 0.0F);
      this.process7(f, matrix4f2, this.hexColorEditor, 1.0F - this.value2);
      this.process7(f, matrix4f2, this.rgbaColorEditor, this.value2);
      return this.getBounds().getY() + this.getBounds().getHeight();
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      int n4 = (int)((float)n - this.getBounds().getX());
      int n5 = (int)((float)n2 - this.getBounds().getY());
      this.hexColorEditor.onMouseReleased(n4, n5, n3);
      this.rgbaColorEditor.onMouseReleased(n4, n5, n3);
   }

   @Override
   public boolean onCharTyped(char c) {
      return this.process6(this.colorTextFormat).onCharTyped(c);
   }

   @Override
   public void update2() {
      this.hexColorEditor.update2();
      this.rgbaColorEditor.update2();
   }

   @Override
   public boolean onKeyPressed(int n) {
      return this.process6(this.colorTextFormat).onKeyPressed(n);
   }

   public float getFloatType() {
      return Math.max(this.hexColorEditor.getFloatType(), this.rgbaColorEditor.getFloatType());
   }

   private AbstractColorTextEditor process6(ColorTextFormat colorTextFormat) {
      return (AbstractColorTextEditor)(colorTextFormat == ColorTextFormat.RGBA ? this.rgbaColorEditor : this.hexColorEditor);
   }

   public void setColorTextFormat(ColorTextFormat colorTextFormat) {
      ColorTextFormat colorTextFormat2 = colorTextFormat == null ? ColorTextFormat.HEX : colorTextFormat;
      if (this.colorTextFormat != colorTextFormat2) {
         this.process6(this.colorTextFormat).update2();
         this.colorTextFormat = colorTextFormat2;
      }
   }

   private void process7(float f, Matrix4f matrix4f2, AbstractColorTextEditor abstractColorTextEditor, float f2) {
      if (!(f2 <= 0.01F)) {
         ClippedLayerRenderer.process(
            WexSideClient.getGuiRenderer(),
            matrix4f2,
            abstractColorTextEditor.getBounds().getX(),
            abstractColorTextEditor.getBounds().getY(),
            abstractColorTextEditor.getBounds().getWidth(),
            abstractColorTextEditor.getBounds().getHeight(),
            0.0F,
            f2 < 0.99F,
            ColorUtils.withAlpha(-1, 255.0F * f2),
            matrix4f -> abstractColorTextEditor.render(f, matrix4f)
         );
      }
   }

   public float getFloatType2() {
      return this.process6(this.colorTextFormat).getFloatType2();
   }

   private void update4() {
      float f = this.getFloatType();
      float f2 = this.getFloatType2();
      this.hexColorEditor.getBounds().setPosition(0.0F, 0.0F);
      this.hexColorEditor.getBounds().setSize(f, f2);
      this.rgbaColorEditor.getBounds().setPosition(0.0F, 0.0F);
      this.rgbaColorEditor.getBounds().setSize(f, f2);
      this.getBounds().setSize(f, f2);
   }
}
