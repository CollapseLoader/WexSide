package ru.wexside.util;

import java.util.function.Supplier;
import ru.wexside.misc.AbstractSettingDescription;
import ru.wexside.misc.ContainerDisplay;
import ru.wexside.misc.FontRegistry;
import ru.wexside.ui.GuiBounds;

public final class CompactSettingRow extends AbstractSettingDescription {
   private static final float COLLAPSED_HEIGHT = 15.0F;
   private static final float EXPANDED_BASE_HEIGHT = 26.5F;
   private static final float TITLE_FONT_SIZE = 7.0F;
   private static final float TITLE_DESCRIPTION_GAP = 2.0F;

   public CompactSettingRow(GuiBounds bounds, Supplier<String> titleSupplier, String description, ContainerDisplay display) {
      super(bounds, titleSupplier, description, display, 6.75F, 6.25F, 4.5F);
   }

   @Override
   protected float process2(float collapseOffset) {
      return this.titleTop() + collapseOffset;
   }

   @Override
   protected boolean isActive() {
      return this.containerDisplay.isActive2();
   }

   @Override
   protected float getDescriptionBaseline() {
      return this.titleTop() + this.titleHeight() + 2.0F;
   }

   @Override
   public float getFloatType2() {
      if (!this.hasDescription()) {
         return 15.0F;
      } else {
         float descriptionHeight = super.getFloatType3();
         float baseLineHeight = this.lineHeight(this.getFloatType4());
         float contentHeight = this.lineHeight(7.0F) + 2.0F + descriptionHeight;
         float verticalPadding = Math.max(0.0F, (26.5F - this.lineHeight(7.0F) - 2.0F - baseLineHeight) / 2.0F);
         float expandedHeight = verticalPadding + contentHeight + verticalPadding;
         float additionalDescriptionHeight = Math.max(0.0F, descriptionHeight - baseLineHeight);
         return expandedHeight - (1.0F - this.getVisibilityProgress()) * (11.5F + additionalDescriptionHeight);
      }
   }

   private float titleTop() {
      float contentTop;
      if (!this.hasDescription()) {
         contentTop = (15.0F - this.titleHeight()) / 2.0F;
      } else {
         float oneLineContent = this.lineHeight(7.0F) + 2.0F + this.lineHeight(this.getFloatType4());
         contentTop = (26.5F - oneLineContent) / 2.0F;
      }

      return this.bounds2.getY() + contentTop;
   }

   private float titleHeight() {
      return FontRegistry.font2.process4(this.getString2(), 7.0F);
   }

   private float lineHeight(float fontSize) {
      return FontRegistry.font2.process4("A", fontSize);
   }

   public float getFloatType5() {
      return this.value5;
   }
}
