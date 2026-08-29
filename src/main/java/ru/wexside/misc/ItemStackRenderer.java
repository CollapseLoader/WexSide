package ru.wexside.misc;

import net.minecraft.class_1799;
import org.joml.Matrix4f;
import ru.wexside.render.HudIconRenderer;
import ru.wexside.render.IconAtlasEntry;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class ItemStackRenderer {
   private final int slot = ColorUtils.rgba(255, 255, 255, 255);
   private final float value3;
   private final float value4;
   private class_1799 stack;
   private class_1799 stack2;
   private final IconAtlasEntry iconEntry = new IconAtlasEntry();
   private final float process2 = 19.5F;
   private final float value5;
   private final float value6 = 19.0F;
   private final float value7;

   public ItemStackRenderer() {
      this.value5 = 8.0F;
      this.value4 = 1.0F;
      this.value7 = 10.0F;
      this.value3 = 5.0F;
      this.stack = class_1799.field_8037;
      this.stack2 = class_1799.field_8037;
   }

   public void process(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3) {
      float f5 = 19.5F * f3;
      float f6 = 19.0F * f3;
      drawApi.drawRoundedOutline(matrix4f, f, f2, f5, f6, 8.0F * f3, 1.0F * f3, ThemeColors.notificationOutline());
      if (!this.stack.method_7960()) {
         if (this.iconEntry.isActive()) {
            float f7 = 10.0F * f3;
            float f8 = f + (f5 - f7) / 2.0F;
            float f4 = f2 + (f6 - f7) / 2.0F;
            int n2 = drawApi.bindTexture(this.iconEntry.getIntType4(), this.iconEntry.getIntType(), this.iconEntry.getIntType());
            drawApi.drawTexture(matrix4f, f8, f4, f7, f7, 0.0F, 1.0F, 1.0F, 0.0F, n2, -1);
         }

         int n;
         if ((n = this.stack.method_7947()) > 1) {
            String string = String.valueOf(n);
            float f4 = FontRegistry.font6.process3(string, 5.0F);
            float f9 = FontRegistry.font6.process4(string, 5.0F);
            FontRegistry.font6.process2(matrix4f, drawApi, string, f + f5 - f4 * f3 - 2.0F * f3, f2 + f6 - f9 * f3 - 1.5F * f3, 5.0F * f3, this.slot);
         }
      }
   }

   public void setStack(class_1799 stack3) {
      this.stack = stack3;
      if (!class_1799.method_7973(stack3, this.stack2)) {
         this.stack2 = stack3.method_7960() ? class_1799.field_8037 : stack3.method_7972();
         this.iconEntry.update();
      }
   }

   public float getFloatType() {
      return 19.0F;
   }

   public float getFloatType2() {
      return 19.5F;
   }

   public BakedIconEntry process3(float f) {
      if (this.stack2.method_7960()) {
         return null;
      } else if (!this.iconEntry.process(f)) {
         return null;
      } else {
         class_1799 stack3 = this.stack2;
         return new BakedIconEntry(this.iconEntry, (trimToWidth, n, n2, n3) -> HudIconRenderer.drawItem(trimToWidth, stack3, n, n2, n3));
      }
   }

   public void update() {
      this.iconEntry.update2();
   }
}
