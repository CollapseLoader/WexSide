package ru.wexside.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.OptionalInt;
import net.minecraft.class_276;
import net.minecraft.class_310;
import ru.wexside.render.ClientRenderPipelines;

class OverlaySampler {
    private GpuBuffer gpuBuffer;

    OverlaySampler() {
    }

    private GpuBuffer getOrCreateQuadBuffer() {
        if (this.gpuBuffer != null) {
            return this.gpuBuffer;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(48).order(ByteOrder.nativeOrder());
        byteBuffer.putFloat(-1.0f).putFloat(-1.0f).putFloat(0.0f);
        byteBuffer.putFloat(1.0f).putFloat(-1.0f).putFloat(0.0f);
        byteBuffer.putFloat(1.0f).putFloat(1.0f).putFloat(0.0f);
        byteBuffer.putFloat(-1.0f).putFloat(1.0f).putFloat(0.0f);
        byteBuffer.flip();
        this.gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "wex/overlay-present-quad", 32, byteBuffer);
        return this.gpuBuffer;
    }

    void present(GpuTextureView overlayTexture) {
        if (overlayTexture == null) {
            return;
        }
        class_276 framebuffer = class_310.method_1551().method_1522();
        GpuTextureView targetView = framebuffer.method_71639();
        if (targetView == null) {
            return;
        }
        GpuBuffer quadBuffer = this.getOrCreateQuadBuffer();
        RenderSystem.class_5590 indexBuffer = RenderSystem.getSequentialBuffer((VertexFormat.class_5596)VertexFormat.class_5596.field_27382);
        GpuBuffer indices = indexBuffer.method_68274(6);
        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        try (RenderPass renderPass = encoder.createRenderPass(() -> "wex/overlay-present", targetView, OptionalInt.empty());){
            renderPass.setPipeline(ClientRenderPipelines.OVERLAY_PRESENT);
            RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
            renderPass.setVertexBuffer(0, quadBuffer);
            renderPass.setIndexBuffer(indices, indexBuffer.method_31924());
            renderPass.bindTexture("OverlaySampler", overlayTexture, RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR));
            renderPass.drawIndexed(0, 0, 6, 1);
        }
    }

    void close() {
        if (this.gpuBuffer != null) {
            this.gpuBuffer.close();
            this.gpuBuffer = null;
        }
    }
}

