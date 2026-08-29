package ru.wexside.ui;

public final class GuiBounds {
   private float x;
   private float y;
   private float width;
   private float height;

   public GuiBounds(float x, float y, float width, float height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public void setSize(float width, float height) {
      this.width = width;
      this.height = height;
   }

   public void setPosition(float x, float y) {
      this.x = x;
      this.y = y;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getWidth() {
      return this.width;
   }

   public float getHeight() {
      return this.height;
   }

   public boolean contains(float pointX, float pointY) {
      return pointX >= this.x && pointX <= this.x + this.width && pointY >= this.y && pointY <= this.y + this.height;
   }
}
