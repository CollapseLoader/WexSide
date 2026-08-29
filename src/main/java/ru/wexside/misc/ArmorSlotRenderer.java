package ru.wexside.misc;

import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class ArmorSlotRenderer {
   private final int slot = ColorUtils.rgba(200, 0, 0, 110);
   private final float value3;
   private final int slot2;
   private class_1799 stack;
   private final float value4;
   private final float value5;
   private final class_1304 equipmentSlot;
   private final int slot3;
   private final float value6;
   private final ItemStackRenderer itemStackRenderer;
   private final int slot4 = ColorUtils.rgba(0, 0, 0, 150);
   private final float value7;

   public ArmorSlotRenderer(class_1304 iliiIIiliI2) {
      this.value5 = 8.0F;
      this.value3 = 0.1F;
      this.value6 = 2.0F;
      this.value4 = 1.0F;
      this.value7 = 2.0F;
      this.slot2 = ColorUtils.rgba(0, 200, 0, 255);
      this.slot3 = ColorUtils.rgba(200, 0, 0, 255);
      this.itemStackRenderer = new ItemStackRenderer();
      this.stack = class_1799.field_8037;
      this.equipmentSlot = iliiIIiliI2;
   }

   private void process(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5) {
      float f6 = this.getFloatType3();
      if (!(f6 >= 1.0F)) {
         float f7 = f + 2.0F * f5;
         float f8 = f2 + f4 - 3.0F * f5;
         float f9 = f3 - 4.0F * f5;
         float f10 = 1.0F * f5;
         drawApi.fillRectangle(matrix4f, f7, f8, f9, f10, this.slot4);
         drawApi.fillRectangle(matrix4f, f7, f8, f9 * f6, f10, ColorUtils.lerp(this.slot3, this.slot2, (double)f6));
      }
   }

   private boolean isActive() {
      return System.currentTimeMillis() % 1000L < 500L;
   }

   public void process2(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3) {
      float f4 = this.itemStackRenderer.getFloatType2() * f3;
      float f5 = this.itemStackRenderer.getFloatType() * f3;
      drawApi.drawRoundedRectangle(matrix4f, f, f2, f4, f5, 8.0F * f3, ThemeColors.visualizerSlot());
      if (this.isActive3() && this.isActive()) {
         drawApi.drawRoundedRectangle(matrix4f, f, f2, f4, f5, 8.0F * f3, this.slot);
      }

      this.itemStackRenderer.process(drawApi, matrix4f, f, f2, f3);
      if (this.isActive2()) {
         this.process(drawApi, matrix4f, f, f2, f4, f5, f3);
      }
   }

   public float getFloatType() {
      return this.itemStackRenderer.getFloatType();
   }

   public float getFloatType2() {
      return this.itemStackRenderer.getFloatType2();
   }

   public BakedIconEntry process3(float f) {
      return this.itemStackRenderer.process3(f);
   }

   private boolean isActive2() {
      return !this.stack.method_7960() && this.stack.method_7936() > 0;
   }

   public boolean process4(class_746 player) {
      return player != null && !player.method_6118(this.equipmentSlot).method_7960();
   }

   private float getFloatType3() {
      int n = this.stack.method_7936();
      return n <= 0 ? 1.0F : (float)(n - this.stack.method_7919()) / (float)n;
   }

   private boolean isActive3() {
      return this.isActive2() && this.getFloatType3() < 0.1F;
   }

   public void updateEquipment(class_746 player) {
      this.stack = player == null ? class_1799.field_8037 : player.method_6118(this.equipmentSlot);
      this.itemStackRenderer.setStack(this.stack);
   }
}
