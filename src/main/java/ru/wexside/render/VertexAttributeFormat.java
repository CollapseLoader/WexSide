package ru.wexside.render;

public enum VertexAttributeFormat {
    float1(5126, 1, 4, false, false),
    float2(5126, 2, 4, false, false),
    float4(5126, 4, 4, false, false),
    color4(5121, 4, 1, true, false),
    byte1(5120, 1, 1, false, true);

    private final int glType;
    private final int componentCount;
    private final int componentSize;
    private final boolean normalized;
    private final boolean integer;

    private VertexAttributeFormat(int glType, int componentCount, int componentSize, boolean normalized, boolean integer) {
        this.glType = glType;
        this.componentCount = componentCount;
        this.componentSize = componentSize;
        this.normalized = normalized;
        this.integer = integer;
    }

    public int byteSize() {
        return this.componentCount * this.componentSize;
    }

    public int glType() {
        return this.glType;
    }

    public int componentCount() {
        return this.componentCount;
    }

    public boolean normalized() {
        return this.normalized;
    }

    public boolean integer() {
        return this.integer;
    }
}

