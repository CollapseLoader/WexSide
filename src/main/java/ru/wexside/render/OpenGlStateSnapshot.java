package ru.wexside.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

final class OpenGlStateSnapshot {
   private static final int TRACKED_TEXTURE_UNITS = 4;
   private final int framebuffer = GL11.glGetInteger(36006);
   private final int renderbuffer = GL11.glGetInteger(36007);
   private final int vertexArray = GL11.glGetInteger(34229);
   private final int arrayBuffer = GL11.glGetInteger(34964);
   private final int program = GL11.glGetInteger(35725);
   private final int activeTexture = GL11.glGetInteger(34016);
   private final int elementArrayBuffer = GL11.glGetInteger(34965);
   private final int blendSourceRgb = GL11.glGetInteger(32969);
   private final int blendDestinationRgb = GL11.glGetInteger(32968);
   private final int blendSourceAlpha = GL11.glGetInteger(32971);
   private final int blendDestinationAlpha = GL11.glGetInteger(32970);
   private final int blendEquationRgb = GL11.glGetInteger(32777);
   private final int blendEquationAlpha = GL11.glGetInteger(34877);
   private final int[] textureBindings = new int[4];
   private final int[] samplerBindings = new int[4];
   private final int[] viewport = new int[4];
   private final int[] scissorBox = new int[4];
   private final boolean depthTest = GL11.glIsEnabled(2929);
   private final boolean cullFace = GL11.glIsEnabled(2884);
   private final boolean scissorTest = GL11.glIsEnabled(3089);
   private final boolean blend = GL11.glIsEnabled(3042);
   private final boolean depthMask = GL11.glGetBoolean(2930);

   private OpenGlStateSnapshot() {
      GL11.glGetIntegerv(2978, this.viewport);
      GL11.glGetIntegerv(3088, this.scissorBox);

      for(int unit = 0; unit < 4; ++unit) {
         this.textureBindings[unit] = GL30.glGetIntegeri(32873, unit);
         this.samplerBindings[unit] = GL30.glGetIntegeri(35097, unit);
      }
   }

   static OpenGlStateSnapshot capture() {
      return new OpenGlStateSnapshot();
   }

   void restore() {
      GL30.glBindFramebuffer(36160, this.framebuffer);
      GL30.glBindRenderbuffer(36161, this.renderbuffer);
      GL30.glBindVertexArray(this.vertexArray);
      GL15.glBindBuffer(34962, this.arrayBuffer);
      GL15.glBindBuffer(34963, this.elementArrayBuffer);
      GL20.glUseProgram(this.program);

      for(int unit = 0; unit < 4; ++unit) {
         GlStateManager._activeTexture(33984 + unit);
         GlStateManager._bindTexture(this.textureBindings[unit]);
         GL33.glBindSampler(unit, this.samplerBindings[unit]);
      }

      GlStateManager._activeTexture(this.activeTexture);
      GlStateManager._viewport(this.viewport[0], this.viewport[1], this.viewport[2], this.viewport[3]);
      GlStateManager._scissorBox(this.scissorBox[0], this.scissorBox[1], this.scissorBox[2], this.scissorBox[3]);
      setDepthTest(this.depthTest);
      setCull(this.cullFace);
      setScissor(this.scissorTest);
      setBlend(this.blend);
      GlStateManager._depthMask(this.depthMask);
      GL20.glBlendEquationSeparate(this.blendEquationRgb, this.blendEquationAlpha);
      GlStateManager._blendFuncSeparate(this.blendSourceRgb, this.blendDestinationRgb, this.blendSourceAlpha, this.blendDestinationAlpha);
   }

   private static void setDepthTest(boolean enabled) {
      if (enabled) {
         GlStateManager._enableDepthTest();
      } else {
         GlStateManager._disableDepthTest();
      }
   }

   private static void setCull(boolean enabled) {
      if (enabled) {
         GlStateManager._enableCull();
      } else {
         GlStateManager._disableCull();
      }
   }

   private static void setScissor(boolean enabled) {
      if (enabled) {
         GlStateManager._enableScissorTest();
      } else {
         GlStateManager._disableScissorTest();
      }
   }

   private static void setBlend(boolean enabled) {
      if (enabled) {
         GlStateManager._enableBlend();
      } else {
         GlStateManager._disableBlend();
      }
   }

   private static final class GL13Constants {
      private static final int ACTIVE_TEXTURE = 34016;
   }

   private static final class GL15Constants {
      private static final int ARRAY_BUFFER_BINDING = 34964;
      private static final int ELEMENT_ARRAY_BUFFER_BINDING = 34965;
   }
}
