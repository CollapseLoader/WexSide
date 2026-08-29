package ru.wexside.render;

import ru.wexside.render.MaskTexture;

final class StencilMaskState {
    final int reference;
    final MaskTexture target;
    boolean writing = true;
    boolean clearBeforeWrite = true;

    StencilMaskState(int reference, MaskTexture target) {
        this.reference = reference;
        this.target = target;
    }
}

