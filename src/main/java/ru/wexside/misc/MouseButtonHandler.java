package ru.wexside.misc;

public interface MouseButtonHandler {
   boolean onMousePressed(int var1, int var2, int var3);

   default void onMouseReleased(int mouseX, int mouseY, int button) {
   }
}
