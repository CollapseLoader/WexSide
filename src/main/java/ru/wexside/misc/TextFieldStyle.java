package ru.wexside.misc;

public interface TextFieldStyle {
   float longType();

   int getIntType();

   int getIntType2();

   float getFloatType();

   float getFloatType2();

   default float getFloatType3() {
      return 1.0F;
   }

   default float getFloatType4() {
      return 4.0F;
   }
}
