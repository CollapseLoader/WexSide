package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.input.InputBindings;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.GuiInteractionState;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.setting.ColorSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;

abstract class AbstractColorSlider
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final SliderMarkerRenderer sliderMarkerRenderer;
   private boolean enabled2;
   private boolean enabled;
   private float value;
   private float value2;
   private final ColorSetting colorSetting;

   protected AbstractColorSlider(GuiBounds bounds2, ColorSetting colorSetting, SliderMarkerRenderer sliderMarkerRenderer) {
      super(bounds2);
      this.colorSetting = colorSetting;
      this.sliderMarkerRenderer = sliderMarkerRenderer;
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
      if (this.enabled2) {
         if (!InputBindings.isMouseButtonPressed(0)) {
            this.enabled2 = false;
            this.sliderMarkerRenderer.setHovered(false);
         } else {
            this.process(
               (float)GuiInteractionState.getInstance().getScaledMouseX() - this.getAbsoluteX(),
               (float)GuiInteractionState.getInstance().getScaledMouseY() - this.getAbsoluteY()
            );
         }
      } else {
         this.sliderMarkerRenderer.setHovered(false);
      }
   }

   @Override
   public final boolean onMousePressed(int n, int n2, int n3) {
      if (n3 == 0 && this.getBounds().contains((float)n, (float)n2)) {
         this.enabled2 = true;
         this.sliderMarkerRenderer.setHovered(true);
         this.process((float)n - this.getBounds().getX(), (float)n2 - this.getBounds().getY());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final float render(float f, Matrix4f matrix4f) {
      this.updateLayout();
      this.setMatrix4f(matrix4f);
      this.sliderMarkerRenderer.render(matrix4f, WexSideClient.getGuiRenderer(), this.getMarkerX(), this.getMarkerY(), this.getDisplayColor());
      return this.getBounds().getY() + this.getBounds().getHeight();
   }

   @Override
   public boolean isActive2() {
      return this.enabled2;
   }

   public ColorSetting getColorSetting() {
      return this.colorSetting;
   }

   protected boolean isActive() {
      return this.colorSetting.isAstolfoMode();
   }

   protected abstract float getMarkerY();

   protected abstract void process(float var1, float var2);

   protected int getBaseColor() {
      return this.colorSetting.getColor();
   }

   protected float process4(float f, float f2) {
      return f2 <= 0.0F ? 0.0F : this.process8(f / f2);
   }

   protected int getIntType2() {
      return this.getBaseColor() >>> 24 & 0xFF;
   }

   public SliderMarkerRenderer getSliderMarkerRenderer() {
      return this.sliderMarkerRenderer;
   }

   protected float process5(float f, float f2) {
      if (f2 <= 0.0F) {
         return 0.0F;
      } else {
         float f3 = f / f2;
         float f4 = 1.0F - this.process7(f2);
         return Math.max(0.0F, Math.min(f4, f3));
      }
   }

   protected void process6(float f, float f2, float f3, int n) {
      this.colorSetting.setEditingHsb(this.process8(f), this.process8(f2), this.process8(f3), n);
   }

   protected abstract void setMatrix4f(Matrix4f var1);

   public boolean isActive3() {
      return this.enabled;
   }

   protected void setFloatType(float f) {
      this.colorSetting.setAstolfoPhaseOffset(f);
   }

   protected float[] getHsbComponents() {
      return this.colorSetting.getCurrentHsb();
   }

   protected int getIntType3() {
      return this.colorSetting.getColor();
   }

   protected float process7(float f) {
      return Math.min(0.5F / f, 0.499F);
   }

   public float getFloatType3() {
      return this.value2;
   }

   protected float process8(float f) {
      return Math.max(0.0F, Math.min(1.0F, f));
   }

   private void updateLayout() {
      GuiBounds bounds2 = this.getBounds();
      float f = bounds2.getX();
      float f2 = bounds2.getY();
      if (!this.enabled) {
         this.value = f;
         this.value2 = f2;
         this.enabled = true;
      } else {
         float f3 = f - this.value;
         float f4 = f2 - this.value2;
         if (f3 != 0.0F || f4 != 0.0F) {
            this.sliderMarkerRenderer.translate(f3, f4);
            this.value = f;
            this.value2 = f2;
         }
      }
   }

   protected float getFloatType4() {
      return this.colorSetting.getAstolfoHue();
   }

   public float getFloatType5() {
      return this.value;
   }

   protected abstract float getMarkerX();

   protected int getDisplayColor() {
      return this.getBaseColor();
   }

   protected int process9(float f) {
      return this.colorSetting.getAstolfoGradientColor(f);
   }

   protected void setFloatType2(float f) {
      this.colorSetting.setAstolfoHue(f);
   }

   protected float getFloatType6() {
      return this.colorSetting.getAstolfoPhaseOffset();
   }
}
