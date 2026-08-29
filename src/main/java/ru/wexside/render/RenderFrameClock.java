package ru.wexside.render;

public final class RenderFrameClock {
    private static final long START_NANOS = System.nanoTime();
    private static int frame;

    private RenderFrameClock() {
    }

    public static float elapsedSeconds() {
        return (float)(System.nanoTime() - START_NANOS) / 1.0E9f;
    }

    public static int currentFrame() {
        return frame;
    }

    public static void advanceFrame() {
        ++frame;
    }
}

