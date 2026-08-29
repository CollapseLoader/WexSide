package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.MsdfFontRenderer;
import ru.wexside.util.SegmentedControlStyle;

public final class SegmentedControlOption
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final SegmentedControlStyle segmentedControlStyle;
   private final String string3;
   private float value;
   private boolean enabled;
   private final String string4;

   public SegmentedControlOption(GuiBounds bounds2, String string, SegmentedControlStyle segmentedControlStyle) {
      this(bounds2, string, null, segmentedControlStyle);
   }

   public SegmentedControlOption(GuiBounds bounds2, String string, String string2, SegmentedControlStyle segmentedControlStyle) {
      super(bounds2);
      this.string4 = string == null ? "" : string;
      this.string3 = string2 != null && !string2.isBlank() ? string2 : null;
      this.segmentedControlStyle = segmentedControlStyle == null ? new SegmentedControlStyle() : segmentedControlStyle;
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void setBooleanType(boolean bl) {
      this.enabled = bl;
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      return n3 == 0 && this.getBounds().contains((float)n, (float)n2);
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = this.segmentedControlStyle.getGuiDrawApi();
      MsdfFontRenderer font5 = this.segmentedControlStyle.getMsdfFontRenderer2();
      if (drawApi != null && font5 != null) {
         this.value = FrameInterpolator.lerpTowards(this.value, this.enabled ? 1.0F : 0.0F, this.segmentedControlStyle.getFloatType4());
         int n = ColorUtils.lerp(this.segmentedControlStyle.getIntType4(), this.segmentedControlStyle.getIntType(), (double)this.value);
         float f3 = this.segmentedControlStyle.getFloatType10();
         float f4 = font5.process3(this.string4, f3);
         float f5 = font5.process4(this.string4, f3);
         MsdfFontRenderer font8 = this.segmentedControlStyle.getMsdfFontRenderer();
         boolean bl = this.string3 != null && font8 != null;
         float f6 = bl ? this.segmentedControlStyle.getFloatType3() : 0.0F;
         float f7 = bl ? font8.process3(this.string3, f6) : 0.0F;
         float f8 = bl ? font8.process4(this.string3, f6) : 0.0F;
         float f9 = bl ? this.segmentedControlStyle.getFloatType8() : 0.0F;
         float f10 = f7 + f9 + f4;
         float f11 = bounds2.getX() + (bounds2.getWidth() - f10) / 2.0F;
         if (bl) {
            float f2 = bounds2.getY() + (bounds2.getHeight() - f8) / 2.0F;
            font8.process5(matrix4f, drawApi, this.string3, f11, f2, f6, n);
            f11 += f7 + f9;
         }

         float f2 = bounds2.getY() + (bounds2.getHeight() - f5) / 2.0F;
         font5.process2(matrix4f, drawApi, this.string4, f11, f2, f3, n);
         return bounds2.getY() + bounds2.getHeight();
      } else {
         return bounds2.getY() + bounds2.getHeight();
      }
   }

   public String getString() {
      return this.string3;
   }

   public String getString2() {
      return this.string4;
   }

   public boolean isActive() {
      return this.enabled;
   }
}
