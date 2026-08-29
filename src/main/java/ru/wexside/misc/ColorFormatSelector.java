package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.setting.ColorSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorTextEditor;
import ru.wexside.util.SegmentedControl;

public final class ColorFormatSelector
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final SegmentedControl segmentedControl;
   private final float value;
   private final ColorTextEditor colorTextEditor;
   private final float value2 = 2.0F;

   public ColorFormatSelector(GuiBounds bounds2, ColorSetting colorSetting) {
      super(bounds2);
      this.value = 12.0F;
      this.segmentedControl = new SegmentedControl(new GuiBounds(0.0F, 0.0F, 0.0F, 12.0F), ColorTextFormat.HEX.title, ColorTextFormat.RGBA.title);
      this.colorTextEditor = new ColorTextEditor(new GuiBounds(0.0F, 0.0F, 0.0F, 12.0F), colorSetting, ColorTextFormat.HEX);
      this.segmentedControl.setIntConsumer(n -> this.colorTextEditor.setColorTextFormat(ColorTextFormat.fromIndex(n)));
      this.addChild(this.segmentedControl);
      this.addChild(this.colorTextEditor);
      this.update4();
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
      for(GuiElement element2 : this.children) {
         element2.update();
      }
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (!this.getBounds().contains((float)n, (float)n2)) {
         return false;
      } else {
         int n5 = (int)((float)n - this.getBounds().getX());
         return super.onMousePressed(n5, (int)((float)n2 - this.getBounds().getY()), n3) || this.getBounds().contains((float)n, (float)n2);
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      this.update4();
      Matrix4f matrix4f2 = new Matrix4f(matrix4f).translate(this.getBounds().getX(), this.getBounds().getY(), 0.0F);

      for(GuiElement element2 : this.children) {
         element2.render(f, matrix4f2);
      }

      return this.getBounds().getY() + this.getBounds().getHeight();
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      int n4 = (int)((float)n - this.getBounds().getX());
      int n5 = (int)((float)n2 - this.getBounds().getY());
      super.onMouseReleased(n4, n5, n3);
   }

   public float getFloatType() {
      return this.segmentedControl.getSegmentedControlStyle().getFloatType5() * (float)ColorTextFormat.values().length
         + 2.0F
         + this.colorTextEditor.getFloatType();
   }

   public SegmentedControl getSegmentedControl() {
      return this.segmentedControl;
   }

   public float getSpacing() {
      return 2.0F;
   }

   public ColorTextEditor getColorTextEditor() {
      return this.colorTextEditor;
   }

   public float getFloatType3() {
      return 12.0F;
   }

   public float getFloatType2() {
      return 12.0F;
   }

   private void update4() {
      float f = this.segmentedControl.getSegmentedControlStyle().getFloatType5() * (float)ColorTextFormat.values().length;
      float f2 = this.colorTextEditor.getFloatType();
      this.segmentedControl.getBounds().setPosition(0.0F, 0.0F);
      this.segmentedControl.getBounds().setSize(f, 12.0F);
      this.colorTextEditor.getBounds().setPosition(f + 2.0F, 0.0F);
      this.colorTextEditor.getBounds().setSize(f2, 12.0F);
      this.getBounds().setSize(f + 2.0F + f2, 12.0F);
   }
}
