package ru.wexside.mixin;

import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import ru.wexside.misc.AttackInvoker;
import ru.wexside.misc.ItemUseCooldownAccessor;

@Mixin({class_310.class})
public abstract class MinecraftClientAccessorMixin implements ItemUseCooldownAccessor, AttackInvoker {
   @Shadow
   private int field_1752;

   @Invoker("method_1536")
   @Override
   public abstract boolean invokeAttack();

   @Override
   public void setItemUseCooldown(int cooldown) {
      this.field_1752 = cooldown;
   }
}
