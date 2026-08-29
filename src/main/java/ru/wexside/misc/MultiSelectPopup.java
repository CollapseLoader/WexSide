package ru.wexside.misc;

import java.util.Arrays;
import java.util.List;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.SelectionPopup;
import ru.wexside.util.ScrollableOptionList;

public final class MultiSelectPopup
   extends SelectionPopup
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private static final float OPTION_WIDTH = 79.0F;
   private String[] cachedOptions;
   private final MultiSelectSetting multiSelectSetting;

   public MultiSelectPopup(MultiSelectSetting multiSelectSetting) {
      super(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F), createOptionList(multiSelectSetting));
      this.multiSelectSetting = multiSelectSetting;
      this.cachedOptions = multiSelectSetting.getOptions();
   }

   private static AbstractOptionRow[] createOptionRows(MultiSelectSetting multiSelectSetting) {
      String[] options = multiSelectSetting.getOptions();
      AbstractOptionRow[] rows = new AbstractOptionRow[options.length];

      for(int i = 0; i < options.length; ++i) {
         rows[i] = new MultiSelectOptionRow(options[i], null, 79.0F);
      }

      return rows;
   }

   @Override
   public void update2() {
      String[] currentOptions = this.multiSelectSetting.getOptions();
      if (!Arrays.equals((Object[])this.cachedOptions, (Object[])currentOptions)) {
         this.cachedOptions = currentOptions;
         this.optionList.setAbstractOptionRow(createOptionRows(this.multiSelectSetting));
      }

      List<String> list = this.multiSelectSetting.getSelectedOptions();

      for(AbstractOptionRow abstractOptionRow : this.optionList.getList()) {
         abstractOptionRow.setBooleanType(list.contains(abstractOptionRow.getString2()));
      }
   }

   @Override
   protected void selectOption(AbstractOptionRow abstractOptionRow) {
      List<String> list = this.multiSelectSetting.getSelectedOptions();
      String string;
      if (list.contains(string = abstractOptionRow.getString2())) {
         list.remove(string);
      } else {
         list.add(string);
      }

      this.update2();
   }

   @Override
   protected void updateSelectionState() {
      this.update2();
   }

   private static ScrollableOptionList createOptionList(MultiSelectSetting multiSelectSetting) {
      return new ScrollableOptionList(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F), createOptionRows(multiSelectSetting));
   }
}
