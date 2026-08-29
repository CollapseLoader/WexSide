package ru.wexside.misc;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.MsdfFontRenderer;

public final class TextLayoutUtils {
   public static void process(
      Matrix4f matrix4f, GuiDrawApi drawApi, String string, MsdfFontRenderer font5, float f, float f2, float f3, float f4, float f5, int n
   ) {
      List<String> list = process2(string, font5, f, f2);
      float f6 = f4;

      for(String string2 : list) {
         font5.process2(matrix4f, drawApi, string2, f3, f6, f, n);
         String string3 = string2.isEmpty() ? " " : string2;
         f6 += font5.process4(string3, f) + f5;
      }
   }

   public static String trimToWidth(String string, MsdfFontRenderer font5, float f, float f2) {
      if (string != null && !string.isEmpty() && !(f2 <= 0.0F)) {
         if (font5.process3(string, f) <= f2) {
            return string;
         } else {
            String string2 = "...";
            float f3 = font5.process3(string2, f);

            for(int i = string.length() - 1; i > 0; --i) {
               String string3 = string.substring(0, i);
               if (font5.process3(string3, f) + f3 <= f2) {
                  return string3 + string2;
               }
            }

            return string2;
         }
      } else {
         return "";
      }
   }

   private TextLayoutUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static List<String> process2(String string, MsdfFontRenderer font5, float f, float f2) {
      ArrayList<String> arrayList = new ArrayList<>();
      if (string != null && !(f2 <= 0.0F)) {
         for(String string2 : string.split("\n", -1)) {
            process5(string2, font5, f, f2, arrayList);
         }

         return arrayList;
      } else {
         arrayList.add("");
         return arrayList;
      }
   }

   public static String process3(String string, int n) {
      if (string == null) {
         return "";
      } else if (n <= 0) {
         return "";
      } else if (string.length() <= n) {
         return string;
      } else {
         String string2 = string.substring(0, n);
         return string2 + "...";
      }
   }

   private static void process4(String string, MsdfFontRenderer font5, float f, float f2, StringBuilder stringBuilder, List<String> list) {
      String string2 = stringBuilder.length() == 0 ? "" : " ";
      String string5 = String.valueOf(stringBuilder);
      String string6 = string5 + string2 + string;
      if (font5.process3(string6, f) <= f2) {
         stringBuilder.setLength(0);
         stringBuilder.append(string6);
      } else if (font5.process3(string, f) <= f2 / 2.0F) {
         list.add(stringBuilder.toString());
         stringBuilder.setLength(0);
         stringBuilder.append(string);
      } else {
         if (stringBuilder.length() > 0 && !string.isEmpty()) {
            char c = string.charAt(0);
            String string7 = String.valueOf(stringBuilder);
            String string8 = string7 + " " + c;
            if (font5.process3(string8, f) <= f2) {
               stringBuilder.append(' ');
            } else {
               list.add(stringBuilder.toString());
               stringBuilder.setLength(0);
            }
         }

         for(int i = 0; i < string.length(); ++i) {
            char c;
            char c2 = c = string.charAt(i);
            String string9 = stringBuilder.toString();
            String string10 = string9 + c2;
            if (font5.process3(string10, f) <= f2) {
               stringBuilder.append(c);
            } else if (stringBuilder.length() == 0) {
               stringBuilder.append(c);
               list.add(stringBuilder.toString());
               stringBuilder.setLength(0);
            } else {
               list.add(stringBuilder.toString());
               stringBuilder.setLength(0);
               --i;
            }
         }
      }
   }

   private static void process5(String string, MsdfFontRenderer font5, float f, float f2, List<String> list) {
      if (string.isEmpty()) {
         list.add("");
      } else {
         StringBuilder stringBuilder = new StringBuilder();

         for(String string2 : string.split(" ", -1)) {
            process4(string2, font5, f, f2, stringBuilder, list);
         }

         list.add(stringBuilder.toString());
      }
   }
}
