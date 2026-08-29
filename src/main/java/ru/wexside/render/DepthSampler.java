package ru.wexside.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderSystem.class_5590;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_310;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import ru.wexside.misc.ClientChat;
import ru.wexside.prediction.PotionImpactMarker;

public final class DepthSampler {
   private static final float value = 2.5F;
   private static final int slot = 8;
   private GpuBufferSlice gpuBufferSlice;
   private static final int slot2 = 336;
   private static final int slot3 = 136;
   private GpuBuffer gpuBuffer;
   private GpuBuffer gpuBuffer2;
   private static final float value2 = 0.85F;
   private static final float value3 = 0.5F;
   private final Matrix4f matrix4f = new Matrix4f();
   private static final AtomicBoolean ERROR_REPORTED = new AtomicBoolean();

   public void setList(List<PotionImpactMarker> list) {
      if (!list.isEmpty()) {
         class_310 mc = class_310.method_1551();
         class_276 iIllIIiilI2 = mc.method_1522();
         if (iIllIIiilI2 != null && iIllIIiilI2.method_71639() != null && iIllIIiilI2.method_71640() != null) {
            class_243 vec = RenderCamera.position();
            if (vec != null) {
               int n = Math.min(list.size(), 8);
               float f = 3.0F;
               this.matrix4f.set(RenderProjection.viewProjectionMatrix()).invert();

               try {
                  this.update();
                  CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
                  MemoryStack memoryStack = MemoryStack.stackPush();

                  try {
                     Std140Builder std140Builder = Std140Builder.onStack(memoryStack, 336).putMat4f(this.matrix4f).putVec4((float)n, 0.5F, 0.85F, 2.5F);

                     for(int n2 = 0; n2 < 8; ++n2) {
                        if (n2 < n) {
                           class_243 vec2 = list.get(n2).position();
                           std140Builder.putVec4(
                              (float)(vec2.field_1352 - vec.field_1352),
                              (float)(vec2.field_1351 - vec.field_1351),
                              (float)(vec2.field_1350 - vec.field_1350),
                              f
                           );
                        } else {
                           std140Builder.putVec4(0.0F, 0.0F, 0.0F, 0.0F);
                        }
                     }

                     for(int var22 = 0; var22 < 8; ++var22) {
                        if (var22 < n) {
                           int n3 = list.get(var22).color();
                           std140Builder.putVec4((float)(n3 >> 16 & 0xFF) / 255.0F, (float)(n3 >> 8 & 0xFF) / 255.0F, (float)(n3 & 0xFF) / 255.0F, 1.0F);
                        } else {
                           std140Builder.putVec4(0.0F, 0.0F, 0.0F, 0.0F);
                        }
                     }

                     commandEncoder.writeToBuffer(this.gpuBufferSlice, std140Builder.get());
                  } catch (Throwable var17) {
                     if (memoryStack != null) {
                        try {
                           memoryStack.close();
                        } catch (Throwable var15) {
                           var17.addSuppressed(var15);
                        }
                     }

                     throw var17;
                  }

                  if (memoryStack != null) {
                     memoryStack.close();
                  }

                  GpuBuffer vertexBuffer = this.getGpuBuffer();
                  class_5590 indexBuffer = RenderSystem.getSequentialBuffer(class_5596.field_27382);
                  GpuBuffer gpuBuffer = indexBuffer.method_68274(6);
                  RenderPass renderPass = commandEncoder.createRenderPass(() -> "wex/potion-decal", iIllIIiilI2.method_71639(), OptionalInt.empty());

                  try {
                     renderPass.setPipeline(ClientRenderPipelines.POTION_DECAL);
                     RenderSystem.bindDefaultUniforms(renderPass);
                     renderPass.setUniform("PotionDecal", this.gpuBufferSlice);
                     renderPass.setVertexBuffer(0, vertexBuffer);
                     renderPass.setIndexBuffer(gpuBuffer, indexBuffer.method_31924());
                     renderPass.bindTexture("DepthSampler", iIllIIiilI2.method_71640(), RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST));
                     renderPass.drawIndexed(0, 0, 6, 1);
                  } catch (Throwable var16) {
                     if (renderPass != null) {
                        try {
                           renderPass.close();
                        } catch (Throwable var14) {
                           var16.addSuppressed(var14);
                        }
                     }

                     throw var16;
                  }

                  if (renderPass != null) {
                     renderPass.close();
                  }
               } catch (Throwable var18) {
                  if (ERROR_REPORTED.compareAndSet(false, true)) {
                     Throwable throwable2 = var18.getCause() != null ? var18.getCause() : var18;
                     String string = String.valueOf(throwable2);
                     ClientChat.send("PotionDecal error: " + string);
                  }
               }
            }
         }
      }
   }

   private GpuBuffer getGpuBuffer() {
      if (this.gpuBuffer2 != null) {
         return this.gpuBuffer2;
      } else {
         ByteBuffer byteBuffer = ByteBuffer.allocateDirect(48).order(ByteOrder.nativeOrder());
         byteBuffer.putFloat(-1.0F).putFloat(-1.0F).putFloat(0.0F);
         byteBuffer.putFloat(1.0F).putFloat(-1.0F).putFloat(0.0F);
         byteBuffer.putFloat(1.0F).putFloat(1.0F).putFloat(0.0F);
         byteBuffer.putFloat(-1.0F).putFloat(1.0F).putFloat(0.0F);
         byteBuffer.flip();
         this.gpuBuffer2 = RenderSystem.getDevice().createBuffer(() -> "wex/potion-decal-quad", 32, byteBuffer);
         return this.gpuBuffer2;
      }
   }

   private void update() {
      if (this.gpuBuffer == null) {
         this.gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "wex/potion-decal-ubo", 136, 336L);
         this.gpuBufferSlice = this.gpuBuffer.slice();
      }
   }
}
