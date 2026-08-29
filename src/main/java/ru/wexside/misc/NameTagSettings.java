package ru.wexside.misc;

import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.NumberSetting;

public final class NameTagSettings {
   public static final String HEALTH = "Health";
   public static final String SPHERE = "Sphere";
   public static final String MONEY = "Money";
   public static final String TALISMAN = "Talisman";
   public static final String ITEMS = "Items";
   private final BooleanSetting enabled;
   private final MultiSelectSetting displayedElements;
   private final BooleanSetting showEnchantments;
   private final BooleanSetting preventOverlap;
   private final NumberSetting overlapThreshold;

   public NameTagSettings(
      BooleanSetting enabled,
      MultiSelectSetting displayedElements,
      BooleanSetting showEnchantments,
      BooleanSetting preventOverlap,
      NumberSetting overlapThreshold
   ) {
      this.enabled = enabled;
      this.displayedElements = displayedElements;
      this.showEnchantments = showEnchantments;
      this.preventOverlap = preventOverlap;
      this.overlapThreshold = overlapThreshold;
   }

   public boolean isEnabled() {
      return this.enabled.isEnabled();
   }

   public boolean isMoneyVisible() {
      return this.isElementVisible("Money");
   }

   public boolean shouldShowEnchantments() {
      return this.showEnchantments != null && this.showEnchantments.isEnabled();
   }

   public boolean isTalismanVisible() {
      return this.isElementVisible("Talisman");
   }

   public boolean isHealthVisible() {
      return this.isElementVisible("Health");
   }

   public boolean isSphereVisible() {
      return this.isElementVisible("Sphere");
   }

   public boolean shouldPreventOverlap() {
      return this.preventOverlap != null && this.preventOverlap.isEnabled();
   }

   public boolean areItemsVisible() {
      return this.isElementVisible("Items");
   }

   public double getOverlapThreshold() {
      return this.overlapThreshold != null ? this.overlapThreshold.getValue() : 1.0;
   }

   private boolean isElementVisible(String element) {
      return this.displayedElements != null && this.displayedElements.getSelectedOptions().contains(element);
   }
}
