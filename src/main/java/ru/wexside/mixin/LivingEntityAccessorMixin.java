package ru.wexside.mixin;

import net.minecraft.class_1309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.wexside.misc.LivingEntityStateAccessor;

@Mixin({class_1309.class})
public abstract class LivingEntityAccessorMixin implements LivingEntityStateAccessor {
   @Shadow
   private int field_6228;

   @Override
   public void setJumpingCooldown(int cooldown) {
      this.field_6228 = cooldown;
   }
}
