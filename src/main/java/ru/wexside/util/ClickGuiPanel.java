package ru.wexside.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import ru.wexside.WexSideClient;
import ru.wexside.config.LocalConfigCatalog;
import ru.wexside.misc.BindingsCenter;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.ClasspathResource;
import ru.wexside.misc.ConfigNavigationEntry;
import ru.wexside.misc.ContainerDisplay;
import ru.wexside.misc.ContainerDisplaySettings;
import ru.wexside.misc.EspNavigationEntry;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutModeButton;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.ModuleBrowser;
import ru.wexside.misc.ModuleCategoryNavigationEntry;
import ru.wexside.misc.ModuleTogglePulse;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.MovablePanel;
import ru.wexside.misc.NavigationContent;
import ru.wexside.misc.NavigationSelectionListener;
import ru.wexside.misc.NavigationState;
import ru.wexside.misc.PopupManager;
import ru.wexside.misc.PopupTreeBinder;
import ru.wexside.misc.PreparedLayer;
import ru.wexside.misc.SearchBar;
import ru.wexside.misc.SearchQueryState;
import ru.wexside.misc.SidebarToggleButton;
import ru.wexside.misc.Table;
import ru.wexside.misc.TextureResource;
import ru.wexside.misc.ThemeColors;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.module.ModuleManager;
import ru.wexside.ui.FloatingPanelManager;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.ui.ModalPopup;
import ru.wexside.ui.NavigationEntry;

public class ClickGuiPanel
   extends MovablePanel
   implements MouseScrollHandler,
   GuiRenderable,
   BoundsProvider,
   MouseButtonHandler,
   CharacterInputHandler,
   LayoutUpdater,
   KeyPressHandler {
   private final NavigationContent navigationContent;
   private final float value;
   private final NavigationState navigationState;
   private final LocalConfigCatalog configCatalog;
   private final ModuleBrowser moduleBrowser;
   private final Table table;
   private final SearchBar searchBar;
   private final ContainerDisplay containerDisplay;
   private final float value2;
   private final TextureResource texture2 = new TextureResource(new ClasspathResource("/assets/wexside/textures/menu/logotypes.png"));
   private final PopupManager popupManager;
   private final SearchQueryState searchQueryState;
   private final float value3;
   private final SidebarToggleButton sidebarToggleButton;
   private final FloatingPanelManager floatingPanelManager;
   private final LayoutModeButton layoutModeButton;
   private final float value4;
   private final ClientMenuContent clientMenuContent;
   private final float value6;
   private final GuiPhotoBanner photoBanner;

   public ClickGuiPanel(
      ModuleManager moduleManager, ContainerDisplaySettings containerDisplaySettings2, LocalConfigCatalog configCatalog, GuiPhotoBanner photoBanner
   ) {
      super(0, 0, 425, 250);
      this.value = 0.5555556F;
      this.value4 = 90.0F;
      this.value2 = 45.0F;
      this.value6 = 5.0F;
      this.value3 = 12.0F;
      this.navigationState = new NavigationState();
      this.searchQueryState = new SearchQueryState();
      this.popupManager = new PopupManager();
      this.floatingPanelManager = new FloatingPanelManager();
      this.popupManager.setParent(this);
      this.floatingPanelManager.setParent(this);
      this.containerDisplay = containerDisplaySettings2.getContainerDisplay();
      this.configCatalog = configCatalog;
      this.photoBanner = photoBanner;
      float f = 6.0F;
      float f2 = 90.0F - f * 2.0F;
      ArrayList<NavigationEntry> arrayList = new ArrayList<>();
      this.clientMenuContent = this.process7(
         new GuiBounds(f, 46.0F, f2, this.getBounds().getHeight() - 46.0F),
         this.navigationState,
         this::process6,
         arrayList,
         this::update5,
         this.navigationState::isActive2
      );
      this.moduleBrowser = new ModuleBrowser(
         new GuiBounds(89.5F, 21.5F, this.getBounds().getWidth() - 89.5F, this.getBounds().getHeight() - 21.5F),
         this.navigationState,
         arrayList,
         moduleManager,
         this.containerDisplay,
         configCatalog,
         this.searchQueryState,
         this::setModule,
         this::process11
      );
      GuiBounds bounds2 = new GuiBounds(98.15F, 7.65F, this.getBounds().getWidth() - 89.5F - 16.0F, 21.5F);
      this.navigationContent = new NavigationContent(bounds2, this.clientMenuContent, this.navigationState);
      this.searchBar = new SearchBar(
         new GuiBounds(96.5F, 7.5F, bounds2.getWidth(), bounds2.getHeight()), this.navigationState, this.searchQueryState, this.moduleBrowser::isActive
      );
      this.sidebarToggleButton = new SidebarToggleButton(new GuiBounds(0.0F, 6.25F, 0.0F, 11.5F), this.moduleBrowser);
      this.layoutModeButton = new LayoutModeButton(new GuiBounds(0.0F, 6.25F, 0.0F, 11.5F), this.containerDisplay, this.moduleBrowser);
      this.table = new Table(new GuiBounds(0.0F, 0.0F, 115.0F, 91.0F), this.containerDisplay);
      this.layoutModeButton.setTable(this.table);
      float f3 = 5.5F;
      float f4 = 8.0F;
      float f5 = 3.0F;
      float f6 = this.layoutModeButton.getFloatType();
      float f7 = this.sidebarToggleButton.getFloatType();
      this.layoutModeButton.getBounds().setPosition(this.getBounds().getWidth() - f4 - f6, f3);
      this.sidebarToggleButton.getBounds().setPosition(this.layoutModeButton.getBounds().getX() - f5 - f7, f3);
      this.addChild(this.clientMenuContent);
      this.addChild(this.navigationContent);
      this.addChild(this.searchBar);
      this.addChild(this.moduleBrowser);
      this.addChild(this.sidebarToggleButton);
      this.addChild(this.layoutModeButton);
      this.popupManager.registerRoot(this.layoutModeButton);
      PopupTreeBinder.bindTree(this.moduleBrowser, this.popupManager);
      this.floatingPanelManager.registerTree(this.moduleBrowser);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
      int n3 = n - (int)this.getBounds().getX();
      int n4 = n2 - (int)this.getBounds().getY();
      if (this.popupManager.getModalPopup() == null) {
         boolean bl = this.popupManager.containsOpenPopup(n3, n4);
         this.popupManager.onMouseScroll(n3, n4, d);
         if (!bl) {
            this.floatingPanelManager.onMouseScroll(n3, n4, d);
            super.onMouseScroll(n3, n4, d);
         }
      }
   }

   @Override
   public void update() {
      for(GuiElement element2 : this.children) {
         element2.update();
      }

      this.floatingPanelManager.update();
      this.popupManager.update();
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      int n4 = n - (int)this.getBounds().getX();
      int n5 = n2 - (int)this.getBounds().getY();
      ModalPopup iiliIIiilI2 = this.popupManager.getModalPopup();
      if (iiliIIiilI2 != null) {
         iiliIIiilI2.onMousePressed(n4, n5, n3);
         return true;
      } else if (this.popupManager.onMousePressed(n4, n5, n3)) {
         return true;
      } else {
         return this.floatingPanelManager.onMousePressed(n4, n5, n3) ? true : super.onMousePressed(n4, n5, n3);
      }
   }

   private float process9(float f, float f2, float f3) {
      return f * (1.0F - f3) + f2 * f3;
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      drawApi.begin();
      ModalPopup iiliIIiilI2 = this.popupManager.getModalPopup();
      if (iiliIIiilI2 != null) {
         this.process12(drawApi, matrix4f, f, iiliIIiilI2);
      } else {
         this.process10(drawApi, matrix4f, f);
         this.popupManager.renderOpenPopups(f, matrix4f);
      }

      drawApi.end();
      return 0.0F;
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      int n4 = n - (int)this.getBounds().getX();
      int n5 = n2 - (int)this.getBounds().getY();
      ModalPopup iiliIIiilI2 = this.popupManager.getModalPopup();
      if (iiliIIiilI2 != null) {
         iiliIIiilI2.onMouseReleased(n4, n5, n3);
      } else {
         this.popupManager.onMouseReleased(n4, n5, n3);
         this.floatingPanelManager.onMouseReleased(n4, n5, n3);
         super.onMouseReleased(n4, n5, n3);
      }
   }

   @Override
   public boolean onCharTyped(char c) {
      ModalPopup iiliIIiilI2 = this.popupManager.getModalPopup();
      if (iiliIIiilI2 != null) {
         iiliIIiilI2.onCharTyped(c);
         return true;
      } else {
         return this.popupManager.onCharTyped(c) || this.floatingPanelManager.onCharTyped(c) || super.onCharTyped(c);
      }
   }

   @Override
   public void update2() {
      this.photoBanner.onGuiClosed();
      this.floatingPanelManager.closeAll();
      this.popupManager.closeAll();
      super.update2();
   }

   @Override
   public boolean onKeyPressed(int n) {
      ModalPopup iiliIIiilI2 = this.popupManager.getModalPopup();
      if (iiliIIiilI2 != null) {
         iiliIIiilI2.onKeyPressed(n);
         return true;
      } else {
         return this.popupManager.onKeyPressed(n) || this.floatingPanelManager.onKeyPressed(n) || super.onKeyPressed(n);
      }
   }

   private void process6(NavigationEntry navigationEntry, boolean bl) {
      if (navigationEntry instanceof ConfigNavigationEntry && this.configCatalog != null) {
         this.configCatalog.refresh();
      }

      if (bl) {
         this.moduleBrowser.update2();
      } else {
         this.floatingPanelManager.closeAll();
      }
   }

   private void setString(String string) {
      if (string != null) {
         this.navigationState.setString(string);
         NavigationEntry navigationEntry = this.clientMenuContent.process10(string);
         if (navigationEntry != null) {
            this.process6(navigationEntry, false);
         } else {
            this.floatingPanelManager.closeAll();
         }
      }
   }

   @Override
   public GuiBounds getVisibleBounds() {
      GuiBounds bounds2 = super.getVisibleBounds();
      GuiBounds bounds3 = this.floatingPanelManager.getBounds();
      if (bounds3 == null) {
         return bounds2;
      } else {
         float f = Math.min(bounds2.getX(), bounds3.getX());
         float f2 = Math.min(bounds2.getY(), bounds3.getY());
         float f3 = Math.max(bounds2.getX() + bounds2.getWidth(), bounds3.getX() + bounds3.getWidth());
         float f4 = Math.max(bounds2.getY() + bounds2.getHeight(), bounds3.getY() + bounds3.getHeight());
         return new GuiBounds(f, f2, f3 - f, f4 - f2);
      }
   }

   private ClientMenuContent process7(
      GuiBounds bounds2,
      NavigationState navigationState,
      NavigationSelectionListener callback24,
      List<NavigationEntry> list,
      Runnable runnable,
      BooleanSupplier booleanSupplier
   ) {
      NavigationSection navigationSection = new NavigationSection("combat", "Combat", new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F));
      NavigationEntry navigationEntry2 = this.process8(list, new ModuleCategoryNavigationEntry("combat.combat", "Combat", "х", ModuleCategory.COMBAT));
      navigationState.setString(navigationEntry2.getString());
      navigationEntry2.setBooleanType(true);
      navigationSection.addEntry(navigationEntry2);
      ClientMenuContent clientMenuContent = new ClientMenuContent(bounds2, navigationState, callback24, runnable, booleanSupplier);
      clientMenuContent.setNavigationSection(navigationSection);
      navigationSection = new NavigationSection("visuals", "Visuals", new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F));
      navigationEntry2 = this.process8(list, new ModuleCategoryNavigationEntry("visuals.render", "Render", "Щ", ModuleCategory.RENDER));
      navigationSection.addEntry(navigationEntry2);
      navigationEntry2 = this.process8(list, new EspNavigationEntry());
      navigationSection.addEntry(navigationEntry2);
      navigationEntry2 = this.process8(list, new ModuleCategoryNavigationEntry("visuals.display", "Display", "j", ModuleCategory.DISPLAY));
      navigationSection.addEntry(navigationEntry2);
      clientMenuContent.setNavigationSection(navigationSection);
      navigationSection = new NavigationSection("player", "Player", new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F));
      navigationEntry2 = this.process8(list, new ModuleCategoryNavigationEntry("player.player", "Player", "А", ModuleCategory.PLAYER));
      navigationSection.addEntry(navigationEntry2);
      navigationEntry2 = this.process8(list, new ModuleCategoryNavigationEntry("player.movement", "Movement", "и", ModuleCategory.MOVEMENT));
      navigationSection.addEntry(navigationEntry2);
      clientMenuContent.setNavigationSection(navigationSection);
      navigationSection = new NavigationSection("settings", "Settings", new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F));
      navigationEntry2 = this.process8(list, new ConfigNavigationEntry("settings.cloud", "Configs", "X", "custom:cloud-configs"));
      navigationSection.addEntry(navigationEntry2);
      navigationEntry2 = this.process8(list, new ModuleCategoryNavigationEntry("settings.misc", "Miscellaneous", "б", ModuleCategory.MISC));
      navigationSection.addEntry(navigationEntry2);
      navigationEntry2 = this.process8(list, new BindingsCenter());
      navigationSection.addEntry(navigationEntry2);
      clientMenuContent.setNavigationSection(navigationSection);
      return clientMenuContent;
   }

   private String getString() {
      return this.clientMenuContent.getString();
   }

   private void setModule(Module module) {
      boolean bl = module.getCategory() == ModuleCategory.HIDDEN;
      String string = bl ? this.clientMenuContent.process5(EspNavigationEntry.class) : this.clientMenuContent.process2(module.getCategory());
      if (string != null) {
         this.setString(string);
         this.navigationState.setString2(null);
         this.searchQueryState.setQuery("");
         if (bl) {
            this.moduleBrowser.setModule(module);
         } else {
            this.moduleBrowser.process4(module.getCategory(), module);
         }

         ModuleTogglePulse.start(module);
      }
   }

   private <T extends NavigationEntry> T process8(List<NavigationEntry> list, T t) {
      list.add(t);
      return t;
   }

   private void process10(GuiDrawApi drawApi, Matrix4f matrix4f, float f) {
      float f2 = this.navigationState.getFloatType();
      float f3 = this.clientMenuContent.getFloatType8();
      float f4 = this.clientMenuContent.getFloatType3();
      float f5 = this.clientMenuContent.getFloatType9();
      float f6 = this.process9(1.0F, 0.5555556F, f2);
      float f7 = FontRegistry.font3.process3("@", 12.0F);
      float f8 = FontRegistry.font3.process4("@", 12.0F);
      float f9 = f5 + f3 / 2.0F;
      float f10 = 5.0F + f8 / 2.0F;
      float f11 = 90.0F * f6;
      float f12 = 45.0F * f6;
      float f13 = f9 - f11 / 2.0F;
      float f14 = f10 - f12 / 2.0F;
      drawApi.drawRoundedRectangle(matrix4f, f5, 0.0F, this.getBounds().getWidth() - f5, this.getBounds().getHeight(), 10.5F, ThemeColors.backgroundPrimary());
      drawApi.beginStencil(1);
      drawApi.drawRoundedRectangleAdvancedUniform(matrix4f, f5, 0.0F, f3, 22.0F, 0.0F, 0.0F, 0.0F, 10.5F, -1.0F, ThemeColors.backgroundSecondary());
      drawApi.applyStencilMask(1);
      drawApi.drawRoundedRectangleAdvancedUniform(matrix4f, f5, 0.0F, f3, 22.0F, 0.0F, 0.0F, 0.0F, 10.5F, -1.0F, ThemeColors.backgroundSecondary());
      int n = drawApi.bindTexture(this.texture2.getTextureId(), this.texture2.getWidth(), this.texture2.getHeight());
      drawApi.drawTintedTexture(matrix4f, f13, f14, f11, f12, n, -1);
      drawApi.endStencil();
      drawApi.drawRoundedRectangleAdvancedUniform(
         matrix4f, f5, 22.0F, f3, this.getBounds().getHeight() - 22.0F, 0.0F, 0.0F, 10.5F, 0.0F, -1.0F, ThemeColors.backgroundSecondary()
      );
      drawApi.drawRoundedRectangleAdvancedUniform(
         matrix4f, f4 + 0.5F, 0.0F, this.getBounds().getWidth() - f4, 22.0F, 0.0F, 10.5F, 0.0F, 0.0F, -1.0F, ThemeColors.backgroundPrimary()
      );
      drawApi.drawRoundedRectangleAdvancedUniform(
         matrix4f,
         f4 + 0.5F,
         22.0F,
         this.getBounds().getWidth() - f4,
         this.getBounds().getHeight() - 22.0F,
         10.5F,
         0.0F,
         0.0F,
         0.0F,
         -1.0F,
         ThemeColors.backgroundPrimary()
      );
      float f15 = f9 - f7 / 2.0F;
      FontRegistry.font3.process5(matrix4f, drawApi, "@", f15, 5.0F, 12.0F, ThemeColors.textPrimary());
      drawApi.fillRectangle(matrix4f, f4, 0.0F, 0.5F, 250.0F, ThemeColors.borderPrimary());
      drawApi.fillRectangle(matrix4f, f5, 21.5F, this.getBounds().getWidth() - f5, 0.5F, ThemeColors.borderPrimary());
      float f16 = 21.5F;
      float f17 = this.getBounds().getHeight() - f16;
      drawApi.beginStencil(1);
      drawApi.drawRoundedRectangleAdvancedUniform(matrix4f, f5, f16, f4 - f5, f17, 0.0F, 0.0F, 10.5F, 0.0F, -1.0F, ThemeColors.backgroundSecondary());
      drawApi.applyStencilMask(1);
      this.clientMenuContent.render(f, matrix4f);
      drawApi.endStencil();
      this.navigationContent.render(f, matrix4f);
      this.searchBar.render(f, matrix4f);
      this.moduleBrowser.render(f, matrix4f);
      this.sidebarToggleButton.render(f, matrix4f);
      this.layoutModeButton.render(f, matrix4f);
      this.floatingPanelManager.render(f, matrix4f);
   }

   private String process11(Module module) {
      String string2 = module.getCategory() == ModuleCategory.HIDDEN
         ? this.clientMenuContent.process5(EspNavigationEntry.class)
         : this.clientMenuContent.process2(module.getCategory());
      if (string2 == null) {
         return module.getDisplayName().toUpperCase(Locale.ROOT);
      } else {
         NavigationEntry navigationEntry = this.clientMenuContent.process10(string2);
         NavigationSection navigationSection = this.clientMenuContent.process4(string2);
         String string4 = navigationSection == null ? "" : navigationSection.getString2();
         String string5 = navigationEntry == null ? module.getCategory().getName() : navigationEntry.getDisplayName();
         String string6 = module.getDisplayName();
         return (string4 + " / " + string5 + " / " + string6).toUpperCase(Locale.ROOT);
      }
   }

   @Override
   public void update4() {
      this.setBooleanType3(true);
   }

   private void process12(GuiDrawApi drawApi, Matrix4f matrix4f, float f, ModalPopup iiliIIiilI2) {
      float f2 = this.getBounds().getWidth();
      float f3 = this.getBounds().getHeight();
      PreparedLayer preparedLayer = drawApi.prepareDedicatedLayer(matrix4f, 0.0F, 0.0F, f2, f3, 0.0F);
      Vector4f vector4f = matrix4f.transform(new Vector4f(0.0F, f3, 0.0F, 1.0F));
      float f4 = drawApi.getLayerOffsetX() + vector4f.x;
      float f5 = drawApi.getLayerOffsetY() + ((float)drawApi.getFramebufferHeight() - vector4f.y);
      drawApi.beginLayerFrame(preparedLayer.getTexture(), f4, f5);
      Matrix4f matrix4f2 = new Matrix4f(preparedLayer.getContentMatrix()).translate(preparedLayer.contentX(), preparedLayer.contentY(), 0.0F);
      this.process10(drawApi, matrix4f2, f);

      this.popupManager.renderOpenPopups(f, matrix4f2);
      drawApi.endLayerFrame();
      iiliIIiilI2.render(f, matrix4f);
   }

   @Override
   public void update3() {
      this.photoBanner.onGuiOpened();
      if (this.configCatalog != null) {
         this.configCatalog.refresh();
      }
   }

   private void update5() {
      if (!this.navigationState.isActive2()) {
         this.navigationState.setString2(this.navigationState.string4());
         this.navigationState.setString("__search__");
         this.floatingPanelManager.closeAll();
         this.searchBar.update4();
      } else {
         String string = this.navigationState.getString();
         if (string == null || string.isBlank() || string.equals("__search__")) {
            string = this.getString();
         }

         this.searchBar.update3();
         this.setString(string);
         this.navigationState.setString2(null);
      }
   }

   public ModuleBrowser getModuleBrowser() {
      return this.moduleBrowser;
   }

   public PopupManager getPopupManager() {
      return this.popupManager;
   }
}
