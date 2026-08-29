package ru.wexside.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import org.joml.Matrix4f;
import ru.wexside.render.ItemIconRenderer;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.PopupPanel;
import ru.wexside.ui.PotionOptionRow;
import ru.wexside.util.ClippedContentRenderer;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.PotionPresetController;
import ru.wexside.util.ScrollController;
import ru.wexside.util.Scrollbar;

public final class PotionSelectorPopup
   extends PopupPanel
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final float value;
   private String string2;
   private final PotionEditorState potionEditorState;
   private final ClippedContentRenderer clippedContentRenderer = new ClippedContentRenderer(0.0F, 14.0F, 14.0F, false);
   private final float value2;
   private final Consumer<PotionCatalogEntry> consumer;
   public static final float value3 = 150.0F;
   private final float value4;
   private final PotionSearchField potionSearchField;
   private final String string3;
   private final String string4;
   private final float value5;
   private final ItemIconRenderer itemIconRenderer;
   private final float value6;
   private final float value7;
   private final String string5;
   private final float value8;
   private final float value9;
   private final String string6;
   static final float value10 = 1.0F;
   public static final float value11 = 115.5F;
   private final PotionPresetController potionPresetController2;
   private final float value12;
   static final float value13 = 106.0F;
   private final float value14;
   private final ScrollController scrollController = new ScrollController(18.0F, 30.0F);
   private final float value15;
   private final Scrollbar scrollbar = new Scrollbar();
   private final List<PotionOptionRow> optionRows = new ArrayList<>();
   private final float value16;

   public PotionSelectorPopup(
      PotionPresetController potionPresetController2,
      PotionEditorState potionEditorState,
      ItemIconRenderer itemIconRenderer,
      Consumer<PotionCatalogEntry> consumer
   ) {
      super(new GuiBounds(0.0F, 0.0F, 115.5F, 150.0F));
      this.value2 = 5.0F;
      this.value15 = 3.0F;
      this.value5 = 44.0F;
      this.value14 = 5.5F;
      this.value = 6.0F;
      this.value8 = 1.5F;
      this.value12 = 12.5F;
      this.value6 = 7.0F;
      this.value4 = 26.0F;
      this.value16 = 2.0F;
      this.value9 = 3.5F;
      this.value7 = 6.0F;
      this.string5 = "Ваши зелья";
      this.string4 = "Текущие зелья";
      this.string3 = "u";
      this.string6 = "Ничего не найдено";
      this.potionPresetController2 = potionPresetController2;
      this.potionEditorState = potionEditorState;
      this.itemIconRenderer = itemIconRenderer;
      this.consumer = consumer;
      this.potionSearchField = new PotionSearchField(potionEditorState);
      this.addChild(this.potionSearchField);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
      if (this.isActive2() && this.getBounds().contains((float)n, (float)n2)) {
         this.scrollController.scrollByWheel(d, this.getFloatType3());
      }
   }

   @Override
   public void update() {
      this.potionSearchField.update();
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (this.isActive2() && this.getBounds().contains((float)n, (float)n2)) {
         int n5 = (int)((float)n - this.getBounds().getX());
         int n4;
         if (this.potionSearchField.onMousePressed(n5, n4 = (int)((float)n2 - this.getBounds().getY()), n3)) {
            return true;
         } else if (this.scrollbar.onMousePressed(n5, n4, n3)) {
            return true;
         } else {
            if ((float)n4 >= 44.0F && (float)n4 <= 44.0F + this.getFloatType3()) {
               for(PotionOptionRow row : this.optionRows) {
                  if (row.onMousePressed(n5, n4, n3)) {
                     return true;
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      this.potionSearchField.onMouseReleased((int)((float)n - this.getBounds().getX()), (int)((float)n2 - this.getBounds().getY()), n3);
   }

   @Override
   public boolean onCharTyped(char c) {
      return this.potionSearchField.onCharTyped(c);
   }

   @Override
   public void update2() {
      this.potionSearchField.update2();
      super.update2();
   }

   @Override
   public boolean onKeyPressed(int n) {
      return this.potionSearchField.onKeyPressed(n);
   }

   public void update3() {
      this.string2 = this.potionEditorState.getSearchQuery();
      this.optionRows.clear();
      this.optionRows.clear();
      this.addChild(this.potionSearchField);
      String string = this.string2 == null ? "" : this.string2.trim().toLowerCase(Locale.ROOT);

      for(PotionCatalogEntry potionCatalogEntry : this.potionPresetController2.getCatalogSortedByAvailability()) {
         if (string.isEmpty() || potionCatalogEntry.getDisplayName().toLowerCase(Locale.ROOT).contains(string)) {
            PotionOptionRow row = new PotionOptionRow(potionCatalogEntry, this.potionPresetController2, this.itemIconRenderer, 106.0F, this.consumer);
            this.optionRows.add(row);
            this.addChild(row);
         }
      }

      this.scrollController.scrollTo(0.0F, this.getFloatType3());
   }

   public void update4() {
      if (this.isActive()) {
         float f = this.getFloatType3();
         float f2 = 44.0F + this.scrollController.getOffset();

         for(PotionOptionRow row : this.optionRows) {
            if (f2 + 14.0F >= 44.0F && f2 <= 44.0F + f) {
               row.prepareIcon();
            }

            f2 += 15.0F;
         }
      }
   }

   @Override
   public void setBounds(GuiBounds bounds2) {
      this.getBounds().setPosition(bounds2.getX() + bounds2.getWidth() + 2.0F, bounds2.getY());
   }

   private float getFloatType() {
      return this.getLastMouseY() - this.getBounds().getY();
   }

   private float getFloatType2() {
      return this.getLastMouseX() - this.getBounds().getX();
   }

   private float getFloatType3() {
      return 101.0F;
   }

   protected float getFloatType4() {
      return 5.0F;
   }

   @Override
   protected void updateLayout() {
      this.potionSearchField.getBounds().setPosition(5.0F, 26.0F);
      if (this.string2 == null || !this.string2.equals(this.potionEditorState.getSearchQuery())) {
         this.update3();
      }
   }

   @Override
   protected void renderPopup(float f, Matrix4f matrix4f2, GuiDrawApi drawApi) {
      drawApi.drawRoundedRectangleOutlined(
         matrix4f2, 0.0F, 0.0F, 115.5F, 150.0F, this.getFloatType4(), 1.0F, ColorUtils.withAlpha(-1, 0.0F), ThemeColors.borderPrimary()
      );
      int n = ThemeColors.accent();
      FontRegistry.font4.process2(matrix4f2, drawApi, "Ваши зелья", 5.0F, 5.0F, 5.5F, n);
      float f2 = 5.0F + FontRegistry.font4.process3("Ваши зелья", 5.5F) + 1.5F;
      float f3 = 5.0F + FontRegistry.font4.process4("Ваши зелья", 5.5F) / 2.0F;
      FontRegistry.font3.process5(matrix4f2, drawApi, "u", f2, f3 - FontRegistry.font3.process14("u".charAt(0), 0.0F, 6.0F), 6.0F, n);
      FontRegistry.font4.process2(matrix4f2, drawApi, "Текущие зелья", 5.0F, 12.5F, 7.0F, ThemeColors.textPrimary());
      this.potionSearchField.render(f, matrix4f2);
      float f4 = this.getFloatType3();
      if (this.optionRows.isEmpty()) {
         FontRegistry.font2
            .process2(
               matrix4f2,
               drawApi,
               "Ничего не найдено",
               3.0F,
               44.0F + (f4 - FontRegistry.font2.process4("Ничего не найдено", 6.0F)) / 2.0F,
               6.0F,
               ThemeColors.textPlaceholder()
            );
      } else {
         float f5 = (float)this.optionRows.size() * 15.0F - 1.0F;
         this.scrollController.update(f4, f5);
         this.clippedContentRenderer
            .render(drawApi, matrix4f2, 3.0F, 44.0F, 106.0F, f4, this.scrollController.getOffset(), this.scrollController.getMinimumOffset(f4), matrix4f -> {
               float rowY = 44.0F + this.scrollController.getOffset();
   
               for(PotionOptionRow row : this.optionRows) {
                  row.getBounds().setPosition(3.0F, rowY);
                  if (rowY + 14.0F >= 44.0F && rowY <= 44.0F + f4) {
                     row.render(f, matrix4f);
                  }
   
                  rowY += 15.0F;
               }
            });
         this.scrollController.setContentHeight(f4, f5);
         this.scrollbar.process(drawApi, matrix4f2, 112.5F, 44.0F, f4, this.scrollController, this.getFloatType2(), this.getFloatType());
      }
   }
}
