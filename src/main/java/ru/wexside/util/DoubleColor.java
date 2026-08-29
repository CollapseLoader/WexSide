package ru.wexside.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.joml.Matrix4f;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.ColorModeLabels;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.setting.color.ColorMode;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;

public final class DoubleColor
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private ColorMode colorMode2;
   private final float value;
   private Consumer<ColorMode> consumer;
   private final List<ColorModeOptionButton> modeButtons = new ArrayList<>();
   private final float value2;
   private final float value3;

   public DoubleColor(GuiBounds bounds2) {
      super(bounds2);
      this.value = 12.5F;
      this.value3 = 9.5F;
      this.value2 = 2.0F;
      this.colorMode2 = ColorMode.STATIC;
      this.consumer = colorMode2 -> {
      };
      this.update3();
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
      if (n3 == 0 && this.getBounds().contains((float)n, (float)n2)) {
         int n4 = (int)((float)n - this.getBounds().getX());
         int n5 = (int)((float)n2 - this.getBounds().getY());

         for(ColorModeOptionButton colorModeOptionButton : this.modeButtons) {
            if (colorModeOptionButton.onMousePressed(n4, n5, n3)) {
               ColorMode colorMode2 = colorModeOptionButton.getColorModeLabels().getColorMode();
               this.setColorMode(colorMode2 == this.colorMode2 ? ColorMode.STATIC : colorMode2);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      this.update4();
      Matrix4f matrix4f2 = new Matrix4f(matrix4f).translate(this.getBounds().getX(), this.getBounds().getY(), 0.0F);

      for(ColorModeOptionButton colorModeOptionButton : this.modeButtons) {
         colorModeOptionButton.render(f, matrix4f2);
      }

      return this.getBounds().getY() + this.getBounds().getHeight();
   }

   public float getFloatType() {
      return this.modeButtons.isEmpty() ? 0.0F : (float)this.modeButtons.size() * 12.5F + (float)Math.max(0, this.modeButtons.size() - 1) * 2.0F;
   }

   private void syncSelection() {
      for(ColorModeOptionButton colorModeOptionButton : this.modeButtons) {
         colorModeOptionButton.setBooleanType(colorModeOptionButton.getColorModeLabels().getColorMode() == this.colorMode2);
      }
   }

   public void setColorMode(ColorMode colorMode2) {
      ColorMode colorMode3 = colorMode2 == null ? ColorMode.STATIC : colorMode2;
      if (this.colorMode2 != colorMode3) {
         this.colorMode2 = colorMode3;
         this.syncSelection();
         this.consumer.accept(this.colorMode2);
      }
   }

   public void setConsumer(Consumer<ColorMode> consumer) {
      this.consumer = consumer == null ? colorMode2 -> {
      } : consumer;
   }

   public float getSpacing() {
      return 2.0F;
   }

   public Consumer<ColorMode> getConsumer() {
      return this.consumer;
   }

   private void update3() {
      for(ColorModeLabels colorModeLabels : List.of(
         new ColorModeLabels(ColorMode.ASTOLFO, "Astolfo", "д"), new ColorModeLabels(ColorMode.DOUBLE_COLOR, "Double Color", "V")
      )) {
         ColorModeOptionButton colorModeOptionButton = new ColorModeOptionButton(new GuiBounds(0.0F, 0.0F, 12.5F, 9.5F), colorModeLabels);
         this.modeButtons.add(colorModeOptionButton);
         this.addChild(colorModeOptionButton);
      }

      this.syncSelection();
   }

   public ColorMode getColorMode() {
      return this.colorMode2;
   }

   public float getFloatType3() {
      return 9.5F;
   }

   public float getFloatType4() {
      return 12.5F;
   }

   public List<ColorModeOptionButton> getList() {
      return this.modeButtons;
   }

   public float getFloatType2() {
      return 9.5F;
   }

   private void update4() {
      float f = 0.0F;

      for(ColorModeOptionButton colorModeOptionButton : this.modeButtons) {
         colorModeOptionButton.getBounds().setPosition(f, 0.0F);
         colorModeOptionButton.getBounds().setSize(12.5F, 9.5F);
         f += 14.5F;
      }

      this.getBounds().setSize(this.getFloatType(), this.getFloatType2());
   }
}
