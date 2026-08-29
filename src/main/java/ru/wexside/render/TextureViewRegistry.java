package ru.wexside.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.lang.ref.WeakReference;
import ru.wexside.misc.TextureViewAccessor;

public final class TextureViewRegistry {
   private static final int CLEANUP_THRESHOLD = 256;
   private static final Int2ObjectMap<WeakReference<TextureViewAccessor>> PROVIDERS = new Int2ObjectOpenHashMap();
   private static final Int2ObjectMap<WeakReference<GpuTextureView>> VIEWS = new Int2ObjectOpenHashMap();

   private TextureViewRegistry() {
   }

   public static GpuTextureView resolve(int textureId) {
      WeakReference<GpuTextureView> reference = (WeakReference)VIEWS.get(textureId);
      if (reference == null) {
         return resolveProvider(textureId);
      } else {
         GpuTextureView view = (GpuTextureView)reference.get();
         if (view != null) {
            return view;
         } else {
            VIEWS.remove(textureId);
            return resolveProvider(textureId);
         }
      }
   }

   public static void unregisterProvider(int textureId) {
      if (textureId > 0) {
         PROVIDERS.remove(textureId);
      }
   }

   public static void registerProvider(int textureId, TextureViewAccessor provider) {
      if (textureId > 0 && provider != null) {
         PROVIDERS.put(textureId, new WeakReference<>(provider));
      }
   }

   public static void unregisterView(int textureId) {
      if (textureId > 0) {
         VIEWS.remove(textureId);
      }
   }

   public static void registerView(int textureId, GpuTextureView view) {
      if (textureId > 0 && view != null) {
         removeCollectedViews();
         VIEWS.put(textureId, new WeakReference(view));
      }
   }

   public static int viewCount() {
      return VIEWS.size();
   }

   private static void removeCollectedViews() {
      if (VIEWS.size() >= 256) {
         ObjectIterator<Entry<WeakReference<GpuTextureView>>> iterator = VIEWS.int2ObjectEntrySet().iterator();

         while(iterator.hasNext()) {
            if (((WeakReference)((Entry)iterator.next()).getValue()).get() == null) {
               iterator.remove();
            }
         }
      }
   }

   private static GpuTextureView resolveProvider(int textureId) {
      WeakReference<TextureViewAccessor> reference = (WeakReference)PROVIDERS.get(textureId);
      if (reference == null) {
         return null;
      } else {
         TextureViewAccessor provider = reference.get();
         if (provider == null) {
            PROVIDERS.remove(textureId);
            return null;
         } else {
            return provider.getGpuTextureView();
         }
      }
   }
}
