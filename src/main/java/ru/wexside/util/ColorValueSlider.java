package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.input.InputBindings;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.ScaleSettings;
import ru.wexside.misc.ThemeColors;
import ru.wexside.setting.ColorSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.ui.SliderTrack;

public final class ColorValueSlider
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private float animatedProgress = -1.0F;
   private final ScaleSettings scaleSettings;
   private final ColorSetting colorSetting;
   private final SliderRenderer sliderRenderer = new SliderRenderer();
   private boolean enabled2;

   public ColorValueSlider(GuiBounds bounds2, ColorSetting colorSetting) {
      super(bounds2);
      this.scaleSettings = new ScaleSettings(1.0, 5.0, 0, true, 1.0, 1.0);
      this.colorSetting = colorSetting;
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (n3 == 0 && this.getBounds().contains((float)n, (float)n2)) {
         SliderTrack track = this.process4(this.getBounds().getX(), this.getBounds().getY());
         if (!((float)n2 < track.y() - 3.0F) && !((float)n2 > track.y() + this.sliderRenderer.getFloatType3() + 3.0F)) {
            this.enabled2 = true;
            this.setFloatType2((float)n);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      float f2 = this.getNormalizedValue();
      this.animatedProgress = this.animatedProgress < 0.0F ? f2 : FrameInterpolator.lerpTowards(this.animatedProgress, f2, this.enabled2 ? 35.0F : 20.0F);
      FontRegistry.font4.process2(matrix4f, drawApi, "Скорость переливания цвета", 0.0F, 0.0F, 5.5F, ThemeColors.textMuted());
      SliderTrack track = this.process4(0.0F, 0.0F);
      String string = NumberFormatting.format((double)this.colorSetting.getAstolfoSpeedPercent(), 0);
      String string2 = NumberFormatting.format(5.0, 0);
      String string3 = NumberFormatting.format(1.0, 0);
      String string4 = string3 + " - " + string2;
      float f3 = track.y() - FontRegistry.font2.process4("1", 6.5F) - 2.0F;
      FontRegistry.font2.process2(matrix4f, drawApi, string, track.x(), f3, 6.5F, ThemeColors.accent());
      FontRegistry.font2
         .process2(matrix4f, drawApi, string4, track.x() + track.width() - FontRegistry.font2.process3(string4, 6.5F), f3, 6.5F, ThemeColors.textMuted());
      int n = ThemeColors.borderSubtle();
      this.sliderRenderer.process2(matrix4f, drawApi, track, n);
      this.sliderRenderer.renderTickMarks(matrix4f, drawApi, track, this.scaleSettings, n, ThemeColors.textDisabled());
      float f4 = track.width() * this.sliderRenderer.process4(this.animatedProgress, this.scaleSettings);
      drawApi.drawRoundedRectangle(matrix4f, track.x(), track.y(), f4, this.sliderRenderer.getFloatType3(), 2.0F, ThemeColors.accent());
      this.sliderRenderer.process(matrix4f, drawApi, track.x() + f4, track.y() + this.sliderRenderer.getFloatType3() / 2.0F, ThemeColors.accent());
      return bounds2.getY() + bounds2.getHeight();
   }

   public void setFloatType(float f) {
      if (this.enabled2) {
         if (!InputBindings.isMouseButtonPressed(0)) {
            this.enabled2 = false;
         } else {
            this.setFloatType2(f);
         }
      }
   }

   private float getFloatType() {
      return FontRegistry.font4.process4("Скорость переливания цвета", 5.5F);
   }

   private void setFloatType2(float f) {
      SliderTrack track = this.process4(this.getBounds().getX(), this.getBounds().getY());
      float f2 = Math.max(0.0F, Math.min(track.width(), f - track.x()));
      float f3 = this.sliderRenderer.process3(f2 / track.width(), this.scaleSettings);
      this.colorSetting.setAstolfoSpeedPercent((int)Math.round(1.0 + 4.0 * (double)f3));
   }

   private float getNormalizedValue() {
      return (float)(((double)this.colorSetting.getAstolfoSpeedPercent() - 1.0) / 4.0);
   }

   private SliderTrack process4(float f, float f2) {
      float f3 = f2 + this.getFloatType() + 2.5F;
      float f4 = this.sliderRenderer.getFloatType();
      GuiBounds bounds2 = new GuiBounds(f - f4, f3, this.getBounds().getWidth() + f4 * 2.0F, 0.0F);
      return this.sliderRenderer.process7(bounds2, FontRegistry.font2.process4("1", 6.5F));
   }

   public float getFloatType2() {
      return this.process4(0.0F, 0.0F).y() + this.sliderRenderer.getFloatType3() + 4.0F;
   }
}
