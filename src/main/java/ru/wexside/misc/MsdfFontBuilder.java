package ru.wexside.misc;

import com.google.gson.Gson;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import ru.wexside.util.MsdfFontRenderer;

public class MsdfFontBuilder {
   private String jsonPath;
   private String atlasPath;
   private String name = "unnamed";
   private final ResourceResolver router = new ResourceResolver("/assets/wexside", ClasspathResource::new);

   public MsdfFontBuilder process(String string) {
      this.atlasPath = string;
      return this;
   }

   public MsdfFontRenderer getMsdfFontRenderer() {
      if (this.jsonPath != null && this.atlasPath != null) {
         ResourceData callback = this.router.resolve(this.jsonPath);
         MsdfFontData msdfFontData = (MsdfFontData)new Gson()
            .fromJson(new InputStreamReader(callback.openStream(), StandardCharsets.UTF_8), MsdfFontData.class);
         TextureResource texture2 = new TextureResource(this.router.resolve(this.atlasPath))
            .minFilter(9729)
            .magFilter(9729)
            .mipmaps(false)
            .wrapS(33071)
            .wrapT(33071);
         MsdfAtlas msdfAtlas = new MsdfAtlas(msdfFontData.atlas.distanceRange, msdfFontData.atlas.width, msdfFontData.atlas.height);
         MsdfMetrics msdfMetrics = new MsdfMetrics(msdfFontData.metrics.lineHeight, msdfFontData.metrics.ascender, msdfFontData.metrics.descender);
         Int2ObjectMap<MsdfGlyph> int2ObjectMap = process3(msdfFontData, msdfAtlas);
         MsdfFont msdfFont = new MsdfFont(this.name, msdfAtlas, msdfMetrics, texture2, int2ObjectMap);
         return new MsdfFontRenderer(msdfFont);
      } else {
         throw new IllegalStateException("MSDFRendererBuilder необходимо указать и json и atlas");
      }
   }

   public MsdfFontBuilder process2(String string) {
      this.name = string;
      return this;
   }

   public static MsdfFontBuilder getMsdfFontBuilder() {
      return new MsdfFontBuilder();
   }

   private static Int2ObjectMap<MsdfGlyph> process3(MsdfFontData msdfFontData, MsdfAtlas msdfAtlas) {
      Int2ObjectOpenHashMap int2ObjectOpenHashMap = new Int2ObjectOpenHashMap();

      for(MsdfGlyphData msdfGlyphData : msdfFontData.glyphs) {
         if (msdfGlyphData.planeBounds != null && msdfGlyphData.atlasBounds != null) {
            MsdfGlyph msdfGlyph = new MsdfGlyph(
               msdfGlyphData.unicode,
               msdfGlyphData.planeBounds.left,
               msdfGlyphData.planeBounds.top,
               msdfGlyphData.planeBounds.right,
               msdfGlyphData.planeBounds.bottom,
               msdfGlyphData.atlasBounds.left / msdfAtlas.getFloatType2(),
               1.0F - msdfGlyphData.atlasBounds.top / msdfAtlas.getFloatType(),
               msdfGlyphData.atlasBounds.right / msdfAtlas.getFloatType2(),
               1.0F - msdfGlyphData.atlasBounds.bottom / msdfAtlas.getFloatType(),
               msdfGlyphData.advance
            );
            int2ObjectOpenHashMap.put(msdfGlyphData.unicode, msdfGlyph);
         }
      }

      return int2ObjectOpenHashMap;
   }

   public MsdfFontBuilder process4(String string) {
      this.jsonPath = string;
      return this;
   }
}
