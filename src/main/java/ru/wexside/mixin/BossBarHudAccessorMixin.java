package ru.wexside.mixin;

import java.util.Map;
import java.util.UUID;
import net.minecraft.class_337;
import net.minecraft.class_345;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import ru.wexside.misc.BossBarMapAccessor;

@Mixin(value={class_337.class})
public abstract class BossBarHudAccessorMixin
implements BossBarMapAccessor {
    @Override
    @Accessor(value="field_2060")
    public abstract Map<UUID, class_345> getMap();
}

