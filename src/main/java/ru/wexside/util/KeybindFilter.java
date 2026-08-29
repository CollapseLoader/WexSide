package ru.wexside.util;

import java.util.Arrays;
import java.util.List;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.MultiSelectPopup;
import ru.wexside.misc.PopupManager;
import ru.wexside.misc.PopupOwner;
import ru.wexside.misc.TextLayoutUtils;
import ru.wexside.misc.ThemeColors;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.MultiSelectSetting;
import ru.wexside.setting.MultiSelectSettingBuilder;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.ui.PopupPanel;

public final class KeybindFilter
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider,
   PopupOwner {
   private final MultiSelectPopup multiSelectPopup;
   private float value3;
   private PopupManager popupManager;
   private final MultiSelectSetting multiSelectSetting;

   public KeybindFilter() {
      super(new GuiBounds(0.0F, 0.0F, 90.0F, 13.5F));
      String[] stringArray = Arrays.stream(ModuleCategory.values()).map(ModuleCategory::getName).toArray(x$0 -> new String[x$0]);
      this.multiSelectSetting = ((MultiSelectSettingBuilder)((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder()
               .id("bindings.categories"))
            .name("Категории"))
         .options(stringArray)
         .optionListEnabled(false)
         .build();
      this.multiSelectPopup = new MultiSelectPopup(this.multiSelectSetting);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (n3 == 0 && this.getBounds().contains((float)n, (float)n2)) {
         if (this.popupManager != null) {
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
      this.value3 = FrameInterpolator.lerpTowards(this.value3, this.multiSelectPopup.isActive2() ? 1.0F : 0.0F, 15.0F);
      drawApi.drawRoundedRectangleOutlined(
         matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 8.0F, 0.75F, ThemeColors.controlFill(), ThemeColors.borderPrimary()
      );
      FontRegistry.font3.process5(matrix4f, drawApi, "ч", bounds2.getX() + 4.0F, bounds2.getY() + 3.75F, 6.0F, ThemeColors.textSecondary());
      float f2 = FontRegistry.font3.process3("ч", 6.0F);
      float f3 = bounds2.getX() + 4.0F + f2 + 2.5F;
      String string = TextLayoutUtils.trimToWidth(this.getString(), FontRegistry.font4, 6.0F, 60.0F);
      float f4 = FontRegistry.font4.process4(string, 6.0F);
      float f5 = bounds2.getY() + (bounds2.getHeight() - f4) / 2.0F;
      FontRegistry.font4.process2(matrix4f, drawApi, string, f3, f5, 6.0F, ThemeColors.textSecondary());
      this.process5(matrix4f, drawApi, bounds2);
      return bounds2.getY() + bounds2.getHeight();
   }

   public List<String> getList() {
      return this.multiSelectSetting.getSelectedOptions();
   }

   private String getString() {
      List<String> list = this.multiSelectSetting.getSelectedOptions();
      return list != null && !list.isEmpty() ? String.join(", ", list) : "Все категории";
   }

   private void process5(Matrix4f matrix4f, GuiDrawApi drawApi, GuiBounds bounds2) {
      float f = FontRegistry.font3.process3("F", 6.5F);
      float f2 = FontRegistry.font3.process4("F", 6.5F);
      float f3 = bounds2.getX() + bounds2.getWidth() - f - 4.5F;
      float f4 = bounds2.getY() + (bounds2.getHeight() - f2) / 2.0F;
      float f5 = f3 + f / 2.0F;
      float f6 = f4 + f2 / 2.0F;
      float f7 = 90.0F - 180.0F * this.value3;
      Matrix4f matrix4f2 = new Matrix4f(matrix4f).translate(f5, f6, 0.0F).rotateZ((float)Math.toRadians((double)f7)).translate(-f5, -f6, 0.0F);
      FontRegistry.font3.process5(matrix4f2, drawApi, "F", f3, f4, 6.5F, ThemeColors.textMuted());
   }

   @Override
   public PopupPanel getPopup() {
      return this.multiSelectPopup;
   }

   @Override
   public boolean process6(int n, int n2) {
      return this.getBounds().contains((float)n, (float)n2);
   }

   @Override
   public void update2() {
      GuiBounds bounds2 = this.getBounds();
      this.multiSelectPopup.getBounds().setPosition(bounds2.getX(), bounds2.getY() + bounds2.getHeight() + 1.0F);
   }

   @Override
   public void setPopupManager(PopupManager popupManager) {
      this.popupManager = popupManager;
   }
}
