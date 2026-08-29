package ru.wexside.misc;

import ru.wexside.setting.RangeSetting;
import ru.wexside.util.CompactTextField;
import ru.wexside.util.RangeValueTextAdapter;

public final class RangeEditorPopup
   extends NumberEditorPopup
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   public RangeEditorPopup(RangeSetting rangeSetting) {
      super(
         rangeSetting.getDisplayName(),
         new LabeledGuiElement("Мин. значение", new CompactTextField(new RangeValueTextAdapter(rangeSetting, true))),
         new LabeledGuiElement("Макс. значение", new CompactTextField(new RangeValueTextAdapter(rangeSetting, false)))
      );
   }
}
