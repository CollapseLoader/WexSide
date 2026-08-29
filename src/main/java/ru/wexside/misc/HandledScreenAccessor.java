package ru.wexside.misc;

import net.minecraft.class_1735;
import net.minecraft.class_332;

public interface HandledScreenAccessor {
   int getContainerX();

   int getContainerY();

   int getContainerWidth();

   int getContainerHeight();

   class_1735 getFocusedSlot();

   void drawContainerBackground(class_332 var1, float var2, int var3, int var4);
}
