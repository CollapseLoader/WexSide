package ru.wexside.render;

import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.class_10017;
import net.minecraft.class_11661;
import net.minecraft.class_11684;
import net.minecraft.class_12075;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4618;
import net.minecraft.class_6367;
import net.minecraft.class_898;
import net.minecraft.class_9799;
import net.minecraft.class_4597.class_4598;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

public final class ChamsEffect {
   private int framebufferWidth;
   private class_4598 primaryVertexConsumers;
   private GpuTextureView sceneDepthView;
   private boolean effectRendered;
   private final class_310 client = class_310.method_1551();
   private class_9799 secondaryBufferAllocator;
   private int framebufferHeight;
   private final ChamsCompositor compositor = new ChamsCompositor();
   private GpuTextureView maskDepthView;
   private float elapsedTime;
   private class_898 entityRenderer;
   private boolean framePrepared;
   private boolean framebuffersCleared;
   private class_4618 outlineVertexConsumers;
   private class_6367 effectFramebuffer;
   private final long startedAtMillis = System.currentTimeMillis();
   private GpuTextureView effectView;
   private class_276 mainFramebuffer;
   private final Matrix4f viewProjectionMatrix = new Matrix4f();
   private class_9799 primaryBufferAllocator;
   private class_4598 secondaryVertexConsumers;
   private class_6367 maskFramebuffer;
   private class_11684 renderDispatcher;
   private GpuTextureView maskColorView;
   private final class_12075 cameraRenderState;
   private final Vector4f clipSpacePosition = new Vector4f();
   private GpuTextureView sceneColorView;
   private class_243 cameraPosition;
   private class_11661 renderQueue;
   private volatile boolean pendingFramebuffersRelease;

   public ChamsEffect() {
      this.cameraRenderState = new class_12075();
      this.cameraPosition = class_243.field_1353;
   }

   private void renderEntityToMask(class_4587 matrices2, class_1297 entity2, float f) {
      if (this.entityRenderer != null) {
         class_10017 renderState = this.entityRenderer.method_72977(entity2, f);
         if (renderState != null) {
            this.prepareRenderState(renderState);
            boolean rendered = false;

            try {
               float offsetX = (float)(renderState.field_53325 - this.cameraPosition.field_1352);
               float offsetY = (float)(renderState.field_53326 - this.cameraPosition.field_1351);
               float offsetZ = (float)(renderState.field_53327 - this.cameraPosition.field_1350);
               this.entityRenderer
                  .method_72976(
                     renderState,
                     this.cameraRenderState,
                     offsetX,
                     offsetY,
                     offsetZ,
                     matrices2,
                     this.getVanillaRenderQueue()
                  );
               this.getVanillaRenderDispatcher().method_73002();
               this.getVanillaRenderQueue().method_72954();
               rendered = true;
            } finally {
               if (!rendered) {
                  this.getVanillaRenderQueue().method_72953();
               }
            }
         }
      }
   }

   private void prepareRenderState(class_10017 renderState) {
      renderState.field_53336 = null;
      renderState.field_53337 = null;
      renderState.field_53338 = null;
      renderState.field_53335 = false;
      renderState.field_61821 = 0;
      renderState.field_53333 = false;
      renderState.field_61822 = 0.0F;
      renderState.field_61823.clear();
   }

   private void updateCameraState(class_4184 camera2) {
      this.cameraRenderState.field_63079 = true;
      this.cameraRenderState.field_63078 = camera2.method_71156();
      this.cameraRenderState.field_63077 = camera2.method_19328();
      this.cameraRenderState.field_63080 = this.cameraRenderState.field_63078;
      this.cameraRenderState.field_63081 = camera2.method_23767();
   }

   public void resetFrameState() {
      this.clearFrameState();
   }

   public void present() {
      if (!this.effectRendered) {
         this.clearFrameState();
      } else if (this.mainFramebuffer != null && this.sceneColorView != null && this.effectView != null) {
         this.compositor.present(this.sceneColorView, this.effectView, this.framebufferWidth, this.framebufferHeight);
         this.clearFrameState();
      } else {
         this.clearFrameState();
      }
   }

   private void ensureRenderDispatcher() {
      if (this.renderDispatcher == null) {
         this.primaryBufferAllocator = new class_9799(1048576);
         this.secondaryBufferAllocator = new class_9799(262144);
         this.primaryVertexConsumers = class_4597.method_22991(this.primaryBufferAllocator);
         this.secondaryVertexConsumers = class_4597.method_22991(this.secondaryBufferAllocator);
         this.outlineVertexConsumers = new class_4618();
         this.renderQueue = new class_11661();
      }
   }

   private class_11684 getVanillaRenderDispatcher() {
      if (this.client.field_1773 == null) {
         return this.renderDispatcher;
      }
      try {
         return (class_11684) this.client.field_1773.getClass().getMethod("method_72911").invoke(this.client.field_1773);
      } catch (Throwable var2) {
         return this.renderDispatcher;
      }
   }

   private class_11661 getVanillaRenderQueue() {
      if (this.client.field_1773 == null) {
         return this.renderQueue;
      }
      try {
         return (class_11661) this.client.field_1773.getClass().getMethod("method_72910").invoke(this.client.field_1773);
      } catch (Throwable var2) {
         return this.renderQueue;
      }
   }

   public void releaseFramebuffers() {
      this.clearFrameState();
      this.pendingFramebuffersRelease = true;
   }

   private void releaseFramebuffersInternal() {
      if (this.maskFramebuffer != null) {
         this.maskFramebuffer.method_1238();
         this.maskFramebuffer = null;
      }

      if (this.effectFramebuffer != null) {
         this.effectFramebuffer.method_1238();
         this.effectFramebuffer = null;
      }
   }

   public void close() {
      this.releaseFramebuffers();
      if (this.renderDispatcher != null) {
         this.renderDispatcher.close();
         this.renderDispatcher = null;
      }

      if (this.primaryBufferAllocator != null) {
         this.primaryBufferAllocator.close();
         this.primaryBufferAllocator = null;
      }

      if (this.secondaryBufferAllocator != null) {
         this.secondaryBufferAllocator.close();
         this.secondaryBufferAllocator = null;
      }

      this.primaryVertexConsumers = null;
      this.secondaryVertexConsumers = null;
      this.outlineVertexConsumers = null;
      this.renderQueue = null;
      this.compositor.close();
   }

   private void clearFrameState() {
      if (this.pendingFramebuffersRelease) {
         this.releaseFramebuffersInternal();
         this.pendingFramebuffersRelease = false;
      }
      this.framePrepared = false;
      this.framebuffersCleared = false;
      this.effectRendered = false;
      this.mainFramebuffer = null;
      this.sceneColorView = null;
      this.sceneDepthView = null;
      this.maskColorView = null;
      this.maskDepthView = null;
      this.effectView = null;
      this.entityRenderer = null;
      this.cameraPosition = class_243.field_1353;
      this.framebufferWidth = 0;
      this.framebufferHeight = 0;
      this.elapsedTime = 0.0F;
   }

   private ScreenRegion calculateScreenRegion(class_1297 entity2, float f) {
      if (this.framebufferWidth > 0 && this.framebufferHeight > 0) {
         class_238 box = entity2.method_5829()
            .method_1009(0.65, 0.4, 0.65)
            .method_997(entity2.method_30950(f).method_1023(entity2.method_23317(), entity2.method_23318(), entity2.method_23321()));
         double d2 = Double.POSITIVE_INFINITY;
         double d3 = Double.POSITIVE_INFINITY;
         double d4 = Double.NEGATIVE_INFINITY;
         double d5 = Double.NEGATIVE_INFINITY;
         boolean bl = false;

         for(int i = 0; i < 2; ++i) {
            double d6 = i == 0 ? box.field_1323 : box.field_1320;

            for(int j = 0; j < 2; ++j) {
               double d = j == 0 ? box.field_1322 : box.field_1325;

               for(int k = 0; k < 2; ++k) {
                  double d7 = k == 0 ? box.field_1321 : box.field_1324;
                  this.clipSpacePosition
                     .set(
                        (float)(d6 - this.cameraPosition.field_1352),
                        (float)(d - this.cameraPosition.field_1351),
                        (float)(d7 - this.cameraPosition.field_1350),
                        1.0F
                     );
                  this.viewProjectionMatrix.transform(this.clipSpacePosition);
                  if (!(this.clipSpacePosition.w() <= 0.0F)) {
                     float f2 = this.clipSpacePosition.x() / this.clipSpacePosition.w();
                     float f3 = this.clipSpacePosition.y() / this.clipSpacePosition.w();
                     if (Float.isFinite(f2) && Float.isFinite(f3)) {
                        double d8 = ((double)f2 * 0.5 + 0.5) * (double)this.framebufferWidth;
                        double d9 = (1.0 - ((double)f3 * 0.5 + 0.5)) * (double)this.framebufferHeight;
                        d2 = Math.min(d2, d8);
                        d3 = Math.min(d3, d9);
                        d4 = Math.max(d4, d8);
                        d5 = Math.max(d5, d9);
                        bl = true;
                     }
                  }
               }
            }
         }

         if (!bl) {
            return null;
         } else {
            double d10 = d4 - d2;
            double d11 = d5 - d3;
            if (!(d10 < 1.0) && !(d11 < 1.0)) {
               double d = Math.max(20.0, d10 * 0.28);
               double d12 = Math.max(14.0, d11 * 0.18);
               d2 = Math.max(0.0, d2 - d);
               d3 = Math.max(0.0, d3 - d12);
               d4 = Math.min((double)this.framebufferWidth, d4 + d);
               d5 = Math.min((double)this.framebufferHeight, d5 + d12);
               d10 = d4 - d2;
               d11 = d5 - d3;
               return !(d10 < 1.0) && !(d11 < 1.0) ? new ScreenRegion((float)d2, (float)((double)this.framebufferHeight - d5), (float)d10, (float)d11) : null;
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private float getElapsedTime() {
      return (float)(System.currentTimeMillis() - this.startedAtMillis) / 700.0F;
   }

   public void prepareFrame() {
      this.effectRendered = false;
      this.framePrepared = false;
      this.framebuffersCleared = false;
      this.mainFramebuffer = this.client.method_1522();
      if (this.mainFramebuffer != null && this.mainFramebuffer.method_71639() != null) {
         this.ensureRenderDispatcher();
         this.framebufferWidth = this.mainFramebuffer.field_1482;
         this.framebufferHeight = this.mainFramebuffer.field_1481;
         this.ensureFramebuffers(this.framebufferWidth, this.framebufferHeight);
         class_4184 camera2 = this.client.field_1773.method_19418();
         this.entityRenderer = this.client.method_1561();
         if (camera2 != null && this.entityRenderer != null && this.maskFramebuffer != null && this.effectFramebuffer != null) {
            this.sceneColorView = this.mainFramebuffer.method_71639();
            this.sceneDepthView = this.mainFramebuffer.method_71640();
            this.maskColorView = this.maskFramebuffer.method_71639();
            this.maskDepthView = this.maskFramebuffer.method_71640();
            this.effectView = this.effectFramebuffer.method_71639();
            if (this.sceneColorView != null
               && this.sceneDepthView != null
               && this.maskColorView != null
               && this.maskDepthView != null
               && this.effectView != null) {
               this.entityRenderer.method_3941(camera2, this.client.method_1560());
               this.updateCameraState(camera2);
               this.cameraPosition = camera2.method_71156();
               this.viewProjectionMatrix.set(RenderFrameState.projectionMatrix).mul(RenderFrameState.viewMatrix);
               this.elapsedTime = this.getElapsedTime();
               this.framePrepared = true;
            } else {
               this.clearFrameState();
            }
         } else {
            this.clearFrameState();
         }
      }
   }

   public void renderEntity(class_4587 matrices2, class_1297 entity2, float f, int n, int n2, boolean bl, boolean bl2, int n3) {
      if (matrices2 != null && entity2 != null && entity2.method_5805() && this.client.field_1687 != null) {
         if (bl || bl2) {
            if (!this.framePrepared) {
               this.prepareFrame();
            }

            if (this.framePrepared
               && this.mainFramebuffer != null
               && this.sceneColorView != null
               && this.sceneDepthView != null
               && this.maskColorView != null
               && this.maskDepthView != null
               && this.effectView != null) {
               ScreenRegion screenRegion = this.expandRegion(this.calculateScreenRegion(entity2, f));
               if (screenRegion != null) {
                  this.prepareEffectFramebuffer();
                  if (this.framebuffersCleared) {
                     this.clearFramebuffer(this.maskFramebuffer);
                     MemoryStack memoryStack = MemoryStack.stackPush();

                     try (RenderTargetOverrideScope ignored = RenderTargetOverrideScope.use(this.maskColorView, this.maskDepthView)) {
                        this.renderEntityToMask(matrices2, entity2, f);
                     } catch (Throwable var17) {
                        if (memoryStack != null) {
                           try {
                              memoryStack.close();
                           } catch (Throwable var14) {
                              var17.addSuppressed(var14);
                           }
                        }

                        throw var17;
                     }

                     if (memoryStack != null) {
                        memoryStack.close();
                     }

                     this.compositor
                        .composite(
                           this.effectView,
                           this.maskColorView,
                           this.maskDepthView,
                           this.sceneColorView,
                           this.sceneDepthView,
                           this.framebufferWidth,
                           this.framebufferHeight,
                           n,
                           n2,
                           bl,
                           bl2,
                           n3,
                           screenRegion.x(),
                           screenRegion.y(),
                           screenRegion.width(),
                           screenRegion.height(),
                           this.elapsedTime
                        );
                     this.effectRendered = true;
                  }
               }
            }
         }
      }
   }

   private void prepareEffectFramebuffer() {
      if (!this.framebuffersCleared) {
         MemoryStack memoryStack = MemoryStack.stackPush();

         try {
            this.clearFramebuffer(this.effectFramebuffer);
            this.framebuffersCleared = true;
         } catch (Throwable var5) {
            if (memoryStack != null) {
               try {
                  memoryStack.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (memoryStack != null) {
            memoryStack.close();
         }
      }
   }

   private void ensureFramebuffers(int n, int n2) {
      int n3 = Math.max(n, 1);
      int n4 = Math.max(n2, 1);
      if (this.maskFramebuffer == null) {
         this.maskFramebuffer = new class_6367("wexside_chams_mask", n3, n4, true);
      } else if (this.maskFramebuffer.field_1482 != n || this.maskFramebuffer.field_1481 != n2) {
         this.maskFramebuffer.method_1234(n3, n4);
      }

      if (this.effectFramebuffer == null) {
         this.effectFramebuffer = new class_6367("wexside_chams_effect", n3, n4, true);
      } else if (this.effectFramebuffer.field_1482 != n || this.effectFramebuffer.field_1481 != n2) {
         this.effectFramebuffer.method_1234(n3, n4);
      }
   }

   private ScreenRegion expandRegion(ScreenRegion screenRegion) {
      if (screenRegion != null && this.framebufferWidth > 0 && this.framebufferHeight > 0) {
         float f = screenRegion.x();
         float f2 = screenRegion.y();
         float f3 = f + screenRegion.width();
         float f4 = f2 + screenRegion.height();
         boolean bl = f <= 52.0F || f3 >= (float)this.framebufferWidth - 52.0F;
         boolean bl2 = f2 <= 38.0F || f4 >= (float)this.framebufferHeight - 38.0F;
         float f5 = Math.max(52.0F, screenRegion.width() * 0.08F);
         float f6 = Math.max(38.0F, screenRegion.height() * 0.08F);
         if (bl) {
            f5 *= 1.75F;
         }

         if (bl2) {
            f6 *= 1.75F;
         }

         f = Math.max(0.0F, f - f5);
         f2 = Math.max(0.0F, f2 - f6);
         f3 = Math.min((float)this.framebufferWidth, f3 + f5);
         f4 = Math.min((float)this.framebufferHeight, f4 + f6);
         float f7 = f3 - f;
         float f8 = f4 - f2;
         return !(f7 < 1.0F) && !(f8 < 1.0F) ? new ScreenRegion(f, f2, f7, f8) : screenRegion;
      } else {
         return screenRegion;
      }
   }

   private void clearFramebuffer(class_6367 framebuffer5) {
      if (framebuffer5 != null) {
         GpuTexture gpuTexture = framebuffer5.method_30277();
         if (gpuTexture != null) {
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            GpuTexture gpuTexture2 = framebuffer5.method_30278();
            if (gpuTexture2 != null) {
               commandEncoder.clearColorAndDepthTextures(gpuTexture, 0, gpuTexture2, 1.0);
            } else {
               commandEncoder.clearColorTexture(gpuTexture, 0);
            }
         }
      }
   }
}
