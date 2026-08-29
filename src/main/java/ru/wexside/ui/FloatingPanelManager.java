package ru.wexside.ui;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;

public final class FloatingPanelManager extends GuiElement {
   private final List<FloatingPanelProvider> providers = new ArrayList<>();

   public FloatingPanelManager() {
      super(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F));
   }

   @Override
   public void onMouseScroll(int mouseX, int mouseY, double amount) {
      for(int index = this.providers.size() - 1; index >= 0; --index) {
         FloatingPanel panel = this.panelOf(this.providers.get(index));
         if (panel != null && panel.isActive2() && panel.getBounds().contains((float)mouseX, (float)mouseY)) {
            panel.onMouseScroll(mouseX, mouseY, amount);
            return;
         }
      }
   }

   @Override
   public void update() {
      for(FloatingPanelProvider provider : this.providers) {
         provider.updateFloatingPanelPosition();
         FloatingPanel panel = provider.getFloatingPanel();
         if (panel != null) {
            panel.update();
         }
      }
   }

   @Override
   public boolean onMousePressed(int mouseX, int mouseY, int button) {
      for(int index = this.providers.size() - 1; index >= 0; --index) {
         FloatingPanelProvider provider = this.providers.get(index);
         FloatingPanel panel = this.panelOf(provider);
         if (panel != null && panel.isActive2() && panel.getBounds().contains((float)mouseX, (float)mouseY)) {
            panel.onMousePressed(mouseX, mouseY, button);
            return true;
         }
      }

      return false;
   }

   @Override
   public void onMouseReleased(int mouseX, int mouseY, int button) {
      for(FloatingPanelProvider provider : this.providers) {
         FloatingPanel panel = provider.getFloatingPanel();
         if (panel != null && panel.isActive2()) {
            panel.onMouseReleased(mouseX, mouseY, button);
         }
      }
   }

   @Override
   public boolean onCharTyped(char character) {
      for(int index = this.providers.size() - 1; index >= 0; --index) {
         FloatingPanel panel = this.providers.get(index).getFloatingPanel();
         if (panel != null && panel.isActive2() && panel.onCharTyped(character)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean onKeyPressed(int keyCode) {
      for(int index = this.providers.size() - 1; index >= 0; --index) {
         FloatingPanel panel = this.providers.get(index).getFloatingPanel();
         if (panel != null && panel.isActive2() && panel.onKeyPressed(keyCode)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public float render(float delta, Matrix4f matrix) {
      float bottom = 0.0F;

      for(FloatingPanelProvider provider : this.providers) {
         FloatingPanel panel = this.panelOf(provider);
         if (panel != null && panel.isActive()) {
            bottom = Math.max(bottom, panel.render(delta, matrix));
         }
      }

      return bottom;
   }

   @Override
   public void update2() {
      this.closeAll();
   }

   public void closeAll() {
      for(FloatingPanelProvider provider : this.providers) {
         FloatingPanel panel = provider.getFloatingPanel();
         if (panel != null) {
            panel.setBooleanType(false);
         }
      }
   }

   public void registerTree(GuiElement root) {
      if (root != null) {
         if (root instanceof FloatingPanelProvider provider) {
            this.register(provider);
         }

         for(GuiElement child : root.getChildren()) {
            this.registerTree(child);
         }
      }
   }

   public void register(FloatingPanelProvider provider) {
      if (provider != null && !this.providers.contains(provider)) {
         this.providers.add(provider);
         provider.setFloatingPanelManager(this);
      }
   }

   public void toggle(FloatingPanelProvider selected) {
      FloatingPanel selectedPanel = selected == null ? null : selected.getFloatingPanel();
      if (selectedPanel != null) {
         boolean open = !selectedPanel.isActive2();

         for(FloatingPanelProvider provider : this.providers) {
            FloatingPanel panel = provider.getFloatingPanel();
            if (panel != null) {
               panel.setBooleanType(false);
            }
         }

         selectedPanel.setBooleanType(open);
      }
   }

   @Override
   public GuiBounds getBounds() {
      GuiBounds union = null;

      for(FloatingPanelProvider provider : this.providers) {
         FloatingPanel panel = this.panelOf(provider);
         if (panel != null && panel.isActive()) {
            GuiBounds bounds = panel.getBounds();
            if (union == null) {
               union = new GuiBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
            } else {
               float minX = Math.min(union.getX(), bounds.getX());
               float minY = Math.min(union.getY(), bounds.getY());
               float maxX = Math.max(union.getX() + union.getWidth(), bounds.getX() + bounds.getWidth());
               float maxY = Math.max(union.getY() + union.getHeight(), bounds.getY() + bounds.getHeight());
               union.setPosition(minX, minY);
               union.setSize(maxX - minX, maxY - minY);
            }
         }
      }

      return union;
   }

   private FloatingPanel panelOf(FloatingPanelProvider provider) {
      provider.updateFloatingPanelPosition();
      return provider.getFloatingPanel();
   }
}
