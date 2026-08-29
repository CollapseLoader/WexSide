package ru.wexside.render;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

public final class IconAtlas {
    private int slot;
    private final Deque<Integer> deque = new ArrayDeque<Integer>();
    private int slot2;
    private int slot3;
    private int slot4;
    private int slot5;
    private int slot6;
    private int slot7;
    private static final int slot8 = 2;
    private final boolean enabled;
    private int slot9;
    private final int slot10;

    public IconAtlas(int n, boolean bl) {
        this.slot10 = Math.max(1, n);
        this.enabled = bl;
    }

    public int getIntType() {
        Integer n = this.deque.poll();
        if (n != null) {
            return n;
        }
        if (this.slot6 >= this.slot10) {
            return -1;
        }
        return this.slot6++;
    }

    public void setIntType(int n) {
        if (n >= 0 && n < this.slot6) {
            this.deque.push(n);
        }
    }

    public int process(int n) {
        return n % this.slot4 * this.slot2;
    }

    public int getIntType2() {
        return this.slot;
    }

    public int getIntType3() {
        return this.slot9;
    }

    public void setIntType2(int n) {
        if (n <= 0 || n == this.slot7 && this.slot5 != 0) {
            return;
        }
        this.update();
        this.slot7 = n;
        this.slot2 = n + 2;
        this.slot4 = (int)Math.ceil(Math.sqrt(this.slot10));
        this.slot = this.slot4 * this.slot2;
        this.update2();
        ++this.slot9;
    }

    public int process2(int n) {
        return n / this.slot4 * this.slot2;
    }

    public int getIntType4() {
        return this.slot3;
    }

    public void update() {
        if (this.slot3 != 0) {
            GL30.glDeleteFramebuffers((int)this.slot3);
            this.slot3 = 0;
        }
        if (this.slot5 != 0) {
            GL30.glDeleteTextures((int)this.slot5);
            this.slot5 = 0;
        }
        this.slot7 = 0;
        this.slot2 = 0;
        this.slot4 = 0;
        this.slot = 0;
        this.slot6 = 0;
        this.deque.clear();
    }

    private void update2() {
        int n = GL30.glGetInteger((int)32873);
        int n2 = GL30.glGetInteger((int)36006);
        float[] fArray = new float[4];
        GL30.glGetFloatv((int)3106, (float[])fArray);
        int n3 = this.enabled ? 9728 : 9729;
        this.slot5 = GL30.glGenTextures();
        GL30.glBindTexture((int)3553, (int)this.slot5);
        GL30.glTexParameteri((int)3553, (int)10241, (int)n3);
        GL30.glTexParameteri((int)3553, (int)10240, (int)n3);
        GL30.glTexParameteri((int)3553, (int)10242, (int)33071);
        GL30.glTexParameteri((int)3553, (int)10243, (int)33071);
        int n4 = GL30.glGetInteger((int)35055);
        GL30.glBindBuffer((int)35052, (int)0);
        GL30.glTexImage2D((int)3553, (int)0, (int)32856, (int)this.slot, (int)this.slot, (int)0, (int)6408, (int)5121, (ByteBuffer)null);
        GL30.glBindBuffer((int)35052, (int)n4);
        this.slot3 = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer((int)36160, (int)this.slot3);
        GL30.glFramebufferTexture2D((int)36160, (int)36064, (int)3553, (int)this.slot5, (int)0);
        int n5 = GL30.glCheckFramebufferStatus((int)36160);
        if (n5 != 36053) {
            int n6 = n5;
            throw new IllegalStateException("Icon atlas framebuffer is incomplete: " + n6);
        }
        boolean bl = GL30.glIsEnabled((int)3089);
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer((int)4);
        GL30.glGetBooleanv((int)3107, (ByteBuffer)byteBuffer);
        GL30.glDisable((int)3089);
        GL30.glColorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        GL30.glClearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        GL30.glClear((int)16384);
        GL30.glColorMask((byteBuffer.get(0) != 0 ? 1 : 0) != 0, (byteBuffer.get(1) != 0 ? 1 : 0) != 0, (byteBuffer.get(2) != 0 ? 1 : 0) != 0, (byteBuffer.get(3) != 0 ? 1 : 0) != 0);
        if (bl) {
            GL30.glEnable((int)3089);
        }
        GL30.glClearColor((float)fArray[0], (float)fArray[1], (float)fArray[2], (float)fArray[3]);
        GL30.glBindFramebuffer((int)36160, (int)n2);
        GL30.glBindTexture((int)3553, (int)n);
    }

    public int getIntType5() {
        return this.slot5;
    }
}

