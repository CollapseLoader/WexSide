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
import ru.wexside.misc.RangeEditorPopup;
import ru.wexside.misc.ScaleSettings;
import ru.wexside.misc.ThemeColors;
import ru.wexside.setting.RangeSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.ui.PopupPanel;
import ru.wexside.ui.SliderTrack;
import ru.wexside.ui.setting.SettingComponent;

public final class RangeSettingComponent
   extends SettingComponent
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider,
   PopupOwner {
   private final int slot;
   private int slot2 = -1;
   private PopupManager popupManager;
   private final RangeEditorPopup editorPopup;
   private float dragOffset;
   private float upperProgress;
   private final int slot3;
   private final SliderRenderer sliderRenderer = new SliderRenderer();
   private float lowerProgress;

   public RangeSettingComponent(RangeSetting rangeSetting) {
      super(new GuiBounds(0.0F, 0.0F, 110.0F, 0.0F), rangeSetting);
      this.slot = 0;
      this.slot3 = 1;
      this.editorPopup = new RangeEditorPopup(rangeSetting);
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
            this.popupManager.toggle(this);
         }

         return true;
      } else if (n3 != 0) {
         return true;
      } else {
         String string = NumberFormatting.format(((RangeSetting)this.getSetting()).getMinimum(), ((RangeSetting)this.getSetting()).getPrecision());
         float f = FontRegistry.font2.process4(string, 6.5F);
         SliderTrack track = this.sliderRenderer.process7(this.getBounds(), f);
         if (!((float)n2 < track.y() - 3.0F) && !((float)n2 > track.y() + this.sliderRenderer.getFloatType3() + 3.0F)) {
            this.dragOffset = (float)GuiInteractionState.getInstance().getScaledMouseX() - ((float)n - this.getBounds().getX());
            this.slot2 = this.process4(((float)n - track.x()) / track.width());
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
      RangeSetting rangeSetting = (RangeSetting)this.getSetting();
      if (this.slot2 != -1) {
         if (!InputBindings.isMouseButtonPressed(0)) {
            this.slot2 = -1;
         } else {
            float f2 = this.getBounds().getX() + (float)GuiInteractionState.getInstance().getScaledMouseX() - this.dragOffset;
            this.setFloatType(f2);
         }
      }

      float f2 = (float)rangeSetting.getLowerNormalizedValue();
      float f3 = (float)rangeSetting.getUpperNormalizedValue();
      this.lowerProgress = FrameInterpolator.lerpTowards(this.lowerProgress, f2, this.slot2 == 0 ? 35.0F : 20.0F);
      this.upperProgress = FrameInterpolator.lerpTowards(this.upperProgress, f3, this.slot2 == 1 ? 35.0F : 20.0F);
      ScaleSettings scaleSettings = this.process5(rangeSetting);
      String string = NumberFormatting.format(rangeSetting.getMinimum(), rangeSetting.getPrecision());
      float f4 = FontRegistry.font2.process4(string, 6.5F);
      float f5 = bounds2.getY() + this.sliderRenderer.getFloatType2();
      SliderTrack track = this.sliderRenderer.process7(bounds2, f4);
      String string2 = this.process6(rangeSetting);
      float f6 = bounds2.getX() + bounds2.getWidth() - this.sliderRenderer.getFloatType() - FontRegistry.font2.process3(string2, 6.5F);
      FontRegistry.font2.process2(matrix4f, drawApi, string2, f6, f5 - 3.0F, 6.5F, ThemeColors.accent());
      FontRegistry.font2.process2(matrix4f, drawApi, string, track.x(), f5 - 3.0F, 6.5F, ThemeColors.textMuted());
      int n = ThemeColors.borderSubtle();
      int n2 = ThemeColors.textDisabled();
      int n3 = ThemeColors.accent();
      this.sliderRenderer.process2(matrix4f, drawApi, track, n);
      this.sliderRenderer.renderTickMarks(matrix4f, drawApi, track, scaleSettings, n, n2);
      float f7 = this.sliderRenderer.process4(this.lowerProgress, scaleSettings);
      float f8 = this.sliderRenderer.process4(this.upperProgress, scaleSettings);
      float f9 = track.x() + track.width() * f7;
      float f10 = track.x() + track.width() * f8;
      float f11 = Math.max(0.0F, f10 - f9);
      float f12 = track.y() + this.sliderRenderer.getFloatType3() / 2.0F;
      drawApi.drawRoundedRectangleGradient(matrix4f, f9, track.y(), f11, this.sliderRenderer.getFloatType3(), 2.0F, n3, n3, n3, n3);
      this.sliderRenderer.process(matrix4f, drawApi, f9, f12, n3);
      this.sliderRenderer.process(matrix4f, drawApi, f10, f12, n3);
      return bounds2.getY() + bounds2.getHeight();
   }

   @Override
   public float getFloatType() {
      return this.getBounds().getWidth();
   }

   private GuiBounds getContainerBounds() {
      for(GuiElement element2 = this.getParent(); element2 != null; element2 = element2.getParent()) {
         if (element2 instanceof BoundsSupplier callback13) {
            return callback13.getBounds();
         }
      }

      return null;
   }

   private int process4(float f) {
      RangeSetting rangeSetting = (RangeSetting)this.getSetting();
      ScaleSettings scaleSettings = this.process5(rangeSetting);
      float f2 = Math.max(0.0F, Math.min(1.0F, f));
      float f3 = this.sliderRenderer.process4((float)rangeSetting.getLowerNormalizedValue(), scaleSettings);
      float f4 = this.sliderRenderer.process4((float)rangeSetting.getUpperNormalizedValue(), scaleSettings);
      float f5 = Math.abs(f2 - f3);
      float f6 = Math.abs(f2 - f4);
      return f6 <= f5 ? 1 : 0;
   }

   private ScaleSettings process5(RangeSetting rangeSetting) {
      return new ScaleSettings(
         rangeSetting.getMinimum(),
         rangeSetting.getMaximum(),
         rangeSetting.getPrecision(),
         rangeSetting.hasMarkers(),
         rangeSetting.getMarkerStep(),
         rangeSetting.getSnapStep()
      );
   }

   private void setFloatType(float f) {
      if (this.slot2 != -1) {
         GuiBounds bounds2 = this.getBounds();
         RangeSetting rangeSetting = (RangeSetting)this.getSetting();
         ScaleSettings scaleSettings = this.process5(rangeSetting);
         float f2 = bounds2.getX() + this.sliderRenderer.getFloatType();
         float f3 = Math.max(1.0F, bounds2.getWidth() - this.sliderRenderer.getFloatType() * 2.0F);
         float f4 = Math.max(0.0F, Math.min(f3, f - f2));
         float f5 = f4 / f3;
         double d = (double)this.sliderRenderer.process3(f5, scaleSettings);
         double d2 = rangeSetting.getMinimum() + (rangeSetting.getMaximum() - rangeSetting.getMinimum()) * d;
         if (rangeSetting.hasSnapStep()) {
            d2 = NumberFormatting.snap(d2, rangeSetting.getMinimum(), rangeSetting.getMaximum(), rangeSetting.getSnapStep());
         }

         double d3 = NumberFormatting.round(d2, rangeSetting.getPrecision());
         double d4 = rangeSetting.getMaximum() - rangeSetting.getMinimum();
         if (!(d4 <= 0.0)) {
            double d5 = (d3 - rangeSetting.getMinimum()) / d4;
            d5 = Math.max(0.0, Math.min(1.0, d5));
            if (this.slot2 == 1) {
               rangeSetting.setUpperNormalizedValue(Math.max(rangeSetting.getLowerNormalizedValue(), d5));
            } else {
               rangeSetting.setLowerNormalizedValue(Math.min(rangeSetting.getUpperNormalizedValue(), d5));
            }
         }
      }
   }

   private String process6(RangeSetting rangeSetting) {
      String string2 = NumberFormatting.format(rangeSetting.getUpperUnscaledValue(), rangeSetting.getPrecision());
      String string3 = NumberFormatting.format(rangeSetting.getLowerUnscaledValue(), rangeSetting.getPrecision());
      String string4 = string3 + " - " + string2;
      if (!rangeSetting.hasFormatter()) {
         return string4;
      } else {
         String string5 = rangeSetting.format(rangeSetting.getUpperUnscaledValue());
         if (string5.isBlank()) {
            return string4;
         } else {
            String string;
            if ("%".equals(string5)) {
               string = string4 + string5;
            } else {
               string = string4 + " " + string5;
            }

            return string;
         }
      }
   }

   public RangeEditorPopup getEditorPopup() {
      return this.editorPopup;
   }

   @Override
   public PopupPanel getPopup() {
      return this.editorPopup;
   }

   @Override
   public boolean process6(int n, int n2) {
      GuiBounds bounds2 = this.getContainerBounds();
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
      if (bounds2 != null) {
         float f = bounds2.getX() + this.getBounds().getX() + this.getBounds().getWidth() / 2.0F;
         float f2 = bounds2.getY() + this.getBounds().getY();
         this.editorPopup.getBounds().setPosition(f, f2);
      } else {
         float f = this.getAbsoluteX();
         float f3 = this.getAbsoluteY() + this.getBounds().getHeight() + 1.0F;
         this.editorPopup.getBounds().setPosition(f, f3);
      }
   }

   @Override
   public void setPopupManager(PopupManager popupManager) {
      this.popupManager = popupManager;
   }

   @Override
   public float getFloatType2() {
      return this.sliderRenderer.process6(((RangeSetting)this.getSetting()).hasMarkers());
   }
}
