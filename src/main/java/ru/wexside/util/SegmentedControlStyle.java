package ru.wexside.util;

import java.util.function.Supplier;
import ru.wexside.WexSideClient;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.ThemeColors;

public final class SegmentedControlStyle {
   private float value;
   private float value2;
   private float value3;
   private float value4;
   private MsdfFontRenderer font5;
   private float value5;
   private float value6;
   private MsdfFontRenderer isActive2;
   private float value7;
   private float value8;
   private float value9;
   private float value10;
   private Supplier<GuiDrawApi> supplier;

   public SegmentedControlStyle() {
      this(WexSideClient::getGuiRenderer, FontRegistry.font4);
   }

   public SegmentedControlStyle(Supplier<GuiDrawApi> supplier, MsdfFontRenderer font5) {
      this.supplier = supplier;
      this.font5 = font5;
      this.isActive2 = FontRegistry.font3;
      this.value7 = 21.25F;
      this.value10 = 12.0F;
      this.value2 = 8.0F;
      this.value6 = 8.0F;
      this.value4 = 0.75F;
      this.value9 = 5.5F;
      this.value3 = 6.5F;
      this.value8 = 2.5F;
      this.value = 25.0F;
      this.value5 = 20.0F;
   }

   public SegmentedControlStyle process(float f) {
      this.value5 = Math.max(0.0F, f);
      return this;
   }

   public float getFloatType() {
      return this.value6;
   }

   public SegmentedControlStyle process2(float f) {
      this.value7 = Math.max(0.0F, f);
      return this;
   }

   public GuiDrawApi getGuiDrawApi() {
      return this.supplier == null ? null : this.supplier.get();
   }

   public SegmentedControlStyle process3(float f) {
      this.value = Math.max(0.0F, f);
      return this;
   }

   public int getIntType() {
      return ThemeColors.backgroundControl();
   }

   public SegmentedControlStyle process4(MsdfFontRenderer font5) {
      this.font5 = font5;
      return this;
   }

   public SegmentedControlStyle process5(float f) {
      this.value8 = Math.max(0.0F, f);
      return this;
   }

   public SegmentedControlStyle process6(float f) {
      this.value6 = Math.max(0.0F, f);
      return this;
   }

   public SegmentedControlStyle process7(float f) {
      this.value9 = Math.max(0.1F, f);
      return this;
   }

   public float getFloatType2() {
      return this.value2;
   }

   public float getFloatType3() {
      return this.value3;
   }

   public int getIntType2() {
      return ColorUtils.withAlpha(ThemeColors.backgroundPrimary(), 0.0F);
   }

   public MsdfFontRenderer getMsdfFontRenderer() {
      return this.isActive2;
   }

   public float getFloatType4() {
      return this.value5;
   }

   public int getIntType3() {
      return ThemeColors.hudBackground();
   }

   public Supplier<GuiDrawApi> getSupplier() {
      return this.supplier;
   }

   public float getFloatType5() {
      return this.value7;
   }

   public int getIntType4() {
      return ThemeColors.textMuted();
   }

   public SegmentedControlStyle process8(float f) {
      this.value4 = Math.max(0.0F, f);
      return this;
   }

   public MsdfFontRenderer getMsdfFontRenderer2() {
      return this.font5;
   }

   public SegmentedControlStyle process9(float f) {
      this.value3 = Math.max(0.0F, f);
      return this;
   }

   public SegmentedControlStyle process10(MsdfFontRenderer font5) {
      this.isActive2 = font5;
      return this;
   }

   public SegmentedControlStyle process11(float f) {
      this.value10 = Math.max(0.0F, f);
      return this;
   }

   public float getFloatType6() {
      return this.value4;
   }

   public SegmentedControlStyle process12(float f) {
      this.value2 = Math.max(0.0F, f);
      return this;
   }

   public float getFloatType7() {
      return this.value;
   }

   public float getFloatType8() {
      return this.value8;
   }

   public float getFloatType9() {
      return this.value10;
   }

   public int getIntType5() {
      return ThemeColors.accent();
   }

   public SegmentedControlStyle process13(Supplier<GuiDrawApi> supplier) {
      this.supplier = supplier;
      return this;
   }

   public int getIntType6() {
      return ThemeColors.borderPrimary();
   }

   public float getFloatType10() {
      return this.value9;
   }
}
