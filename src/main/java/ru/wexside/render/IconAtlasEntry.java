package ru.wexside.render;

import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL30;
import ru.wexside.render.IconAtlas;

public final class IconAtlasEntry {
    private int slot;
    private boolean enabled;
    private boolean enabled2;
    private int slot2;
    private int slot3 = -1;
    private int slot4 = -1;
    private IconAtlas field40;
    private int slot5;
    private float renderScale = -1.0f;

    public IconAtlasEntry() {
    }

    public IconAtlasEntry(IconAtlas renderPipeline12) {
        this.field40 = renderPipeline12;
    }

    public IconAtlasEntry(boolean bl) {
        this.enabled2 = bl;
    }

    public boolean process(float f) {
        return !this.enabled || f != this.renderScale;
    }

    public void update() {
        this.enabled = false;
        this.renderScale = -1.0f;
    }

    public boolean isActive() {
        if (this.field40 != null) {
            return this.isActive4() && this.field40.getIntType5() != 0 && this.slot2 > 0;
        }
        return this.slot5 != 0 && this.slot2 > 0;
    }

    public int getIntType() {
        return this.slot2;
    }

    public int getIntType2() {
        return this.field40 != null && this.isActive4() ? this.field40.process2(this.slot4) : 0;
    }

    public int getIntType3() {
        return this.field40 != null ? this.field40.getIntType2() : this.slot2;
    }

    public void update2() {
        if (this.field40 != null) {
            if (this.isActive4()) {
                this.field40.setIntType(this.slot4);
            }
            this.slot4 = -1;
            this.slot3 = -1;
            this.slot2 = 0;
            this.enabled = false;
            this.renderScale = -1.0f;
            return;
        }
        if (this.slot != 0) {
            GL30.glDeleteFramebuffers((int)this.slot);
            this.slot = 0;
        }
        if (this.slot5 != 0) {
            GL30.glDeleteTextures((int)this.slot5);
            this.slot5 = 0;
        }
        this.slot2 = 0;
    }

    public int getIntType4() {
        return this.field40 != null ? this.field40.getIntType5() : this.slot5;
    }

    public int getIntType5() {
        return this.field40 != null && this.isActive4() ? this.field40.process(this.slot4) : 0;
    }

    public float getFloatType() {
        return this.field40 == null ? 1.0f : (float)(this.getIntType5() + this.slot2) / (float)this.field40.getIntType2();
    }

    public boolean isActive2() {
        return this.enabled;
    }

    private void allocateAtlasRegion(int n) {
        if (n <= 0) {
            return;
        }
        this.field40.setIntType2(n);
        if (this.slot4 >= 0 && this.slot3 != this.field40.getIntType3()) {
            this.slot4 = -1;
        }
        if (this.slot4 < 0) {
            this.slot4 = this.field40.getIntType();
            if (this.slot4 < 0) {
                this.slot2 = 0;
                return;
            }
            this.slot3 = this.field40.getIntType3();
            this.enabled = false;
            this.renderScale = -1.0f;
        }
        this.slot2 = n;
    }

    public boolean isActive3() {
        return this.enabled2;
    }

    public float getFloatType2() {
        return this.field40 == null ? 1.0f : (float)(this.getIntType2() + this.slot2) / (float)this.field40.getIntType2();
    }

    public float getFloatType3() {
        return this.renderScale;
    }

    private boolean isActive4() {
        return this.slot4 >= 0 && this.slot3 == this.field40.getIntType3();
    }

    public int getIntType6() {
        return this.field40 != null ? this.field40.getIntType4() : this.slot;
    }

    public float getFloatType4() {
        return this.field40 == null ? 0.0f : (float)this.getIntType5() / (float)this.field40.getIntType2();
    }

    public float getFloatType5() {
        return this.field40 == null ? 0.0f : (float)this.getIntType2() / (float)this.field40.getIntType2();
    }

    public int getIntType7() {
        return this.slot3;
    }

    public int getIntType8() {
        return this.slot4;
    }

    public IconAtlas getRenderPipeline12() {
        return this.field40;
    }

    public void process2(float f, boolean bl) {
        this.renderScale = f;
        this.enabled = bl;
    }

    public void setIntType(int n) {
        if (this.field40 != null) {
            this.allocateAtlasRegion(n);
            return;
        }
        if (n <= 0 || n == this.slot2 && this.slot5 != 0) {
            return;
        }
        this.update2();
        int n2 = GL30.glGetInteger((int)32873);
        int n3 = GL30.glGetInteger((int)36006);
        int n4 = this.enabled2 ? 9728 : 9729;
        this.slot5 = GL30.glGenTextures();
        GL30.glBindTexture((int)3553, (int)this.slot5);
        GL30.glTexParameteri((int)3553, (int)10241, (int)n4);
        GL30.glTexParameteri((int)3553, (int)10240, (int)n4);
        GL30.glTexParameteri((int)3553, (int)10242, (int)33071);
        GL30.glTexParameteri((int)3553, (int)10243, (int)33071);
        int n5 = GL30.glGetInteger((int)35055);
        GL30.glBindBuffer((int)35052, (int)0);
        GL30.glTexImage2D((int)3553, (int)0, (int)32856, (int)n, (int)n, (int)0, (int)6408, (int)5121, (ByteBuffer)null);
        GL30.glBindBuffer((int)35052, (int)n5);
        this.slot = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer((int)36160, (int)this.slot);
        GL30.glFramebufferTexture2D((int)36160, (int)36064, (int)3553, (int)this.slot5, (int)0);
        int n6 = GL30.glCheckFramebufferStatus((int)36160);
        if (n6 != 36053) {
            int n7 = n6;
            throw new IllegalStateException("Icon framebuffer is incomplete: " + n7);
        }
        GL30.glBindFramebuffer((int)36160, (int)n3);
        GL30.glBindTexture((int)3553, (int)n2);
        this.slot2 = n;
    }
}

