package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.input.BindInput;
import ru.wexside.input.InputBindings;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class KeybindCaptureField
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private float value;
   private boolean enabled;
   private final KeybindDescriptor callback10;
   private final String string2 = "...";

   public KeybindCaptureField(KeybindDescriptor callback10) {
      super(new GuiBounds(0.0F, 0.0F, 0.0F, 12.0F));
      this.callback10 = callback10;
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
            this.enabled = true;
            return true;
         } else {
            return false;
         }
      } else if (n3 != 0 && n3 != 1) {
         if (!bl) {
            return false;
         } else {
            this.callback10.setBindInput(BindInput.mouse(n3));
            this.enabled = false;
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      String string = this.getString();
      float f2 = bounds2.getY() + (bounds2.getHeight() - FontRegistry.font4.process4(string, 6.0F)) / 2.0F;
      this.value = FrameInterpolator.lerpTowards(this.value, this.enabled ? 1.0F : 0.0F, 30.0F);
      int n = ColorUtils.lerp(ThemeColors.borderPrimary(), ThemeColors.accent(), (double)this.value);
      int n2 = ColorUtils.lerp(ThemeColors.controlFill(), ThemeColors.accentTint(), (double)this.value);
      int n3 = ColorUtils.lerp(ThemeColors.textSecondary(), ThemeColors.accent(), (double)this.value);
      drawApi.drawRoundedRectangleOutlined(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 7.0F, 1.0F, n2, n);
      FontRegistry.font4.process2(matrix4f, drawApi, string, bounds2.getX() + 5.0F, f2, 6.0F, n3);
      return bounds2.getY() + bounds2.getHeight();
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      if (this.enabled) {
         if (n3 != 0 && n3 != 1) {
            this.callback10.setBindInput(BindInput.mouse(n3));
            this.enabled = false;
         }
      }
   }

   @Override
   public void update2() {
      this.enabled = false;
      super.update2();
   }

   @Override
   public boolean onKeyPressed(int n) {
      if (!this.enabled) {
         return false;
      } else if (n == 256) {
         return false;
      } else if (n != 261 && n != 259) {
         this.callback10.setBindInput(BindInput.keyboard(n));
         this.enabled = false;
         return true;
      } else {
         this.callback10.setBindInput(BindInput.unbound());
         this.enabled = false;
         return true;
      }
   }

   public float getFloatType() {
      return 5.0F + FontRegistry.font4.process3(this.getString(), 6.0F) + 5.0F;
   }

   private String getString() {
      return this.enabled ? "..." : InputBindings.displayName(this.callback10.getBindInput());
   }

   public float getFloatType2() {
      return 12.0F;
   }
}
