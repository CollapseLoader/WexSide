package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.input.BindInput;
import ru.wexside.setting.BindSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.setting.SettingComponent;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class BindSettingComponent
   extends SettingComponent
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private boolean enabled;
   private float value = 1.0F;
   private float value2 = 7.0F;
   private float value3 = 6.0F;
   private float value4 = 12.0F;
   private final String string2;
   private float value5 = 5.0F;
   private float value6;

   public BindSettingComponent(BindSetting bindSetting) {
      super(new GuiBounds(0.0F, 0.0F, 0.0F, 12.0F), bindSetting);
      this.string2 = "...";
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      boolean bl = this.getBounds().contains((float)n, (float)n2);
      if (!this.enabled) {
         if (n3 == 0 && bl) {
            this.update4();
            return true;
         } else {
            return false;
         }
      } else if (n3 != 0 && n3 != 1) {
         this.setIntType2(n3);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      String string = this.enabled ? "..." : ((BindSetting)this.getSetting()).getKeyDisplayName();
      float f2 = bounds2.getY() + (bounds2.getHeight() - FontRegistry.font4.process4(string, this.value3)) / 2.0F;
      this.value6 = FrameInterpolator.lerpTowards(this.value6, this.enabled ? 1.0F : 0.0F, 30.0F);
      int n = ColorUtils.lerp(ThemeColors.borderPrimary(), ThemeColors.accent(), (double)this.value6);
      int n2 = ColorUtils.lerp(ThemeColors.controlFill(), ThemeColors.accentTint(), (double)this.value6);
      int n3 = ColorUtils.lerp(ThemeColors.textSecondary(), ThemeColors.accent(), (double)this.value6);
      drawApi.drawRoundedRectangleOutlined(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), this.value2, this.value, n2, n);
      FontRegistry.font4.process2(matrix4f, drawApi, string, bounds2.getX() + this.value5, f2, this.value3, n3);
      return bounds2.getY() + bounds2.getHeight();
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      if (this.enabled) {
         if (n3 != 0 && n3 != 1) {
            this.setIntType2(n3);
         }
      }
   }

   @Override
   public void update2() {
      this.update5();
      super.update2();
   }

   @Override
   public boolean onKeyPressed(int n) {
      if (!this.enabled) {
         return false;
      } else if (n == 256) {
         return false;
      } else if (n != 261 && n != 259) {
         this.setIntType(n);
         return true;
      } else {
         this.update3();
         return true;
      }
   }

   @Override
   public float getFloatType() {
      String string = this.enabled ? "..." : ((BindSetting)this.getSetting()).getKeyDisplayName();
      return this.value5 + FontRegistry.font4.process3(string, this.value3) + this.value5;
   }

   private void setIntType(int n) {
      ((BindSetting)this.getSetting()).setBindInput(BindInput.keyboard(n));
      this.update5();
   }

   private void setIntType2(int n) {
      ((BindSetting)this.getSetting()).setBindInput(BindInput.mouse(n));
      this.update5();
   }

   private void update3() {
      ((BindSetting)this.getSetting()).setBindInput(BindInput.unbound());
      this.update5();
   }

   private void update4() {
      this.enabled = true;
   }

   private void update5() {
      this.enabled = false;
   }

   @Override
   public float getFloatType2() {
      return this.value4;
   }
}
