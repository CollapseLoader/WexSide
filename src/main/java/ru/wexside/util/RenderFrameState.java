package ru.wexside.util;

import java.util.Deque;
import ru.wexside.misc.AlphaStack;
import ru.wexside.misc.ScissorStack;
import ru.wexside.util.RenderContextState;
import ru.wexside.util.StencilState;

record RenderFrameState(AlphaStack alphaStack, ScissorStack scissorStack, Deque<RenderContextState> contextStates, Deque<StencilState> stencilStates, int framebufferWidth, int framebufferHeight, int stencilMode, boolean layerFrame, float layerOriginX, float layerOriginY) {
}

