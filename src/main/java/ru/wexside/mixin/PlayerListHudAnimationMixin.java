package ru.wexside.mixin;

import net.minecraft.class_266;
import net.minecraft.class_269;
import net.minecraft.class_332;
import net.minecraft.class_355;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.module.hud.AnimateModule;
import ru.wexside.module.hud.ExtraTabModule;

@Mixin(value={class_355.class})
public abstract class PlayerListHudAnimationMixin {
    @Unique
    private boolean wexside$animationMatrixPushed;

    @Inject(method={"method_1921"}, at={@At(value="TAIL")})
    private void wexside$trackVisibility(boolean visible, CallbackInfo callback) {
        AnimateModule.onBooleanType(visible);
    }

    @ModifyConstant(method={"method_48213"}, constant={@Constant(longValue=80L)})
    private long wexside$expandPlayerLimit(long vanillaLimit) {
        return ExtraTabModule.isEnabled() ? Long.MAX_VALUE : vanillaLimit;
    }

    @Inject(method={"method_1919"}, at={@At(value="HEAD")})
    private void wexside$beginAnimation(class_332 context, int scaledWindowWidth, class_269 scoreboard, class_266 objective, CallbackInfo callback) {
        float offset = AnimateModule.compute3(context.method_51443());
        if (Math.abs(offset) <= 0.001f) {
            return;
        }
        context.method_51448().pushMatrix();
        context.method_51448().translate(0.0f, offset);
        this.wexside$animationMatrixPushed = true;
    }

    @Inject(method={"method_1919"}, at={@At(value="RETURN")})
    private void wexside$endAnimation(class_332 context, int scaledWindowWidth, class_269 scoreboard, class_266 objective, CallbackInfo callback) {
        if (!this.wexside$animationMatrixPushed) {
            return;
        }
        this.wexside$animationMatrixPushed = false;
        context.method_51448().popMatrix();
    }
}

