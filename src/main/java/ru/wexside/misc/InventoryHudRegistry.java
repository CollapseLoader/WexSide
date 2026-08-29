package ru.wexside.misc;

import java.util.ArrayList;
import java.util.List;
import ru.wexside.ui.HudElementLayout;
import ru.wexside.util.AbstractHudElement;

public final class InventoryHudRegistry {
    private final List<AbstractHudElement> elements = new ArrayList<AbstractHudElement>();

    public List<AbstractHudElement> getElements() {
        return List.copyOf(this.elements);
    }

    public AbstractHudElement findAt(float x, float y) {
        for (int index = this.elements.size() - 1; index >= 0; --index) {
            AbstractHudElement element = this.elements.get(index);
            HudElementLayout layout = element.getLayout();
            if (!layout.isVisible() || x < layout.getX() || y < layout.getY() || x > layout.getX() + layout.getWidth() || y > layout.getY() + layout.getHeight()) continue;
            return element;
        }
        return null;
    }

    public void register(AbstractHudElement element) {
        if (element != null && !this.elements.contains(element)) {
            this.elements.add(element);
        }
    }
}

