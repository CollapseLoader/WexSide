package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.setting.SettingComponent;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class BooleanSettingComponent
   extends SettingComponent
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   static final float value = 10.0F;
   private float value2;
   static final float value3 = 15.0F;

   public BooleanSettingComponent(BooleanSetting booleanSetting) {
      super(new GuiBounds(0.0F, 0.0F, 15.0F, 10.0F), booleanSetting);
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
         ((BooleanSetting)this.getSetting()).setEnabled(!((BooleanSetting)this.getSetting()).isEnabled());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      this.value2 = FrameInterpolator.lerpTowards(this.value2, ((BooleanSetting)this.getSetting()).isEnabled() ? 1.0F : 0.0F, 15.0F);
      int n = ColorUtils.lerp(ThemeColors.borderPrimary(), ThemeColors.accent(), (double)this.value2);
      int n2 = ColorUtils.lerp(ThemeColors.textMuted(), ThemeColors.backgroundControl(), (double)this.value2);
      float f2 = 7.0F;
      float f3 = 1.5F;
      float f4 = bounds2.getWidth() - f2 - f3 * 2.0F;
      float f5 = bounds2.getX() + f3 + f4 * this.value2;
      float f6 = bounds2.getY() + (bounds2.getHeight() - f2) / 2.0F;
      drawApi.drawRoundedRectangle(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 16.0F, n);
      drawApi.drawRoundedRectangle(matrix4f, f5, f6, f2, f2, 16.0F, n2);
      return bounds2.getY() + bounds2.getHeight();
   }

   @Override
   public float getFloatType() {
      return 15.0F;
   }

   @Override
   public float getFloatType2() {
      return 10.0F;
   }
}
