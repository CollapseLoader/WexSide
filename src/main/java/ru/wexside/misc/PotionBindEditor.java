package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.GuiDrawApi;

public final class PotionBindEditor
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   public static final float value = 18.0F;
   static final String string = "Триггер кнопка";
   static final String string2 = "Зелья выбрасываются при нажатии бинда";
   private final KeybindCaptureField keybindCaptureField;
   private final float value2;
   private final float value3;
   private final float value4;
   private final float value5;

   public PotionBindEditor(PotionEditorState potionEditorState, float f) {
      super(new GuiBounds(0.0F, 0.0F, f, 18.0F));
      this.keybindCaptureField = new KeybindCaptureField(
         KeybindDescriptor.process(
            "Триггер кнопка",
            "Зелья выбрасываются при нажатии бинда",
            () -> potionEditorState.getWorkingCopy().getBindInput(),
            bind -> potionEditorState.getWorkingCopy().setBindInput(bind)
         )
      );
      this.value4 = 7.0F;
      this.value2 = 6.5F;
      this.value5 = 10.0F;
      this.value3 = 3.0F;
      this.addChild(this.keybindCaptureField);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (!this.getBounds().contains((float)n, (float)n2)) {
         return false;
      } else {
         this.update2();
         return super.onMousePressed((int)((float)n - this.getBounds().getX()), (int)((float)n2 - this.getBounds().getY()), n3);
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      Matrix4f matrix4f2 = new Matrix4f(matrix4f).translate(bounds2.getX(), bounds2.getY(), 0.0F);
      this.update2();
      FontRegistry.font2.process2(matrix4f2, drawApi, "Триггер кнопка", 0.0F, 0.0F, 7.0F, ThemeColors.textPrimary());
      FontRegistry.font2.process2(matrix4f2, drawApi, "Зелья выбрасываются при нажатии бинда", 0.0F, 10.0F, 6.5F, ThemeColors.textMuted());
      this.keybindCaptureField.render(f, matrix4f2);
      return bounds2.getY() + 18.0F;
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      this.keybindCaptureField.onMouseReleased((int)((float)n - this.getBounds().getX()), (int)((float)n2 - this.getBounds().getY()), n3);
   }

   @Override
   public boolean onKeyPressed(int n) {
      return this.keybindCaptureField.onKeyPressed(n);
   }

   @Override
   public void update2() {
      float f = this.keybindCaptureField.getFloatType();
      this.keybindCaptureField.getBounds().setSize(f, this.keybindCaptureField.getFloatType2());
      this.keybindCaptureField.getBounds().setPosition(this.getBounds().getWidth() - f, 3.0F);
   }
}
