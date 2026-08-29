package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.TextFieldStyle;
import ru.wexside.misc.TextInputController;
import ru.wexside.misc.TextSettingTextAdapter;
import ru.wexside.misc.ThemeColors;
import ru.wexside.setting.TextSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.setting.SettingComponent;

public final class TextSettingComponent
   extends SettingComponent
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final String string3 = "Введите текст...";
   private final String string4 = "|";
   private final TextInputController textInputController;
   private float value;
   private float value2;
   private final TextFieldStyle callback60;

   public TextSettingComponent(TextSetting textSetting, TextFieldStyle callback60) {
      super(new GuiBounds(0.0F, 0.0F, callback60.getFloatType4(), callback60.getFloatType2()), textSetting);
      this.callback60 = callback60;
      this.textInputController = new TextInputController(new TextSettingTextAdapter(textSetting));
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
      return this.textInputController.onMousePressed(this.getBounds(), n, n2, n3);
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      String string = this.textInputController.getText();
      boolean bl = string.isBlank();
      boolean bl2 = bl && !this.textInputController.isFocused();
      String string2 = bl2 ? "Введите текст..." : string;
      float f2 = this.callback60.longType();
      float f3 = this.callback60.getFloatType();
      float f4 = bounds2.getY() + (f3 - FontRegistry.font2.process4(string2, f2)) / 2.0F;
      int n = bl2 ? ThemeColors.textPlaceholder() : ThemeColors.textPrimary();
      this.value2 = FrameInterpolator.lerpTowards(this.value2, this.textInputController.isAllSelected() ? 1.0F : 0.0F, 30.0F);
      int n2 = bl2 ? n : ColorUtils.lerp(n, ThemeColors.adjustForTheme(n), (double)this.value2);
      float f5 = bounds2.getX() + this.callback60.getFloatType4();
      float f6 = this.textInputController.isFocused() ? FontRegistry.font2.process3("|", f2) : 0.0F;
      float f7 = FontRegistry.font2.process3(string2, f2) + f6;
      float f8 = Math.max(0.0F, bounds2.getWidth() - this.callback60.getFloatType4() * 2.0F);
      float f9 = this.textInputController.isFocused() ? Math.max(0.0F, f7 - f8) : 0.0F;
      this.value = FrameInterpolator.lerpTowards(this.value, f9, 30.0F);
      float f10 = f5 - this.value;
      drawApi.drawRoundedRectangleOutlined(
         matrix4f,
         bounds2.getX(),
         bounds2.getY(),
         bounds2.getWidth(),
         f3,
         this.callback60.getFloatType3(),
         this.callback60.getFloatType3(),
         this.callback60.getIntType(),
         this.callback60.getIntType2()
      );
      drawApi.beginStencil(1);
      drawApi.drawRoundedRectangle(
         matrix4f,
         bounds2.getX() + 1.0F,
         bounds2.getY() + 1.0F,
         bounds2.getWidth() - 2.0F,
         f3 - 2.0F,
         this.callback60.getFloatType3(),
         ColorUtils.rgba(0, 0, 0, 0)
      );
      drawApi.applyStencilMask(1);
      FontRegistry.font2.process2(matrix4f, drawApi, string2, f10, f4, f2, n2);
      if (this.textInputController.isCaretVisible()) {
         float f11 = f10 + FontRegistry.font2.process3(string2, f2);
         FontRegistry.font2.process2(matrix4f, drawApi, "|", f11, f4, f2, ThemeColors.textPrimary());
      }

      drawApi.endStencil();
      return bounds2.getY() + bounds2.getHeight();
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      this.textInputController.blurIfOutside(this.getBounds(), n, n2);
   }

   @Override
   public boolean onCharTyped(char c) {
      return this.textInputController.onCharTyped(c);
   }

   @Override
   public void update2() {
      this.textInputController.blur();
      super.update2();
   }

   @Override
   public boolean onKeyPressed(int n) {
      return this.textInputController.onKeyPressed(n);
   }

   @Override
   public float getFloatType() {
      return this.callback60.getFloatType4();
   }

   @Override
   public float getFloatType2() {
      return this.callback60.getFloatType2();
   }
}
