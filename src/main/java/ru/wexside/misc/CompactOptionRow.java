package ru.wexside.misc;

import ru.wexside.util.ColorUtils;

public class CompactOptionRow
   extends IconOptionRow
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider,
   MouseHitTest {
   public CompactOptionRow(String string, String string2) {
      super(string, string2);
   }

   public CompactOptionRow(String string, String string2, float f) {
      super(string, string2, f);
   }

   @Override
   protected float getFloatType() {
      return 6.5F;
   }

   @Override
   protected float getFloatType4() {
      return 6.0F;
   }

   @Override
   protected int getIntType4() {
      return ColorUtils.lerp(this.getIntType3(), ThemeColors.accent(), (double)this.getFloatType2());
   }

   @Override
   protected float getFloatType8() {
      return 6.25F;
   }
}
