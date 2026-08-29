package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class LabeledTextField
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final float value;
   private final String string3;
   private final GuiBounds inputBounds = new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F);
   private final String string4;
   private final String string5;
   private final MutableTextBuffer mutableTextBuffer;
   private final TextInputController textInputController;
   private final String string6;
   private final float value2;
   private float value3;
   private final float value4;
   private boolean enabled = true;
   private final float value5 = 6.5F;
   private final float value6;
   private final float value7 = 6.0F;
   private final float value8;
   private final float value9;
   private final float value10;
   private final float value11 = 1.5F;
   private final float value12;

   public LabeledTextField(String string, String string2, int n, float f) {
      super(new GuiBounds(0.0F, 0.0F, f, 0.0F));
      this.value2 = 4.0F;
      this.value8 = 4.0F;
      this.value12 = 14.0F;
      this.value4 = 7.0F;
      this.value9 = 5.5F;
      this.value10 = 5.0F;
      this.value = 0.75F;
      this.string6 = "Введите текст...";
      this.string3 = "|";
      this.string4 = string;
      this.string5 = string2;
      this.mutableTextBuffer = new MutableTextBuffer(n);
      this.textInputController = new TextInputController(this.mutableTextBuffer);
      float f2 = string2 != null && !string2.isBlank() ? 11.5F : 4.0F;
      this.value6 = 6.5F + f2 + 14.0F;
      this.getBounds().setSize(f, this.value6);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
      this.textInputController.tick();
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      return !this.enabled ? false : this.textInputController.onMousePressed(this.getInputBounds(), n, n2, n3);
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      int n = this.enabled ? ThemeColors.textPrimary() : ThemeColors.textMuted();
      FontRegistry.font2.process2(matrix4f, drawApi, this.string4, bounds2.getX(), bounds2.getY(), 6.5F, n);
      if (this.string5 != null && !this.string5.isBlank()) {
         int n2 = this.enabled ? ThemeColors.textMuted() : ThemeColors.textDisabled();
         FontRegistry.font2.process2(matrix4f, drawApi, this.string5, bounds2.getX(), bounds2.getY() + 6.5F + 1.5F, 6.0F, n2);
      }

      GuiBounds bounds3 = this.getInputBounds();
      int n3 = ColorUtils.withAlpha(ThemeColors.formatFieldFill(), this.enabled ? 255.0F : 120.0F);
      int n4 = ColorUtils.withAlpha(ThemeColors.borderPrimary(), this.enabled ? 255.0F : 90.0F);
      drawApi.drawRoundedRectangleOutlined(matrix4f, bounds3.getX(), bounds3.getY(), bounds3.getWidth(), bounds3.getHeight(), 7.0F, 0.75F, n3, n4);
      String string = this.textInputController.getText();
      boolean bl = string.isBlank();
      boolean bl2 = bl && !this.textInputController.isFocused();
      String string2 = bl2 ? "Введите текст..." : string;
      this.value3 = FrameInterpolator.lerpTowards(this.value3, this.textInputController.isAllSelected() ? 1.0F : 0.0F, 30.0F);
      int n5 = bl2 ? ThemeColors.textPlaceholder() : ThemeColors.textPrimary();
      int n6 = bl2 ? n5 : ColorUtils.lerp(n5, ThemeColors.adjustForTheme(n5), (double)this.value3);
      int n7 = ColorUtils.withAlpha(n6, this.enabled ? 255.0F : 120.0F);
      float f2 = bounds3.getX() + 5.0F;
      float f3 = bounds3.getY() + (bounds3.getHeight() - FontRegistry.font2.process4(string2, 5.5F)) / 2.0F;
      drawApi.beginStencil(1);
      drawApi.drawRoundedRectangle(
         matrix4f, bounds3.getX() + 1.0F, bounds3.getY() + 1.0F, bounds3.getWidth() - 2.0F, bounds3.getHeight() - 2.0F, 7.0F, ColorUtils.rgba(0, 0, 0, 0)
      );
      drawApi.applyStencilMask(1);
      FontRegistry.font2.process2(matrix4f, drawApi, string2, f2, f3, 5.5F, n7);
      if (this.textInputController.isCaretVisible()) {
         float f4 = f2 + FontRegistry.font2.process3(string, 5.5F);
         FontRegistry.font2.process2(matrix4f, drawApi, "|", f4, f3, 5.5F, ThemeColors.textPrimary());
      }

      drawApi.endStencil();
      return bounds2.getY() + bounds2.getHeight();
   }

   @Override
   public boolean onCharTyped(char c) {
      return !this.enabled ? false : this.textInputController.onCharTyped(c);
   }

   @Override
   public void update2() {
      this.textInputController.blur();
      super.update2();
   }

   @Override
   public boolean onKeyPressed(int n) {
      return !this.enabled ? false : this.textInputController.onKeyPressed(n);
   }

   public void update3() {
      this.textInputController.blur();
   }

   public void update4() {
      this.mutableTextBuffer.setText("");
      this.textInputController.blur();
   }

   public boolean isActive() {
      return this.textInputController.isFocused();
   }

   private GuiBounds getInputBounds() {
      GuiBounds componentBounds = super.getBounds();
      this.inputBounds.setPosition(componentBounds.getX(), componentBounds.getY() + componentBounds.getHeight() - 14.0F);
      this.inputBounds.setSize(componentBounds.getWidth(), 14.0F);
      return this.inputBounds;
   }

   public String getString() {
      return this.textInputController.getText();
   }

   public void setString(String string) {
      this.mutableTextBuffer.setText(string);
      this.textInputController.blur();
   }

   public float getFloatType() {
      return this.value6;
   }

   @Override
   public void setBooleanType(boolean bl) {
      this.enabled = bl;
      if (!bl) {
         this.textInputController.blur();
      }
   }

   @Override
   public boolean isActive2() {
      return this.enabled;
   }
}
