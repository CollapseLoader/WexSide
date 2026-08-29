package ru.wexside.util;

import java.awt.Color;
import java.util.Objects;
import ru.wexside.misc.AstolfoState;
import ru.wexside.setting.color.ColorChannel;
import ru.wexside.setting.color.ColorMode;

public final class ColorValueState {
   private final int primaryColor;
   private final int secondaryColor;
   private final boolean secondaryAssigned;
   private final ColorMode colorMode;
   private final ColorChannel editingChannel;
   private final AstolfoState astolfoState;

   public ColorValueState(Color color) {
      this(color == null ? -1 : color.getRGB());
   }

   public ColorValueState(int color) {
      this(color, color, false, ColorMode.STATIC, ColorChannel.PRIMARY, new AstolfoState());
   }

   public ColorValueState(
      int primaryColor, int secondaryColor, boolean secondaryAssigned, ColorMode colorMode, ColorChannel editingChannel, AstolfoState astolfoState
   ) {
      this.colorMode = colorMode == null ? ColorMode.STATIC : colorMode;
      this.editingChannel = normalizeChannel(this.colorMode, editingChannel);
      this.astolfoState = astolfoState == null ? new AstolfoState() : astolfoState;
      this.primaryColor = primaryColor;
      this.secondaryColor = secondaryColor;
      this.secondaryAssigned = secondaryAssigned;
   }

   public int getPrimaryColor() {
      return this.primaryColor;
   }

   public int getSecondaryColor() {
      return this.secondaryColor;
   }

   public boolean hasSecondaryColor() {
      return this.secondaryAssigned;
   }

   public ColorMode getColorMode() {
      return this.colorMode;
   }

   public ColorChannel getEditingChannel() {
      return this.editingChannel;
   }

   public AstolfoState getAstolfoState() {
      return this.astolfoState;
   }

   public ColorValueState withPrimaryColor(int color) {
      return new ColorValueState(color, this.secondaryColor, this.secondaryAssigned, this.colorMode, this.editingChannel, this.astolfoState);
   }

   public ColorValueState withSecondaryColor(int color, boolean assigned) {
      return new ColorValueState(this.primaryColor, color, assigned, this.colorMode, this.editingChannel, this.astolfoState);
   }

   public ColorValueState withColorMode(ColorMode mode) {
      return new ColorValueState(this.primaryColor, this.secondaryColor, this.secondaryAssigned, mode, this.editingChannel, this.astolfoState);
   }

   public ColorValueState withEditingChannel(ColorChannel channel) {
      return new ColorValueState(this.primaryColor, this.secondaryColor, this.secondaryAssigned, this.colorMode, channel, this.astolfoState);
   }

   public ColorValueState withAstolfoState(AstolfoState state) {
      return new ColorValueState(this.primaryColor, this.secondaryColor, this.secondaryAssigned, this.colorMode, this.editingChannel, state);
   }

   private static ColorChannel normalizeChannel(ColorMode mode, ColorChannel channel) {
      if (mode != ColorMode.DOUBLE_COLOR) {
         return ColorChannel.PRIMARY;
      } else {
         return channel == null ? ColorChannel.PRIMARY : channel;
      }
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof ColorValueState)) {
         return false;
      } else {
         ColorValueState other = (ColorValueState)object;
         return this.primaryColor == other.primaryColor
            && this.secondaryColor == other.secondaryColor
            && this.secondaryAssigned == other.secondaryAssigned
            && this.colorMode == other.colorMode
            && this.editingChannel == other.editingChannel
            && Objects.equals(this.astolfoState, other.astolfoState);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.primaryColor, this.secondaryColor, this.secondaryAssigned, this.colorMode, this.editingChannel, this.astolfoState);
   }

   @Override
   public String toString() {
      return "ColorValueState[primaryColor="
         + this.primaryColor
         + ", secondaryColor="
         + this.secondaryColor
         + ", secondaryAssigned="
         + this.secondaryAssigned
         + ", colorMode="
         + this.colorMode
         + ", editingChannel="
         + this.editingChannel
         + ", astolfoState="
         + this.astolfoState
         + "]";
   }
}
