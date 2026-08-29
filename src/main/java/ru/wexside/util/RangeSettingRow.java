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
import ru.wexside.setting.RangeSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.setting.SettingRow;

public class RangeSettingRow
   extends SettingRow<RangeSetting>
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private CompactSettingRow compactSettingRow;

   public RangeSettingRow(GuiBounds bounds2, RangeSetting rangeSetting, ContainerDisplay containerDisplay) {
      super(bounds2, rangeSetting, new RangeSettingComponent(rangeSetting));
      this.compactSettingRow = new CompactSettingRow(
         new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F), rangeSetting::getDisplayName, rangeSetting.getDescription(), containerDisplay
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
      this.update3();
      float f2 = this.calculateHeight();
      float f3 = this.getFloatType2();
      bounds2.setSize(bounds2.getWidth(), f3);
      this.refreshLayout();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      this.compactSettingRow.process3(matrix4f, drawApi);
      if (this.getSettingComponent() == null) {
         return bounds2.getY() + bounds2.getHeight();
      } else {
         this.getSettingComponent().render(f, matrix4f);
         this.renderRowDecorations(f, matrix4f);
         return bounds2.getY() + bounds2.getHeight();
      }
   }

   @Override
   public void update2() {
      this.setFloatType(this.calculateHeight());
   }

   private float calculateHeight() {
      return this.compactSettingRow.getFloatType2();
   }

   private void setFloatType(float f) {
      if (this.getSettingComponent() != null) {
         GuiBounds bounds2 = this.getBounds();
         this.getSettingComponent().getBounds().setPosition(bounds2.getX(), bounds2.getY() + f);
         this.getSettingComponent().getBounds().setSize(bounds2.getWidth(), this.getSettingComponent().getFloatType2());
         this.updateComponentVisibility();
      }
   }

   private void update3() {
      GuiBounds bounds2 = this.getBounds();
      this.compactSettingRow.setFloatType(Math.max(0.0F, bounds2.getWidth() - 2.0F * this.compactSettingRow.getContentOffset()));
   }

   @Override
   public float getFloatType2() {
      this.update3();
      float f = this.calculateHeight();
      return this.getSettingComponent() == null ? f : f + this.getSettingComponent().getFloatType2();
   }

   @Override
   public void refreshLayout() {
      this.update3();
      GuiBounds bounds2 = this.getBounds();
      float f = this.calculateHeight();
      this.compactSettingRow.getBounds().setPosition(bounds2.getX(), bounds2.getY());
      this.compactSettingRow.getBounds().setSize(bounds2.getWidth(), f);
      this.setFloatType(f);
   }
}
