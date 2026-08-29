package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.input.InputBindings;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.BoundsSupplier;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.GuiInteractionState;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.PopupManager;
import ru.wexside.misc.PopupOwner;
import ru.wexside.misc.ScaleSettings;
import ru.wexside.misc.SingleNumberEditorPopup;
import ru.wexside.misc.ThemeColors;
import ru.wexside.setting.NumberSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.ui.PopupPanel;
import ru.wexside.ui.SliderTrack;
import ru.wexside.ui.setting.SettingComponent;

public final class NumberSettingComponent
   extends SettingComponent
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider,
   PopupOwner {
   private final SingleNumberEditorPopup editorPopup;
   private float value;
   private final float value2;
   private final SliderRenderer sliderRenderer = new SliderRenderer();
   private PopupManager popupManager;
   private float value3;
   private float value4;
   private boolean enabled2;

   public NumberSettingComponent(NumberSetting numberSetting) {
      super(new GuiBounds(0.0F, 0.0F, 110.0F, 0.0F), numberSetting);
      this.value2 = 6.5F;
      this.editorPopup = new SingleNumberEditorPopup(numberSetting);
      this.getBounds().setSize(this.getBounds().getWidth(), this.getFloatType2());
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (!this.getBounds().contains((float)n, (float)n2)) {
         return false;
      } else if (n3 == 1) {
         if (this.popupManager != null) {
            this.value3 = (float)GuiInteractionState.getInstance().getScaledMouseX() - GuiInteractionState.getInstance().getRootPanel().getBounds().getX();
            this.popupManager.toggle(this);
         }

         return true;
      } else if (n3 != 0) {
         return true;
      } else {
         String string = NumberFormatting.format(((NumberSetting)this.getSetting()).getValue(), ((NumberSetting)this.getSetting()).getPrecision());
         float f = FontRegistry.font2.process4(string, 6.5F);
         SliderTrack track = this.sliderRenderer.process7(this.getBounds(), f);
         if (!((float)n2 < track.y() - 3.0F) && !((float)n2 > track.y() + this.sliderRenderer.getFloatType3() + 3.0F)) {
            this.enabled2 = true;
            this.value = (float)GuiInteractionState.getInstance().getScaledMouseX() - ((float)n - this.getBounds().getX());
            this.setFloatType((float)n);
            return true;
         } else {
            return true;
         }
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      NumberSetting numberSetting = (NumberSetting)this.getSetting();
      if (this.enabled2) {
         if (!InputBindings.isMouseButtonPressed(0)) {
            this.enabled2 = false;
         } else {
            float f2 = this.getBounds().getX() + (float)GuiInteractionState.getInstance().getScaledMouseX() - this.value;
            this.setFloatType(f2);
         }
      }

      float f2 = this.getNormalizedValue();
      this.value4 = FrameInterpolator.lerpTowards(this.value4, f2, this.enabled2 ? 35.0F : 20.0F);
      ScaleSettings scaleSettings = this.process4(numberSetting);
      String string = NumberFormatting.format(numberSetting.getValue(), numberSetting.getPrecision());
      float f3 = FontRegistry.font2.process4(string, 6.5F);
      float f4 = bounds2.getY() + this.sliderRenderer.getFloatType2();
      SliderTrack track = this.sliderRenderer.process7(bounds2, f3);
      String string2 = NumberFormatting.unit(numberSetting);
      float f5 = bounds2.getX() + bounds2.getWidth() - this.sliderRenderer.getFloatType() - FontRegistry.font2.process3(string2, 6.5F);
      float f6 = track.x();
      FontRegistry.font2.process2(matrix4f, drawApi, string2, f5, f4 - 3.0F, 6.5F, ThemeColors.accent());
      FontRegistry.font2.process2(matrix4f, drawApi, string, f6, f4 - 3.0F, 6.5F, ThemeColors.textMuted());
      int n = ThemeColors.borderSubtle();
      int n2 = ThemeColors.textDisabled();
      int n3 = ThemeColors.accent();
      this.sliderRenderer.process2(matrix4f, drawApi, track, n);
      this.sliderRenderer.renderTickMarks(matrix4f, drawApi, track, scaleSettings, n, n2);
      float f7 = track.width() * this.sliderRenderer.process4(this.value4, scaleSettings);
      float f8 = track.x() + f7;
      float f9 = track.y() + this.sliderRenderer.getFloatType3() / 2.0F;
      drawApi.drawRoundedRectangleGradient(matrix4f, track.x(), track.y(), f7, this.sliderRenderer.getFloatType3(), 2.0F, n3, n3, n3, n3);
      this.sliderRenderer.process(matrix4f, drawApi, f8, f9, n3);
      return bounds2.getY() + bounds2.getHeight();
   }

   @Override
   public float getFloatType() {
      return this.getBounds().getWidth();
   }

   public SingleNumberEditorPopup getEditorPopup() {
      return this.editorPopup;
   }

   @Override
   public PopupPanel getPopup() {
      return this.editorPopup;
   }

   private ScaleSettings process4(NumberSetting numberSetting) {
      return new ScaleSettings(
         numberSetting.getMinimum(),
         numberSetting.getMaximum(),
         numberSetting.getPrecision(),
         numberSetting.hasMarkers(),
         numberSetting.getMarkerStep(),
         numberSetting.getSnapStep()
      );
   }

   private float getNormalizedValue() {
      NumberSetting numberSetting = (NumberSetting)this.getSetting();
      double d = numberSetting.getMaximum() - numberSetting.getMinimum();
      return d <= 0.0 ? 0.0F : (float)((numberSetting.getUnscaledValue() - numberSetting.getMinimum()) / d);
   }

   private GuiBounds getContainerBounds() {
      for(GuiElement element2 = this.getParent(); element2 != null; element2 = element2.getParent()) {
         if (element2 instanceof BoundsSupplier callback13) {
            return callback13.getBounds();
         }
      }

      return null;
   }

   private void setFloatType(float f) {
      GuiBounds bounds2 = this.getContainerBounds();
      NumberSetting numberSetting = (NumberSetting)this.getSetting();
      ScaleSettings scaleSettings = this.process4(numberSetting);
      float f2 = bounds2.getX() + this.sliderRenderer.getFloatType();
      float f3 = Math.max(1.0F, bounds2.getWidth() - this.sliderRenderer.getFloatType() * 2.0F);
      float f4 = Math.max(0.0F, Math.min(f3, f - f2));
      float f5 = f4 / f3;
      double d = (double)this.sliderRenderer.process3(f5, scaleSettings);
      double d2 = numberSetting.getMinimum() + (numberSetting.getMaximum() - numberSetting.getMinimum()) * d;
      if (numberSetting.hasSnapStep()) {
         d2 = NumberFormatting.snap(d2, numberSetting.getMinimum(), numberSetting.getMaximum(), numberSetting.getSnapStep());
      }

      double d3 = NumberFormatting.round(d2, numberSetting.getPrecision());
      double d4 = (d3 - numberSetting.getMinimum()) / (numberSetting.getMaximum() - numberSetting.getMinimum());
      numberSetting.setNormalizedValue(Math.max(0.0, Math.min(1.0, d4)));
   }

   @Override
   public boolean process6(int n, int n2) {
      GuiBounds bounds2 = this.getBounds();
      return bounds2 != null
         ? new GuiBounds(
               bounds2.getX() + this.getBounds().getX(), bounds2.getY() + this.getBounds().getY(), this.getBounds().getWidth(), this.getBounds().getHeight()
            )
            .contains((float)n, (float)n2)
         : new GuiBounds(this.getAbsoluteX(), this.getAbsoluteY(), this.getBounds().getWidth(), this.getBounds().getHeight()).contains((float)n, (float)n2);
   }

   @Override
   public void update2() {
      GuiBounds bounds2 = this.getContainerBounds();
      float f = this.value3;
      float f2 = bounds2 != null ? bounds2.getY() + this.getBounds().getY() : this.getAbsoluteY() + this.getBounds().getHeight() + 1.0F;
      this.editorPopup.getBounds().setPosition(f, f2);
   }

   @Override
   public void setPopupManager(PopupManager popupManager) {
      this.popupManager = popupManager;
   }

   @Override
   public float getFloatType2() {
      return this.sliderRenderer.process6(((NumberSetting)this.getSetting()).hasMarkers());
   }
}
