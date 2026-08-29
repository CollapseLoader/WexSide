package ru.wexside.misc;

import java.util.Arrays;
import java.util.List;
import org.joml.Matrix4f;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.ui.PopupPanel;
import ru.wexside.util.GuiDrawApi;

public class NumberEditorPopup
   extends PopupPanel
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final float value;
   private final float value2;
   private final float value3;
   private final float value4;
   private final String string2;
   private final PopupHeader popupHeader;
   private final float value5 = 125.0F;
   private final float value6;
   private final float value7;
   private final String string3;
   private final List<LabeledGuiElement> entries;
   private final float value8;

   protected NumberEditorPopup(String string, LabeledGuiElement... cls0755Array) {
      super(new GuiBounds(0.0F, 0.0F, 125.0F, 47.0F));
      this.value2 = 5.0F;
      this.value6 = 5.0F;
      this.value8 = 6.0F;
      this.value4 = 5.0F;
      this.value3 = 7.0F;
      this.value7 = 6.25F;
      this.value = 3.0F;
      this.string2 = "Настройка ползунка";
      this.string3 = "B";
      this.popupHeader = new PopupHeader(new GuiBounds(5.0F, 5.0F, 115.0F, 0.0F), "Настройка ползунка", "B", string);
      this.entries = Arrays.asList(cls0755Array);

      for(LabeledGuiElement labeledGuiElement : this.entries) {
         this.addChild(labeledGuiElement.getElement());
      }
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
      int n3 = (int)((float)n - this.getBounds().getX());
      int n4 = (int)((float)n2 - this.getBounds().getY());

      for(LabeledGuiElement entry : this.entries) {
         entry.getElement().onMouseScroll(n3, n4, d);
      }
   }

   @Override
   public void update() {
      for(LabeledGuiElement entry : this.entries) {
         entry.getElement().update();
      }
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      int n4 = (int)((float)n - this.getBounds().getX());
      int n5 = (int)((float)n2 - this.getBounds().getY());
      super.onMouseReleased(n4, n5, n3);
   }

   protected float getFloatType4() {
      return 6.0F;
   }

   @Override
   protected void updateLayout() {
      float f = this.getFloatType11();

      for(LabeledGuiElement labeledGuiElement : this.entries) {
         GuiElement element2 = labeledGuiElement.getElement();
         float f2 = 120.0F - element2.getBounds().getWidth();
         element2.getBounds().setPosition(f2, f);
         element2.getBounds().setSize(element2.getBounds().getWidth(), element2.getBounds().getHeight());
         f += element2.getBounds().getHeight() + 5.0F;
      }

      float f3 = this.entries.isEmpty() ? this.getFloatType11() + 7.0F : f - 5.0F + 7.0F;
      this.getBounds().setSize(125.0F, f3);
   }

   @Override
   protected void renderPopup(float f, Matrix4f matrix4f, GuiDrawApi drawApi) {
      this.popupHeader.BlockHitResult(matrix4f, drawApi);

      for(LabeledGuiElement labeledGuiElement : this.entries) {
         GuiElement element2 = labeledGuiElement.getElement();
         FontRegistry.font2
            .process2(matrix4f, drawApi, labeledGuiElement.ModelPartBuilder(), 5.0F, element2.getBounds().getY() + 3.0F, 6.25F, ThemeColors.textPrimary());
         element2.render(f, matrix4f);
      }
   }

   protected float getFloatType() {
      return 6.0F;
   }

   public float getFloatType2() {
      return 5.0F;
   }

   public float getFloatType3() {
      return 7.0F;
   }

   public float getFloatType5() {
      return 5.0F;
   }

   public String getString() {
      return "Настройка ползунка";
   }

   public String getString2() {
      return "B";
   }

   public float getFloatType6() {
      return 6.25F;
   }

   public float getFloatType7() {
      return 3.0F;
   }

   public List<LabeledGuiElement> getList() {
      return this.entries;
   }

   public float getFloatType8() {
      return 6.0F;
   }

   public float getFloatType9() {
      return 5.0F;
   }

   public float getFloatType10() {
      return 125.0F;
   }

   private float getFloatType11() {
      return 5.0F + this.popupHeader.getFloatType2() + 6.0F;
   }

   public PopupHeader getPopupHeader() {
      return this.popupHeader;
   }
}
