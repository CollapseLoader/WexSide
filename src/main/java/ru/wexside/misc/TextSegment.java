package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.util.GuiDrawApi;

final class TextSegment {
   private final String string2;
   private final boolean enabled;

   private TextSegment(String string, boolean bl) {
      this.string2 = string;
      this.enabled = bl;
   }

   void member7928(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6) {
      if (this.enabled) {
         FontRegistry.font3
            .process5(
               matrix4f, drawApi, this.string2, f + f6, f2 + (f3 - FontRegistry.font3.process4(this.string2, f5)) / 2.0F, f5, ThemeColors.hudTextSecondary()
            );
      } else {
         FontRegistry.font2
            .process2(matrix4f, drawApi, this.string2, f, f2 + (f3 - FontRegistry.font2.process4(this.string2, f4)) / 2.0F, f4, ThemeColors.hudTextSecondary());
      }
   }

   static TextSegment key(String string) {
      return new TextSegment(string, true);
   }

   float value(float f, float f2, float f3) {
      return this.enabled ? FontRegistry.font3.process3(this.string2, f2) + f3 * 2.0F : FontRegistry.font2.process3(this.string2, f);
   }

   private String getString() {
      return this.string2;
   }

   private boolean isActive() {
      return this.enabled;
   }

   static TextSegment text(String string) {
      return new TextSegment(string, false);
   }
}
