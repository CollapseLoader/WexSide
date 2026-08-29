package ru.wexside.misc;

import java.util.List;
import org.joml.Matrix4f;
import ru.wexside.util.GuiDrawApi;

final class TextSegmentRow {
   private final List<TextSegment> segments;

   TextSegmentRow(List<TextSegment> list) {
      this.segments = list;
   }

   void member9094(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, float f6) {
      float f7 = f;

      for(TextSegment textSegment : this.segments) {
         textSegment.member7928(drawApi, matrix4f, f7, f2, f3, f4, f5, f6);
         f7 += textSegment.value(f4, f5, f6);
      }
   }

   float width(float f, float f2, float f3) {
      float f4 = 0.0F;

      for(TextSegment textSegment : this.segments) {
         f4 += textSegment.value(f, f2, f3);
      }

      return f4;
   }

   private List<TextSegment> getList() {
      return this.segments;
   }
}
