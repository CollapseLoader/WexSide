package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.setting.ColorSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.ToggleIndicatorRenderer;

public final class ColorPaletteEntry
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final ColorSetting colorSetting;
   private final float value;
   private final int slot;
   private final ToggleIndicatorRenderer toggleIndicatorRenderer = new ToggleIndicatorRenderer();

   public ColorPaletteEntry(ColorSetting colorSetting, int n) {
      super(new GuiBounds(0.0F, 0.0F, 5.0F, 5.0F));
      this.value = 5.0F;
      this.colorSetting = colorSetting;
      this.slot = n;
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
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
      boolean bl = this.colorSetting.getSelectedIndex() == this.slot;
      this.toggleIndicatorRenderer.process(matrix4f, drawApi, bounds2, this.getIntType2(), bl);
      return bounds2.getX() + bounds2.getWidth();
   }

   public int getIntType() {
      return this.slot;
   }

   public float getFloatType() {
      return 5.0F;
   }

   public ToggleIndicatorRenderer getToggleIndicatorRenderer() {
      return this.toggleIndicatorRenderer;
   }

   private int getIntType2() {
      return this.colorSetting.getPrimaryColor(this.slot);
   }

   public ColorSetting getColorSetting() {
      return this.colorSetting;
   }

   public float getFloatType2() {
      return 5.0F;
   }
}
