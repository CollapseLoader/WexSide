package ru.wexside.render;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;
import ru.wexside.misc.TextureUnitAllocator;

public class OpenGlTextureUnitAllocator
implements TextureUnitAllocator {
    private final IntBuffer intBuffer;
    private final Int2IntMap int2IntMap = new Int2IntOpenHashMap();
    private static final int slot = 16;
    private int slot2 = -1;
    private int slot3 = 8;
    private static final int slot4 = 8;

    public OpenGlTextureUnitAllocator() {
        this.intBuffer = MemoryUtil.memAllocInt((int)16);
        for (int i = 0; i < 16; ++i) {
            this.intBuffer.put(i);
        }
        this.intBuffer.flip();
    }

    @Override
    public int process(int n) {
        if (this.int2IntMap.containsKey(n)) {
            return this.int2IntMap.get(n);
        }
        if (this.slot3 >= this.getIntType()) {
            return -1;
        }
        int n2 = this.slot3++;
        this.int2IntMap.put(n, n2);
        GL33.glActiveTexture((int)(33984 + n2));
        GL33.glBindTexture((int)3553, (int)n);
        return n2;
    }

    public IntBuffer getIntBuffer() {
        return this.intBuffer;
    }

    @Override
    public void update() {
        this.int2IntMap.clear();
        this.slot3 = 8;
    }

    private int getIntType() {
        if (this.slot2 < 0) {
            int n = GL11.glGetInteger((int)34930);
            this.slot2 = Math.max(9, Math.min(16, n));
        }
        return this.slot2;
    }
}

