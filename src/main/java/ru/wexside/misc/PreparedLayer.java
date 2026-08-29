package ru.wexside.misc;

import java.util.Objects;
import org.joml.Matrix4f;
import ru.wexside.misc.TextureHandle;

public final class PreparedLayer {
    private final Matrix4f contentMatrix;
    private final float maxU;
    private final float drawWidth;
    private final float contentX;
    private final float drawY;
    private final float maxV;
    private final float drawHeight;
    private final float contentY;
    private final float drawX;
    private final TextureHandle texture;

    public PreparedLayer(TextureHandle texture, Matrix4f contentMatrix, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        this.texture = texture;
        this.contentMatrix = contentMatrix;
        this.contentX = f;
        this.contentY = f2;
        this.drawX = f3;
        this.drawY = f4;
        this.drawWidth = f5;
        this.drawHeight = f6;
        this.maxU = f7;
        this.maxV = f8;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof PreparedLayer)) {
            return false;
        }
        PreparedLayer preparedLayer = (PreparedLayer)object;
        return Objects.equals(this.texture, preparedLayer.texture) && Objects.equals(this.contentMatrix, preparedLayer.contentMatrix) && Float.compare(this.contentX, preparedLayer.contentX) == 0 && Float.compare(this.contentY, preparedLayer.contentY) == 0 && Float.compare(this.drawX, preparedLayer.drawX) == 0 && Float.compare(this.drawY, preparedLayer.drawY) == 0 && Float.compare(this.drawWidth, preparedLayer.drawWidth) == 0 && Float.compare(this.drawHeight, preparedLayer.drawHeight) == 0 && Float.compare(this.maxU, preparedLayer.maxU) == 0 && Float.compare(this.maxV, preparedLayer.maxV) == 0;
    }

    public String toString() {
        float f = this.maxV;
        float f2 = this.maxU;
        float f3 = this.drawHeight;
        float f4 = this.drawWidth;
        float f5 = this.drawY;
        float f6 = this.drawX;
        float f7 = this.contentY;
        float f8 = this.contentX;
        String string = String.valueOf(this.contentMatrix);
        String string2 = String.valueOf(this.texture);
        return "PreparedLayer[target=" + string2 + ", contentMatrix=" + string + ", contentX=" + f8 + ", contentY=" + f7 + ", drawX=" + f6 + ", drawY=" + f5 + ", drawWidth=" + f4 + ", drawHeight=" + f3 + ", maxU=" + f2 + ", maxV=" + f + "]";
    }

    public int hashCode() {
        return Objects.hash(this.texture, this.contentMatrix, Float.valueOf(this.contentX), Float.valueOf(this.contentY), Float.valueOf(this.drawX), Float.valueOf(this.drawY), Float.valueOf(this.drawWidth), Float.valueOf(this.drawHeight), Float.valueOf(this.maxU), Float.valueOf(this.maxV));
    }

    public float contentX() {
        return this.contentX;
    }

    public Matrix4f getContentMatrix() {
        return this.contentMatrix;
    }

    public float contentY() {
        return this.contentY;
    }

    public float drawHeight() {
        return this.drawHeight;
    }

    public float drawY() {
        return this.drawY;
    }

    public float drawX() {
        return this.drawX;
    }

    public float drawWidth() {
        return this.drawWidth;
    }

    public float maxV() {
        return this.maxV;
    }

    public float maxU() {
        return this.maxU;
    }

    public TextureHandle getTexture() {
        return this.texture;
    }
}

