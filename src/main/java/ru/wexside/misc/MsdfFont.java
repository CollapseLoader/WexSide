package ru.wexside.misc;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Objects;

public final class MsdfFont {
   private final String name;
   private final Int2ObjectMap<MsdfGlyph> glyphs;
   private final TextureResource texture;
   private final MsdfAtlas atlas;
   private final MsdfMetrics metrics;

   public MsdfFont(String string, MsdfAtlas msdfAtlas, MsdfMetrics msdfMetrics, TextureResource texture2, Int2ObjectMap<MsdfGlyph> int2ObjectMap) {
      this.name = string;
      this.atlas = msdfAtlas;
      this.metrics = msdfMetrics;
      this.texture = texture2;
      this.glyphs = int2ObjectMap;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof MsdfFont)) {
         return false;
      } else {
         MsdfFont msdfFont = (MsdfFont)object;
         return Objects.equals(this.name, msdfFont.name)
            && Objects.equals(this.atlas, msdfFont.atlas)
            && Objects.equals(this.metrics, msdfFont.metrics)
            && Objects.equals(this.texture, msdfFont.texture)
            && Objects.equals(this.glyphs, msdfFont.glyphs);
      }
   }

   @Override
   public String toString() {
      String string = String.valueOf(this.glyphs);
      String string2 = String.valueOf(this.texture);
      String string3 = String.valueOf(this.metrics);
      String string4 = String.valueOf(this.atlas);
      String string5 = this.name;
      return "MSDFont[name=" + string5 + ", atlas=" + string4 + ", metrics=" + string3 + ", texture=" + string2 + ", glyphs=" + string + "]";
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.name, this.atlas, this.metrics, this.texture, this.glyphs);
   }

   public MsdfGlyph process(int n) {
      MsdfGlyph msdfGlyph = (MsdfGlyph)this.glyphs.get(n);
      if (msdfGlyph == null) {
         msdfGlyph = (MsdfGlyph)this.glyphs.get(32);
      }

      return msdfGlyph;
   }

   public Int2ObjectMap<MsdfGlyph> getInt2ObjectMap() {
      return this.glyphs;
   }

   public String getString() {
      return this.name;
   }

   public TextureResource getTexture() {
      return this.texture;
   }

   public MsdfMetrics getMetrics() {
      return this.metrics;
   }

   public MsdfAtlas getMsdfAtlas() {
      return this.atlas;
   }
}
