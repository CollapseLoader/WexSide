package ru.wexside.misc;

import ru.wexside.util.MsdfFontRenderer;

public final class FontRegistry {
   public static final MsdfFontRenderer regularText = load("sf_pro_display_medium");
   public static final MsdfFontRenderer smallText = load("inter_semibold");
   public static final MsdfFontRenderer brandText = load("Maison_Neue_Extended_Bold");
   public static final MsdfFontRenderer icons = load("icons");
   public static final MsdfFontRenderer semiboldText = load("inter_semibold");
   public static final MsdfFontRenderer cosmeticIcons = load("cosmetics_icons");
   public static final MsdfFontRenderer boldText = load("inter_bold");
   public static final MsdfFontRenderer headingText = load("inter_display_bold");
   public static final MsdfFontRenderer overlayText = regularText;
   public static final MsdfFontRenderer font2 = regularText;
   public static final MsdfFontRenderer font5 = smallText;
   public static final MsdfFontRenderer font8 = brandText;
   public static final MsdfFontRenderer font3 = icons;
   public static final MsdfFontRenderer font4 = semiboldText;
   public static final MsdfFontRenderer font10 = cosmeticIcons;
   public static final MsdfFontRenderer font6 = boldText;
   public static final MsdfFontRenderer font7 = headingText;
   public static final MsdfFontRenderer font9 = overlayText;

   private FontRegistry() {
   }

   private static MsdfFontRenderer load(String name) {
      String root = "/fonts/msdf/";
      return MsdfFontBuilder.getMsdfFontBuilder().process4(root + name + ".json").process(root + "atlas/" + name + ".png").process2(name).getMsdfFontRenderer();
   }
}
