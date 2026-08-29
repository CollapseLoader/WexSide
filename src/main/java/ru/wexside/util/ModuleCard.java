package ru.wexside.util;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.ContainerColumnLayout;
import ru.wexside.misc.ContainerDisplay;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.ModuleTogglePulse;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.SettingKeybindButton;
import ru.wexside.misc.SettingRowFactory;
import ru.wexside.misc.SettingsColumnLayout;
import ru.wexside.misc.ThemeColors;
import ru.wexside.misc.ThemeManager;
import ru.wexside.module.Module;
import ru.wexside.setting.BindSetting;
import ru.wexside.setting.Setting;
import ru.wexside.ui.FloatingPanel;
import ru.wexside.ui.FloatingPanelProvider;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.ui.setting.SettingComponent;
import ru.wexside.ui.setting.SettingRow;

public final class ModuleCard
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private int slot;
   private final List<GuiElement> settingElements = new ArrayList<>();
   private final SettingsContainer settingsContainer;
   private float value2;
   private final ExpandButton expandButton;
   private SettingKeybindButton settingKeybindButton;
   private final SettingDescriptionRenderer settingDescriptionRenderer;
   private final Module module;
   private final SettingComponent<?> settingComponent;
   private final ContainerDisplay containerDisplay;

   public ModuleCard(GuiBounds bounds2, Module module, ContainerDisplay containerDisplay) {
      super(bounds2);
      this.module = module;
      this.containerDisplay = containerDisplay;
      this.expandButton = new ExpandButton(new GuiBounds(0.0F, 0.0F, 13.0F, 11.0F));
      this.settingsContainer = new SettingsContainer(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F));
      this.settingDescriptionRenderer = new SettingDescriptionRenderer(bounds2, module.getDisplayName(), module.getDescription(), containerDisplay);
      this.settingElements.add(this.expandButton);
      SettingRow<?> headerSettingRow = null;
      Setting keybindHeaderSetting = null;

      for(Setting setting : module.getSettings()) {
         if (setting instanceof BindSetting) {
            continue;
         }

         SettingRow<?> row = SettingRowFactory.process(setting, containerDisplay);
         if (row != null) {
            if (row.isHeaderControl()) {
               headerSettingRow = row;
            } else {
               this.settingsContainer.addChild(row);
            }
         }

         if (keybindHeaderSetting == null && setting.hasKeybind()) {
            keybindHeaderSetting = setting;
         }
      }

      SettingComponent<?> headerControl = null;
      if (headerSettingRow != null && headerSettingRow.getSettingComponent() != null) {
         headerControl = headerSettingRow.getSettingComponent();
         this.settingElements.add(headerControl);
      }

      this.settingComponent = headerControl;
      if (keybindHeaderSetting != null && keybindHeaderSetting.getKeybind() != null) {
         this.settingKeybindButton = new SettingKeybindButton(keybindHeaderSetting);
         super.addChild(this.settingKeybindButton);
      }

      super.addChild(this.settingsContainer);
      this.slot = module.getSettings().size();
      this.update5();
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
      if (this.isActive()) {
         this.settingsContainer.setSettingsColumnLayout(this.getSettingsColumnLayout());
         this.settingsContainer.onMouseScroll(n, n2, d);
      }
   }

   @Override
   public void update() {
      for(GuiElement element2 : this.settingElements) {
         element2.update();
      }

      if (this.settingKeybindButton != null) {
         this.settingKeybindButton.update();
      }

      this.settingsContainer.setSettingsColumnLayout(this.getSettingsColumnLayout());
      this.settingsContainer.update();
      this.recomputeHeight();
   }

   private void recomputeHeight() {
      float descriptionHeight = this.settingDescriptionRenderer.getFloatType2();
      float value2 = FrameInterpolator.lerpTowards(this.value2, this.isActive() ? 1.0F : 0.0F, 20.0F);
      float f6 = descriptionHeight + 2.0F - 5.0F;
      float f7 = this.module.getDescription().isBlank() ? f6 : 20.0F + this.settingDescriptionRenderer.getFloatType4() + 2.0F - 12.0F;
      float f9 = this.settingsContainer.getFloatType();
      float f10 = Math.max(34.5F, f7 + f9);
      float f11 = descriptionHeight + (f10 - descriptionHeight) * value2;
      this.getBounds().setSize(this.getBounds().getWidth(), f11);
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      this.update5();
      this.settingsContainer.setSettingsColumnLayout(this.getSettingsColumnLayout());
      boolean bl = this.getBounds().contains((float)n, (float)n2);
      this.update6();
      if (bl) {
         for(GuiElement element2 : this.settingElements) {
            if (element2 != this.expandButton || this.isActive2()) {
               boolean bl2 = element2 == this.expandButton && this.expandButton.isActive();
               if (element2.onMousePressed(n, n2, n3)) {
                  if (element2 == this.expandButton && bl2 && !this.expandButton.isActive()) {
                     this.settingsContainer.update2();
                     this.closeFloatingPanels(this);
                  }

                  return true;
               }
            }
         }

         if (this.settingKeybindButton != null && this.settingKeybindButton.onMousePressed(n, n2, n3)) {
            return true;
         }

         if (!this.isActive()) {
            return true;
         }
      }

      return bl && this.isActive() && this.settingsContainer.onMousePressed(n, n2, n3) ? true : bl;
   }

   @Override
   public float render(float f, Matrix4f matrix4f2) {
      this.update4();
      this.update5();
      this.settingsContainer.setSettingsColumnLayout(this.getSettingsColumnLayout());
      GuiBounds bounds2 = this.getBounds();
      this.updateHeaderWidth();
      float f2 = 34.5F;
      float f3 = 5.0F;
      float f4 = 12.0F;
      float f5 = this.settingDescriptionRenderer.getFloatType2();
      this.value2 = FrameInterpolator.lerpTowards(this.value2, this.isActive() ? 1.0F : 0.0F, 20.0F);
      float f6 = bounds2.getY() + f5 + 2.0F - f3;
      float f7 = this.module.getDescription().isBlank() ? f6 : bounds2.getY() + 20.0F + this.settingDescriptionRenderer.getFloatType4() + 2.0F - f4;
      float f8 = f6 + (f7 - f6) * this.value2;
      float f9 = this.settingsContainer.getFloatType();
      float f10 = Math.max(f2, f7 - bounds2.getY() + f9);
      float f11 = f5 + (f10 - f5) * this.value2;
      float f12 = Math.max(0.0F, f11 - (f8 - bounds2.getY()));
      float f13 = bounds2.getX() + 2.0F;
      float f14 = bounds2.getWidth() - 4.0F;
      boolean bl = this.settingsContainer.isActive() && f9 > 0.01F && this.value2 > 0.01F;
      boolean bl2 = bl && this.value2 < 0.99F;
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      this.settingsContainer.getBounds().setPosition(f13, f8);
      this.settingsContainer.getBounds().setSize(f14, f12);
      drawApi.beginStencil(1);
      drawApi.drawRoundedRectangle(matrix4f2, bounds2.getX(), bounds2.getY() - 0.25F, bounds2.getWidth(), f11, 8.0F, ColorUtils.rgba(0, 0, 0, 0));
      drawApi.applyStencilMask(1);
      drawApi.drawRoundedRectangleOutlined(
         matrix4f2,
         bounds2.getX(),
         bounds2.getY(),
         bounds2.getWidth(),
         f11,
         8.0F,
         0.75F,
         ColorUtils.withAlpha(ThemeColors.backgroundPrimary(), 0.0F),
         ThemeColors.borderPrimary()
      );
      this.settingDescriptionRenderer.process(matrix4f2, drawApi, this.value2);
      this.update6();

      for(GuiElement element2 : this.settingElements) {
         if (element2 != this.expandButton || this.isActive2()) {
            element2.render(f, matrix4f2);
         }
      }

      if (this.settingKeybindButton != null) {
         this.settingKeybindButton.render(f, matrix4f2);
      }

      if (bl && f12 > 0.01F) {
         float f15 = bl2 ? f9 : f12;
         this.settingsContainer.getBounds().setPosition(0.0F, 0.0F);
         this.settingsContainer.getBounds().setSize(f14, f15);
         ClippedLayerRenderer.process(
            drawApi,
            matrix4f2,
            f13,
            f8,
            f14,
            f9,
            0.0F,
            bl2,
            ColorUtils.withAlpha(-1, 255.0F * this.value2),
            matrix4f -> this.settingsContainer.render(f, matrix4f)
         );
         this.settingsContainer.getBounds().setPosition(f13, f8);
         this.settingsContainer.getBounds().setSize(f14, f12);
      }

      this.settingsContainer.update2();
      float f16 = ModuleTogglePulse.progress(this.module);
      if (f16 > 0.0F) {
         int n = ThemeManager.getThemeManager().isDarkTheme() ? 235 : 140;
         drawApi.drawShimmer(
            matrix4f2, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), f11, 8.0F, f16, ColorUtils.withAlpha(ThemeColors.accent(), (float)n)
         );
      }

      drawApi.endStencil();
      bounds2.setSize(bounds2.getWidth(), f11);
      return bounds2.getY() + f11;
   }

   @Override
   public boolean onCharTyped(char c) {
      for(GuiElement element2 : this.settingElements) {
         if (element2.onCharTyped(c)) {
            return true;
         }
      }

      this.settingsContainer.setSettingsColumnLayout(this.getSettingsColumnLayout());
      return this.isActive() && this.settingsContainer.onCharTyped(c);
   }

   public boolean isActive() {
      return this.expandButton.isActive();
   }

   @Override
   public void update2() {
      for(GuiElement element2 : this.settingElements) {
         element2.update2();
      }

      if (this.settingKeybindButton != null) {
         this.settingKeybindButton.update2();
      }

      this.settingsContainer.update2();
   }

   @Override
   public void setBooleanType(boolean bl) {
      boolean bl2 = this.expandButton.isActive();
      this.expandButton.setExpanded(bl);
      if (bl2 && !bl) {
         this.settingsContainer.update2();
         this.closeFloatingPanels(this);
      }
   }

   @Override
   public boolean onKeyPressed(int n) {
      for(GuiElement element2 : this.settingElements) {
         if (element2.onKeyPressed(n)) {
            return true;
         }
      }

      this.settingsContainer.setSettingsColumnLayout(this.getSettingsColumnLayout());
      return this.isActive() && this.settingsContainer.onKeyPressed(n);
   }

   public float getFloatType() {
      float f = this.settingDescriptionRenderer.getFloatType2();
      if (this.value2 < 0.001F) {
         return f;
      } else {
         float f2 = 34.5F;
         float f3 = 5.0F;
         float f4 = 12.0F;
         float f5 = this.module.getDescription().isBlank() ? f + 2.0F - f3 : 20.0F + this.settingDescriptionRenderer.getFloatType4() + 2.0F - f4;
         float f6 = Math.max(f2, f5 + this.settingsContainer.getFloatType());
         return f + (f6 - f) * this.value2;
      }
   }

   public void collapse() {
      this.setBooleanType(false);
      this.value2 = 0.0F;
      this.getBounds().setSize(this.getBounds().getWidth(), this.settingDescriptionRenderer.getFloatType2());
   }

   public Module getModule() {
      return this.module;
   }

   private void updateHeaderWidth() {
      GuiBounds bounds2 = this.getBounds();
      this.settingDescriptionRenderer.setFloatType(Math.max(0.0F, bounds2.getWidth() - 2.0F * this.settingDescriptionRenderer.getContentOffset()));
   }

   @Override
   public boolean isActive2() {
      return this.settingsContainer.isActive();
   }

   private void update4() {
      List<Setting> list = this.module.getSettings();
      if (list.size() != this.slot) {
         for(int i = this.slot; i < list.size(); ++i) {
            Setting setting2 = list.get(i);
            if (setting2 instanceof BindSetting) {
               continue;
            }
            SettingRow<?> settingRow2 = SettingRowFactory.process(setting2, this.containerDisplay);
            if (settingRow2 != null && !settingRow2.isHeaderControl()) {
               this.settingsContainer.addChild(settingRow2);
            }
         }

         this.slot = list.size();
      }
   }

   private SettingsColumnLayout getSettingsColumnLayout() {
      return this.containerDisplay.getContainerColumnLayout() == ContainerColumnLayout.SINGLE_COLUMN
         ? SettingsColumnLayout.TWO_COLUMNS
         : SettingsColumnLayout.SINGLE_COLUMN;
   }

   private void update5() {
      boolean bl = this.isActive2();
      boolean bl2 = this.expandButton.isActive();
      this.expandButton.setInteractive(bl);
      if (!bl) {
         this.expandButton.setExpanded(false);
         if (bl2) {
            this.settingsContainer.update2();
            this.closeFloatingPanels(this);
         }
      }
   }

   private void closeFloatingPanels(GuiElement element) {
      FloatingPanelProvider provider;
      FloatingPanel panel;
      if (element instanceof FloatingPanelProvider && (panel = (provider = (FloatingPanelProvider)element).getFloatingPanel()) != null) {
         panel.setBooleanType(false);
      }

      for(GuiElement child : element.getChildren()) {
         this.closeFloatingPanels(child);
      }
   }

   private void update6() {
      GuiBounds bounds2 = this.getBounds();
      float f = 7.0F;
      float f2 = 10.5F;
      float f3 = bounds2.getY() + 7.0F;
      float f4 = 5.0F;

      for(GuiElement element2 : this.settingElements) {
         if (element2 != this.expandButton || this.isActive2()) {
            float f5 = element2 == this.expandButton ? 12.5F : ((SettingComponent)element2).getFloatType();
            float f6 = element2 == this.expandButton ? f2 : ((SettingComponent)element2).getFloatType2();
            float f7 = bounds2.getX() + bounds2.getWidth() - f - f5;
            float f8 = f3 + (f2 - f6) / 2.0F;
            element2.getBounds().setPosition(f7, f8);
            element2.getBounds().setSize(f5, f6);
            f += f5 + f4;
         }
      }

      if (this.settingKeybindButton != null) {
         GuiBounds anchor = this.settingComponent != null ? this.settingComponent.getBounds() : this.expandButton.getBounds();
         this.settingKeybindButton.setBounds(anchor);
         GuiBounds keybindBounds = this.settingKeybindButton.getBounds();
         this.settingKeybindButton.process5(keybindBounds.getX(), keybindBounds.getY());
         this.settingKeybindButton
            .process4(keybindBounds.getX(), keybindBounds.getY(), keybindBounds.getWidth(), keybindBounds.getHeight());
      }
   }
}
