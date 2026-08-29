package ru.wexside.misc;

import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.Easing;

public abstract class MovablePanel
   extends GuiElement
   implements MouseScrollHandler,
   GuiRenderable,
   BoundsProvider,
   MouseButtonHandler,
   CharacterInputHandler,
   LayoutUpdater,
   KeyPressHandler {
   private int horizontalSelection;
   private int verticalSelection;
   private boolean auxiliaryState;
   private boolean visible;
   private boolean open;
   private final TransitionAnimation transition = new TransitionAnimation(Easing.EASE_OUT_CUBIC, Easing.EASE_IN_CUBIC);

   protected MovablePanel(int x, int y, int width, int height) {
      super(new GuiBounds((float)x, (float)y, (float)width, (float)height));
   }

   public TransitionAnimation getTransition() {
      return this.transition;
   }

   public GuiBounds getVisibleBounds() {
      return new GuiBounds(0.0F, 0.0F, this.getBounds().getWidth(), this.getBounds().getHeight());
   }

   public boolean isActive() {
      return this.open;
   }

   public void process6(int horizontalSelection, int verticalSelection) {
      this.horizontalSelection = horizontalSelection;
      this.verticalSelection = verticalSelection;
   }

   public void setIntType(int verticalSelection) {
      this.verticalSelection = verticalSelection;
   }

   public int getIntType() {
      return this.verticalSelection;
   }

   public void setIntType2(int horizontalSelection) {
      this.horizontalSelection = horizontalSelection;
   }

   public int getIntType2() {
      return this.horizontalSelection;
   }

   @Override
   public void setBooleanType(boolean visible) {
      this.visible = visible;
   }

   @Override
   public boolean isActive2() {
      return this.visible;
   }

   public void setBooleanType2(boolean open) {
      this.open = open;
   }

   public void setBooleanType3(boolean auxiliaryState) {
      this.auxiliaryState = auxiliaryState;
   }

   public boolean isActive3() {
      return this.auxiliaryState;
   }

   public void process7(int x, int y) {
      this.getBounds().setPosition((float)x, (float)y);
   }

   public void update3() {
      this.open = true;
   }

   public void update4() {
   }
}
