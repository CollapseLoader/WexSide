package ru.wexside.ui;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.PotionCatalogEntry;
import ru.wexside.misc.PotionPresetDraft;
import ru.wexside.misc.ThemeColors;
import ru.wexside.render.BakedItemIcon;
import ru.wexside.render.ItemIconRenderer;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.PotionPresetController;

public final class PotionPresetRow extends GuiElement {
   private final PotionPresetDraft preset;
   private final PotionPresetController potionService;
   private final ItemIconRenderer icons;
   private final Runnable onSelected;
   private boolean selected;

   public PotionPresetRow(PotionPresetDraft preset, PotionPresetController potionService, ItemIconRenderer icons, float width, Runnable onSelected) {
      super(new GuiBounds(0.0F, 0.0F, width, 12.0F));
      this.preset = preset;
      this.potionService = potionService;
      this.icons = icons;
      this.onSelected = onSelected;
   }

   public PotionPresetDraft getPreset() {
      return this.preset;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   @Override
   public boolean onMousePressed(int mouseX, int mouseY, int button) {
      if (button == 0 && this.getBounds().contains((float)mouseX, (float)mouseY)) {
         this.onSelected.run();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public float render(float delta, Matrix4f matrix) {
      GuiDrawApi renderer = WexSideClient.getGuiRenderer();
      GuiBounds bounds = this.getBounds();
      renderer.drawRoundedRectangle(
         matrix,
         bounds.getX(),
         bounds.getY(),
         bounds.getWidth(),
         bounds.getHeight(),
         4.0F,
         this.selected ? ThemeColors.formatFieldFill() : ThemeColors.borderPrimary()
      );
      float iconX = bounds.getX() + 2.0F;

      for(PotionCatalogEntry potion : this.preset.getPotions()) {
         BakedItemIcon icon = this.icons.process(this.potionService.resolveStack(potion));
         if (icon != null) {
            this.icons.process2(renderer, matrix, icon, iconX, bounds.getY() + 2.0F, 8.0F, -1);
            iconX += 9.0F;
         }
      }

      FontRegistry.font2
         .process2(matrix, renderer, this.preset.getName(), Math.max(bounds.getX() + 3.0F, iconX + 2.0F), bounds.getY() + 3.0F, 6.0F, ThemeColors.textPrimary());
      return bounds.getY() + bounds.getHeight();
   }
}
