package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public abstract class AbstractOptionRow
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider,
   MouseHitTest {
   private float value;
   private final String string3;
   private boolean enabled;
   private float value2;
   private final String string4;
   public static final float value3 = 14.0F;
   public static final float value4 = 109.0F;

   protected AbstractOptionRow(String string, String string2) {
      this(string, string2, 109.0F);
   }

   protected AbstractOptionRow(String string, String string2, float f) {
      super(new GuiBounds(0.0F, 0.0F, f, 14.0F));
      this.string4 = string;
      this.string3 = string2;
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
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      GuiInteractionState guiInteractionState = GuiInteractionState.getInstance();
      boolean bl = this.process13(guiInteractionState.getScaledMouseX(), guiInteractionState.getScaledMouseY());
      this.value = FrameInterpolator.lerpTowards(this.value, bl ? 1.0F : 0.0F, 15.0F);
      this.value2 = FrameInterpolator.lerpTowards(this.value2, this.enabled ? 1.0F : 0.0F, 30.0F);
      this.process4(matrix4f, drawApi, bounds2);
      this.process5(matrix4f, drawApi, bounds2);
      this.process(matrix4f, drawApi, bounds2);
      return bounds2.getY() + bounds2.getHeight();
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

   protected int getIntType() {
      return ColorUtils.lerp(ColorUtils.withAlpha(ThemeColors.borderSubtle(), 0.0F), ThemeColors.borderSubtle(), (double)this.value);
   }

   protected float getFloatType() {
      return 7.0F;
   }

   protected float getFloatType2() {
      return this.value2;
   }

   protected float getFloatType3() {
      return 7.0F;
   }

   protected boolean hasDescription() {
      return this.string3 != null && !this.string3.isBlank();
   }

   protected int getIntType2() {
      return this.getIntType3();
   }

   protected float getFloatType4() {
      return 7.0F;
   }

   public float getFloatType5() {
      return this.value;
   }

   protected void process4(Matrix4f matrix4f, GuiDrawApi drawApi, GuiBounds bounds2) {
      drawApi.drawRoundedRectangle(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), this.getFloatType4(), this.getIntType());
   }

   protected abstract void process(Matrix4f var1, GuiDrawApi var2, GuiBounds var3);

   protected int getIntType3() {
      return ColorUtils.lerp(ThemeColors.textSecondary(), ThemeColors.textPrimary(), (double)this.value);
   }

   public float getFloatType6() {
      return this.value2;
   }

   protected int getIntType4() {
      return this.getIntType3();
   }

   @Override
   public boolean process13(int n, int n2) {
      GuiBounds bounds2 = this.getBounds();
      float f = this.getAbsoluteX();
      float f2 = this.getAbsoluteY();
      return (float)n >= f && (float)n <= f + bounds2.getWidth() && (float)n2 >= f2 && (float)n2 <= f2 + bounds2.getHeight();
   }

   protected float getFloatType7() {
      return 14.0F;
   }

   protected void process5(Matrix4f matrix4f, GuiDrawApi drawApi, GuiBounds bounds2) {
      float f = bounds2.getX() + 3.5F;
      if (this.hasDescription()) {
         FontRegistry.font3.process5(matrix4f, drawApi, this.string3, f, bounds2.getY() + 3.25F, this.getFloatType3(), this.getIntType2());
         f += 10.0F;
      }

      float f2 = bounds2.getX() + bounds2.getWidth() - this.getFloatType7() - f;
      String string = TextLayoutUtils.trimToWidth(this.string4, FontRegistry.font2, this.getFloatType8(), f2);
      FontRegistry.font2.process2(matrix4f, drawApi, string, f, bounds2.getY() + 3.0F, this.getFloatType8(), this.getIntType4());
   }

   protected float getFloatType8() {
      return 6.0F;
   }
}
