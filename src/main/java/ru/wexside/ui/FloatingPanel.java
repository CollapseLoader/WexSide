package ru.wexside.ui;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.ThemeColors;
import ru.wexside.misc.ThemeManager;
import ru.wexside.util.ClippedLayerRenderer;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public class FloatingPanel extends GuiElement {
   private boolean open;
   private float openingProgress;

   public FloatingPanel(GuiBounds bounds) {
      super(bounds);
      super.setBooleanType(false);
   }

   public boolean isActive() {
      return this.open || this.openingProgress > 0.01F;
   }

   @Override
   public boolean isActive2() {
      return this.open;
   }

   @Override
   public void setBooleanType(boolean open) {
      this.open = open;
      super.setBooleanType(open);
      if (open) {
         this.updateLayout();
      }
   }

   @Override
   public float render(float delta, Matrix4f matrix) {
      this.openingProgress = FrameInterpolator.lerpTowards(this.openingProgress, this.open ? 1.0F : 0.0F, 30.0F);
      if (this.openingProgress <= 0.01F) {
         return this.getBounds().getY();
      } else {
         this.updateLayout();
         GuiDrawApi renderer = WexSideClient.getGuiRenderer();
         if (renderer != null) {
            GuiBounds bounds = this.getBounds();
            boolean animated = this.openingProgress < 0.99F;
            int opacity = ColorUtils.withAlpha(-1, 255.0F * this.openingProgress);
            ClippedLayerRenderer.process(
               renderer, matrix, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 0.0F, animated, opacity, localMatrix -> {
                  this.renderSurface(localMatrix, renderer);
                  this.renderPanel(delta, localMatrix, renderer);
               }
            );
         }

         return this.getBounds().getY() + this.getBounds().getHeight();
      }
   }

   @Override
   public boolean onMousePressed(int mouseX, int mouseY, int button) {
      GuiBounds bounds = this.getBounds();
      if (this.open && bounds.contains((float)mouseX, (float)mouseY)) {
         int localX = Math.round((float)mouseX - bounds.getX());
         int localY = Math.round((float)mouseY - bounds.getY());
         return super.onMousePressed(localX, localY, button) || bounds.contains((float)mouseX, (float)mouseY);
      } else {
         return false;
      }
   }

   @Override
   public void onMouseReleased(int mouseX, int mouseY, int button) {
      GuiBounds bounds = this.getBounds();
      super.onMouseReleased(Math.round((float)mouseX - bounds.getX()), Math.round((float)mouseY - bounds.getY()), button);
   }

   protected void updateLayout() {
   }

   protected void renderPanel(float delta, Matrix4f matrix, GuiDrawApi renderer) {
      for(GuiElement child : this.children) {
         child.render(delta, matrix);
      }
   }

   private void renderSurface(Matrix4f matrix, GuiDrawApi renderer) {
      GuiBounds bounds = this.getBounds();
      float width = bounds.getWidth();
      float height = bounds.getHeight();
      if (ThemeManager.getThemeManager().isBlurEnabled()) {
         renderer.drawBlurredRoundedRectangle(matrix, 0.0F, 0.0F, width, height, 6.0F);
      }

      renderer.drawRoundedRectangle(matrix, 0.0F, 0.0F, width, height, 6.0F, ThemeColors.panelBackground());
      renderer.drawRoundedRectangleOutlined(
         matrix, 0.0F, 0.0F, width, height, 6.0F, 0.75F, ColorUtils.withAlpha(ThemeColors.backgroundPrimary(), 0.0F), ThemeColors.borderPrimary()
      );
   }
}
