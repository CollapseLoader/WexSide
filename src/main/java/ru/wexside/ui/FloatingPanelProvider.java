package ru.wexside.ui;

public interface FloatingPanelProvider {
   FloatingPanel getFloatingPanel();

   void updateFloatingPanelPosition();

   default void setFloatingPanelManager(FloatingPanelManager manager) {
   }
}
