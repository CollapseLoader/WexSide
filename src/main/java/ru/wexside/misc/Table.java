package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.PopupPanel;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.ScrollableOptionList;
import ru.wexside.util.ViewModeSelector;

public final class Table
   extends PopupPanel
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final IconOptionRow moduleDescriptionOption;
   private final ViewModeSelector viewModeSelector;
   private final ScrollableOptionList scrollableOptionList;
   private final ViewModeButton viewModeButton2;
   private final ViewModeButton viewModeButton3;
   private final ContainerDisplay containerDisplay;
   private final IconOptionRow settingDescriptionOption;

   public Table(GuiBounds bounds2, ContainerDisplay containerDisplay) {
      super(bounds2);
      this.containerDisplay = containerDisplay;
      this.viewModeButton2 = new ViewModeButton("List", "С");
      this.viewModeButton3 = new ViewModeButton("Table", "ц");
      this.viewModeSelector = new ViewModeSelector(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F), 2, this.viewModeButton2, this.viewModeButton3);
      this.moduleDescriptionOption = new IconOptionRow("Описание модуля", "м", 109.0F);
      this.settingDescriptionOption = new IconOptionRow("Описание настроек", "g", 109.0F);
      this.scrollableOptionList = new ScrollableOptionList(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F), this.moduleDescriptionOption, this.settingDescriptionOption);
      this.update3();
      this.addChild(this.viewModeSelector);
      this.addChild(this.scrollableOptionList);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      boolean bl = super.onMousePressed(n, n2, n3);
      if (bl) {
         this.update4();
      }

      return bl;
   }

   @Override
   public void update2() {
      float f = 21.0F;
      this.viewModeSelector.getBounds().setPosition(5.25F, f);
      float var2;
      this.scrollableOptionList.getBounds().setPosition(3.0F, var2 = f + this.viewModeSelector.getBounds().getHeight() + 3.5F);
      this.getBounds().setSize(this.getBounds().getWidth(), f = var2 + this.scrollableOptionList.getBounds().getHeight() + 3.0F);
   }

   public ScrollableOptionList getScrollableOptionList() {
      return this.scrollableOptionList;
   }

   private void update3() {
      this.viewModeSelector.setIntType2(this.containerDisplay.getContainerColumnLayout() == ContainerColumnLayout.TWO_COLUMNS ? 1 : 0);
      this.moduleDescriptionOption.setBooleanType(this.containerDisplay.isActive());
      this.settingDescriptionOption.setBooleanType(this.containerDisplay.isActive2());
   }

   public ViewModeButton getViewModeButton() {
      return this.viewModeButton3;
   }

   public int getIntType() {
      return this.containerDisplay.getIntType();
   }

   public ViewModeButton getViewModeButton2() {
      return this.viewModeButton2;
   }

   public ViewModeSelector getViewModeSelector() {
      return this.viewModeSelector;
   }

   public ContainerDisplay getContainerDisplay() {
      return this.containerDisplay;
   }

   public IconOptionRow getPrimaryOptionRow() {
      return this.settingDescriptionOption;
   }

   private void update4() {
      this.containerDisplay
         .setContainerColumnLayout(this.viewModeSelector.getIntType2() == 1 ? ContainerColumnLayout.TWO_COLUMNS : ContainerColumnLayout.SINGLE_COLUMN);
      this.containerDisplay.setBooleanType2(this.moduleDescriptionOption.isActive());
      this.containerDisplay.setBooleanType(this.settingDescriptionOption.isActive());
   }

   public IconOptionRow getSecondaryOptionRow() {
      return this.moduleDescriptionOption;
   }

   @Override
   protected void updateLayout() {
      this.update3();
      this.update2();
   }

   @Override
   protected void renderPopup(float f, Matrix4f matrix4f, GuiDrawApi drawApi) {
      GuiBounds bounds2 = this.getBounds();
      drawApi.fillRectangle(matrix4f, 0.0F, 15.0F, bounds2.getWidth(), 0.5F, ThemeColors.borderPrimary());
      FontRegistry.font2.process2(matrix4f, drawApi, "Вид...", 4.0F, 4.0F, 5.75F, ThemeColors.textPlaceholder());
      this.renderChildren(f, matrix4f);
   }

   protected boolean process3(float f) {
      return this.isActive2() && f >= 0.99F;
   }
}
