package ru.wexside.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(
   targets = {"net/minecraft/class_1796$class_1797"}
)
public interface ItemCooldownEntryAccessorMixin {
   @Accessor("comp_3084")
   int wexside$getEndTick();
}
