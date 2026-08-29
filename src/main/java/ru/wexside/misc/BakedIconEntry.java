package ru.wexside.misc;

import net.minecraft.class_332;
import ru.wexside.render.IconAtlasEntry;

public final class BakedIconEntry {
    private static final int BASE_ICON_SIZE = 16;
    private final IconAtlasEntry texture;
    private final IconRenderer renderer;

    public BakedIconEntry(IconAtlasEntry texture, IconRenderer renderer) {
        this.texture = texture;
        this.renderer = renderer;
    }

    public int baseSize() {
        return 16;
    }

    public int requestedSize() {
        return 16;
    }

    public IconAtlasEntry texture() {
        return this.texture;
    }

    public IconRenderer renderer() {
        return this.renderer;
    }

    @FunctionalInterface
    public static interface IconRenderer {
        public void render(class_332 var1, int var2, int var3, int var4);
    }
}

