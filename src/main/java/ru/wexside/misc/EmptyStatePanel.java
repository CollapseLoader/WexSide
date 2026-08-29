package ru.wexside.misc;

import java.util.List;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.GuiDrawApi;

public final class EmptyStatePanel
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final float value;
   private final float value2 = 6.5F;
   private final String string2;
   private final float value3 = 9.0F;
   private final float value4;
   private final List<String> messageLines;

   public EmptyStatePanel(List<String> list, float f, float f2) {
      super(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F));
      this.string2 = "a";
      this.messageLines = list;
      this.value = f;
      this.value4 = f2;
   }

   public EmptyStatePanel() {
      this(List.of("Ничего не найдено", "Попробуйте ввести название предмета", "в поиске иначе"));
   }

   public EmptyStatePanel(List<String> list) {
      this(list, 16.0F, 6.0F);
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
      float f2 = bounds2.getX() + bounds2.getWidth() / 2.0F;
      float f3 = bounds2.getY() + (bounds2.getHeight() - this.value - this.value4 - (float)this.messageLines.size() * 9.0F) / 2.0F;
      FontRegistry.font3
         .process5(matrix4f, drawApi, "a", f2 - FontRegistry.font3.process3("a", this.value) / 2.0F, f3, this.value, ThemeColors.textSecondary());
      f3 += this.value + this.value4;

      for(String string : this.messageLines) {
         float f4 = FontRegistry.font2.process3(string, 6.5F);
         float f5 = FontRegistry.font2.process4(string, 6.5F);
         FontRegistry.font2.process2(matrix4f, drawApi, string, f2 - f4 / 2.0F, f3 + (9.0F - f5) / 2.0F, 6.5F, ThemeColors.textSecondary());
         f3 += 9.0F;
      }

      return bounds2.getY() + bounds2.getHeight();
   }
}
