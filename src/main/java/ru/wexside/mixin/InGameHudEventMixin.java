package ru.wexside.mixin;

import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.wexside.WexSideClient;
import ru.wexside.event.EventBus;
import ru.wexside.event.OverlayRenderEvent;
import ru.wexside.event.OverlayType;
import ru.wexside.module.hud.CrosshairModule;
import ru.wexside.module.hud.HotbarModule;

@Mixin(value={class_329.class})
public abstract class InGameHudEventMixin {
    @Inject(method={"method_1736"}, at={@At(value="HEAD")}, cancellable=true)
    private void replaceVanillaCrosshair(class_332 context, class_9779 tickCounter, CallbackInfo callback) {
        if (CrosshairModule.isEnabled3()) {
            callback.cancel();
        }
    }

    @Inject(method={"method_1759"}, at={@At(value="HEAD")}, cancellable=true)
    private void replaceVanillaHotbar(class_332 context, class_9779 tickCounter, CallbackInfo callback) {
        if (HotbarModule.isEnabled()) {
            callback.cancel();
        }
    }

    @Inject(method={"method_70837"}, at={@At(value="HEAD")}, cancellable=true)
    private void beforeBossBars(class_332 context, class_9779 tickCounter, CallbackInfo callback) {
        this.cancelIfRequested(OverlayType.BOSS_BAR, callback);
    }

    @Inject(method={"method_1765"}, at={@At(value="HEAD")}, cancellable=true)
    private void beforeStatusEffects(class_332 context, class_9779 tickCounter, CallbackInfo callback) {
        this.cancelIfRequested(OverlayType.STATUS_EFFECTS, callback);
    }

    @Inject(method={"method_55803"}, at={@At(value="HEAD")}, cancellable=true)
    private void beforeScoreboard(class_332 context, class_9779 tickCounter, CallbackInfo callback) {
        this.cancelIfRequested(OverlayType.SCOREBOARD, callback);
    }

    private void cancelIfRequested(OverlayType type, CallbackInfo callback) {
        EventBus events = WexSideClient.getEventBus();
        if (events == null) {
            return;
        }
        OverlayRenderEvent event = new OverlayRenderEvent(type);
        events.post(event);
        if (event.isCancelled()) {
            callback.cancel();
        }
    }
}

