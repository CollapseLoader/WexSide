package ru.wexside.misc;

import java.util.Deque;
import ru.wexside.misc.StencilFrameState;
import ru.wexside.util.GlRenderStateSnapshot;

public record RenderFrameContext(GlRenderStateSnapshot stateSnapshot, boolean shaderBound, int stencilMode, Deque<StencilFrameState> stencilStates) {
}

