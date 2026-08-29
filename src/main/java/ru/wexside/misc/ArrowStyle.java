package ru.wexside.misc;

import java.util.Arrays;

public enum ArrowStyle {
   TRIANGLE("Triangle", "/assets/wexside/textures/arrows/triangle.png"),
   CLASSIC("Classic", "/assets/wexside/textures/arrows/old_triangle.png");

   private final String displayName;
   private final TextureResource texture;

   private ArrowStyle(String displayName, String resourcePath) {
      this.displayName = displayName;
      this.texture = new TextureResource(new ClasspathResource(resourcePath)).wrapT(33071).wrapS(33071);
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public TextureResource getTexture() {
      return this.texture;
   }

   public static String[] displayNames() {
      return Arrays.stream(values()).map(ArrowStyle::getDisplayName).toArray(x$0 -> new String[x$0]);
   }

   public static ArrowStyle fromDisplayName(String displayName) {
      return Arrays.stream(values()).filter(style -> style.displayName.equalsIgnoreCase(displayName)).findFirst().orElse(CLASSIC);
   }
}
