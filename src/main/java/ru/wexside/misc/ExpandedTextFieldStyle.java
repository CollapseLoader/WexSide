package ru.wexside.misc;

public final class ExpandedTextFieldStyle implements TextFieldStyle {
   @Override
   public float longType() {
      return 6.0F;
   }

   @Override
   public int getIntType() {
      return ThemeColors.controlFill();
   }

   @Override
   public int getIntType2() {
      return ThemeColors.borderPrimary();
   }

   @Override
   public float getFloatType() {
      return 14.0F;
   }

   @Override
   public float getFloatType2() {
      return 14.0F;
   }

   @Override
   public float getFloatType3() {
      return 8.0F;
   }

   @Override
   public float getFloatType4() {
      return 57.5F;
   }
}
