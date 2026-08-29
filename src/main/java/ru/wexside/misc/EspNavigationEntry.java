package ru.wexside.misc;

import ru.wexside.ui.NavigationEntry;

public final class EspNavigationEntry
   extends NavigationEntry
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final String string = "custom:esp";

   public EspNavigationEntry() {
      super("visuals.esp", "ESP", "Д");
   }

   @Override
   public String getString() {
      return this.string;
   }
}
