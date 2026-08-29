package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.misc.BindActivationModeSelector;
import ru.wexside.misc.BindingVisibility;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.BoundsSupplier;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.PopupManager;
import ru.wexside.misc.PopupOwner;
import ru.wexside.misc.PopupTreeBinder;
import ru.wexside.misc.SettingComponentFactory;
import ru.wexside.misc.ThemeColors;
import ru.wexside.setting.Setting;
import ru.wexside.setting.SettingKeybind;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.ui.PopupPanel;
import ru.wexside.ui.setting.SettingComponent;

public final class SettingKeybindPopup
   extends PopupPanel
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider,
   BoundsSupplier {
   private final LabeledSettingComponent labeledSettingComponent;
   private final LabeledSettingComponent labeledSettingComponent2;
   private final BindActivationModeSelector activationModeSelector;
   private final BindingVisibility bindingVisibility;
   private final LabeledSettingComponent labeledSettingComponent3;
   private boolean enabled;
   private final SettingKeybind settingKeybind;
   private boolean enabled2;

   public SettingKeybindPopup(SettingKeybind settingKeybind) {
      super(new GuiBounds(0.0F, 0.0F, 96.0F, 74.0F));
      this.settingKeybind = settingKeybind;
      this.bindingVisibility = new BindingVisibility(settingKeybind);
      this.labeledSettingComponent = new LabeledSettingComponent("Значение", this.process5(this.bindingVisibility.getSetting()));
      this.labeledSettingComponent3 = new LabeledSettingComponent("Кнопка", this.process5(this.bindingVisibility.getBindSetting()));
      this.labeledSettingComponent2 = new LabeledSettingComponent("Видимость", this.process5(this.bindingVisibility.getBooleanSetting()));
      this.activationModeSelector = new BindActivationModeSelector(
         this.bindingVisibility.getBindActivationMode(), this.bindingVisibility::setBindActivationMode
      );
      this.addChild(this.labeledSettingComponent);
      this.addChild(this.labeledSettingComponent3);
      this.addChild(this.labeledSettingComponent2);
      this.addChild(this.activationModeSelector);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
      int n3 = (int)((float)n - this.getBounds().getX());
      int n4 = (int)((float)n2 - this.getBounds().getY());

      for(GuiElement element2 : this.children) {
         element2.onMouseScroll(n3, n4, d);
      }
   }

   @Override
   public void update() {
      boolean bl = this.isActive2();
      if (bl && !this.enabled2) {
         this.bindingVisibility.loadFromKeybind();
         this.settingKeybind.markEditorInitialized();
      }

      if (bl) {
         this.bindingVisibility.saveToKeybind();

         for(GuiElement element2 : this.children) {
            element2.update();
         }
      } else if (this.enabled2) {
         this.bindingVisibility.saveToKeybind();
      }

      this.enabled2 = bl;
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      int n4 = (int)((float)n - this.getBounds().getX());
      int n5 = (int)((float)n2 - this.getBounds().getY());
      super.onMouseReleased(n4, n5, n3);
   }

   @Override
   public void update2() {
      if (this.isActive2()) {
         this.bindingVisibility.saveToKeybind();
      }

      super.update2();
   }

   public void process2(PopupManager popupManager, PopupOwner callback34) {
      if (!this.enabled && popupManager != null && callback34 != null) {
         PopupTreeBinder.bindTree(this.labeledSettingComponent, popupManager, callback34);
         PopupTreeBinder.bindTree(this.labeledSettingComponent3, popupManager, callback34);
         PopupTreeBinder.bindTree(this.labeledSettingComponent2, popupManager, callback34);
         this.enabled = true;
      }
   }

   @Override
   protected void updateLayout() {
      float f = Math.max(
         this.labeledSettingComponent.getFloatType(), Math.max(this.labeledSettingComponent3.getFloatType(), this.labeledSettingComponent2.getFloatType())
      );
      float f2 = Math.max(96.0F, f + 10.5F);
      float f3 = 21.0F;
      float f4 = f2 - 10.5F;
      this.process4(this.labeledSettingComponent, f4, f3);
      float var5;
      this.process4(this.labeledSettingComponent3, f4, var5 = f3 + this.labeledSettingComponent.getBounds().getHeight() + 4.0F);
      this.process4(this.labeledSettingComponent2, f4, f3 = var5 + this.labeledSettingComponent3.getBounds().getHeight() + 4.0F);
      float var7;
      this.process7(this.activationModeSelector, f4, var7 = f3 + this.labeledSettingComponent2.getBounds().getHeight() + 4.0F);
      this.getBounds().setSize(f2, f3 = var7 + this.activationModeSelector.getBounds().getHeight() + 5.0F);
   }

   @Override
   protected void renderPopup(float f, Matrix4f matrix4f, GuiDrawApi drawApi) {
      GuiBounds bounds2 = this.getBounds();
      drawApi.fillRectangle(matrix4f, 0.0F, 15.0F, bounds2.getWidth(), 0.5F, ThemeColors.borderPrimary());
      FontRegistry.font2.process2(matrix4f, drawApi, "Бинд", 4.0F, 4.0F, 5.75F, ThemeColors.textPlaceholder());
      this.renderChildren(f, matrix4f);
   }

   private void process4(LabeledSettingComponent labeledSettingComponent, float f, float f2) {
      labeledSettingComponent.getBounds().setPosition(5.25F, f2);
      labeledSettingComponent.getBounds().setSize(f, labeledSettingComponent.getFloatType2());
   }

   private SettingComponent<?> process5(Setting setting) {
      SettingComponent<?> settingComponent = SettingComponentFactory.process(setting);
      if (settingComponent == null) {
         String string = setting.getClass().getName();
         throw new IllegalStateException("Unsupported popup control setting: " + string);
      } else {
         return settingComponent;
      }
   }

   private void process7(BindActivationModeSelector selector, float f, float f2) {
      selector.getBounds().setPosition(5.25F, f2);
      selector.getBounds().setSize(f, selector.getFloatType2());
   }

   @Override
   public GuiBounds getBounds() {
      return super.getBounds();
   }
}
