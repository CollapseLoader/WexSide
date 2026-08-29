package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.color.ColorChannel;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.SegmentedControl;
import ru.wexside.util.SegmentedControlStyle;

public final class ColorModeSelector
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final ColorSetting colorSetting;
   private final SegmentedControl segmentedControl;
   private final float value = 11.0F;
   private final SegmentedControlStyle segmentedControlStyle;

   public ColorModeSelector(GuiBounds bounds2, ColorSetting colorSetting) {
      super(bounds2);
      this.colorSetting = colorSetting;
      this.segmentedControlStyle = new SegmentedControlStyle().process12(7.0F).process6(6.0F);
      this.segmentedControl = new SegmentedControl(new GuiBounds(0.0F, 0.0F, 0.0F, 11.0F), this.segmentedControlStyle, "Первый цвет", "Второй цвет");
      this.segmentedControl.setIntConsumer(n -> colorSetting.setEditingChannel(n == 1 ? ColorChannel.SECONDARY : ColorChannel.PRIMARY));
      this.syncChannel();
      this.addChild(this.segmentedControl);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
      this.segmentedControl.update();
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (!this.getBounds().contains((float)n, (float)n2)) {
         return false;
      } else {
         int n4 = (int)((float)n - this.getBounds().getX());
         int n5 = (int)((float)n2 - this.getBounds().getY());
         return this.segmentedControl.onMousePressed(n4, n5, n3);
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      this.update4();
      this.syncChannel();
      this.segmentedControl.render(f, new Matrix4f(matrix4f).translate(this.getBounds().getX(), this.getBounds().getY(), 0.0F));
      return this.getBounds().getY() + this.getBounds().getHeight();
   }

   private void syncChannel() {
      this.segmentedControl.setIntType2(this.colorSetting.getEditingChannel() == ColorChannel.SECONDARY ? 1 : 0);
   }

   public float getFloatType2() {
      return 11.0F;
   }

   private void update4() {
      float f = this.getBounds().getWidth() / 2.0F;
      this.segmentedControlStyle.process2(f);
      this.segmentedControlStyle.process11(11.0F);
      this.segmentedControl.getBounds().setPosition(0.0F, 0.0F);
      this.segmentedControl.getBounds().setSize(this.getBounds().getWidth(), 11.0F);
      this.getBounds().setSize(this.getBounds().getWidth(), 11.0F);
   }
}
