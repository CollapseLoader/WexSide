package ru.wexside.misc;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import org.joml.Matrix4f;
import ru.wexside.config.ConfigCatalogView;
import ru.wexside.config.LocalConfigCatalog;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.module.ModuleManager;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.ui.NavigationEntry;
import ru.wexside.util.EspSettingsPanel;
import ru.wexside.util.KeybindsPanel;
import ru.wexside.util.ModuleSearchResults;

public final class ModuleBrowser
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final NavigationState navigationState;
   private final Map<String, String> map2 = new LinkedHashMap<>();
   private final LocalConfigCatalog configCatalog;
   private final Map<String, GuiElement> map3 = new LinkedHashMap<>();
   private final ContainerDisplay containerDisplay;

   public ModuleBrowser(
      GuiBounds bounds2,
      NavigationState navigationState,
      Collection<NavigationEntry> collection,
      ModuleManager moduleManager,
      ContainerDisplay containerDisplay,
      LocalConfigCatalog configCatalog,
      SearchQueryState searchQueryState,
      Consumer<Module> consumer,
      Function<Module, String> function
   ) {
      super(bounds2);
      this.navigationState = navigationState;
      this.containerDisplay = containerDisplay;
      this.configCatalog = configCatalog;
      this.process6(collection, moduleManager);
      this.process7(moduleManager, searchQueryState, consumer, function);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
      GuiElement element2 = this.getElement();
      if (element2 != null) {
         element2.onMouseScroll(n, n2, d);
      }
   }

   @Override
   public void update() {
      for(GuiElement element2 : this.map3.values()) {
         element2.update();
      }
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      GuiElement element2 = this.getElement();
      return element2 != null && element2.onMousePressed(n, n2, n3);
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiElement element2 = this.getElement();
      if (element2 == null) {
         return bounds2.getY() + bounds2.getHeight();
      } else {
         element2.getBounds().setPosition(bounds2.getX(), bounds2.getY());
         element2.getBounds().setSize(bounds2.getWidth(), bounds2.getHeight());
         return element2.render(f, matrix4f);
      }
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      GuiElement element2 = this.getElement();
      if (element2 != null) {
         element2.onMouseReleased(n, n2, n3);
      }
   }

   public boolean isActive() {
      GuiElement element2 = this.map3.get("search");
      ModuleSearchResults moduleSearchResults;
      return element2 instanceof ModuleSearchResults && (moduleSearchResults = (ModuleSearchResults)element2).isActive();
   }

   public void process4(ModuleCategory moduleCategory, Module module) {
      String string = moduleCategory.name().toLowerCase(Locale.ROOT);
      String string2 = "modules:" + string;
      GuiElement element2 = this.map3.get(string2);
      if (element2 instanceof ModuleListPanel miscellaneousModules) {
         miscellaneousModules.setModule(module);
      }
   }

   @Override
   public void update2() {
      GuiElement element2 = this.getElement();
      if (element2 != null) {
         element2.update2();
      }
   }

   public void setModule(Module module) {
      for(GuiElement element2 : this.map3.values()) {
         if (element2 instanceof EspSettingsPanel eSPPreview) {
            eSPPreview.setModule(module);
            return;
         }
      }
   }

   @Override
   public boolean isActive2() {
      return this.getElement() instanceof ModuleListPanel;
   }

   private GuiElement process5(NavigationEntry navigationEntry2, ModuleManager moduleManager) {
      GuiBounds bounds2 = new GuiBounds(this.getBounds().getX(), this.getBounds().getY(), this.getBounds().getWidth(), this.getBounds().getHeight());
      if (navigationEntry2 instanceof ModuleCategoryNavigationEntry categoryEntry) {
         return new ModuleListPanel(bounds2, categoryEntry.getModuleCategory(), moduleManager, this.containerDisplay);
      } else if (navigationEntry2 instanceof ConfigNavigationEntry configEntry) {
         return new ConfigCatalogView(bounds2, navigationEntry2.getDisplayName(), this.configCatalog);
      } else if (navigationEntry2 instanceof EspNavigationEntry) {
         return new EspSettingsPanel(bounds2, navigationEntry2.getDisplayName(), moduleManager, this.containerDisplay);
      } else if (navigationEntry2 instanceof BindingsCenter) {
         return new KeybindsPanel(bounds2, moduleManager);
      } else {
         String string = navigationEntry2.getClass().getName();
         throw new IllegalStateException("Unsupported tab type: " + string);
      }
   }

   private void process6(Collection<NavigationEntry> collection, ModuleManager moduleManager) {
      for(NavigationEntry navigationEntry2 : collection) {
         String string = this.process8(navigationEntry2);
         this.map2.put(navigationEntry2.getString(), string);
         if (!this.map3.containsKey(string)) {
            GuiElement element2 = this.process5(navigationEntry2, moduleManager);
            this.map3.put(string, element2);
            this.addChild(element2);
         }
      }
   }

   private void process7(ModuleManager moduleManager, SearchQueryState searchQueryState, Consumer<Module> consumer, Function<Module, String> function) {
      String string = "search";
      this.map2.put("__search__", string);
      GuiBounds bounds2 = new GuiBounds(this.getBounds().getX(), this.getBounds().getY(), this.getBounds().getWidth(), this.getBounds().getHeight());
      ModuleSearchResults moduleSearchResults = new ModuleSearchResults(bounds2, moduleManager, searchQueryState, consumer, function);
      this.map3.put(string, moduleSearchResults);
      this.addChild(moduleSearchResults);
   }

   public Map<String, GuiElement> getMap() {
      return this.map3;
   }

   private GuiElement getElement() {
      String string = this.navigationState.string4();
      if (string == null) {
         return null;
      } else {
         String string2 = this.map2.get(string);
         return string2 == null ? null : this.map3.get(string2);
      }
   }

   public Map<String, String> getMap2() {
      return this.map2;
   }

   public void update3() {
      GuiElement element2 = this.getElement();
      if (element2 instanceof ModuleListPanel miscellaneousModules) {
         miscellaneousModules.update3();
      }
   }

   private String process8(NavigationEntry navigationEntry2) {
      if (navigationEntry2 instanceof ModuleCategoryNavigationEntry categoryEntry) {
         ModuleCategory moduleCategory = categoryEntry.getModuleCategory();
         String string = moduleCategory.name().toLowerCase(Locale.ROOT);
         return "modules:" + string;
      } else if (navigationEntry2 instanceof ConfigNavigationEntry configEntry) {
         return configEntry.getString();
      } else if (navigationEntry2 instanceof EspNavigationEntry eSP) {
         return eSP.getString();
      } else if (navigationEntry2 instanceof BindingsCenter bindingsCenter) {
         return bindingsCenter.getString();
      } else {
         String string = navigationEntry2.getClass().getName();
         throw new IllegalStateException("Unsupported tab type: " + string);
      }
   }
}
