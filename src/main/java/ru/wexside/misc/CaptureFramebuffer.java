package ru.wexside.misc;

import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.class_10868;
import net.minecraft.class_276;

public final class CaptureFramebuffer extends class_276 {
   public CaptureFramebuffer() {
      super("wex/vanilla-capture", true);
   }

   public int getIntType() {
      GpuTexture gpuTexture = this.method_30277();
      int n;
      if (gpuTexture instanceof class_10868 iIiIIllIl2) {
         n = iIiIIllIl2.method_68427();
      } else {
         n = 0;
      }

      return n;
   }

   public void process(int n, int n2) {
      if (n > 0 && n2 > 0) {
         if (this.field_1482 != n || this.field_1481 != n2 || this.method_30277() == null) {
            this.method_1234(n, n2);
         }
      }
   }
}
