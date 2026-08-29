package ru.wexside.misc;

import ru.wexside.ui.PopupPanel;

public interface PopupOwner {
   PopupPanel getPopup();

   default boolean process6(int n, int n2) {
      return false;
   }

   void update2();

   default void setPopupManager(PopupManager popupManager) {
   }
}
