package ru.wexside.misc;

import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL33;
import ru.wexside.render.ImportedGlTexture;
import ru.wexside.render.TextureViewRegistry;

public class TextureResource implements ResizableTexture, TextureViewAccessor, AutoCloseable {
   private int minFilter = 9729;
   private int wrapT = 10497;
   private int wrapS = 10497;
   private final ByteBuffer byteBuffer;
   private ImportedGlTexture gpuTexture;
   private int width;
   private int height;
   private OpenGlTexture openGlTexture;
   private int magFilter = 9729;
   private boolean generateMipmaps;
   private int textureId;

   public TextureResource(ByteBuffer byteBuffer) {
      this.byteBuffer = byteBuffer;
   }

   public TextureResource(ResourceData callback) {
      this(callback.toDirectByteBuffer());
   }

   public int getTextureId() {
      if (this.textureId != 0) {
         return this.textureId;
      } else {
         DecodedImage decodedImage2 = DecodedImage.decode(this.byteBuffer);

         try {
            this.width = decodedImage2.getWidth();
            this.height = decodedImage2.getHeight();
            this.textureId = GL33.glGenTextures();
            this.openGlTexture = OpenGlTexture.create(
               decodedImage2, this.magFilter, this.minFilter, this.wrapS, this.wrapT, this.generateMipmaps, this.textureId
            );
            TextureViewRegistry.registerProvider(this.textureId, this);
         } finally {
            decodedImage2.close();
         }

         return this.textureId;
      }
   }

   @Override
   public int getHeight() {
      return this.height;
   }

   @Override
   public int getWidth() {
      return this.width;
   }

   public TextureResource magFilter(int n) {
      this.magFilter = n;
      return this;
   }

   public TextureResource mipmaps(boolean bl) {
      this.generateMipmaps = bl;
      return this;
   }

   public TextureResource wrapT(int n) {
      this.wrapT = n;
      return this;
   }

   public TextureResource minFilter(int n) {
      this.minFilter = n;
      return this;
   }

   public TextureResource wrapS(int n) {
      this.wrapS = n;
      return this;
   }

   @Override
   public void close() {
      TextureViewRegistry.unregisterProvider(this.textureId);
      if (this.gpuTexture != null) {
         this.gpuTexture.close();
         this.gpuTexture = null;
         this.openGlTexture = null;
      }

      if (this.openGlTexture != null) {
         this.openGlTexture.close();
         this.openGlTexture = null;
      }

      this.textureId = 0;
      this.width = 0;
      this.height = 0;
   }

   @Override
   public GpuTextureView getGpuTextureView() {
      if (this.textureId == 0) {
         return null;
      } else {
         if (this.gpuTexture == null) {
            DecodedImage decodedImage2 = DecodedImage.decode(this.byteBuffer);

            try {
               this.gpuTexture = ImportedGlTexture.wrap(this.textureId, decodedImage2.getWidth(), decodedImage2.getHeight());
            } finally {
               decodedImage2.close();
            }
         }

         return this.gpuTexture.view();
      }
   }

   public void bindToUnit(int n) {
      this.getTextureId();
      this.openGlTexture.bindToUnit(n);
   }
}
