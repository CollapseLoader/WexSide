package ru.wexside.mixin;

import net.minecraft.class_1735;
import net.minecraft.class_332;
import net.minecraft.class_465;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.wexside.misc.HandledScreenAccessor;

@Mixin({class_465.class})
public abstract class HandledScreenAccessorMixin implements HandledScreenAccessor {
   @Shadow
   protected int field_2776;
   @Shadow
   protected int field_2800;
   @Shadow
   protected int field_2792;
   @Shadow
   protected int field_2779;
   @Shadow
   protected class_1735 field_2787;

   @Shadow
   protected abstract void method_2389(class_332 var1, float var2, int var3, int var4);

   @Override
   public int getContainerX() {
      return this.field_2776;
   }

   @Override
   public int getContainerY() {
      return this.field_2800;
   }

   @Override
   public int getContainerWidth() {
      return this.field_2792;
   }

   @Override
   public int getContainerHeight() {
      return this.field_2779;
   }

   @Override
   public class_1735 getFocusedSlot() {
      return this.field_2787;
   }

   @Override
   public void drawContainerBackground(class_332 context, float delta, int mouseX, int mouseY) {
      this.method_2389(context, delta, mouseX, mouseY);
   }
}
