package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.ui.PopupPanel;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class LayoutModeButton
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider,
   PopupOwner {
   private Table table;
   private final ModuleBrowser moduleBrowser;
   private float value;
   private boolean enabled;
   private final ContainerDisplay containerDisplay;
   private PopupManager popupManager;
   private final String string2;
   private final String string3 = "р";
   private float value2;

   public LayoutModeButton(GuiBounds bounds2, ContainerDisplay containerDisplay, ModuleBrowser moduleBrowser) {
      super(bounds2);
      this.string2 = "Отображение";
      this.containerDisplay = containerDisplay;
      this.moduleBrowser = moduleBrowser;
      this.value2 = moduleBrowser.isActive2() ? 1.0F : 0.0F;
      this.getBounds().setSize(this.getFloatType(), 11.5F);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (this.moduleBrowser.isActive2() && this.getBounds().contains((float)n, (float)n2)) {
         if (n3 == 0 && this.table != null && this.popupManager != null) {
            this.popupManager.toggle(this);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      this.value2 = FrameInterpolator.lerpTowards(this.value2, this.moduleBrowser.isActive2() ? 1.0F : 0.0F, 45.0F);
      this.value = FrameInterpolator.lerpTowards(this.value, this.isActive3() ? 1.0F : 0.0F, 30.0F);
      if (this.value2 <= 0.01F) {
         return bounds2.getY() + bounds2.getHeight();
      } else {
         int n = (int)(255.0F * this.value2);
         int n2 = ColorUtils.withAlpha(ThemeColors.backgroundHover(), (float)((int)(255.0F * this.value * this.value2)));
         int n3 = ColorUtils.withAlpha(ThemeColors.textSecondary(), (float)n);
         int n4 = ColorUtils.withAlpha(ThemeColors.borderPrimary(), (float)n);
         int n5 = ColorUtils.withAlpha(ThemeColors.textMuted(), (float)n);
         float f2 = 5.75F;
         float f3 = 5.75F;
         float f4 = 4.75F;
         float f5 = 7.0F;
         float f6 = 7.5F;
         float f7 = FontRegistry.font3.process3("р", f2);
         float f8 = FontRegistry.font2.process3("Отображение", f3);
         float f9 = f7 + 3.0F + f8 + 2.0F + f5;
         float f10 = bounds2.getX() + (bounds2.getWidth() - f9) / 2.0F;
         float f11 = bounds2.getY() + (bounds2.getHeight() - FontRegistry.font3.process4("р", f2)) / 2.0F;
         float f12 = f10 + f7 + 3.0F;
         float f13 = bounds2.getY() + (bounds2.getHeight() - FontRegistry.font2.process4("Отображение", f3)) / 2.0F;
         float f14 = f12 + f8 + 3.0F;
         float f15 = bounds2.getY() + (bounds2.getHeight() - f6) / 2.0F;
         String string = Integer.toString(this.containerDisplay.getIntType());
         float f16 = f14 + (f5 - FontRegistry.font2.process3(string, f4)) / 2.0F;
         float f17 = f15 + (f6 - FontRegistry.font2.process4(string, f4)) / 2.0F;
         drawApi.drawRoundedRectangle(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 7.0F, n2);
         drawApi.drawRoundedRectangleOutlined(
            matrix4f,
            bounds2.getX(),
            bounds2.getY(),
            bounds2.getWidth(),
            bounds2.getHeight(),
            7.0F,
            1.0F,
            ColorUtils.withAlpha(ThemeColors.backgroundPrimary(), 0.0F),
            n4
         );
         drawApi.drawRoundedRectangleOutlined(matrix4f, f14, f15, f5, f6, 5.0F, 1.0F, ColorUtils.withAlpha(ThemeColors.backgroundPrimary(), 0.0F), n4);
         FontRegistry.font3.process5(matrix4f, drawApi, "р", f10, f11, f2, n3);
         FontRegistry.font2.process2(matrix4f, drawApi, "Отображение", f12, f13, f3, n3);
         FontRegistry.font2.process2(matrix4f, drawApi, string, f16, f17, f4, n5);
         return bounds2.getY() + bounds2.getHeight();
      }
   }

   public float getFloatType() {
      float f = 5.75F;
      float f2 = 5.75F;
      float f3 = 7.0F;
      float f4 = FontRegistry.font3.process3("р", f);
      float f5 = FontRegistry.font2.process3("Отображение", f2);
      return f4 + 3.0F + f5 + 2.0F + f3 + 6.0F;
   }

   public void setTable(Table table) {
      this.table = table;
   }

   public boolean isActive3() {
      this.enabled = this.table != null && this.table.isActive2();
      return this.enabled;
   }

   public Table getTable() {
      return this.table;
   }

   @Override
   public PopupPanel getPopup() {
      return this.table;
   }

   @Override
   public void update2() {
      if (this.table != null) {
         this.table.getBounds().setPosition(this.getBounds().getX() + this.getBounds().getWidth() + 4.0F, this.getBounds().getY());
      }
   }

   @Override
   public void setPopupManager(PopupManager popupManager) {
      this.popupManager = popupManager;
   }
}
