package ru.wexside.ui.color;

import java.util.List;
import org.joml.Matrix4f;
import ru.wexside.misc.ColorPicker;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.PopupHeader;
import ru.wexside.setting.ColorSetting;
import ru.wexside.ui.FloatingPanel;
import ru.wexside.ui.GuiBounds;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.ToggleIndicatorRenderer;

public final class ColorPickerPopup extends FloatingPanel {
   private static final float WIDTH = 125.0F;
   private static final float PADDING = 5.0F;
   private static final float SWATCH_SIZE = 7.0F;
   private static final float SWATCH_GAP = 2.0F;
   private final ColorSetting setting;
   private final PopupHeader header;
   private final ColorPicker picker;
   private final List<ToggleIndicatorRenderer> swatches = List.of(new ToggleIndicatorRenderer(), new ToggleIndicatorRenderer());

   public ColorPickerPopup(ColorSetting setting) {
      super(new GuiBounds(0.0F, 0.0F, 125.0F, 172.5F));
      this.setting = setting;
      this.header = new PopupHeader(new GuiBounds(5.0F, 5.0F, 115.0F, 0.0F), "Выбор цвета", "Z", setting.getDisplayName());
      this.picker = new ColorPicker(new GuiBounds(5.0F, 0.0F, 115.0F, 77.0F), setting);
      this.addChild(this.picker);
   }

   @Override
   public void update() {
      this.picker.update();
   }

   @Override
   protected void updateLayout() {
      float labelHeight = FontRegistry.font4.process4(this.getValueLabel(), 5.0F);
      float contentY = Math.max(5.0F + this.header.getFloatType2(), 5.0F + labelHeight + 10.0F) + 5.0F;
      float pickerHeight = this.picker.getFloatType2();
      this.picker.getBounds().setPosition(5.0F, contentY);
      this.picker.getBounds().setSize(115.0F, pickerHeight);
      this.getBounds().setSize(125.0F, contentY + pickerHeight + 12.5F);
   }

   @Override
   protected void renderPanel(float delta, Matrix4f matrix, GuiDrawApi renderer) {
      this.header.BlockHitResult(matrix, renderer);
      this.renderValue(matrix, renderer);
      this.picker.render(delta, matrix);
   }

   private void renderValue(Matrix4f matrix, GuiDrawApi renderer) {
      if (!this.setting.isDoubleColorMode()) {
         String value = this.setting.getDisplayText();
         float width = FontRegistry.font4.process3(value, 5.0F);
         float height = FontRegistry.font4.process4(value, 5.0F);
         float x = 120.0F - width;
         float swatchY = 5.0F + height + 3.0F;
         FontRegistry.font4.process2(matrix, renderer, value, x, 5.0F, 5.0F, this.setting.getColor());
         this.swatches.getFirst().process(matrix, renderer, new GuiBounds(113.0F, swatchY, 7.0F, 7.0F), this.setting.getColor(), true);
      } else {
         String first = this.setting.getPrimaryHex();
         String separator = ", ";
         String second = this.setting.getSecondaryHex();
         float firstWidth = FontRegistry.font4.process3(first, 5.0F);
         float separatorWidth = FontRegistry.font4.process3(separator, 5.0F);
         float totalWidth = firstWidth + separatorWidth + FontRegistry.font4.process3(second, 5.0F);
         float x = 120.0F - totalWidth;
         float swatchY = 5.0F + FontRegistry.font4.process4(first, 5.0F) + 3.0F;
         FontRegistry.font4.process2(matrix, renderer, first, x, 5.0F, 5.0F, this.setting.getPrimaryColor());
         FontRegistry.font4.process2(matrix, renderer, separator, x + firstWidth, 5.0F, 5.0F, this.setting.getPrimaryColor());
         FontRegistry.font4.process2(matrix, renderer, second, x + firstWidth + separatorWidth, 5.0F, 5.0F, this.setting.getSecondaryColor());
         int[] colors = new int[]{this.setting.getPrimaryColor(), this.setting.getSecondaryColor()};
         float swatchesWidth = (float)colors.length * 7.0F + (float)(colors.length - 1) * 2.0F;
         float swatchX = 120.0F - swatchesWidth;

         for(int index = 0; index < colors.length; ++index) {
            this.swatches.get(index).process(matrix, renderer, new GuiBounds(swatchX + (float)index * 9.0F, swatchY, 7.0F, 7.0F), colors[index], true);
         }
      }
   }

   private String getValueLabel() {
      return this.setting.isDoubleColorMode() ? this.setting.getPrimaryHex() + ", " + this.setting.getSecondaryHex() : this.setting.getDisplayText();
   }
}
