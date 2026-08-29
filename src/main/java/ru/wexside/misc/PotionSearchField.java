package ru.wexside.misc;

import ru.wexside.util.SearchTextField;

public final class PotionSearchField
   extends SearchTextField
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider,
   MouseHitTest {
   static final String string = "Поиск";
   static final float value = 105.5F;

   public PotionSearchField(PotionEditorState potionEditorState) {
      super(new PotionSearchTextAdapter(potionEditorState), () -> true, "Поиск", true);
   }

   public float getFloatType() {
      return 105.5F;
   }
}
