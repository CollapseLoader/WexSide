package ru.wexside.misc;

public final class CompactTextFieldStyle implements TextFieldStyle {
   @Override
   public float longType() {
      return 5.0F;
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
      return 12.0F;
   }

   @Override
   public float getFloatType2() {
      return 12.0F;
   }
}
