package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.setting.ColorSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.color.ColorPickerButton;
import ru.wexside.ui.setting.SettingComponent;
import ru.wexside.util.ColorPalette;
import ru.wexside.util.GuiDrawApi;

public final class ColorSettingComponent
   extends SettingComponent
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final float value;
   private final float value2;
   private final float value3;
   private final float value4;
   private final float value5;
   private final float value6;
   private final float value7 = 142.5F;
   private final float value8;
   private final float value9 = 17.0F;
   private final float value10;
   private final ColorPalette colorPalette;
   private final String string2;
   private final ColorPickerButton colorPickerButton;
   private final float value11 = 13.0F;

   public ColorSettingComponent(ColorSetting colorSetting) {
      super(new GuiBounds(0.0F, 0.0F, 142.5F, 13.0F), colorSetting);
      this.value5 = 8.0F;
      this.value4 = 1.0F;
      this.value6 = 34.5F;
      this.value8 = 17.0F;
      this.value10 = 5.5F;
      this.value = 3.25F;
      this.value2 = 7.0F;
      this.value3 = 2.0F;
      this.string2 = "Z";
      this.colorPalette = new ColorPalette(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F), colorSetting);
      this.colorPickerButton = new ColorPickerButton("Z", 7.0F, colorSetting);
      this.addChild(this.colorPalette);
      this.addChild(this.colorPickerButton);
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
         float f = 35.0F;
         float f2 = 85.0F;
         float f3 = f + (f2 - this.colorPalette.getFloatType());
         float f4 = (13.0F - this.colorPalette.getFloatType2()) / 2.0F;
         this.colorPalette.getBounds().setPosition(f3, f4);
         this.colorPalette.getBounds().setSize(this.colorPalette.getFloatType(), this.colorPalette.getFloatType2());
         int n4 = (int)((float)n - this.getBounds().getX());
         int n5 = (int)((float)n2 - this.getBounds().getY());
         if (this.colorPalette.onMousePressed(n4, n5, n3)) {
            return true;
         } else {
            this.colorPickerButton.getBounds().setPosition(125.5F, 0.0F);
            this.colorPickerButton.getBounds().setSize(17.0F, 13.0F);
            return this.colorPickerButton.onMousePressed(n4, n5, n3) ? true : true;
         }
      } else {
         return false;
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      int n = ThemeColors.borderPrimary();
      String string = ((ColorSetting)this.getSetting()).getModeLabel();
      float f3 = bounds2.getX() + 34.5F;
      float f4 = bounds2.getX() + bounds2.getWidth() - 17.0F;
      float f5 = 30.5F;
      float f2 = 5.5F;

      while(f2 > 3.25F && FontRegistry.font2.process3(string, f2) > f5) {
         f2 -= 0.25F;
      }

      float f6 = FontRegistry.font2.process3(string, f2);
      float f7 = FontRegistry.font2.process4(string, f2);
      float f8 = bounds2.getX() + (34.5F - f6) / 2.0F;
      float f9 = bounds2.getY() + (13.0F - f7) / 2.0F;
      float f10 = 35.0F;
      float f11 = bounds2.getWidth() - 34.5F - 17.0F - 6.0F;
      float f12 = f10 + (f11 - this.colorPalette.getFloatType());
      float f13 = (13.0F - this.colorPalette.getFloatType2()) / 2.0F;
      this.colorPalette.getBounds().setPosition(f12, f13);
      this.colorPalette.getBounds().setSize(this.colorPalette.getFloatType(), this.colorPalette.getFloatType2());
      this.colorPickerButton.getBounds().setPosition(bounds2.getWidth() - 17.0F, 0.0F);
      this.colorPickerButton.getBounds().setSize(17.0F, 13.0F);
      drawApi.drawRoundedRectangleOutlined(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), 13.0F, 8.0F, 1.0F, ThemeColors.controlFill(), n);
      drawApi.fillRectangle(matrix4f, f3, bounds2.getY(), 0.5F, 13.0F, n);
      drawApi.fillRectangle(matrix4f, f4, bounds2.getY(), 0.5F, 13.0F, n);
      FontRegistry.font2.process2(matrix4f, drawApi, string, f8, f9, f2, ThemeColors.textSecondary());
      this.colorPalette.render(f, new Matrix4f(matrix4f).translate(bounds2.getX(), bounds2.getY(), 0.0F));
      this.colorPickerButton.render(f, new Matrix4f(matrix4f).translate(bounds2.getX(), bounds2.getY(), 0.0F));
      return bounds2.getY() + 13.0F;
   }

   @Override
   public float getFloatType() {
      return 142.5F;
   }

   @Override
   public float getFloatType2() {
      return 17.0F;
   }
}
