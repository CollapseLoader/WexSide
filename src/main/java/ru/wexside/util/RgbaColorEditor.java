package ru.wexside.util;

import java.util.function.Supplier;
import ru.wexside.misc.AbstractColorTextEditor;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.ColorChannelTextAdapter;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.TextField;
import ru.wexside.setting.ColorSetting;
import ru.wexside.ui.GuiBounds;

public final class RgbaColorEditor
   extends AbstractColorTextEditor
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final TextField textField3;
   private final TextField textField4;
   private final TextField textField5;
   private final TextField textField6;
   private final ColorSetting colorSetting;

   public RgbaColorEditor(GuiBounds bounds2, ColorSetting colorSetting) {
      super(bounds2);
      this.colorSetting = colorSetting;
      Supplier<String> supplier = this::getString;
      this.textField5 = new TextField(new GuiBounds(0.0F, 0.0F, 16.875F, 12.0F), new ColorChannelTextAdapter(this, 0, supplier));
      this.textField6 = new TextField(new GuiBounds(17.875F, 0.0F, 16.875F, 12.0F), new ColorChannelTextAdapter(this, 1, supplier));
      this.textField4 = new TextField(new GuiBounds(35.75F, 0.0F, 16.875F, 12.0F), new ColorChannelTextAdapter(this, 2, supplier));
      this.textField3 = new TextField(new GuiBounds(53.625F, 0.0F, 16.875F, 12.0F), new ColorChannelTextAdapter(this, 3, supplier));
      this.addChild(this.textField5);
      this.addChild(this.textField6);
      this.addChild(this.textField4);
      this.addChild(this.textField3);
      this.getBounds().setSize(this.getFloatType(), this.getFloatType2());
   }

   @Override
   public float getFloatType() {
      return 70.5F;
   }

   public int clampChannel(int n, int n2, int n3) {
      return Math.max(n2, Math.min(n3, n));
   }

   public ColorSetting getColorSetting() {
      return this.colorSetting;
   }

   private String getString() {
      int[] nArray = ColorUtils.unpackRgba(this.colorSetting.getColor());
      int n = nArray[3];
      int n2 = nArray[2];
      int n3 = nArray[1];
      int n4 = nArray[0];
      return "rgba(" + n4 + ", " + n3 + ", " + n2 + ", " + n + ")";
   }

   @Override
   public float getFloatType2() {
      return 12.0F;
   }
}
