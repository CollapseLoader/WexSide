package ru.wexside.misc;

import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;

public final class PlacementPreview {
   private class_2338 blockPos;
   private class_238 box;

   public PlacementPreview(class_2338 blockPos, class_238 box) {
      this.update(blockPos, box);
   }

   public void update(class_2338 blockPos, class_238 box) {
      this.blockPos = blockPos;
      this.box = box;
   }

   public class_243 center() {
      return this.blockPos == null ? null : class_243.method_24953(this.blockPos);
   }

   public class_238 box() {
      return this.box;
   }
}
