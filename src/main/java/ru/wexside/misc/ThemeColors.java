package ru.wexside.misc;

import java.util.function.ToIntFunction;
import ru.wexside.misc.ThemeManager;
import ru.wexside.misc.ThemePalette;
import ru.wexside.util.ColorUtils;

public final class ThemeColors {
    private ThemeColors() {
    }

    private static ThemeManager manager() {
        return ThemeManager.getThemeManager();
    }

    private static int color(ToIntFunction<ThemePalette> selector) {
        return ThemeColors.manager().interpolateColor(selector);
    }

    public static int visualizerSlot() {
        return ThemeColors.color(ThemePalette::visualizerSlot);
    }

    public static int textMuted() {
        return ThemeColors.color(ThemePalette::textMuted);
    }

    public static int backgroundSecondary() {
        return ThemeColors.color(ThemePalette::backgroundSecondary);
    }

    public static int textSecondary() {
        return ThemeColors.color(ThemePalette::textSecondary);
    }

    public static int borderSubtle() {
        return ThemeColors.color(ThemePalette::borderSubtle);
    }

    public static int borderPrimary() {
        return ThemeColors.color(ThemePalette::borderPrimary);
    }

    public static int textPrimary() {
        return ThemeColors.color(ThemePalette::textPrimary);
    }

    public static int backgroundPrimary() {
        return ThemeColors.color(ThemePalette::backgroundPrimary);
    }

    public static int formatSelectorFill() {
        return ThemeColors.color(ThemePalette::formatSelectorFill);
    }

    public static int separator() {
        return ThemeColors.color(ThemePalette::separator);
    }

    public static int hudTextPrimary() {
        return ThemeColors.color(ThemePalette::hudTextPrimary);
    }

    public static int notificationOutline() {
        return ThemeColors.color(ThemePalette::notificationOutline);
    }

    public static int hudBackground() {
        return ThemeColors.color(ThemePalette::hudBackground);
    }

    public static int hudTextMuted() {
        return ThemeColors.color(ThemePalette::hudTextMuted);
    }

    public static int accent() {
        return ThemeColors.color(ThemePalette::accent);
    }

    public static int panelBackground() {
        return ThemeColors.color(ThemePalette::panelBackground);
    }

    public static int borderStrong() {
        return ThemeColors.color(ThemePalette::borderStrong);
    }

    public static int borderSoft() {
        return ThemeColors.color(ThemePalette::borderSoft);
    }

    public static int controlFill() {
        return ThemeColors.color(ThemePalette::controlFill);
    }

    public static int accentTint() {
        return ThemeColors.color(ThemePalette::accentTint);
    }

    public static int textPlaceholder() {
        return ThemeColors.color(ThemePalette::textPlaceholder);
    }

    public static int backgroundControl() {
        return ThemeColors.color(ThemePalette::backgroundControl);
    }

    public static int textDisabled() {
        return ThemeColors.color(ThemePalette::textDisabled);
    }

    public static int formatFieldFill() {
        return ThemeColors.color(ThemePalette::formatFieldFill);
    }

    public static int backgroundHover() {
        return ThemeColors.color(ThemePalette::backgroundHover);
    }

    public static int modalScrim() {
        return ThemeColors.color(ThemePalette::modalScrim);
    }

    public static int hudTextSecondary() {
        return ThemeColors.color(ThemePalette::hudTextSecondary);
    }

    public static int danger() {
        return ThemeColors.color(ThemePalette::danger);
    }

    public static int hudVignette() {
        return ThemeColors.color(ThemePalette::hudVignette);
    }

    public static int avatarPlaceholder() {
        return ThemeColors.color(ThemePalette::avatarPlaceholder);
    }

    public static int withHoverOverlay(int baseColor) {
        return ThemeColors.blend(baseColor, ColorUtils.rgba(255, 255, 255, 25));
    }

    public static int separatorHover() {
        return ThemeColors.blend(ThemeColors.separator(), ColorUtils.rgba(255, 255, 255, 20));
    }

    public static int adjustForTheme(int color) {
        return ThemeColors.manager().isDarkTheme() ? ColorUtils.darken(color, 0.7) : ColorUtils.brighten(color, 0.5);
    }

    public static int blend(int from, int to) {
        float progress = ThemeColors.manager().getLightThemeBlend();
        if (progress <= 0.0f) {
            return from;
        }
        if (progress >= 1.0f) {
            return to;
        }
        return ColorUtils.lerp(from, to, progress);
    }
}

