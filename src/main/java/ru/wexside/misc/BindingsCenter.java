package ru.wexside.misc;

import ru.wexside.ui.NavigationEntry;

public final class BindingsCenter
   extends NavigationEntry
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final String string = "custom:bindings-center";

   public BindingsCenter() {
      super("settings.bindings", "Bindings Center", "Л");
   }

   @Override
   public String getString() {
      return this.string;
   }
}
