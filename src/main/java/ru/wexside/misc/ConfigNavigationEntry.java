package ru.wexside.misc;

import java.util.Objects;
import ru.wexside.ui.NavigationEntry;

public final class ConfigNavigationEntry
   extends NavigationEntry
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final String string5;

   public ConfigNavigationEntry(String string, String string2, String string3, String string4) {
      super(string, string2, string3);
      this.string5 = Objects.requireNonNull(string4, "containerId");
   }

   @Override
   public String getString() {
      return this.string5;
   }
}
