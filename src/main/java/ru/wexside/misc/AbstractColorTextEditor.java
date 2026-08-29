package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;

public abstract class AbstractColorTextEditor
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   protected AbstractColorTextEditor(GuiBounds bounds2) {
      super(bounds2);
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
         int n5 = (int)((float)n - this.getBounds().getX());
         return super.onMousePressed(n5, (int)((float)n2 - this.getBounds().getY()), n3) || this.getBounds().contains((float)n, (float)n2);
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      Matrix4f matrix4f2 = new Matrix4f(matrix4f).translate(this.getBounds().getX(), this.getBounds().getY(), 0.0F);

      for(GuiElement element2 : this.children) {
         element2.render(f, matrix4f2);
      }

      return this.getBounds().getY() + this.getBounds().getHeight();
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      int n4 = (int)((float)n - this.getBounds().getX());
      int n5 = (int)((float)n2 - this.getBounds().getY());
      super.onMouseReleased(n4, n5, n3);
   }

   public abstract float getFloatType();

   public abstract float getFloatType2();
}
