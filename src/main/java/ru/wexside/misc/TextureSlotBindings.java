package ru.wexside.misc;

import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import ru.wexside.render.TextureViewRegistry;

public class TextureSlotBindings {
    private final GpuTextureView[] gpuTextureView;
    private static final int slot = 6;
    private static final int slot2 = 16;
    private GpuTextureView gpuTextureView2;
    private static final int slot3 = 5;
    private final Int2IntMap int2IntMap = new Int2IntOpenHashMap();
    private int slot4 = 6;

    public TextureSlotBindings() {
        this.gpuTextureView = new GpuTextureView[16];
    }

    private void resetInternal() {
        this.gpuTextureView2 = null;
        this.resetTextureSlots();
    }

    public void resetTextureSlots() {
        this.int2IntMap.clear();
        for (int i = 6; i < 16; ++i) {
            this.gpuTextureView[i] = null;
        }
        this.slot4 = 6;
    }

    public void beginFrame() {
        this.resetInternal();
    }

    public void setInputTexture(GpuTextureView gpuTextureView) {
        this.gpuTextureView2 = gpuTextureView;
    }

    public int resolveTextureSlot(int n, int n2, int n3) {
        if (n <= 0) {
            return -1;
        }
        int n4 = this.int2IntMap.getOrDefault(n, -1);
        if (n4 >= 0) {
            return n4;
        }
        if (this.slot4 >= 16) {
            return -1;
        }
        GpuTextureView iiIlIililI2 = TextureViewRegistry.resolve(n);
        if (iiIlIililI2 == null) {
            return -1;
        }
        int n5 = this.slot4++;
        this.int2IntMap.put(n, n5);
        this.gpuTextureView[n5] = iiIlIililI2;
        return n5;
    }

    public void bindTextures(RenderPass renderPass) {
        if (this.gpuTextureView2 != null) {
            renderPass.bindTexture("textureSampler[5]", this.gpuTextureView2, RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR));
        }
        for (int i = 6; i < this.slot4; ++i) {
            GpuTextureView gpuTextureView = this.gpuTextureView[i];
            if (gpuTextureView == null) continue;
            int n = i;
            renderPass.bindTexture("textureSampler[" + n + "]", gpuTextureView, RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR));
        }
    }

    public void close() {
        this.resetInternal();
    }
}

