package ru.wexside.render;

import ru.wexside.misc.ModelSurfaceMode;

public record SurfaceRenderKey(boolean depthTestEnabled, ModelSurfaceMode effect) {
}
