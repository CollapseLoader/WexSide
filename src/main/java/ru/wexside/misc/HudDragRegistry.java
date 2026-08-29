package ru.wexside.misc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.ui.HudDragHandle;
import ru.wexside.util.AbstractHudElement;
import ru.wexside.util.GuiDrawApi;

public final class HudDragRegistry {
    private final Map<AbstractHudElement, HudDragHandle> handles = new LinkedHashMap<AbstractHudElement, HudDragHandle>();

    public void render() {
        ArrayList<HudDragHandle> visibleHandles = new ArrayList<HudDragHandle>();
        for (HudDragHandle handle : this.handles.values()) {
            if (!handle.isVisible() || !handle.isSelected()) continue;
            visibleHandles.add(handle);
        }
        if (visibleHandles.isEmpty()) {
            return;
        }
        GuiDrawApi renderer = WexSideClient.getHudRenderer();
        if (renderer == null) {
            return;
        }
        Matrix4f matrix = new Matrix4f().scale(2.0f);
        renderer.begin();
        for (HudDragHandle handle : visibleHandles) {
            handle.renderOutline(renderer, matrix);
        }
        for (HudDragHandle handle : visibleHandles) {
            handle.renderLabel(renderer, matrix);
        }
        renderer.end();
    }

    public boolean onMousePressed(float x, float y) {
        for (HudDragHandle handle : this.handles.values()) {
            if (!handle.isDragging()) continue;
            handle.click(x, y);
            return true;
        }
        for (HudDragHandle handle : this.handles.values()) {
            if (!handle.click(x, y)) continue;
            return true;
        }
        return false;
    }

    public void select(AbstractHudElement element) {
        HudDragHandle handle = this.handles.computeIfAbsent(element, HudDragHandle::new);
        boolean wasSelected = handle.isSelected();
        this.clearSelection();
        if (!wasSelected) {
            handle.select();
        }
    }

    public void clearSelection() {
        for (HudDragHandle handle : this.handles.values()) {
            handle.clearSelection();
        }
    }

    public void stopDragging() {
        for (HudDragHandle handle : this.handles.values()) {
            handle.stopDragging();
        }
    }
}

