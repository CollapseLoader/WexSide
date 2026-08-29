package ru.wexside.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1799;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BakedIconEntry;
import ru.wexside.render.BakedItemIcon;
import ru.wexside.render.ItemIconCache;
import ru.wexside.util.GuiDrawApi;

public final class ItemIconRenderer {
    private final ItemIconCache cache = new ItemIconCache();
    private final List<BakedIconEntry> pendingBakes = new ArrayList<BakedIconEntry>();

    public BakedItemIcon get(class_1799 stack) {
        return stack == null || stack.method_7960() ? null : this.cache.get(stack);
    }

    public void collectBakes() {
        this.pendingBakes.clear();
        this.cache.collectBakeEntries(2.0f, this.pendingBakes);
        if (!this.pendingBakes.isEmpty()) {
            WexSideClient.getRenderPipeline2().setList(this.pendingBakes);
        }
    }

    public void render(GuiDrawApi renderer, Matrix4f matrix, BakedItemIcon icon, float x, float y, float size, int color) {
        this.cache.render(renderer, matrix, icon, x, y, size, color);
    }

    public void evictUnused() {
        this.cache.evictUnused();
    }

    public void beginFrame() {
        this.cache.beginFrame();
    }

    public void close() {
        this.cache.close();
    }

    public BakedItemIcon process(class_1799 stack) {
        return this.get(stack);
    }

    public void update() {
        this.collectBakes();
    }

    public void process2(GuiDrawApi renderer, Matrix4f matrix, BakedItemIcon icon, float x, float y, float size, int color) {
        this.render(renderer, matrix, icon, x, y, size, color);
    }

    public void update2() {
        this.evictUnused();
    }

    public void update3() {
        this.beginFrame();
    }
}

