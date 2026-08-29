package ru.wexside.misc;

import java.util.List;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.GuiDrawApi;

public final class SectionHeader
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private String string5;
   private final String string6;
   private final String string7;
   public static final float value5 = 40.0F;
   private final String string8;

   public SectionHeader(String string, String string2, String string3, String string4, float f) {
      super(new GuiBounds(0.0F, 0.0F, f, 40.0F));
      this.string5 = string;
      this.string7 = string2;
      this.string6 = string3;
      this.string8 = string4;
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      int n = ThemeColors.accent();
      FontRegistry.font4.process2(matrix4f, drawApi, this.string5, bounds2.getX(), bounds2.getY(), 5.5F, n);
      float f2 = bounds2.getX() + FontRegistry.font4.process3(this.string5, 5.5F) + 1.5F;
      float f3 = bounds2.getY() + FontRegistry.font4.process4(this.string5, 5.5F) / 2.0F;
      FontRegistry.font3.process5(matrix4f, drawApi, this.string7, f2, f3 - FontRegistry.font3.process14(this.string7.charAt(0), 0.0F, 6.0F), 6.0F, n);
      FontRegistry.font4.process2(matrix4f, drawApi, this.string6, bounds2.getX(), bounds2.getY() + 8.5F, 8.0F, ThemeColors.textPrimary());
      List<String> list = TextLayoutUtils.process2(this.string8, FontRegistry.font2, 6.5F, bounds2.getWidth());
      float f4 = bounds2.getY() + 22.0F;

      for(String string : list) {
         FontRegistry.font2.process2(matrix4f, drawApi, string, bounds2.getX(), f4, 6.5F, ThemeColors.textMuted());
         f4 += 9.0F;
      }

      return bounds2.getY() + 40.0F;
   }

   public void setString(String string) {
      this.string5 = string;
   }
}
