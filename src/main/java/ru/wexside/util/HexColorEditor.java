package ru.wexside.util;

import java.util.function.Supplier;
import ru.wexside.misc.AbstractColorTextEditor;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.TextField;
import ru.wexside.setting.ColorSetting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.HexColorTextCallback;

final class HexColorEditor
   extends AbstractColorTextEditor
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final float value = 12.0F;
   private final TextField textField;
   private final float value2;
   private final float value3;
   private final float value4 = 1.0F;
   private final TextField textField2;
   private final ColorSetting colorSetting;

   HexColorEditor(GuiBounds bounds2, ColorSetting colorSetting) {
      super(bounds2);
      this.value2 = 49.5F;
      this.value3 = 20.0F;
      this.colorSetting = colorSetting;
      Supplier<String> supplier = this::getString;
      this.textField2 = new TextField(new GuiBounds(0.0F, 0.0F, 49.5F, 12.0F), new HexColorTextCallback(colorSetting));
      this.textField = new TextField(new GuiBounds(50.5F, 0.0F, 20.0F, 12.0F), new AlphaPercentageTextAdapter(this, supplier), "%");
      this.addChild(this.textField2);
      this.addChild(this.textField);
      this.getBounds().setSize(this.getFloatType(), this.getFloatType2());
   }

   @Override
   public float getFloatType() {
      return 70.5F;
   }

   int clampPercentage(int value) {
      return Math.max(0, Math.min(100, value));
   }

   private String getString() {
      String string = this.getAlphaPercentage();
      String string2 = this.colorSetting.getAstolfoHex();
      return string2 + " " + string + "%";
   }

   String getAlphaPercentage() {
      int n = ColorUtils.unpackRgba(this.colorSetting.getColor())[3];
      return String.valueOf(Math.round((float)n / 255.0F * 100.0F));
   }

   ColorSetting getColorSetting() {
      return this.colorSetting;
   }

   @Override
   public float getFloatType2() {
      return 12.0F;
   }
}
