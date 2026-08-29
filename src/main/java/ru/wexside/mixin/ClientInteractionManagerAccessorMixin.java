package ru.wexside.mixin;

import net.minecraft.class_636;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import ru.wexside.misc.BlockBreakingAccessor;

@Mixin({class_636.class})
public abstract class ClientInteractionManagerAccessorMixin implements BlockBreakingAccessor {
   @Shadow
   private float field_3715;
   @Shadow
   private int field_3716;

   @Override
   public void setBreakingProgress(float progress) {
      this.field_3715 = progress;
   }

   @Override
   public void setBreakingCooldown(int cooldown) {
      this.field_3716 = cooldown;
   }

   @Override
   public float getBreakingProgress() {
      return this.field_3715;
   }
}
