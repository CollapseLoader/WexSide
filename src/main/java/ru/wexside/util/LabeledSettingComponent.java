package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.ThemeColors;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.ui.setting.SettingComponent;

public final class LabeledSettingComponent
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final String string2;
   private final SettingComponent<?> settingComponent;
   private final float value2 = 6.0F;
   private final float value3 = 6.0F;

   public LabeledSettingComponent(String string, SettingComponent<?> settingComponent2) {
      super(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F));
      this.string2 = string;
      this.settingComponent = settingComponent2;
      this.addChild(settingComponent2);
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
      this.settingComponent.onMouseScroll(n, n2, d);
   }

   @Override
   public void update() {
      this.settingComponent.update();
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      float f2 = bounds2.getX() + bounds2.getWidth() - this.settingComponent.getFloatType();
      float f3 = bounds2.getY() + (bounds2.getHeight() - this.settingComponent.getFloatType2()) / 2.0F;
      this.settingComponent.getBounds().setPosition(f2, f3);
      this.settingComponent.getBounds().setSize(this.settingComponent.getFloatType(), this.settingComponent.getFloatType2());
      float f4 = bounds2.getY() + (bounds2.getHeight() - FontRegistry.font2.process4(this.string2, 6.0F)) / 2.0F;
      FontRegistry.font2.process2(matrix4f, drawApi, this.string2, bounds2.getX(), f4, 6.0F, ThemeColors.textPrimary());
      this.settingComponent.render(f, matrix4f);
      return bounds2.getY() + bounds2.getHeight();
   }

   public float getFloatType() {
      return FontRegistry.font2.process3(this.string2, 6.0F) + 6.0F + this.settingComponent.getFloatType();
   }

   public String getString2() {
      return this.string2;
   }

   public float getSpacing() {
      return 6.0F;
   }

   public float getFloatType3() {
      return 6.0F;
   }

   public SettingComponent<?> getSettingComponent() {
      return this.settingComponent;
   }

   public float getFloatType2() {
      float f = FontRegistry.font2.process4(this.string2, 6.0F);
      return Math.max(f, this.settingComponent.getFloatType2());
   }
}
