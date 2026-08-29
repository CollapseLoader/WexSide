package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.CompactTextFieldStyle;
import ru.wexside.misc.ContainerDisplay;
import ru.wexside.misc.ExpandedTextFieldStyle;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.TextFieldStyle;
import ru.wexside.setting.TextSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.setting.SettingRow;

public final class TextSettingRow
   extends SettingRow<TextSetting>
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final CompactSettingRow compactSettingRow;

   public TextSettingRow(GuiBounds bounds2, TextSetting textSetting, ContainerDisplay containerDisplay) {
      super(
         bounds2,
         textSetting,
         new TextSettingComponent(textSetting, (TextFieldStyle)(textSetting.isExpanded() ? new ExpandedTextFieldStyle() : new CompactTextFieldStyle()))
      );
      this.compactSettingRow = new CompactSettingRow(
         new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F), textSetting::getDisplayName, textSetting.getDescription(), containerDisplay
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
      float f2 = this.compactSettingRow.getFloatType2();
      float f3 = this.getFloatType2();
      bounds2.setSize(bounds2.getWidth(), f3);
      this.refreshLayout();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      this.compactSettingRow.process3(matrix4f, drawApi);
      if (this.getSettingComponent() != null) {
         this.getSettingComponent().render(f, matrix4f);
      }

      this.renderRowDecorations(f, matrix4f);
      return bounds2.getY() + bounds2.getHeight();
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      this.refreshLayout();
      if (this.getSettingComponent() != null) {
         this.getSettingComponent().onMouseReleased(n, n2, n3);
      }
   }

   @Override
   public void update2() {
      this.setFloatType(this.compactSettingRow.getFloatType2());
   }

   private void setFloatType(float f) {
      if (this.getSettingComponent() != null) {
         GuiBounds bounds2 = this.getBounds();
         float f4 = this.getSettingComponent().getFloatType();
         float f5 = this.getSettingComponent().getFloatType2();
         float f2;
         float f3;
         if (this.getSetting().isExpanded()) {
            f3 = bounds2.getX() + bounds2.getWidth() - f4 - 4.0F;
            f2 = bounds2.getY() + (f - f5) / 2.0F + 0.25F;
         } else {
            f3 = bounds2.getX() + (bounds2.getWidth() - f4) / 2.0F;
            f2 = bounds2.getY() + f;
         }

         this.getSettingComponent().getBounds().setPosition(f3, f2);
         this.getSettingComponent().getBounds().setSize(f4, f5);
         this.updateComponentVisibility();
      }
   }

   private void update3() {
      GuiBounds bounds2 = this.getBounds();
      if (this.getSettingComponent() != null && this.getSetting().isExpanded()) {
         float f = this.getSettingComponent().getFloatType();
         float f2 = bounds2.getX() + bounds2.getWidth() - f - 4.0F;
         float f3 = f2 - 4.0F - (bounds2.getX() + this.compactSettingRow.getFloatType5());
         this.compactSettingRow.setFloatType(Math.max(0.0F, f3));
      } else {
         this.compactSettingRow.setFloatType(Math.max(0.0F, bounds2.getWidth() - 2.0F * this.compactSettingRow.getContentOffset()));
      }
   }

   @Override
   public float getFloatType2() {
      this.update3();
      float f = this.compactSettingRow.getFloatType2();
      return this.getSettingComponent() != null && !this.getSetting().isExpanded() ? f + this.getSettingComponent().getFloatType2() : f;
   }

   @Override
   public void refreshLayout() {
      this.update3();
      GuiBounds bounds2 = this.getBounds();
      float f = this.compactSettingRow.getFloatType2();
      this.compactSettingRow.getBounds().setPosition(bounds2.getX(), bounds2.getY());
      this.compactSettingRow.getBounds().setSize(bounds2.getWidth(), f);
      this.setFloatType(f);
   }
}
