package ru.wexside.ui.color;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.ThemeColors;
import ru.wexside.setting.ColorSetting;
import ru.wexside.ui.FloatingPanel;
import ru.wexside.ui.FloatingPanelManager;
import ru.wexside.ui.FloatingPanelProvider;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.GuiDrawApi;

public final class ColorPickerButton extends GuiElement implements FloatingPanelProvider {
   private final String icon;
   private final float iconSize;
   private final ColorPickerPopup popup;
   private FloatingPanelManager manager;

   public ColorPickerButton(String icon, float iconSize, ColorSetting setting) {
      super(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F));
      this.icon = icon;
      this.iconSize = iconSize;
      this.popup = new ColorPickerPopup(setting);
   }

   @Override
   public boolean onMousePressed(int mouseX, int mouseY, int button) {
      if (!this.getBounds().contains((float)mouseX, (float)mouseY)) {
         return false;
      } else {
         if (button == 0 && this.manager != null) {
            this.manager.toggle(this);
         }

         return true;
      }
   }

   @Override
   public float render(float delta, Matrix4f matrix) {
      GuiBounds bounds = this.getBounds();
      GuiDrawApi renderer = WexSideClient.getGuiRenderer();
      if (renderer != null) {
         float width = FontRegistry.font3.process3(this.icon, this.iconSize);
         float height = FontRegistry.font3.process4(this.icon, this.iconSize);
         float x = bounds.getX() + (bounds.getWidth() - width) / 2.0F;
         float y = bounds.getY() + (bounds.getHeight() - height) / 2.0F;
         FontRegistry.font3.process5(matrix, renderer, this.icon, x, y, this.iconSize, ThemeColors.textSecondary());
      }

      return bounds.getX() + bounds.getWidth();
   }

   @Override
   public FloatingPanel getFloatingPanel() {
      return this.popup;
   }

   @Override
   public void updateFloatingPanelPosition() {
      GuiElement parent = this.getParent();
      if (parent != null) {
         this.popup.getBounds().setPosition(parent.getBounds().getWidth() + 4.0F, 0.0F);
      }
   }

   @Override
   public void setFloatingPanelManager(FloatingPanelManager manager) {
      this.manager = manager;
      this.popup.setParent(manager);
   }
}
