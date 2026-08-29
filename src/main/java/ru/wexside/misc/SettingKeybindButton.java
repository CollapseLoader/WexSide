package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.setting.Setting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.ui.PopupPanel;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.SettingKeybindPopup;

public final class SettingKeybindButton
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider,
   PopupOwner {
   private final Setting setting;
   private final SettingKeybindPopup settingKeybindPopup;
   private final GuiBounds bounds3;
   private PopupManager popupManager;
   private final float value;
   private final GuiBounds bounds4 = new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F);
   private final String string = "Л";
   private final float value2 = 6.75F;
   private float value3;

   public SettingKeybindButton(Setting setting) {
      super(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F));
      this.value = 5.0F;
      this.bounds3 = new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F);
      this.setting = setting;
      this.settingKeybindPopup = new SettingKeybindPopup(setting.getKeybind());
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
   }

   @Override
   public void update() {
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      if (!this.getBounds().contains((float)n, (float)n2)) {
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
      GuiBounds bounds3 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      boolean bl = this.isActive();
      boolean bl2 = this.settingKeybindPopup.isActive2();
      this.value3 = FrameInterpolator.lerpTowards(this.value3, !bl && !bl2 ? 0.0F : 1.0F, 15.0F);
      if (this.value3 <= 0.01F) {
         return bounds3.getY() + bounds3.getHeight();
      } else {
         float f2 = FontRegistry.font3.process3("Л", 6.75F);
         float f3 = FontRegistry.font3.process4("Л", 6.75F);
         float f4 = bounds3.getX() + (bounds3.getWidth() - f2) / 2.0F;
         float f5 = bounds3.getY() + (bounds3.getHeight() - f3) / 2.0F;
         int n = ColorUtils.withAlpha(ThemeColors.textPlaceholder(), 255.0F * this.value3);
         FontRegistry.font3.process5(matrix4f, drawApi, "Л", f4, f5, 6.75F, n);
         return bounds3.getY() + bounds3.getHeight();
      }
   }

   private boolean isActive() {
      GuiBounds bounds3 = GuiInteractionState.getInstance().getRootPanel().getBounds();
      GuiInteractionState guiInteractionState = GuiInteractionState.getInstance();
      float f2 = (float)guiInteractionState.getScaledMouseX() - bounds3.getX();
      float f;
      return this.bounds4.contains(f2, f = (float)guiInteractionState.getScaledMouseY() - bounds3.getY()) || this.bounds3.contains(f2, f);
   }

   public SettingKeybindPopup getSettingKeybindPopup() {
      return this.settingKeybindPopup;
   }

   @Override
   public PopupPanel getPopup() {
      return this.settingKeybindPopup;
   }

   @Override
   public void update2() {
      float f = this.bounds4.getX() + this.bounds4.getWidth() + 4.0F;
      float f2 = this.bounds4.getY();
      this.settingKeybindPopup.getBounds().setPosition(f, f2);
   }

   @Override
   public void setPopupManager(PopupManager popupManager) {
      this.popupManager = popupManager;
      this.settingKeybindPopup.process2(popupManager, this);
   }

   public void process4(float f, float f2, float f3, float f4) {
      this.bounds3.setPosition(f, f2);
      this.bounds3.setSize(f3, f4);
   }

   @Override
   public void setBounds(GuiBounds bounds3) {
      float f = FontRegistry.font3.process3("Л", 6.75F);
      float f2 = FontRegistry.font3.process4("Л", 6.75F);
      float f3 = f * 2.5F;
      float f4 = f2 * 1.5F;
      float f5 = bounds3.getX() - 5.0F - f;
      float f6 = bounds3.getY() + (bounds3.getHeight() - f2) / 2.0F;
      this.getBounds().setPosition(f5 - (f3 - f) / 2.0F, f6 - (f4 - f2) / 2.0F);
      this.getBounds().setSize(f3, f4);
   }

   public void process5(float f, float f2) {
      this.bounds4.setPosition(f, f2);
      this.bounds4.setSize(this.getBounds().getWidth(), this.getBounds().getHeight());
   }
}
