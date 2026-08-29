package ru.wexside.util;

import java.util.List;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.KeybindCaptureField;
import ru.wexside.misc.KeybindDescriptor;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.TextLayoutUtils;
import ru.wexside.misc.ThemeColors;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;

public final class KeybindRow
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final KeybindDescriptor callback10;
   private final KeybindCaptureField keybindCaptureField;

   public KeybindRow(KeybindDescriptor callback10) {
      super(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F));
      this.callback10 = callback10;
      this.keybindCaptureField = new KeybindCaptureField(callback10);
      this.addChild(this.keybindCaptureField);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      float f3 = bounds2.getWidth();
      float f4 = this.process2(f3);
      bounds2.setSize(f3, f4);
      float f5 = this.process5(f3);
      float f6 = bounds2.getY() + (f4 - f5) / 2.0F;
      FontRegistry.font2.process2(matrix4f, drawApi, this.callback10.getString(), bounds2.getX(), f6, 6.75F, ThemeColors.textPrimary());
      if (this.isActive()) {
         float f2 = FontRegistry.font2.process4(this.callback10.getString(), 6.75F);
         TextLayoutUtils.process(
            matrix4f,
            drawApi,
            this.callback10.getString2(),
            FontRegistry.font2,
            6.25F,
            this.process4(f3),
            bounds2.getX(),
            f6 + f2 + 1.5F,
            1.0F,
            ThemeColors.textMuted()
         );
      }

      float f2 = this.keybindCaptureField.getFloatType();
      float f7 = this.keybindCaptureField.getFloatType2();
      float f8 = bounds2.getX() + bounds2.getWidth() - f2 - 0.0F;
      float f9 = bounds2.getY() + (f4 - f7) / 2.0F;
      this.keybindCaptureField.getBounds().setPosition(f8, f9);
      this.keybindCaptureField.getBounds().setSize(f2, f7);
      this.keybindCaptureField.render(f, matrix4f);
      return bounds2.getY() + f4;
   }

   private boolean isActive() {
      return this.callback10.getString2() != null && !this.callback10.getString2().isBlank();
   }

   public float process2(float f) {
      return Math.max(this.process5(f), this.keybindCaptureField.getFloatType2());
   }

   public float getDescriptionFontSize() {
      return 6.25F;
   }

   public KeybindDescriptor getCallback10() {
      return this.callback10;
   }

   public float getRightMargin() {
      return 0.0F;
   }

   public float getTitleDescriptionGap() {
      return 1.5F;
   }

   private float process4(float f) {
      return Math.max(0.0F, f - this.keybindCaptureField.getFloatType() - 4.0F);
   }

   public float getEntrySpacing() {
      return 4.0F;
   }

   private float process5(float f) {
      float f2 = FontRegistry.font2.process4(this.callback10.getString(), 6.75F);
      if (!this.isActive()) {
         return f2;
      } else {
         List<String> list = TextLayoutUtils.process2(this.callback10.getString2(), FontRegistry.font2, 6.25F, this.process4(f));
         float f3 = FontRegistry.font2.process4("A", 6.25F);
         int n = Math.max(1, list.size());
         float f4 = (float)n * f3 + (float)Math.max(0, n - 1) * 1.0F;
         return f2 + 1.5F + f4;
      }
   }

   public KeybindCaptureField getKeybindCaptureField() {
      return this.keybindCaptureField;
   }

   public float getLineSpacing() {
      return 1.0F;
   }

   public float getTitleFontSize() {
      return 6.75F;
   }
}
