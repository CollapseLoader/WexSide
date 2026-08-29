package ru.wexside.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.ThemeColors;
import ru.wexside.module.Module;
import ru.wexside.module.misc.EspFeatureModule;
import ru.wexside.setting.Setting;
import ru.wexside.setting.SettingKeybind;
import ru.wexside.util.AbstractHudElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.EspFeatureRegistry;
import ru.wexside.util.GuiDrawApi;

public final class KeybindsHud
extends AbstractHudElement {
    private static final float MINIMUM_WIDTH = 90.0f;
    private static final float HEADER_HEIGHT = 18.0f;
    private static final long CACHE_LIFETIME_NS = 100000000L;
    private final Map<String, AnimatedEntry> animatedEntries = new LinkedHashMap<String, AnimatedEntry>();
    private List<Module> modules;
    private List<KeybindEntry> cachedEntries;
    private long cacheTime;
    private float animatedWidth = 90.0f;
    private float animatedHeight = 18.0f;

    public KeybindsHud(BooleanSupplier visibility) {
        super("Keybinds", visibility);
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
    protected void updateLayout() {
        this.synchronizeEntries();
        float width = KeybindsHud.titleWidth();
        float rowsHeight = 0.0f;
        this.animatedEntries.values().removeIf(entry -> {
            entry.updateAnimation();
            return entry.isExpired();
        });
        for (AnimatedEntry entry2 : this.animatedEntries.values()) {
            if (entry2.getVisibility() <= 0.001f) continue;
            rowsHeight += entry2.getVisibility() * (entry2.getHeight() + 3.0f);
            width = Math.max(width, entry2.getWidth());
        }
        float height = 18.0f + (rowsHeight > 0.0f ? 4.5f + rowsHeight + 2.0f : 0.0f);
        this.animatedWidth = FrameInterpolator.lerpTowards(this.animatedWidth, Math.max(90.0f, width), 30.0f);
        this.animatedHeight = FrameInterpolator.lerpTowards(this.animatedHeight, height, 30.0f);
    }

    @Override
    protected void renderContent(GuiDrawApi renderer, Matrix4f matrix, float x, float y, float width, float height, float scale) {
        int titleColor = ThemeColors.hudTextPrimary();
        this.renderPanelSurface(renderer, matrix, x, y, width, height, 8.0f, scale);
        renderer.beginStencil(1);
        renderer.drawRoundedRectangle(matrix, x, y, width, height, 8.0f * scale, -1);
        renderer.applyStencilMask(1);
        FontRegistry.font7.process2(matrix, renderer, "Keybinds", x + 5.0f * scale, y + 4.5f * scale, 8.0f * scale, titleColor);
        FontRegistry.font3.process5(matrix, renderer, "\u041b", x + width - 13.0f * scale, y + 6.0f * scale, 8.0f * scale, titleColor);
        float rowY = y + 22.5f * scale;
        for (AnimatedEntry entry : this.animatedEntries.values()) {
            float visibility = entry.getVisibility();
            if (visibility <= 0.001f) continue;
            entry.render(renderer, matrix, x, rowY, width, scale, visibility);
            rowY += visibility * (entry.getHeight() + 3.0f) * scale;
        }
        renderer.endStencil();
    }

    @Override
    protected boolean isContentVisible() {
        return !this.currentEntries().isEmpty();
    }

    private void synchronizeEntries() {
        for (AnimatedEntry entry : this.animatedEntries.values()) {
            entry.setPresent(false);
        }
        List<KeybindEntry> entries = this.currentEntries();
        if (entries.isEmpty() && this.isEditorScreen()) {
            entries = KeybindsHud.previewEntries();
        }
        for (KeybindEntry entry : entries) {
            this.animatedEntries.compute(entry.id(), (id, animated) -> {
                if (animated == null) {
                    return new AnimatedEntry(entry);
                }
                animated.setEntry(entry);
                animated.setPresent(true);
                return animated;
            });
        }
    }

    private static boolean isKeybindChanged(Setting setting, SettingKeybind keybind) {
        return keybind.isActive() || Arrays.equals(setting.copyPayload(), keybind.getActivationPayload());
    }

    private static float titleWidth() {
        return 5.0f + FontRegistry.font7.process3("Keybinds", 8.0f) + 19.0f;
    }

    private static String featureName(Module module) {
        String string;
        if (module instanceof EspFeatureModule) {
            EspFeatureModule feature = (EspFeatureModule)module;
            string = feature.getString();
        } else {
            string = "";
        }
        return string;
    }

    private static String ownerName(Module module) {
        String feature = KeybindsHud.featureName(module);
        return feature.isEmpty() ? module.getDisplayName() : feature + " / " + module.getDisplayName();
    }

    private List<Module> modules() {
        if (this.modules == null) {
            this.modules = new ArrayList<Module>(WexSideClient.getInstance().getModuleManager().getModules());
            EspFeatureRegistry espFeatures = WexSideClient.getEspFeatureRegistry();
            if (espFeatures != null) {
                this.modules.addAll(espFeatures.getModules());
            }
        }
        return this.modules;
    }

    private List<KeybindEntry> collectEntries() {
        ArrayList<KeybindEntry> entries = new ArrayList<KeybindEntry>();
        for (Module module : this.modules()) {
            for (Setting setting : module.getSettings()) {
                SettingKeybind keybind = setting.getKeybind();
                if (!setting.hasKeybind() || keybind == null || keybind.getBindInput().isUnbound() || !keybind.isShownInHud() || !KeybindsHud.isKeybindChanged(setting, keybind)) continue;
                boolean moduleToggle = module.isToggleSetting(setting);
                String name = moduleToggle ? module.getDisplayName() : setting.getDisplayName();
                String owner = moduleToggle ? KeybindsHud.featureName(module) : KeybindsHud.ownerName(module);
                entries.add(new KeybindEntry(setting.getConfigId(), owner, name, keybind.getDisplayName()));
            }
        }
        return entries;
    }

    private List<KeybindEntry> currentEntries() {
        long now = System.nanoTime();
        if (this.cachedEntries == null || now - this.cacheTime >= 100000000L) {
            this.cachedEntries = this.collectEntries();
            this.cacheTime = now;
        }
        return this.cachedEntries;
    }

    private static List<KeybindEntry> previewEntries() {
        return List.of(new KeybindEntry("preview/aura", "", "Attack Aura", "R"), new KeybindEntry("preview/velocity", "", "Velocity", "V"), new KeybindEntry("preview/sprint", "", "Sprint", "CTRL"), new KeybindEntry("preview/hud", "HUD", "Watermark", "H"));
    }

    private static final class AnimatedEntry {
        private KeybindEntry entry;
        private boolean present = true;
        private float visibility = 1.0f;

        private AnimatedEntry(KeybindEntry entry) {
            this.entry = entry;
        }

        private void setEntry(KeybindEntry entry) {
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

        private float getHeight() {
            return this.hasOwner() ? 16.0f : 11.0f;
        }

        private float getWidth() {
            float ownerWidth = this.hasOwner() ? FontRegistry.font5.process3(this.entry.owner().toUpperCase(Locale.ROOT), 4.25f) : 0.0f;
            float nameWidth = FontRegistry.font5.process3(this.entry.name(), 6.5f);
            float keyWidth = FontRegistry.font6.process3(this.entry.key(), 5.5f) + 8.0f;
            return 6.0f + Math.max(ownerWidth, nameWidth) + 8.0f + keyWidth + 6.0f;
        }

        private void render(GuiDrawApi renderer, Matrix4f matrix, float x, float y, float width, float scale, float alpha) {
            float slide = (1.0f - alpha) * 4.0f * scale;
            float contentX = x + slide;
            float rowHeight = this.getHeight() * scale;
            int primary = ColorUtils.multiplyAlpha(ThemeColors.hudTextPrimary(), alpha);
            int accent = ColorUtils.multiplyAlpha(ThemeColors.accent(), alpha);
            int muted = ColorUtils.multiplyAlpha(ThemeColors.hudTextMuted(), alpha);
            if (this.hasOwner()) {
                String owner = this.entry.owner().toUpperCase(Locale.ROOT);
                FontRegistry.font5.process2(matrix, renderer, owner, contentX + 6.0f * scale, y + 2.0f * scale, 4.25f * scale, accent);
                FontRegistry.font5.process2(matrix, renderer, this.entry.name(), contentX + 6.0f * scale, y + 7.75f * scale, 6.5f * scale, primary);
            } else {
                float nameHeight = FontRegistry.font5.process4(this.entry.name(), 6.5f) * scale;
                FontRegistry.font5.process2(matrix, renderer, this.entry.name(), contentX + 6.0f * scale, y + (rowHeight - nameHeight) * 0.5f, 6.5f * scale, primary);
            }
            float keyTextWidth = FontRegistry.font6.process3(this.entry.key(), 5.5f) * scale;
            float badgeWidth = keyTextWidth + 8.0f * scale;
            float badgeHeight = 10.5f * scale;
            float badgeX = x + width - 6.0f * scale - badgeWidth + slide;
            float badgeY = y + (rowHeight - badgeHeight) * 0.5f;
            renderer.drawRoundedOutline(matrix, badgeX, badgeY, badgeWidth, badgeHeight, 6.0f * scale, scale, ColorUtils.multiplyAlpha(ThemeColors.separatorHover(), alpha));
            FontRegistry.font6.process2(matrix, renderer, this.entry.key(), badgeX + (badgeWidth - keyTextWidth) * 0.5f, badgeY + (badgeHeight - FontRegistry.font6.process4(this.entry.key(), 5.5f) * scale) * 0.5f, 5.5f * scale, muted);
        }

        private boolean hasOwner() {
            return this.entry.owner() != null && !this.entry.owner().isEmpty();
        }
    }

    private record KeybindEntry(String id, String owner, String name, String key) {
    }
}

