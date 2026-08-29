package ru.wexside.util;

import java.util.function.BooleanSupplier;
import net.minecraft.class_1796;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_490;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import ru.wexside.misc.BakedIconEntry;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.ItemBindBox;
import ru.wexside.misc.ThemeColors;
import ru.wexside.render.HudIconRenderer;
import ru.wexside.render.IconAtlasEntry;

public final class ItemStatusTile {
   private final float value;
   private int slot = -1;
   private final float value2;
   static final long member8972 = 50000000L;
   private final float value3;
   private final class_1799 process9;
   private boolean enabled;
   private float value4;
   private final float value5;
   private final float value6;
   private final float value7;
   private final BooleanSupplier booleanSupplier;
   private final int slot2;
   private float value8;
   private float value9;
   private int slot3;
   private final float value10;
   private final float value11;
   private final float value12;
   private final float value13;
   private final float value14;
   private final int slot4 = ColorUtils.rgba(227, 227, 227, 255);
   private float value15;
   private final IconAtlasEntry renderPipeline10;
   private final int slot5;
   private class_1799 displayedStack;
   private final float value16;
   private final ItemBindBox itemBindBox;
   private final float value17;
   private final int slot6;
   private final int slot7 = ColorUtils.rgba(255, 204, 94, 255);
   private final float value18;
   private long longType;
   private float value19;
   private final float value20;

   public ItemStatusTile(ItemBindBox itemBindBox, BooleanSupplier booleanSupplier) {
      this.value13 = 21.0F;
      this.value10 = 10.0F;
      this.value5 = 8.0F;
      this.value2 = 1.5F;
      this.value17 = 4.5F;
      this.value6 = 1.5F;
      this.value16 = 4.0F;
      this.value20 = 20.0F;
      this.value = 0.2F;
      this.value7 = 0.3F;
      this.value11 = 1.0F;
      this.value18 = 0.1F;
      this.value12 = 0.35F;
      this.value14 = 0.5F;
      this.value3 = 0.3F;
      this.slot6 = 1;
      this.slot5 = ColorUtils.rgba(76, 167, 101, 255);
      this.slot2 = ColorUtils.rgba(255, 82, 82, 255);
      this.renderPipeline10 = new IconAtlasEntry();
      this.itemBindBox = itemBindBox;
      this.displayedStack = this.process9 = new class_1799(itemBindBox.getItem());
      this.booleanSupplier = booleanSupplier;
   }

   private int getBackgroundColor() {
      int n = ThemeColors.visualizerSlot();
      float f = 51.0F;
      if (this.value4 > 0.001F) {
         n = ColorUtils.lerp(n, ColorUtils.withAlpha(this.slot7, f), (double)this.value4);
      }

      if (this.value15 > 0.001F) {
         n = ColorUtils.lerp(n, ColorUtils.withAlpha(this.slot5, f), (double)this.value15);
      }

      if (this.value19 > 0.001F) {
         n = ColorUtils.lerp(n, ColorUtils.withAlpha(this.slot2, f), (double)this.value19);
      }

      return n;
   }

   public void update() {
      class_310 mc = class_310.method_1551();
      class_746 player2 = mc.field_1724;
      if (player2 == null) {
         this.update2();
      } else {
         this.slot3 = this.process10(player2);
         boolean bl = this.slot3 > 0;
         boolean bl2 = mc.field_1755 instanceof class_490;
         this.enabled = this.itemBindBox.isActive() && (bl2 || bl || this.booleanSupplier.getAsBoolean());
         boolean bl3 = this.process5(mc, player2);
         boolean bl4 = this.process4(player2);
         boolean bl5 = !bl && !bl2;
         this.value4 = FrameInterpolator.lerpTowards(this.value4, bl3 ? 1.0F : 0.0F, 20.0F);
         this.value15 = FrameInterpolator.lerpTowards(this.value15, bl4 ? 1.0F : 0.0F, 20.0F);
         this.value19 = FrameInterpolator.lerpTowards(this.value19, bl5 ? 1.0F : 0.0F, 20.0F);
      }
   }

   public boolean isActive() {
      return this.enabled;
   }

   public void process(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3) {
      float f4 = 21.0F * f3;
      float f5 = 10.0F * f3;
      drawApi.drawRoundedRectangle(matrix4f, f, f2, f4, f4, f5, this.getBackgroundColor());
      if (this.value4 > 0.001F && this.value9 > 0.0F) {
         this.process6(drawApi, matrix4f, f, f2, f4, f5, this.value9, ColorUtils.withAlpha(this.slot7, 76.5F * this.value4));
      }

      if (this.value15 > 0.001F && this.value8 > 0.0F) {
         this.process6(drawApi, matrix4f, f, f2, f4, f5, this.value8, ColorUtils.withAlpha(this.slot5, 76.5F * this.value15));
      }

      drawApi.drawRoundedOutline(matrix4f, f, f2, f4, f4, f5, 1.0F * f3, this.getIntType());
      this.process8(drawApi, matrix4f, f, f2, f3, f4);
   }

   public BakedIconEntry process2(float f) {
      if (!this.renderPipeline10.process(f)) {
         return null;
      } else {
         class_1799 stack = this.displayedStack;
         return new BakedIconEntry(this.renderPipeline10, (trimToWidth, n, n2, n3) -> HudIconRenderer.drawItem(trimToWidth, stack, n, n2, n3));
      }
   }

   private int process3(class_746 player2) {
      int n = 0;
      class_1799 stack = null;

      for(int i = 0; i < player2.method_31548().method_5439(); ++i) {
         class_1799 stack2 = player2.method_31548().method_5438(i);
         if (!stack2.method_7960() && this.itemBindBox.getPredicate().test(stack2)) {
            n += stack2.method_7947();
            if (stack == null) {
               stack = stack2;
            }
         }
      }

      this.setStack(stack);
      return n;
   }

   private boolean process4(class_746 player2) {
      boolean bl = player2.method_6115() && this.itemBindBox.getPredicate().test(player2.method_6030());
      if (bl) {
         int n = player2.method_6048();
         int n2 = player2.method_6030().method_7935(player2);
         this.value8 = n2 > 0 ? (float)n / (float)n2 : 0.0F;
      } else {
         this.value8 = 0.0F;
      }

      return bl;
   }

   private void setStack(class_1799 stack) {
      class_1799 stack2 = stack != null ? stack : this.process9;
      if (!class_1799.method_7973(stack2, this.displayedStack)) {
         this.displayedStack = stack != null ? stack.method_7972() : this.process9;
         this.renderPipeline10.update();
      }
   }

   public float getFloatType() {
      return 21.0F;
   }

   private boolean process5(class_310 mc, class_746 player2) {
      class_1796 cooldownManager = player2.method_7357();
      boolean bl = cooldownManager.method_7904(this.process9);
      if (bl) {
         float f = mc.method_61966().method_60637(false);
         this.value9 = cooldownManager.method_7905(this.process9, f);
      } else {
         this.value9 = 0.0F;
      }

      return bl;
   }

   private void process6(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, float f5, int n) {
      float f6 = f5 * f3;
      float f7 = f2 + f3 - f6;
      drawApi.beginStencil(1);
      drawApi.fillRectangle(matrix4f, f, f7, f3, f6, -1);
      drawApi.applyStencilMask(1);
      drawApi.drawRoundedRectangle(matrix4f, f, f2, f3, f3, f4, n);
      drawApi.endStencil();
   }

   private float process7(String string) {
      float f = 18.0F;
      float f2 = FontRegistry.font6.process3(string, 4.5F);
      return !(f <= 0.0F) && !(f2 <= f) ? Math.max(3.0F, 4.5F * f / f2) : 4.5F;
   }

   private void process8(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4) {
      boolean bl = class_310.method_1551().field_1755 instanceof class_490;
      boolean bl2 = this.itemBindBox.isActive2();
      boolean bl3 = bl && !bl2 && this.slot3 == 0;
      int n = bl3 ? 1 : this.slot3;
      String string = bl2 ? this.itemBindBox.getBindSetting().getKeyDisplayName() : (bl3 ? "PREVIEW" : "None");
      float f5 = FontRegistry.font6.process4(string, 4.5F);
      float f6 = 9.5F + f5;
      float f7 = (21.0F - f6) / 2.0F;
      float f8 = 8.0F * f3;
      float f9 = f + 6.5F * f3;
      float f10 = f2 + f7 * f3;
      if (this.renderPipeline10.isActive()) {
         int n3 = drawApi.bindTexture(this.renderPipeline10.getIntType4(), this.renderPipeline10.getIntType(), this.renderPipeline10.getIntType());
         drawApi.drawTexture(matrix4f, f9, f10, f8, f8, 0.0F, 1.0F, 1.0F, 0.0F, n3, -1);
      }

      String string2 = String.valueOf(n);
      float f11 = FontRegistry.font6.process3(string2, 4.0F);
      float f12 = FontRegistry.font6.process4(string2, 4.0F);
      FontRegistry.font6.process2(matrix4f, drawApi, string2, f9 + f8 - f11 * f3, f10 + f8 - f12 * f3, 4.0F * f3, this.slot4);
      float f13 = this.process7(string);
      float f14 = FontRegistry.font6.process3(string, f13);
      float f15 = f + (f4 - f14 * f3) / 2.0F;
      float f16 = f10 + f8 + 1.5F * f3;
      FontRegistry.font6.process2(matrix4f, drawApi, string, f15, f16, f13 * f3, this.slot4);
   }

   private int getIntType() {
      int n = ColorUtils.rgba(255, 255, 255, 25);
      if (this.value4 > 0.001F) {
         n = ColorUtils.lerp(n, ColorUtils.withAlpha(this.slot7, 89.25F), (double)this.value4);
      }

      if (this.value15 > 0.001F) {
         n = ColorUtils.lerp(n, ColorUtils.withAlpha(this.slot5, 127.5F), (double)this.value15);
      }

      if (this.value19 > 0.001F) {
         n = ColorUtils.lerp(n, ColorUtils.withAlpha(this.slot2, 76.5F), (double)this.value19);
      }

      return n;
   }

   private void update2() {
      this.value19 = 0.0F;
      this.value15 = 0.0F;
      this.value4 = 0.0F;
      this.value8 = 0.0F;
      this.value9 = 0.0F;
      this.slot3 = 0;
      this.enabled = false;
      this.slot = -1;
   }

   private int process10(class_746 player2) {
      long l = System.nanoTime();
      if (this.slot >= 0 && l - this.longType < 50000000L) {
         return this.slot;
      } else {
         this.slot = this.process3(player2);
         this.longType = l;
         return this.slot;
      }
   }

   public void update3() {
      this.renderPipeline10.update2();
   }
}
