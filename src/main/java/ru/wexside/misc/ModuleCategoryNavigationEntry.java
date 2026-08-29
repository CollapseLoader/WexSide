package ru.wexside.misc;

import java.util.Objects;
import ru.wexside.module.ModuleCategory;
import ru.wexside.ui.NavigationEntry;

public final class ModuleCategoryNavigationEntry
   extends NavigationEntry
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final ModuleCategory moduleCategory;

   public ModuleCategoryNavigationEntry(String string, String string2, String string3, ModuleCategory moduleCategory) {
      super(string, string2, string3);
      this.moduleCategory = Objects.requireNonNull(moduleCategory, "category");
   }

   public ModuleCategory getModuleCategory() {
      return this.moduleCategory;
   }
}
