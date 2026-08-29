package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.setting.ColorSetting;
import ru.wexside.setting.color.ColorChannel;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.AlphaSlider;
import ru.wexside.util.ClippedLayerRenderer;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.ColorValueSlider;
import ru.wexside.util.DoubleColor;
import ru.wexside.util.HueSaturationPicker;
import ru.wexside.util.HueSlider;
import ru.wexside.util.SliderMarkerRenderer;

public final class ColorPicker
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final HueSlider hueSlider;
   private final DoubleColor doubleColor;
   private final float value;
   private final float value2;
   private final float value3;
   private int slot;
   private final ColorPreviewButton colorPreviewButton;
   private final float value4;
   private final float value5;
   private final float value6;
   private final ColorValueSlider colorValueSlider;
   private final ColorModeSelector colorModeSelector;
   private final AlphaSlider alphaSlider;
   private final float value7;
   private final float value8;
   private final float value9;
   private ColorChannel colorChannel;
   private final float value10;
   private final float value11;
   private final float value12;
   private final float value13;
   private final HueSaturationPicker hueSaturationPicker;
   private final float value14;
   private float value15;
   private final float value16;
   private final float value17;
   private boolean enabled;
   private final float value18;
   private final float value19;
   private final float value20;
   private final ColorSetting colorSetting;
   private final float value21;
   private final float value22;
   private final int slot2 = ColorUtils.rgba(0, 0, 0, 60);
   private final ColorFormatSelector colorFormatSelector;
   private final float value23;
   private float value24;

   public ColorPicker(GuiBounds bounds2, ColorSetting colorSetting) {
      super(bounds2);
      this.value16 = 7.0F;
      this.value23 = 11.0F;
      this.value9 = 5.0F;
      this.value12 = 15.0F;
      this.value21 = 12.0F;
      this.value20 = 5.0F;
      this.value18 = 6.0F;
      this.value = 7.5F;
      this.value5 = 6.0F;
      this.value8 = 15.0F;
      this.value3 = 64.0F;
      this.value22 = 3.0F;
      this.value11 = 5.0F;
      this.value4 = 4.0F;
      this.value6 = 5.0F;
      this.value7 = 1.0F;
      this.value13 = 7.0F;
      this.value2 = 30.0F;
      this.value19 = 20.0F;
      this.value10 = 30.0F;
      this.value14 = 30.0F;
      this.value17 = 1.0F;
      this.colorSetting = colorSetting;
      this.colorChannel = colorSetting.getEditingChannel();
      this.colorModeSelector = new ColorModeSelector(new GuiBounds(0.0F, 0.0F, 0.0F, 11.0F), colorSetting);
      this.colorFormatSelector = new ColorFormatSelector(new GuiBounds(0.0F, 0.0F, 0.0F, 12.0F), colorSetting);
      this.hueSaturationPicker = new HueSaturationPicker(new GuiBounds(0.0F, 0.0F, 0.0F, 64.0F), colorSetting, this.getSliderMarkerRenderer(), 7.0F);
      this.hueSlider = new HueSlider(new GuiBounds(0.0F, 0.0F, 0.0F, 3.0F), colorSetting, this.getSliderMarkerRenderer2());
      this.alphaSlider = new AlphaSlider(new GuiBounds(0.0F, 0.0F, 0.0F, 3.0F), colorSetting, this.getSliderMarkerRenderer2());
      this.doubleColor = new DoubleColor(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F));
      this.colorPreviewButton = new ColorPreviewButton(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F), colorSetting);
      this.colorValueSlider = new ColorValueSlider(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F), colorSetting);
      this.doubleColor.setConsumer(colorSetting::setColorMode);
      this.doubleColor.setColorMode(colorSetting.getColorMode());
      this.addChild(this.hueSaturationPicker);
      this.addChild(this.hueSlider);
      this.addChild(this.alphaSlider);
      this.addChild(this.colorFormatSelector);
      this.addChild(this.colorPreviewButton);
      this.addChild(this.doubleColor);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
      this.update5();
      this.updateAnimations();
      this.updateSectionAnimations();
      this.colorModeSelector.update();

      for(GuiElement element2 : this.children) {
         element2.update();
      }

      this.update3();
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (!this.getBounds().contains((float)n, (float)n2)) {
         return false;
      } else {
         int n4 = this.colorSetting.getColor();
         int n5 = (int)((float)n - this.getBounds().getX());
         int n6 = (int)((float)n2 - this.getBounds().getY());
         boolean bl = this.isActive3() && this.colorModeSelector.onMousePressed(n5, n6, n3);
         if (!bl && this.isEyedropperActive()) {
            bl = this.colorValueSlider.onMousePressed(n5, n6, n3);
         }

         if (!bl) {
            bl = super.onMousePressed(n5, n6, n3) || this.getBounds().contains((float)n, (float)n2);
         }

         if (!this.enabled && this.isActive4()) {
            this.enabled = true;
            this.slot = n4;
         }

         return bl;
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      this.update5();
      this.updateAnimations();
      this.updateSectionAnimations();
      this.layoutChildren();
      Matrix4f matrix4f2 = new Matrix4f(matrix4f).translate(this.getBounds().getX(), this.getBounds().getY(), 0.0F);
      this.process4(f, matrix4f2);
      this.colorValueSlider.setFloatType(this.getLastMouseX() - this.getBounds().getX());
      this.process5(f, matrix4f2);

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

   public ColorSetting getColorSetting() {
      return this.colorSetting;
   }

   public boolean isActive() {
      return this.enabled;
   }

   public ColorChannel getColorChannel() {
      return this.colorChannel;
   }

   public float getFloatType() {
      return 5.0F;
   }

   private SliderMarkerRenderer getSliderMarkerRenderer() {
      return new SliderMarkerRenderer(5.0F, 1.0F, 7.0F, 20.0F, 30.0F, this.slot2);
   }

   public float getHorizontalPadding() {
      return 7.0F;
   }

   public float getFloatType3() {
      return this.value15;
   }

   private boolean isEyedropperActive() {
      return this.value15 > 0.01F;
   }

   public float getFloatType4() {
      return 5.0F;
   }

   public int getIntType() {
      return this.slot2;
   }

   public float getFloatType5() {
      return 1.0F;
   }

   public float getFloatType6() {
      return this.getFloatType16();
   }

   public float getFloatType7() {
      return 1.0F;
   }

   private void updateAnimations() {
      this.value24 = FrameInterpolator.lerpTowards(this.value24, this.colorSetting.isDoubleColorMode() ? 1.0F : 0.0F, 15.0F);
   }

   public float getFloatType8() {
      return this.getFloatType20();
   }

   public ColorValueSlider getColorValueSlider() {
      return this.colorValueSlider;
   }

   private void process4(float f, Matrix4f matrix4f2) {
      if (this.isActive3()) {
         ClippedLayerRenderer.process(
            WexSideClient.getGuiRenderer(),
            matrix4f2,
            this.colorModeSelector.getBounds().getX(),
            this.colorModeSelector.getBounds().getY(),
            this.colorModeSelector.getBounds().getWidth(),
            this.colorModeSelector.getBounds().getHeight(),
            0.0F,
            this.value24 < 0.99F,
            ColorUtils.withAlpha(-1, 255.0F * this.value24),
            matrix4f -> this.colorModeSelector.render(f, matrix4f)
         );
      }
   }

   private void update3() {
      if (!this.isActive4()) {
         if (this.enabled) {
            this.enabled = false;
            if (this.colorSetting.getColor() != this.slot) {
               this.colorSetting.addCurrentColorToRecents();
            }
         }
      }
   }

   public float getFloatType9() {
      return 12.0F;
   }

   public float getFloatType10() {
      return 20.0F;
   }

   private void updateSectionAnimations() {
      this.value15 = FrameInterpolator.lerpTowards(
         this.value15, !this.colorSetting.isAstolfoMode() && !this.colorSetting.isDoubleColorMode() ? 0.0F : 1.0F, 15.0F
      );
   }

   public float getFloatType11() {
      return 6.0F;
   }

   public float getFloatType12() {
      return this.value24;
   }

   public AlphaSlider getAlphaSlider() {
      return this.alphaSlider;
   }

   public ColorModeSelector getColorModeSelector() {
      return this.colorModeSelector;
   }

   public HueSaturationPicker getHueSaturationPicker() {
      return this.hueSaturationPicker;
   }

   public ColorPreviewButton getColorPreviewButton() {
      return this.colorPreviewButton;
   }

   private SliderMarkerRenderer getSliderMarkerRenderer2() {
      return new SliderMarkerRenderer(5.0F, 1.0F, 7.0F, 30.0F, 30.0F, this.slot2);
   }

   public DoubleColor getDoubleColor() {
      return this.doubleColor;
   }

   public float getFloatType13() {
      return 4.0F;
   }

   public float getFloatType14() {
      return 7.5F;
   }

   public float getFloatType15() {
      return 5.0F;
   }

   private float getFloatType16() {
      return (this.colorValueSlider.getFloatType2() + 6.0F) * this.value15;
   }

   private void process5(float f, Matrix4f matrix4f2) {
      if (this.isEyedropperActive()) {
         ClippedLayerRenderer.process(
            WexSideClient.getGuiRenderer(),
            matrix4f2,
            this.colorValueSlider.getBounds().getX(),
            this.colorValueSlider.getBounds().getY(),
            this.colorValueSlider.getBounds().getWidth(),
            this.colorValueSlider.getBounds().getHeight(),
            4.0F,
            this.value15 < 0.99F,
            ColorUtils.withAlpha(-1, 255.0F * this.value15),
            matrix4f -> this.colorValueSlider.render(f, matrix4f)
         );
      }
   }

   public float getFloatType17() {
      return 7.0F;
   }

   public float getFloatType18() {
      return 15.0F;
   }

   public float getFloatType19() {
      return 64.0F;
   }

   private float getFloatType20() {
      return 16.0F * this.value24;
   }

   public float getFloatType21() {
      return 30.0F;
   }

   public float getFloatType22() {
      return 6.0F;
   }

   public float getFloatType23() {
      return 11.0F;
   }

   public float getFloatType24() {
      return 3.0F;
   }

   public float getFloatType25() {
      return 30.0F;
   }

   public float getFloatType26() {
      return 15.0F;
   }

   public ColorFormatSelector getColorFormatSelector() {
      return this.colorFormatSelector;
   }

   public HueSlider getHueSlider() {
      return this.hueSlider;
   }

   private boolean isActive3() {
      return this.value24 > 0.01F;
   }

   public float getFloatType27() {
      return 30.0F;
   }

   private void update5() {
      this.doubleColor.setColorMode(this.colorSetting.getColorMode());
      ColorChannel colorChannel = this.colorSetting.getEditingChannel();
      if (this.colorChannel != colorChannel) {
         this.colorChannel = colorChannel;
         this.colorFormatSelector.update2();
      }
   }

   public float getFloatType28() {
      return 5.0F;
   }

   public int getIntType2() {
      return this.slot;
   }

   private boolean isActive4() {
      return this.hueSaturationPicker.isActive2() || this.hueSlider.isActive2() || this.alphaSlider.isActive2();
   }

   public float getFloatType2() {
      this.update5();
      this.updateAnimations();
      this.updateSectionAnimations();
      this.colorPreviewButton.getBounds().setSize(this.getBounds().getWidth(), this.colorPreviewButton.getBounds().getHeight());
      return this.getFloatType20() + 64.0F + 5.0F + 3.0F + 4.0F + 3.0F + 5.0F + 12.0F + this.getFloatType16() + 7.5F + this.colorPreviewButton.getFloatType2();
   }

   private void layoutChildren() {
      float f = this.getBounds().getWidth();
      float f2 = this.getFloatType20();
      this.colorModeSelector.getBounds().setPosition(0.0F, 0.0F);
      this.colorModeSelector.getBounds().setSize(f, 11.0F);
      this.hueSaturationPicker.getBounds().setPosition(0.0F, f2);
      this.hueSaturationPicker.getBounds().setSize(f, 64.0F);
      float f3;
      this.hueSlider.getBounds().setPosition(0.0F, f3 = f2 + 69.0F);
      this.hueSlider.getBounds().setSize(f, 3.0F);
      float var6;
      this.alphaSlider.getBounds().setPosition(0.0F, var6 = f3 + 7.0F);
      this.alphaSlider.getBounds().setSize(f, 3.0F);
      this.colorFormatSelector.getBounds().setPosition(0.0F, f3 = var6 + 8.0F);
      this.colorFormatSelector.getBounds().setSize(f, 12.0F);
      float f4 = this.doubleColor.getFloatType();
      this.doubleColor.getBounds().setPosition(f - f4, f3 + 12.0F + 6.0F);
      this.doubleColor.getBounds().setSize(f4, this.doubleColor.getFloatType2());
      float var8;
      this.colorValueSlider.getBounds().setPosition(0.0F, (var8 = f3 + 12.0F) + 6.0F);
      this.colorValueSlider.getBounds().setSize(f, this.colorValueSlider.getFloatType2());
      this.colorPreviewButton.getBounds().setPosition(0.0F, f3 = var8 + this.getFloatType16() + 7.5F);
      this.colorPreviewButton.getBounds().setSize(f, this.colorPreviewButton.getBounds().getHeight());
      float f5 = this.colorPreviewButton.getFloatType2();
      this.getBounds().setSize(this.getBounds().getWidth(), f3 + f5);
   }
}
