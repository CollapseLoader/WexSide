package ru.wexside.misc;

import java.util.Objects;
import org.lwjgl.opengl.GL33;

public final class OpenGlScissorRegion {
    private final int height;
    private final int x;
    private final int y;
    private final int width;

    public OpenGlScissorRegion(int n, int n2, int n3, int n4) {
        this.x = n;
        this.y = n2;
        this.width = n3;
        this.height = n4;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof OpenGlScissorRegion)) {
            return false;
        }
        OpenGlScissorRegion openGlScissorRegion = (OpenGlScissorRegion)object;
        return this.x == openGlScissorRegion.x && this.y == openGlScissorRegion.y && this.width == openGlScissorRegion.width && this.height == openGlScissorRegion.height;
    }

    public String toString() {
        int n = this.height;
        int n2 = this.width;
        int n3 = this.y;
        int n4 = this.x;
        return "ScissorRegion[x=" + n4 + ", y=" + n3 + ", width=" + n2 + ", height=" + n + "]";
    }

    public int hashCode() {
        return Objects.hash(this.x, this.y, this.width, this.height);
    }

    public int getX() {
        return this.x;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getY() {
        return this.y;
    }

    public void apply() {
        GL33.glScissor((int)this.x, (int)this.y, (int)this.width, (int)this.height);
    }
}

