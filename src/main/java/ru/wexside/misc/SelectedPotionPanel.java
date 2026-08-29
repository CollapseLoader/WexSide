package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.GuiDrawApi;

public final class SelectedPotionPanel
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final ActionButton actionButton;
   static final float value = 13.5F;
   private final PotionEditorState potionEditorState;
   public static final float value2 = 36.5F;
   private final ActionButton actionButton2;
   private final float value3;
   private final float value4;
   static final float value5 = 96.0F;
   private final String string2;
   private final float value6;
   private final float value7;
   private final float value8 = 6.0F;
   private final String string3;
   private final String string4;

   public SelectedPotionPanel(PotionEditorState potionEditorState, float f, Runnable runnable, Runnable runnable2) {
      super(new GuiBounds(0.0F, 0.0F, f, 36.5F));
      this.value6 = 8.0F;
      this.value4 = 8.5F;
      this.value3 = 23.0F;
      this.value7 = 2.0F;
      this.string3 = "Выбранное зелье";
      this.string4 = "u";
      this.string2 = "ь";
      this.potionEditorState = potionEditorState;
      this.actionButton = new ActionButton("Заменить", "u", 96.0F, 13.5F, runnable);
      this.actionButton2 = new ActionButton("Удалить", "ь", 96.0F, 13.5F, runnable2);
      this.addChild(this.actionButton);
      this.addChild(this.actionButton2);
      this.updateLayout();
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      return !this.getBounds().contains((float)n, (float)n2)
         ? false
         : super.onMousePressed((int)((float)n - this.getBounds().getX()), (int)((float)n2 - this.getBounds().getY()), n3);
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      Matrix4f matrix4f2 = new Matrix4f(matrix4f).translate(bounds2.getX(), bounds2.getY(), 0.0F);
      PotionCatalogEntry potionCatalogEntry = PotionCatalog.findById(
         this.potionEditorState.getWorkingCopy().getPotionId(this.potionEditorState.getSelectedSlot())
      );
      String string = potionCatalogEntry == null ? "" : potionCatalogEntry.getDisplayName();
      FontRegistry.font2.process2(matrix4f2, drawApi, "Выбранное зелье", 0.0F, 0.0F, 6.0F, ThemeColors.textPlaceholder());
      FontRegistry.font4.process2(matrix4f2, drawApi, string, 0.0F, 8.5F, 8.0F, ThemeColors.textPrimary());
      this.actionButton.render(f, matrix4f2);
      this.actionButton2.render(f, matrix4f2);
      return bounds2.getY() + 36.5F;
   }

   private void updateLayout() {
      this.actionButton.getBounds().setPosition(0.0F, 23.0F);
      this.actionButton2.getBounds().setPosition(98.0F, 23.0F);
   }
}
