package ru.wexside.ui;

import org.joml.Matrix4f;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.GuiInteractionState;
import ru.wexside.misc.ThemeColors;
import ru.wexside.ui.HudElementLayout;
import ru.wexside.util.AbstractHudElement;
import ru.wexside.util.GuiDrawApi;

public final class HudDragHandle {
    private final AbstractHudElement element;
    private boolean selected;
    private boolean dragging;
    private float dragOffsetX;
    private float dragOffsetY;

    public HudDragHandle(AbstractHudElement element) {
        this.element = element;
    }

    public boolean isVisible() {
        return this.element.getLayout().isVisible();
    }

    public void renderOutline(GuiDrawApi renderer, Matrix4f matrix) {
        HudElementLayout layout = this.element.getLayout();
        if (this.dragging) {
            GuiInteractionState mouse = GuiInteractionState.getInstance();
            layout.setPosition((float)mouse.getScaledMouseX() - this.dragOffsetX, (float)mouse.getScaledMouseY() - this.dragOffsetY);
        }
        int color = this.selected ? ThemeColors.accent() : ThemeColors.borderPrimary();
        renderer.drawRoundedRectangleOutlined(matrix, layout.getX(), layout.getY(), layout.getWidth(), layout.getHeight(), 3.0f, 1.0f, 0, color);
    }

    public void renderLabel(GuiDrawApi renderer, Matrix4f matrix) {
        HudElementLayout layout = this.element.getLayout();
        FontRegistry.font2.process2(matrix, renderer, this.element.getName(), layout.getX() + 3.0f, Math.max(0.0f, layout.getY() - 8.0f), 6.0f, ThemeColors.textPrimary());
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public boolean click(float mouseX, float mouseY) {
        boolean inside;
        HudElementLayout layout = this.element.getLayout();
        boolean bl = inside = mouseX >= layout.getX() && mouseX <= layout.getX() + layout.getWidth() && mouseY >= layout.getY() && mouseY <= layout.getY() + layout.getHeight();
        if (!inside) {
            this.dragging = false;
            return false;
        }
        this.selected = true;
        this.dragging = true;
        this.dragOffsetX = mouseX - layout.getX();
        this.dragOffsetY = mouseY - layout.getY();
        return true;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void select() {
        this.selected = true;
    }

    public void clearSelection() {
        this.selected = false;
        this.dragging = false;
    }

    public void stopDragging() {
        this.dragging = false;
    }
}

