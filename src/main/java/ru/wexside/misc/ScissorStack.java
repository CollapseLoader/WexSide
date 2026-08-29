package ru.wexside.misc;

import java.util.ArrayDeque;
import java.util.Deque;
import ru.wexside.misc.OpenGlScissorRegion;

public class ScissorStack {
    private static final int MAX_DEPTH = 16;
    private final Deque<OpenGlScissorRegion> deque = new ArrayDeque<OpenGlScissorRegion>(16);

    public void push(int n, int n2, int n3, int n4) {
        if (this.deque.size() >= 16) {
            this.throwOverflow();
        }
        OpenGlScissorRegion openGlScissorRegion = new OpenGlScissorRegion(n, n2, n3, n4);
        this.deque.push(openGlScissorRegion);
        openGlScissorRegion.apply();
    }

    public void pop() {
        if (this.deque.isEmpty()) {
            this.throwUnderflow();
        }
        this.deque.pop();
        this.peek().apply();
    }

    public void verifyBalanced() {
        this.deque.pop();
        if (!this.deque.isEmpty()) {
            this.throwOverflow();
        }
    }

    public OpenGlScissorRegion peek() {
        OpenGlScissorRegion openGlScissorRegion = this.deque.peek();
        if (openGlScissorRegion == null) {
            this.throwUnderflow();
        }
        return openGlScissorRegion;
    }

    private void throwOverflow() {
        throw new IllegalStateException("Stack overflow");
    }

    private void throwUnderflow() {
        throw new IllegalStateException("Stack underflow");
    }
}

