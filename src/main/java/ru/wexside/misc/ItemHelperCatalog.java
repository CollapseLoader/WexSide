package ru.wexside.misc;

import java.util.List;
import net.minecraft.class_1802;

public final class ItemHelperCatalog {
   public static final ItemHelperEntry CHORUS = new ItemHelperEntry("chorus", "Плод хоруса", class_1802.field_8233, false);
   public static final ItemHelperEntry GOLDEN_APPLE = new ItemHelperEntry("gapple", "Золотое яблоко", class_1802.field_8463, false);
   public static final ItemHelperEntry ENCHANTED_GOLDEN_APPLE = new ItemHelperEntry(
      "enchanted_gapple", "Зачарованное золотое яблоко", class_1802.field_8367, false
   );
   public static final ItemHelperEntry INSTANT_HEALING = new ItemHelperEntry("instant_healing", "Зелье исцеления", class_1802.field_8574, true);
   public static final ItemHelperEntry SHIELD = new ItemHelperEntry("shield", "Щит", class_1802.field_8255, false);
   public static final ItemHelperEntry CROSSBOW = new ItemHelperEntry("crossbow", "Арбалет", class_1802.field_8399, false);
   public static final ItemHelperEntry MILK = new ItemHelperEntry("milk", "Молоко", class_1802.field_8103, false);
   public static final List<ItemHelperEntry> list = List.of(CHORUS, GOLDEN_APPLE, ENCHANTED_GOLDEN_APPLE, INSTANT_HEALING, SHIELD, CROSSBOW, MILK);
   public static final ItemHelperEntry itemHelperEntry2 = CHORUS;
   public static final ItemHelperEntry itemHelperEntry = GOLDEN_APPLE;
   public static final ItemHelperEntry itemHelperEntry3 = ENCHANTED_GOLDEN_APPLE;
   public static final ItemHelperEntry itemHelperEntry4 = INSTANT_HEALING;
   public static final ItemHelperEntry itemHelperEntry5 = SHIELD;
   public static final ItemHelperEntry itemHelperEntry6 = CROSSBOW;
   public static final ItemHelperEntry itemHelperEntry7 = MILK;

   private ItemHelperCatalog() {
   }
}
