package ru.wexside.util;

import java.awt.Color;
import net.minecraft.class_3532;

public final class ColorUtils {
    public static int lightContrastColor = -16777216;
    public static int darkContrastColor = -1;
    private static float globalAlpha = -1.0f;

    private ColorUtils() {
    }

    public static int multiplyAlpha(int color, float factor) {
        int[] rgba = ColorUtils.unpackRgba(color);
        return ColorUtils.rgba(rgba[0], rgba[1], rgba[2], Math.round((float)rgba[3] * factor));
    }

    public static int lerp(int firstColor, int secondColor, double progress) {
        float[] first = ColorUtils.toNormalizedRgba(firstColor);
        float[] second = ColorUtils.toNormalizedRgba(secondColor);
        return ColorUtils.rgba(ColorUtils.lerpChannel(first[0], second[0], progress) * 255.0, ColorUtils.lerpChannel(first[1], second[1], progress) * 255.0, ColorUtils.lerpChannel(first[2], second[2], progress) * 255.0, ColorUtils.lerpChannel(first[3], second[3], progress) * 255.0);
    }

    public static int withAlpha(int color, float alpha) {
        float[] rgba = ColorUtils.toNormalizedRgba(color);
        return ColorUtils.rgba(rgba[0] * 255.0f, rgba[1] * 255.0f, rgba[2] * 255.0f, alpha);
    }

    public static Color withAlpha(Color color, float alpha) {
        return new Color(ColorUtils.withAlpha(color.hashCode(), alpha), true);
    }

    public static float[] toNormalizedRgba(int color) {
        return new float[]{(float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f, (float)(color >> 24 & 0xFF) / 255.0f};
    }

    public static float[] toNormalizedRgb(int color) {
        float[] rgba = ColorUtils.toNormalizedRgba(color);
        return new float[]{rgba[0], rgba[1], rgba[2]};
    }

    public static int[] unpackRgba(int color) {
        return new int[]{color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >> 24 & 0xFF};
    }

    public static int rgba(int red, int green, int blue, int alpha) {
        if (globalAlpha != -1.0f && alpha != 0) {
            alpha = class_3532.method_15340((int)((int)((float)alpha * (globalAlpha / 255.0f))), (int)0, (int)255);
        }
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static int rgba(double red, double green, double blue, double alpha) {
        return ColorUtils.rgba((int)red, (int)green, (int)blue, (int)alpha);
    }

    public static int rgb(int red, int green, int blue) {
        return ColorUtils.rgba(red, green, blue, 255);
    }

    public static int rgb(double red, double green, double blue) {
        return ColorUtils.rgba((int)red, (int)green, (int)blue, 255);
    }

    public static int grayscale(double brightness, double alpha) {
        return ColorUtils.rgba(brightness, brightness, brightness, alpha);
    }

    public static int grayscale(double brightness) {
        return ColorUtils.grayscale(brightness, 255.0);
    }

    public static int darken(int color, double factor) {
        int[] rgba = ColorUtils.unpackRgba(color);
        return ColorUtils.rgba(Math.max((double)rgba[0] * factor, 0.0), Math.max((double)rgba[1] * factor, 0.0), Math.max((double)rgba[2] * factor, 0.0), (double)rgba[3]);
    }

    public static int darken(int color) {
        return ColorUtils.darken(color, 0.7);
    }

    public static int brighten(int color, double factor) {
        int[] rgba = ColorUtils.unpackRgba(color);
        int floor = (int)(1.0 / (1.0 - factor));
        int red = ColorUtils.liftNonZeroChannel(rgba[0], floor);
        int green = ColorUtils.liftNonZeroChannel(rgba[1], floor);
        int blue = ColorUtils.liftNonZeroChannel(rgba[2], floor);
        if (red == 0 && green == 0 && blue == 0) {
            return ColorUtils.rgba(floor, floor, floor, rgba[3]);
        }
        return ColorUtils.rgba(Math.min((int)((double)red / factor), 255), Math.min((int)((double)green / factor), 255), Math.min((int)((double)blue / factor), 255), rgba[3]);
    }

    public static int brighten(int color) {
        return ColorUtils.brighten(color, 0.7);
    }

    public static int lighten(int color, double amount) {
        float[] rgba = ColorUtils.toNormalizedRgba(color);
        return ColorUtils.rgba(Math.min((double)rgba[0] + 0.1 * amount, 1.0) * 255.0, Math.min((double)rgba[1] + 0.1 * amount, 1.0) * 255.0, Math.min((double)rgba[2] + 0.1 * amount, 1.0) * 255.0, (double)rgba[3] * 255.0);
    }

    public static int lightenSubtle(int color, double amount) {
        float[] rgba = ColorUtils.toNormalizedRgba(color);
        return ColorUtils.rgba(Math.min((double)rgba[0] + 0.06 * amount, 1.0) * 255.0, Math.min((double)rgba[1] + 0.06 * amount, 1.0) * 255.0, Math.min((double)rgba[2] + 0.06 * amount, 1.0) * 255.0, (double)rgba[3] * 255.0);
    }

    public static int animatedGradient(int firstColor, int secondColor, int phaseOffset, int speed) {
        int phase = (int)((System.currentTimeMillis() / (long)speed + (long)phaseOffset) % 360L);
        phase = (phase > 180 ? 360 - phase : phase) + 180;
        int color = ColorUtils.lerp(firstColor, secondColor, class_3532.method_15363((float)((float)phase / 180.0f - 1.0f), (float)0.0f, (float)1.0f));
        float[] rgb = ColorUtils.toNormalizedRgb(color);
        float[] hsb = Color.RGBtoHSB((int)(rgb[0] * 255.0f), (int)(rgb[1] * 255.0f), (int)(rgb[2] * 255.0f), null);
        hsb[1] = Math.min(hsb[1] * 1.5f, 1.0f);
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
    }

    public static float hue(int red, int green, int blue) {
        float minimum;
        float r = (float)red / 255.0f;
        float g = (float)green / 255.0f;
        float b = (float)blue / 255.0f;
        float maximum = Math.max(r, Math.max(g, b));
        float delta = maximum - (minimum = Math.min(r, Math.min(g, b)));
        float degrees = delta == 0.0f ? 0.0f : (maximum == r ? 60.0f * ((g - b) / delta % 6.0f) : (maximum == g ? 60.0f * ((b - r) / delta + 2.0f) : 60.0f * ((r - g) / delta + 4.0f)));
        if (degrees < 0.0f) {
            degrees += 360.0f;
        }
        return degrees / 360.0f;
    }

    public static double luminance(int red, int green, int blue) {
        return ((double)red * 0.299 + (double)green * 0.587 + (double)blue * 0.114) / 255.0;
    }

    public static int contrastColor(int red, int green, int blue) {
        return ColorUtils.luminance(red, green, blue) > 0.5 ? lightContrastColor : darkContrastColor;
    }

    public static int lerpToDarkContrast(int color, double progress) {
        return ColorUtils.lerp(color, darkContrastColor, progress);
    }

    public static void setGlobalAlpha(float alpha) {
        globalAlpha = alpha;
    }

    public static void clearGlobalAlpha() {
        globalAlpha = -1.0f;
    }

    private static double lerpChannel(float first, float second, double progress) {
        return (double)first + (double)(second - first) * progress;
    }

    private static int liftNonZeroChannel(int channel, int floor) {
        return channel > 0 && channel < floor ? floor : channel;
    }
}

