package ru.wexside.util;

import net.minecraft.class_310;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.BoundsSupplier;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.MultiSelectAnimation;
import ru.wexside.misc.MultiSelectPopup;
import ru.wexside.misc.PopupManager;
import ru.wexside.misc.PopupOwner;
import ru.wexside.misc.ThemeColors;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.ui.PopupPanel;
import ru.wexside.ui.setting.SettingComponent;

public final class MultiSelectSettingComponent
   extends SettingComponent
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider,
   PopupOwner {
   private final MultiSelectPopup multiSelectPopup;
   private PopupManager popupManager;
   private long longType;
   private float value8;
   private boolean enabled;
   private final PopupPlacement popupPlacement;

   public MultiSelectSettingComponent(MultiSelectSetting multiSelectSetting) {
      super(new GuiBounds(0.0F, 0.0F, 55.0F, 14.0F), multiSelectSetting);
      this.multiSelectPopup = new MultiSelectPopup(multiSelectSetting);
      this.popupPlacement = new PopupPlacement(3.0F, 1.0F);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (n3 == 0 && ((MultiSelectSetting)this.getSetting()).getAction() != null && this.process6(this.getBounds()).contains((float)n, (float)n2)) {
         ((MultiSelectSetting)this.getSetting()).getAction().run();
         if (((MultiSelectSetting)this.getSetting()).getAnimation() == MultiSelectAnimation.SPIN) {
            this.longType = System.currentTimeMillis();
         }

         return true;
      } else if (!this.getBounds().contains((float)n, (float)n2)) {
         return false;
      } else {
         if (n3 == 0 && this.popupManager != null) {
            this.popupManager.toggle(this);
         }

         return true;
      }
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      this.enabled = this.multiSelectPopup.isActive2();
      this.value8 = FrameInterpolator.lerpTowards(this.value8, this.enabled ? 1.0F : 0.0F, 15.0F);
      drawApi.drawRoundedRectangleOutlined(
         matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 8.0F, 1.0F, ThemeColors.controlFill(), ThemeColors.borderPrimary()
      );
      this.process7(matrix4f, drawApi, bounds2);
      String string = this.getString();
      float f2 = this.process4(string, bounds2);
      float f3 = bounds2.getY() + 3.25F + (FontRegistry.font4.process4(string, 6.0F) - FontRegistry.font4.process4(string, f2)) / 2.0F;
      FontRegistry.font4.process2(matrix4f, drawApi, string, bounds2.getX() + 5.0F, f3, f2, ColorUtils.withAlpha(ThemeColors.textSecondary(), 255.0F));
      this.process5(matrix4f, drawApi, bounds2);
      return bounds2.getY() + this.getFloatType2();
   }

   @Override
   public float getFloatType() {
      return 55.0F;
   }

   private GuiBounds getContainerBounds() {
      for(GuiElement element2 = this.getParent(); element2 != null; element2 = element2.getParent()) {
         if (element2 instanceof BoundsSupplier callback13) {
            return callback13.getBounds();
         }
      }

      return null;
   }

   public MultiSelectPopup getMultiSelectPopup() {
      return this.multiSelectPopup;
   }

   @Override
   public PopupPanel getPopup() {
      return this.multiSelectPopup;
   }

   private float process4(String string, GuiBounds bounds2) {
      float f = FontRegistry.font3.process3("F", 6.5F);
      float f2 = bounds2.getX() + bounds2.getWidth() - f - 4.5F;
      float f3 = f2 - (bounds2.getX() + 5.0F) - 2.0F;
      float f4 = FontRegistry.font4.process3(string, 6.0F);
      return !(f3 <= 0.0F) && !(f4 <= f3) ? Math.max(4.0F, 6.0F * f3 / f4) : 6.0F;
   }

   private void process5(Matrix4f matrix4f, GuiDrawApi drawApi, GuiBounds bounds2) {
      float f = FontRegistry.font3.process3("F", 6.5F);
      float f2 = FontRegistry.font3.process4("F", 6.5F);
      float f3 = bounds2.getX() + bounds2.getWidth() - f - 4.5F;
      float f4 = bounds2.getY() + (bounds2.getHeight() - f2) / 2.0F;
      float f5 = f3 + f / 2.0F;
      float f6 = f4 + f2 / 2.0F;
      float f7 = 90.0F - 180.0F * this.value8;
      Matrix4f matrix4f2 = new Matrix4f(matrix4f).translate(f5, f6, 0.0F).rotateZ((float)Math.toRadians((double)f7)).translate(-f5, -f6, 0.0F);
      FontRegistry.font3.process5(matrix4f2, drawApi, "F", f3, f4, 6.5F, ThemeColors.textMuted());
   }

   private String getString() {
      int n2 = ((MultiSelectSetting)this.getSetting()).getSelectedOptions() == null ? 0 : ((MultiSelectSetting)this.getSetting()).getSelectedOptions().size();
      String[] stringArray = ((MultiSelectSetting)this.getSetting()).getOptions();
      int n = stringArray == null ? 0 : stringArray.length;
      return "Selected " + n2 + "/" + n;
   }

   private float getActionIconRotation() {
      if (((MultiSelectSetting)this.getSetting()).getAnimation() == MultiSelectAnimation.SPIN && this.longType != 0L) {
         float f = (float)(System.currentTimeMillis() - this.longType) / 450.0F;
         if (f >= 1.0F) {
            return 0.0F;
         } else {
            float f2 = 1.0F - (1.0F - f) * (1.0F - f) * (1.0F - f);
            return 360.0F * f2;
         }
      } else {
         return 0.0F;
      }
   }

   private GuiBounds process6(GuiBounds bounds2) {
      return new GuiBounds(bounds2.getX() - 1.0F - 14.0F, bounds2.getY(), 14.0F, 14.0F);
   }

   private void process7(Matrix4f matrix4f, GuiDrawApi drawApi, GuiBounds bounds2) {
      String string = ((MultiSelectSetting)this.getSetting()).getActionLabel();
      if (((MultiSelectSetting)this.getSetting()).getAction() != null && string != null && !string.isEmpty()) {
         GuiBounds bounds3 = this.process6(bounds2);
         float f = FontRegistry.font3.process3(string, 7.0F);
         float f2 = FontRegistry.font3.process4(string, 7.0F);
         float f3 = bounds3.getX() + (bounds3.getWidth() - f) / 2.0F;
         float f4 = bounds3.getY() + (bounds3.getHeight() - f2) / 2.0F;
         Matrix4f matrix4f2 = matrix4f;
         float f5 = this.getActionIconRotation();
         if (f5 != 0.0F) {
            float f6 = f3 + f / 2.0F;
            float f7 = f4 + f2 / 2.0F;
            matrix4f2 = new Matrix4f(matrix4f).translate(f6, f7, 0.0F).rotateZ((float)Math.toRadians((double)f5)).translate(-f6, -f7, 0.0F);
         }

         FontRegistry.font3.process5(matrix4f2, drawApi, string, f3, f4, 7.0F, ThemeColors.textMuted());
      }
   }

   @Override
   public boolean process6(int n, int n2) {
      GuiBounds bounds2 = this.getContainerBounds();
      return bounds2 != null
         ? new GuiBounds(
               bounds2.getX() + this.getBounds().getX(), bounds2.getY() + this.getBounds().getY(), this.getBounds().getWidth(), this.getBounds().getHeight()
            )
            .contains((float)n, (float)n2)
         : new GuiBounds(this.getAbsoluteX(), this.getAbsoluteY(), this.getBounds().getWidth(), this.getBounds().getHeight()).contains((float)n, (float)n2);
   }

   @Override
   public void update2() {
      GuiBounds bounds2 = this.getContainerBounds();
      float anchorX;
      float anchorY;
      if (bounds2 != null) {
         anchorX = bounds2.getX() + this.getBounds().getX();
         anchorY = bounds2.getY() + this.getBounds().getY();
      } else {
         anchorX = this.getAbsoluteX();
         anchorY = this.getAbsoluteY();
      }

      class_310 client = class_310.method_1551();
      float viewportWidth = client.field_1755 == null ? (float)client.method_22683().method_4486() : (float)client.field_1755.field_22789;
      float viewportHeight = client.field_1755 == null ? (float)client.method_22683().method_4502() : (float)client.field_1755.field_22790;
      this.popupPlacement.place(this.multiSelectPopup.getBounds(), anchorX, anchorY, this.getBounds().getHeight(), viewportWidth, viewportHeight);
   }

   @Override
   public void setPopupManager(PopupManager popupManager) {
      this.popupManager = popupManager;
   }

   @Override
   public float getFloatType2() {
      return 14.0F;
   }
}
