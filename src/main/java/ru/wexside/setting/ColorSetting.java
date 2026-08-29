package ru.wexside.setting;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.misc.AstolfoState;
import ru.wexside.setting.color.ColorChannel;
import ru.wexside.setting.color.ColorIndexReadResult;
import ru.wexside.setting.color.ColorMode;
import ru.wexside.setting.color.ColorPaletteReadResult;
import ru.wexside.util.AstolfoColorController;
import ru.wexside.util.ColorSettingCodec;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.ColorValueState;
import ru.wexside.util.RecentColorPalette;

public final class ColorSetting extends Setting implements ConfigSerializable {
   private static final int COLOR_COUNT = 7;
   private static final int EXTENDED_FORMAT_MARKER = -2;
   private static final long DOUBLE_COLOR_CYCLE_MILLIS = 2000L;
   private static final float SECOND_COLOR_PHASE = 0.5F;
   private final AstolfoColorController astolfoColorController = new AstolfoColorController();
   private final ColorSettingCodec codec = new ColorSettingCodec(this.astolfoColorController);
   private ColorValueState[] colors = createDefaultColors();
   private float[] primaryHues = createHueCache(this.colors);
   private float[] secondaryHues;
   private RecentColorPalette recentColors = createDefaultRecentColors();
   private int selectedIndex;

   ColorSetting(ColorSettingBuilder builder) {
      super(builder);
      this.selectedIndex = clampIndex(builder.getSelectedIndex());
      this.recentColors.addColor(this.getSelectedState().getPrimaryColor());
      this.secondaryHues = this.createFallbackSecondaryHueCache();
      this.applyInitialMode(builder.getColorMode(), builder.getEditingChannel());
   }

   public static ColorSettingBuilder builder() {
      return new ColorSettingBuilder();
   }

   @Override
   protected void readValue(DataInputStream input) throws IOException {
      ColorIndexReadResult indexResult = this.codec.readSelectedIndex(input, 7);
      if (!indexResult.valid()) {
         this.colors = createDefaultColors();
         this.primaryHues = createHueCache(this.colors);
         this.recentColors = createDefaultRecentColors();
         this.selectedIndex = 0;
         this.setPrimaryColor(0, indexResult.rawValue());
         this.recentColors.addColor(this.getPrimaryColor());
         this.secondaryHues = this.createFallbackSecondaryHueCache();
      } else {
         this.selectedIndex = indexResult.index();
         this.colors = this.codec.readPrimaryColors(input, 7);
         this.primaryHues = createHueCache(this.colors);
         this.recentColors = createDefaultRecentColors();
         this.recentColors.addColor(this.getPrimaryColor());
         this.secondaryHues = this.createFallbackSecondaryHueCache();
         ColorPaletteReadResult palette = this.codec.readPalette(input, 7, -2, this.getFallbackSecondaryColor(), this.colors, this.secondaryHues);
         this.colors = palette.colors();
         this.secondaryHues = palette.values();
      }
   }

   @Override
   protected void writeValue(DataOutputStream output) throws IOException {
      this.codec.write(output, this.selectedIndex, this.colors, this.secondaryHues, -2);
   }

   public int getColor() {
      return this.getEditingColor(0.0F);
   }

   public int getColor(float phase) {
      if (this.isAstolfoMode()) {
         return this.getAstolfoColor(this.selectedIndex, phase);
      } else if (this.isDoubleColorMode()) {
         long cycle = Math.max(200L, 2000L / (long)this.getAstolfoSpeedPercent());
         float time = (float)((double)(System.currentTimeMillis() % cycle) / (double)cycle);
         return ColorUtils.lerp(this.getPrimaryColor(), this.getSecondaryColor(), (double)triangleWave(time + phase * 0.5F));
      } else {
         return this.getPrimaryColor();
      }
   }

   public int getEditingColor(float phase) {
      if (this.isAstolfoMode()) {
         return this.getAstolfoColor(this.selectedIndex, phase);
      } else {
         return this.getEffectiveEditingChannel() == ColorChannel.SECONDARY ? this.getSecondaryColor() : this.getPrimaryColor();
      }
   }

   public int getPrimaryColor() {
      return this.isAstolfoMode() ? this.getAstolfoColor(this.selectedIndex, 0.0F) : this.getSelectedState().getPrimaryColor();
   }

   public int getSecondaryColor() {
      if (this.isAstolfoMode()) {
         return this.getAstolfoColor(this.selectedIndex, 0.5F);
      } else {
         ColorValueState state = this.getSelectedState();
         return state.hasSecondaryColor() ? state.getSecondaryColor() : this.getFallbackSecondaryColor();
      }
   }

   public int getStoredPrimaryColor() {
      return this.getSelectedState().getPrimaryColor();
   }

   public int getPrimaryColor(int index) {
      ColorValueState state = this.getState(index);
      return state.getColorMode() == ColorMode.ASTOLFO ? this.getAstolfoColor(index, 0.0F) : state.getPrimaryColor();
   }

   public void setColor(Color color) {
      this.setEditingColor(color == null ? -1 : color.getRGB());
   }

   public void setEditingColor(int color) {
      if (this.isAstolfoMode()) {
         this.applyAstolfoColor(this.selectedIndex, color);
      } else if (this.getEffectiveEditingChannel() == ColorChannel.SECONDARY) {
         this.setSecondaryColor(color);
      } else {
         this.setPrimaryColor(color);
      }
   }

   public void setPrimaryColor(int color) {
      this.setPrimaryColor(this.selectedIndex, color);
   }

   public void setPrimaryColor(int index, int color) {
      int safeIndex = clampIndex(index);
      this.colors[safeIndex] = this.colors[safeIndex].withPrimaryColor(color);
      this.updatePrimaryHue(safeIndex, color);
   }

   public void setSecondaryColor(int color) {
      this.colors[this.selectedIndex] = this.getSelectedState().withSecondaryColor(color, true);
      this.updateSecondaryHue(this.selectedIndex, color);
   }

   public void setPrimaryHsb(int index, float hue, float saturation, float brightness, int alpha) {
      int safeIndex = clampIndex(index);
      float safeHue = clamp01(hue);
      this.primaryHues[safeIndex] = safeHue;
      int rgb = Color.HSBtoRGB(safeHue, clamp01(saturation), clamp01(brightness)) & 16777215;
      this.colors[safeIndex] = this.colors[safeIndex].withPrimaryColor(clampByte(alpha) << 24 | rgb);
   }

   public void setSecondaryHsb(float hue, float saturation, float brightness, int alpha) {
      float safeHue = clamp01(hue);
      this.secondaryHues[this.selectedIndex] = safeHue;
      int rgb = Color.HSBtoRGB(safeHue, clamp01(saturation), clamp01(brightness)) & 16777215;
      this.colors[this.selectedIndex] = this.getSelectedState().withSecondaryColor(clampByte(alpha) << 24 | rgb, true);
   }

   public void setEditingHsb(float hue, float saturation, float brightness, int alpha) {
      if (this.isAstolfoMode()) {
         this.setAstolfoState(this.astolfoColorController.withHsb(this.getAstolfoState(), hue, saturation, brightness, alpha));
         this.synchronizeAstolfoPrimaryColor(this.selectedIndex);
      } else if (this.getEffectiveEditingChannel() == ColorChannel.SECONDARY) {
         this.setSecondaryHsb(hue, saturation, brightness, alpha);
      } else {
         this.setPrimaryHsb(this.selectedIndex, hue, saturation, brightness, alpha);
      }
   }

   public float[] getCurrentHsb() {
      if (this.isAstolfoMode()) {
         return this.astolfoColorController.getCurrentHsb(this.getAstolfoState());
      } else {
         return this.getEffectiveEditingChannel() == ColorChannel.SECONDARY ? this.getSecondaryHsb() : this.getPrimaryHsb();
      }
   }

   public float[] getPrimaryHsb() {
      int color = this.getSelectedState().getPrimaryColor();
      float[] hsb = rgbToHsb(color);
      if (hasVisibleHue(hsb)) {
         this.primaryHues[this.selectedIndex] = hsb[0];
      } else {
         hsb[0] = this.primaryHues[this.selectedIndex];
      }

      return hsb;
   }

   public float[] getSecondaryHsb() {
      int color = this.getSecondaryColor();
      float[] hsb = rgbToHsb(color);
      if (hasVisibleHue(hsb)) {
         this.secondaryHues[this.selectedIndex] = hsb[0];
      } else {
         hsb[0] = this.secondaryHues[this.selectedIndex];
      }

      return hsb;
   }

   public ColorValueState getSelectedState() {
      return this.getState(this.selectedIndex);
   }

   public ColorValueState getState(int index) {
      return this.colors[clampIndex(index)];
   }

   public int getColorCount() {
      return this.colors.length;
   }

   public int getSelectedIndex() {
      return this.selectedIndex;
   }

   public void setSelectedIndex(int index) {
      this.selectedIndex = clampIndex(index);
   }

   public ColorMode getColorMode() {
      return this.getSelectedState().getColorMode();
   }

   public void setColorMode(ColorMode mode) {
      ColorValueState current = this.getSelectedState();
      ColorMode previousMode = current.getColorMode();
      ColorMode normalizedMode = normalizeMode(mode);
      this.colors[this.selectedIndex] = current.withColorMode(normalizedMode);
      if (normalizedMode == ColorMode.ASTOLFO) {
         if (previousMode != ColorMode.ASTOLFO) {
            this.initializeAstolfoFromColor(this.selectedIndex, current.getPrimaryColor());
         }

         this.synchronizeAstolfoPrimaryColor(this.selectedIndex);
      }
   }

   public ColorChannel getEditingChannel() {
      return this.getSelectedState().getEditingChannel();
   }

   public void setEditingChannel(ColorChannel channel) {
      this.colors[this.selectedIndex] = this.getSelectedState().withEditingChannel(channel);
   }

   public ColorChannel getEffectiveEditingChannel() {
      return this.isDoubleColorMode() && this.getEditingChannel() == ColorChannel.SECONDARY ? ColorChannel.SECONDARY : ColorChannel.PRIMARY;
   }

   public boolean isAstolfoMode() {
      return this.getColorMode() == ColorMode.ASTOLFO;
   }

   public boolean isDoubleColorMode() {
      return this.getColorMode() == ColorMode.DOUBLE_COLOR;
   }

   public float getAstolfoHue() {
      return this.astolfoColorController.getCurrentHue(this.getAstolfoState());
   }

   public float getAstolfoPhaseOffset() {
      return this.getAstolfoState().getPhaseOffset();
   }

   public float getAstolfoSaturation() {
      return this.getAstolfoState().getSaturation();
   }

   public float getAstolfoBrightness() {
      return this.getAstolfoState().getBrightness();
   }

   public float getAstolfoAlpha() {
      return this.getAstolfoState().getAlpha();
   }

   public void setAstolfoHue(float hue) {
      this.setAstolfoState(this.astolfoColorController.withCurrentHue(this.getAstolfoState(), hue));
      this.synchronizeAstolfoPrimaryColor(this.selectedIndex);
   }

   public void setAstolfoPhaseOffset(float offset) {
      this.setAstolfoState(this.getAstolfoState().withPhaseOffset(wrapHue(offset)));
      this.synchronizeAstolfoPrimaryColor(this.selectedIndex);
   }

   public void setAstolfoSaturation(float saturation) {
      this.setAstolfoState(this.getAstolfoState().withSaturation(clamp01(saturation)));
      this.synchronizeAstolfoPrimaryColor(this.selectedIndex);
   }

   public void setAstolfoBrightness(float brightness) {
      this.setAstolfoState(this.getAstolfoState().withBrightness(clamp01(brightness)));
      this.synchronizeAstolfoPrimaryColor(this.selectedIndex);
   }

   public void setAstolfoAlpha(float alpha) {
      this.setAstolfoState(this.getAstolfoState().withAlpha(clamp01(alpha)));
      this.synchronizeAstolfoPrimaryColor(this.selectedIndex);
   }

   public int getAstolfoSpeedPercent() {
      return Math.max(1, Math.round(this.getAstolfoState().getHueSpeed() / 0.01F));
   }

   public void setAstolfoSpeedPercent(int percent) {
      this.setAstolfoState(this.getAstolfoState().withHueSpeed((float)Math.max(1, percent) * 0.01F));
   }

   public int getAstolfoGradientColor(float phase) {
      return this.astolfoColorController.getStaticGradientColor(this.getAstolfoState(), phase);
   }

   public RecentColorPalette getRecentColors() {
      return this.recentColors;
   }

   public void addRecentColor(int color) {
      this.recentColors.addColor(color);
   }

   public void addCurrentColorToRecents() {
      this.addRecentColor(this.isAstolfoMode() ? this.getPrimaryColor() : this.getColor());
   }

   public String getDisplayText() {
      return this.isAstolfoMode() ? this.getAstolfoHex() : this.getPrimaryHex();
   }

   public String getModeLabel() {
      return this.isAstolfoMode() ? "Astolfo" : this.getDisplayText();
   }

   public String getAstolfoHex() {
      return toHex(this.isAstolfoMode() ? this.getAstolfoColor(this.selectedIndex, 0.0F) : this.getColor());
   }

   public String getPrimaryHex() {
      return toHex(this.getPrimaryColor());
   }

   public String getSecondaryHex() {
      return toHex(this.getSecondaryColor());
   }

   public ColorSetting copy() {
      ColorSettingBuilder builder = (ColorSettingBuilder)builder().id(this.getId()).name(this.getDisplayName());
      ColorSetting result = builder.selectedIndex(this.selectedIndex).mode(this.getColorMode()).editingChannel(this.getEditingChannel()).build();
      result.restorePayload(this.copyPayload());
      result.primaryHues = (float[])this.primaryHues.clone();
      result.secondaryHues = (float[])this.secondaryHues.clone();
      result.recentColors = this.recentColors.copy();
      return result;
   }

   private void applyInitialMode(ColorMode mode, ColorChannel channel) {
      ColorValueState current = this.getSelectedState();
      this.colors[this.selectedIndex] = current.withColorMode(normalizeMode(mode)).withEditingChannel(channel);
      if (this.isAstolfoMode()) {
         this.initializeAstolfoFromColor(this.selectedIndex, this.getSelectedState().getPrimaryColor());
         this.synchronizeAstolfoPrimaryColor(this.selectedIndex);
      }
   }

   private void applyAstolfoColor(int index, int color) {
      this.initializeAstolfoFromColor(index, color);
      this.synchronizeAstolfoPrimaryColor(index);
   }

   private void initializeAstolfoFromColor(int index, int color) {
      this.colors[index] = this.colors[index].withAstolfoState(this.astolfoColorController.fromColor(this.colors[index].getAstolfoState(), color));
   }

   private void synchronizeAstolfoPrimaryColor(int index) {
      int safeIndex = clampIndex(index);
      int color = this.getAstolfoColor(safeIndex, 0.0F);
      this.colors[safeIndex] = this.colors[safeIndex].withPrimaryColor(color);
      this.updatePrimaryHue(safeIndex, color);
   }

   private int getAstolfoColor(int index, float phase) {
      return this.astolfoColorController.getColor(this.colors[clampIndex(index)].getAstolfoState(), phase);
   }

   private AstolfoState getAstolfoState() {
      return this.getSelectedState().getAstolfoState();
   }

   private void setAstolfoState(AstolfoState state) {
      this.colors[this.selectedIndex] = this.getSelectedState().withAstolfoState(state);
   }

   private void updatePrimaryHue(int index, int color) {
      float[] hsb = rgbToHsb(color);
      if (hasVisibleHue(hsb)) {
         this.primaryHues[index] = hsb[0];
      }
   }

   private void updateSecondaryHue(int index, int color) {
      float[] hsb = rgbToHsb(color);
      if (hasVisibleHue(hsb)) {
         this.secondaryHues[index] = hsb[0];
      }
   }

   private int getFallbackSecondaryColor() {
      return this.recentColors.size() == 0 ? this.getPrimaryColor() : this.recentColors.getColor(Math.min(1, this.recentColors.size() - 1));
   }

   private float[] createFallbackSecondaryHueCache() {
      float[] hues = new float[7];
      float hue = hueOrFallback(this.getFallbackSecondaryColor(), 0.0F);

      for(int i = 0; i < hues.length; ++i) {
         hues[i] = hue;
      }

      return hues;
   }

   private static ColorValueState[] createDefaultColors() {
      return new ColorValueState[]{
         new ColorValueState(new Color(76, 167, 101)),
         new ColorValueState(new Color(232, 116, 33)),
         new ColorValueState(new Color(114, 103, 255)),
         new ColorValueState(new Color(255, 76, 79)),
         new ColorValueState(new Color(255, 204, 94)),
         new ColorValueState(new Color(94, 132, 255)),
         new ColorValueState(new Color(214, 80, 0))
      };
   }

   private static RecentColorPalette createDefaultRecentColors() {
      return new RecentColorPalette(
         -11753627,
         -1543135,
         -9279489,
         -46001,
         -13218,
         -10582785,
         -2732032,
         -1023342,
         -44462,
         -8812853,
         -3238952,
         -4560696,
         -11677471,
         -749647,
         -6982195,
         -8271996,
         -11684180,
         -12631995,
         -5796870,
         -2302756
      );
   }

   private static float[] createHueCache(ColorValueState[] states) {
      float[] hues = new float[states.length];

      for(int i = 0; i < states.length; ++i) {
         hues[i] = hueOrFallback(states[i].getPrimaryColor(), 0.0F);
      }

      return hues;
   }

   private static float hueOrFallback(int color, float fallback) {
      float[] hsb = rgbToHsb(color);
      return hasVisibleHue(hsb) ? hsb[0] : fallback;
   }

   private static float[] rgbToHsb(int color) {
      return Color.RGBtoHSB(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, null);
   }

   private static boolean hasVisibleHue(float[] hsb) {
      return hsb[1] > 1.0E-4F && hsb[2] > 1.0E-4F;
   }

   private static int clampIndex(int index) {
      return Math.clamp((long)index, 0, 6);
   }

   private static int clampByte(int value) {
      return Math.clamp((long)value, 0, 255);
   }

   private static float clamp01(float value) {
      return Math.max(0.0F, Math.min(1.0F, value));
   }

   private static float wrapHue(float value) {
      float wrapped = value % 1.0F;
      return wrapped < 0.0F ? wrapped + 1.0F : wrapped;
   }

   private static float triangleWave(float value) {
      float doubled = wrapHue(value) * 2.0F;
      return doubled > 1.0F ? 2.0F - doubled : doubled;
   }

   private static ColorMode normalizeMode(ColorMode mode) {
      return mode == null ? ColorMode.STATIC : mode;
   }

   private static String toHex(int color) {
      return String.format("#%06X", color & 16777215);
   }
}
