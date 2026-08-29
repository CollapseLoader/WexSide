package ru.wexside.misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import net.minecraft.class_1796;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.ThemeColors;
import ru.wexside.mixin.ItemCooldownEntryAccessorMixin;
import ru.wexside.mixin.ItemCooldownManagerAccessorMixin;
import ru.wexside.render.BakedItemIcon;
import ru.wexside.render.ItemIconRenderer;
import ru.wexside.util.AbstractHudElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class Cooldowns
extends AbstractHudElement {
    private static final float MINIMUM_WIDTH = 110.0f;
    private static final float HEADER_HEIGHT = 18.0f;
    private static final float ROW_HEIGHT = 10.5f;
    private final Map<String, AnimatedCooldown> animatedCooldowns = new LinkedHashMap<String, AnimatedCooldown>();
    private final ItemIconRenderer itemIcons = new ItemIconRenderer();
    private float animatedWidth = 110.0f;
    private float animatedHeight = 18.0f;

    public Cooldowns(BooleanSupplier visible) {
        super("Cooldowns", visible);
    }

    @Override
    protected float getWidth() {
        return this.animatedWidth;
    }

    @Override
    protected float getHeight() {
        return this.animatedHeight;
    }

    @Override
    protected boolean isContentVisible() {
        return !this.collectActualCooldowns().isEmpty();
    }

    @Override
    protected void updateLayout() {
        this.synchronizeCooldowns();
        float targetWidth = this.titleWidth();
        float rowsHeight = 0.0f;
        this.itemIcons.beginFrame();
        this.animatedCooldowns.values().removeIf(cooldown -> {
            cooldown.updateAnimation();
            return cooldown.isExpired();
        });
        for (AnimatedCooldown cooldown2 : this.animatedCooldowns.values()) {
            if (cooldown2.getVisibility() <= 0.001f) continue;
            targetWidth = Math.max(targetWidth, cooldown2.getWidth());
            rowsHeight += cooldown2.getVisibility() * 13.5f;
            this.itemIcons.get(cooldown2.getStack());
        }
        float targetHeight = 18.0f + (rowsHeight > 0.0f ? 4.5f + rowsHeight + 2.0f : 0.0f);
        this.animatedWidth = FrameInterpolator.lerpTowards(this.animatedWidth, Math.max(110.0f, targetWidth), 30.0f);
        this.animatedHeight = FrameInterpolator.lerpTowards(this.animatedHeight, targetHeight, 30.0f);
        this.itemIcons.collectBakes();
        this.itemIcons.evictUnused();
    }

    @Override
    protected void renderContent(GuiDrawApi renderer, Matrix4f matrix, float x, float y, float width, float height, float scale) {
        int titleColor = ThemeColors.hudTextPrimary();
        this.renderPanelSurface(renderer, matrix, x, y, width, height, 8.0f, scale);
        renderer.beginStencil(1);
        renderer.drawRoundedRectangle(matrix, x, y, width, height, 8.0f * scale, -1);
        renderer.applyStencilMask(1);
        FontRegistry.font7.process2(matrix, renderer, "Cooldowns", x + 5.0f * scale, y + 4.5f * scale, 8.0f * scale, titleColor);
        FontRegistry.font3.process5(matrix, renderer, "\u044a", x + width - 13.0f * scale, y + 6.0f * scale, 8.0f * scale, titleColor);
        float rowY = y + 22.5f * scale;
        for (AnimatedCooldown cooldown : this.animatedCooldowns.values()) {
            float visibility = cooldown.getVisibility();
            if (visibility <= 0.001f) continue;
            cooldown.render(renderer, matrix, x, rowY, width, scale, visibility);
            rowY += visibility * 13.5f * scale;
        }
        renderer.endStencil();
    }

    private void synchronizeCooldowns() {
        for (AnimatedCooldown cooldown : this.animatedCooldowns.values()) {
            cooldown.setPresent(false);
        }
        List<CooldownEntry> cooldowns = this.collectActualCooldowns();
        if (cooldowns.isEmpty() && this.isEditorScreen()) {
            cooldowns = this.previewCooldowns();
        }
        for (CooldownEntry cooldown : cooldowns) {
            this.animatedCooldowns.compute(cooldown.id(), (ignored, animated) -> {
                if (animated == null) {
                    return new AnimatedCooldown(cooldown);
                }
                animated.setEntry(cooldown);
                animated.setPresent(true);
                return animated;
            });
        }
    }

    private List<CooldownEntry> collectActualCooldowns() {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return List.of();
        }
        ArrayList<CooldownEntry> cooldowns = new ArrayList<CooldownEntry>();
        class_1796 manager = player.method_7357();
        if (!(manager instanceof ItemCooldownManagerAccessorMixin)) {
            return cooldowns;
        }
        ItemCooldownManagerAccessorMixin state = (ItemCooldownManagerAccessorMixin)manager;
        HashSet<class_2960> seen = new HashSet<class_2960>();
        for (int slot = 0; slot < player.method_31548().method_5439(); ++slot) {
            ItemCooldownEntryAccessorMixin entry;
            int remainingTicks;
            Object rawEntry;
            class_2960 group;
            class_1799 stack = player.method_31548().method_5438(slot);
            if (stack.method_7960() || !manager.method_7904(stack) || !seen.add(group = manager.method_62836(stack)) || !((rawEntry = state.wexside$getEntries().get(group)) instanceof ItemCooldownEntryAccessorMixin) || (remainingTicks = (entry = (ItemCooldownEntryAccessorMixin)rawEntry).wexside$getEndTick() - state.wexside$getTick()) <= 0) continue;
            cooldowns.add(new CooldownEntry(group.toString(), stack.method_7972(), remainingTicks));
        }
        return cooldowns;
    }

    private List<CooldownEntry> previewCooldowns() {
        return List.of(new CooldownEntry("preview/pearl", new class_1799((class_1935)class_1802.field_8634), 200), new CooldownEntry("preview/gapple", new class_1799((class_1935)class_1802.field_8463), 160), new CooldownEntry("preview/chorus", new class_1799((class_1935)class_1802.field_8233), 120), new CooldownEntry("preview/totem", new class_1799((class_1935)class_1802.field_8288), 80));
    }

    private float titleWidth() {
        return 5.0f + FontRegistry.font7.process3("Cooldowns", 8.0f) + 19.0f;
    }

    private final class AnimatedCooldown {
        private CooldownEntry entry;
        private boolean present = true;
        private float visibility = 1.0f;

        private AnimatedCooldown(CooldownEntry entry) {
            this.entry = entry;
        }

        private void setEntry(CooldownEntry entry) {
            this.entry = entry;
        }

        private void setPresent(boolean present) {
            this.present = present;
        }

        private void updateAnimation() {
            this.visibility = FrameInterpolator.lerpTowards(this.visibility, this.present ? 1.0f : 0.0f, 30.0f);
        }

        private boolean isExpired() {
            return !this.present && this.visibility <= 0.001f;
        }

        private float getVisibility() {
            return this.visibility;
        }

        private class_1799 getStack() {
            return this.entry.stack();
        }

        private float getWidth() {
            String duration = this.durationText();
            return 15.0f + FontRegistry.font4.process3(this.entry.stack().method_7964().getString(), 6.5f) + 4.0f + FontRegistry.font6.process3(duration, 5.5f) + 5.0f;
        }

        private void render(GuiDrawApi renderer, Matrix4f matrix, float x, float y, float width, float scale, float alpha) {
            int primary = ColorUtils.multiplyAlpha(ThemeColors.hudTextPrimary(), alpha);
            float slide = (1.0f - alpha) * 4.0f * scale;
            BakedItemIcon icon = Cooldowns.this.itemIcons.get(this.entry.stack());
            Cooldowns.this.itemIcons.render(renderer, matrix, icon, x + 5.0f * scale + slide, y + 1.75f * scale, 7.0f * scale, ColorUtils.withAlpha(-1, 255.0f * alpha));
            String name = this.entry.stack().method_7964().getString();
            float nameHeight = FontRegistry.font4.process4(name, 6.5f) * scale;
            FontRegistry.font4.process2(matrix, renderer, name, x + 15.0f * scale + slide, y + (10.5f * scale - nameHeight) * 0.5f, 6.5f * scale, primary);
            String duration = this.durationText();
            float durationWidth = FontRegistry.font6.process3(duration, 5.5f) * scale;
            FontRegistry.font6.process2(matrix, renderer, duration, x + width - 5.0f * scale - durationWidth + slide, y + (10.5f * scale - FontRegistry.font6.process4(duration, 5.5f) * scale) * 0.5f, 5.5f * scale, ColorUtils.multiplyAlpha(ThemeColors.hudTextMuted(), alpha));
        }

        private String durationText() {
            float seconds = (float)this.entry.remainingTicks() / 20.0f;
            if (seconds < 1.0f) {
                return (float)Math.round(seconds * 10.0f) / 10.0f + " \u0441\u0435\u043a.";
            }
            if (seconds < 60.0f) {
                return "%d \u0441\u0435\u043a.".formatted((int)seconds);
            }
            return "%d:%02d".formatted((int)seconds / 60, (int)seconds % 60);
        }
    }

    private record CooldownEntry(String id, class_1799 stack, int remainingTicks) {
    }
}

