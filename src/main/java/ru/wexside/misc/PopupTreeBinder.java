package ru.wexside.misc;

import ru.wexside.ui.GuiElement;

public final class PopupTreeBinder {
   public static void bindTree(GuiElement root, PopupManager popupManager) {
      bindTree(root, popupManager, null);
   }

   public static void bindTree(GuiElement root, PopupManager popupManager, PopupOwner parentOwner) {
      if (root != null && popupManager != null) {
         bindRecursively(root, popupManager, parentOwner);
      }
   }

   private static void bindRecursively(GuiElement element, PopupManager popupManager, PopupOwner parentOwner) {
      if (element instanceof PopupOwner owner) {
         popupManager.register(owner, parentOwner);
      }

      for(GuiElement child : element.getChildren()) {
         bindRecursively(child, popupManager, parentOwner);
      }
   }
}
