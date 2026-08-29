package ru.wexside.misc;

import java.util.Arrays;
import net.minecraft.class_2960;

public enum WexsideHitParticles {
   CROSS("Cross", 0),
   DOLLAR("Dollar", 1),
   STAR("Star", 2),
   BLOOM("Bloom", 3),
   SNOWFLAKE("Snowflake", 4),
   LINE("Line", 5),
   LIGHT("Light", 6);

   private static final int CELL_WIDTH = 1064;
   private static final int CELL_HEIGHT = 1066;
   private static final float ATLAS_WIDTH = 7449.0F;
   private static final float ATLAS_HEIGHT = 2133.0F;
   private static final class_2960 ATLAS = class_2960.method_60655("wexside", "textures/visuals/particles.png");
   private static final String[] LABELS = Arrays.stream(values()).map(WexsideHitParticles::getString).toArray(x$0 -> new String[x$0]);
   private final String label;
   private final SpriteAtlasRegion primarySprite;
   private final SpriteAtlasRegion secondarySprite;

   private WexsideHitParticles(String label, int column) {
      this.label = label;
      int left = column * 1064;
      int right = left + 1064;
      this.primarySprite = sprite(label, left, 0, right, 1066);
      this.secondarySprite = sprite(label, left, 1066, right, 2132);
   }

   public String getString() {
      return this.label;
   }

   public static String[] getString2() {
      return (String[])LABELS.clone();
   }

   public static class_2960 getParticleTexture() {
      return ATLAS;
   }

   public SpriteAtlasRegion getSpriteAtlasRegion2() {
      return this.primarySprite;
   }

   public SpriteAtlasRegion getSpriteAtlasRegion() {
      return this.secondarySprite;
   }

   public static WexsideHitParticles process2(String label) {
      for(WexsideHitParticles particle : values()) {
         if (particle.label.equalsIgnoreCase(label)) {
            return particle;
         }
      }

      return CROSS;
   }

   public SpriteAtlasRegion process3(boolean primary) {
      return primary ? this.primarySprite : this.secondarySprite;
   }

   private static SpriteAtlasRegion sprite(String name, int left, int top, int right, int bottom) {
      float u1 = ((float)left + 2.0F) / 7449.0F;
      float v1 = ((float)top + 2.0F) / 2133.0F;
      float u2 = ((float)right - 2.0F) / 7449.0F;
      float v2 = ((float)bottom - 2.0F) / 2133.0F;
      return new SpriteAtlasRegion(name, u1, v1, u2, v2);
   }
}
