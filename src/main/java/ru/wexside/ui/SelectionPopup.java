package ru.wexside.ui;

import org.joml.Matrix4f;
import ru.wexside.misc.AbstractOptionRow;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.ScrollableOptionList;

public abstract class SelectionPopup extends PopupPanel {
   protected final ScrollableOptionList optionList;

   protected SelectionPopup(GuiBounds bounds, ScrollableOptionList optionList) {
      super(bounds);
      this.optionList = optionList;
      this.addChild(optionList);
   }

   @Override
   public void update() {
      this.updateSelectionState();
      this.optionList.update();
   }

   @Override
   public boolean onMousePressed(int mouseX, int mouseY, int button) {
      if (this.isActive2() && button == 0) {
         int localX = Math.round((float)mouseX - this.getBounds().getX());
         int localY = Math.round((float)mouseY - this.getBounds().getY());

         for(AbstractOptionRow option : this.optionList.getList()) {
            if (option.getBounds().contains((float)localX, (float)localY)) {
               this.selectOption(option);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   protected void updateLayout() {
      this.updateSelectionState();
      this.optionList.getBounds().setPosition(3.0F, 3.0F);
      this.optionList.getBounds().setSize(79.0F, this.optionList.getViewportHeight());
      this.getBounds().setSize(85.0F, this.optionList.getViewportHeight() + 6.0F);
   }

   @Override
   protected void renderPopup(float delta, Matrix4f matrix, GuiDrawApi renderer) {
      this.optionList.render(delta, matrix);
   }

   protected abstract void updateSelectionState();

   protected abstract void selectOption(AbstractOptionRow var1);
}
