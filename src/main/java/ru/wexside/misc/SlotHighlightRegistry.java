package ru.wexside.misc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.class_465;

public class SlotHighlightRegistry {
   private final List<SlotHighlightProvider> callbacks = new ArrayList<>();
   private long cacheTime;
   private static final long CACHE_MS = 150L;
   private class_465<?> cachedScreen;
   private List<SlotHighlight> cachedHighlights = List.of();

   public void setCallback56(SlotHighlightProvider callback) {
      if (callback != null && !this.callbacks.contains(callback)) {
         this.callbacks.add(callback);
      }
   }

   public void setCallback562(SlotHighlightProvider callback) {
      this.callbacks.remove(callback);
   }

   private List<SlotHighlight> collect(class_465<?> screen) {
      LinkedHashMap<Integer, Integer> merged = new LinkedHashMap<>();

      for(SlotHighlightProvider callback : this.callbacks) {
         List<SlotHighlight> highlights;
         try {
            highlights = callback.process4(screen);
         } catch (Throwable var8) {
            continue;
         }

         if (highlights != null) {
            for(SlotHighlight highlight : highlights) {
               if (highlight != null) {
                  merged.putIfAbsent(Integer.valueOf(highlight.slot()), Integer.valueOf(highlight.color()));
               }
            }
         }
      }

      if (merged.isEmpty()) {
         return List.of();
      } else {
         ArrayList<SlotHighlight> result = new ArrayList<>(merged.size());
         merged.forEach((slot, color) -> result.add(new SlotHighlight(slot, color)));
         return result;
      }
   }

   public List<SlotHighlight> process2(class_465<?> screen) {
      if (screen != null && !this.callbacks.isEmpty()) {
         long now = System.currentTimeMillis();
         if (screen == this.cachedScreen && now - this.cacheTime < 150L) {
            return this.cachedHighlights;
         } else {
            List<SlotHighlight> highlights = this.collect(screen);
            this.cachedScreen = screen;
            this.cacheTime = now;
            this.cachedHighlights = highlights;
            return highlights;
         }
      } else {
         return List.of();
      }
   }
}
