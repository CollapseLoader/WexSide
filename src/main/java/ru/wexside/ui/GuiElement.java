package ru.wexside.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joml.Matrix4f;

public class GuiElement {
   protected final List<GuiElement> children = new ArrayList<>();
   private final GuiBounds bounds;
   private GuiElement parent;
   private boolean visible = true;
   private int lastMouseX;
   private int lastMouseY;

   public GuiElement(GuiBounds bounds) {
      this.bounds = bounds;
   }

   public GuiBounds getBounds() {
      return this.bounds;
   }

   public void setBounds(GuiBounds bounds) {
      this.bounds.setPosition(bounds.getX(), bounds.getY());
      this.bounds.setSize(bounds.getWidth(), bounds.getHeight());
   }

   public void addChild(GuiElement child) {
      if (child != null && child != this && !this.children.contains(child)) {
         child.setParent(this);
         this.children.add(child);
      }
   }

   public void removeChild(GuiElement child) {
      if (this.children.remove(child)) {
         child.setParent(null);
      }
   }

   public List<GuiElement> getChildren() {
      return Collections.unmodifiableList(this.children);
   }

   public GuiElement getParent() {
      return this.parent;
   }

   public void setParent(GuiElement parent) {
      this.parent = parent;
   }

   public float getAbsoluteX() {
      return (this.parent == null ? 0.0F : this.parent.getAbsoluteX()) + this.bounds.getX();
   }

   public float getAbsoluteY() {
      return (this.parent == null ? 0.0F : this.parent.getAbsoluteY()) + this.bounds.getY();
   }

   public boolean isActive2() {
      return this.visible;
   }

   public void setBooleanType(boolean visible) {
      this.visible = visible;
   }

   public void onMouseScroll(int mouseX, int mouseY, double amount) {
      this.rememberMousePosition(mouseX, mouseY);
      if (this.visible) {
         for(GuiElement child : this.children) {
            child.onMouseScroll(mouseX, mouseY, amount);
         }
      }
   }

   public boolean onMousePressed(int mouseX, int mouseY, int button) {
      this.rememberMousePosition(mouseX, mouseY);
      if (!this.visible) {
         return false;
      } else {
         for(GuiElement child : this.children) {
            if (child.onMousePressed(mouseX, mouseY, button)) {
               return true;
            }
         }

         return false;
      }
   }

   public void onMouseReleased(int mouseX, int mouseY, int button) {
      this.rememberMousePosition(mouseX, mouseY);

      for(GuiElement child : this.children) {
         child.onMouseReleased(mouseX, mouseY, button);
      }
   }

   public boolean onCharTyped(char character) {
      for(GuiElement child : this.children) {
         if (child.onCharTyped(character)) {
            return true;
         }
      }

      return false;
   }

   public boolean onKeyPressed(int keyCode) {
      for(GuiElement child : this.children) {
         if (child.onKeyPressed(keyCode)) {
            return true;
         }
      }

      return false;
   }

   public void update() {
      for(GuiElement child : this.children) {
         child.update();
      }
   }

   public void update2() {
      for(GuiElement child : this.children) {
         child.update2();
      }
   }

   public float render(float delta, Matrix4f matrix) {
      if (!this.visible) {
         return this.bounds.getY();
      } else {
         float bottom = this.bounds.getY() + this.bounds.getHeight();

         for(GuiElement child : this.children) {
            bottom = Math.max(bottom, child.render(delta, matrix));
         }

         return bottom;
      }
   }

   protected final float getLastMouseX() {
      return (float)this.lastMouseX;
   }

   protected final float getLastMouseY() {
      return (float)this.lastMouseY;
   }

   private void rememberMousePosition(int mouseX, int mouseY) {
      this.lastMouseX = mouseX;
      this.lastMouseY = mouseY;
   }
}
