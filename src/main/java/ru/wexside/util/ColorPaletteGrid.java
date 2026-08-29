package ru.wexside.util;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.ColorSwatch;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.setting.ColorSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;

public final class ColorPaletteGrid
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final float value;
   private final List<ColorSwatch> swatches = new ArrayList<>();
   private final int slot;
   private int slot2;
   private final float value2 = 4.0F;

   public ColorPaletteGrid(GuiBounds bounds2, ColorSetting colorSetting) {
      super(bounds2);
      this.value = 4.0F;
      this.slot = 2;

      for(int i = 0; i < colorSetting.getRecentColors().size(); ++i) {
         ColorSwatch colorSwatch = new ColorSwatch(colorSetting, i);
         this.swatches.add(colorSwatch);
         this.addChild(colorSwatch);
      }

      this.update4();
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
      } else {
         int n5 = (int)((float)n - this.getBounds().getX());
         return super.onMousePressed(n5, (int)((float)n2 - this.getBounds().getY()), n3) || this.getBounds().contains((float)n, (float)n2);
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      this.update4();
      Matrix4f matrix4f2 = new Matrix4f(matrix4f).translate(this.getBounds().getX(), this.getBounds().getY(), 0.0F);
      float f2 = this.getBounds().getX() + this.getBounds().getWidth();

      for(int i = 0; i < this.slot2; ++i) {
         f2 = this.swatches.get(i).render(f, matrix4f2);
      }

      return f2;
   }

   private float process4(int n, float f) {
      return n <= 1 ? 0.0F : (this.getBounds().getWidth() - (float)n * f) / (float)(n - 1);
   }

   private int process5(float f) {
      float f2 = f + 4.0F;
      return f2 <= 0.0F ? 1 : Math.max(1, (int)Math.floor((double)((this.getBounds().getWidth() + 4.0F) / f2)));
   }

   public float getFloatType2() {
      this.update4();
      return this.getBounds().getHeight();
   }

   private void update4() {
      if (this.swatches.isEmpty()) {
         this.getBounds().setSize(this.getBounds().getWidth(), 0.0F);
         this.slot2 = 0;
      } else {
         float f = this.swatches.get(0).getFloatType();
         int n = this.process5(f);
         float f2 = this.process4(n, f);
         this.slot2 = Math.min(this.swatches.size(), n * 2);
         int n2 = Math.max(1, (int)Math.ceil((double)this.slot2 / (double)n));

         for(int i = 0; i < this.swatches.size(); ++i) {
            ColorSwatch colorSwatch = this.swatches.get(i);
            boolean bl = i < this.slot2;
            int n3 = i / n;
            int n4 = i % n;
            if (!bl) {
               colorSwatch.getBounds().setPosition(-9999.0F, -9999.0F);
            } else {
               float f3 = (float)n4 * (f + f2);
               float f4 = (float)n3 * (f + 4.0F);
               colorSwatch.getBounds().setPosition(f3, f4);
               colorSwatch.getBounds().setSize(f, f);
            }
         }

         float f5 = (float)n2 * f + (float)Math.max(0, n2 - 1) * 4.0F;
         this.getBounds().setSize(this.getBounds().getWidth(), f5);
      }
   }
}
