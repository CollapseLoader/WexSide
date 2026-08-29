package ru.wexside.misc;

public final class GuiInteractionState {
   private static final GuiInteractionState INSTANCE = new GuiInteractionState();
   private static final MovablePanel EMPTY_PANEL = new MovablePanel(0, 0, 0, 0) {
   };
   private int scaledMouseX;
   private int scaledMouseY;
   private int rawMouseX;
   private int rawMouseY;
   private MovablePanel rootPanel;

   private GuiInteractionState() {
   }

   public static GuiInteractionState getInstance() {
      return INSTANCE;
   }

   public int getScaledMouseX() {
      return this.scaledMouseX;
   }

   public int getScaledMouseY() {
      return this.scaledMouseY;
   }

   public int getRawMouseX() {
      return this.rawMouseX;
   }

   public int getRawMouseY() {
      return this.rawMouseY;
   }

   public void setScaledMousePosition(int x, int y) {
      this.scaledMouseX = x;
      this.scaledMouseY = y;
   }

   public void setRawMousePosition(int x, int y) {
      this.rawMouseX = x;
      this.rawMouseY = y;
   }

   public MovablePanel getRootPanel() {
      return this.rootPanel == null ? EMPTY_PANEL : this.rootPanel;
   }

   public void setRootPanel(MovablePanel panel) {
      this.rootPanel = panel;
   }
}
