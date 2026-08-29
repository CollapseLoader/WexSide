package ru.wexside.config;

import java.io.IOException;
import java.util.List;
import net.minecraft.class_310;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.ConfigManager;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.GuiInteractionState;
import ru.wexside.misc.TextureResource;
import ru.wexside.misc.ThemeColors;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class ConfigCatalogView extends GuiElement {
   private final String title;
   private final LocalConfigCatalog catalog;
   private final StringBuilder profileName = new StringBuilder();
   private float scrollOffset;
   private float targetScrollOffset;
   private int selectedTab;
   private boolean createDialogOpen;
   private boolean serverSpecific;
   private String status = "";
   private long statusExpiresAt;

   public ConfigCatalogView(GuiBounds bounds, String title, LocalConfigCatalog catalog) {
      super(bounds);
      this.title = title;
      this.catalog = catalog;
      this.refresh();
   }

   @Override
   public boolean onMousePressed(int mouseX, int mouseY, int button) {
      if (!this.getBounds().contains((float)mouseX, (float)mouseY)) {
         return false;
      } else if (this.createDialogOpen) {
         return this.handleCreateDialogClick(mouseX, mouseY, button);
      } else if (button == 0 && this.tabBounds(0).contains((float)mouseX, (float)mouseY)) {
         this.selectedTab = 0;
         this.targetScrollOffset = 0.0F;
         return true;
      } else if (button == 0 && this.tabBounds(1).contains((float)mouseX, (float)mouseY)) {
         this.selectedTab = 1;
         this.targetScrollOffset = 0.0F;
         return true;
      } else {
         float toolbarY = this.getBounds().getY();
         if (button == 0 && this.contains((float)mouseX, (float)mouseY, this.toolbarButtonX(3), toolbarY, 15.0F, 15.0F)) {
            this.saveActiveProfile();
            return true;
         } else if (button == 0 && this.contains((float)mouseX, (float)mouseY, this.toolbarButtonX(2), toolbarY, 15.0F, 15.0F)) {
            this.refresh();
            this.showStatus("Список обновлён");
            return true;
         } else if (button == 0 && this.contains((float)mouseX, (float)mouseY, this.toolbarButtonX(1), toolbarY, 15.0F, 15.0F)) {
            this.openCreateDialog();
            return true;
         } else if (button == 0 && this.contains((float)mouseX, (float)mouseY, this.toolbarButtonX(0), toolbarY, 15.0F, 15.0F)) {
            this.openFolder();
            return true;
         } else {
            int index = this.rowIndexAt(mouseX, mouseY);
            List<LocalConfigEntry> entries = this.visibleEntries();
            if (index >= 0 && index < entries.size()) {
               LocalConfigEntry entry = entries.get(index);
               if (button == 0 && this.menuButtonBounds(this.rowBounds(index)).contains((float)mouseX, (float)mouseY)) {
                  this.save(entry);
                  return true;
               } else if (button == 1) {
                  this.delete(entry);
                  return true;
               } else {
                  if (button == 0) {
                     this.load(entry);
                  }

                  return true;
               }
            } else {
               return true;
            }
         }
      }
   }

   @Override
   public void onMouseScroll(int mouseX, int mouseY, double amount) {
      if (this.getBounds().contains((float)mouseX, (float)mouseY) && !this.createDialogOpen) {
         this.targetScrollOffset = Math.clamp(this.targetScrollOffset - (float)amount * 18.0F, 0.0F, this.maximumScrollOffset());
      }
   }

   @Override
   public boolean onCharTyped(char character) {
      if (this.createDialogOpen && !Character.isISOControl(character) && this.profileName.length() < 32) {
         if ("\\/:*?\"<>|".indexOf(character) < 0) {
            this.profileName.append(character);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean onKeyPressed(int keyCode) {
      if (!this.createDialogOpen) {
         return false;
      } else if (keyCode == 256) {
         this.createDialogOpen = false;
         return true;
      } else if (keyCode == 259 && !this.profileName.isEmpty()) {
         this.profileName.deleteCharAt(this.profileName.length() - 1);
         return true;
      } else if (keyCode != 257 && keyCode != 335) {
         return false;
      } else {
         this.createProfile();
         return true;
      }
   }

   @Override
   public float render(float delta, Matrix4f matrix) {
      this.scrollOffset = FrameInterpolator.lerpTowards(this.scrollOffset, this.targetScrollOffset, 30.0F);
      GuiDrawApi renderer = WexSideClient.getGuiRenderer();
      this.renderTabs(renderer, matrix);
      this.renderToolbar(renderer, matrix);
      this.renderRows(renderer, matrix);
      if (this.createDialogOpen) {
         this.renderCreateDialog(renderer, matrix);
      }

      return this.getBounds().getY() + this.getBounds().getHeight();
   }

   private void renderTabs(GuiDrawApi renderer, Matrix4f matrix) {
      this.renderTab(renderer, matrix, 0, "Все");
      this.renderTab(renderer, matrix, 1, "Локальные");
   }

   private void renderTab(GuiDrawApi renderer, Matrix4f matrix, int index, String label) {
      GuiBounds tab = this.tabBounds(index);
      boolean active = this.selectedTab == index;
      renderer.drawRoundedRectangle(
         matrix, tab.getX(), tab.getY(), tab.getWidth(), tab.getHeight(), 6.0F, active ? ThemeColors.backgroundControl() : ThemeColors.backgroundSecondary()
      );
      renderer.drawRoundedOutline(
         matrix, tab.getX(), tab.getY(), tab.getWidth(), tab.getHeight(), 6.0F, 0.75F, active ? ThemeColors.accent() : ThemeColors.borderPrimary()
      );
      float textWidth = FontRegistry.font4.process3(label, 6.5F);
      float textHeight = FontRegistry.font4.process4(label, 6.5F);
      FontRegistry.font4
         .process2(
            matrix,
            renderer,
            label,
            tab.getX() + (tab.getWidth() - textWidth) * 0.5F,
            tab.getY() + (tab.getHeight() - textHeight) * 0.5F,
            6.5F,
            active ? ThemeColors.accent() : ThemeColors.textSecondary()
         );
   }

   private void renderToolbar(GuiDrawApi renderer, Matrix4f matrix) {
      float y = this.getBounds().getY();
      this.renderToolbarButton(renderer, matrix, this.toolbarButtonX(3), y, "S");
      this.renderToolbarButton(renderer, matrix, this.toolbarButtonX(2), y, "е");
      this.renderToolbarButton(renderer, matrix, this.toolbarButtonX(1), y, "Я");
      this.renderToolbarButton(renderer, matrix, this.toolbarButtonX(0), y, "x");
      FontRegistry.font2
         .process2(matrix, renderer, this.statusText(), this.getBounds().getX() + 8.0F, this.getBounds().getY() + 17.0F, 5.0F, ThemeColors.textMuted());
   }

   private void renderToolbarButton(GuiDrawApi renderer, Matrix4f matrix, float x, float y, String icon) {
      boolean hovered = this.hovered(x, y, 15.0F, 15.0F);
      renderer.drawRoundedRectangle(matrix, x, y, 15.0F, 15.0F, 5.0F, hovered ? ThemeColors.backgroundHover() : ThemeColors.backgroundControl());
      renderer.drawRoundedOutline(matrix, x, y, 15.0F, 15.0F, 5.0F, 0.75F, ThemeColors.borderPrimary());
      float iconWidth = FontRegistry.font3.process3(icon, 6.0F);
      float iconHeight = FontRegistry.font3.process4(icon, 6.0F);
      FontRegistry.font3
         .process5(
            matrix,
            renderer,
            icon,
            x + (15.0F - iconWidth) * 0.5F,
            y + (15.0F - iconHeight) * 0.5F,
            6.0F,
            hovered ? ThemeColors.accent() : ThemeColors.textSecondary()
         );
   }

   private void renderRows(GuiDrawApi renderer, Matrix4f matrix) {
      float contentY = this.getBounds().getY() + 24.0F;
      float contentHeight = Math.max(0.0F, this.getBounds().getHeight() - 24.0F);
      renderer.pushScissor(matrix, this.getBounds().getX(), contentY, this.getBounds().getWidth(), contentHeight);
      List<LocalConfigEntry> entries = this.visibleEntries();

      for(int index = 0; index < entries.size(); ++index) {
         GuiBounds row = this.rowBounds(index);
         if (row.getY() + row.getHeight() >= contentY && row.getY() <= contentY + contentHeight) {
            this.renderRow(renderer, matrix, row, entries.get(index));
         }
      }

      renderer.popScissor();
   }

   private void renderRow(GuiDrawApi renderer, Matrix4f matrix, GuiBounds row, LocalConfigEntry entry) {
      boolean active = this.isActive(entry);
      boolean hovered = this.hovered(row.getX(), row.getY(), row.getWidth(), row.getHeight());
      renderer.drawRoundedRectangle(
         matrix,
         row.getX(),
         row.getY(),
         row.getWidth(),
         row.getHeight(),
         11.0F,
         hovered ? ThemeColors.backgroundHover() : ColorUtils.withAlpha(ThemeColors.backgroundSecondary(), 0.0F)
      );
      renderer.drawRoundedOutline(
         matrix, row.getX(), row.getY(), row.getWidth(), row.getHeight(), 11.0F, 0.75F, active ? ThemeColors.accent() : ThemeColors.backgroundControl()
      );
      this.renderAvatar(renderer, matrix, row, entry.avatar());
      float textX = row.getX() + 28.0F;
      FontRegistry.font4.process2(matrix, renderer, entry.name(), textX, row.getY() + 6.0F, 6.5F, ThemeColors.textPrimary());
      FontRegistry.font2.process2(matrix, renderer, "Локальный  •  " + entry.updatedAt(), textX, row.getY() + 16.5F, 5.5F, ThemeColors.textMuted());
      this.renderStateButton(renderer, matrix, row, active);
      this.renderSaveButton(renderer, matrix, row, active);
   }

   private void renderAvatar(GuiDrawApi renderer, Matrix4f matrix, GuiBounds row, TextureResource avatar) {
      float x = row.getX() + 6.0F;
      float y = row.getY() + 6.0F;
      if (avatar != null) {
         int texture = renderer.bindTexture(avatar.getTextureId(), avatar.getWidth(), avatar.getHeight());
         renderer.drawRoundedTextureTinted(matrix, x, y, 18.0F, 18.0F, 12.0F, texture, -1);
      } else {
         renderer.drawRoundedRectangle(matrix, x, y, 18.0F, 18.0F, 12.0F, ThemeColors.backgroundControl());
         renderer.drawRoundedOutline(matrix, x, y, 18.0F, 18.0F, 12.0F, 0.75F, ThemeColors.borderPrimary());
         float glyphWidth = FontRegistry.font3.process3("@", 9.0F);
         float glyphHeight = FontRegistry.font3.process4("@", 9.0F);
         FontRegistry.font3
            .process5(matrix, renderer, "@", x + (18.0F - glyphWidth) * 0.5F, y + (18.0F - glyphHeight) * 0.5F, 9.0F, ThemeColors.textSecondary());
      }
   }

   private void renderStateButton(GuiDrawApi renderer, Matrix4f matrix, GuiBounds row, boolean active) {
      float x = row.getX() + row.getWidth() - 42.0F;
      float y = row.getY() + 7.5F;
      renderer.drawRoundedRectangle(matrix, x, y, 15.0F, 15.0F, 9.0F, active ? ThemeColors.accentTint() : ThemeColors.backgroundControl());
      String glyph = active ? "ON" : ">";
      float width = FontRegistry.font6.process3(glyph, 5.0F);
      float height = FontRegistry.font6.process4(glyph, 5.0F);
      FontRegistry.font6
         .process2(
            matrix, renderer, glyph, x + (15.0F - width) * 0.5F, y + (15.0F - height) * 0.5F, 5.0F, active ? ThemeColors.accent() : ThemeColors.textSecondary()
         );
   }

   private void renderSaveButton(GuiDrawApi renderer, Matrix4f matrix, GuiBounds row, boolean active) {
      GuiBounds button = this.menuButtonBounds(row);
      int background = active ? ThemeColors.accentTint() : ThemeColors.backgroundControl();
      renderer.drawRoundedRectangle(matrix, button.getX(), button.getY(), button.getWidth(), button.getHeight(), 9.0F, background);
      float width = FontRegistry.font6.process3("S", 5.0F);
      float height = FontRegistry.font6.process4("S", 5.0F);
      FontRegistry.font6
         .process2(
            matrix,
            renderer,
            "S",
            button.getX() + (button.getWidth() - width) * 0.5F,
            button.getY() + (button.getHeight() - height) * 0.5F,
            5.0F,
            active ? ThemeColors.accent() : ThemeColors.textSecondary()
         );
   }

   private void renderCreateDialog(GuiDrawApi renderer, Matrix4f matrix) {
      GuiBounds dialog = this.createDialogBounds();
      renderer.drawRoundedRectangle(
         matrix, this.getBounds().getX(), this.getBounds().getY(), this.getBounds().getWidth(), this.getBounds().getHeight(), 10.5F, ThemeColors.modalScrim()
      );
      renderer.drawRoundedRectangle(matrix, dialog.getX(), dialog.getY(), dialog.getWidth(), dialog.getHeight(), 10.5F, ThemeColors.backgroundPrimary());
      renderer.drawRoundedOutline(matrix, dialog.getX(), dialog.getY(), dialog.getWidth(), dialog.getHeight(), 10.5F, 0.75F, ThemeColors.borderStrong());
      FontRegistry.font7.process2(matrix, renderer, "Создать конфигурацию", dialog.getX() + 8.0F, dialog.getY() + 8.0F, 8.0F, ThemeColors.textPrimary());
      GuiBounds input = this.createInputBounds();
      renderer.drawRoundedRectangle(matrix, input.getX(), input.getY(), input.getWidth(), input.getHeight(), 6.0F, ThemeColors.formatFieldFill());
      renderer.drawRoundedOutline(matrix, input.getX(), input.getY(), input.getWidth(), input.getHeight(), 6.0F, 0.75F, ThemeColors.borderPrimary());
      String inputText = this.profileName.isEmpty() ? "Название" : this.profileName.toString();
      FontRegistry.font4
         .process2(
            matrix,
            renderer,
            inputText,
            input.getX() + 6.0F,
            input.getY() + 5.0F,
            6.5F,
            this.profileName.isEmpty() ? ThemeColors.textPlaceholder() : ThemeColors.textPrimary()
         );
      GuiBounds serverToggle = this.serverToggleBounds();
      renderer.drawRoundedRectangle(
         matrix,
         serverToggle.getX(),
         serverToggle.getY(),
         serverToggle.getWidth(),
         serverToggle.getHeight(),
         6.0F,
         this.serverSpecific ? ThemeColors.accentTint() : ThemeColors.backgroundControl()
      );
      FontRegistry.font4
         .process2(
            matrix,
            renderer,
            this.serverSpecific ? "Для текущего сервера" : "Общий конфиг",
            serverToggle.getX() + 6.0F,
            serverToggle.getY() + 4.0F,
            6.0F,
            this.serverSpecific ? ThemeColors.accent() : ThemeColors.textSecondary()
         );
      GuiBounds create = this.createButtonBounds();
      renderer.drawRoundedRectangle(
         matrix,
         create.getX(),
         create.getY(),
         create.getWidth(),
         create.getHeight(),
         8.0F,
         this.profileName.isEmpty() ? ThemeColors.backgroundControl() : ThemeColors.accentTint()
      );
      FontRegistry.font4
         .process2(
            matrix,
            renderer,
            "ENTER   Создать конфигурацию",
            create.getX() + 8.0F,
            create.getY() + 9.0F,
            6.0F,
            this.profileName.isEmpty() ? ThemeColors.textDisabled() : ThemeColors.accent()
         );
   }

   private boolean handleCreateDialogClick(int mouseX, int mouseY, int button) {
      if (button != 0) {
         return true;
      } else if (this.serverToggleBounds().contains((float)mouseX, (float)mouseY)) {
         this.serverSpecific = !this.serverSpecific;
         return true;
      } else if (this.createButtonBounds().contains((float)mouseX, (float)mouseY)) {
         this.createProfile();
         return true;
      } else {
         if (!this.createDialogBounds().contains((float)mouseX, (float)mouseY)) {
            this.createDialogOpen = false;
         }

         return true;
      }
   }

   private void openCreateDialog() {
      this.profileName.setLength(0);
      this.serverSpecific = false;
      this.createDialogOpen = true;
   }

   private void createProfile() {
      String name = this.profileName.toString().trim();
      if (!name.isBlank() && name.length() <= 32) {
         ConfigManager manager = WexSideClient.getConfigManager();
         if (manager != null && !manager.profileExists(name)) {
            try {
               manager.saveProfileWithDisplayName(name, this.serverSpecific ? this.currentServer() : "Общий");
               this.createDialogOpen = false;
               this.refresh();
               this.showStatus("Конфиг создан: " + name);
            } catch (IOException var4) {
               this.showStatus("Не удалось создать конфиг");
            }
         } else {
            this.showStatus("Конфиг с таким именем уже существует");
         }
      } else {
         this.showStatus("Название должно быть от 1 до 32 символов");
      }
   }

   private void load(LocalConfigEntry entry) {
      ConfigManager manager = WexSideClient.getConfigManager();
      if (manager != null) {
         try {
            manager.loadProfile(entry.name());
            this.showStatus("Загружен: " + entry.name());
         } catch (RuntimeException var4) {
            this.showStatus("Не удалось загрузить конфиг");
         }
      }
   }

   private void save(LocalConfigEntry entry) {
      this.save(entry.name());
   }

   private void save(String profileName) {
      ConfigManager manager = WexSideClient.getConfigManager();
      if (manager != null) {
         try {
            manager.saveProfile(profileName);
            this.refresh();
            this.showStatus("Сохранён: " + profileName);
         } catch (RuntimeException | IOException var4) {
            this.showStatus("Не удалось сохранить конфиг");
         }
      }
   }

   private void saveActiveProfile() {
      ConfigManager manager = WexSideClient.getConfigManager();
      String profileName = manager == null ? null : manager.getCurrentProfileName();
      if (profileName != null && !profileName.isBlank()) {
         this.save(profileName);
      } else {
         this.showStatus("Сначала создайте или загрузите конфиг");
      }
   }

   private void delete(LocalConfigEntry entry) {
      if (this.isActive(entry)) {
         this.showStatus("Нельзя удалить активный конфиг");
      } else {
         if (this.catalog.delete(entry)) {
            this.showStatus("Конфиг удалён: " + entry.name());
         } else {
            this.showStatus("Не удалось удалить конфиг");
         }
      }
   }

   private void openFolder() {
      ConfigManager manager = WexSideClient.getConfigManager();
      if (manager != null) {
         try {
            manager.openConfigFolder();
         } catch (IOException var3) {
            this.showStatus("Не удалось открыть папку конфигов");
         }
      }
   }

   private void refresh() {
      if (this.catalog != null) {
         this.catalog.refresh();
      }

      this.targetScrollOffset = Math.min(this.targetScrollOffset, this.maximumScrollOffset());
   }

   private List<LocalConfigEntry> visibleEntries() {
      return this.catalog == null ? List.of() : this.catalog.entries();
   }

   private GuiBounds tabBounds(int index) {
      return new GuiBounds(this.getBounds().getX() + (float)index * 87.5F, this.getBounds().getY(), 87.5F, 12.0F);
   }

   private GuiBounds rowBounds(int index) {
      return new GuiBounds(
         this.getBounds().getX() + 8.0F, this.getBounds().getY() + 30.0F + (float)index * 34.0F - this.scrollOffset, this.getBounds().getWidth() - 16.0F, 30.0F
      );
   }

   private GuiBounds menuButtonBounds(GuiBounds row) {
      return new GuiBounds(row.getX() + row.getWidth() - 22.0F, row.getY() + 7.5F, 15.0F, 15.0F);
   }

   private int rowIndexAt(int mouseX, int mouseY) {
      List<LocalConfigEntry> entries = this.visibleEntries();

      for(int index = 0; index < entries.size(); ++index) {
         if (this.rowBounds(index).contains((float)mouseX, (float)mouseY)) {
            return index;
         }
      }

      return -1;
   }

   private float maximumScrollOffset() {
      return Math.max(0.0F, (float)this.visibleEntries().size() * 34.0F - Math.max(0.0F, this.getBounds().getHeight() - 30.0F));
   }

   private float toolbarButtonX(int positionFromRight) {
      return this.getBounds().getX() + this.getBounds().getWidth() - 23.0F - (float)positionFromRight * 19.0F;
   }

   private GuiBounds createDialogBounds() {
      return new GuiBounds(
         this.getBounds().getX() + (this.getBounds().getWidth() - 175.0F) * 0.5F,
         this.getBounds().getY() + (this.getBounds().getHeight() - 112.0F) * 0.5F,
         175.0F,
         112.0F
      );
   }

   private GuiBounds createInputBounds() {
      GuiBounds dialog = this.createDialogBounds();
      return new GuiBounds(dialog.getX() + 8.0F, dialog.getY() + 29.0F, 159.0F, 18.0F);
   }

   private GuiBounds serverToggleBounds() {
      GuiBounds dialog = this.createDialogBounds();
      return new GuiBounds(dialog.getX() + 8.0F, dialog.getY() + 53.0F, 159.0F, 16.0F);
   }

   private GuiBounds createButtonBounds() {
      GuiBounds dialog = this.createDialogBounds();
      return new GuiBounds(dialog.getX() + 8.0F, dialog.getY() + 77.0F, 159.0F, 27.0F);
   }

   private boolean isActive(LocalConfigEntry entry) {
      ConfigManager manager = WexSideClient.getConfigManager();
      return manager != null && entry.name().equals(manager.getCurrentProfileName());
   }

   private String currentServer() {
      class_310 client = class_310.method_1551();
      return client.method_1558() == null ? "Одиночная игра" : client.method_1558().field_3761;
   }

   private boolean hovered(float x, float y, float width, float height) {
      GuiInteractionState interaction = GuiInteractionState.getInstance();
      float mouseX = (float)interaction.getScaledMouseX() - interaction.getRootPanel().getBounds().getX();
      float mouseY = (float)interaction.getScaledMouseY() - interaction.getRootPanel().getBounds().getY();
      return this.contains(mouseX, mouseY, x, y, width, height);
   }

   private boolean contains(float mouseX, float mouseY, float x, float y, float width, float height) {
      return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
   }

   private String statusText() {
      return !this.status.isBlank() && System.currentTimeMillis() < this.statusExpiresAt ? this.status : this.title;
   }

   private void showStatus(String message) {
      this.status = message;
      this.statusExpiresAt = System.currentTimeMillis() + 3000L;
   }
}
