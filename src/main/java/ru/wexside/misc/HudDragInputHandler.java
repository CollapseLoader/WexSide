package ru.wexside.misc;

import net.minecraft.class_310;
import net.minecraft.class_408;
import net.minecraft.class_490;
import ru.wexside.WexSideClient;
import ru.wexside.misc.ColorPlaneInputHandler;
import ru.wexside.misc.GuiInteractionState;
import ru.wexside.misc.HudDragRegistry;
import ru.wexside.util.AbstractHudElement;

public final class HudDragInputHandler
implements ColorPlaneInputHandler {
    private final HudDragRegistry hudDragRegistry;

    public HudDragInputHandler(HudDragRegistry hudDragRegistry) {
        this.hudDragRegistry = hudDragRegistry;
    }

    public void updateScreenState() {
        class_310 client = class_310.method_1551();
        double scale = client.method_22683().method_4495();
        GuiInteractionState mouse = GuiInteractionState.getInstance();
        mouse.setRawMousePosition((int)client.field_1729.method_1603(), (int)client.field_1729.method_1604());
        mouse.setScaledMousePosition((int)(client.field_1729.method_1603() / scale), (int)(client.field_1729.method_1604() / scale));
        if (!this.isEditorScreen()) {
            this.hudDragRegistry.clearSelection();
        }
    }

    @Override
    public boolean onMousePressed(float x, float y, int button) {
        if (button == 1) {
            AbstractHudElement element = WexSideClient.getInventoryHudRegistry().findAt(x, y);
            if (element == null) {
                this.hudDragRegistry.clearSelection();
                return false;
            }
            this.hudDragRegistry.select(element);
            return true;
        }
        if (button != 0) {
            return false;
        }
        if (this.hudDragRegistry.onMousePressed(x, y)) {
            return true;
        }
        AbstractHudElement element = WexSideClient.getInventoryHudRegistry().findAt(x, y);
        if (element != null) {
            this.hudDragRegistry.select(element);
            return this.hudDragRegistry.onMousePressed(x, y);
        }
        this.hudDragRegistry.clearSelection();
        return false;
    }

    public void onMouseReleased(int button) {
        if (button == 0) {
            this.hudDragRegistry.clearSelection();
        }
    }

    public boolean isEditorScreen() {
        return class_310.method_1551().field_1755 instanceof class_408 || class_310.method_1551().field_1755 instanceof class_490;
    }
}

