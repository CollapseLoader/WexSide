package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.ContainerDisplay;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.setting.SettingRow;

public class MultiSelectSettingRow
   extends SettingRow<MultiSelectSetting>
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private CompactSettingRow compactSettingRow;

   public MultiSelectSettingRow(GuiBounds bounds2, MultiSelectSetting multiSelectSetting, ContainerDisplay containerDisplay) {
      super(bounds2, multiSelectSetting, new MultiSelectSettingComponent(multiSelectSetting));
      this.compactSettingRow = new CompactSettingRow(
         new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F), multiSelectSetting::getDisplayName, multiSelectSetting.getDescription(), containerDisplay
      );
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
      if (this.getSettingComponent() != null) {
         this.getSettingComponent().onMouseScroll(n, n2, d);
      }
   }

   @Override
   public void update() {
      if (this.getSettingComponent() != null) {
         this.getSettingComponent().update();
      }

      this.syncVisibility();
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      this.refreshLayout();
      if (this.handleRowClick(n, n2, n3)) {
         return true;
      } else {
         return this.getSettingComponent() != null && this.getSettingComponent().onMousePressed(n, n2, n3);
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      this.update3();
      float f2 = this.compactSettingRow.getFloatType2();
      bounds2.setSize(bounds2.getWidth(), f2);
      this.refreshLayout();
      this.compactSettingRow.process3(matrix4f, drawApi);
      if (this.getSettingComponent() != null) {
         this.getSettingComponent().render(f, matrix4f);
      }

      this.renderRowDecorations(f, matrix4f);
      return bounds2.getY() + f2;
   }

   @Override
   public void update2() {
      if (this.getSettingComponent() != null) {
         GuiBounds bounds2 = this.getBounds();
         float f = this.getSettingComponent().getFloatType();
         float f2 = this.getSettingComponent().getFloatType2();
         float f3 = bounds2.getX() + bounds2.getWidth() - f - 4.0F;
         float f4 = bounds2.getY() + (bounds2.getHeight() - f2) / 2.0F + 0.25F;
         this.getSettingComponent().getBounds().setPosition(f3, f4);
         this.getSettingComponent().getBounds().setSize(f, f2);
         this.updateComponentVisibility();
      }
   }

   private void update3() {
      GuiBounds bounds2 = this.getBounds();
      if (this.getSettingComponent() == null) {
         this.compactSettingRow.setFloatType(Math.max(0.0F, bounds2.getWidth() - 2.0F * this.compactSettingRow.getContentOffset()));
      } else {
         float f = this.getSettingComponent().getFloatType();
         float f2 = bounds2.getX() + bounds2.getWidth() - f - 4.0F;
         float f3 = f2 - 4.0F - (bounds2.getX() + this.compactSettingRow.getFloatType5());
         this.compactSettingRow.setFloatType(Math.max(0.0F, f3));
      }
   }

   @Override
   public float getFloatType2() {
      this.update3();
      return this.compactSettingRow.getFloatType2();
   }

   @Override
   public void refreshLayout() {
      this.update3();
      GuiBounds bounds2 = this.getBounds();
      float f = this.compactSettingRow.getFloatType2();
      this.compactSettingRow.getBounds().setPosition(bounds2.getX(), bounds2.getY());
      this.compactSettingRow.getBounds().setSize(bounds2.getWidth(), f);
      this.update2();
   }
}
