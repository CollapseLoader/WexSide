package ru.wexside.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.ToIntFunction;
import ru.wexside.misc.BuiltInThemes;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.ThemePalette;
import ru.wexside.util.ColorUtils;

public final class ThemeManager {
    private static final ThemeManager INSTANCE = new ThemeManager();
    private final List<ThemePalette> themes = new ArrayList<ThemePalette>();
    private final List<Runnable> changeListeners = new ArrayList<Runnable>();
    private ThemePalette previousTheme;
    private ThemePalette currentTheme;
    private float transitionProgress = 1.0f;

    private ThemeManager() {
        this.registerTheme(BuiltInThemes.themePalette);
        this.registerTheme(BuiltInThemes.themePalette2);
        this.previousTheme = BuiltInThemes.themePalette2;
        this.currentTheme = BuiltInThemes.themePalette2;
    }

    public static ThemeManager getThemeManager() {
        return INSTANCE;
    }

    public ThemePalette getCurrentTheme() {
        return this.currentTheme;
    }

    public void switchTheme(String id) {
        ThemePalette nextTheme = this.findTheme(id);
        if (nextTheme == null || nextTheme == this.currentTheme) {
            return;
        }
        this.previousTheme = this.currentTheme;
        this.currentTheme = nextTheme;
        this.transitionProgress = 0.0f;
        this.notifyThemeChanged();
    }

    public void cycleTheme() {
        if (this.themes.isEmpty()) {
            return;
        }
        int currentIndex = this.themes.indexOf(this.currentTheme);
        this.switchTheme(this.themes.get((currentIndex + 1) % this.themes.size()).id());
    }

    public int interpolateColor(ToIntFunction<ThemePalette> selector) {
        int currentColor = selector.applyAsInt(this.currentTheme);
        if (this.transitionProgress >= 1.0f || this.previousTheme == this.currentTheme) {
            return currentColor;
        }
        int previousColor = selector.applyAsInt(this.previousTheme);
        return ColorUtils.lerp(previousColor, currentColor, this.transitionProgress);
    }

    public boolean isBlurEnabled() {
        return this.currentTheme.blur() || this.transitionProgress < 1.0f && this.previousTheme.blur();
    }

    public boolean isHudBlurEnabled() {
        return this.currentTheme.hudBlur() || this.transitionProgress < 1.0f && this.previousTheme.hudBlur();
    }

    public List<ThemePalette> getThemes() {
        return Collections.unmodifiableList(this.themes);
    }

    public float getLightThemeBlend() {
        float current;
        float f = current = ThemeManager.isLightTheme(this.currentTheme) ? 1.0f : 0.0f;
        if (this.transitionProgress >= 1.0f || this.previousTheme == this.currentTheme) {
            return current;
        }
        float previous = ThemeManager.isLightTheme(this.previousTheme) ? 1.0f : 0.0f;
        return previous + (current - previous) * this.transitionProgress;
    }

    public void selectImmediately(String id) {
        ThemePalette theme = this.findTheme(id);
        if (theme == null) {
            return;
        }
        this.previousTheme = theme;
        this.currentTheme = theme;
        this.transitionProgress = 1.0f;
        this.notifyThemeChanged();
    }

    public void tickTransition() {
        if (this.transitionProgress >= 1.0f) {
            return;
        }
        this.transitionProgress = FrameInterpolator.lerpTowards(this.transitionProgress, 1.0f, 20.0f);
        if (this.transitionProgress > 0.999f) {
            this.transitionProgress = 1.0f;
        }
    }

    public void addChangeListener(Runnable listener) {
        this.changeListeners.add(listener);
    }

    public boolean isDarkTheme() {
        return "dark".equals(this.currentTheme.id());
    }

    public void registerTheme(ThemePalette theme) {
        this.themes.add(theme);
    }

    private ThemePalette findTheme(String id) {
        for (ThemePalette theme : this.themes) {
            if (!theme.id().equals(id)) continue;
            return theme;
        }
        return null;
    }

    private static boolean isLightTheme(ThemePalette theme) {
        return "light".equals(theme.id());
    }

    private void notifyThemeChanged() {
        this.changeListeners.forEach(Runnable::run);
    }
}

