package ru.wexside.misc;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BooleanSupplier;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.ThemeColors;
import ru.wexside.schedule.ScheduledEvent;
import ru.wexside.util.AbstractHudElement;
import ru.wexside.util.GuiDrawApi;

public final class Schedules
extends AbstractHudElement {
    private static final ZoneId MOSCOW = ZoneId.of("Europe/Moscow");
    private static final float MIN_WIDTH = 75.0f;
    private static final float ROW_HEIGHT = 13.5f;
    private List<ScheduledEvent> events = List.of();
    private float animatedWidth = 75.0f;
    private float animatedHeight = 18.0f;

    public Schedules(BooleanSupplier visible) {
        super("Schedules", visible);
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
        return WexSideClient.getInstance().getEventSchedules() != null;
    }

    @Override
    protected void updateLayout() {
        if (WexSideClient.getInstance().getEventSchedules() == null) {
            this.events = List.of();
            return;
        }
        this.events = WexSideClient.getInstance().getEventSchedules().events();
        float targetWidth = Schedules.titleWidth();
        for (ScheduledEvent event : this.events) {
            String row = event.name() + "  " + Schedules.countdown(event);
            targetWidth = Math.max(targetWidth, FontRegistry.font4.process3(row, 6.5f) + 10.0f);
        }
        targetWidth = Math.max(75.0f, targetWidth);
        float targetHeight = 18.0f + (this.events.isEmpty() ? 0.0f : 4.5f + (float)this.events.size() * 13.5f + 5.0f);
        this.animatedWidth = FrameInterpolator.lerpTowards(this.animatedWidth, targetWidth, 30.0f);
        this.animatedHeight = FrameInterpolator.lerpTowards(this.animatedHeight, targetHeight, 30.0f);
    }

    @Override
    protected void renderContent(GuiDrawApi renderer, Matrix4f matrix, float x, float y, float width, float height, float scale) {
        int color = ThemeColors.hudTextPrimary();
        this.renderPanelSurface(renderer, matrix, x, y, width, height, 8.0f, scale);
        renderer.beginStencil(1);
        renderer.drawRoundedRectangle(matrix, x, y, width, height, 8.0f * scale, -1);
        renderer.applyStencilMask(1);
        FontRegistry.font7.process2(matrix, renderer, "Schedules", x + 5.0f * scale, y + 4.5f * scale, 8.0f * scale, color);
        FontRegistry.font3.process5(matrix, renderer, "\u0412", x + width - 13.0f * scale, y + 6.0f * scale, 8.0f * scale, color);
        float rowY = y + 22.5f * scale;
        for (ScheduledEvent event : this.events) {
            String remaining = Schedules.countdown(event);
            FontRegistry.font4.process2(matrix, renderer, event.name(), x + 5.0f * scale, rowY, 6.5f * scale, color);
            float remainingWidth = FontRegistry.font4.process3(remaining, 6.5f * scale);
            FontRegistry.font4.process2(matrix, renderer, remaining, x + width - 5.0f * scale - remainingWidth, rowY, 6.5f * scale, color);
            rowY += 13.5f * scale;
        }
        renderer.endStencil();
    }

    private static float titleWidth() {
        return 5.0f + FontRegistry.font7.process3("Schedules", 8.0f) + 19.0f;
    }

    private static String countdown(ScheduledEvent event) {
        ZonedDateTime now = ZonedDateTime.now(MOSCOW);
        int currentSecond = now.getHour() * 3600 + now.getMinute() * 60 + now.getSecond();
        int remaining = Integer.MAX_VALUE;
        for (int minute : event.minutesOfDay()) {
            int delta = minute * 60 - currentSecond;
            if (delta < 0) {
                delta += 86400;
            }
            remaining = Math.min(remaining, delta);
        }
        return "%02d:%02d:%02d".formatted(remaining / 3600, remaining / 60 % 60, remaining % 60);
    }
}

