package ru.wexside.util;

public final class StencilState {
    private final int reference;
    private int mode;

    public StencilState(int reference, int mode) {
        this.reference = reference;
        this.mode = mode;
    }

    public int mode() {
        return this.mode;
    }

    public int reference() {
        return this.reference;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}

