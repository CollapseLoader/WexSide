package ru.wexside.misc;

import ru.wexside.setting.NumberSetting;
import ru.wexside.util.CompactTextField;
import ru.wexside.util.NumericTextEditor;

public final class SingleNumberEditorPopup
   extends NumberEditorPopup
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   public SingleNumberEditorPopup(NumberSetting numberSetting) {
      super(numberSetting.getDisplayName(), new LabeledGuiElement("Значение", new CompactTextField(new NumericTextEditor(numberSetting))));
   }
}
