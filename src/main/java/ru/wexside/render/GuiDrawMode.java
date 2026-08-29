package ru.wexside.render;

public enum GuiDrawMode {
    COLOR(0),
    TEXTURE(1),
    ROUNDED_RECTANGLE(2),
    ROUNDED_TEXTURE(3),
    MSDF_TEXT(4),
    BLURRED_ROUNDED_RECTANGLE(5),
    GRADIENT_ROUNDED_RECTANGLE(6),
    CIRCLE(7),
    BLURRED_RECTANGLE(8),
    TRIANGLE(9),
    SHADOW(10),
    ROUNDED_SHADOW(11),
    LAYER_TEXTURE(12),
    ROUNDED_LAYER_TEXTURE(13),
    BACKDROP_STACK(14),
    SHIMMER_HIGHLIGHT(15),
    RING_SECTOR(16);

    private final int shaderId;

    private GuiDrawMode(int shaderId) {
        this.shaderId = shaderId;
    }

    public int shaderId() {
        return this.shaderId;
    }
}

