package ru.wexside.misc;

public class AlphaStack {
    private final float[] values;
    private int index;
    private static final int MAX_DEPTH = 128;

    public AlphaStack(float[] fArray) {
        this.values = fArray;
    }

    public AlphaStack() {
        this(new float[128]);
    }

    public void reset() {
        this.setCurrentAlpha(1.0f);
    }

    public void verifyBalanced() {
        if (this.index > 0) {
            throw new IllegalStateException("Stack overflow");
        }
    }

    public float getCurrentAlpha() {
        return this.values[this.index];
    }

    public int packArgb(int n, int n2, int n3, int n4) {
        return n << 24 | n2 << 16 | n3 << 8 | n4;
    }

    public void setCurrentAlpha(float f) {
        this.values[this.index] = f;
    }

    public void pop() {
        int n = this.index - 1;
        if (n < 0) {
            throw new IllegalStateException("Stack underflow");
        }
        this.index = n;
    }

    public int packWithCurrentAlpha(int n, int n2, int n3, int n4) {
        int n5 = Math.clamp((long)Math.round((float)n * this.getCurrentAlpha()), 0, 255);
        return this.packArgb(n5, n2, n3, n4);
    }

    public void multiplyCurrentAlpha(float f) {
        int n = this.index;
        this.values[n] = this.values[n] * f;
    }

    public void push() {
        int n = this.index + 1;
        if (n >= this.values.length) {
            throw new IllegalStateException("Stack overflow");
        }
        this.values[n] = this.values[n - 1];
        this.index = n;
    }

    public int applyToColor(int n) {
        return this.applyToColorWithAlpha(n, 255);
    }

    public int applyToColorWithAlpha(int n, int n2) {
        int n3 = n >> 16 & 0xFF;
        int n4 = n >> 8 & 0xFF;
        int n5 = n & 0xFF;
        int n6 = Math.clamp((long)Math.round((float)n2 * this.getCurrentAlpha()), 0, 255);
        return this.packArgb(n6, n3, n4, n5);
    }

    public int getTransparentColor() {
        return this.packArgb(0, 0, 0, 0);
    }

    public int rgbWithCurrentAlpha(int n, int n2, int n3) {
        return this.packWithCurrentAlpha(255, n, n2, n3);
    }
}

