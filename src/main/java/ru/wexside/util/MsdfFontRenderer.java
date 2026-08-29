package ru.wexside.util;

import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import ru.wexside.misc.MsdfFont;
import ru.wexside.misc.MsdfGlyph;
import ru.wexside.render.GuiDrawMode;
import ru.wexside.render.TextGradient;

public class MsdfFontRenderer {
   static final int MEASURE_CACHE_LIMIT = 4096;
   private final MsdfFont font;
   private final Map<String, Float> widthCache = new HashMap<>();

   public MsdfFontRenderer(MsdfFont msdfFont) {
      this.font = msdfFont;
   }

   public float process(String string, float f) {
      int n = 1;

      for(int i = 0; i < string.length(); ++i) {
         if (string.charAt(i) == '\n') {
            ++n;
         }
      }

      return (float)n * this.font.getMetrics().getFloatType4() * f;
   }

   public void process2(Matrix4f matrix4f, GuiDrawApi drawApi, String string, float f, float f2, float f3, int n) {
      this.process7(matrix4f, drawApi, string, f, f2, f3, 0.0F, 0.0F, n, n);
   }

   public float process3(String string, float f) {
      Float f2 = this.widthCache.get(string);
      if (f2 != null) {
         return f2 * f;
      } else {
         float f3 = this.process10(string, 1.0F);
         if (this.widthCache.size() >= 4096) {
            this.widthCache.clear();
         }

         this.widthCache.put(string, f3);
         return f3 * f;
      }
   }

   public float process4(String string, float f) {
      return this.process6(this.process(string, f));
   }

   public void process5(Matrix4f matrix4f, GuiDrawApi drawApi, String string, float f, float f2, float f3, int n) {
      this.process7(matrix4f, drawApi, string, f, f2, f3, 0.0F, 0.0F, n, n);
   }

   private float process6(float f) {
      return (float)Math.round(f * 2.0F) / 2.0F;
   }

   public void update() {
      if (this.font != null && this.font.getTexture() != null) {
         this.font.getTexture().getTextureId();
      }
   }

   public void process7(Matrix4f matrix4f, GuiDrawApi drawApi, String string, float f, float f2, float f3, float f4, float f5, int n, int n2) {
      int n3 = drawApi.bindTexture(this.font.getTexture().getTextureId(), this.font.getTexture().getWidth(), this.font.getTexture().getHeight());
      float f6 = (float)this.font.getTexture().getWidth();
      float f7 = (float)this.font.getTexture().getHeight();
      float f8 = this.font.getMsdfAtlas().getFloatType3();
      float f9 = (float)(n2 >> 16 & 0xFF) / 255.0F;
      float f10 = (float)(n2 >> 8 & 0xFF) / 255.0F;
      float f11 = (float)(n2 & 0xFF) / 255.0F;
      float f12 = (float)(n2 >> 24 & 0xFF) / 255.0F;
      float f13 = (float)(n >> 16 & 0xFF) / 255.0F;
      float f14 = (float)(n >> 8 & 0xFF) / 255.0F;
      float f15 = (float)(n & 0xFF) / 255.0F;
      float f16 = (float)(n >> 24 & 0xFF) / 255.0F;
      float f17 = f;
      float f18 = f2 + this.font.getMetrics().getFloatType2() * f3;

      for(int i = 0; i < string.length(); ++i) {
         char c = string.charAt(i);
         if (c == '\n') {
            f17 = f;
            f18 += this.font.getMetrics().getFloatType4() * f3;
         } else if (c == ' ') {
            MsdfGlyph msdfGlyph = this.font.process(32);
            f17 += (msdfGlyph != null ? msdfGlyph.getFloatType4() : this.font.getMetrics().getFloatType4() * 0.25F) * f3;
         } else {
            MsdfGlyph msdfGlyph = this.font.process(c);
            if (msdfGlyph != null) {
               float f19 = msdfGlyph.getFloatType8() * f3;
               float f20 = msdfGlyph.getFloatType2() * f3;
               float f21 = f17 + msdfGlyph.getPlaneLeft() * f3;
               float f22 = f18 - msdfGlyph.getFloatType7() * f3;
               drawApi.getGuiVertexBuffer()
                  .process(
                     matrix4f,
                     f21,
                     f22,
                     f19,
                     f20,
                     msdfGlyph.getFloatType6(),
                     msdfGlyph.getFloatType(),
                     msdfGlyph.getFloatType5(),
                     msdfGlyph.getFloatType10(),
                     0.0F,
                     0.0F,
                     0.0F,
                     0.0F,
                     f4,
                     f5,
                     0.2F,
                     f3,
                     f8,
                     f6,
                     f7,
                     this.process12(f16, f13, f14, f15),
                     this.process12(f12, f9, f10, f11),
                     this.process12(f12, f9, f10, f11),
                     this.process12(f12, f9, f10, f11),
                     this.process12(f12, f9, f10, f11),
                     n3,
                     GuiDrawMode.MSDF_TEXT
                  );
               f17 += msdfGlyph.getFloatType4() * f3;
            }
         }
      }
   }

   public void process8(
      Matrix4f matrix4f, GuiDrawApi drawApi, String string, float f, float f2, float f3, float f4, int n, TextGradient textGradient, float f5, float f6
   ) {
      int n2 = drawApi.bindTexture(this.font.getTexture().getTextureId(), this.font.getTexture().getWidth(), this.font.getTexture().getHeight());
      float f7 = (float)this.font.getTexture().getWidth();
      float f8 = (float)this.font.getTexture().getHeight();
      float f9 = this.font.getMsdfAtlas().getFloatType3();
      float f10 = f6 <= 1.0E-4F ? 1.0F : f6;
      float f11 = f;
      float f12 = f2 + this.font.getMetrics().getFloatType2() * f3;

      for(int i = 0; i < string.length(); ++i) {
         char c = string.charAt(i);
         if (c == '\n') {
            f11 = f;
            f12 += this.font.getMetrics().getFloatType4() * f3;
         } else if (c == ' ') {
            MsdfGlyph msdfGlyph = this.font.process(32);
            f11 += (msdfGlyph != null ? msdfGlyph.getFloatType4() : this.font.getMetrics().getFloatType4() * 0.25F) * f3;
         } else {
            MsdfGlyph msdfGlyph = this.font.process(c);
            if (msdfGlyph != null) {
               float f13 = msdfGlyph.getFloatType8() * f3;
               float f14 = msdfGlyph.getFloatType2() * f3;
               float f15 = f11 + msdfGlyph.getPlaneLeft() * f3;
               float f16 = f12 - msdfGlyph.getFloatType7() * f3;
               float f17 = process11((f15 - f5) / f10);
               float f18 = process11((f15 + f13 - f5) / f10);
               int n3 = textGradient.sample(f17);
               int n4 = textGradient.sample(f18);
               drawApi.getGuiVertexBuffer()
                  .process(
                     matrix4f,
                     f15,
                     f16,
                     f13,
                     f14,
                     msdfGlyph.getFloatType6(),
                     msdfGlyph.getFloatType(),
                     msdfGlyph.getFloatType5(),
                     msdfGlyph.getFloatType10(),
                     0.0F,
                     0.0F,
                     0.0F,
                     0.0F,
                     0.0F,
                     f4,
                     0.2F,
                     f3,
                     f9,
                     f7,
                     f8,
                     n,
                     n3,
                     n4,
                     n4,
                     n3,
                     n2,
                     GuiDrawMode.MSDF_TEXT
                  );
               f11 += msdfGlyph.getFloatType4() * f3;
            }
         }
      }
   }

   public void process9(Matrix4f matrix4f, GuiDrawApi drawApi, String string, float f, float f2, float f3, float f4, int n, TextGradient textGradient) {
      this.process8(matrix4f, drawApi, string, f, f2, f3, f4, n, textGradient, f, this.process3(string, f3));
   }

   private float process10(String string, float f) {
      float f2 = 0.0F;
      float f3 = 0.0F;

      for(int i = 0; i < string.length(); ++i) {
         char c = string.charAt(i);
         if (c == '\n') {
            if (f2 > f3) {
               f3 = f2;
            }

            f2 = 0.0F;
         } else if (c == ' ') {
            MsdfGlyph msdfGlyph = this.font.process(32);
            f2 += (msdfGlyph != null ? msdfGlyph.getFloatType4() : this.font.getMetrics().getFloatType4() * 0.25F) * f;
         } else {
            MsdfGlyph msdfGlyph = this.font.process(c);
            if (msdfGlyph != null) {
               f2 += msdfGlyph.getFloatType4() * f;
            }
         }
      }

      return Math.max(f3, f2);
   }

   private static float process11(float f) {
      return f < 0.0F ? 0.0F : (f > 1.0F ? 1.0F : f);
   }

   private int process12(float f, float f2, float f3, float f4) {
      return (int)(f * 255.0F) << 24 | (int)(f2 * 255.0F) << 16 | (int)(f3 * 255.0F) << 8 | (int)(f4 * 255.0F);
   }

   public float process13(char c, float f, float f2) {
      MsdfGlyph msdfGlyph = this.font.process(c);
      return msdfGlyph == null ? f : f + (msdfGlyph.getPlaneLeft() + msdfGlyph.getFloatType8() * 0.5F) * f2;
   }

   public float process14(char c, float f, float f2) {
      MsdfGlyph msdfGlyph = this.font.process(c);
      return msdfGlyph == null ? f : f + (this.font.getMetrics().getFloatType2() - msdfGlyph.getFloatType7() + msdfGlyph.getFloatType2() * 0.5F) * f2;
   }

   public void process15(Matrix4f matrix4f, GuiDrawApi drawApi, String string, float f, float f2, float f3, float f4, int n, int n2) {
      this.process7(matrix4f, drawApi, string, f, f2, f3, 0.0F, f4, n, n2);
   }
}
