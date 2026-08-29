package ru.wexside.misc;

import java.util.function.Supplier;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.RgbaColorEditor;

public final class ColorChannelTextAdapter implements TextInputModel {
   private String editingText = "";
   private final int channelIndex;
   private int colorBeforeEditing;
   private final RgbaColorEditor editor;
   private boolean editing;
   private final Supplier<String> labelSupplier;

   public ColorChannelTextAdapter(RgbaColorEditor editor, int channelIndex, Supplier<String> labelSupplier) {
      this.editor = editor;
      this.channelIndex = channelIndex;
      this.labelSupplier = labelSupplier;
   }

   @Override
   public boolean accepts(char character, String currentText) {
      return Character.isDigit(character);
   }

   @Override
   public void onFocusGained() {
      this.editing = true;
      this.colorBeforeEditing = this.editor.getColorSetting().getColor();
      this.editingText = this.readChannelValue();
   }

   @Override
   public int getMaximumLength() {
      return 3;
   }

   @Override
   public void onFocusLost() {
      this.editing = false;
      if (this.editor.getColorSetting().getColor() != this.colorBeforeEditing) {
         this.editor.getColorSetting().addCurrentColorToRecents();
      }

      this.editingText = this.readChannelValue();
   }

   @Override
   public String getText() {
      return this.editing ? this.editingText : this.readChannelValue();
   }

   @Override
   public String getClipboardText() {
      return this.labelSupplier.get();
   }

   @Override
   public void setText(String text) {
      this.editingText = this.normalize(text);
      this.writeChannelValue(this.editingText);
   }

   private String normalize(String text) {
      if (text != null && !text.isBlank()) {
         int value = Integer.parseInt(text);
         return String.valueOf(this.editor.clampChannel(value, 0, 255));
      } else {
         return "";
      }
   }

   private void writeChannelValue(String text) {
      if (text != null && !text.isBlank()) {
         int[] channels = ColorUtils.unpackRgba(this.editor.getColorSetting().getColor());
         channels[this.channelIndex] = this.editor.clampChannel(Integer.parseInt(text), 0, 255);
         this.editor.getColorSetting().setEditingColor(ColorUtils.rgba(channels[0], channels[1], channels[2], channels[3]));
      }
   }

   private String readChannelValue() {
      return String.valueOf(ColorUtils.unpackRgba(this.editor.getColorSetting().getColor())[this.channelIndex]);
   }
}
