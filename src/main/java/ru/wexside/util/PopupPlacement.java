package ru.wexside.util;

import ru.wexside.ui.GuiBounds;

public final class PopupPlacement {
   private final float edgeMargin;
   private final float anchorGap;

   public PopupPlacement(float edgeMargin, float anchorGap) {
      this.edgeMargin = edgeMargin;
      this.anchorGap = anchorGap;
   }

   public void place(GuiBounds popup, float anchorX, float anchorY, float anchorHeight, float viewportWidth, float viewportHeight) {
      float maximumX = Math.max(this.edgeMargin, viewportWidth - popup.getWidth() - this.edgeMargin);
      float x = Math.max(this.edgeMargin, Math.min(anchorX, maximumX));
      float below = anchorY + anchorHeight + this.anchorGap;
      float above = anchorY - popup.getHeight() - this.anchorGap;
      float maximumY = Math.max(this.edgeMargin, viewportHeight - popup.getHeight() - this.edgeMargin);
      float y = below + popup.getHeight() <= viewportHeight - this.edgeMargin ? below : Math.max(this.edgeMargin, Math.min(above, maximumY));
      popup.setPosition(x, y);
   }
}
