package ru.wexside.util;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import ru.wexside.misc.AstolfoState;
import ru.wexside.setting.color.ColorChannel;
import ru.wexside.setting.color.ColorIndexReadResult;
import ru.wexside.setting.color.ColorMode;
import ru.wexside.setting.color.ColorPaletteReadResult;

public final class ColorSettingCodec {
   public static final float DEFAULT_HUE_SPEED = 0.01F;
   private final AstolfoColorController astolfoColorController;

   public ColorSettingCodec(AstolfoColorController astolfoColorController) {
      this.astolfoColorController = astolfoColorController;
   }

   public void write(DataOutputStream output, int selectedIndex, ColorValueState[] colors, float[] secondaryHues, int extendedFormatMarker) throws IOException {
      output.writeInt(selectedIndex);

      for(ColorValueState color : colors) {
         output.writeInt(color.getPrimaryColor());
      }

      output.writeInt(extendedFormatMarker);

      for(ColorValueState color : colors) {
         output.writeInt(color.getSecondaryColor());
      }

      for(float hue : secondaryHues) {
         output.writeFloat(hue);
      }

      for(ColorValueState color : colors) {
         output.writeBoolean(color.hasSecondaryColor());
      }

      for(ColorValueState color : colors) {
         output.writeInt(color.getColorMode().ordinal());
      }

      for(ColorValueState color : colors) {
         output.writeInt(color.getEditingChannel().ordinal());
      }

      for(ColorValueState color : colors) {
         output.writeFloat(color.getAstolfoState().getPhaseOffset());
      }

      for(ColorValueState color : colors) {
         output.writeFloat(color.getAstolfoState().getSaturation());
      }

      for(ColorValueState color : colors) {
         output.writeFloat(color.getAstolfoState().getBrightness());
      }

      for(ColorValueState color : colors) {
         output.writeFloat(color.getAstolfoState().getAlpha());
      }

      for(ColorValueState color : colors) {
         output.writeFloat(color.getAstolfoState().getHueSpeed());
      }
   }

   public ColorIndexReadResult readSelectedIndex(DataInputStream input, int colorCount) throws IOException {
      int rawValue = input.readInt();
      return rawValue >= 0 && rawValue < colorCount ? new ColorIndexReadResult(true, rawValue, 0) : new ColorIndexReadResult(false, 0, rawValue);
   }

   public ColorValueState[] readPrimaryColors(DataInputStream input, int colorCount) throws IOException {
      ColorValueState[] colors = new ColorValueState[colorCount];

      for(int index = 0; index < colorCount; ++index) {
         colors[index] = new ColorValueState(input.readInt());
      }

      return colors;
   }

   public ColorPaletteReadResult readPalette(
      DataInputStream input,
      int colorCount,
      int extendedFormatMarker,
      int fallbackSecondaryColor,
      ColorValueState[] originalColors,
      float[] originalSecondaryHues
   ) throws IOException {
      ColorValueState[] colors = (ColorValueState[])originalColors.clone();
      float[] secondaryHues = (float[])originalSecondaryHues.clone();

      try {
         if (input.available() <= 0) {
            return new ColorPaletteReadResult(colors, secondaryHues);
         }

         int format = input.readInt();
         if (format == extendedFormatMarker) {
            this.readExtendedPalette(input, colorCount, fallbackSecondaryColor, colors, secondaryHues);
         } else {
            this.readLegacyPalette(input, colorCount, format, fallbackSecondaryColor, colors, secondaryHues);
         }
      } catch (EOFException var10) {
      }

      return new ColorPaletteReadResult(colors, secondaryHues);
   }

   private void readExtendedPalette(DataInputStream input, int colorCount, int fallbackSecondaryColor, ColorValueState[] colors, float[] secondaryHues) throws IOException {
      int[] secondaryColors = new int[colorCount];
      boolean[] secondaryAssigned = new boolean[colorCount];
      ColorMode[] modes = new ColorMode[colorCount];
      ColorChannel[] channels = new ColorChannel[colorCount];
      float[] phaseOffsets = new float[colorCount];
      float[] saturations = new float[colorCount];
      float[] brightness = new float[colorCount];
      float[] alpha = new float[colorCount];
      float[] hueSpeeds = new float[colorCount];
      Arrays.fill(hueSpeeds, 0.01F);

      for(int i = 0; i < colorCount; ++i) {
         secondaryColors[i] = input.readInt();
      }

      for(int i = 0; i < colorCount; ++i) {
         secondaryHues[i] = input.readFloat();
      }

      for(int i = 0; i < colorCount; ++i) {
         secondaryAssigned[i] = input.readBoolean();
      }

      for(int i = 0; i < colorCount; ++i) {
         modes[i] = normalizeMode(ColorMode.fromOrdinal(input.readInt()));
      }

      for(int i = 0; i < colorCount; ++i) {
         channels[i] = normalizeChannel(modes[i], ColorChannel.fromOrdinal(input.readInt()));
      }

      for(int i = 0; i < colorCount; ++i) {
         phaseOffsets[i] = wrapHue(input.readFloat());
      }

      for(int i = 0; i < colorCount; ++i) {
         saturations[i] = clamp01(input.readFloat());
      }

      for(int i = 0; i < colorCount; ++i) {
         brightness[i] = clamp01(input.readFloat());
      }

      for(int i = 0; i < colorCount; ++i) {
         alpha[i] = clamp01(input.readFloat());
      }

      try {
         for(int i = 0; i < colorCount; ++i) {
            hueSpeeds[i] = input.readFloat();
         }
      } catch (EOFException var16) {
      }

      for(int i = 0; i < colorCount; ++i) {
         colors[i] = new ColorValueState(
            colors[i].getPrimaryColor(),
            secondaryColors[i],
            secondaryAssigned[i],
            modes[i],
            channels[i],
            new AstolfoState(phaseOffsets[i], hueSpeeds[i], saturations[i], brightness[i], alpha[i])
         );
         if (!secondaryAssigned[i]) {
            secondaryHues[i] = hueOf(fallbackSecondaryColor, secondaryHues[i]);
         }
      }
   }

   private void readLegacyPalette(
      DataInputStream input, int colorCount, int modeOrdinal, int fallbackSecondaryColor, ColorValueState[] colors, float[] secondaryHues
   ) throws IOException {
      ColorMode mode = normalizeMode(ColorMode.fromOrdinal(modeOrdinal));
      ColorChannel channel = normalizeChannel(mode, ColorChannel.fromOrdinal(input.readInt()));
      this.readLegacySecondaryColors(input, colorCount, mode, fallbackSecondaryColor, colors, secondaryHues);
      AstolfoState sharedAstolfoState = this.readLegacyAstolfoState(input);

      for(int i = 0; i < colorCount; ++i) {
         ColorValueState state = colors[i].withColorMode(mode).withEditingChannel(channel);
         if (sharedAstolfoState != null) {
            state = state.withAstolfoState(sharedAstolfoState);
         } else if (mode == ColorMode.ASTOLFO) {
            state = state.withAstolfoState(this.astolfoColorController.fromColor(state.getAstolfoState(), state.getPrimaryColor()));
         }

         colors[i] = state;
      }
   }

   private void readLegacySecondaryColors(
      DataInputStream input, int colorCount, ColorMode mode, int fallbackSecondaryColor, ColorValueState[] colors, float[] secondaryHues
   ) throws IOException {
      int completePaletteSize = colorCount * 4 + colorCount * 4 + colorCount;
      if (input.available() < completePaletteSize) {
         int secondaryColor = input.readInt();
         float secondaryHue = input.readFloat();
         boolean assigned = mode == ColorMode.DOUBLE_COLOR;

         try {
            assigned = input.readBoolean();
         } catch (EOFException var13) {
         }

         for(int i = 0; i < colorCount; ++i) {
            int color = assigned ? secondaryColor : fallbackSecondaryColor;
            colors[i] = colors[i].withSecondaryColor(color, assigned);
            secondaryHues[i] = assigned ? secondaryHue : hueOf(color, secondaryHue);
         }
      } else {
         for(int i = 0; i < colorCount; ++i) {
            colors[i] = colors[i].withSecondaryColor(input.readInt(), colors[i].hasSecondaryColor());
         }

         for(int i = 0; i < colorCount; ++i) {
            secondaryHues[i] = input.readFloat();
         }

         for(int i = 0; i < colorCount; ++i) {
            boolean assigned = input.readBoolean();
            colors[i] = colors[i].withSecondaryColor(colors[i].getSecondaryColor(), assigned);
            if (!assigned) {
               secondaryHues[i] = hueOf(fallbackSecondaryColor, secondaryHues[i]);
            }
         }
      }
   }

   private AstolfoState readLegacyAstolfoState(DataInputStream input) throws IOException {
      return input.available() < 16
         ? null
         : new AstolfoState(wrapHue(input.readFloat()), 0.01F, clamp01(input.readFloat()), clamp01(input.readFloat()), clamp01(input.readFloat()));
   }

   private static ColorMode normalizeMode(ColorMode mode) {
      return mode == null ? ColorMode.STATIC : mode;
   }

   private static ColorChannel normalizeChannel(ColorMode mode, ColorChannel channel) {
      if (normalizeMode(mode) != ColorMode.DOUBLE_COLOR) {
         return ColorChannel.PRIMARY;
      } else {
         return channel == null ? ColorChannel.PRIMARY : channel;
      }
   }

   private static float hueOf(int argb, float fallback) {
      float[] hsb = Color.RGBtoHSB(argb >> 16 & 0xFF, argb >> 8 & 0xFF, argb & 0xFF, null);
      return hsb.length > 0 ? hsb[0] : fallback;
   }

   private static float wrapHue(float value) {
      float wrapped = value % 1.0F;
      return wrapped < 0.0F ? wrapped + 1.0F : wrapped;
   }

   private static float clamp01(float value) {
      return Math.max(0.0F, Math.min(1.0F, value));
   }
}
