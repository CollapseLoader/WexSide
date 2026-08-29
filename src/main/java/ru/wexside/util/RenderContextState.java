package ru.wexside.util;

import java.util.Deque;
import ru.wexside.misc.AlphaStack;
import ru.wexside.misc.ScissorStack;
import ru.wexside.util.StencilState;

final class RenderContextState {
    private final AlphaStack colors;
    private final ScissorStack scissors;
    private final int stencilMode;
    private final Deque<StencilState> stencilStates;

    RenderContextState(AlphaStack colors, ScissorStack scissors, int stencilMode, Deque<StencilState> stencilStates) {
        this.colors = colors;
        this.scissors = scissors;
        this.stencilMode = stencilMode;
        this.stencilStates = stencilStates;
    }

    AlphaStack colors() {
        return this.colors;
    }

    ScissorStack scissors() {
        return this.scissors;
    }

    int stencilMode() {
        return this.stencilMode;
    }

    Deque<StencilState> stencilStates() {
        return this.stencilStates;
    }
}

