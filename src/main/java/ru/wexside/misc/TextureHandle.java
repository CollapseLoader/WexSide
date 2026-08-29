package ru.wexside.misc;

public interface TextureHandle {
    public int getTextureId();

    public int getHeight();

    public int getWidth();

    default public int getGeneration() {
        return 0;
    }
}

