package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.setting.ColorSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorPaletteGrid;
import ru.wexside.util.GuiDrawApi;

public final class ColorPreviewButton
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final float value;
   private final String string = "Предыдущие цвета";
   private final ColorPaletteGrid colorPaletteGrid;
   private final float value2 = 5.5F;

   public ColorPreviewButton(GuiBounds bounds2, ColorSetting colorSetting) {
      super(bounds2);
      this.value = 5.0F;
      this.colorPaletteGrid = new ColorPaletteGrid(new GuiBounds(0.0F, 0.0F, bounds2.getWidth(), 0.0F), colorSetting);
      this.addChild(this.colorPaletteGrid);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
      for(GuiElement element2 : this.children) {
         element2.update();
      }
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (!this.getBounds().contains((float)n, (float)n2)) {
         return false;
      } else {
         int n4 = (int)((float)n - this.getBounds().getX());
         int n5 = (int)((float)n2 - this.getBounds().getY());
         return super.onMousePressed(n4, n5, n3);
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      this.update4();
      Matrix4f matrix4f2 = new Matrix4f(matrix4f).translate(this.getBounds().getX(), this.getBounds().getY(), 0.0F);
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      FontRegistry.font4.process2(matrix4f2, drawApi, "Предыдущие цвета", 0.0F, 0.0F, 5.5F, ThemeColors.textMuted());
      this.process4(f, matrix4f2);
      return this.getBounds().getY() + this.getBounds().getHeight();
   }

   private void process4(float f, Matrix4f matrix4f) {
      for(GuiElement element2 : this.children) {
         element2.render(f, matrix4f);
      }
   }

   public float getFloatType2() {
      this.update4();
      return this.getBounds().getHeight();
   }

   private void update4() {
      float f = FontRegistry.font4.process4("Предыдущие цвета", 5.5F);
      float f2 = f + 5.0F;
      this.colorPaletteGrid.getBounds().setPosition(0.0F, f2);
      this.colorPaletteGrid.getBounds().setSize(this.getBounds().getWidth(), this.colorPaletteGrid.getBounds().getHeight());
      float f3 = f2 + this.colorPaletteGrid.getFloatType2();
      this.getBounds().setSize(this.getBounds().getWidth(), f3);
   }
}
