package ru.wexside.misc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import net.minecraft.class_1934;
import net.minecraft.class_268;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_640;
import org.joml.Matrix4f;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.ThemeColors;
import ru.wexside.util.AbstractHudElement;
import ru.wexside.util.GuiDrawApi;

public final class StaffList
extends AbstractHudElement {
    private static final List<String> RANK_MARKERS = List.of("\u0445\u0435\u043b\u043f\u0435\u0440", "helper", "\u043c\u043e\u0434\u0435\u0440", "moder", "developer", "\u0430\u0434\u043c\u0438\u043d", "admin", "\u044e\u0442\u0443\u0431", "youtube", "\u043f\u043e\u043c\u043e\u0449\u043d\u0438\u043a", "\u0441\u043e\u0442\u0440\u0443\u0434\u043d\u0438\u043a", "\u0437\u0430\u043c\u0435\u0441\u0442\u0438\u0442\u0435\u043b\u044c", "\u043a\u0443\u0440\u0430\u0442\u043e\u0440", "\u0441\u0442\u0430\u0436\u0451\u0440", "staff");
    private final List<StaffEntry> staff = new ArrayList<StaffEntry>();
    private float animatedWidth = 80.0f;
    private float animatedHeight = 18.0f;

    public StaffList(BooleanSupplier visible) {
        super("Staff List", visible);
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
        this.collectStaff();
        return !this.staff.isEmpty();
    }

    @Override
    protected void updateLayout() {
        this.collectStaff();
        float targetWidth = StaffList.titleWidth();
        for (StaffEntry entry : this.staff) {
            String row = entry.name() + "  " + entry.rank();
            targetWidth = Math.max(targetWidth, FontRegistry.font4.process3(row, 6.5f) + 18.0f);
        }
        targetWidth = Math.max(80.0f, targetWidth);
        float targetHeight = 18.0f + (this.staff.isEmpty() ? 0.0f : 4.5f + (float)this.staff.size() * 13.5f + 5.0f);
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
        FontRegistry.font7.process2(matrix, renderer, "Staff List", x + 5.0f * scale, y + 4.5f * scale, 8.0f * scale, color);
        FontRegistry.font3.process5(matrix, renderer, "\u0416", x + width - 13.0f * scale, y + 6.0f * scale, 8.0f * scale, color);
        float rowY = y + 22.5f * scale;
        for (StaffEntry entry : this.staff) {
            String rank = ((String)(entry.spectator() ? entry.rank() + " [S]" : entry.rank())).toUpperCase(Locale.ROOT);
            float rankTextWidth = FontRegistry.font6.process3(rank, 5.5f);
            float badgeWidth = (rankTextWidth + 8.0f) * scale;
            float badgeX = x + width - 5.0f * scale - badgeWidth;
            FontRegistry.font4.process2(matrix, renderer, entry.name(), x + 5.0f * scale, rowY + (10.5f - FontRegistry.font4.process4(entry.name(), 6.5f)) * 0.5f * scale, 6.5f * scale, color);
            renderer.drawRoundedOutline(matrix, badgeX, rowY, badgeWidth, 10.5f * scale, 6.0f * scale, scale, ThemeColors.separator());
            FontRegistry.font6.process2(matrix, renderer, rank, badgeX + (badgeWidth - rankTextWidth * scale) * 0.5f, rowY + (10.5f - FontRegistry.font6.process4(rank, 5.5f)) * 0.5f * scale, 5.5f * scale, ThemeColors.hudTextMuted());
            rowY += 13.5f * scale;
        }
        renderer.endStencil();
    }

    private static float titleWidth() {
        return 5.0f + FontRegistry.font7.process3("Staff List", 8.0f) + 19.0f;
    }

    private void collectStaff() {
        this.staff.clear();
        class_634 network = class_310.method_1551().method_1562();
        if (network == null) {
            return;
        }
        for (class_640 player : network.method_2880()) {
            String name = player.method_2966().name();
            String rank = StaffList.teamLabel(player.method_2955());
            if (name == null || !StaffList.containsRankMarker(rank)) continue;
            this.staff.add(new StaffEntry(name, rank, player.method_2958() == class_1934.field_9219));
        }
        this.staff.sort(Comparator.comparing(StaffEntry::name, String.CASE_INSENSITIVE_ORDER));
        if (this.staff.isEmpty() && this.isEditorScreen()) {
            this.staff.add(new StaffEntry("markushv", "\u26a1 \u0410\u0434\u043c\u0438\u043d", false));
            this.staff.add(new StaffEntry("annihilatorq", "\u26a1 \u0421\u0442\u0440\u0430\u0436", false));
            this.staff.add(new StaffEntry("Hunger", "\u26a1 \u041c\u043e\u0434\u0435\u0440\u0430\u0442\u043e\u0440", false));
        }
    }

    private static String teamLabel(class_268 team) {
        if (team == null) {
            return "";
        }
        return (team.method_1144().getString() + team.method_1136().getString()).replaceAll("\u00a7.", "").trim();
    }

    private static boolean containsRankMarker(String rank) {
        String normalized = rank.toLowerCase(Locale.ROOT);
        return RANK_MARKERS.stream().anyMatch(normalized::contains);
    }

    private record StaffEntry(String name, String rank, boolean spectator) {
    }
}

