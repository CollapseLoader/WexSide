package ru.wexside.ui;

import java.util.function.Consumer;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.PotionCatalogEntry;
import ru.wexside.misc.ThemeColors;
import ru.wexside.render.BakedItemIcon;
import ru.wexside.render.ItemIconRenderer;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.PotionPresetController;

public final class PotionOptionRow extends GuiElement {
   private final PotionCatalogEntry potion;
   private final PotionPresetController potionService;
   private final ItemIconRenderer icons;
   private final Consumer<PotionCatalogEntry> onSelected;
   private BakedItemIcon icon;

   public PotionOptionRow(
      PotionCatalogEntry potion, PotionPresetController potionService, ItemIconRenderer icons, float width, Consumer<PotionCatalogEntry> onSelected
   ) {
      super(new GuiBounds(0.0F, 0.0F, width, 14.0F));
      this.potion = potion;
      this.potionService = potionService;
      this.icons = icons;
      this.onSelected = onSelected;
   }

   @Override
   public boolean onMousePressed(int mouseX, int mouseY, int button) {
      if (button == 0 && this.getBounds().contains((float)mouseX, (float)mouseY)) {
         this.onSelected.accept(this.potion);
         return true;
      } else {
         return false;
      }
   }

   public void prepareIcon() {
      this.icon = this.icons.process(this.potionService.resolveStack(this.potion));
   }

   @Override
   public float render(float delta, Matrix4f matrix) {
      this.prepareIcon();
      GuiDrawApi renderer = WexSideClient.getGuiRenderer();
      GuiBounds bounds = this.getBounds();
      int background = this.potionService.isAvailable(this.potion) ? ThemeColors.formatFieldFill() : ThemeColors.borderPrimary();
      renderer.drawRoundedRectangle(matrix, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 4.0F, background);
      if (this.icon != null) {
         this.icons.process2(renderer, matrix, this.icon, bounds.getX() + 2.0F, bounds.getY() + 2.0F, 10.0F, -1);
      }

      FontRegistry.font2.process2(matrix, renderer, this.potion.getDisplayName(), bounds.getX() + 15.0F, bounds.getY() + 4.0F, 6.0F, ThemeColors.textPrimary());
      return bounds.getY() + bounds.getHeight();
   }
}
