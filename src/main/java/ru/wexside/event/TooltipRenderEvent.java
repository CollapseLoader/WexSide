package ru.wexside.event;

import net.minecraft.class_332;

public class TooltipRenderEvent implements Event {
   private final class_332 context;
   private final int mouseY;
   private final int mouseX;

   public int getIntType() {
      return this.mouseY;
   }

   public int getIntType2() {
      return this.mouseX;
   }

   public class_332 getDrawContext() {
      return this.context;
   }

   public TooltipRenderEvent(class_332 context, int mouseX, int mouseY) {
      this.context = context;
      this.mouseX = mouseX;
      this.mouseY = mouseY;
   }
}
